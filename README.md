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

Build a runnable JAR with all dependencies, including JavaFX:

```sh
./mvnw clean package
java -jar target/NASA.jar
```

Both commands require Java 25 or newer. On macOS, select the installed JDK 25
before running them if your terminal defaults to an older Java version:

```sh
export JAVA_HOME=$(/usr/libexec/java_home -v 25)
export PATH="$JAVA_HOME/bin:$PATH"
```

On Windows, build with `mvnw.cmd clean package`.

The JAR includes JavaFX native libraries for the operating system and architecture
used to build it. Rebuild on each target platform; this macOS Apple Silicon build
is not a Windows or Intel Mac build. A Java 25+ runtime is still required.

In IntelliJ IDEA, reload the Maven project and run the shared **Build JAR** run
configuration, or run `clean package` from the Maven tool window. Set the Maven
runner JRE to JDK 25. The old **JavaFX Application** artifact uses the retired
`fx:deploy` task and does not work with modern JDKs; use the Maven build instead.

