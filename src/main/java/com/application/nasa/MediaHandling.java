package com.application.nasa;

import java.io.File;

public class MediaHandling {

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

}
