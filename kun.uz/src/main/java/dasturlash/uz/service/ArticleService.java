package dasturlash.uz.service;

import dasturlash.uz.dto.article.*;
import dasturlash.uz.entity.*;
import dasturlash.uz.enums.AppLanguageEnum;
import dasturlash.uz.enums.ArticleStatusEnum;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleService {

    private final ArticleRepository articleRepository;
    private final CategoryRepository categoryRepository;
    private final SectionRepository sectionRepository;
    private final AttachRepository attachRepository;
    private final RegionRepository regionRepository;

    // --------------------------- CREATE ---------------------------
    @Transactional
    public ArticleInfoDTO createArticle(ArticleCreateDTO dto, ProfileEntity moderator) {

        ArticleEntity entity = new ArticleEntity();
        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());
        entity.setModerator(moderator);
        entity.setStatus(ArticleStatusEnum.NOT_PUBLISHED);
        entity.setVisible(true);
        // CreatedDate @CreationTimestamp bilan avtomatik qo‘yiladi

        if (dto.getImageId() != null) {
            entity.setImage(attachRepository.findById(String.valueOf(dto.getImageId()))
                    .orElseThrow(() -> new AppBadException("Image not found")));
        }

        if (dto.getRegionId() != null) {
            entity.setRegion(regionRepository.findById(dto.getRegionId())
                    .orElseThrow(() -> new AppBadException("Region not found")));
        }

        entity.setCategories(dto.getCategoryList() != null ?
                new HashSet<>(categoryRepository.findAllById(dto.getCategoryList())) :
                new HashSet<>());

        entity.setSections(dto.getSectionList() != null ?
                new HashSet<>(sectionRepository.findAllById(dto.getSectionList())) :
                new HashSet<>());

        ArticleEntity saved = articleRepository.save(entity);
        return toArticleInfoDTO(saved, AppLanguageEnum.UZ); // default language
    }

    // --------------------------- UPDATE ---------------------------
    @Transactional
    public ArticleInfoDTO updateArticle(Integer id, ArticleUpdateDTO dto, ProfileEntity moderator) {

        ArticleEntity entity = articleRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Article not found"));

        entity.setTitle(dto.getTitle());
        entity.setDescription(dto.getDescription());
        entity.setContent(dto.getContent());
        entity.setModerator(moderator);
        entity.setStatus(ArticleStatusEnum.NOT_PUBLISHED);

        if (dto.getImageId() != null) {
            entity.setImage(attachRepository.findById(String.valueOf(dto.getImageId()))
                    .orElseThrow(() -> new AppBadException("Image not found")));
        }

        if (dto.getRegionId() != null) {
            entity.setRegion(regionRepository.findById(dto.getRegionId())
                    .orElseThrow(() -> new AppBadException("Region not found")));
        }

        entity.setCategories(dto.getCategoryList() != null ?
                new HashSet<>(categoryRepository.findAllById(dto.getCategoryList())) :
                new HashSet<>());

        entity.setSections(dto.getSectionList() != null ?
                new HashSet<>(sectionRepository.findAllById(dto.getSectionList())) :
                new HashSet<>());

        ArticleEntity saved = articleRepository.save(entity);
        return toArticleInfoDTO(saved, AppLanguageEnum.UZ); // default language
    }

    // --------------------------- DELETE ---------------------------
    @Transactional
    public String deleteArticle(Integer id) {
        ArticleEntity entity = articleRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Article not found"));
        entity.setVisible(false);
        articleRepository.save(entity);
        return "Article deleted";
    }

    // --------------------------- CHANGE STATUS ---------------------------
    @Transactional
    public String changeStatus(Integer id, ArticleStatusEnum status, ProfileEntity publisher) {
        ArticleEntity entity = articleRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Article not found"));
        entity.setStatus(status);
        entity.setPublisher(publisher);
        if (status == ArticleStatusEnum.PUBLISHED) {
            entity.setPublishedDate(LocalDateTime.now());
        }
        articleRepository.save(entity);
        return "Status changed to " + status;
    }

    // --------------------------- DTO MAPPER ---------------------------
    public ArticleInfoDTO toArticleInfoDTO(ArticleEntity entity, AppLanguageEnum lang) {
        ArticleInfoDTO dto = new ArticleInfoDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setContent(entity.getContent());
        dto.setSharedCount(entity.getSharedCount());
        dto.setViewCount(entity.getViewCount());
        dto.setPublishedDate(entity.getPublishedDate());

        if (entity.getRegion() != null) {
            dto.setRegionKey(entity.getRegion().getRegionKey());
            switch (lang) {
                case UZ -> dto.setRegionName(entity.getRegion().getNameUz());
                case RU -> dto.setRegionName(entity.getRegion().getNameRu());
                case EN -> dto.setRegionName(entity.getRegion().getNameEn());
                case KRILL -> dto.setRegionName(entity.getRegion().getNameKr());
            }
        }

        dto.setCategoryNames(entity.getCategories() != null ?
                entity.getCategories().stream()
                        .map(cat -> switch (lang) {
                            case UZ -> cat.getNameUz();
                            case RU -> cat.getNameRu();
                            case EN -> cat.getNameEn();
                            case KRILL -> cat.getNameKr();
                        })
                        .collect(Collectors.toList()) :
                new ArrayList<>());

        dto.setSectionNames(entity.getSections() != null ?
                entity.getSections().stream()
                        .map(sec -> switch (lang) {
                            case UZ -> sec.getNameUz();
                            case RU -> sec.getNameRu();
                            case EN -> sec.getNameEn();
                            case KRILL -> sec.getNameKr();
                        })
                        .collect(Collectors.toList()) :
                new ArrayList<>());

        Optional.ofNullable(entity.getImage()).ifPresent(img -> {
            dto.setImageId(Integer.valueOf(img.getId()));
            dto.setImageUrl("/api/v1/attach/open/" + img.getId());
        });

        return dto;
    }

    // ====================== HELPERS ======================

    private ArticleShortInfoDTO toShortInfoDTO(ArticleEntity entity) {
        ArticleShortInfoDTO dto = new ArticleShortInfoDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setPublishedDate(entity.getPublishedDate());
        if (entity.getImage() != null) {
            dto.setImageUrl("/api/v1/attach/open/" + entity.getImage().getId());
        }
        return dto;
    }

    private ArticleFullInfoDTO toFullInfoDTO(ArticleEntity entity, String lang) {
        ArticleFullInfoDTO dto = new ArticleFullInfoDTO();
        dto.setId(entity.getId());
        dto.setTitle(entity.getTitle());
        dto.setDescription(entity.getDescription());
        dto.setContent(entity.getContent());
        dto.setSharedCount(entity.getSharedCount());
        dto.setViewCount(entity.getViewCount());
        dto.setPublishedDate(entity.getPublishedDate());
        if (entity.getImage() != null) {
            dto.setImageUrl("/api/v1/attach/open/" + entity.getImage().getId());
        }

        if (entity.getRegion() != null) {
            ArticleFullInfoDTO.RegionInfo region = new ArticleFullInfoDTO.RegionInfo();
            region.setKey(entity.getRegion().getRegionKey());
            switch (lang.toUpperCase()) {
                case "UZ" -> region.setName(entity.getRegion().getNameUz());
                case "RU" -> region.setName(entity.getRegion().getNameRu());
                case "EN" -> region.setName(entity.getRegion().getNameEn());
                case "KR" -> region.setName(entity.getRegion().getNameKr());
                default -> region.setName(entity.getRegion().getNameUz());
            }
            dto.setRegion(region);
        }

        dto.setCategoryList(entity.getCategories().stream().map(cat -> {
            ArticleFullInfoDTO.CategoryInfo c = new ArticleFullInfoDTO.CategoryInfo();
            c.setKey(cat.getCategoryKey());
            c.setName(switch (lang.toUpperCase()) {
                case "UZ" -> cat.getNameUz();
                case "RU" -> cat.getNameRu();
                case "EN" -> cat.getNameEn();
                case "KR" -> cat.getNameKr();
                default -> cat.getNameUz();
            });
            return c;
        }).collect(Collectors.toList()));

        dto.setSectionList(entity.getSections().stream().map(sec -> {
            ArticleFullInfoDTO.SectionInfo s = new ArticleFullInfoDTO.SectionInfo();
            s.setId(sec.getSectionId());
            s.setName(switch (lang.toUpperCase()) {
                case "UZ" -> sec.getNameUz();
                case "RU" -> sec.getNameRu();
                case "EN" -> sec.getNameEn();
                default -> sec.getNameUz();
            });
            return s;
        }).collect(Collectors.toList()));

        // Tag list bo'sh hozir, keyin qo'shish mumkin
        dto.setTagList(new ArrayList<>());

        return dto;
    }

    // ====================== LAST N ARTICLES ======================

    public List<ArticleShortInfoDTO> getLastNBySection(Integer sectionId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdDate").descending());
        List<ArticleEntity> articles = articleRepository.findAllBySections_SectionIdAndStatusAndVisibleTrue(sectionId, ArticleStatusEnum.PUBLISHED, pageable);
        return articles.stream().map(this::toShortInfoDTO).collect(Collectors.toList());
    }

    public List<ArticleShortInfoDTO> getLast12Except(List<Integer> excludeIds) {
        Pageable pageable = PageRequest.of(0, 12, Sort.by("createdDate").descending());
        List<ArticleEntity> articles = articleRepository.findAllByStatusAndVisibleTrueAndIdNotIn(ArticleStatusEnum.PUBLISHED, excludeIds, pageable);
        return articles.stream().map(this::toShortInfoDTO).collect(Collectors.toList());
    }

    public List<ArticleShortInfoDTO> getLastNByCategory(Integer categoryId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdDate").descending());
        List<ArticleEntity> articles = articleRepository.findAllByCategories_CategoryIdAndStatusAndVisibleTrue(categoryId, ArticleStatusEnum.PUBLISHED, pageable);
        return articles.stream().map(this::toShortInfoDTO).collect(Collectors.toList());
    }

    public List<ArticleFullInfoDTO> getLastNByRegion(Integer regionId, int size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by("createdDate").descending());
        List<ArticleEntity> articles = articleRepository.findAllByRegion_IdAndStatusAndVisibleTrue(regionId, ArticleStatusEnum.PUBLISHED, pageable);
        return articles.stream().map(a -> toFullInfoDTO(a, "UZ")).collect(Collectors.toList());
    }

    public ArticleFullInfoDTO getArticleByIdAndLang(Integer id, String lang) {
        ArticleEntity entity = articleRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new AppBadException("Article not found"));
        return toFullInfoDTO(entity, lang);
    }

    // ====================== INCREASE VIEW/SHARE ======================

    @Transactional
    public Integer increaseViewCount(Integer articleId) {
        ArticleEntity entity = articleRepository.findById(articleId)
                .orElseThrow(() -> new AppBadException("Article not found"));
        entity.setViewCount(entity.getViewCount() + 1);
        return articleRepository.save(entity).getViewCount();
    }

    @Transactional
    public Integer increaseShareCount(Integer articleId) {
        ArticleEntity entity = articleRepository.findById(articleId)
                .orElseThrow(() -> new AppBadException("Article not found"));
        entity.setSharedCount(entity.getSharedCount() + 1);
        return articleRepository.save(entity).getSharedCount();
    }

    // ====================== FILTER METHODS ======================

    public List<ArticleShortInfoDTO> filterPublisher(ArticleFilterDTO filter) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdDate").descending());
        List<ArticleEntity> articles = articleRepository.filterPublisher(filter, pageable);
        return articles.stream().map(this::toShortInfoDTO).collect(Collectors.toList());
    }

    public List<ArticleShortInfoDTO> filterModerator(ArticleFilterDTO filter, ProfileEntity moderator) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdDate").descending());
        List<ArticleEntity> articles = articleRepository.filterModerator(filter, moderator.getId(), pageable);
        return articles.stream().map(this::toShortInfoDTO).collect(Collectors.toList());
    }

    public List<ArticleShortInfoDTO> filterAdmin(ArticleFilterDTO filter) {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdDate").descending());
        List<ArticleEntity> articles = articleRepository.filterAdmin(filter, pageable);
        return articles.stream().map(this::toShortInfoDTO).collect(Collectors.toList());
    }


}
