package sia.labelEditor.models;

public class LabelProperty {
    private String name;
    private String value;

    public LabelProperty(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public LabelProperty() {
    }

    @Override
    public String toString() {
        return name + "=" + value;
    }

    public void setValue(String trim) {
        value = trim;
    }
    public void setName(String attribute) {
        name = attribute;
    }

    public String getValue(){
        return value;
    }
    public String getName(){
        return name;
    }
}
