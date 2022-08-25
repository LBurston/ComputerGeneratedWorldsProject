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
import java.util.Random;
import java.util.ResourceBundle;

/**
 * A Controller class for the Home Page of the World Wiki, managing the User interactions.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class WikiHomeTabController implements Initializable {

    @FXML public Tab worldHomeTab;  // The tab for the Home Page
    // Labels for the World name that appears on the Home Page.
    @FXML private Label worldNameLabel;
    @FXML private Label worldNameLabel2;
    @FXML private Label worldNameLabel3;
    // Buttons to open a new Tab of a random Feature of type.
    @FXML private Button randomFeatureButton;
    @FXML private Button randomNPCButton;
    @FXML private Button randomSettlementButton;
    // HyperLinks to open randomly chosen NPCs and Settlements.
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

    /**
     * Sets up the Home Page content based on the World given.
     * @param world The World for this wiki.
     */
    public void setUpWorldHome(World world) {
        currentWorld = world;
        String worldName = currentWorld.getName();
        worldHomeTab.setText(worldName + " Homepage");
        worldNameLabel.setText(worldName);
        worldNameLabel2.setText(worldName);
        worldNameLabel3.setText(worldName);

        // Gets a new Color with the same Saturation and Brightness but different Hue
        Color wikiColor = getRandomHue();
        setWikiColor(wikiColor);

        setUpHyperLinks();
    }

    /**
     * Gets 5 random NPCs and Settlements from the current World
     * and places each into one of the 5 HyperLinks for each within
     * the Home Page to display their name and open their tabs.
     */
    private void setUpHyperLinks() {
        // Gets 5 random NPCs, without duplicates.
        ArrayList<NPC> npcs = currentWorld.getAllNPCs();
        ArrayList<NPC> linkedNPCs = new ArrayList<>();
        while(!(linkedNPCs.size() >= 5)) {
            NPC npc = npcs.get(Randomiser.getRandom().nextInt(npcs.size()));
            if(!linkedNPCs.contains(npc)) {
                linkedNPCs.add(npc);
            }
        }

        // Sets the Text of the HyperLink to the NPC name
        npcHL1.setText(linkedNPCs.get(0).getName());
        npcHL2.setText(linkedNPCs.get(1).getName());
        npcHL3.setText(linkedNPCs.get(2).getName());
        npcHL4.setText(linkedNPCs.get(3).getName());
        npcHL5.setText(linkedNPCs.get(4).getName());
        // Sets all the links to the given NPC's tab.
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

        // Gets 5 random Settlements, without duplicates.
        ArrayList<Settlement> settlements = currentWorld.getAllSettlements();
        ArrayList<Settlement> linkedSettlements = new ArrayList<>();
        while(!(linkedSettlements.size() >= 5)) {
            Settlement settlement = settlements.get(Randomiser.getRandom().nextInt(settlements.size()));
            if(!linkedSettlements.contains(settlement)) {
                linkedSettlements.add(settlement);
            }
        }

        // Sets the Text of the HyperLink to the Settlement's name
        settlementHL1.setText(linkedSettlements.get(0).getName());
        settlementHL2.setText(linkedSettlements.get(1).getName());
        settlementHL3.setText(linkedSettlements.get(2).getName());
        settlementHL4.setText(linkedSettlements.get(3).getName());
        settlementHL5.setText(linkedSettlements.get(4).getName());
        //Sets all the links to the given Settlement's tab.
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

    /**
     * Opens the tab of a random Feature of any type within the World.
     * @throws IOException From opening the FXML file for the Feature's Wiki template.
     */
    @FXML
    private void openRandomFeature() throws IOException {
        ArrayList<Feature> features = currentWorld.getFeatures();
        currentWiki.openTabFromLink(features.get(Randomiser.getRandom().nextInt(features.size())));
    }

    /**
     * Opens the tab of a random NPC of any type within the World.
     * @throws IOException From opening the FXML file for the NPC's Wiki template.
     */
    @FXML
    private void openRandomNPC() throws IOException {
        ArrayList<NPC> npcs = currentWorld.getAllNPCs();
        currentWiki.openTabFromLink(npcs.get(Randomiser.getRandom().nextInt(npcs.size())));
    }

    /**
     * Opens the tab of a random Settlement of any type within the World.
     * @throws IOException From opening the FXML file for the Settlement's Wiki template.
     */
    @FXML
    private void openRandomSettlement() throws IOException {
        ArrayList<Settlement> settlements = currentWorld.getAllSettlements();
        currentWiki.openTabFromLink(settlements.get(Randomiser.getRandom().nextInt(settlements.size())));
    }

    /**
     * Sets all the Labels and Button text's to the same color for the World's Theme.
     * @param wikiColor Randomly chosen Hue for the World based on it's HashCode.
     */
    private void setWikiColor(Color wikiColor) {
        worldNameLabel.setTextFill(wikiColor);
        worldNameLabel2.setTextFill(wikiColor);
        worldNameLabel3.setTextFill(wikiColor);
        randomFeatureButton.setTextFill(wikiColor);
        randomNPCButton.setTextFill(wikiColor);
        randomSettlementButton.setTextFill(wikiColor);
    }

    /**
     * Creates a new Color based on the preset Saturation and Brightness, but uses the
     * World's HashCode to create a random Hue value, which will remain the same for
     * whenever this World's HomePage is loaded.
     * @return The Color created with a Random Hue value.
     */
    public Color getRandomHue() {
        Color initialColor = (Color) worldNameLabel.getTextFill();
        double[] hsb = new double[3];
        Random random = new Random(currentWorld.hashCode());
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
     * Initializes the Home Tab User Data so that the World Wiki Controller can
     * search for the Home Tab and to redirect to it if it is already open.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        worldHomeTab.setUserData("home");
    }
}
