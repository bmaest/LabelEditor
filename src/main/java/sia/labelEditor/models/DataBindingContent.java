package sia.labelEditor.models;

import java.util.List;

public class DataBindingContent implements CellContent {
    private String dataField; // e.g. "firstPrice"
    private String expression; // e.g. JavaScript or XPath
    private String dataType;
    private String id;
    private LabelStyle style;
    private List<LabelProperty> properties;


    @Override
    public String getType() {
        return "data";
    }
    @Override
    public String toString() {
        return "DataBinding[id=" + id + ", field=" + dataField + ", expr=" + expression + "]";
    }

    public void setDataField(String property) {
        dataField = property;
    }
	public void setDataType(String property) {
		dataType = property;
	}
    public void setExpression(String expression2) {
        expression = expression2;
    }
    public void setId(String id) { 
        this.id = id; 
    }
    public void setStyle(LabelStyle style) { 
        this.style = style; 
    }
    public void setProperties(List<LabelProperty> props) {
        this.properties = props;
        this.style = new LabelStyle(props); // keep style in sync
    }

    public String getDataField() {
       return dataField;
    }
	public String getDataType() {
		return dataType;
	}
    public String getExpression() {
        return expression;
    }
    public String getId() { 
        return id; 
    }
    public LabelStyle getStyle() { 
        return style; 
    }
    public List<LabelProperty> getProperties() {
        return properties;

    }
}