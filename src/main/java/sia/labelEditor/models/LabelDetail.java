package sia.labelEditor.models;
import java.util.List;
import java.util.ArrayList;

public class LabelDetail {
    private List<LabelGrid> grids = new ArrayList<>();
    private List<LabelRow> rows = new ArrayList<>();
    private List<LabelProperty> properties;
    private LabelStyle style;

    public void setGrids(List<LabelGrid> grids){
        this.grids = grids;
    }
    public void setRows(List<LabelRow> rows){
        this.rows = rows;
    }
    public void setProperties(List<LabelProperty> properties2) {
        properties = properties2;
    }
    public void setStyle(LabelStyle style) { 
        this.style = style; 
    }

    public List<LabelGrid> getGrids() {
        return grids;
    }
    public List<LabelRow> getRows() {
        return rows;
    }
    public List<LabelProperty> getProperties(){
        return properties;
    }
    public LabelStyle getStyle() { 
        return style; 
    }
}