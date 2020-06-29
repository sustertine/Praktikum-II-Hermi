package org.coolsoft;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelReader {
    //private static final String testPath = "../resources/OŠTEVILČENJE IZJAV – 2020_vzrorec_1.xlsx";
    private String excelFilePath;
    private FileInputStream fileInputStream;
    private Workbook workbook;
    private Sheet sheet;


    Logger log = LoggerFactory.getLogger(this.getClass());

    public ExcelReader() {};

    public ExcelReader(String excelFilePath) {
        this.excelFilePath = excelFilePath;
    }

    //Vrne objekt tipa Product glede na izbrano ime
    public Product selectProduct(String idNumber) throws IOException {
        List<Product> vsi = getProducts();
        for (Product p : vsi) {
            if (p.getIdNumber().equals(idNumber)) {
                return p;
            }
        }
        return new Product();
    }

    //Samo vmes preverja če je vrstica popolnoma prazna
    private static boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c);
            if (cell != null && cell.getCellType() != CellType.BLANK)
                return false;
        }
        return true;
    }


    //Vrne headerje oz. imena atributov v xslx datoteki.
    public List<String> getHeaders() throws IOException {
        List<String> headers = new ArrayList<String>();
        fileInputStream = new FileInputStream(new File(excelFilePath));

        workbook = new XSSFWorkbook(fileInputStream);
        sheet = workbook.getSheetAt(0);
        Row header = sheet.getRow(0);

        Iterator<Cell> cellIterator = header.cellIterator();
        while (cellIterator.hasNext()) {
            Cell cell = cellIterator.next();
            headers.add(cell.getStringCellValue());

            //log.info("Header" +header);
        }
        fileInputStream.close();
        return headers;
    }

    public List<Product> getProducts() throws IOException {
        DataFormatter dataFormatter = new DataFormatter();
        List<Product> products = new ArrayList<>();
        fileInputStream = new FileInputStream(new File(excelFilePath));

        workbook = new XSSFWorkbook(fileInputStream);
        sheet = workbook.getSheetAt(0);

        Row headers = sheet.getRow(0);

        //Zaćasno okler se ne zmenimo o formatu .xlsx
        //headers.removeCell(headers.getCell(headers.getPhysicalNumberOfCells()));

        log.info("Pridobivanje produktov");

        int cols = headers.getPhysicalNumberOfCells();
        int rows = sheet.getPhysicalNumberOfRows();
        for (int i = 0; i < rows; i++) {
            Row row = sheet.getRow(i);
            if (isRowEmpty(row) == false) {
                Product product = new Product();
                if (row != headers && row.getCell(1).getCellType() != CellType.BLANK) {
                    Cell idCell = row.getCell(0);
                    String idVal = dataFormatter.formatCellValue(idCell);
                    for (int j = 0; j < cols; j++) {
                        Cell cell = row.getCell(j);
                        String cellValue = dataFormatter.formatCellValue(cell);
                        String headerKey = dataFormatter.formatCellValue(headers.getCell(j)).toUpperCase();
                        if (cell == null || cell.getCellType() == CellType.BLANK) {
                            product.addAttribute(headerKey, " ");
                            product.setIdNumber(idVal);
                        } else {
                            product.addAttribute(headerKey, cellValue);
                            product.setIdNumber(idVal);
                        }
                    }
                    products.add(product);
                }
            }
        }
        fileInputStream.close();
        return products;
    }

    public void printXlsx() throws IOException {
        fileInputStream = new FileInputStream(excelFilePath);

        workbook = new XSSFWorkbook(fileInputStream);
        sheet = workbook.getSheetAt(0);
        Row headers = sheet.getRow(0);
        System.out.println(headers.getPhysicalNumberOfCells());
    }

    //getterji setterji
    public String getExcelFilePath() {
        return excelFilePath;
    }


    public void setExcelFilePath(String excelFilePath) {
        this.excelFilePath = excelFilePath;
    }
}
