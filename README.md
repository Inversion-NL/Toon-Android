## Toon-Android
Toon Android app for rooted Toon devices. This app lets your control your rooted Toon device with your Android phone.

## Screenshots
![Alt text](/screenshots/screenshot_welcome1.png?raw=true "Welcome" | width=100)
![Alt text](/screenshots/screenshot_welcome2.png?raw=true "Welcome" | width=100)
![Alt text](/screenshots/screenshot_controls1.png?raw=true "Controls" | width=100)
![Alt text](/screenshots/screenshot_settings1.png?raw=true "Settings" | width=100)

## Features
- View current temperature
- Set temperature higher or lower
- Select a scene (Home, Sleep, Away, Comfort)
- Turn program on/off
- View current watt usage
- View current gas usage

## Installation
Install apk which can be found in the install directory

## How to use?
1. Download APK from here: https://github.com/Inversion-NL/Toon-Android/blob/master/install/toon-v0.5.apk
2. Install the apk file (make sure you can install apps from unknown sources). 
3. Follow the instructions in welcome screen
5. (Optional) In settings -> advanced settings, use the redirect service
6. (Optional) Fill in token. Token will be placed in header "Api-Key".
7. Restart the app

To use the app outside of your internal network you can use the following methods:
1. Use port forwarding to Toon in router settings (not recommended).
2. Use VPN
3. Host a webservice within your network that accepts only requests that contains the Api-Key. Use a port forwarding rule to forward to the script.

## License
MIT License. 
MIT © [Fabian Blom]()
