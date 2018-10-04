package ed.launcher;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Eike on 14.09.2018.
 */
public class EDUpdater {

    private final String version = "0.17";

    public static int build = 0;
    private int updateCircle = 1000;
    private boolean firststart = true;
    private String executPath = "bin/dashmirror/Dashmirror.jar";
    private String os = "windows";
    private Updater updater = new Updater();
    private Processer processer = new Processer();
    private Timer timer;


    public void start() throws Exception {
        System.out.println("Version: " + version);
        timer = new Timer("updateTimer");


        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader("bin/exec.json"));
        JSONObject jsonObject = (JSONObject) obj;
        os = jsonObject.get("os").toString();
        executPath = jsonObject.get("path").toString();
        updateCircle = Integer.parseInt(jsonObject.get("updateCircle").toString());
        String appname = jsonObject.get("appname").toString();

        if (firststart) {
            System.out.println("starting " + appname);
            check(updater.getVersion());

            processer.startJar(executPath, os);

            firststart = false;
        }
        long loop = 1000 * 60 * updateCircle;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
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
            }
        }, loop, loop);


    }

    public boolean check(int version) throws Exception {
        EDUpdater.build = version;

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


}
