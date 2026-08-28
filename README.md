# Loud Bird

This is a voice controlled Flappy Bird style game. Instead of clicking or tapping to fly, you use your microphone to yell. The louder you are, the higher the bird flies. Stay quiet and the bird drops lower. The goal is to pass through the pipes without hitting them, may the highest score wins.

## Why I made it

I got scolded because I was yelling too hard over game chat. My phone got taken away, so there was not much to do except code (watch how hard i have been working on lapse). After long 12 hour coding sessions I got bored and thought about Flappy Bird.

Then I had the idea: what if Flappy Bird used your voice instead of taps? If you yell louder, the bird goes higher. If you are quiet, it goes lower. You still have to dodge the pipes, except now the controller is basically your own voice.

Hopefully I do not get kicked out of the house for testing it. (Future ethan: i did...)

## Tools

Main version: Java 21 + JavaFX. Uses Maven.

There is also an HTML version in one file with plain HTML, CSS, and JavaScript. No install needed for that version.

## Run the Java version

Make sure Java 21 is installed. Then in the project folder:

- Mac/Linux: `./mvnw javafx:run`
- Windows: `mvnw.cmd javafx:run`

If Maven says `JAVA_HOME` is missing, set it to your Java 21 folder first.

Example on Windows PowerShell:

```powershell
$env:JAVA_HOME='C:\Users\Administrator\.jdks\ms-21.0.12'
.\mvnw.cmd javafx:run
```

## Run the HTML version

Open `index.html` in a browser.

The browser may ask you for microphone permission. Allow it if you want voice control. If the mic does not work, you can still use the arrow keys or `W` and `S` to enjoy the game.

## How to rebuild it yourself

1. Make a bird with both an X and an Y position.
2. Read microphone volume.
3. Convert volume into a height on the screen.
4. Smooth the volume so the bird does not shake too much.
5. Spawn pipes on the right side.
6. Move pipes left every frame.
7. Check if the bird hits a pipe.
8. Add score when the bird passes a pipe.
9. Save or show a high score.
10. Add restart after game over.

In the Java version I split the code into model, view, and controller. The model stores the game state, the view draws everything, and the controller handles keyboard input, audio input, and the game loop.

The HTML version uses the same idea but keeps it all in one file so it is easier to open and share.


<img width="527" height="907" alt="屏幕截图 2026-08-28 121403" src="https://github.com/user-attachments/assets/3dfc7137-f4d6-4cf6-bd63-ec7954da4590" />

