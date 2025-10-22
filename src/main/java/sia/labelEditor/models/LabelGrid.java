package sia.labelEditor.models;
import java.util.ArrayList;
import java.util.List;

public class LabelGrid {
    private List<LabelRow> rows = new ArrayList<>();
    private List<LabelColumn> columns = new ArrayList<>();
    private List<LabelProperty> properties;
    private String id;
    private LabelStyle style;

    public void setColumns(List<LabelColumn> columns) {
        this.columns = columns;
    }
    public void setRows(List<LabelRow> rows) {
        this.rows = rows;
    }
    public void setProperties(List<LabelProperty> properties2) {
        properties = properties2;
    }
    public void setId(String id) { 
        this.id = id; 
    }
    public void setStyle(LabelStyle style) { 
        this.style = style; 
    }

    public List<LabelColumn> getColumns() {
        return columns;
    }
    public List<LabelRow> getRows() {
        return rows;
    }
    public List<LabelProperty> getProperties(){
        return properties;
    }
    public String getId() { 
        return id; 
    }
    public LabelStyle getStyle() { 
        return style; 
    }
}