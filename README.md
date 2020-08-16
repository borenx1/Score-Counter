# Score-Counter
An app for score counting

## How to open
APK: app-debug.apk

## Achivements
-	Select 1-8 teams/players and show the scores on the screen, different layouts for different numbers
-	3 different scoreboard layout styles: grid (shown above), rows, “display” (lack of a better word) which hides the increment/decrement score buttons and shows when pressing on a scoreboard, this was intended as the best way to display the scores for everyone to see
-	Customise each scoreboard individually: title, increment number, colour, button style
-	7 button styles to position the increment/decrement score buttons, including “tap score” styles where you tap the top/bottom half or right/left half of the scoreboard to increment and decrement the score
-	Long press increment/decrement score buttons to change score by a custom amount
-	Long press scoreboard title to change positioning/order of scoreboards
-	Vibrate and sound on button press customisable in settings
-	Undo/redo feature
-	Save/open session feature
-	Consideration of portrait and landscape modes, different screen sizes
-	Increment/decrement score actions run on a separate thread to increase responsiveness
-	Use of Android Architecture: LiveData, ViewModel and Room Persistence Library

## Improvements
-	Convenient and intuitive way of informing user of all features, eg. reorder scoreboards by long-pressing name
-	Better layout design for some settings, eg. a row layout with many players looks squished especially in landscape mode and with certain button styles
-	Add dice and timer features for a more complete party game experience

## Screenshots
![Screenshot 1](https://github.com/borenx1/Score-Counter/blob/master/screenshots/1.png?raw=true)
![Screenshot 2](https://github.com/borenx1/Score-Counter/blob/master/screenshots/2.png?raw=true)
