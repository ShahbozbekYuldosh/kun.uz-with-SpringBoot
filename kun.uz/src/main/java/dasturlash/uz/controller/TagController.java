package dasturlash.uz.controller;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.dto.TagDTO;
import dasturlash.uz.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    //----------------- ANY: Tag yaratish --------------------
    @PostMapping("/create")
    public ApiResponse<TagDTO> create(@RequestBody TagDTO dto) {
        return tagService.create(dto);
    }

    //---------------- ADMIN: Tag ro'yxati -------------------
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ApiResponse<?> list() {
        return tagService.list();
    }
}
