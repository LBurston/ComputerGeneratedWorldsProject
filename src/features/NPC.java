package features;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;


public class NPC extends Being {

    Random randNum;
    String name;
    String race;
    int age;
    char gender;
    boolean isAlive;
    int height;
//    char hairLength;
//    String hairColour;
//    String eyeColour;
//    String occupation;
//    String alignment;
//    String bond;
//    String ideal;
//    String flaw;
//    String talent;
//    String highAbility;
//    String lowAbility;
//    String quirk;
//    String interaction;

    public NPC(HashMap<String, Integer> resources) {
        randNum = new Random();

        // Assign Race
        try (Stream<String> lines = Files.lines(Paths.get("src/resources/npc/used/npcRace.txt"))){
            race = lines.skip(randNum.nextInt(resources.get("npcRace"))).findFirst().get();
        } catch(IOException e){
            e.printStackTrace();
        }

        // Assign Gender
        int ageNum = randNum.nextInt(9);
        if (ageNum < 4) {
            gender = 'm';
        } else if (ageNum < 8) {
            gender = 'f';
        } else {
            gender = 'n';
        }

        // Assign First Name based on Gender
        String givenName = "";
        String file = "";
        String list = "";
        if (gender == 'm') {
            file = "src/resources/npc/used/names/npcMaleFirstNames.txt";
            list = "npcMaleFirstNames";
        } else if (gender == 'f') {
            file = "src/resources/npc/used/names/npcFemaleFirstNames.txt";
            list = "npcFemaleFirstNames";
        } else if (randNum.nextBoolean()) {
            file = "src/resources/npc/used/names/npcMaleFirstNames.txt";
            list = "npcMaleFirstNames";
        } else {
            file = "src/resources/npc/used/names/npcFemaleFirstNames.txt";
            list = "npcFemaleFirstNames";
        }
        try (Stream<String> lines = Files.lines(Paths.get(file))){
            givenName = lines.skip(randNum.nextInt(resources.get(list))).findFirst().get();
        } catch(IOException e){
            e.printStackTrace();
        }

        // Assign Last Name
        try (Stream<String> lines = Files.lines(Paths.get("src/resources/npc/used/names/npcLastNames.txt"))){
            givenName += " " + lines.skip(randNum.nextInt(resources.get("npcLastNames"))).findFirst().get();
        } catch(IOException e){
            e.printStackTrace();
        }
        name = givenName;
    }


    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public char getGender() {
        return gender;
    }

    public void setGender(char gender) {
        this.gender = gender;
    }

    public boolean isAlive() {
        return isAlive;
    }

    public void setAlive(boolean alive) {
        isAlive = alive;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

//    public char getHairLength() {
//        return hairLength;
//    }
//
//    public void setHairLength(char hairLength) {
//        this.hairLength = hairLength;
//    }
//
//    public String getHairColour() {
//        return hairColour;
//    }
//
//    public void setHairColour(String hairColour) {
//        this.hairColour = hairColour;
//    }
//
//    public String getEyeColour() {
//        return eyeColour;
//    }
//
//    public void setEyeColour(String eyeColour) {
//        this.eyeColour = eyeColour;
//    }
//
//    public String getOccupation() {
//        return occupation;
//    }
//
//    public void setOccupation(String occupation) {
//        this.occupation = occupation;
//    }
//
//    public String getAlignment() {
//        return alignment;
//    }
//
//    public void setAlignment(String alignment) {
//        this.alignment = alignment;
//    }
//
//    public String getBond() {
//        return bond;
//    }
//
//    public void setBond(String bond) {
//        this.bond = bond;
//    }
//
//    public String getIdeal() {
//        return ideal;
//    }
//
//    public void setIdeal(String ideal) {
//        this.ideal = ideal;
//    }
//
//    public String getFlaw() {
//        return flaw;
//    }
//
//    public void setFlaw(String flaw) {
//        this.flaw = flaw;
//    }
//
//    public String getTalent() {
//        return talent;
//    }
//
//    public void setTalent(String talent) {
//        this.talent = talent;
//    }
//
//    public String getHighAbility() {
//        return highAbility;
//    }
//
//    public void setHighAbility(String highAbility) {
//        this.highAbility = highAbility;
//    }
//
//    public String getLowAbility() {
//        return lowAbility;
//    }
//
//    public void setLowAbility(String lowAbility) {
//        this.lowAbility = lowAbility;
//    }
//
//    public String getQuirk() {
//        return quirk;
//    }
//
//    public void setQuirk(String quirk) {
//        this.quirk = quirk;
//    }
//
//    public String getInteraction() {
//        return interaction;
//    }
//
//    public void setInteraction(String interaction) {
//        this.interaction = interaction;
//    }
}
