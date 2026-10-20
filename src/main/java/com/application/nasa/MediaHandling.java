package com.application.nasa;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.application.nasa.apodModel.Apod;

public class MediaHandling {

    public static final Path CACHE_DIR = Path.of(System.getProperty("user.home"), ".cache", "nasa");

    public static void saveVideo(apodModel.Apod apod, String path) {
        try{
            String url = apod.hdurl();
            String fileName = apod.title() + ".mp4";
            ProcessBuilder pb = new ProcessBuilder("ffmpeg", "-i", url, "-c", "copy", fileName);

            pb.directory(new File(path));
            Process p = pb.start();
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    public static File loadYoutubeVideo(Apod apod) {
        try {
            String cleanedTitle = sanitize(apod.title());
            Files.createDirectories(CACHE_DIR);
            //Check if cached.
            if(Files.exists(CACHE_DIR.resolve(cleanedTitle + ".mp4"))) {
                System.out.println("Found cached video" + CACHE_DIR.resolve(cleanedTitle + ".mp4"));
                return CACHE_DIR.resolve(cleanedTitle + ".mp4").toFile();
            }

            String ytUrl = extractYoutubeLink(apod.basic_html());
            System.out.println("YouTube URL: " + ytUrl);

            ProcessBuilder pb = new ProcessBuilder("yt-dlp", "--merge-output", "mp4", "--remux-video", "mp4", "-o", cleanedTitle, ytUrl);
            pb.directory(CACHE_DIR.toFile());
            Process p = pb.start();
            p.waitFor();
            System.out.println("Downloaded video to: " + CACHE_DIR.resolve(cleanedTitle + ".mp4"));
            return CACHE_DIR.resolve(cleanedTitle + ".mp4").toFile();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
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
