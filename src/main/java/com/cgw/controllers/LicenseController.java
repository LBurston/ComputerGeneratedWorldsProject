package com.cgw.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * A Controller class for the License Window, displaying information on the GNU GPL v3.0 License.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class LicenseController implements Initializable {

    @FXML private VBox textArea;

    /**
     * Initializes the License window, loading in the License from a txt file.
     * Each line is placed in a label and added to a VBox in the Text Area.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            InputStream is = getClass().getResourceAsStream("/COPYING.txt");
            assert is != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String currentLine;
            while((currentLine = reader.readLine()) != null) {
                Label label = new Label(currentLine);
                label.setAlignment(Pos.CENTER);
                label.setTextAlignment(TextAlignment.CENTER);
                label.setMaxWidth(Double.MAX_VALUE);
                label.setFont(Font.font("Calibri", FontWeight.NORMAL, FontPosture.REGULAR, 15));
                textArea.getChildren().add(label);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
