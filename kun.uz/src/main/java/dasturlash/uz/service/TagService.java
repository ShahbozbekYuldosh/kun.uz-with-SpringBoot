package dasturlash.uz.service;

import dasturlash.uz.dto.ApiResponse;
import dasturlash.uz.dto.TagDTO;
import dasturlash.uz.entity.TagEntity;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public ApiResponse<TagDTO> create(TagDTO dto) {

        if (tagRepository.existsByName(dto.getName())) {
            throw new AppBadException("Tag already exists");
        }

        TagEntity entity = new TagEntity();
        entity.setName(dto.getName());
        tagRepository.save(entity);

        dto.setId(entity.getId());
        return ApiResponse.success(dto);
    }

    public ApiResponse<List<TagDTO>> list() {
        List<TagDTO> result = tagRepository.findAll()
                .stream()
                .map(tag -> {
                    TagDTO dto = new TagDTO();
                    dto.setId(tag.getId());
                    dto.setName(tag.getName());
                    return dto;
                })
                .toList();

        return ApiResponse.success(result);
    }
}
