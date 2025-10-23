package sia.labelEditor.models;

public class LabelElement {
    public String type;
    public String content;
    public int x, y;

    public LabelElement(String type, String content, int x, int y, int width, int height) {
        this.type = type;
        this.content = content;
        this.x = x;
        this.y = y;
    }
}
