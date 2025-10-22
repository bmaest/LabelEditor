package sia.labelEditor.services;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import sia.labelEditor.models.LabelDocument;
import sia.labelEditor.parser.LabelParser;

@Service
public class LabelService {

    private LabelDocument labelDoc;
    private Document rawDoc; // optional: keep raw DOM for export
    private final LabelParser parser = new LabelParser();
    private final Path outputPath = Paths.get("label.rptdesign");

    public LabelDocument getLabelDocument() {
        return labelDoc;
    }

    public void loadLabel(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(is);
            this.rawDoc = doc;
            this.labelDoc = parser.parse(doc);
            System.out.println("Label parsed and stored");
            labelDoc.debug();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createLabel(double width, double height) {
        try {
            InputStream templateStream = getClass().getClassLoader().getResourceAsStream("base_template.rptdesign");
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(templateStream);

            NodeList pages = doc.getElementsByTagName("simple-master-page");
            for (int i = 0; i < pages.getLength(); i++) {
                Element page = (Element) pages.item(i);
                NodeList props = page.getElementsByTagName("property");
                for (int j = 0; j < props.getLength(); j++) {
                    Element prop = (Element) props.item(j);
                    String name = prop.getAttribute("name");
                    if ("height".equals(name)) {
                        prop.setTextContent(height + "in");
                    }
                    if ("width".equals(name)) {
                        prop.setTextContent(width + "in");
                    }
                }
            }

            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(outputPath.toFile()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getLabelFile() {
        if (rawDoc == null) return null;
        try {
            File output = new File("edited_label.rptdesign");
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(rawDoc), new StreamResult(output));
            return output;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Map<String, Integer> getLabelSize() {
        Map<String, Integer> size = new HashMap<>();
        size.put("width", 0);
        size.put("height", 0);

        if (rawDoc == null) return size;

        NodeList masters = rawDoc.getElementsByTagName("simple-master-page");
        for (int i = 0; i < masters.getLength(); i++) {
            Element master = (Element) masters.item(i);
            NodeList children = master.getChildNodes();

            String type = "custom";
            String widthStr = null;
            String heightStr = null;

            for (int j = 0; j < children.getLength(); j++) {
                Node child = children.item(j);
                if (child.getNodeType() == Node.ELEMENT_NODE && "property".equals(child.getNodeName())) {
                    Element prop = (Element) child;
                    String name = prop.getAttribute("name").trim().toLowerCase();
                    String value = prop.getTextContent().trim().toLowerCase();

                    if ("type".equals(name)) {
                        type = value;
                    } else if ("width".equals(name)) {
                        widthStr = value;
                    } else if ("height".equals(name)) {
                        heightStr = value;
                    }
                }
            }

            if ("us-letter".equals(type)) {
                size.put("width", 850);
                size.put("height", 1100);
                return size;
            }

            if ("custom".equals(type) && widthStr != null && heightStr != null) {
                size.put("width", parseDimension(widthStr));
                size.put("height", parseDimension(heightStr));
                return size;
            }
        }

        return size;
    }

    private int parseDimension(String value) {
        try {
            value = value.trim().toLowerCase();
            if (value.endsWith("in")) {
                return (int)(Double.parseDouble(value.replace("in", "")) * 100);
            } else if (value.endsWith("pt")) {
                return (int)(Double.parseDouble(value.replace("pt", "")) * 100 / 72);
            } else if (value.endsWith("cm")) {
                return (int)(Double.parseDouble(value.replace("cm", "")) * 100 / 2.54);
            } else if (value.endsWith("mm")) {
                return (int)(Double.parseDouble(value.replace("mm", "")) * 100 / 25.4);
            } else {
                return (int)(Double.parseDouble(value)); // assume pixels
            }
        } catch (Exception e) {
            System.out.println("Failed to parse dimension: '" + value + "'");
            return 0;
        }
    }

    public void reset() {
        this.labelDoc = null;
        this.rawDoc = null;
        System.out.println("LabelService: state reset");
    }
}
