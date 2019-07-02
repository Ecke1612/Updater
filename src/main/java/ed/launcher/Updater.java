package ed.launcher;

import javafx.scene.control.Alert;
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
    private boolean enableAlerts = false;

    private String newBuild = null;
    private final String server = "ecke1612.bplaced.net";
    private final String user = "ecke1612_interval";
    private final String pw = "Interval#18";
    //private AppObject appObject;


    public boolean checkForUpdate(AppObject appObject, int installedVersion) throws Exception {
        //this.appObject = appObject;
        try {
            ftp_handler = new FTP_Handler(server, user, pw);
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
        if(fileExist(appObject.getLocalPath() + "ver/newbuild.txt")) {
            try (BufferedReader reader = new BufferedReader((new InputStreamReader( new FileInputStream(appObject.getLocalPath() + "/ver/newbuild.txt"), "UTF-8")))) {
                //Wenn durch Windows TextCodierung der UTF-8 Stream nicht mit readable Code anfängt, dann lösche es raus
                reader.mark(1);
                if (reader.read() != 0xFEFF)
                    reader.reset();
                newBuild = reader.readLine();
                System.out.println("new build new: " + newBuild);
                //float newBuildFloat = Float.parseFloat(newBuild);
                //if(!newBuild.equals(null) && !newBuild.equals(ed.Launcher.EDUpdater.build)) {
                if(Integer.parseInt(newBuild) > installedVersion) {
                    if(enableAlerts) {
                        if (alerts.confirmDialogFX("Update", "Es ist ein Update verfügbar", "Möchtest du " + appObject.getName() + " aktualisieren?")) {
                            System.out.println("True");
                            return true;
                        } else {
                            System.out.println("False");
                            return false;
                        }
                    } else {
                        return true;
                    }
                }
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Update konnte nicht ausgeführt werden, Server nicht erreichbar");
            }
            return false;
        }
        System.out.println("build Datei nicht vorhanden");
        return false;
    }

    public int getInstalledVersion(AppObject appObject) {
        if(fileExist(appObject.getLocalPath() + "ver/newbuild.txt")) {
            try (BufferedReader reader = new BufferedReader((new InputStreamReader( new FileInputStream(appObject.getLocalPath() + "/ver/newbuild.txt"), "UTF-8")))) {
                //Wenn durch Windows TextCodierung der UTF-8 Stream nicht mit readable Code anfängt, dann lösche es raus
                reader.mark(1);
                if (reader.read() != 0xFEFF) reader.reset();

                int newBuild = Integer.parseInt(reader.readLine());
                System.out.println("installed build: " + newBuild);
                return newBuild;
            } catch (IOException e) {
                //e.printStackTrace();
                System.out.println("Update konnte nicht ausgeführt werden, Server nicht erreichbar");
            }
        }
        System.out.println("build Datei nicht vorhanden");
        return 0;
    }

    public void update(AppObject appObject) throws IOException, ParseException {
        if(!fileExist(appObject.getLocalPath() + "ver")) createDir(appObject.getLocalPath() + "ver");
        ftp_handler.downloadFile(appObject.getInitialPath() + "/" + appObject.getName() + "_" + newBuild + ".jar", appObject.getLocalPath() +  appObject.getName() + ".jar");
        ftp_handler.downloadFile(appObject.getInitialPath() + "/alist.json", appObject.getLocalPath() + "ver/alist");

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(appObject.getLocalPath() + "ver/alist"));
        JSONObject jsonObj = (JSONObject) obj;

        JSONArray deleteArray = (JSONArray) jsonObj.get("delete");
        for(int i = 0; i < deleteArray.size(); i++) {
            String path = deleteArray.get(i).toString();
            File file = new File( appObject.getLocalPath() + path);
            file.delete();
            System.out.println("deleted: " + appObject.getLocalPath() + path);
        }

        JSONArray addArray = (JSONArray) jsonObj.get("add");
        for(int x = 0; x < addArray.size(); x++) {
            JSONObject addObject = (JSONObject) addArray.get(x);
            for (Object key : addObject.keySet()) {
                String keyStr = (String) key;
                Object keyvalue = addObject.get(keyStr);
                System.out.println("key: "+ keyStr + " value: " + keyvalue);
                ftp_handler.downloadFile(appObject.getInitialPath() + "/" + keyStr, appObject.getLocalPath() + keyvalue.toString());
            }
        }
        System.out.println("heruntergeladen");
        ftp_handler.disconnect();
    }

    public void showChangeLog(AppObject appObject) {
        //if(enableAlerts) {
        ArrayList<String> log = null;
        try {
            log = fileLoader(appObject.getLocalPath() + "ver/newbuild.txt");

            System.out.println("Changelog");

            String content = "";
            for (String entry : log) {
                content = content + entry + "\n";
            }

            alerts.confirmDialogFX("Changelog", "Hinweise zur aktuellen Version", content);
            /*Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Changelog");
            alert.setHeaderText(null);
            alert.setContentText(content);*/

            //alert.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //}
        //renameBuildFile();
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

    public static ArrayList<String> fileLoader(String stringPath) throws IOException {
        ArrayList<String> data = new ArrayList<>();
        try {
            FileReader fr = new FileReader(stringPath);
            BufferedReader br = new BufferedReader(fr);
            String line;
            while((line = br.readLine()) != null) {
                data.add(line);
            }
            fr.close();
            br.close();

            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void createDir(String name) {
        File dir = new File(name);
        dir.mkdir();
    }

}
