package org.coolsoft;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Statement {

    private String documentXmlPath;

    Logger log = LoggerFactory.getLogger(this.getClass());


    public Statement() {};

    public Statement(String documentXmlPath) {
        this.documentXmlPath = documentXmlPath;
    }

    public String getDocumentXmlPath() {
        return documentXmlPath;
    }

    public void setDocumentXmlPath(String documentXmlPath) {
        this.documentXmlPath = documentXmlPath;
    }

    public void populateDoc(Product product, String lang) throws NullPointerException {
        try {
            //Atributi
            Map<String, String> properties = product.getAttributes();

            //Grajenje dokumenta z dom parser
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(documentXmlPath);


            NodeList nodeList = document.getElementsByTagName("w:t");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getTextContent().replaceAll("\\s+", "") == "") {
                    continue;
                }
                String content = node.getTextContent();
                Pattern pattern = Pattern.compile("<<#(.*?)>>|&lt;&lt;#(.*?)&gt;&gt;");
                Matcher matcher = pattern.matcher(content);

                log.info("Found content: " + content);

                if (matcher.find()) {
                    String propName = matcher.group(1);
                    log.info("Matcher found string: " + propName);
                    if(properties.keySet().contains(propName+"_"+lang)) {
                        propName += "_" + lang;
                        log.info("Found lang: " + lang + ", new value is: " + propName);
                    }
                    String propVal = properties.get(propName);
                    log.info(propName + " : " + product.getAttributes().get(propName));
                    log.info("Replacing: " + propName + " with " + propVal);
                    //System.out.println(propName + " : " + propVal);

                    String newContent = content.replaceAll("<<#(.*?)>>|&lt;&lt;#(.*?)&gt;&gt;", propVal);
                    node.setTextContent(newContent);
                    saveChanges(document);
                }
            }
            log.info("...uspešno");
        }
        catch (ParserConfigurationException | IOException | SAXException e) {
            log.error("...neuspešno", e);
        }
    }

    //Nevem najbolj kaj tocno to dela amapk shrani spremembe v dokumentu, namenjeno za uporabo znotraj classa
    private void saveChanges(Document document) {

        log.info("Shranjevanja word dokumenta...");

        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            DOMSource source = new DOMSource(document);
            OutputStream stream = new FileOutputStream(documentXmlPath);
            StreamResult sresult = new StreamResult(stream);
            transformer.transform(source, sresult);
            stream.close();
        }
        catch (TransformerException | IOException e) {
            log.error("...neuspešno", e);
        }
    }

}
