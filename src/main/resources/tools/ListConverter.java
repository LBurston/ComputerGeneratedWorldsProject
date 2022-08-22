package resources.tools;


import java.io.*;

public class  ListConverter {

    public static void main(String[] args) {
        String file = "";

        try {
            BufferedReader reader = new BufferedReader((new FileReader(file)));
            String content = reader.readLine();
            reader.close();
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            String[] items = content.split(", ");
            for (int i = 0; i < items.length; i++) {
                if (i == 0) {
                    writer.write(items[i]);
                } else {
                    writer.write("\n" + items[i]);
                }
            }
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ListConverter() {
    }
}
