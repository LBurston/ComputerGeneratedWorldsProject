package com.cgw.controllers;

import com.cgw.Bloom;
import com.cgw.EleutherAI;
import com.cgw.GPT2AI;
import com.cgw.features.Feature;
import com.cgw.features.NPC;
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
 * @version 0.1
 * @since 0.1
 */
public class WikiNPCTabController implements Initializable {


    @FXML public Tab npcTab;    // The tab for the NPC page.
    // Labels for the NPC's Attributes.
    @FXML public Label npcName;
    @FXML public Label npcGender;
    @FXML public Label npcAge;
    @FXML public Label npcAgeGroup;
    @FXML public Label npcRace;
    // VBox to be able to add as many Relationships as required.
    // Within a ScrollPane which appears when Relationships overfill the VBox.
    @FXML public VBox relationshipLabelVBox;
    // Story Text Section, with a Button to generate Story and Text Area to place the result.
    @FXML public Button generateStoryButton;
    @FXML public TextArea storyText;
    @FXML public ComboBox<String> apiModelChoice;

    private WorldWikiController currentWiki;
    private NPC npc;

    /**
     * Sets up the NPC Wiki content based on the NPC given.
     * @param npc The NPC for this Wiki.
     */
    @FXML
    public void setUpNPC(NPC npc) {
        this.npc = npc;
        npcTab.setUserData(npc);
        npcTab.setText(npc.getName());

        // Sets the color for this NPC's Wiki Color theme, based on it's HashCode.
        Color wikiColor = getRandomHue();
        setWikiColor(wikiColor);

        npcName.setText(npc.getName());

        String gender = npc.getGenderString();
        npcGender.setText(gender.substring(0,1).toUpperCase() + gender.substring(1));

        npcAge.setText(npc.getAge() + " Years");

        String ageGroup = npc.getAgeGroupString();
        npcAgeGroup.setText(ageGroup.substring(0,1).toUpperCase() + ageGroup.substring(1));

        npcRace.setText(npc.getRace());

        // Gets the Relationships of the NPC and places each on the Wiki with a Label
        // of the relationship, and a HyperLink to take the user to their Wiki page.
        ArrayList<TextFlow> relationshipTexts = new ArrayList<>();
        ArrayList<Triple<String, Feature, Relationship>> relationships = npc.getTripleRelationships();
        for(Triple<String, Feature, Relationship> relationship : relationships) {
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
            switch(relationship.getLeft()) {
                case "residence" -> relationshipLabel.setText(RelationshipStrings.RESIDENCE.toString());
                case "rules" -> relationshipLabel.setText(RelationshipStrings.RULES.toString());
                case "partner" -> {
                    NPC partner = (NPC) relationship.getMiddle();
                    switch (partner.getGender()) {
                        case 'm' -> relationshipLabel.setText(RelationshipStrings.HUSBAND.toString());
                        case 'f' -> relationshipLabel.setText(RelationshipStrings.WIFE.toString());
                        case 'n' -> relationshipLabel.setText(RelationshipStrings.PARTNER.toString());
                    }
                }
                case "child" -> {
                    NPC child = (NPC) relationship.getMiddle();
                    switch (child.getGender()) {
                        case 'm' -> relationshipLabel.setText(RelationshipStrings.SON.toString());
                        case 'f' -> relationshipLabel.setText(RelationshipStrings.DAUGHTER.toString());
                        case 'n' -> relationshipLabel.setText(RelationshipStrings.CHILD.toString());
                    }
                }
                case "parent" -> relationshipLabel.setText(RelationshipStrings.PARENT.toString());
                case "mother" -> relationshipLabel.setText(RelationshipStrings.MOTHER.toString());
                case "father" -> relationshipLabel.setText(RelationshipStrings.FATHER.toString());
                case "sibling" -> {
                    NPC sibling = (NPC) relationship.getMiddle();
                    switch (sibling.getGender()) {
                        case 'm' -> relationshipLabel.setText(RelationshipStrings.BROTHER.toString());
                        case 'f' -> relationshipLabel.setText(RelationshipStrings.SISTER.toString());
                        case 'n' -> relationshipLabel.setText(RelationshipStrings.SIBLING.toString());
                    }
                }
                case "killer" -> relationshipLabel.setText(RelationshipStrings.KILLER.toString());
                case "killed" -> relationshipLabel.setText(RelationshipStrings.KILLED.toString());

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
     * Sets all the Labels and Button text's to the same color for the NPC's Theme.
     * @param wikiColor Randomly chosen Hue for the NPC based on it's HashCode.
     */
    private void setWikiColor(Color wikiColor) {
        npcName.setTextFill(wikiColor);
        npcGender.setTextFill(wikiColor);
        npcAge.setTextFill(wikiColor);
        npcAgeGroup.setTextFill(wikiColor);
        npcRace.setTextFill(wikiColor);
    }

    /**
     * Creates a new Color based on the preset Saturation and Brightness, but uses the
     * NPC's HashCode to create a random Hue value, which will remain the same for
     * whenever this NPC's page is loaded.
     * @return The Color created with a Random Hue value.
     */
    public Color getRandomHue() {
        Color initialColor = (Color) npcName.getTextFill();
        double[] hsb = new double[3];
        Random random = new Random(npc.hashCode());
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
        String prompt = "This is a story about " + npcName.getText() + ", a " + npcAge.getText().split(" ")[0] +
                " year-old " + npcAgeGroup.getText() + " " + npcRace.getText() + ", who ";
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        storyText.setWrapText(true);

        ObservableList<String> apiChoices = FXCollections.observableArrayList("GPT2", "Bloom", "Eleuther");
        apiModelChoice.getItems().addAll(apiChoices);
        apiModelChoice.setValue(apiModelChoice.getItems().get(0));
    }
}
