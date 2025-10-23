package sia.labelEditor.models;
import java.util.List;

public class LabelCell {
    private CellContent content;
    private List<LabelProperty> properties;
    private int x, y;
    private String id;
    private LabelStyle style;

    @Override
    public String toString() {
        return "Cell[id=" + id +
            ", x=" + x + ", y=" + y +
            ", content=" + content + "]";
    }

    public void setProperties(List<LabelProperty> properties2) {
        properties = properties2;
    }
    public void setContent(NestedGridContent nestedGridContent) {
        content = nestedGridContent;
    }
    public void setContent(DataBindingContent content2) {
        content = content2;
    }
    public void setX(int x){
        this.x = x;
    }
    public void setY(int y){
        this.y = y;
    }
    public void setId(String id) { 
        this.id = id; 
    }
    public void setStyle(LabelStyle style) { 
        this.style = style; 
    }

    public List<LabelProperty> getProperties(){
        return properties;
    }
    public CellContent getContent(){
        return content;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
    public String getId() { 
        return id; 
    }
    public LabelStyle getStyle() { 
        return style; 
    }
}