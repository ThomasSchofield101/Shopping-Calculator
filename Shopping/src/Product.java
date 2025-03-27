import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

public class Product {
    private int cost;
    private String name;

    public Product(String name, int cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getCost() {
        return cost;
    }
    public void setCost(int cost) {
        this.cost = cost;
    }
    public void writeToFile(){
        File allProducts = new File("Products.txt");
        FileWriter writer = null;

        boolean contains = false;
        try {
            for (String line : Files.readAllLines(allProducts.toPath(), StandardCharsets.UTF_8)){
                if (line.toLowerCase().contains(name)) {
                    contains = true;
                    break;
                }
            }
            if (!contains){
                writer = new FileWriter(allProducts, true);
                writer.append("\n").append(name).append(",").append(String.valueOf(cost));
                writer.close();
            }
        } catch (IOException e){
            throw new RuntimeException("Failed to write to file: " + e);
        }


    }
}
