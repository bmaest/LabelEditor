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

            LabelDetail detail = table.getDetail();
            if (detail != null && detail.getRows() != null) {
                for (LabelRow row : detail.getRows()) {
                    System.out.println("Row[id=" + row.getId() + ", cells=" + row.getCells().size() + "]");
                    if (row.getStyle() != null) {
                        System.out.println("  Row Style: " + row.getStyle().getAll());
                    }

                    for (LabelCell cell : row.getCells()) {
                        System.out.print("  Cell[id=" + cell.getId() +
                                        ", x=" + cell.getX() + ", y=" + cell.getY() +
                                        ", w=" + (cell.getWidth() != null ? cell.getWidth().getRaw() : "null") +
                                        ", h=" + (cell.getHeight() != null ? cell.getHeight().getRaw() : "null") + "] → ");

                        CellContent content = cell.getContent();
                        if (content instanceof DataBindingContent dbc) {
                            System.out.println("DataBinding[id=" + dbc.getId() +
                                            ", field=" + dbc.getDataField() +
                                            ", expr=" + dbc.getExpression() + "]");
                            if (dbc.getStyle() != null) {
                                System.out.println("    DataBinding Style: " + dbc.getStyle().getAll());
                            }
                        } else if (content instanceof NestedGridContent ngc) {
                            LabelGrid grid = ngc.getGrid();
                            System.out.println("NestedGrid[id=" + grid.getId() +
                                            ", rows=" + grid.getRows().size() + "]");
                            if (grid.getStyle() != null) {
                                System.out.println("    Grid Style: " + grid.getStyle().getAll());
                            }
                        } else if (content != null) {
                            System.out.println(content.getType());
                        } else {
                            System.out.println("null");
                        }

                        if (cell.getStyle() != null) {
                            System.out.println("    Cell Style: " + cell.getStyle().getAll());
                        }
                    }
                }
            } else {
                System.out.println("No detail rows found.");
            }
        } else {
            System.out.println("No table found.");
        }
    }
}
