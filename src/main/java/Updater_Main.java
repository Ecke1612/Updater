import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.DashmirrorMain;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Eike on 14.09.2018.
 */
public class Updater_Main extends Application {

    private final String version = "0.1";

    public static int build = 0;
    private int updateCircle = 5;
    private boolean firststart = true;
    private DashmirrorMain dmain;
    private Process process;
    private String executPath = "bin/dashmirror/Dashmirror.jar";
    private String os = "windows";
    private Updater updater = new Updater();

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.out.println("Version: " + version);
        Scanner scanner = new Scanner(System.in);
        System.out.println("Update Circle eingeben (in Minuten): ");
        updateCircle = scanner.nextInt();

        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);

        dmain = new DashmirrorMain();

        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("bin/exec.json"));
        JSONObject jsonObject = (JSONObject) obj;
        os = jsonObject.get("os").toString();
        executPath = jsonObject.get("path").toString();

        if(firststart) {
            System.out.println("starting " + dmain.getAppName());
            check(updater.getVersion());

            if(os.equals("windows")) process = startJarOnWindows(executPath);
            else if(os.equals("linux")) process = startJarOnLinux(executPath);

            firststart = false;
        }
        KeyFrame frame = new KeyFrame(Duration.minutes(updateCircle), event -> {
            try {
                System.out.println("check for updates!");
                if (check(updater.getVersion())) {
                    process.destroy();
                    if(os.equals("windows")) process = startJarOnWindows(executPath);
                    else if(os.equals("linux")) process = startJarOnLinux(executPath);
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

    public static void main (String args[]) {
        launch();
    }

    private Process startJarOnWindows(String jarFile) throws InterruptedException,IOException {
        System.out.println("start on Windows");
        List alist = new ArrayList<>();

        // add the list of commands to the list
        alist.add("java");
        alist.add("-jar");
        alist.add(jarFile);

        // initialize the processbuilder
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(alist);
        try {
            // start the process
            Process p = builder.start();
            return p;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Process startJarOnLinux(String jarFile) throws InterruptedException,IOException {
        System.out.println("start on Linux");
        List alist = new ArrayList<>();

        // add the list of commands to the list
        alist.add("java");
        alist.add("-Djavafx.platform=gtk2");
        alist.add("-jar");
        alist.add(jarFile);

        // initialize the processbuilder
        ProcessBuilder builder = new ProcessBuilder();
        builder.command(alist);
        try {
            // start the process
            Process p = builder.start();
            return p;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
