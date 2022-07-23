package features;

public class Settlement {

    private String name;
    private String type;
    private char size;
    private int population;
    //private char government;

    public Settlement() {}

    /* Getters and Setters */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public char getSize() {
        return size;
    }

    public String getSizeString() {
        return switch (size) {
            case 's' -> "small";
            case 'l' -> "large";
            case 'n' -> "n/a";
            default -> null;
        };
    }

    public void setSize(char size) {
        this.size = size;
    }

    public int getPopulation() {
        return population;
    }

    public void setPopulation(int population) {
        this.population = population;
    }

//    public char getGovernment() {
//        return government;
//    }
//
//    public void setGovernment(char government) {
//        this.government = government;
//    }
}
