
import javafx.scene.control.Alert;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private String appName = "";
    private String server = "";
    private String user = "";
    private String pw = "";
    private String remoteTxtPath = "";
    private String initialPath = "";
    private String localPath = "";


    public boolean checkForUpdate() throws Exception {
        loadConfig();
        try {
            ftp_handler = new FTP_Handler(server, user, pw);
            ftp_handler.downloadFile(remoteTxtPath, "ver/newbuild.txt");
            System.out.println("build erfolgreich heruntergeladen");
            boolean doUpdate = checkVersion();
            if(doUpdate) {
                update();
                ftp_handler.disconnect();
                return true;
            }
            else {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Fehler beim updaten");
            return false;
        }
    }

    private void loadConfig() {
        JSONParser parser = new JSONParser();
        try {
            String filePath = new File("").getAbsolutePath();
            System.out.println(filePath);
            Object obj = parser.parse(new InputStreamReader(getClass().getResourceAsStream( "config/serverconfig.json")));
            JSONObject jsonObject = (JSONObject) obj;
            server = (String) jsonObject.get("server");
            user = (String) jsonObject.get("user");
            pw = (String) jsonObject.get("pw");
            remoteTxtPath = (String) jsonObject.get("remoteTxtPath");
            appName = (String) jsonObject.get("appName");
            initialPath = (String) jsonObject.get("initalPath");
            localPath = (String) jsonObject.get("localPath");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean checkVersion() {
        if(fileExist("ver/newbuild.txt")) {
            try (BufferedReader reader = new BufferedReader((new InputStreamReader( new FileInputStream("ver/newbuild.txt"), "UTF-8")))) {
                //Wenn durch Windows TextCodierung der UTF-8 Stream nicht mit readable Code anfängt, dann lösche es raus
                reader.mark(1);
                if (reader.read() != 0xFEFF)
                    reader.reset();
                newBuild = reader.readLine();
                System.out.println("old build: " + Updater_Main.build);
                //float newBuildFloat = Float.parseFloat(newBuild);
                //if(!newBuild.equals(null) && !newBuild.equals(Updater_Main.build)) {
                System.out.println("direkt vor flaoting");
                if(Integer.parseInt(newBuild) > Updater_Main.build) {
                    if(enableAlerts) {
                        if (alerts.confirmDialogFX("Update", "Es ist ein Update verfügbar", "Möchtest du " + appName + " aktualisieren?")) {
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

    private void update() throws IOException {
        System.out.println("newbuild: " + newBuild);
        ftp_handler.downloadFile(initialPath + "/" + appName + "_" + newBuild + ".jar", localPath + appName + ".jar");
        System.out.println("heruntergeladen");
    }

    public void showChangeLog() throws IOException {
        if(enableAlerts) {
            ArrayList<String> log = fileLoader("ver/newbuild.txt");
            System.out.println("Changelog");

            String content = "";
            for (String entry : log) {
                content = content + entry + "\n";
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Changelog");
            alert.setHeaderText(null);
            alert.setContentText(content);

            alert.showAndWait();
        }
        renameBuildFile();
    }

    public void renameBuildFile() throws IOException {
        if(fileExist("ver/newbuild.txt")) {
//            File_Handler.deleteFile("ver/newbuild.txt");
            System.out.println("build.txt gelöscht");
        }
    }

    private boolean fileExist(String path) {
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

}
