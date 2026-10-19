package com.application.nasa;

import com.application.nasa.apodModel.Apod;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.web.WebView;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import javafx.embed.swing.SwingFXUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static com.application.nasa.MediaHandling.CACHE_DIR;
import static com.application.nasa.MediaHandling.extractSrc;

public class APIController {

    HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private Apod ACTIVE_APOD;
    private ArrayList<Apod> media;

    @FXML
    private VideoPlayer VIDEO_PLAYER;

    @FXML
    private ImageView APOD_VIEWER;

    @FXML
    private ComboBox<Object> IMAGE_SELECTOR;

    @FXML
    private Button LOAD_APOD;

    @FXML
    private Button SAVE_IMAGE;

    @FXML
    private Label SAVE_STATUS;

    @FXML
    private Button BUNDLE;

    @FXML
    private WebView WEBVIEW;

    @FXML
    protected void bundleMedia() {
        LocalDate dateObj = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        String formattedDate = dateObj.format(formatter);
        System.out.println("Formatted Date: " + formattedDate);

        String path = System.getProperty("user.home") + "/Downloads/" + formattedDate + "/";
        try {
            File dir = new File(path);
            Boolean _ = dir.mkdirs();
        }catch (Exception e) {
            System.out.println("Error creating directory");
        }

        System.out.println("Saving images to " + path);

        for (Apod apod : media) {
            saveMedia(path, apod);
        }
        System.out.println("Done");
    }
    @FXML
    protected void saveMedia() {
        String path = System.getProperty("user.home") + "/Downloads/";

        if(ACTIVE_APOD.hdurl().endsWith("mp4")) {
            MediaHandling.saveVideo(ACTIVE_APOD, path);
            System.out.println("Video saved");
        }else {
            saveMedia(path, ACTIVE_APOD);
        }
    }

    protected void saveMedia(String path, Apod apod) {
        Task<Void> task = new Task<> () {

            @Override
            protected Void call() {
                boolean success = false;
                if(apod.hdurl().endsWith("mp4")) {

                    MediaHandling.saveVideo(apod, path);
                    success = true;
                }

                Image img = new Image(apod.hdurl(), false);
                String title = apod.title();

                BufferedImage bi = convertToBufferedImage(img);
                File savedImage = new File(path + title + ".png" );

                try{
                    success = ImageIO.write(bi, "png", savedImage);

                    boolean finalSuccess = success;
                    Platform.runLater(() -> {
                        SAVE_STATUS.setVisible(true);
                        if (finalSuccess) {
                            SAVE_STATUS.setText("Image saved to downloads folder");
                            SAVE_STATUS.getStyleClass().add("label-success");
                        }else {
                            SAVE_STATUS.setText("Image failed to save, please try again.");
                            SAVE_STATUS.getStyleClass().add("label-error");
                        }
                    });
                }catch(Exception e) {
                    throw new RuntimeException(e);
                }

                return null;
            }
        };
        task.setOnFailed(_ -> {
            SAVE_STATUS.setVisible(true);
            SAVE_STATUS.setText("Image failed to save, please try again.");
            SAVE_STATUS.getStyleClass().removeAll("label-success", "label-error");
            SAVE_STATUS.getStyleClass().add("label-error");
            throw new RuntimeException(task.getException());
        });
        new Thread(task).start();
    }

    @FXML
    protected void getAPOD() {
        try {
            HttpRequest req = HttpRequest.newBuilder()
                    .uri(URI.create("https://science.nasa.gov/wp-json/wp/v2/apod-basic"))
                    .header("Accept", "application/json")
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            client.sendAsync(req, HttpResponse.BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(body -> {
                        try{
                            return MAPPER.readValue(body, new TypeReference<List<Apod>>() {});
                        }catch(Exception e){
                            throw new RuntimeException(e);
                        }
                    })
                    .thenAccept(list -> Platform.runLater(() -> {
                        media = (ArrayList<Apod>) list;
                        checkHDUrl(media);
                        var items = FXCollections.observableArrayList();
                        for (int i = 0; i < media.size(); i++) {
                            items.add(i);
                        }
                        LOAD_APOD.setVisible(false);
                        BUNDLE.setVisible(true);
                        IMAGE_SELECTOR.setVisible(true);
                        IMAGE_SELECTOR.setItems(items);
                        IMAGE_SELECTOR.getSelectionModel().selectedItemProperty().addListener((_, _, val) -> {
                            boolean isVideo = false, isYoutubeVideo = false;
                            SAVE_STATUS.setVisible(false);

                            Apod selected = media.get((Integer) val);
                            System.out.println(selected.hdurl());
                            if (MediaHandling.extractYoutubeLink(selected.basic_html()) != null) {
                                isYoutubeVideo = true;
                            }
                            else if(selected.hdurl().endsWith("mp4")) {
                                isVideo = true;
                            }else {
                                VIDEO_PLAYER.silence();
                            }

                            String[] hdurl = selected.hdurl().split("\\?");

                            if (isVideo) {
                                //Is a video.
                                SAVE_IMAGE.setText("Download Video");
                                APOD_VIEWER.setVisible(false);
                                VIDEO_PLAYER.setVisible(true);
                                VIDEO_PLAYER.setVideoURL(hdurl[0]);
                            } else if (isYoutubeVideo) {

                                SAVE_IMAGE.setText("Download Video");
                                APOD_VIEWER.setVisible(false);
                                VIDEO_PLAYER.setVisible(true);
                                VIDEO_PLAYER.setVideoURL(MediaHandling.loadYoutubeVideo(selected).toURI().toString());
                            } else {
                                SAVE_IMAGE.setText("Download Image");

                                Image img = new Image(hdurl[0], true);
                                SAVE_IMAGE.disableProperty().bind(img.progressProperty().lessThan(1).or(img.errorProperty()));

                                APOD_VIEWER.setImage(img);
                                APOD_VIEWER.setVisible(true);
                                VIDEO_PLAYER.setVisible(false);
                            }
                            ACTIVE_APOD = selected;
                        });
                        IMAGE_SELECTOR.getSelectionModel().select(0);
                        SAVE_IMAGE.setVisible(true);
                    }))
                    .exceptionally(ex -> {
                        throw new RuntimeException(ex);
                    });
            System.out.println("Done");
        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private BufferedImage convertToBufferedImage(Image image) {
        BufferedImage bufferedImage = SwingFXUtils.fromFXImage(image, null);
        if (bufferedImage == null) {
            throw new IllegalStateException("Image has not finished loading or failed to load.");
        }
        return bufferedImage;
    }

    /**
     * Loops through all images and checks if hdurl is empty. If it is, it will attempt to find the source in basic_html.
     * Typically, this occurs when videos are returned.
     * @param images
     */
    private void checkHDUrl(ArrayList<Apod> images) {
        for(Apod apod : images) {
            if(apod.hdurl().isEmpty()) {
                String src = extractSrc(apod.basic_html());
                apod.setHdurl(src);
            }
        }
    }
}
