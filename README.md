# NASA APOD Viewer

A JavaFX desktop app for browsing NASA's Astronomy Picture of the Day, viewing images and videos, and reading their explanations.

- Download images to your Downloads folder.
- Save a batch of media in a dated folder.
- Load NASA's public feed without an API key.

Requires JDK 25 and an internet connection. Video downloads need `ffmpeg`; YouTube playback also needs `yt-dlp` on your PATH.

Run with the included Maven wrapper:

```sh
./mvnw javafx:run
```

On Windows, use `mvnw.cmd javafx:run`.

Run tests with `./mvnw test`.

