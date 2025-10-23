package sia.labelEditor.controllers;

import sia.labelEditor.models.LabelRequest;
import sia.labelEditor.services.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.*;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import sia.labelEditor.models.LabelCell;
import sia.labelEditor.models.LabelDocument;

@Controller
public class HomeController {

    @Autowired
    private LabelService labelService;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @PostMapping("/create-label")
    public ResponseEntity<String> updateLabel(@RequestBody LabelRequest request) {
        labelService.updateLabelDimensions(request.widthInInches, request.heightInInches);
        return ResponseEntity.ok("Label updated");
    }

    @GetMapping("/download-label")
    public ResponseEntity<Resource> downloadLabel() throws Exception {
        ByteArrayOutputStream out = labelService.getLabelStream();
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        InputStreamResource resource = new InputStreamResource(in);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=label.rptdesign")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .contentLength(out.size())
            .body(resource);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadLabel(@RequestParam("file") MultipartFile file) {
        try {
            labelService.loadLabel(file);
            return ResponseEntity.ok("Label parsed successfully");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to parse label");
        }
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile() throws IOException {
        File file = labelService.getLabelFile();
        InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=edited_label.rptdesign")
            .contentType(MediaType.APPLICATION_OCTET_STREAM)
            .body(resource);
    }

    @GetMapping("/label/debug")
    @ResponseBody
    public String debugLabel() {
        LabelDocument doc = labelService.getLabelDocument();
        if (doc == null) return "No label loaded.";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(out);
        System.setOut(ps);
        doc.debug();
        System.setOut(System.out);
        return out.toString();
    }

    @GetMapping("/label-cells")
    @ResponseBody
    public List<LabelCell> getLabelCells() {
        LabelDocument doc = labelService.getLabelDocument();
        if (doc == null || doc.getBody() == null) return List.of();
        return doc.getBody().getTable().getDetail().getRows().stream()
            .flatMap(row -> row.getCells().stream())
            .collect(Collectors.toList());
    }

    @GetMapping("/label-size")
    @ResponseBody
    public Map<String, Integer> getLabelSize() {
        return labelService.getLabelSize();
    }

    @RestController
    @RequestMapping("/label")
    public class LabelController {

        @Autowired
        private LabelService labelService;

        @PostMapping("/reset")
        public ResponseEntity<Void> resetLabel() {
            labelService.reset();
            return ResponseEntity.ok().build();
        }
    }

    @GetMapping("/label-structure")
    public ResponseEntity<Map<String, Object>> getLabelStructure() {
        return ResponseEntity.ok(labelService.getLabelStructure());
    }
}
