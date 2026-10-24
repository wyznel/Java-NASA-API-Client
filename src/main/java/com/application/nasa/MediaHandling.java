package com.application.nasa;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.application.nasa.apodModel.Apod;
import javafx.concurrent.Task;

public class MediaHandling {

    public static final Path CACHE_DIR = Path.of(System.getProperty("user.home"), ".cache", "nasa");

    public static void saveVideo(apodModel.Apod apod, String path) {
        try{

            if(extractYoutubeLink(apod.basic_html()) != null) {
                //Save to downloads.
                String cleanedTitle = sanitize(apod.title());

                Task<File> getVideo = loadYoutubeVideo(apod);
                getVideo.setOnSucceeded(event -> {
                    File videoFile = getVideo.getValue();
                    try {
                        Files.move(videoFile.toPath(), Path.of(path, cleanedTitle + ".mp4"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                new Thread(getVideo).start();
                return;
            }

            String url = apod.hdurl();
            String fileName = apod.title() + ".mp4";
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", url, "-c", "copy", fileName);

            pb.directory(new File(path));
            Process p = pb.start();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static Task<File> loadYoutubeVideo(Apod apod) {
        return new Task<>() {
            @Override
            protected File call() throws Exception {
                String cleanedTitle = sanitize(apod.title());

                Files.createDirectories(CACHE_DIR);

                Path outputPath = CACHE_DIR
                        .resolve(cleanedTitle + ".mp4")
                        .toAbsolutePath();

                // Return the cached file.
                if (Files.isRegularFile(outputPath)) {
                    System.out.println("Found cached video: " + outputPath);
                    return outputPath.toFile();
                }

                String ytUrl = extractYoutubeLink(apod.basic_html());
                System.out.println("YouTube URL: " + ytUrl);

                ProcessBuilder pb = new ProcessBuilder(
                        "yt-dlp",
                        "--no-playlist",
                        "--merge-output-format", "mp4",
                        "--remux-video", "mp4",
                        "-o", cleanedTitle + ".%(ext)s",
                        ytUrl
                );

                pb.directory(CACHE_DIR.toFile());
                pb.inheritIO();

                Process process = pb.start();
                int exitCode = process.waitFor();

                if (exitCode != 0) {
                    throw new IOException(
                            "yt-dlp failed with exit code " + exitCode
                    );
                }

                if (!Files.isRegularFile(outputPath)) {
                    throw new IOException(
                            "Download finished but file was not found: " + outputPath
                    );
                }

                System.out.println("Downloaded video to: " + outputPath);
                return outputPath.toFile();
            }
        };
    }

    public static String sanitize(String name) {
        String s = name
                .replaceAll("[\\\\/:*?\"<>|\\p{Cntrl}]", "_")
                .replaceAll("\\s+", " ")
                .replaceAll("[. ]+$", "")
                .trim();
        if (s.matches("(?i)^(CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(\\..*)?$")) s = "_" + s;
        if (s.length() > 150) s = s.substring(0, 150);
        return s.isEmpty() ? "untitled" : s;
    }

    /**
     * Finds media url from the basic_html of an APOD.
     * @param html
     * @return
     */
    public static String extractSrc(String html) {
        Pattern SRC_PATTERN = Pattern.compile("<source[^>]*\\ssrc\\s*=\\s*\"([^\"]+)\"", Pattern.CASE_INSENSITIVE);
        Matcher m = SRC_PATTERN.matcher(html);
        return m.find() ? m.group(1) : null;
    }

    public static String extractYoutubeLink(String src) {
        Pattern SRC_PATTERN = Pattern.compile("/(?:www\\.)?youtube(?:-nocookie)?\\.com\\/embed\\/([\\w-]{11})(?![\\w-])");
        Matcher m = SRC_PATTERN.matcher(src);
        return m.find() ? m.group().substring(1) : null;
    }

}
