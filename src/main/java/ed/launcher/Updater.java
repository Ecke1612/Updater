package ed.launcher;

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

/**
 * Created by Eike on 17.06.2017.
 */
public class Updater {

    private FTP_Handler ftp_handler;
    private Alert_Windows alerts = new Alert_Windows();

    private String newBuild = null;


    public boolean checkForUpdate(AppObject appObject, int installedVersion) throws Exception {
        System.out.println("Check for Updates");
        ServerData serverData = new ServerData();
        try {
            ftp_handler = new FTP_Handler(serverData.getServer(), serverData.getUser(), serverData.getPw());
            if(!fileExist(appObject.getLocalPath() + "ver")) {
                createDir(appObject.getLocalPath() + "ver");
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
        if(fileExist(appObject.getLocalPath() + "ver/newbuild.txt")) {
            newBuild = readLine(appObject.getLocalPath() + "ver/newbuild.txt");
            System.out.println("new build new: " + newBuild);
            if(Integer.parseInt(newBuild) > installedVersion) {
                return alerts.confirmDialogFX("Update", "Es ist ein Update verfügbar", "Möchtest du " + appObject.getName() + " aktualisieren?");
            }
        }
        System.out.println("build Datei nicht vorhanden");
        return false;
    }

    public int getInstalledVersion(AppObject appObject) {
        if(fileExist(appObject.getLocalPath() + "ver/newbuild.txt")) {
            String firstLine = readLine(appObject.getLocalPath() + "ver/newbuild.txt");
            int newBuild = Integer.parseInt(firstLine);
            System.out.println("installed build: " + newBuild);
            return newBuild;
        }
        System.out.println("build Datei nicht vorhanden");
        return 0;
    }

    public void update(AppObject appObject) {
        if(!fileExist(appObject.getLocalPath() + "ver")) createDir(appObject.getLocalPath() + "ver");
        ftp_handler.downloadFile(appObject.getInitialPath() + "/" + appObject.getName() + "_" + newBuild + ".jar", appObject.getLocalPath() +  appObject.getName() + ".jar");
        ftp_handler.downloadFile(appObject.getInitialPath() + "/alist.json", appObject.getLocalPath() + "ver/alist");

        executeAListInstructions(appObject);

        System.out.println("heruntergeladen");
        ftp_handler.disconnect();

    }

    private void executeAListInstructions(AppObject appObject) {
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader(appObject.getLocalPath() + "ver/alist"));
            JSONObject jsonObj = (JSONObject) obj;

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
        } catch (ParseException | IOException e) {
            e.printStackTrace();
        }
    }

    public void showChangeLog(AppObject appObject) {
        ArrayList<String> log = null;
        try {
            log = fileLoader(appObject.getLocalPath() + "ver/newbuild.txt");
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

    public void renameBuildFile(AppObject appObject) throws IOException {
        if(fileExist(appObject.getLocalPath() + "ver/newbuild.txt")) {
//            File_Handler.deleteFile("ver/newbuild.txt");
            System.out.println("build.txt gelöscht");
        }
    }

    public static boolean fileExist(String path) {
        File file = new File(path);
        if(file.exists()) {
            return true;
        }
        return false;
    }

    private ArrayList<String> fileLoader(String stringPath) throws IOException {
        ArrayList<String> data = new ArrayList<>();
        try {
            //FileReader fr = new FileReader(stringPath);
            //BufferedReader br = new BufferedReader(fr);
            String line;

            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(stringPath), "Cp1252"));
            while((line = reader.readLine()) != null) {
                data.add(line);
            }
            reader.close();
            //br.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    private String readLine(String path) {
        BufferedReader brLine = null;
        try {
            brLine = new BufferedReader(new FileReader(path));
            String line = brLine.readLine();
            System.out.println("Firstline is : " + line);
            return line;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void createDir(String name) {
        File dir = new File(name);
        dir.mkdir();
    }

}
