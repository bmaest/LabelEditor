package sia.labelEditor.models;
import java.util.List;

public class LabelBody {
    private LabelTable table;
    private List<LabelProperty> properties;
    private LabelStyle style;

    public void setTable(LabelTable table2) {
        table = table2;
    }
    public void setProperties(List<LabelProperty> properties2) {
        properties = properties2;
    }
    public List<LabelProperty> setProperties() {
        return properties;
    }
    public void setStyle(LabelStyle style) { 
        this.style = style; 
    }

    public LabelTable getTable(){
        return table;
    }
    public LabelStyle getStyle() { 
        return style; 
    }
}