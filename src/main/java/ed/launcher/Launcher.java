package ed.launcher;

import com.ed.filehandler.PlainFileHandler;
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

    public static final int version = 243;
    public static ConfigObject configObject;
    private PlainFileHandler plainFileHandler = new PlainFileHandler();

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
        if(!plainFileHandler.fileExist("ver")) plainFileHandler.createDir("ver");
        if(!plainFileHandler.fileExist("bin")) createAppFolders();

        AppObject appObject = new AppObject(0, "ED_FX_Launcher", "ED_FX_Launcher.jar", "/apps/ed_launcher/build.txt",
                "/apps/ed_launcher", "");
        EDUpdater edUpdater = new EDUpdater();
        if(edUpdater.checkForUpdates(appObject))
        {
            System.out.println("found new Version");
            edUpdater.executeUpdate(appObject);
        }

    }

    private void createAppFolders() {
        plainFileHandler.createDir("bin");
        plainFileHandler.createDir("bin/apps");
    }

}
