package com.MediaPlayer.Controller.tutorial;

import com.MediaPlayer.Controller.changeButtonPicture;
import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class tutorial_main {
    @FXML
    WebView webView;

    @FXML
    ImageView openInBrowser_img;
    @FXML
    ImageView language_img;

    @FXML
    JFXButton openInBrowser_btn;
    @FXML
    JFXButton language_btn;

    @FXML
    AnchorPane progress;
    @FXML
    AnchorPane progressBack;
    @FXML
    AnchorPane functionBar;
    @FXML
    AnchorPane parentPane;

    @FXML
    JFXButton okay_btn;

    changeButtonPicture changeButtonPicture = new changeButtonPicture();

    boolean trackUrl = false;

    String currentUrl;

    public void loading(){
        loadPage("https://seechen.github.io/lumacaMediaPlayer/howToUse/howToUse-en.html");
        currentUrl = "https://seechen.github.io/lumacaMediaPlayer/howToUse/howToUse-en.html";

        changeButtonPicture.changeBtnPicture("webBrowser", openInBrowser_img);
        changeButtonPicture.changeBtnPicture("rate", language_img);

        setTooltip("Open this page in Default Browser", openInBrowser_btn);
        setTooltip("中文", language_btn);

        openInBrowser_btn.setOnAction(e -> {
            String url = webView.getEngine().getLocation();
            goTo(url);
        });
        language_btn.setOnAction(e -> {
            trackUrl = false;
            String[] text = new String[3];
            if(language_btn.getText().equals("中")){
                text[0] = "EN";
                text[1] = "English";
                text[2] = "https://seechen.github.io/lumacaMediaPlayer/howToUse/howToUse-zh.html";
            }else{
                text[0] = "中";
                text[1] = "中文";
                text[2] = "https://seechen.github.io/lumacaMediaPlayer/howToUse/howToUse-en.html";
            }
            language_btn.setText(text[0]);
            setTooltip(text[1], language_btn);
            currentUrl = text[2];
            loadPage(text[2]);
        });
    }

    private void setTooltip(String tips, JFXButton btnId){
        Tooltip tooltip = new Tooltip();
        tooltip.setText(tips);
        tooltip.setStyle("-fx-font-size: 12pt; -fx-background-color: #85DDFF; -fx-text-fill: white;");
        btnId.setTooltip(tooltip);
    }

    public void clearMemory(){
        webView.getEngine().load("about:blank");
        java.net.CookieHandler.setDefault(new java.net.CookieManager());
    }

    private void loadPage(String webUrl){
        webView.setVisible(false);
        progress.setVisible(true);
        progressBack.setVisible(true);

        functionBar.setVisible(false);
        okay_btn.setVisible(false);

        new Thread(() -> Platform.runLater(() -> {
            webView.getEngine().load(webUrl);

            webView.getEngine().getLoadWorker().progressProperty().addListener((observable, oldValue, newValue) -> {
                double num = newValue.doubleValue();
                progress.setPrefWidth(num * 343);
            });

            webView.getEngine().getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {

                if(trackUrl) {
                    if(newValue == Worker.State.RUNNING) {
                        String toOpen = webView.getEngine().getLoadWorker().getMessage().trim();
                        if (toOpen.contains("https://")) {
                            goTo(webView.getEngine().getLocation());
                            trackUrl = false;
                            loadPage(currentUrl);
                        }
                    }
                }

                if(newValue == Worker.State.SUCCEEDED){
                    new Thread(() -> {
                        try {
                            Thread.sleep(500);
                            webView.setVisible(true);
                            progress.setVisible(false);
                            progressBack.setVisible(false);

                            functionBar.setVisible(true);
                            okay_btn.setVisible(true);

                            trackUrl = true;
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            });
        })).start();
    }

    private void goTo(String url){
        try {
            Desktop.getDesktop().browse(new URL(url).toURI());
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void close(){
        clearMemory();
        Stage stage = (Stage) parentPane.getScene().getWindow();
        stage.close();
    }
}
