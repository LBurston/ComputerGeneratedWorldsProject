package com.cgw.controllers;

import com.cgw.features.Feature;
import com.cgw.features.NPC;
import com.cgw.features.Settlement;
import com.cgw.features.World;
import com.cgw.generators.Randomiser;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.ResourceBundle;

public class WikiHomeTabController implements Initializable {

    @FXML public Tab worldHomeTab;
    @FXML private Label worldNameLabel;
    @FXML private Label worldNameLabel2;
    @FXML private Label worldNameLabel3;
    @FXML private Button randomFeatureButton;
    @FXML private Button randomNPCButton;
    @FXML private Button randomSettlementButton;
    @FXML private Hyperlink npcHL1;
    @FXML private Hyperlink npcHL2;
    @FXML private Hyperlink npcHL3;
    @FXML private Hyperlink npcHL4;
    @FXML private Hyperlink npcHL5;
    @FXML private Hyperlink settlementHL1;
    @FXML private Hyperlink settlementHL2;
    @FXML private Hyperlink settlementHL3;
    @FXML private Hyperlink settlementHL4;
    @FXML private Hyperlink settlementHL5;

    private WorldWikiController currentWiki;
    private World currentWorld;

    public void setUpWorldHome(World world) {
        currentWorld = world;
        String worldName = currentWorld.getName();
        worldHomeTab.setText(worldName + " Homepage");
        worldNameLabel.setText(worldName);
        worldNameLabel2.setText(worldName);
        worldNameLabel3.setText(worldName);

        Color wikiColor = getRandomHue();
        setWikiColor(wikiColor);

        setUpHyperLinks();

    }

    private void setUpHyperLinks() {
        ArrayList<NPC> npcs = currentWorld.getAllNPCs();
        ArrayList<NPC> linkedNPCs = new ArrayList<>();
        while(!(linkedNPCs.size() >= 5)) {
            NPC npc = npcs.get(Randomiser.getRandom().nextInt(npcs.size()));
            if(!linkedNPCs.contains(npc)) {
                linkedNPCs.add(npc);
            }
        }
        npcHL1.setText(linkedNPCs.get(0).getName());
        npcHL2.setText(linkedNPCs.get(1).getName());
        npcHL3.setText(linkedNPCs.get(2).getName());
        npcHL4.setText(linkedNPCs.get(3).getName());
        npcHL5.setText(linkedNPCs.get(4).getName());
        npcHL1.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedNPCs.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        npcHL2.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedNPCs.get(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        npcHL3.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedNPCs.get(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        npcHL4.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedNPCs.get(3));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        npcHL5.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedNPCs.get(4));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        ArrayList<Settlement> settlements = currentWorld.getAllSettlements();
        ArrayList<Settlement> linkedSettlements = new ArrayList<>();
        while(!(linkedSettlements.size() >= 5)) {
            Settlement settlement = settlements.get(Randomiser.getRandom().nextInt(settlements.size()));
            if(!linkedSettlements.contains(settlement)) {
                linkedSettlements.add(settlement);
            }
        }
        settlementHL1.setText(linkedSettlements.get(0).getName());
        settlementHL2.setText(linkedSettlements.get(1).getName());
        settlementHL3.setText(linkedSettlements.get(2).getName());
        settlementHL4.setText(linkedSettlements.get(3).getName());
        settlementHL5.setText(linkedSettlements.get(4).getName());
        settlementHL1.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedSettlements.get(0));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settlementHL2.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedSettlements.get(1));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settlementHL3.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedSettlements.get(2));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settlementHL4.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedSettlements.get(3));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        settlementHL5.setOnAction(event -> {
            try {
                currentWiki.openTabFromLink(linkedSettlements.get(4));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @FXML
    private void openRandomFeature() throws IOException {
        ArrayList<Feature> features = currentWorld.getFeatures();
        currentWiki.openTabFromLink(features.get(Randomiser.getRandom().nextInt(features.size())));
    }

    @FXML
    private void openRandomNPC() throws IOException {
        ArrayList<NPC> npcs = currentWorld.getAllNPCs();
        currentWiki.openTabFromLink(npcs.get(Randomiser.getRandom().nextInt(npcs.size())));
    }

    @FXML
    private void openRandomSettlement() throws IOException {
        ArrayList<Settlement> settlements = currentWorld.getAllSettlements();
        currentWiki.openTabFromLink(settlements.get(Randomiser.getRandom().nextInt(settlements.size())));
    }

    private void setWikiColor(Color wikiColor) {
        worldNameLabel.setTextFill(wikiColor);
        worldNameLabel2.setTextFill(wikiColor);
        worldNameLabel3.setTextFill(wikiColor);
        randomFeatureButton.setTextFill(wikiColor);
        randomNPCButton.setTextFill(wikiColor);
        randomSettlementButton.setTextFill(wikiColor);
    }

    public Color getRandomHue() {
        Color initialColor = (Color) worldNameLabel.getTextFill();
        double[] hsb = new double[3];
        Random random = new Random(currentWorld.hashCode());
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
        worldHomeTab.setUserData("home");
    }
}
