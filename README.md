# CarConnectForGoogleNow
An app for Android which allows you to use Google Now hands-free while driving. 

This app was made for people who connect their phones to their car stereo via Bluetooth. It listens for key shortcuts like "rewind,forward" sent from your stereo and initiates Google Now. It also sends text from Google Now to your stereo's display so it's easier to read.

It uses AccessibilityServices and Bluetooth AVRCP.

A lot of code for AVRCP came from another open source app called [Botifier](https://github.com/grimpy/Botifier "Botifier GitHub"). 

This is a work in progress.

TODO:
* Leave screen unlocked while driving (otherwise the service isn't listening)
* Build settings screen
* Add options for other key shortcuts
