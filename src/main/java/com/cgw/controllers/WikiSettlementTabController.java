package com.cgw.controllers;

import com.cgw.Bloom;
import com.cgw.EleutherAI;
import com.cgw.GPT2AI;
import com.cgw.features.Feature;
import com.cgw.features.Settlement;
import com.cgw.relationships.Relationship;
import com.cgw.relationships.RelationshipStrings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.net.URL;
import java.util.*;

/**
 * A Controller class for the NPC wiki page template to be loaded for each NPC.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.2
 * @since 0.1
 */
public class WikiSettlementTabController implements Initializable {

    @FXML public Tab settlementTab; // The tab for the Settlement page.
    // Labels for the Settlement's Attributes.
    @FXML public Label settlementName;
    @FXML public Label settlementType;
    @FXML public Label settlementSize;
    @FXML public Label settlementPopulation;
    // VBox to be able to add as many Relationships as required.
    // Within a ScrollPane which appears when Relationships overfill the VBox.
    @FXML public VBox relationshipLabelVBox;
    // Story Text Section, with a Button to generate Story and Text Area to place the result.
    @FXML public Button generateStoryButton;
    @FXML public TextArea storyText;
    @FXML public ComboBox<String> apiModelChoice;


    private WorldWikiController currentWiki;
    private Settlement settlement;

    /**
     * Sets up the Settlement Wiki content based on the Settlement given.
     * @param settlement The Settlement for this Wiki.
     */
    public void setUpSettlement(Settlement settlement) {
        this.settlement = settlement;
        settlementTab.setUserData(settlement);
        settlementTab.setText(settlement.getName());

        // Sets the color for this Settlement's Wiki Color theme, based on it's HashCode.
        Color wikiColor = getRandomHue();
        setWikiColor(wikiColor);

        settlementName.setText(settlement.getName());
        settlementType.setText(settlement.getType());
        settlementSize.setText(settlement.getSizeString());
        settlementPopulation.setText(settlement.getPopulation()+"");

        // Gets the Relationships of the Settlement and places each on the Wiki with a Label
        // of the relationship, and a HyperLink to take the user to their Wiki page.
        ArrayList<TextFlow> relationshipTexts = new ArrayList<>();
        ArrayList<Triple<String, Feature, Relationship>> relationships = settlement.getTripleRelationships();
        for(Triple<String, Feature, Relationship> relationship: relationships) {
            Label relationshipLabel = new Label();
            Hyperlink relationshipFeature = new Hyperlink(relationship.getMiddle().getName());

            relationshipLabel.setTextFill(wikiColor);
            relationshipLabel.setFont(Font.font("Candara", FontWeight.BOLD, FontPosture.REGULAR, 20));
            relationshipFeature.setFont(Font.font("Candara", FontWeight.NORMAL, FontPosture.REGULAR, 20));

            relationshipFeature.setOnAction(event -> {
                try {
                    currentWiki.openTabFromLink(relationship.getMiddle());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // Uses ENUMs to set the specific label for that relationship, different for different genders.
            switch (relationship.getLeft()) {
                case "resident" -> relationshipLabel.setText((RelationshipStrings.RESIDENT.toString()));
                case "ruler" -> relationshipLabel.setText((RelationshipStrings.RULER.toString()));
                case "trades" -> relationshipLabel.setText((RelationshipStrings.TRADES.toString()));
                case "rival" -> relationshipLabel.setText((RelationshipStrings.RIVAL.toString()));
            }

            relationshipTexts.add(new TextFlow(relationshipLabel, relationshipFeature));
        }

        // Compares the Relationships by their tags and sorts them into an order based on their Enum order.
        Comparator<TextFlow> relationshipViewOrder = (o1, o2) -> {
            Label l1 = (Label) o1.getChildren().get(0);
            Label l2 = (Label) o2.getChildren().get(0);
            String s1 = l1.getText();
            String s2 = l2.getText();
            return RelationshipStrings.getRelationshipFromString(s1).compareTo(RelationshipStrings.getRelationshipFromString(s2));
        };
        relationshipTexts.sort(relationshipViewOrder);
        relationshipLabelVBox.getChildren().addAll(relationshipTexts);
    }

    /**
     * Sets all the Labels and Button text's to the same color for the Settlement's Theme.
     * @param wikiColor Randomly chosen Hue for the Settlement based on it's HashCode.
     */
    private void setWikiColor(Color wikiColor) {
        settlementName.setTextFill(wikiColor);
        settlementType.setTextFill(wikiColor);
        settlementSize.setTextFill(wikiColor);
        settlementPopulation.setTextFill(wikiColor);
    }

    /**
     * Creates a new Color based on the preset Saturation and Brightness, but uses the
     * Settlement's HashCode to create a random Hue value, which will remain the same for
     * whenever this Settlement's page is loaded.
     * @return The Color created with a Random Hue value.
     */
    public Color getRandomHue() {
        Color initialColor = (Color) settlementName.getTextFill();
        double[]hsb = new double[3];
        Random random = new Random(settlement.hashCode());
        hsb[0] = random.nextInt(360);
        hsb[1] = initialColor.getSaturation();
        hsb[2] = initialColor.getBrightness();

        return Color.hsb(hsb[0], hsb[1], hsb[2]);
    }

    /**
     * Sets the Wiki Controller for the Wiki Scene in order to
     * call its methods for opening a new Tab.
     * @param currentWiki The World Wiki Controller.
     */
    public void setCurrentWiki(WorldWikiController currentWiki) {
        this.currentWiki = currentWiki;
    }

    /**
     * Calls the API for text Generation and sets the text in the Text Area.
     */
    public void generateStory() throws IOException {
        String prompt = "This is a story about " + settlementName.getText() + " a " + settlementSize.getText() +
                " sized " + settlementType.getText() + ", home to " + settlementPopulation.getText() + "people ";
        switch (apiModelChoice.getValue()) {
            case "GPT2" -> {
                String response = Objects.requireNonNull(GPT2AI.cURLTest(prompt)).substring(22);
                response = response.substring(0, response.length() - 3);
                response = response.replace(" }", "");

                storyText.setText(response);
            }
            case "Bloom" -> {
                storyText.setText(Bloom.cURLTest(prompt));
            }
            case "Eleuther" -> {
                String response = Objects.requireNonNull(EleutherAI.cURLTest(prompt)).substring(22);
                response = response.substring(0, response.length() - 3);
                response = response.replace(" }", "");
                response = response.replace("'{", " ");
                response = response.replace("}'{", " ");

                storyText.setText(response);
            }
        }
    }

    /**
     * Initializes the Tab Controller, setting up the AI Choices
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        storyText.setWrapText(true);

        ObservableList<String> apiChoices = FXCollections.observableArrayList("GPT2", "Bloom", "Eleuther");
        apiModelChoice.getItems().addAll(apiChoices);
        apiModelChoice.setValue(apiModelChoice.getItems().get(0));
    }


}
