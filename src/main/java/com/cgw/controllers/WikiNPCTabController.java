package com.cgw.controllers;

import com.cgw.features.Feature;
import com.cgw.features.NPC;
import com.cgw.relationships.Relationship;
import com.cgw.relationships.RelationshipStrings;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextFlow;
import org.apache.commons.lang3.tuple.Triple;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;
import java.util.ResourceBundle;

public class WikiNPCTabController implements Initializable {

    @FXML public Tab npcTab;
    @FXML public Label npcName;
    @FXML public Label npcGender;
    @FXML public Label npcAge;
    @FXML public Label npcAgeGroup;
    @FXML public Label npcRace;
    @FXML public VBox relationshipLabelVBox;

    private WorldWikiController currentWiki;

    private NPC npc;

    public void setUpNPC(NPC npc) {
        this.npc = npc;
        npcTab.setUserData(npc);
        npcTab.setText(npc.getName());

        Color wikiColor = getRandomHue();
        setWikiColor(wikiColor);

        npcName.setText(npc.getName());

        String gender = npc.getGenderString();
        npcGender.setText(gender.substring(0,1).toUpperCase() + gender.substring(1));

        npcAge.setText(npc.getAge() + " Years");

        String ageGroup = npc.getAgeGroupString();
        npcAgeGroup.setText(ageGroup.substring(0,1).toUpperCase() + ageGroup.substring(1));

        npcRace.setText(npc.getRace());

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

    private void setWikiColor(Color wikiColor) {
        npcName.setTextFill(wikiColor);
        npcGender.setTextFill(wikiColor);
        npcAge.setTextFill(wikiColor);
        npcAgeGroup.setTextFill(wikiColor);
        npcRace.setTextFill(wikiColor);
    }

    public Color getRandomHue() {
        Color initialColor = (Color) npcName.getTextFill();
        double[] hsb = new double[3];
        Random random = new Random(npc.hashCode());
        hsb[0] = random.nextInt(360);
        hsb[1] = initialColor.getSaturation();
        hsb[2] = initialColor.getBrightness();

        return Color.hsb(hsb[0], hsb[1], hsb[2]);
    }

    public void setCurrentWiki(WorldWikiController currentWiki) {
        this.currentWiki = currentWiki;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
