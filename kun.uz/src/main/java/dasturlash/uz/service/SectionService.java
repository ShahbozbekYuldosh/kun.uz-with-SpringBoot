package dasturlash.uz.service;

import dasturlash.uz.dto.SectionDTO;
import dasturlash.uz.entity.SectionEntity;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SectionService {

    private final SectionRepository sectionRepository;

    private SectionDTO toDto(SectionEntity entity) {
        SectionDTO dto = new SectionDTO();
        dto.setSectionId(entity.getSectionId());
        dto.setOrderNumber(entity.getOrderNumber());
        dto.setNameUz(entity.getNameUz());
        dto.setNameRu(entity.getNameRu());
        dto.setNameEn(entity.getNameEn());
        dto.setNameKr(entity.getNameKr());
        dto.setSectionKey(entity.getSectionKey());
        dto.setVisible(entity.getVisible());
        dto.setCreatedDate(entity.getCreatedDate());
        return dto;
    }

    private SectionDTO toLangDto(SectionEntity entity, AppLanguageEnum lang) {
        SectionDTO dto = new SectionDTO();
        dto.setSectionId(entity.getSectionId());
        dto.setSectionKey(entity.getSectionKey());

        switch (lang) {
            case UZ -> dto.setName(entity.getNameUz());
            case RU -> dto.setName(entity.getNameRu());
            case EN -> dto.setName(entity.getNameEn());
            case KRILL ->  dto.setName(entity.getNameKr());
        }

        return dto;
    }

    @Transactional
    public SectionDTO create(SectionDTO dto) {
        Optional<SectionEntity> optional = sectionRepository.findBySectionKey(dto.getSectionKey());
        if (optional.isPresent()) {
            throw new AppBadException("Section key already exists");
        }

        SectionEntity entity = new SectionEntity();
        entity.setOrderNumber(dto.getOrderNumber());
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setNameEn(dto.getNameEn());
        entity.setNameKr(dto.getNameKr());
        entity.setSectionKey(dto.getSectionKey());

        sectionRepository.save(entity);

        return toDto(entity);
    }

    @Transactional
    public SectionDTO update(Integer id, SectionDTO dto) {
        SectionEntity entity = sectionRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Section not found"));

        Optional<SectionEntity> keyOptional = sectionRepository.findBySectionKey(dto.getSectionKey());
        if (keyOptional.isPresent() && !keyOptional.get().getSectionId().equals(id)) {
            throw new AppBadException("Section key already exists");
        }

        entity.setOrderNumber(dto.getOrderNumber());
        entity.setNameUz(dto.getNameUz());
        entity.setNameRu(dto.getNameRu());
        entity.setNameEn(dto.getNameEn());
        entity.setNameKr(dto.getNameKr());
        entity.setSectionKey(dto.getSectionKey());

        sectionRepository.save(entity);

        return toDto(entity);
    }

    @Transactional
    public Boolean delete(Integer id) {
        return sectionRepository.updateVisibleById(id, false) == 1;
    }

    public List<SectionDTO> getAll() {
        List<SectionDTO> dtos = new LinkedList<>();
        sectionRepository.findAllByVisibleIsTrueOrderByOrderNumber().forEach(entity -> dtos.add(toDto(entity)));
        return dtos;
    }

    public List<SectionDTO> getAllByLang(AppLanguageEnum lang) {
        List<SectionDTO> dtos = new LinkedList<>();
        sectionRepository.findAllByVisibleIsTrueOrderByOrderNumber().forEach(entity -> dtos.add(toLangDto(entity, lang)));
        return dtos;
    }
}
