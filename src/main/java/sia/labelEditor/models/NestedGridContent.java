package sia.labelEditor.models;

public class NestedGridContent implements CellContent {
    private final LabelGrid grid;

    @Override
    public String getType() {
        return "grid";
    }
    @Override
    public String toString() {
        return "NestedGrid[id=" + (grid != null ? grid.getId() : "null") + 
            ", rows=" + (grid != null && grid.getRows() != null ? grid.getRows().size() : 0) + "]";
    }

    public NestedGridContent(LabelGrid grid) {
        this.grid = grid;
    }

    public LabelGrid getGrid() {
        return grid;
    }
}
