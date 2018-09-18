package ed.launcher;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import javax.swing.*;
import java.util.Optional;

/**
 * Created by Eike on 20.05.2017.
 */
public class Alert_Windows {


    public boolean confirmDialog(String title, String header, String text) {
        System.out.println("alert start");
        JOptionPane optionPane = new JOptionPane();
        int response = JOptionPane.showConfirmDialog(null,
                "Do you want to proceed?", "Select an Option...",JOptionPane.YES_NO_CANCEL_OPTION);
        System.out.println("vor dialog");
        JDialog dialog = optionPane.createDialog("Title");
        dialog.setAlwaysOnTop(true);
        dialog.setVisible(true);


        System.out.println("ersteltl");

        if (response == JOptionPane.NO_OPTION) {
            System.out.println("No button clicked");
            return false;
        } else if (response == JOptionPane.YES_OPTION) {
            System.out.println("Yes button clicked");
            return true;
        } else if (response == JOptionPane.CLOSED_OPTION) {
            System.out.println("JOptionPane closed");
            return false;
        }
        return false;
    }

    public boolean confirmDialogFX(String title, String header, String text) {
        System.out.println("alertTest1");
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(text);
        //alert.initOwner(Main_Application.primaryStage);

        Optional<ButtonType> result = alert.showAndWait();
        System.out.println("alertTest2");
        if (result.get() == ButtonType.OK){
            return true;
        } else {
            return false;
        }
    }

}
