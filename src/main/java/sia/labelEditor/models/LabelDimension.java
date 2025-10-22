package sia.labelEditor.models;

public class LabelDimension {
    public enum Unit {
        INCH, PT, CM, MM, PERCENT, PIXEL, UNKNOWN
    }

    private String raw;
    private double value;
    private Unit unit;

    public LabelDimension(String raw) {
        this.raw = raw.trim().toLowerCase();
        this.unit = parseUnit(this.raw);
        this.value = parseValue(this.raw, this.unit);
    }

    private Unit parseUnit(String raw) {
        if (raw.endsWith("in")) return Unit.INCH;
        if (raw.endsWith("pt")) return Unit.PT;
        if (raw.endsWith("cm")) return Unit.CM;
        if (raw.endsWith("mm")) return Unit.MM;
        if (raw.endsWith("%")) return Unit.PERCENT;
        try {
            Double.parseDouble(raw);
            return Unit.PIXEL;
        } catch (Exception e) {
            return Unit.UNKNOWN;
        }
    }

    private double parseValue(String raw, Unit unit) {
        try {
            switch (unit) {
                case INCH: return Double.parseDouble(raw.replace("in", ""));
                case PT: return Double.parseDouble(raw.replace("pt", ""));
                case CM: return Double.parseDouble(raw.replace("cm", ""));
                case MM: return Double.parseDouble(raw.replace("mm", ""));
                case PERCENT: return Double.parseDouble(raw.replace("%", ""));
                case PIXEL: return Double.parseDouble(raw);
                default: return 0;
            }
        } catch (Exception e) {
            return 0;
        }
    }

    public String getRaw() { return raw; }
    public double getValue() { return value; }
    public Unit getUnit() { return unit; }

    public boolean isRelative() {
        return unit == Unit.PERCENT;
    }

    @Override
    public String toString() {
        return raw + " (" + unit + ", " + value + ")";
    }
}

