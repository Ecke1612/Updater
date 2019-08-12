package ed.launcher.controller;

import ed.launcher.AppObject;
import ed.launcher.EDUpdater;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UpdaterThread extends Thread {

    private EDUpdater edUpdater;
    private AppObject appObject;
    private Button btn_update;
    private Label labelOut;

    public UpdaterThread(EDUpdater edUpdater, AppObject appObject, Button btn_update, Label labelOut) {
        setDaemon(true);
        this.edUpdater = edUpdater;
        this.appObject = appObject;
        this.btn_update = btn_update;
        this.labelOut = labelOut;
        System.out.println("updater thread constr");
    }

    @Override
    public void run() {
            // UI updaten
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    System.out.println("updater Thread startet");
                    //updating.set(true);
                    edUpdater.executeUpdate(appObject);
                    //updateAvailable = false;
                    //updating.set(false);

                    System.out.println("updating Bool changed to false");
                    btn_update.setText("\uE117");
                    labelOut.setText("Update erfolgreich ausgefüht");
                    btn_update.setDisable(false);
                }
            });


    }
}
