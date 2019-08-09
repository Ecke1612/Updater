package ed.launcher.controller;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class ChangeLogController {


    public TextFlow textFlow;

    private String logText;

    public ChangeLogController(String logText) {
        this.logText = logText;
    }

    public void initialize() {
        System.out.println("lohtext: " + logText);
        textFlow.getChildren().add(new Text(logText));
    }

}
