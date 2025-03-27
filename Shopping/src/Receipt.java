import javax.swing.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Scanner;

public class Receipt {
    private int totalCost;
    private final Product[] Products;
    private final int numProducts;
    private final File receiptFile;
    private final JTextPane textBox;

    public Receipt(Path filepathIn, JTextPane textBox) throws IOException {
        this.textBox = textBox;
        receiptFile = new File(filepathIn.toString());

        ArrayList<String> receiptFileContents = (ArrayList<String>) Files.readAllLines(filepathIn, StandardCharsets.UTF_8);
        numProducts = receiptFileContents.size();
        Products = new Product[numProducts];

        setupProducts();
    }

    public int getTotalCost() {
        return totalCost;
    }

    public void printProducts(){
        for (int i = 0; i < numProducts; i++){
            double cost = (double) Products[i].getCost() / 100;

            textBox.setText(textBox.getText() + "\n" + Products[i].getName() + " : £" + String.format("%.2f",cost));
        }
    }

    private void setupProducts() throws FileNotFoundException {
        Scanner reader = new Scanner(receiptFile);
        int index = 0;
        while(reader.hasNextLine()){
            String line = reader.nextLine();
            String[] parts = line.split(",");
            Products[index] = new Product(parts[0].toLowerCase(), Integer.parseInt(parts[1]));
            totalCost += Integer.parseInt(parts[1]);
            index++;
        }
        reader.close();
    }

    public void addProducts() throws IOException {
        for (int i = 0; i < numProducts; i++){
            Products[i].writeToFile();
        }
    }
}
