package com.kellerkompanie.kekosync.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kellerkompanie.kekosync.core.entities.News;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Schwaggot
 */
public class LauncherMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/layout/Launcher.fxml"));

        stage.setTitle("KekoSync");
        stage.getIcons().add(new Image(this.getClass().getResourceAsStream("/drawable/kk-signet-small-color.png")));
        stage.setMinWidth(800);
        stage.setMinHeight(600);
        //stage.setResizable(false);

        Scene scene = new Scene(root, 800, 600);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.addAll(getClass().getResource("/css/jfoenix-design.css").toExternalForm(),
                getClass().getResource("/css/jfoenix-main-demo.css").toExternalForm(),
                getClass().getResource("/css/custom.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
    }

    private void createDefaultNews() {
        ArrayList<News> newsList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            News news = News.createDefaultNews();
            newsList.add(news);
        }
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String json = gson.toJson(newsList);

        try {
            //write converted json data to a file named "CountryGSON.json"
            FileWriter writer = new FileWriter("news.json");
            writer.write(json);
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
