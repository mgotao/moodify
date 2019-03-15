# Sprint #1 - 11/13/2018

+ Send request for token from Spotify API
+ Use token to query data

Mostly focused on learning how to get the Spotify API up and running in Java. The hardest part of this sprint was figuring out how to set the request properties of the URL connection, since the Spotify documentation is a little esoteric in writing. After a lot of research and testing, I was finally able to pull an access token. The next hardest task was to build the GET request in order to pull data from the API. Thanks to the Spotify console, this was easier to execute, since it was able to modularly show me how to build the request. Then it was all a matter of parsing and reading through the JSON object, which was thankfully a more forgiving task as Google's GSON library was very easy to use.

___
# Sprint #2 - 11/28/2018

+ Successfully pull multiple tracks and parse through results
+ Create string parser to convert user input to appropriate HTML search
+ Pull audio analysis of track results
+ Research into AsynchronousTasks in Android

More research went into configuring the HTML requests for search, and the audio analysis, and went mostly without a hitch. The test  environment currently has the functionality to convert the input string from the user into the appropriate HTML format to perform the search. The audio analysis of each track pulled by the search was also successfully pulled from the API. The biggest challenege this sprint was getting it to work in a native Android environment. The first couple steps towards importing the code to Android were successful, as only some minor changes needed to be changed to get the mechanisms to work on Android (e.g. switching to Android's Base64 library). More research needs to be put into optimizing the code to run on the application, as most of the load is executed on the main thread and may pose performance problems. Otherwise, progress is mostly shaping up, and we could have a demo ready by poster day.

___
# Sprint #3 - 12/6/2018

+ Change implementation of search and how it pulls tracks
+ Add another connection block that generates the playlist
+ Assist Scott in getting code working on Android for demo

More research into search was done, requiring changes to be made to how it pull songs. As search simply pulls tracks that are similar to the search query in name alone, more work needed to be done in order to get reliable playlists that reflect what the user wants to search for. Because of this, saving the playlist to the user's Spotify profile needed to be scrapped to meet the deadline, as well as to get a working application up for research day. This turned out positively, as Spotify's API has a recommendations endpoint that served our purpose very well. The biggest challenge was importing the Java-side code to the Android environment, which required some finetuning to work, and we were thankfully able to get up and running just in time for Research Day.
