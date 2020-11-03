package sample;

import com.sun.webkit.dom.DocumentFragmentImpl;
import com.sun.xml.internal.ws.api.pipe.Engine;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.web.WebView;

import javax.swing.text.Document;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.Set;

public class Controller implements Initializable {
    DataParsing dataParsing = new DataParsing();
    @FXML
    ComboBox comBox_site;

    @FXML
    ScrollPane scrollcenter;

    @FXML
    ComboBox comBox_genres;

    @FXML
    DatePicker top_date;

    @FXML
    Label label_course;

    @FXML
    Button butt_search;

    @FXML
    VBox center_vbox;

    @FXML
    VBox left_VBox;

    @FXML
    HBox top_hbox;

    @FXML
    BorderPane borderPane;

    @FXML
    TextField search_text;

    private int more = 1;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        long time = System.currentTimeMillis();

        // Дата
        top_date.setValue(LocalDate.from(LocalDateTime.now()));
        //Курс валют
        label_course.setText(dataParsing.getCourse());
        //Сайт та Жанр
        comboBox();
        //Конпка пощуку
        buttonSearch();

        System.out.println(System.currentTimeMillis() - time);
    }

    private void comboBox(){
        comBox_site.getItems().addAll( "korrespondent.net", "ТСН", "УНІАН");
        comBox_site.setValue(String.valueOf(comBox_site.getItems().get(0)));
        comBox_genres.getItems().addAll("Всі", "Світ", "Наука", "Економіка");
        comBox_genres.setValue(String.valueOf(comBox_genres.getItems().get(0)));

        comBox_site.setOnAction(Event->{{
            if(comBox_site.getValue().equals("УНІАН")) {
                search_text.setDisable(true);
                top_date.setDisable(true);
            }
            else {
                search_text.setDisable(false);
                top_date.setDisable(false);
            }
            centerComponent(true);
        }});

        comBox_genres.setOnAction(Event->{{
            centerComponent(true);
        }});
    }

    private void buttonSearch(){
        butt_search.setOnAction(Event ->{
            centerComponent(true);
        });
    }

    private void centerComponent(boolean clear){
        if(clear) {
            center_vbox.getChildren().clear();
            more = 1;
        }
        String genres = String.valueOf(comBox_genres.getValue());
        String site = String.valueOf(comBox_site.getValue());

        dataParsing.getNews(search_text.getText(), site, top_date.getValue(), genres, more);

        Task longTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                butt_search.setDisable(true);
                comBox_site.setDisable(true);
                comBox_genres.setDisable(true);
                int s = dataParsing.getResult;
                if(s > 20) s = 20;
                int max = s;
                for (int i = 0; i < max; i++) {
                    if (isCancelled()) {
                        break;
                    }

                    updateProgress(i, max);

                    int finalI = i;
                    Platform.runLater(() -> {
                        HBox hBox = new HBox();
                        HBox panel = new HBox();

                        //Панелька з назвою та картинкою
                        panel.getStylesheets().add(getClass().getResource("css\\content_pane.css").toExternalForm());
                        panel.getStyleClass().add("pane");
                        panel.setPrefHeight(50);
                        panel.setPrefWidth(510);
                        panel.setOnMouseClicked(event -> {
                            if(!isRunning())
                                openPage(dataParsing.urlPage.get(finalI));
                        });

                        //Картинка

                        String imageSource = dataParsing.imgPage.get(finalI);
                        if(imageSource.equals("") || imageSource == null)
                        {

                        }else {
                            ImageView imageView = new ImageView(new Image(imageSource));
                            imageView.setFitHeight(80);
                            imageView.setFitWidth(140);
                            Rectangle clip = new Rectangle(
                                    imageView.getFitWidth(), imageView.getFitHeight()
                            );
                            clip.setArcWidth(10);
                            clip.setArcHeight(10);
                            imageView.setClip(clip);
                            imageView.setEffect(new DropShadow(20, Color.BLACK));
                            panel.getChildren().add(imageView);
                        }
                        //Назва новини
                        Label label = new Label(dataParsing.namePage.get(finalI));
                        label.setPrefWidth(450);
                        label.setMinHeight(40);
                        label.setMaxWidth(Double.MAX_VALUE);
                        label.setWrapText(true);
                        label.getStylesheets().add(getClass().getResource("css\\content_pane.css").toExternalForm());
                        label.getStyleClass().add("name_article");
                        label.setPadding(new Insets(10, 10, 10, 10));
                        panel.getChildren().add(label);

                        //Час новини
                        Label lbl = new Label(dataParsing.timePage.get(finalI));
                        lbl.getStylesheets().add(getClass().getResource("css\\content_pane.css").toExternalForm());
                        lbl.getStyleClass().add("time_pane");
                        lbl.setPrefHeight(70);
                        lbl.setPrefWidth(150);
                        if(site.equals("УНІАН") && borderPane.getWidth() <= 680) lbl.setPrefWidth(lbl.getPrefWidth() + 50);
                        lbl.setPadding(new Insets(10, 10, 10, 10));
                        lbl.setWrapText(true);

                        hBox.setPadding(new Insets(15, 0, 5, 10));
                        hBox.setSpacing(5);
                        HBox.setHgrow(label, Priority.ALWAYS);
                        HBox.setHgrow(lbl, Priority.ALWAYS);
                        HBox.setHgrow(panel, Priority.ALWAYS);
                        hBox.getChildren().addAll(panel, lbl);

                        center_vbox.getChildren().addAll(hBox);

                        if (finalI == max-1) {
                            center_vbox.getChildren().remove(center_vbox.getChildren().size()-finalI-2);
                            if(dataParsing.viewPage > 0 && more != dataParsing.viewPage){
                                Button viewMore = new Button("Показати більше");

                                viewMore.setOnAction(event -> {
                                    more += 1;
                                    center_vbox.getChildren().remove(center_vbox.getChildren().size()-1);
                                    centerComponent(false);
                                });
                                if(site.equals("korrespondent.net")) {
                                    HBox hButt = new HBox();
                                    viewMore.setMinSize(520, 70);
                                    viewMore.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
                                    viewMore.getStylesheets().add(getClass().getResource("css\\webpage.css").toExternalForm());
                                    viewMore.setStyle("-fx-background-color: none;");
                                    viewMore.getStyleClass().add("viewMore");
                                    HBox.setHgrow(viewMore, Priority.ALWAYS);
                                    hButt.getChildren().add(viewMore);
                                    center_vbox.getChildren().add(hButt);
                                }
                            }
                        }
                    });
                    Thread.sleep(100);
                }

                butt_search.setDisable(false);
                comBox_site.setDisable(false);
                comBox_genres.setDisable(false);


                return null;
            }
        };

        if(dataParsing.getResult != 0) {
            ProgressBar progressBar = new ProgressBar();
            progressBar.setMaxWidth(Double.MAX_VALUE);
            progressBar.getStylesheets().add(getClass().getResource("css\\content_pane.css").toExternalForm());
            progressBar.getStyleClass().add("progress-bar");
            progressBar.progressProperty().bind(longTask.progressProperty());
            new Thread(longTask).start();
            center_vbox.getChildren().add(progressBar);
            HBox.setHgrow(progressBar, Priority.ALWAYS);
        }

    }

    private void openPage(String url){
        left_VBox.setVisible(false);
        top_hbox.setVisible(false);
        left_VBox.managedProperty().bind(left_VBox.visibleProperty());
        top_hbox.managedProperty().bind(top_hbox.visibleProperty());

        center_vbox.setStyle("-fx-background-color: #ffffff;");
        center_vbox.getChildren().clear();
        WebView webView = new WebView();
        String webText = dataParsing.getHTMLView(url);
        webView.getEngine().loadContent(webText);

        webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        webView.getEngine().setJavaScriptEnabled(true);


        webView.getChildrenUnmodifiable().addListener(new ListChangeListener<Node>() {
            @Override public void onChanged(ListChangeListener.Change<? extends Node> change) {
                Set<Node> deadSeaScrolls = webView.lookupAll(".scroll-bar");
                for (Node scroll : deadSeaScrolls) {
                    scroll.setStyle("-fx-background-color: transparent;");
                }
            }
        });

        VBox root = new VBox();

        Button back = new Button(" Назад");
        back.getStylesheets().add(getClass().getResource("css\\webpage.css").toExternalForm());
        back.getStyleClass().add("back");
        back.setOnAction(event -> {
            center_vbox.getChildren().clear();
            center_vbox.setStyle("-fx-background-color: none;");
            left_VBox.setVisible(true);
            scrollcenter.setVbarPolicy(ScrollPane.ScrollBarPolicy.ALWAYS);
            top_hbox.setVisible(true);
            center_vbox.setPadding(new Insets(0, 0, 0, 0));
            centerComponent(true);
        });


        createContextMenu(webView, url);
        webView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        root.getChildren().addAll(back, webView);

        VBox.setVgrow(webView, Priority.ALWAYS);
        scrollcenter.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        center_vbox.setPadding(new Insets(10, 0, 10, 15));
        center_vbox.getChildren().addAll(back, webView);
    }

    int count_notat = 0;

    private void createContextMenu(WebView webView, String url) {
        webView.setContextMenuEnabled(false);
        ContextMenu contextMenu = new ContextMenu();
        MenuItem notatClean = new MenuItem("Видалити нотатки");
        notatClean.setVisible(false);
        MenuItem notat = new MenuItem("Зробити нотатку");
        notat.setOnAction(e -> {
            webView.getEngine().executeScript(
                    "var range = window.getSelection().getRangeAt(0);\n" +
                    "var selectionContents = range.extractContents();\n" +
                    "var div = document.createElement(\"div\");\n" +
                    "div.style.backgroundColor = \"yellow\";\n" +
                    "div.appendChild(selectionContents);\n" +
                    "range.insertNode(div);"
            );
            notatClean.setVisible(true);
            count_notat++;
        });

        notatClean.setOnAction(e -> {
            notatClean.setVisible(false);
            webView.getEngine().loadContent(dataParsing.getHTMLView(url));
            count_notat = 0;
        });

        MenuItem copy = new MenuItem("Копіювати");
        copy.setOnAction(e -> System.out.println("Save page..."));
        contextMenu.getItems().addAll(notat, copy, notatClean);

        webView.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.SECONDARY) {
                contextMenu.show(webView, e.getScreenX(), e.getScreenY());
            } else {
                contextMenu.hide();
            }
        });
    }

}

