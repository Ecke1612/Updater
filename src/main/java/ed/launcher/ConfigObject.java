package ed.launcher;

import com.ed.filehandler.JsonHandler;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import java.util.ArrayList;

public class ConfigObject {

    private String os;
    private int updateCircle;
    private ArrayList<AppObject> appObjects = new ArrayList<>();
    private JsonHandler jsonHandler = new JsonHandler();

    public void loadConfig() {
        JSONObject jsonObject = jsonHandler.readJsonData("bin/exec.json");

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
