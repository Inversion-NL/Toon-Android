## Toon-Android
Toon Android app for rooted Toon devices. This app lets your control your rooted Toon device with Android phone.

## Screenshots
![Alt text](/screenshots/screenshot1.png?raw=true "Screenshot 1")

## Features
- View current temperature
- Set temperature higher or lower
- Select a scene (Home, Sleep, Away, Comfort)
- Turn program on/off
- View current watt usage
- View current gas usage

## Installation
Todo

## How to use?
1. Install the apk file (make sure you can install apps from unknown sources). 
2. Press the settings icon
3. Fill in the url of your Toon (e.g. http://192.168.0.1:1234)
4. (Optional) In advanced settings, use the redirect service
5. (Optional) Fill in token. Token will be placed in header "Api-Key".
6. Restart the app

To use the app outside of your internal network you can use the following methods:
1. Use port forwarding to Toon in router settings (not recommended).
2. Host a webservice within your network that accepts only requests that contains the Api-Key. Use a port forwarding rule to forward to the script.

## License
MIT License. 
MIT © [Fabian Blom]()