import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.DashmirrorMain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Eike on 14.09.2018.
 */
public class Updater_Main extends Application {

    private final String version = "0.13";

    public static int build = 0;
    private int updateCircle = 5;
    private boolean firststart = true;
    //private Process process;
    private String executPath = "bin/dashmirror/Dashmirror.jar";
    private String os = "windows";
    private Updater updater = new Updater();
    private Processer processer = new Processer();
    //private DashmirrorMain dMain;

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Version: " + version);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Update Circle eingeben (in Minuten): ");
        updateCircle = scanner.nextInt();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("bin/exec.json"));
        JSONObject jsonObject = (JSONObject) obj;
        os = jsonObject.get("os").toString();
        executPath = jsonObject.get("path").toString();
        String appname = jsonObject.get("appname").toString();

        if (firststart) {
            System.out.println("starting " + appname);
            check(updater.getVersion());

            processer.startJar(executPath, os);

            firststart = false;
        }
        KeyFrame frame = new KeyFrame(Duration.minutes(updateCircle), event -> {
            try {
                System.out.println("check for updates!");
                if (check(updater.getVersion())) {
                    processer.destroyProcess();
                    processer.startJar(executPath, os);
                } else {
                    System.out.println("no Updates were found, keep going");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        timeline.getKeyFrames().add(frame);
        timeline.play();
    }

    public boolean check(int version) throws Exception {
        Updater_Main.build = version;

        System.out.println("old build main: " + build);

        boolean update = updater.checkForUpdate();
        if (update) {
            updater.showChangeLog();
            return true;
        } else {
            System.out.println("altes Programm gestartet");
            return false;
        }
    }

    public static void main(String args[]) {
        launch();
    }
}
