import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Duration;
import main.DashmirrorMain;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Eike on 17.06.2017.
 */
public class Updater_Main extends Application {

    public static int build = 0;
    private final int updateCircle = 10;
    private boolean firststart = true;
    private DashmirrorMain dmain;
    private Process process;

    @Override
    public void start(Stage primaryStage) throws Exception {
        Timeline timeline = new Timeline();
        timeline.setCycleCount(Timeline.INDEFINITE);
        dmain = new DashmirrorMain();
        /*if(check(dmain.getBuild())) {
        } else {
        }*/
        if(firststart) {
            //Runtime rt1 = Runtime.getRuntime();
            //Process pr2 = rt1.exec("java -jar bin/dashmirror/Dashmirror.jar");
            System.out.println("try to start App");
            process = startJar("bin/dashmirror/Dashmirror.jar");

            firststart = false;
        }
        KeyFrame frame = new KeyFrame(Duration.seconds(updateCircle), event -> {
            try {
                System.out.println("check for updates!");
                if (check(dmain.getBuild())) {
                    process.destroy();
                    //Runtime rt2 = Runtime.getRuntime();
                    //Process pr2 = rt2.exec("java -jar bin/dashmirror/Dashmirror.jar");
                    process = startJar("bin/dashmirror/Dashmirror.jar");
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

            Updater updater = new Updater();
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

    private Process startJar(String jarFile) throws InterruptedException,IOException {
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
}
