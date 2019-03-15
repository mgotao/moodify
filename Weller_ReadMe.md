# Scott Weller ReadMe File

### Sprint #1 - 11/13/2018

+ Connect to Spotify API

For this I wanted to get the app running and connected to Spotify, but I'm currently having trouble with API authorization. I'm relatively sure all I have to do is figure out how to get my app's SHA1 key, but there were also some issues with the redirect uri.In the next day or so I want to get the app fully connected and get the media player up and running

___
### Sprint #2 - 11/28/2018

+ Succesfully authenticate app with Spotify Android SDK
+ Integrate Playback
+ Begin UI design

The hardest part about this sprint was still getting the app connected to the Spotify Api. The Android SDK is in beta, and the documentation on their developer website often refers to outdated versions, making it necessary to dive into the source code to understand how the API works. The approach we settled on is to make Web API calls to get the song URIs and then use the Android SDK for playback. This is mainly because the Android SDK does not have a search function, and also because the source code currently does not have a method which takes the token received on authentication, making it useless in that regard.
___
### Sprint #3 - 12/6/2018

+ Implement Miguel's Web API java searches into the Android code
+ Implement AsyncTask in app for web API calls
+ Redesign UI

This sprint went much smoother than than the others. Because we'd already figured out how to make the search and playback functions work in the app it was relatively trivial implementing Miguel's code into mine. Some changes to the UI were made to make the app look a little nicer and there were a few version errors between me and Miguel's code, but those didn't take all that long to work out. 

