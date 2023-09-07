# SubSnap - Reddit Image Fetcher
## Overview
SubSnap is a versatile Android application designed for exploring and displaying random images from various subreddits. It provides users with the ability to effortlessly discover captivating content from their favorite subreddits and even download or share these images. This application is intended primarily for testing and experimentation purposes and relies on the Meme_Api project (https://github.com/D3vd/Meme_Api) for its functionality.

## Key Features
- Display Random Images: By default, SubSnap loads random images from the 'meme' subreddit. Users can easily specify a different subreddit of their choice to view random images from.

- Image Sharing: SubSnap allows users to share image links with others, making it simple to spread interesting content.

- Image Download: Users can download images to their device, with the downloaded files conveniently stored in the device's downloads folder.

## Getting Started
To use SubSnap, follow these steps:

1.  **Clone the Repository:** Start by cloning the SubSnap repository to your local machine using Git:
    
    bashCopy code
    
    `git clone https://github.com/YourGitHubUsername/SubSnap.git` 
    
2.  **Navigate to the Project Directory:** Change your working directory to the project's root folder:
    
    bashCopy code
    
    `cd SubSnap` 
    
3.  **Build the APK:** Use Gradle to build the APK. Execute the following command:
    
    bashCopy code
    
    `./gradlew assembleDebug` 
    
    This command will build the debug version of the APK.
    
4.  **Locate the APK:** Once the build process is complete, you can find the generated APK file in the following directory:
    
    luaCopy code
    
    `app/build/outputs/apk/debug/app-debug.apk` 
    
5.  **Install the APK:** Transfer the APK to your Android device and install it. You may need to enable "Install from Unknown Sources" in your device's settings if you haven't already.
