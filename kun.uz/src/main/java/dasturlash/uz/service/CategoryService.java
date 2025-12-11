package dasturlash.uz.service;

import dasturlash.uz.dto.CategoryDTO;
import dasturlash.uz.entity.CategoryEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.CategoryRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    private CategoryDTO toDto(CategoryEntity entity) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(entity.getCategoryId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setNameUz(entity.getNameUz());
        dto.setNameRu(entity.getNameRu());
        dto.setNameEn(entity.getNameEn());
        dto.setNameKr(entity.getNameKr());
        dto.setCategoryKey(entity.getCategoryKey());
        dto.setVisible(entity.getVisible());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    private CategoryDTO toLangDto(CategoryEntity entity, AppLanguageEnum lang) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryId(entity.getCategoryId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setCategoryKey(entity.getCategoryKey());

        switch (lang) {
            case UZ -> dto.setName(entity.getNameUz());
            case RU -> dto.setName(entity.getNameRu());
            case EN -> dto.setName(entity.getNameEn());
            case KRILL -> dto.setName(entity.getNameKr());
        }

        return dto;
    }
// ----------------------------------------------------------------------------------
    public CategoryDTO create(CategoryDTO dto) {
        Optional<CategoryEntity> optional = categoryRepository.findByCategoryKey(dto.getCategoryKey());
        if (optional.isPresent()) {
            throw new AppBadException("Category key already exists");
        }

        CategoryEntity entity = new CategoryEntity();
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setNameEn(dto.getNameEn());
        entity.setNameKr(dto.getNameKr());
        entity.setCategoryKey(dto.getCategoryKey());

        categoryRepository.save(entity);

        return toDto(entity);
    }

    // ----------------------------------------------------------------------------------
    @Transactional
    public CategoryDTO update(Integer id, CategoryDTO dto) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Category not found"));

        Optional<CategoryEntity> keyOptional = categoryRepository.findByCategoryKey(dto.getCategoryKey());
        if (keyOptional.isPresent() && !keyOptional.get().getCategoryId().equals(id)) {
            throw new AppBadException("Category key already exists");
        }

        entity.setOrderNumber(dto.getOrderNumber());
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setNameEn(dto.getNameEn());
        entity.setNameKr(dto.getNameKr());
        entity.setCategoryKey(dto.getCategoryKey());

        categoryRepository.save(entity);

        return toDto(entity);
    }

    // ----------------------------------------------------------------------------------
    public Boolean delete(Integer id) {
        return categoryRepository.updateVisibleById(id, false) == 1;
    }

    public List<CategoryDTO> getAll() {
        List<CategoryDTO> dtos = new LinkedList<>();
        categoryRepository.findAllByVisibleIsTrueOrderByOrderNumber().forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }

    public List<CategoryDTO> getAllByLang(AppLanguageEnum lang) {
        List<CategoryDTO> dtos = new LinkedList<>();
        categoryRepository.findAllByVisibleIsTrueOrderByOrderNumber().forEach(entity -> dtos.add(toLangDto(entity, lang)));
        return dtos;
    }
}

