package ed.launcher;

import java.io.IOException;

/**
 * Created by Eike on 14.09.2018.
 */

public class EDUpdater {

    private Updater updater = new Updater();
    private Processer processer = new Processer();
    private String executPath;


    public EDUpdater() {
        System.out.println("Launcher Version: " + Launcher.version);
    }

    public void startApp(AppObject appObject) {
        executPath = "bin/apps/" + appObject.getName() + "/" + appObject.getExec();
        System.out.println("starting " + appObject.getName());
        String[] formatArray = appObject.getExec().split("\\.");
        String format = formatArray[1];
        try {
            if(format.equals("jar")) {
                processer.startJar(executPath, Launcher.configObject.getOs());
            }
            else if(format.equals("bat")) {
                processer.startBat(executPath);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean checkForUpdates(AppObject appObject) {
        boolean isNewVersion = false;
        try {
            int installedVersion = updater.getInstalledVersion(appObject);
            isNewVersion = updater.checkForUpdate(appObject, installedVersion);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return isNewVersion;
    }

    public void executeUpdate(AppObject appObject) {
        try {
            processer.destroyProcess();
            updater.update(appObject);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void showChangeLog(AppObject appObject) {
        updater.showChangeLog(appObject);
    }

    public Updater getUpdater() {
        return updater;
    }

}
