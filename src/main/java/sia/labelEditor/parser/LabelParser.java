package sia.labelEditor.parser;

import sia.labelEditor.models.*;
import org.w3c.dom.*;
import java.util.*;

public class LabelParser {

    public LabelDocument parse(Document doc) {
        LabelDocument label = new LabelDocument();
        label.setBody(parseBody(doc));
        return label;
    }

    private LabelBody parseBody(Document doc) {
        NodeList bodyNodes = doc.getElementsByTagName("body");
        if (bodyNodes.getLength() == 0) return null;

        Element body = (Element) bodyNodes.item(0);
        LabelBody labelBody = new LabelBody();
        List<LabelProperty> props = parseProperties(body);
        labelBody.setProperties(props);
        labelBody.setStyle(new LabelStyle(props));
        labelBody.setTable(parseTable(body));
        return labelBody;
    }

    private LabelTable parseTable(Element body) {
        NodeList tables = body.getElementsByTagName("table");
        if (tables.getLength() == 0) return null;

        Element table = (Element) tables.item(0);
        LabelTable labelTable = new LabelTable();
        labelTable.setId(table.getAttribute("id"));
        List<LabelProperty> props = parseProperties(table);
        labelTable.setProperties(props);
        labelTable.setStyle(new LabelStyle(props));
        labelTable.setColumns(parseColumns(table));
        labelTable.setDetail(parseDetail(table));
        return labelTable;
    }

    private List<LabelColumn> parseColumns(Element table) {
        List<LabelColumn> columns = new ArrayList<>();
        NodeList columnNodes = table.getElementsByTagName("column");
        for (int i = 0; i < columnNodes.getLength(); i++) {
            Element col = (Element) columnNodes.item(i);
            LabelColumn column = new LabelColumn();
            List<LabelProperty> props = parseProperties(col);
            column.setProperties(props);
            column.setStyle(new LabelStyle(props));
            columns.add(column);
        }
        return columns;
    }

    private LabelDetail parseDetail(Element table) {
        NodeList detailNodes = table.getElementsByTagName("detail");
        if (detailNodes.getLength() == 0) return null;

        Element detail = (Element) detailNodes.item(0);
        LabelDetail labelDetail = new LabelDetail();
        List<LabelProperty> props = parseProperties(detail);
        labelDetail.setProperties(props);
        labelDetail.setStyle(new LabelStyle(props));

        NodeList rowNodes = detail.getElementsByTagName("row");
        for (int i = 0; i < rowNodes.getLength(); i++) {
            Element row = (Element) rowNodes.item(i);
            labelDetail.getRows().add(parseRow(row));
        }

        return labelDetail;
    }

    private LabelRow parseRow(Element rowElement) {
        LabelRow row = new LabelRow();
        row.setId(rowElement.getAttribute("id"));
        List<LabelProperty> props = parseProperties(rowElement);
        row.setProperties(props);
        row.setStyle(new LabelStyle(props));

        NodeList cellNodes = rowElement.getElementsByTagName("cell");
        for (int i = 0; i < cellNodes.getLength(); i++) {
            Element cell = (Element) cellNodes.item(i);
            row.getCells().add(parseCell(cell));
        }

        return row;
    }

    private LabelCell parseCell(Element cellElement) {
        LabelCell cell = new LabelCell();
        cell.setId(cellElement.getAttribute("id"));
        List<LabelProperty> props = parseProperties(cellElement);
        cell.setProperties(props);
        cell.setStyle(new LabelStyle(props));

        for (LabelProperty prop : props) {
            switch (prop.getName()) {
                case "x" -> cell.setX(parseDimensionAsInt(prop.getValue()));
                case "y" -> cell.setY(parseDimensionAsInt(prop.getValue()));
                case "width" -> cell.setWidth(new LabelDimension(prop.getValue()));
                case "height" -> cell.setHeight(new LabelDimension(prop.getValue()));
            }
        }

        NodeList children = cellElement.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element child = (Element) node;
                List<LabelProperty> childProps = parseProperties(child);
                for (LabelProperty prop : childProps) {
                    switch (prop.getName()) {
                        case "x" -> cell.setX(parseDimensionAsInt(prop.getValue()));
                        case "y" -> cell.setY(parseDimensionAsInt(prop.getValue()));
                        case "width" -> {
                            if (cell.getWidth() == null) cell.setWidth(new LabelDimension(prop.getValue()));
                        }
                        case "height" -> {
                            if (cell.getHeight() == null) cell.setHeight(new LabelDimension(prop.getValue()));
                        }
                    }
                }
            }
        }

        NodeList gridNodes = cellElement.getElementsByTagName("grid");
        if (gridNodes.getLength() > 0) {
            Element gridElement = (Element) gridNodes.item(0);
            LabelGrid grid = parseGrid(gridElement);
            grid.setId(gridElement.getAttribute("id"));
            cell.setContent(new NestedGridContent(grid));
        } else {
            NodeList dataNodes = cellElement.getElementsByTagName("data");
            if (dataNodes.getLength() > 0) {
                Element data = (Element) dataNodes.item(0);
                DataBindingContent content = new DataBindingContent();
                content.setId(data.getAttribute("id"));
                List<LabelProperty> dataProps = parseProperties(data);
                content.setProperties(dataProps);
                content.setStyle(new LabelStyle(dataProps));
                content.setDataField(getProperty(data, "resultSetColumn"));
                content.setExpression(getExpression(data));
                content.setDataType(getProperty(data, "dataType"));
                cell.setContent(content);
            }
        }

        return cell;
    }

    private LabelGrid parseGrid(Element gridElement) {
        LabelGrid grid = new LabelGrid();
        List<LabelProperty> props = parseProperties(gridElement);
        grid.setProperties(props);
        grid.setStyle(new LabelStyle(props));

        NodeList rowNodes = gridElement.getElementsByTagName("row");
        for (int i = 0; i < rowNodes.getLength(); i++) {
            Element row = (Element) rowNodes.item(i);
            grid.getRows().add(parseRow(row));
        }

        NodeList colNodes = gridElement.getElementsByTagName("column");
        for (int i = 0; i < colNodes.getLength(); i++) {
            Element col = (Element) colNodes.item(i);
            LabelColumn column = new LabelColumn();
            List<LabelProperty> colProps = parseProperties(col);
            column.setProperties(colProps);
            column.setStyle(new LabelStyle(colProps));
            grid.getColumns().add(column);
        }

        return grid;
    }

    private List<LabelProperty> parseProperties(Element element) {
        List<LabelProperty> props = new ArrayList<>();
        NodeList children = element.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node node = children.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE && "property".equals(node.getNodeName())) {
                Element prop = (Element) node;
                LabelProperty p = new LabelProperty();
                p.setName(prop.getAttribute("name"));
                p.setValue(prop.getTextContent().trim());
                props.add(p);
            }
        }
        return props;
    }

    private String getProperty(Element element, String name) {
        NodeList props = element.getElementsByTagName("property");
        for (int i = 0; i < props.getLength(); i++) {
            Element prop = (Element) props.item(i);
            if (name.equals(prop.getAttribute("name"))) {
                return prop.getTextContent().trim();
            }
        }
        return null;
    }

    private String getExpression(Element element) {
        NodeList exprs = element.getElementsByTagName("expression");
        for (int i = 0; i < exprs.getLength(); i++) {
            Element expr = (Element) exprs.item(i);
            String name = expr.getAttribute("name");
            if ("expression".equals(name) || "valueExpr".equals(name)) {
                return expr.getTextContent().trim();
            }
        }
        return getProperty(element, "resultSetExpression");
    }

    private int parseDimensionAsInt(String value) {
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
            } else if (value.endsWith("%")) {
                System.out.println("Failed to parse dimension: '" + value + "' — percentage not supported");
                return 0;
            } else {
                return (int)(Double.parseDouble(value)); // assume pixels
            }
        } catch (Exception e) {
            System.out.println("Failed to parse dimension: '" + value + "'");
            return 0;
        }
    }
}
