package sia.labelEditor.models;
import java.util.List;

public class LabelColumn {
    private List<LabelProperty> properties;
    private LabelStyle style;

    public void setProperties(List<LabelProperty> properties2) {
        properties = properties2;
    }
    public void setStyle(LabelStyle style) { 
        this.style = style; 
    }

    public List<LabelProperty> getProperties(){
        return properties;
    }
    public LabelStyle getStyle() {
        return style; 
    }
}