package org.coolsoft;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

//Macro: Alt + P

public class Controller {

    @FXML
    public AnchorPane mainPane;
    @FXML
    public ListView<String> dataListView;
    @FXML
    public ListView<String> templatesListView;
    @FXML
    public TextField prodSearchTextField;
    @FXML
    public Button prodSearchBtn;
    @FXML
    public ListView<String> productsListView;
    @FXML
    public Button selectAllBtn;
    @FXML
    public Button generateBtn;
    @FXML
    public ListView<String> wordListView;
    @FXML
    public ListView<String> pdfListView;
    @FXML
    public Button readExcelBtn;
    @FXML
    public ComboBox<String> langComboBox;

    // -------------Paths-------------------------
    private static final String contentRoot = "src/main/resources/";

    private static final String dataPath = contentRoot + "data/";
    private static final String templatesPath = contentRoot + "templates/";
    private static final String imgPath = contentRoot + "img/";
    private static final String outPath = contentRoot + "out/";
    private static final String workPath = contentRoot + "work/";
    private static final String xmlPath = contentRoot + "work/word/document.xml";

    //-----------------Functionals----------------
    private ExcelReader excelReader;
    private FileHandler fileHandler;
    private Statement statement;

    // ------------Utils--------------------------
    Logger log = LoggerFactory.getLogger(this.getClass());

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    private static void clearListView(ListView listView) {
        if (!listView.getItems().isEmpty()) {
            listView.getItems().clear();
        }
    }

    //-----------Init-------------------------
    @FXML
    public void initialize() {
        populateFolderListViews();
        productsListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        langComboBox.getItems().add("SLO");
        langComboBox.getSelectionModel().select(0);

        System.out.println("Initializing...");
    }

    // ----------Main methdos------------------
    public void populateFolderListViews() {
        File[] listOfFiles;
        clearListView(dataListView);
        clearListView(templatesListView);
        clearListView(pdfListView);
        clearListView(wordListView);

        File dataFolder = new File(dataPath);
        File templatesFolder = new File(templatesPath);
        File outFolder = new File(outPath);

        listOfFiles = dataFolder.listFiles();
        for (File f : listOfFiles) {
            if (!dataListView.getItems().contains(f.getName()) && !f.getName().contains("~$")) {
                dataListView.getItems().add(f.getName());
            }
        }


        listOfFiles = templatesFolder.listFiles();
        for (File f : listOfFiles) {
            if (!templatesListView.getItems().contains(f.getName()) && !f.getName().contains("~$")) {
                templatesListView.getItems().add(f.getName());
            }
        }

        listOfFiles = outFolder.listFiles();
        for (File f : listOfFiles) {
            if (getFileExtension(f).equals("pdf") && !f.getName().contains("~$")) {
                if (!pdfListView.getItems().contains(f.getName())) {
                    pdfListView.getItems().add(f.getName());
                }
            } else {
                if (!wordListView.getItems().contains(f.getName()) && !f.getName().contains("~$")) {
                    wordListView.getItems().add(f.getName());
                }
            }
        }
    }


    public void populateOutListViews() {
        File[] listOfFiles;
        File outFolder = new File(outPath);

        clearListView(wordListView);
        clearListView(pdfListView);

        listOfFiles = outFolder.listFiles();
        for (File f : listOfFiles) {
            if (getFileExtension(f).equals("pdf")) {
                if (!pdfListView.getItems().contains(f.getName())) {
                    pdfListView.getItems().add(f.getName());
                }
            } else {
                if (!wordListView.getItems().contains(f.getName())) {
                    wordListView.getItems().add(f.getName());
                }
            }
        }
    }

    @FXML
    public void generateFiles(ActionEvent actionEvent) {
        String dataFilePath = dataPath + dataListView.getSelectionModel().getSelectedItem();
        String templateFilePath = templatesPath + templatesListView.getSelectionModel().getSelectedItem();

        String lang = langComboBox.getSelectionModel().getSelectedItem();


        excelReader = new ExcelReader();
        excelReader.setExcelFilePath(dataFilePath);

        fileHandler = new FileHandler();

        statement = new Statement();
        statement.setDocumentXmlPath(xmlPath);

        List products = productsListView.getSelectionModel().getSelectedItems();

        if (productsListView.getSelectionModel().getSelectedItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Napaka: Izbiranje produkta");
            alert.setHeaderText("Potrebna je izbira vsaj enega produkta !");
            alert.showAndWait();

        } else if (templatesListView.getSelectionModel().getSelectedItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Napaka: Izbiranje predloge");
            alert.setHeaderText("Potrebna je izbira predloge!");
            alert.showAndWait();

        } else if (dataListView.getSelectionModel().getSelectedItems().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Napaka: Izbiranje EXCELA");
            alert.setHeaderText("Potrebna je izbira EXCEL datoteke!");
            alert.showAndWait();
        } else {
            for (Object o : products) {
                String productName = o.toString();
                try {
                    Product product = excelReader.selectProduct(productName);
                    fileHandler.setProductName(productName);
                    fileHandler.primeFile(templateFilePath);
                    fileHandler.insertImage(product.getAttributes().get("SLIKA"));
                    statement.populateDoc(product, lang);
                    fileHandler.postFile();
                    populateOutListViews();
                } catch (IOException | NullPointerException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Napaka: Branje Word");
                    alert.setHeaderText("Napaka pri pisanju WORD dokumenta !");
                    alert.showAndWait();
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void selectAll(ActionEvent actionEvent) {
        productsListView.getSelectionModel().selectAll();
    }

    @FXML
    public void readExcel(ActionEvent actionEvent) {
        String excelPath = dataPath + dataListView.getSelectionModel().getSelectedItem();
        excelReader = new ExcelReader();
        excelReader.setExcelFilePath(excelPath);

        try {
            for (Product p : excelReader.getProducts()) {
                if (!productsListView.getItems().contains(p.getIdNumber()))
                    productsListView.getItems().add(p.getIdNumber());
            }

            List<String> list = excelReader.getHeaders();
            for (Object o : list) {
                String header = o.toString();
                String[] split = header.split("_");

                String suffix = split[split.length - 1];
                if (split.length > 1 && !langComboBox.getItems().contains(suffix)) {
                    langComboBox.getItems().add(suffix);
                }
            }

        } catch (IOException | NullPointerException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Napaka: Branje Excel");
            alert.setHeaderText("Napaka pri branju EXCEL dokumenta !");
            alert.showAndWait();
            e.printStackTrace();
        }


    }

    @FXML
    public void searchProducts() {
        String query = prodSearchTextField.getText();
        excelReader = new ExcelReader();
        excelReader.setExcelFilePath(dataPath + dataListView.getSelectionModel().getSelectedItem());
        try {
            List<Product> prods = excelReader.getProducts();
            if (!query.equals("") || !query.equals(" ")) {
                clearListView(productsListView);
                for (Product p : prods) {
                    if (p.getIdNumber().contains(query)) {
                        productsListView.getItems().add(p.getIdNumber());
                    }
                }
            } else {
                for (Product p : prods) {
                    clearListView(productsListView);
                    productsListView.getItems().add(p.getIdNumber());
                }
                if (query.equals("")) {
                    clearListView(productsListView);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void openInExcel(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String link = dataPath + dataListView.getSelectionModel().getSelectedItem();
            File file = new File(link);
            if (!Desktop.isDesktopSupported()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Namizje nepodprto");
                alert.setHeaderText("Vase namizje je nepodprto!");
                alert.showAndWait();
            }
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void openTemplateInWord(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String link = templatesPath + templatesListView.getSelectionModel().getSelectedItem();
            File file = new File(link);
            if (!Desktop.isDesktopSupported()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Namizje nepodprto");
                alert.setHeaderText("Vase namizje je nepodprto!");
                alert.showAndWait();
            }
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void openGeneratedInWord(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String link = outPath + wordListView.getSelectionModel().getSelectedItem();
            File file = new File(link);
            if (!Desktop.isDesktopSupported()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Namizje nepodprto");
                alert.setHeaderText("Vase namizje je nepodprto!");
                alert.showAndWait();
            }
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void openGeneratedInPdf(MouseEvent event) {
        if (event.getClickCount() == 2) {
            String link = outPath + pdfListView.getSelectionModel().getSelectedItem();
            File file = new File(link);
            if (!Desktop.isDesktopSupported()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Namizje nepodprto");
                alert.setHeaderText("Vase namizje je nepodprto!");
                alert.showAndWait();
            }
            Desktop desktop = Desktop.getDesktop();
            if (file.exists()) {
                try {
                    desktop.open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void refreshGui(ActionEvent actionEvent) {
        populateFolderListViews();
    }


}
