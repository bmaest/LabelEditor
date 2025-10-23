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

import sia.labelEditor.models.LabelBody;
import sia.labelEditor.models.LabelCell;
import sia.labelEditor.models.LabelDetail;
import sia.labelEditor.models.LabelDocument;
import sia.labelEditor.models.LabelGrid;
import sia.labelEditor.models.LabelProperty;
import sia.labelEditor.models.LabelRow;
import sia.labelEditor.models.LabelTable;
import sia.labelEditor.models.NestedGridContent;
import sia.labelEditor.parser.LabelParser;
import sia.labelEditor.writer.LabelWriter;

@Service
public class LabelService {

    private LabelDocument labelDoc;
    private Document rawDoc;
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
        try {
            File output = new File("edited_label.rptdesign");

            if (labelDoc != null) {
                LabelWriter writer = new LabelWriter();
                ByteArrayOutputStream mem = writer.writeToMemory(labelDoc);
                try (FileOutputStream fos = new FileOutputStream(output)) {
                    mem.writeTo(fos);
                }
                return output;
            }

            if (rawDoc != null) {
                Transformer transformer = TransformerFactory.newInstance().newTransformer();
                transformer.transform(new DOMSource(rawDoc), new StreamResult(output));
                return output;
            }

            return null;
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

    public void updateLabelDimensions(double width, double height) {
        if (labelDoc == null || labelDoc.getBody() == null || labelDoc.getBody().getTable() == null) {
            throw new IllegalStateException("LabelDocument not initialized");
        }

        LabelTable table = labelDoc.getBody().getTable();
        updateProperty(table.getProperties(), "width", width + "in");
        updateProperty(table.getProperties(), "height", height + "in");
    }

    private void updateProperty(List<LabelProperty> props, String name, String newValue) {
        for (LabelProperty prop : props) {
            if (prop.getName().equals(name)) {
                prop.setValue(newValue);
                return;
            }
        }
        props.add(new LabelProperty(name, newValue));
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

    public ByteArrayOutputStream getLabelStream() throws Exception {
        LabelWriter writer = new LabelWriter();
        return writer.writeToMemory(labelDoc);
    }

    public Map<String, Object> getLabelStructure() {
        if (labelDoc == null) return Map.of("name", "LabelDocument", "children", List.of());
        return buildNode("LabelDocument", labelDoc);
    }

    private Map<String, Object> buildNode(String name, Object obj) {
        Map<String, Object> node = new LinkedHashMap<>();
        node.put("name", name);
        List<Map<String, Object>> children = new ArrayList<>();

        if (obj instanceof LabelDocument doc) {
            children.add(buildNode("Body", doc.getBody()));
        } else if (obj instanceof LabelBody body) {
            children.add(buildNode("Table", body.getTable()));
        } else if (obj instanceof LabelTable table) {
            for (int i = 0; i < table.getColumns().size(); i++) {
                children.add(buildNode("Column[" + i + "]", table.getColumns().get(i)));
            }
            children.add(buildNode("Detail", table.getDetail()));
        } else if (obj instanceof LabelDetail detail) {
            for (int i = 0; i < detail.getRows().size(); i++) {
                children.add(buildNode("Row[" + i + "]", detail.getRows().get(i)));
            }
        } else if (obj instanceof LabelRow row) {
            for (int i = 0; i < row.getCells().size(); i++) {
                children.add(buildNode("Cell[" + i + "]", row.getCells().get(i)));
            }
        } else if (obj instanceof LabelCell cell) {
            if (cell.getContent() instanceof NestedGridContent ngc) {
                children.add(buildNode("Grid", ngc.getGrid()));
            }
        } else if (obj instanceof LabelGrid grid) {
            for (int i = 0; i < grid.getRows().size(); i++) {
                children.add(buildNode("Row[" + i + "]", grid.getRows().get(i)));
            }
        }

        if (!children.isEmpty()) node.put("children", children);
        return node;
    }
}
