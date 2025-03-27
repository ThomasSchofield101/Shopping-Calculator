import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class Application {

    public static void main(String[] args){

        Application app = new Application();
        app.setupGui();
    }

    private void setupGui(){
        //creates a JFrame object to create a window that is then set to half the main monitor's resolution and centered
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width = gd.getDisplayMode().getWidth();
        int height = gd.getDisplayMode().getHeight();

        JFrame frame = new JFrame("GUI"); //creating the window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width/2, height/2);
        frame.setLocationRelativeTo(null);
        frame.getContentPane().setBackground(Color.DARK_GRAY);
        frame.setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout()); //creating the main panel for the program
        mainPanel.setBackground(Color.decode("#404040"));
        JPanel buttonPanel = new JPanel(new GridLayout(4, 1, 0, 30));

        buttonPanel.setBackground(Color.decode("#505050")); //creating a side panel for the buttons
        buttonPanel.setBorder(BorderFactory.createEtchedBorder(BevelBorder.RAISED));
        mainPanel.add(buttonPanel, BorderLayout.WEST);
        frame.setContentPane(mainPanel);

        JButton[] buttons = new JButton[4]; //initializing all the buttons + click events + array to iterate over them

        JTextPane textBox = setupTextBox(mainPanel);

        textBox.setText("Please click a button to begin using the app.");
        JButton readReceipts = new JButton("Read Receipts");
        buttons[1] = readReceipts;
        createAddReceipts(buttons, textBox, mainPanel);
        createReadReceipts(buttons, textBox, mainPanel);
        createAddProducts(buttons, textBox, mainPanel);
        createCalculateCost(buttons, mainPanel);

        for (JButton button : buttons) { //sets design for every button
            buttonPanel.add(button);
            button.setBackground(Color.decode("#909090"));
            button.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        }


        frame.setVisible(true);
    }

    private void createAddReceipts(JButton[] buttons, JTextPane textBox, JPanel mainPanel){
        JButton addReceipts = new JButton("Add Receipts from file");
        buttons[0] = addReceipts;

        addReceipts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Receipt receipt = receiptButtonsClick(textBox, mainPanel);
                try {
                    if (receipt != null) {
                        receipt.addProducts();
                        textBox.setText(textBox.getText() + "\n\nSuccessfully added products from receipt");
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    private void createReadReceipts(JButton[] buttons, JTextPane textBox, JPanel mainPanel){
        JButton readReceipts = new JButton("Read Receipts from file");
        buttons[1] = readReceipts;
        readReceipts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Receipt receipt = receiptButtonsClick(textBox, mainPanel);
                if (receipt != null) {
                    receipt.printProducts();
                }
            }
        });
    }

    private void createAddProducts(JButton[] buttons, JTextPane textBox, JPanel mainPanel){
        JButton addProducts = new JButton("Add Products manually");
        buttons[2] = addProducts;

        addProducts.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePanel(mainPanel, "inputpanel");

                textBox.setText("");

                GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
                int width = gd.getDisplayMode().getWidth();
                int height = gd.getDisplayMode().getHeight();

                JPanel inputPanel = new JPanel(new GridBagLayout());
                inputPanel.setName("inputPanel");
                mainPanel.add(inputPanel, BorderLayout.CENTER);
                GridBagConstraints c;
                c = new GridBagConstraints();
                c.weightx = 1.0;
                c.weighty = 1.0;
                c.fill = GridBagConstraints.BOTH;
                c.gridwidth = 2;
                c.gridx = 0;
                c.gridy = 0;

                JTextArea inputText = new JTextArea(2,1);
                inputText.setText("Please enter below the name of the product (left) and the cost (right) as an integer e.g. apples 149 (would be apples £1.49)");
                inputText.setLineWrap(true);
                inputText.setWrapStyleWord(true);
                inputText.setBackground(Color.decode("#404040"));
                inputText.setForeground(Color.decode("#DDDDDD"));
                inputText.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
                inputText.setFont(new Font("Arial", Font.PLAIN, 20));
                inputPanel.add(inputText, c);
                ;
                JButton confirmButton = new JButton("Confirm");
                c.gridy = 2;
                inputPanel.add(confirmButton, c);
                confirmButton.setBackground(Color.decode("#404060"));
                confirmButton.setForeground(Color.decode("#DDDDDD"));
                confirmButton.setBorder(BorderFactory.createEtchedBorder());

                c.gridwidth = 1;
                c.gridy = 1;

                JTextField nameInput = setupInputBox(inputPanel, c);
                inputPanel.add(nameInput, c);

                c.gridx = 1;

                JTextField costInput = setupInputBox(inputPanel, c);
                inputPanel.add(costInput, c);

                mainPanel.remove(textBox);

                confirmButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        String name = nameInput.getText().toLowerCase();
                        int cost = Integer.parseInt(costInput.getText());
                        Product product = new Product(name, cost);
                        product.writeToFile();
                        removePanel(mainPanel, "inputpanel");
                        mainPanel.add(textBox);
                        textBox.setText("Successfully added product to file");
                    }
                });

            }

        });
    }

    private JTextField setupInputBox(JPanel inputPanel, GridBagConstraints c) {
        JTextField inputBox;
        inputBox = new JTextField("");
        inputBox.setHorizontalAlignment(SwingConstants.CENTER);
        inputBox.setBackground(Color.decode("#202020"));
        inputBox.setForeground(Color.decode("#DDDDDD"));
        inputBox.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        inputBox.setFont(new Font("Arial", Font.PLAIN, 30));
        return inputBox;
    }

    private void createCalculateCost(JButton[] buttons, JPanel mainPanel){
        JButton calculateCost = new JButton("Calculate Cost");
        buttons[3] = calculateCost;

        calculateCost.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                removePanel(mainPanel, "inputpanel");
                removePanel(mainPanel, "textBox");

                JPanel inputPanel = new JPanel(new GridBagLayout());
                JScrollPane scrollPane = new JScrollPane(inputPanel);
                scrollPane.getVerticalScrollBar().setUnitIncrement(30);
                scrollPane.getVerticalScrollBar().setBackground(Color.decode("#202020"));
                scrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI(){
                    @Override
                        protected void configureScrollBarColors(){
                        this.thumbColor = Color.decode("#303030");
                    }
                });
                mainPanel.add(scrollPane, BorderLayout.CENTER);
                inputPanel.setBackground(Color.decode("#404040"));
                scrollPane.setName("inputpanel");
                inputPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                GridBagConstraints c;
                c = new GridBagConstraints();
                c.weightx = 1.0;
                c.weighty = 0;
                c.fill = GridBagConstraints.NONE;
                c.insets = new Insets(10,10,10,10);

                JTextField infoText = new JTextField("Please choose a product:");
                infoText.setBackground(Color.decode("#606060"));
                infoText.setForeground(Color.decode("#DDDDDD"));
                infoText.setFont(new Font("Arial", Font.PLAIN, 20));
                infoText.setHorizontalAlignment(SwingConstants.CENTER);
                infoText.setEditable(false);
                infoText.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                inputPanel.add(infoText, c);

                JComboBox productsDropDown = new JComboBox();
                File productsFile = new File("Products.txt");
                double[] costs;
                try {
                    costs = new double[Files.readAllLines(productsFile.toPath(), StandardCharsets.UTF_8).size()];
                    int index = 0;
                    for (String line : Files.readAllLines(productsFile.toPath(), StandardCharsets.UTF_8)){
                        String[] parts = line.split(",");
                        double cost = (double) Integer.parseInt(parts[1]) / 100;
                        costs[index] = cost;
                        String fixedLine = parts[0] + " £" + String.format("%.2f",cost);
                        productsDropDown.addItem(fixedLine);
                        index++;
                    }
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

                c.gridy = 1;
                inputPanel.add(productsDropDown, c);

                JButton addProductButton = new JButton("Add Product");
                c.gridy = 2;
                addProductButton.setBackground(Color.decode("#404060"));
                addProductButton.setForeground(Color.decode("#DDDDDD"));
                addProductButton.setBorder(BorderFactory.createEtchedBorder());
                addProductButton.setFont(new Font("Arial", Font.PLAIN, 20));
                addProductButton.setPreferredSize(infoText.getPreferredSize());
                inputPanel.add(addProductButton, c);

                JTextArea itemsAdded = new JTextArea(2,1);
                itemsAdded.setEditable(false);
                itemsAdded.setText("Items Added:");
                itemsAdded.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                itemsAdded.setForeground(Color.decode("#DDDDDD"));
                itemsAdded.setFont(new Font("Arial", Font.PLAIN, 20));
                itemsAdded.setBackground(Color.decode("#404040"));
                c.gridy = 3;
                inputPanel.add(itemsAdded, c);

                c.gridy = 4;
                final double[] totalCost = {0};
                JTextField totalCostText = new JTextField("£");
                totalCostText.setPreferredSize(infoText.getPreferredSize());
                totalCostText.setBackground(Color.decode("#202020"));
                totalCostText.setForeground(Color.decode("#DDDDDD"));
                totalCostText.setFont(new Font("Arial", Font.PLAIN, 20));
                totalCostText.setHorizontalAlignment(SwingConstants.CENTER);
                totalCostText.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
                inputPanel.add(totalCostText, c);

                addProductButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        itemsAdded.setText(itemsAdded.getText() + "\n" + productsDropDown.getSelectedItem());
                        totalCost[0] += costs[productsDropDown.getSelectedIndex()];
                        totalCostText.setText("Total Cost: £" + String.format("%.2f", totalCost[0]));
                    }
                });
            }
        });
    }

    private void removePanel(JPanel parentPanel, String nameToRemove){
        Component[] mainPanelComponents = parentPanel.getComponents();
        for (Component component : mainPanelComponents) {
            if (component.getName() != null && component.getName().equalsIgnoreCase(nameToRemove)) {
                parentPanel.remove(component);
                break;
            }
        }
        parentPanel.revalidate();
        parentPanel.repaint();
    }

    private Receipt receiptButtonsClick(JTextPane textBox, JPanel mainPanel){
        removePanel(mainPanel, "inputpanel");
        mainPanel.add(textBox);

        JFileChooser fileChooser = new JFileChooser();
        int choice = fileChooser.showOpenDialog(null);
        if (choice != JFileChooser.APPROVE_OPTION) {
            return null;
        }
        Path filepath = fileChooser.getSelectedFile().getAbsoluteFile().toPath();

        try {
            String fileName = filepath.getFileName().toString();
            fileName = fileName.replace(".txt", "");
            fileName = fileName.replace('_', '/');
            textBox.setText(fileName + "\n");
            return new Receipt(filepath, textBox);

        } catch (IOException ex) {
            System.out.println("Exception Raised: " + ex.getMessage());
        }
        return null;
    }

    private JTextPane setupTextBox(JPanel mainPanel){
        JTextPane textBox = new JTextPane(); //creates a textbox to print values on screen
        textBox.setName("textBox");
        mainPanel.add(textBox);
        textBox.setBackground(Color.decode("#404040"));
        textBox.setForeground(Color.decode("#DDDDDD"));
        textBox.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
        textBox.setFont(new Font("Arial", Font.PLAIN, 20));
        textBox.setEditable(false);
        SimpleAttributeSet centerAttribute = new SimpleAttributeSet();
        StyleConstants.setAlignment(centerAttribute, StyleConstants.ALIGN_CENTER);
        textBox.getStyledDocument().setParagraphAttributes(0, textBox.getStyledDocument().getLength(), centerAttribute, false);
        return textBox;
    }
}
