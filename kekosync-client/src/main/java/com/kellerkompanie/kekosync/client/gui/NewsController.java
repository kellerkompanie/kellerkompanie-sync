package com.kellerkompanie.kekosync.client.gui;

import com.kellerkompanie.kekosync.client.gui.task.UpdateNewsTask;
import com.kellerkompanie.kekosync.core.entities.News;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import lombok.extern.slf4j.Slf4j;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import static javafx.animation.Interpolator.EASE_BOTH;

// FIXME broken after migration to JavaFX 12

@Slf4j
public class NewsController implements Initializable {

    private static NewsController instance;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private VBox masonryPane;


    public static NewsController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
    }

    public void updateNews(List<News> newsList) {
        ArrayList<Node> children = new ArrayList<>();
        int i = 0;
        for (News news : newsList) {
            StackPane newsCard = createNewsCard(news, Duration.millis(100 * i++ + 1000));
            children.add(newsCard);
        }
        masonryPane.getChildren().addAll(children);
        Platform.runLater(() -> scrollPane.requestLayout());

        log.info("updateNews finished");
    }

    public void updateNews() {
        log.info("downloading news");
        UpdateNewsTask updateNewsTask = new UpdateNewsTask();
        LauncherController.getInstance().queueProgressTask(updateNewsTask);
    }

    private StackPane createNewsCard(News news, Duration duration) {
        StackPane child = new StackPane();
        double width = 690;
        child.setPrefWidth(width);
        double height = 50;
        child.setPrefHeight(height);

        // create content
        StackPane header = new StackPane();
        header.setStyle("-fx-background-radius: 5 5 0 0; -fx-background-color: #888888");
        header.setMinHeight(30);
        Label titleLabel = new Label();
        titleLabel.setTextAlignment(TextAlignment.LEFT);
        titleLabel.setText(news.getTitle());
        titleLabel.setStyle("-fx-text-fill: #f2f5f4; -fx-font-size: 14px; -fx-font-weight: bold");
        StackPane.setMargin(titleLabel, new Insets(6, 40, 6, 6));
        StackPane.setAlignment(titleLabel, Pos.CENTER_LEFT);
        header.getChildren().add(titleLabel);

        StackPane body = new StackPane();
        Label contentLabel = new Label();
        contentLabel.setText(news.getContent());
        contentLabel.setStyle("-fx-text-fill: #161618; -fx-font-size: 14px;");
        contentLabel.setWrapText(true);
        contentLabel.setTextAlignment(TextAlignment.LEFT);
        StackPane.setMargin(contentLabel, new Insets(6, 40, 6, 6));
        StackPane.setAlignment(contentLabel, Pos.CENTER_LEFT);
        body.getChildren().add(contentLabel);

        VBox.setVgrow(body, Priority.ALWAYS);
        VBox content = new VBox();
        content.getChildren().addAll(header, body);
        body.setStyle("-fx-background-radius: 0 0 5 5; -fx-background-color: #f2f5f4;");

        // create button
        Button button = new Button("");
        button.setStyle("-fx-background-radius: 0;-fx-background-color:transparent;");
        button.setPrefSize(25, 25);
        button.setMinSize(20, 20);
        button.setMaxSize(25, 25);
        button.setScaleX(0);
        button.setScaleY(0);

        String imagePath;
        switch (news.getNewsType()) {
            case NEWS:
                imagePath = "/drawable/announcement.png";
                break;
            case MISSION:
                imagePath = "/drawable/calendar.png";
                break;
            case DONATION:
                imagePath = "/drawable/euro.png";
                break;
            default:
                imagePath = "/drawable/link.png";
                break;
        }
        Image image = new Image(this.getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(28);
        imageView.setFitHeight(28);
        button.setGraphic(imageView);
        button.translateYProperty().bind(Bindings.createDoubleBinding(() -> -1.0, header.boundsInParentProperty(), button.heightProperty()));
        StackPane.setMargin(button, new Insets(2, 1, 0, 0));
        StackPane.setAlignment(button, Pos.TOP_RIGHT);

        button.setOnAction(event -> {
            try {
                Desktop.getDesktop().browse(new URI(news.getWeblink()));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        });

        Timeline animation = new Timeline(new KeyFrame(Duration.millis(240),
                new KeyValue(button.scaleXProperty(),
                        1,
                        EASE_BOTH),
                new KeyValue(button.scaleYProperty(),
                        1,
                        EASE_BOTH)));
        animation.setDelay(duration);
        animation.play();
        child.getChildren().addAll(content, button);

        return child;
    }
}
