# Label Editor Core

A modular Java library for parsing, inspecting, and manipulating BIRT `.rptdesign` label layouts. Designed for clarity, editability, and future rendering.

## ✨ Features

- Parses BIRT-style XML into structured Java objects
- Preserves layout fidelity with unit-aware dimensions (`in`, `pt`, `cm`, `mm`, `%`)
- Centralized style model for easy access to padding, alignment, font size, etc.
- Supports nested grids, data bindings, and extensible content types
- Debug-friendly output for visual inspection and testing

## 📦 Package Structure

- `sia.labelEditor.models`: Core data structures (LabelDocument, LabelCell, LabelStyle, etc.)
- `sia.labelEditor.parser`: XML parser that builds the model from `.rptdesign` files

## 📚 Example Output

```
=== LabelDocument ===
Table[id=1322] Properties: [dataSet=Items]
Row[id=1332, cells=12]
  Cell[id=1333, x=0, y=0, w=2.39in, h=0.91in] → NestedGrid[id=1383, rows=2]
    Cell Style: {paddingTop=0pt, textAlign=center, fontSize=8pt}
```

## 💡 Roadmap

- [ ] Style inheritance and cascading
- [ ] Layout rendering engine
- [ ] Interactive label editor UI
- [ ] Export back to `.rptdesign`
