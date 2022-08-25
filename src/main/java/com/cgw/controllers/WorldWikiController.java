package com.cgw.controllers;

import com.cgw.features.*;
import com.cgw.generators.WorldGenerator;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

/**
 * A Controller Class for the World Wiki as a whole, with a TreeView for Navigation and a Tab Pane for pages.
 * @author Luke Burston
 * @author lb800@kent.ac.uk
 * @version 0.1
 * @since 0.1
 */
public class WorldWikiController implements Initializable {

    // Tree View listing all World Features and their separate categories.
    @FXML public TreeView<Feature> treeView;
    // Tab pane for placing new Tabs and searching for opened tabs.
    @FXML public TabPane tabPane; // Size H: 534, W: 714

    private World currentWorld;

    /**
     * Sets the Current World in which the Wiki is loaded for
     * @param world The World being displayed by the Wiki.
     */
    public void setCurrentWorld(World world) {
        currentWorld = world;
    }

    /**
     * Sets up the Wiki based on its current World
     * @throws IOException Thrown if Home Page fxml cannot be loaded.
     */
    public void populateWiki() throws IOException {
        // Sets the Directories of the TreeView. These must all be Features in order to
        // be added, so the World extends Feature and a Folder class that also extends Feature
        // is used for the Feature types, which is its only function.
        TreeItem<Feature> worldRoot = new TreeItem<>(currentWorld);
        TreeItem<Feature> npcsBranch = new TreeItem<>(new Folder("People"));
        TreeItem<Feature> settlementsBranch = new TreeItem<>(new Folder("Settlements"));

        // Creates a leaf node for each NPC and Settlement, adding them to their respective branch.
        for(NPC npc: currentWorld.getAllNPCs()) {
            npcsBranch.getChildren().add(new TreeItem<>(npc));
        }
        for(Settlement settlement: currentWorld.getAllSettlements()) {
            settlementsBranch.getChildren().add(new TreeItem<>(settlement));
        }
        // Sorts the two branches to be alphabetical. For NPCs, this is first by Last Name, then First.
        npcsBranch.getChildren().sort((o1, o2) -> {
            NPC npc1 = (NPC) o1.getValue();
            NPC npc2 = (NPC) o2.getValue();
            int result = npc1.getLastName().compareToIgnoreCase(npc2.getLastName());
            if (result != 0) {
                return result;
            }
            return npc1.getName().compareToIgnoreCase(npc2.getName());
        });
        settlementsBranch.getChildren().sort((o1, o2) -> o1.toString().compareToIgnoreCase(o2.toString()));

        worldRoot.getChildren().addAll(npcsBranch, settlementsBranch);
        worldRoot.setExpanded(true);    // Sets the World Branch to be expanded on open.
        treeView.setRoot(worldRoot);
        treeView.setEditable(false);    // Stops the User from changing the TreeView.

        // Allows Tabs to be closed by the user and re-ordered on their preference.
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);

        // Creates a new home Tab to open with.
        tabPane.getTabs().add(createHomeTab());
    }

    /**
     * Home Page Button action method. If opened, redirects to HomePage tab, or creates a new one
     * @throws IOException If Home Page fxml file is not found.
     */
    public void openHomePage() throws IOException {
        // Searches TabPane for already opened Home Page.
        Tab currentHomePage = tabPane.getTabs().stream().filter(tab -> tab.getUserData().equals("home")).findFirst().orElse(null);
        if(currentHomePage == null) {
            // If none found, creates a new Home Page Tab, sets it as the first tabs and reloads all other tabs.
            Tab newHomeTab = createHomeTab();
            List<Tab> tabs = new ArrayList<>(tabPane.getTabs());
            tabPane.getTabs().clear();
            tabPane.getTabs().add(newHomeTab);
            tabPane.getTabs().addAll(tabs);
            tabPane.getSelectionModel().select(newHomeTab);
        } else {
            // Redirects Tab Pane to open Home Page Tab
            tabPane.getSelectionModel().select(currentHomePage);
        }
    }

    /**
     * Method for opening a new Tab from the Tree View when double-clicked.
     * @param event Mouse Click
     * @throws IOException If Feature's FXML file is not found.
     */
    public void openTab(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2 && !event.isConsumed()) {
            event.consume();
            // Slight issue with this. If Tree item is selected and the collapse/expand part of a Tree
            // branch it is not contained in is double-clicked, this opens the item that was selected.
            TreeItem<Feature> item = treeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                // Null check and then gets the tab from its userData
                Tab alreadyOpened = tabPane.getTabs().stream().filter(tab -> tab.getUserData() == item.getValue()).findFirst().orElse(null);
                if (alreadyOpened == null) {
                    // If none found, creates a new Tab based on the Feature.
                    Feature selectedFeature = item.getValue();
                    switch (selectedFeature.getSubClass().getSimpleName()) {
                        case "NPC" -> {
                            Tab newNPCTab = createNPCTab((NPC) selectedFeature);
                            tabPane.getTabs().add(newNPCTab);
                            tabPane.getSelectionModel().select(newNPCTab);
                        }
                        case "Settlement" -> {
                            Tab newSettlementTab = createSettlementTab((Settlement) selectedFeature);
                            tabPane.getTabs().add(newSettlementTab);
                            tabPane.getSelectionModel().select(newSettlementTab);
                        }
                    }
                } else {
                    // If tab is already opened, redirects to it.
                    tabPane.getSelectionModel().select(alreadyOpened);
                }
            }
        }
    }

    /**
     * Method for opening a Tab from a HyperLink on any other page.
     * @param feature The Feature to be opened
     * @throws IOException If Feature's fxml file could not be found.
     */
    public void openTabFromLink(Feature feature) throws IOException {
        // Checks if the Tab is already opened, like in previous methods, opening a new tab or redirecting.
        Tab alreadyOpened = tabPane.getTabs().stream().filter(tab -> tab.getUserData() == feature).findFirst().orElse(null);
        if (alreadyOpened == null) {
            switch (feature.getSubClass().getSimpleName()) {
                case "NPC" -> {
                    Tab newNPCTab = createNPCTab((NPC) feature);
                    tabPane.getTabs().add(newNPCTab);
                    tabPane.getSelectionModel().select(newNPCTab);
                }
                case "Settlement" -> {
                    Tab newSettlementTab = createSettlementTab((Settlement) feature);
                    tabPane.getTabs().add(newSettlementTab);
                    tabPane.getSelectionModel().select(newSettlementTab);
                }
            }
        } else {
            tabPane.getSelectionModel().select(alreadyOpened);
        }
    }

    /**
     * Loads the Home Page from an FXML and returns a new Tab with its details set up.
     * @return Home Page Tab.
     * @throws IOException If FXML file is not found.
     */
    private Tab createHomeTab() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/wikiHome.fxml"));
        Tab newHomeTab = loader.load();
        WikiHomeTabController controller = loader.getController();
        controller.setCurrentWiki(this);

        controller.setUpWorldHome(currentWorld);

        return newHomeTab;
    }

    /**
     * Loads the NPC Page from an FXML and returns a new Tab with its details set up.
     * @return NPC Page Tab.
     * @throws IOException If FXML file is not found.
     */
    private Tab createNPCTab(NPC npc) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/wikiNPCTemplate.fxml"));
        Tab newNPCTab = loader.load();
        WikiNPCTabController controller = loader.getController();
        controller.setCurrentWiki(this);

        controller.setUpNPC(npc);

        return newNPCTab;
    }

    /**
     * Loads the Settlement Page from an FXML and returns a new Tab with its details set up.
     * @return Settlement Page Tab.
     * @throws IOException If FXML file is not found.
     */
    private Tab createSettlementTab(Settlement settlement) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/wikiSettlementTemplate.fxml"));
        Tab newSettlementTab = loader.load();
        WikiSettlementTabController controller = loader.getController();
        controller.setCurrentWiki(this);

        controller.setUpSettlement(settlement);

        return newSettlementTab;
    }

    /**
     * Initializes the World Wiki, setting its world from the World Generator and Populating the Wiki Scene.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCurrentWorld(WorldGenerator.getWorldGenerator().getWorld());
        try {
            populateWiki();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
