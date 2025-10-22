package sia.labelEditor.models;
import java.util.List;
import java.util.ArrayList;

public class LabelRow {
    private List<LabelCell> cells = new ArrayList<>();
    private List<LabelProperty> properties;
    private String id;
    private LabelStyle style;

    @Override
    public String toString() {
        return "Row[id=" + id + ", cells=" + cells.size() + "]";
    }

    public void setCells(List<LabelCell> cells){
        this.cells = cells;
    }
    public void setProperties(List<LabelProperty> properties2){
        properties = properties2;
    }
    public void setId(String id) { 
        this.id = id; 
    }
    public void setStyle(LabelStyle style) { 
        this.style = style; 
    }

    public List<LabelCell> getCells() {
        return cells;
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