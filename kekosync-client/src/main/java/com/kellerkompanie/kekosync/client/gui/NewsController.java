package com.kellerkompanie.kekosync.client.gui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXButton.ButtonType;
import com.jfoenix.controls.JFXMasonryPane;
import com.jfoenix.controls.JFXScrollPane;
import com.jfoenix.effects.JFXDepthManager;
import com.jfoenix.svg.SVGGlyph;
import com.kellerkompanie.kekosync.client.gui.task.UpdateNewsTask;
import com.kellerkompanie.kekosync.core.entities.News;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
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

@Slf4j
public class NewsController implements Initializable {


    private static NewsController instance;
    @FXML
    private ScrollPane scrollPane;
    @FXML
    private JFXMasonryPane masonryPane;


    public static NewsController getInstance() {
        return instance;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        instance = this;
    }

    public void updateNews(List<News> newsList) throws IOException {
        ArrayList<Node> children = new ArrayList<>();
        int i = 0;
        for (News news : newsList) {
            StackPane newsCard = createNewsCard(news, Duration.millis(100 * i++ + 1000));
            children.add(newsCard);
        }
        masonryPane.getChildren().addAll(children);
        Platform.runLater(() -> scrollPane.requestLayout());

        JFXScrollPane.smoothScrolling(scrollPane);

        log.info("updateNews finished");
    }

    public void updateNews() {
        log.info("downloading news");
        UpdateNewsTask updateNewsTask = new UpdateNewsTask();
        LauncherController.getInstance().queueProgressTask(updateNewsTask);
    }

    private StackPane createNewsCard(News news, Duration duration) throws IOException {
        StackPane child = new StackPane();
        double width = 690;
        child.setPrefWidth(width);
        double height = 50;
        child.setPrefHeight(height);
        JFXDepthManager.setDepth(child, 1);


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
        JFXButton button = new JFXButton("");
        button.setButtonType(ButtonType.RAISED);
        button.setStyle("-fx-background-radius: 40;-fx-background-color: #ee4d2e;");
        button.setPrefSize(40, 40);
        button.setRipplerFill(Color.valueOf("#ee4d2e"));
        button.setScaleX(0);
        button.setScaleY(0);
        String svgPath = "";
        switch (news.getNewsType()) {
            case NEWS:
                svgPath = SVGIcons.ANNOUNCEMENT;
                break;
            case MISSION:
                svgPath = SVGIcons.WEB;
                break;
            case DONATION:
                svgPath = SVGIcons.EURO;
                break;
        }
        SVGGlyph glyph = new SVGGlyph(-1, "test", svgPath, Color.WHITE);
        glyph.setSize(20, 20);
        button.setGraphic(glyph);
        button.translateYProperty().bind(Bindings.createDoubleBinding(() -> header.getBoundsInParent().getHeight() - button.getHeight() / 2, header.boundsInParentProperty(), button.heightProperty()));
        StackPane.setMargin(button, new Insets(0, 12, 0, 0));
        StackPane.setAlignment(button, Pos.TOP_RIGHT);

        button.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    Desktop.getDesktop().browse(new URI(news.getWeblink()));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
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
