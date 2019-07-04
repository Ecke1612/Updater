package ed.launcher.controller;

import ed.launcher.AppObject;
import ed.launcher.EDUpdater;
import javafx.concurrent.Task;

public class UpdaterTask extends Task {

    private EDUpdater edUpdater;
    private AppObject appObject;

    public UpdaterTask(EDUpdater edUpdater, AppObject appObject) {
        this.edUpdater = edUpdater;
        this.appObject = appObject;
    }

    @Override
    protected Object call() throws Exception {
        edUpdater.executeUpdate(appObject);
        return null;
    }
}
