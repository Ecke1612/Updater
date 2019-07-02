package ed.launcher;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class ConfigObject {

    private String os;
    private int updateCircle;
    private ArrayList<AppObject> appObjects = new ArrayList<>();


    public void loadConfig() {
        JSONParser parser = new JSONParser();
        Object obj = null;
        try {
            obj = parser.parse(new FileReader("bin/exec.json"));
            JSONObject jsonObject = (JSONObject) obj;
            os = jsonObject.get("os").toString();
            updateCircle = Integer.parseInt(jsonObject.get("updateCircle").toString());
            JSONArray appArray = (JSONArray) jsonObject.get("apps");
            for(int i = 0; i < appArray.size(); i++) {
                JSONObject jsonAppObj = (JSONObject) appArray.get(i);
                appObjects.add(new AppObject(
                        i,
                        jsonAppObj.get("name").toString(),
                        jsonAppObj.get("exec").toString(),
                        jsonAppObj.get("remoteTxtPath").toString(),
                        jsonAppObj.get("initalPath").toString(),
                        jsonAppObj.get("localPath").toString()
                ));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public String getOs() {
        return os;
    }

    public int getUpdateCircle() {
        return updateCircle;
    }

    public ArrayList<AppObject> getAppObjects() {
        return appObjects;
    }
}
