package ed.launcher;

import ed.launcher.controller.MainWindow_Controller;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Scanner;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    public static final String version = "0.230";
    public static ConfigObject configObject;

    @Override
    public void start(Stage primaryStage) {
        updateLauncher();

        configObject = new ConfigObject();
        configObject.loadConfig();

        Parent mainWindow = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/mainWindow.fxml"));
            MainWindow_Controller mainWindow_controller = new MainWindow_Controller(configObject.getAppObjects(), primaryStage);
            fxmlLoader.setController(mainWindow_controller);
            mainWindow = fxmlLoader.load();

            primaryStage.setTitle("ED Launcher");
            Scene mainWindowScene = new Scene(mainWindow);
            primaryStage.setScene(mainWindowScene);
        } catch (IOException e) {
            e.printStackTrace();
        }

        primaryStage.show();

    }

    private void updateLauncher() {
        System.out.println("update Launcher");
        if(!Updater.fileExist("ver")) Updater.createDir("ver");
        if(!Updater.fileExist("bin")) createAppFolders();

        AppObject appObject = new AppObject(0, "ED_FX_Launcher", "ED_FX_Launcher.jar", "/ed_launcher/build.txt",
                "/ed_launcher", "");
        EDUpdater edUpdater = new EDUpdater();
        if(edUpdater.checkForUpdates(appObject))
        {
            System.out.println("new Version found");
            edUpdater.executeUpdate(appObject);
        }
    }

    private void createAppFolders() {
        Updater.createDir("bin");
        Updater.createDir("bin/apps");
    }

}
