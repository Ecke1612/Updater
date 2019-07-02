package ed.launcher.controller;

import ed.launcher.AppObject;
import ed.launcher.EDUpdater;
import ed.launcher.Launcher;
import ed.launcher.Updater;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MainWindow_Controller {

    @FXML
    public Button btn_settings;
    @FXML
    public VBox vbox_apps;
    @FXML
    public Label label_version;

    private Stage primaryStage;
    private ArrayList<AppObject> appObjects;
    private EDUpdater edUpdater = new EDUpdater();
    private boolean updateAvailable = false;
    private boolean appStart = true;

    public MainWindow_Controller(ArrayList<AppObject> appObjects, Stage primaryStage) {
        this.appObjects = appObjects;
        this.primaryStage = primaryStage;
    }

    public void initialize() {
        label_version.setText("Version " + Launcher.version);
        vbox_apps.getChildren().clear();
        for(AppObject o : appObjects) {
            vbox_apps.getChildren().add(getAppRow(o, checkifinstalled(o)));
        }
    }

    private VBox getAppRow(AppObject appObject, boolean installed) {
        VBox vboxMain = new VBox(0);
        vboxMain.setPadding(new Insets(0,5,0,5));
        vboxMain.setStyle(
                "-fx-background-color: rgb(218,218,218)"
        );

        Label labelOut = new Label();
        labelOut.setStyle("-fx-font-size: 11;" +
                "-fx-text-fill: rgb(65,65,65);"
        );

        HBox hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER_LEFT);
        //hbox.getStylesheets().add();

        Label label_AppName = new Label(appObject.getName());
        label_AppName.setStyle(
                "-fx-font-size: 20;" +
                "-fx-text-fill: rgb(45,45,45)"
        );

        HBox hbox_expander = new HBox();
        hbox.setHgrow(hbox_expander, Priority.ALWAYS);
        hbox.getStylesheets().add(this.getClass().getResource("/fxml/mainStyle.css").toExternalForm());

        Button btn_start = new Button("Starten");
        btn_start.getStyleClass().add("btn_start");
        /*btn_start.setStyle(
                "-fx-background-color: rgb(255,250,254);" +
                        "-fx-font-size: 16;" +
                        "-fx-border-radius: 6;"
        );*/
        btn_start.setPadding(new Insets(-1,2,-1,2));

        ChoiceBox cb_start = new ChoiceBox();
        cb_start.setPadding(new Insets(-1,2,-1,2));


        Label label_popup = new Label("\uE011");
        label_popup.setStyle(
                "-fx-background-color: white;" +
                "-fx-font-family: 'Segoe MDL2 Assets';" +
                "-fx-font-size: 12;" +
                "-fx-border-radius: 3;"
        );


        ContextMenu popup=new ContextMenu();
        MenuItem itemUninstall=new MenuItem("Deinstallieren");
        MenuItem itemChangeLog=new MenuItem("Changelog");
        popup.getItems().add(itemUninstall);
        popup.getItems().add(itemChangeLog);

        itemUninstall.setOnAction(event -> {
            System.out.println("uninstall");
            try {
                FileUtils.deleteDirectory(new File(appObject.getLocalPath()));
                initialize();
                labelOut.setText(appObject.getName() + " wurde deinstalliert");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        itemChangeLog.setOnAction(event -> {
            edUpdater.showChangeLog(appObject);
        });


        label_popup.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent t) {
                if (t.getButton() == MouseButton.PRIMARY) {
                    popup.show(primaryStage, t.getScreenX(), t.getScreenY());
                }
            }
        });

        if(installed) {
            btn_start.setOnAction(event -> {
                try {
                    edUpdater.startApp(appObject);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            btn_start.setText("Installieren");
            btn_start.setOnAction(event -> {
                try {
                    Updater.createDir(appObject.getLocalPath());
                    edUpdater.checkForUpdates(appObject);
                    edUpdater.executeUpdate(appObject);
                    btn_start.setText("Starten");
                    initialize();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


        Button btn_update = new Button("\uE117");
        btn_update.setStyle(
                "-fx-font-family: 'Segoe MDL2 Assets';" +
                "-fx-background-color: rgb(255,250,254);" +
                "-fx-font-size: 14;" +
                "-fx-border-radius: 6;"
        );
        btn_update.setPadding(new Insets(1));

        btn_update.setOnAction(event -> {
            if(!updateAvailable) {
                updateAvailable = edUpdater.checkForUpdates(appObject);
                if(updateAvailable) {
                    btn_update.setText("\uE118");
                    labelOut.setText("Update verfügbar");
                } else {
                    labelOut.setText("Sie sind auf dem neusten Stand");
                }
            } else {
                labelOut.setText("Update wird heruntergeladen und installiert");
                //btn_start.setDisable(true);
                btn_update.setDisable(true);



                new Thread(() -> Platform.runLater(() -> {
                        edUpdater.executeUpdate(appObject);
                        updateAvailable = false;
                        btn_update.setText("\uE117");
                        labelOut.setText("Update erfolgreich ausgefüht");
                        //btn_start.setDisable(false);
                        btn_update.setDisable(false);
                })).start();
            }
        });

        if(appStart && installed) {
            updateAvailable =  edUpdater.checkForUpdates(appObject);
            if(updateAvailable) {
                btn_update.setText("\uE118");
                labelOut.setText("Update verfügbar");
            } else {
                labelOut.setText("Sie sind auf dem neusten Stand");
            }
        }

        hbox.getChildren().addAll(label_AppName, hbox_expander, btn_start, btn_update, label_popup);

        vboxMain.getChildren().addAll(hbox, labelOut);

        return vboxMain;
    }

    private VBox getPopupContent() {
        VBox vbox = new VBox(5);
        Button btn_uninstall = new Button("deinstallieren");
        btn_uninstall.setOnAction(event -> {
            System.out.println("test");
        });

        vbox.getChildren().add(btn_uninstall);

        return vbox;
    }

    private boolean checkifinstalled(AppObject appObject) {
        if(!Updater.fileExist(appObject.getLocalPath() + "/" + appObject.getName() + ".jar")) {
            return false;
        }
        return true;
    }

    public void showSettings() {
        System.out.println("test");
    }

}
