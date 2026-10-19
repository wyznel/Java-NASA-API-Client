package com.application.nasa;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class VideoPlayer extends MediaView {

    private Boolean isHovered = false;

    public VideoPlayer() {
        setPreserveRatio(true);

        this.hoverProperty().addListener(_ -> {

            isHovered = !isHovered;

            boolean hasMedia = getMediaPlayer() != null;
            boolean hasContent = hasMedia && getMediaPlayer().getMedia() != null;
            if (isHovered && hasContent) {
                System.out.println("Video is being hovered.");
            }
        });
    }

    public void setVideoURL(String videoURL) {
        MediaPlayer previous = getMediaPlayer();
        if (previous != null) {
            previous.dispose();
        }

        MediaPlayer player = getPlayer(new Media(videoURL));
        player.setCycleCount(MediaPlayer.INDEFINITE);
        setMediaPlayer(player);
    }


    private MediaPlayer getPlayer(Media media) {
        MediaPlayer mp = new MediaPlayer(media);
        mp.setAutoPlay(true);
        mp.setCycleCount(MediaPlayer.INDEFINITE);

        return mp;
    }

}
