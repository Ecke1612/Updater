package ed.launcher.controller;

import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;

public class ChangeLogController {


    public TextFlow textFlow;

    private String logText;
    private Stage stage;

    public ChangeLogController(String logText, Stage stage) {
        this.logText = logText;
        this.stage = stage;
    }

    public void initialize() {
        textFlow.getChildren().add(new Text(logText));
    }

    public void okay() {
        stage.close();
    }

}
