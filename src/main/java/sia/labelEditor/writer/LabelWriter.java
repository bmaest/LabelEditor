package sia.labelEditor.writer;

import sia.labelEditor.models.*;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

public class LabelWriter {

    public ByteArrayOutputStream writeToMemory(LabelDocument label) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.newDocument();

        Element report = doc.createElementNS("http://www.eclipse.org/birt/2005/design", "report");
        report.setAttribute("version", "3.2.23");
        report.setAttribute("id", "1");
        doc.appendChild(report);

        injectStaticHeader(doc, report);
        buildBody(doc, report, label.getBody());

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        transformer.transform(new DOMSource(doc), new StreamResult(out));
        return out;
    }

    private void injectStaticHeader(Document doc, Element report) throws Exception {
        InputStream input = getClass().getClassLoader().getResourceAsStream("static/label_header.txt");
        if (input == null) throw new FileNotFoundException("label_header.txt not found in resources/static");

        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document headerDoc = builder.parse(input);
        Element headerRoot = headerDoc.getDocumentElement();

        NodeList children = headerRoot.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node imported = doc.importNode(children.item(i), true);
            report.appendChild(imported);
        }
    }

    private void buildBody(Document doc, Element report, LabelBody body) {
        Element bodyEl = doc.createElement("body");
        appendProperties(doc, bodyEl, body.getProperties());
        report.appendChild(bodyEl);

        LabelTable table = body.getTable();
        if (table != null) {
            Element tableEl = doc.createElement("table");
            tableEl.setAttribute("id", table.getId());
            appendProperties(doc, tableEl, table.getProperties());
            bodyEl.appendChild(tableEl);

            for (LabelColumn col : table.getColumns()) {
                Element colEl = doc.createElement("column");
                appendProperties(doc, colEl, col.getProperties());
                tableEl.appendChild(colEl);
            }

            LabelDetail detail = table.getDetail();
            if (detail != null) {
                Element detailEl = doc.createElement("detail");
                appendProperties(doc, detailEl, detail.getProperties());
                tableEl.appendChild(detailEl);

                for (LabelRow row : detail.getRows()) {
                    Element rowEl = doc.createElement("row");
                    rowEl.setAttribute("id", row.getId());
                    appendProperties(doc, rowEl, row.getProperties());
                    detailEl.appendChild(rowEl);

                    for (LabelCell cell : row.getCells()) {
                        Element cellEl = doc.createElement("cell");
                        cellEl.setAttribute("id", cell.getId());
                        appendProperties(doc, cellEl, cell.getProperties());
                        rowEl.appendChild(cellEl);

                        CellContent content = cell.getContent();
                        if (content instanceof DataBindingContent dbc) {
                            Element dataEl = doc.createElement("data");
                            dataEl.setAttribute("id", dbc.getId());
                            appendProperties(doc, dataEl, dbc.getProperties());

                            if (dbc.getExpression() != null) {
                                Element exprEl = doc.createElement("expression");
                                exprEl.setAttribute("name", "expression");
                                exprEl.setTextContent(dbc.getExpression());
                                dataEl.appendChild(exprEl);
                            }

                            cellEl.appendChild(dataEl);
                        } else if (content instanceof NestedGridContent ngc) {
                            Element gridEl = buildGrid(doc, ngc.getGrid());
                            cellEl.appendChild(gridEl);
                        }
                    }
                }
            }
        }
    }

    private Element buildGrid(Document doc, LabelGrid grid) {
        Element gridEl = doc.createElement("grid");
        gridEl.setAttribute("id", grid.getId());
        appendProperties(doc, gridEl, grid.getProperties());

        for (LabelRow row : grid.getRows()) {
            Element rowEl = doc.createElement("row");
            rowEl.setAttribute("id", row.getId());
            appendProperties(doc, rowEl, row.getProperties());
            gridEl.appendChild(rowEl);

            for (LabelCell cell : row.getCells()) {
                Element cellEl = doc.createElement("cell");
                cellEl.setAttribute("id", cell.getId());
                appendProperties(doc, cellEl, cell.getProperties());
                rowEl.appendChild(cellEl);

                CellContent content = cell.getContent();
                if (content instanceof DataBindingContent dbc) {
                    Element dataEl = doc.createElement("data");
                    dataEl.setAttribute("id", dbc.getId());
                    appendProperties(doc, dataEl, dbc.getProperties());

                    if (dbc.getExpression() != null) {
                        Element exprEl = doc.createElement("expression");
                        exprEl.setAttribute("name", "expression");
                        exprEl.setTextContent(dbc.getExpression());
                        dataEl.appendChild(exprEl);
                    }

                    cellEl.appendChild(dataEl);
                } else if (content instanceof NestedGridContent ngc) {
                    Element nestedGrid = buildGrid(doc, ngc.getGrid());
                    cellEl.appendChild(nestedGrid);
                }
            }
        }

        return gridEl;
    }

    private void appendProperties(Document doc, Element parent, List<LabelProperty> props) {
        for (LabelProperty prop : props) {
            Element propEl = doc.createElement("property");
            propEl.setAttribute("name", prop.getName());
            propEl.setTextContent(prop.getValue());
            parent.appendChild(propEl);
        }
    }
}
