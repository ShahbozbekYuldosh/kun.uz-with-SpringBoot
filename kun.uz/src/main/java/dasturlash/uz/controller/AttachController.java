package dasturlash.uz.controller;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.PaginationResponseDTO;
import dasturlash.uz.service.AttachService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/attach")
@RequiredArgsConstructor
public class AttachController {

    private final AttachService attachService;

    @PostMapping("/upload")
    public AttachDTO upload(@RequestParam("file") MultipartFile file) {
        return attachService.upload(file);
    }

    @GetMapping("/open/{id}")
    public ResponseEntity<Resource> open(@PathVariable String id) {
        return attachService.open(id);
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable String id) {
        return attachService.download(id);
    }

    @GetMapping("/adm/pagination")
    public PaginationResponseDTO<AttachDTO> pagination(@RequestParam int page,
                                                       @RequestParam int size) {
        return attachService.pagination(page, size);
    }

    @DeleteMapping("/adm/{id}")
    public String delete(@PathVariable String id) {
        return attachService.delete(id);
    }
}
