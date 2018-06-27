# Casa Las Glorias

This is an Android app for the restaurant [Casa Las Glorias](www.casalasglorias.com), which I developed as my capstone project for Udacity's Android Developer Nanodegree. It features a map with restaurant and current location markers and a searchable menu. 

## Installation

1. Clone this repository and import it into Android Studio.
2. Manually add Firebase by following these instructions (up until _Add the SDK_): [https://firebase.google.com/docs/android/setup#manually_add_firebase](https://firebase.google.com/docs/android/setup#manually_add_firebase)
3. Register on the Google Cloud Platform Console and get a Google API key by following these instructions: [https://developers.google.com/maps/documentation/android-sdk/signup](https://developers.google.com/maps/documentation/android-sdk/signup)
4. Add the following line to your global gradle.properties file: `MyCasaLasGloriasGoogleMapsKey="replace_this_with_your_key"`
5. In your [Firebase Console](https://console.firebase.google.com/), configure the database rules as follows:
```
{
  "rules": {
    ".read": true,
    ".write": false
  }
}
```
6. In your [Firebase Console](https://console.firebase.google.com/), import the following JSON file for the database data: [https://casa-las-glorias.firebaseio.com/public.json](https://casa-las-glorias.firebaseio.com/public.json)

## License

MIT License

Copyright (c) [2018] [Greg Turek]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
