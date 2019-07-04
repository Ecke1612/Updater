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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainWindow_Controller {

    @FXML
    public Button btn_settings;
    @FXML
    public VBox vbox_apps;
    @FXML
    public Label label_version;

    private Stage primaryStage;
    private ArrayList<AppObject> appObjects;
    //private EDUpdater edUpdater = new EDUpdater();
    private boolean updateAvailable = false;
    private boolean appStart = true;
    private int threadCounter = 0;

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
        EDUpdater edUpdater = new EDUpdater();

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

        Label label_AppName = new Label(appObject.getName());
        label_AppName.setStyle(
                "-fx-font-size: 20;" +
                "-fx-text-fill: rgb(45,45,45)"
        );

        HBox hbox_expander = new HBox();
        hbox.setHgrow(hbox_expander, Priority.ALWAYS);
        hbox.getStylesheets().add(this.getClass().getResource("/fxml/mainStyle.css").toExternalForm());

        Button btn_start = initBtn_Start();

        Label label_popup = new Label("\uE011");
        label_popup.setStyle(
                "-fx-background-color: white;" +
                "-fx-font-family: 'Segoe MDL2 Assets';" +
                "-fx-font-size: 12;" +
                "-fx-border-radius: 3;"
        );

        initDropdown_Menu(edUpdater, appObject, labelOut, label_popup);

        Button btn_update = initBtn_Update(edUpdater, appObject, labelOut, btn_start);

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
                    installApp(edUpdater, appObject, btn_start, labelOut, btn_update);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }


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

    private Button initBtn_Start() {
        Button btn_start = new Button("Starten");
        btn_start.getStyleClass().add("btn_start");
        /*btn_start.setStyle(
                "-fx-background-color: rgb(255,250,254);" +
                        "-fx-font-size: 16;" +
                        "-fx-border-radius: 6;"
        );*/
        btn_start.setPadding(new Insets(-1,2,-1,2));

        return btn_start;
    }

    private Button initBtn_Update(EDUpdater edUpdater, AppObject appObject, Label labelOut, Button btn_start) {
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

                try {
                    UpdaterTask task = new UpdaterTask(edUpdater, appObject);

                    task.setOnRunning((succeesesEvent) -> {
                        btn_start.setDisable(true);
                        btn_update.setDisable(true);
                        labelOut.setText("Update wird heruntergeladen und installiert");
                    });

                    task.setOnSucceeded((succeededEvent) -> {
                        btn_update.setText("\uE117");
                        labelOut.setText("Update erfolgreich ausgefüht");
                        btn_start.setDisable(false);
                        btn_update.setDisable(false);
                        updateAvailable = false;
                    });

                    ExecutorService executorService = Executors.newFixedThreadPool(1);
                    executorService.execute(task);
                    executorService.shutdown();

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        });
        return btn_update;
    }

    private void initDropdown_Menu(EDUpdater edUpdater, AppObject appObject, Label labelOut, Label label_popup) {
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
    }

    private void installApp(EDUpdater edUpdater, AppObject appObject, Button btn_start, Label labelOut, Button btn_update) {
        CheckForUpdatesTask checkTast = new CheckForUpdatesTask(edUpdater, appObject);

        checkTast.setOnRunning((succeesesEvent1) -> {
            System.out.println("Tcounter start" + threadCounter);
            threadCounter++;
            btn_start.setDisable(true);
            btn_update.setDisable(true);
            labelOut.setText("verifiziere Version");
        });

        checkTast.setOnSucceeded((succeededEvent1) -> {
            UpdaterTask updaterTask = new UpdaterTask(edUpdater, appObject);

            updaterTask.setOnRunning((succeesesEvent2) -> {
                labelOut.setText("Download gestartet");
            });

            updaterTask.setOnSucceeded((succeededEvent2) -> {
                labelOut.setText("Download abgeschlossen");

                threadCounter--;
                System.out.println("Tcounter End" + threadCounter);
                if(threadCounter == 0) {
                    btn_start.setDisable(false);
                    btn_update.setDisable(false);
                    initialize();
                } else {
                    btn_start.setText("wartend");
                    labelOut.setText("Warten auf andere Threads");
                }
            });

            ExecutorService executorService = Executors.newFixedThreadPool(1);
            executorService.execute(updaterTask);
            executorService.shutdown();


        });

        ExecutorService executorService = Executors.newFixedThreadPool(1);
        executorService.execute(checkTast);
        executorService.shutdown();
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
