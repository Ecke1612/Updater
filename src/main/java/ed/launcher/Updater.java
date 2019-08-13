package ed.launcher;

import com.ed.filehandler.JsonHandler;
import com.ed.filehandler.PlainFileHandler;
import ed.launcher.controller.ChangeLogController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Created by Eike on 17.06.2017.
 */
public class Updater {

    private FTP_Handler ftp_handler;
    private Alert_Windows alerts = new Alert_Windows();
    private PlainFileHandler plainFileHandler = new PlainFileHandler();
    private JsonHandler jsonHandler = new JsonHandler();

    private String newBuild = null;


    public int getInstalledVersion(AppObject appObject) {
        if(plainFileHandler.fileExist(appObject.getLocalPath() + "ver/build.txt")) {
            String firstLine = plainFileHandler.readFirstLine(appObject.getLocalPath() + "ver/build.txt");
            int newBuild = Integer.parseInt(firstLine);
            System.out.println("installed build: " + newBuild);
            return newBuild;
        }
        System.out.println("build Datei nicht vorhanden");
        return 0;
    }

    public boolean checkForUpdate(AppObject appObject, int installedVersion) throws Exception {
        System.out.println("Check for Updates");
        ServerData serverData = new ServerData();
        try {
            ftp_handler = new FTP_Handler(serverData.getServer(), serverData.getUser(), serverData.getPw());
            if(!plainFileHandler.fileExist(appObject.getLocalPath() + "ver")) {
                plainFileHandler.createDir(appObject.getLocalPath() + "ver");
            }
            ftp_handler.downloadFile(appObject.getRemoteTextPath(), appObject.getLocalPath() + "ver/newbuild.txt");
            System.out.println("build erfolgreich heruntergeladen");
            boolean newVersionAvailable = compareLocalAndRemoteVersion(appObject, installedVersion);
            System.out.println("new Version found: " + newVersionAvailable);
            return newVersionAvailable;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fehler beim updaten");
            return false;
        }
    }

    private boolean compareLocalAndRemoteVersion(AppObject appObject, int installedVersion) {
        System.out.println("Compare Installed and Remote Version");
        if(plainFileHandler.fileExist(appObject.getLocalPath() + "ver/newbuild.txt")) {
            newBuild = plainFileHandler.readFirstLine(appObject.getLocalPath() + "ver/newbuild.txt");
            System.out.println("new build new: " + newBuild);
            if(Integer.parseInt(newBuild) > installedVersion) {
                //return alerts.confirmDialogFX("Update", "Es ist ein Update verfügbar", "Möchtest du " + appObject.getName() + " aktualisieren?");
                return true;
            }
        }
        System.out.println("build Datei nicht vorhanden");
        return false;
    }

    public void update(AppObject appObject) {
        if(!plainFileHandler.fileExist(appObject.getLocalPath() + "ver")) plainFileHandler.createDir(appObject.getLocalPath() + "ver");
        ftp_handler.downloadFile(appObject.getInitialPath() + "/" + appObject.getName() + "_" + newBuild + ".jar", appObject.getLocalPath() +  appObject.getName() + ".jar");
        ftp_handler.downloadFile(appObject.getInitialPath() + "/alist.json", appObject.getLocalPath() + "ver/alist");

        executeAListInstructions(appObject);

        System.out.println("heruntergeladen");
        ftp_handler.disconnect();

        renamingBuildFile(appObject);
    }

    private void renamingBuildFile(AppObject appObject) {
        if(plainFileHandler.fileExist(appObject.getLocalPath() + "ver/newbuild.txt")) {
            File newBuildFile = new File(appObject.getLocalPath() + "ver/newbuild.txt");
            File buildFile = new File(appObject.getLocalPath() + "ver/build.txt");
            boolean sucess = newBuildFile.renameTo(buildFile);
            if(!sucess) System.out.println("newBuild was not successfully renamed!");
            else {
                newBuildFile.delete();
                System.out.println("newBuild deleted");
            }
        }
    }

    private void executeAListInstructions(AppObject appObject) {
        JSONObject jsonObj = jsonHandler.readJsonData(appObject.getLocalPath() + "ver/alist");

        JSONArray deleteArray = (JSONArray) jsonObj.get("delete");
        for (int i = 0; i < deleteArray.size(); i++) {
            String path = deleteArray.get(i).toString();
            File file = new File(appObject.getLocalPath() + path);
            file.delete();
            System.out.println("deleted: " + appObject.getLocalPath() + path);
        }

        JSONArray addArray = (JSONArray) jsonObj.get("add");
        for (int x = 0; x < addArray.size(); x++) {
            JSONObject addObject = (JSONObject) addArray.get(x);
            for (Object key : addObject.keySet()) {
                String keyStr = (String) key;
                Object keyvalue = addObject.get(keyStr);
                System.out.println("key: " + keyStr + " value: " + keyvalue);
                ftp_handler.downloadFile(appObject.getInitialPath() + "/" + keyStr, appObject.getLocalPath() + keyvalue.toString());
            }
        }

    }

    public void showChangeLog(AppObject appObject) {
        ArrayList<String> log = null;
        try {
            log = plainFileHandler.fileLoaderStream(appObject.getLocalPath() + "ver/build.txt", "Cp1252");
            String content = "";
            for (String entry : log) {
                content = content + entry + "\n";
            }

            Stage logstage = new Stage();
            FXMLLoader fxmlLoader = null;
            fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/changelog.fxml"));
            ChangeLogController changeLogController = new ChangeLogController(content, logstage);
            fxmlLoader.setController(changeLogController);
            Parent root = fxmlLoader.load();
            Scene scene = new Scene(root);

            logstage.setScene(scene);
            logstage.setTitle("Changelog: " + appObject.getName());
            logstage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
