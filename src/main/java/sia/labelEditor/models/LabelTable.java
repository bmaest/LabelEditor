package sia.labelEditor.models;
import java.util.List;

public class LabelTable {
    private List<LabelColumn> columns;
    private LabelDetail detail;
    private List<LabelProperty> properties;
    private String id;
    private LabelStyle style;

    public void setColumns(List<LabelColumn> columns2) {
        columns = columns2;
    }
    public void setDetail(LabelDetail detail2) {
        detail = detail2;
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

    public List<LabelColumn> getColumns(){
        return columns;
    }
    public LabelDetail getDetail() {
        return detail;
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