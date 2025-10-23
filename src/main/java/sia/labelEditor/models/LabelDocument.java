package sia.labelEditor.models;
import java.util.List;

public class LabelDocument {
    private LabelBody body;
    private List<LabelProperty> metadata;

    public void setBody(LabelBody body2) {
        body = body2;
    }
    public void setMetadata(List<LabelProperty> metadata){
        this.metadata = metadata;
    }

    public LabelBody getBody() {
        return body;
    }
    public List<LabelProperty> getMetadata(){
        return metadata;
    }

    public void debug() {
        System.out.println("=== LabelDocument ===");

        if (body != null && body.getTable() != null) {
            LabelTable table = body.getTable();
            System.out.println("Table[id=" + table.getId() + "] Properties: " + table.getProperties());
            if (table.getStyle() != null) {
                System.out.println("  Table Style: " + table.getStyle().getAll());
            }

            LabelDetail detail = table.getDetail();
            if (detail != null && detail.getRows() != null) {
                debugGrid(detail.getRows(), "  ");
            } else {
                System.out.println("  No detail rows found.");
            }
        } else {
            System.out.println("No table found.");
        }
    }

    private void debugGrid(List<LabelRow> rows, String indent) {
        for (LabelRow row : rows) {
            System.out.println(indent + "Row[id=" + row.getId() + ", cells=" + row.getCells().size() + "]");
            if (row.getStyle() != null) {
                System.out.println(indent + "  Row Style: " + row.getStyle().getAll());
            }

            for (LabelCell cell : row.getCells()) {
                LabelStyle style = cell.getStyle();
                String width = style != null && style.getWidth() != null ? style.getWidth().getRaw() : "null";
                String height = style != null && style.getHeight() != null ? style.getHeight().getRaw() : "null";

                System.out.print(indent + "  Cell[id=" + cell.getId() +
                                ", x=" + cell.getX() + ", y=" + cell.getY() +
                                ", w=" + width + ", h=" + height + "] → ");

                CellContent content = cell.getContent();
                if (content instanceof DataBindingContent dbc) {
                    System.out.println("DataBinding[id=" + dbc.getId() +
                                    ", field=" + dbc.getDataField() +
                                    ", expr=" + dbc.getExpression() + "]");
                    if (dbc.getStyle() != null) {
                        System.out.println(indent + "    DataBinding Style: " + dbc.getStyle().getAll());
                    }
                } else if (content instanceof NestedGridContent ngc) {
                    LabelGrid grid = ngc.getGrid();
                    System.out.println("NestedGrid[id=" + grid.getId() + ", rows=" + grid.getRows().size() + "]");
                    if (grid.getStyle() != null) {
                        System.out.println(indent + "    Grid Style: " + grid.getStyle().getAll());
                    }
                    debugGrid(grid.getRows(), indent + "    ");
                } else if (content != null) {
                    System.out.println(content.getType());
                } else {
                    System.out.println("null");
                }

                if (style != null) {
                    System.out.println(indent + "    Cell Style: " + style.getAll());
                }
            }
        }
    }
}
