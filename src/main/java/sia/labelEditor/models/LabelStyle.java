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

    public boolean has(String key) {
        return raw.containsKey(key);
    }

    public String getFontSize() {
        return raw.getOrDefault("fontSize", "default");
    }

    public String getTextAlign() {
        return raw.getOrDefault("textAlign", "left");
    }

    public Map<String, String> getPadding() {
        Map<String, String> padding = new HashMap<>();
        padding.put("paddingTop", raw.getOrDefault("paddingTop", "0pt"));
        padding.put("paddingLeft", raw.getOrDefault("paddingLeft", "0pt"));
        padding.put("paddingBottom", raw.getOrDefault("paddingBottom", "0pt"));
        padding.put("paddingRight", raw.getOrDefault("paddingRight", "0pt"));
        return padding;
    }

    public Map<String, String> getAll() {
        return raw;
    }

    @Override
    public String toString() {
        return raw.toString();
    }
}
