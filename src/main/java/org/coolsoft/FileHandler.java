package org.coolsoft;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Files;


public class FileHandler {

    private static String workDir = "src/main/resources/work/";
    private static String zipPath = "src/main/resources/work/test.zip";
    private static String docxPath = "src/main/resources/work/test.docx";

    private static String productName;

    Logger log = LoggerFactory.getLogger(this.getClass());

    public FileHandler() {}

    public FileHandler(String productName) {
        setProductName(productName);
    }

    public void setProductName(String productName) {
        this.productName = productName;
        this.zipPath = "src/main/resources/work/" + productName + ".zip";
        this.docxPath = "src/main/resources/work/" + productName + ".docx";
    }

    private void copyF(String path) {
        File originalFile = new File(path);
        File newFile = new File(docxPath);

        log.info("Kopiranje dokumenta v delovni direktorij...");

        try {
            Files.copy(originalFile.toPath(), newFile.toPath());
            log.info("...uspešno");
        } catch (IOException e) {

            log.error("neuspešno...", e);
        }
    }

    private void createZip() {
        File oldFile = new File(docxPath);
        File newFile = new File(zipPath);
        oldFile.renameTo(newFile);

        log.info("Kreiranje zipa");
    }

    private void unzip() {
        File file = new File(zipPath);

        log.info("Unzipanje datoteke v delovnem direktoriju...");

        try {
            ZipFile zipFile = new ZipFile(zipPath);
            zipFile.extractAll(workDir);
            file.delete();

            log.info("...uspešno");
        } catch (ZipException e) {

            log.error("...neuspešno", e);
        }
    }

    private void zip() throws ZipException, IOException {
        ZipFile zipFile = new ZipFile("src/main/resources/out/" + productName + ".docx");
        ZipParameters params = new ZipParameters();
        log.info("Kreiranje wordovega dokumenta...");
        try{
            for (File f : new File(workDir).listFiles()) {
                if (f.isFile()) {
                    zipFile.addFile(f, params);
                }
                if (f.isDirectory()) {
                    zipFile.addFolder(f, params);
                }
            }
            log.info("...uspešno");
        }

        catch (ZipException e){
            log.error("...neuspešno", e);
        }
    }

    private static String getExtension(File file) {
        String fileName = file.getName();
        if (fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".") + 1);
        else return "";
    }

    public void insertImage(String imagePath) throws IOException, NullPointerException {
        File source = new File(imagePath);

        File workDir = new File("src/main/resources/work/word/media/");
        String[] files = workDir.list();
        String ext = FilenameUtils.getExtension(files[0]);

        File dest = new File("src/main/resources/work/word/media/image1."+ext);
        FileChannel sourceChannel = null;
        FileChannel destChannel = null;

        try {
            sourceChannel = new FileInputStream(source).getChannel();
            destChannel = new FileOutputStream(dest).getChannel();
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally{
            assert sourceChannel != null;
            sourceChannel.close();
            assert destChannel != null;
            destChannel.close();
        }

    }

    private void deleteF() throws IOException {
        File directory = new File(workDir);
        FileUtils.cleanDirectory(directory);

        log.info("Brisanje delovnega direktorija");
    }

    public void primeFile(String templatePath) {
        copyF(templatePath);
        createZip();
        unzip();
    }

    public void postFile() {
        try {
            zip();
            deleteF();
        } catch (ZipException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
