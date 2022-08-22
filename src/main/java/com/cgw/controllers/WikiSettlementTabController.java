package com.cgw.controllers;

import com.cgw.features.Feature;
import com.cgw.features.Settlement;
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
import java.util.*;

public class WikiSettlementTabController implements Initializable {

    @FXML public Tab settlementTab;
    @FXML public Label settlementName;
    @FXML public Label settlementType;
    @FXML public Label settlementSize;
    @FXML public Label settlementPopulation;
    @FXML public VBox relationshipLabelVBox;

    private WorldWikiController currentWiki;

    private Settlement settlement;

    public void setUpSettlement(Settlement settlement) {
        this.settlement = settlement;
        settlementTab.setUserData(settlement);
        settlementTab.setText(settlement.getName());

        Color wikiColor = getRandomHue();
        setWikiColor(wikiColor);

        settlementName.setText(settlement.getName());
        settlementType.setText(settlement.getType());
        settlementSize.setText(settlement.getSizeString());
        settlementPopulation.setText(settlement.getPopulation()+"");

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

            switch (relationship.getLeft()) {
                case "resident" -> relationshipLabel.setText((RelationshipStrings.RESIDENT.toString()));
                case "ruler" -> relationshipLabel.setText((RelationshipStrings.RULER.toString()));
                case "trades" -> relationshipLabel.setText((RelationshipStrings.TRADES.toString()));
                case "rival" -> relationshipLabel.setText((RelationshipStrings.RIVAL.toString()));
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
        settlementName.setTextFill(wikiColor);
        settlementType.setTextFill(wikiColor);
        settlementSize.setTextFill(wikiColor);
        settlementPopulation.setTextFill(wikiColor);
    }

    public Color getRandomHue() {
        Color initialColor = (Color) settlementName.getTextFill();
        double[]hsb = new double[3];
        Random random = new Random(settlement.hashCode());
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
