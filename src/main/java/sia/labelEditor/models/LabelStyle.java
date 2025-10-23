package sia.labelEditor.models;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class LabelStyle {
    private final Map<String, String> raw = new HashMap<>();

    public LabelStyle(List<LabelProperty> props) {
        for (LabelProperty prop : props) {
            raw.put(prop.getName(), prop.getValue());
        }
    }

    public String get(String key) {
        return raw.get(key);
    }

    public LabelDimension getDimension(String key) {
        String value = raw.get(key);
        return value != null ? new LabelDimension(value) : null;
    }

    public LabelDimension getWidth() {
        return getDimension("width");
    }

    public LabelDimension getHeight() {
        return getDimension("height");
    }

    public boolean has(String key) {
        return raw.containsKey(key);
    }

    public String getFontSize() {
        return raw.getOrDefault("fontSize", "10pt");
    }

    public String getTextAlign() {
        return raw.getOrDefault("textAlign", "center");
    }

    public String getVerticalAlign() {
        return raw.getOrDefault("verticalAlign", "left");
    }

    public String getFontFamily() {
        return raw.getOrDefault("fontFamily", "sans-serif");
    }

    public String getColSpan() {
        return raw.getOrDefault("colSpan", "1");
    }

    public String getRowSpan() {
        return raw.getOrDefault("rowSpan", "1");
    }

    public String getFontWeight() {
        return raw.getOrDefault("fontWeight", "normal");
    }

    public Map<String, String> getPadding() {
        Map<String, String> padding = new HashMap<>();
        padding.put("paddingTop", raw.getOrDefault("paddingTop", "0pt"));
        padding.put("paddingLeft", raw.getOrDefault("paddingLeft", "0pt"));
        padding.put("paddingBottom", raw.getOrDefault("paddingBottom", "0pt"));
        padding.put("paddingRight", raw.getOrDefault("paddingRight", "0pt"));
        return padding;
    }

    public Map<String, String> getMargin() {
        Map<String, String> margin = new HashMap<>();
        margin.put("marginTop", raw.getOrDefault("marginTop", "0pt"));
        margin.put("marginLeft", raw.getOrDefault("marginLeft", "0pt"));
        margin.put("marginBottom", raw.getOrDefault("marginBottom", "0pt"));
        margin.put("marginRight", raw.getOrDefault("marginRight", "0pt"));
        return margin;
    }

    public Map<String, String> getAll() {
        return raw;
    }

    @Override
    public String toString() {
        return raw.toString();
    }
}
