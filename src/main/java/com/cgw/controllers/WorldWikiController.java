package com.cgw.controllers;

import com.cgw.features.*;
import com.cgw.generators.WorldGenerator;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class WorldWikiController implements Initializable {

    @FXML public AnchorPane wikiScreen;
    @FXML public TreeView<Feature> treeView;
    @FXML public TabPane tabPane; // Size H: 534, W: 714


    private World currentWorld;

    public void setCurrentWorld(World world) {
        currentWorld = world;
    }

    public void populateWiki() throws IOException {
        TreeItem<Feature> worldRoot = new TreeItem<>(currentWorld);
        TreeItem<Feature> npcsBranch = new TreeItem<>(new Folder("NPCs"));
        TreeItem<Feature> settlementsBranch = new TreeItem<>(new Folder("Settlements"));

        for(NPC npc: currentWorld.getAllNPCs()) {
            npcsBranch.getChildren().add(new TreeItem<>(npc));
        }
        for(Settlement settlement: currentWorld.getAllSettlements()) {
            settlementsBranch.getChildren().add(new TreeItem<>(settlement));
        }
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

        worldRoot.setExpanded(true);
        treeView.setRoot(worldRoot);
        treeView.setEditable(false);

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        tabPane.setTabDragPolicy(TabPane.TabDragPolicy.REORDER);

        tabPane.getTabs().add(createHomeTab());
    }

//    private boolean doubleNames() {
//        ArrayList<String> names = currentWorld.getAllNPCs().stream()
//                .map(Feature::getName)
//                .collect(Collectors.toCollection(ArrayList::new));
//        HashSet<String> namesHashSet = new HashSet<>();
//        for(String name : names){
//            int startingSize = namesHashSet.size();
//            namesHashSet.add(name);
//            if (namesHashSet.size() == startingSize) {
//                System.out.println(name);
//            }
//        }
//        return names.size() == namesHashSet.size();
//    }

    public void openHomePage() throws IOException {
        Tab currentHomePage = tabPane.getTabs().stream().filter(tab -> tab.getUserData().equals("home")).findFirst().orElse(null);
        if(currentHomePage == null) {
            Tab newHomeTab = createHomeTab();
            List<Tab> tabs = new ArrayList<>(tabPane.getTabs());
            tabPane.getTabs().clear();
            tabPane.getTabs().add(newHomeTab);
            tabPane.getTabs().addAll(tabs);
            tabPane.getSelectionModel().select(newHomeTab);
        } else {
            tabPane.getSelectionModel().select(currentHomePage);
        }
    }

    public void openTab(MouseEvent event) throws IOException {
        if (event.getClickCount() == 2 && !event.isConsumed()) {
            event.consume();
            TreeItem<Feature> item = treeView.getSelectionModel().getSelectedItem();
            if (item != null) {
                Tab alreadyOpened = tabPane.getTabs().stream().filter(tab -> tab.getUserData() == item.getValue()).findFirst().orElse(null);
                if (alreadyOpened == null) {
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
                    tabPane.getSelectionModel().select(alreadyOpened);
                }
            }
        }
    }

    public void openTabFromLink(Feature feature) throws IOException {
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

    private Tab createHomeTab() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/wikiHome.fxml"));
        Tab newHomeTab = loader.load();
        WikiHomeTabController controller = loader.getController();
        controller.setCurrentWiki(this);

        controller.setUpWorldHome(currentWorld);

        return newHomeTab;
    }

    private Tab createNPCTab(NPC npc) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/wikiNPCTemplate.fxml"));
        Tab newNPCTab = loader.load();
        WikiNPCTabController controller = loader.getController();
        controller.setCurrentWiki(this);

        controller.setUpNPC(npc);

        return newNPCTab;
    }

    private Tab createSettlementTab(Settlement settlement) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/wikiSettlementTemplate.fxml"));
        Tab newSettlementTab = loader.load();
        WikiSettlementTabController controller = loader.getController();
        controller.setCurrentWiki(this);

        controller.setUpSettlement(settlement);

        return newSettlementTab;
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setCurrentWorld(WorldGenerator.getWorldGenerator().getWorld());
        SceneNavigator.passWikiController(this);
        try {
            populateWiki();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
