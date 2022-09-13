package com.example.exchangerates.util;

import com.example.exchangerates.exception.ErrorMessages;
import com.example.exchangerates.model.CurrencyWithExchangeRates;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Currency;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.springframework.util.ResourceUtils.getFile;
@Component
public class XMLFileUtil {
    String INDENT = "4";
    String XSD_FILE = "exchange-rates.xsd";
    public void writeFile(List<CurrencyWithExchangeRates> currenciesWithExchangeRates, String filePath) {
        String xmlOutput = getXMLString(currenciesWithExchangeRates);
        try
        {
            //Create DocumentBuilder with default configuration
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();

            //Parse the content to Document object
            Document doc = builder.parse(new InputSource(new StringReader(xmlOutput)));

            //Use Transformer to write Document object to destination XML File
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", INDENT);
            DOMSource source = new DOMSource(doc);
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            FileWriter writer = new FileWriter(file);
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
            if(!file.isFile())
                throw new RuntimeException(ErrorMessages.XML_FILE_NOT_CREATED.getMessage());
        }
        catch (Exception e)
        {
            throw new RuntimeException(ErrorMessages.ERROR_WRITING_XML_TO_FILE.getMessage(), e);
        }
    }

    public String getXMLString(List<CurrencyWithExchangeRates> currenciesWithExchangeRates) {
        String xmlOutput = "<exchange-rates>";
        for(CurrencyWithExchangeRates currencyWithExchangeRates : currenciesWithExchangeRates) {
            xmlOutput += "<base-currency id=\"" + currencyWithExchangeRates.getBaseCurrency() + "\">";
            for(Map.Entry<Currency,Double> entry: currencyWithExchangeRates.getExchangeRates().entrySet()) {
                xmlOutput += "<currency id=\"" + entry.getKey() + "\">";
                xmlOutput += "<rate>" + entry.getValue() + "</rate>";
                xmlOutput += "</currency>";
            }
            xmlOutput += "</base-currency>";
        }
        xmlOutput += "</exchange-rates>";
        return xmlOutput;
    }

    public boolean validateFile(String filePath) {
        try {
            Validator validator = initValidator(XSD_FILE);
            System.out.println(filePath);
            validator.validate(new StreamSource(getFile(filePath)));
            return true;
        } catch (SAXException | IOException e) {
            throw new RuntimeException(ErrorMessages.ERROR_VALIDATING_XML_FILE.getMessage(), e);
        }
    }

    private Validator initValidator(String xsdPath) throws SAXException, FileNotFoundException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        File xsdFile = new File(Objects.requireNonNull(XMLFileUtil.class.getClassLoader().getResource(xsdPath)).getFile());
        Source schemaFile = new StreamSource(xsdFile);
        Schema schema = factory.newSchema(schemaFile);
        return schema.newValidator();
    }

    public String getFileNameWithExtension(String fileName) {
        return fileName + ".xml";
    }
}
