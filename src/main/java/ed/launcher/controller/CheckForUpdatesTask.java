package ed.launcher.controller;

import ed.launcher.AppObject;
import ed.launcher.EDUpdater;
import javafx.concurrent.Task;

public class CheckForUpdatesTask extends Task {

    private EDUpdater edUpdater;
    private AppObject appObject;

    public CheckForUpdatesTask(EDUpdater edUpdater, AppObject appObject) {
        this.edUpdater = edUpdater;
        this.appObject = appObject;
    }

    @Override
    protected Object call() throws Exception {
        edUpdater.checkForUpdates(appObject);
        return null;
    }
}
