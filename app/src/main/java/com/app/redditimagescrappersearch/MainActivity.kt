package com.app.redditimagescrappersearch

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import java.io.File


class MainActivity : AppCompatActivity() {
    private lateinit var option : Spinner
    private var currentImageUrl:String?=null
    private var subRed: String=""
    private var msg: String? = ""
    private var lastMsg = ""

    @SuppressLint("CutPasteId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        option = findViewById(R.id.spinnerSelect)
        val options = arrayOf("meme","MemeEconomy","ComedyCemetery","dankmemes","PrequelMemes","funny","gaming")
        option.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, options)
        option.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                subRed= options[position]
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        val addToggleButton: ToggleButton =findViewById(R.id.addToggleButton)
        val search: EditText = findViewById(R.id.search)
        val searchButton: ImageButton =findViewById(R.id.searchButton)
        addToggleButton.setOnClickListener{
            if(addToggleButton.isChecked){
                option.visibility=View.GONE
                search.setText("")
                subRed=""
                search.visibility = View.VISIBLE
                searchButton.visibility=View.VISIBLE
            }
            else{
                option.visibility=View.VISIBLE
                search.visibility = View.GONE
                search.setText("")
                searchButton.visibility=View.GONE
            }
        }

        val aboutToggle: ToggleButton = findViewById<ToggleButton>(R.id.aboutToggle)
        val aboutText: TextView = findViewById<TextView>(R.id.aboutText)
        val spinnerSelect: Spinner =findViewById(R.id.spinnerSelect)
        val iv= findViewById<ImageView>(R.id.memeImageView)
        val addtb=findViewById<ToggleButton>(R.id.addToggleButton)
        val searchText=findViewById<EditText>(R.id.search)
        val searchB=findViewById<ImageButton>(R.id.searchButton)
        aboutToggle.setOnClickListener{
            if(aboutToggle.isChecked){
                aboutText.visibility=View.VISIBLE
                spinnerSelect.visibility=View.GONE
                iv.visibility=View.GONE
                addtb.visibility=View.GONE
                searchText.visibility=View.GONE
                searchB.visibility=View.GONE
            }
            else{
                aboutText.visibility=View.GONE
                spinnerSelect.visibility=View.VISIBLE
                iv.visibility=View.VISIBLE
                addtb.visibility=View.VISIBLE
            }
        }

        loadMeme()
    }

    private fun loadMeme() {
        val progressBar: ProgressBar =findViewById<ProgressBar>(R.id.progressBar)
        progressBar.visibility= View.VISIBLE
        val memeImageView: ImageView =findViewById<ImageView>(R.id.memeImageView)
        val url = "https://meme-api.herokuapp.com/gimme/$subRed"
        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
            { response -> currentImageUrl = response.getString("url")
                Glide.with(this).load(currentImageUrl).listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        progressBar.visibility = View.GONE
                        return false
                    }
                }).into(memeImageView)
            },
            { error -> Toast.makeText(this, "Error", Toast.LENGTH_LONG).show() })
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    fun shareButtonClicked(view: View) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type="text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, "$currentImageUrl")
        val chooser= Intent.createChooser(intent, "Share ...")
        startActivity(chooser)
    }

    fun nextButtonClicked(view: View) {
        loadMeme()
    }

    fun downloadButtonClicked(view: View) {
        downloadImage("$currentImageUrl")
    }

    fun searchedButtonClicked(view: View) {
        val search: EditText = findViewById<EditText>(R.id.search)
        subRed=search.editableText.toString()
        loadMeme()
    }

    private fun downloadImage(url: String) {
        val directory = File(Environment.DIRECTORY_DOWNLOADS)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val downloadManager = this.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadUri = Uri.parse(url)
        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/") + 1))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    url.substring(url.lastIndexOf("/") + 1)
                )
        }
        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread(Runnable {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    this.runOnUiThread {
                        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }).start()
    }
    private fun statusMessage(url: String, directory: File, status: Int): String? {
        var msg = ""
        msg = when (status) {
            DownloadManager.STATUS_FAILED -> "Download Failed, Please Try Again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image Downloaded in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's Nothing To Download"
        }
        return msg
    }
}