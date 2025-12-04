package dasturlash.uz.service;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.PaginationResponseDTO;
import dasturlash.uz.entity.AttachEntity;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.AttachRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.*;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AttachService {

    private final AttachRepository attachRepository;

    @Value("${attach.upload.folder}")
    private String folderName;

    @Value("${server.domain.name}")
    private String domainName;

    // --------------------------- UPLOAD ---------------------------
    public AttachDTO upload(MultipartFile file) {

        if (file.isEmpty()) {
            throw new AppBadException("File not found");
        }

        try {
            String id = UUID.randomUUID().toString();
            String dateFolder = getFolderPath();
            String ext = getExtension(Objects.requireNonNull(file.getOriginalFilename()));

            File folder = new File(folderName + "/" + dateFolder);
            if (!folder.exists()) {
                folder.mkdirs();
            }

            String fileName = id + "." + ext;
            Path path = Paths.get(folderName + "/" + dateFolder + "/" + fileName);

            Files.write(path, file.getBytes());

            // Save DB
            AttachEntity entity = new AttachEntity();
            entity.setId(id);
            entity.setFileName(fileName);
            entity.setPath(dateFolder);
            entity.setSize(file.getSize());
            entity.setOriginalName(file.getOriginalFilename());
            entity.setExtension(ext);
            entity.setVisible(true);

            AttachEntity saved = attachRepository.save(entity);

            return toDTO(saved);

        } catch (IOException e) {
            throw new AppBadException("Error while uploading file");
        }
    }

    // --------------------------- OPEN ---------------------------
    public ResponseEntity<Resource> open(String id) {
        AttachEntity entity = get(id);

        Path filePath = Paths.get(getFullPath(entity));

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new AppBadException("File not found");
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(resource);

        } catch (IOException e) {
            throw new AppBadException("File open error");
        }
    }

    // --------------------------- DOWNLOAD ---------------------------
    public ResponseEntity<Resource> download(String id) {
        AttachEntity entity = get(id);
        Path filePath = Paths.get(getFullPath(entity));

        try {
            Resource resource = new UrlResource(filePath.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + entity.getOriginalName() + "\"")
                    .body(resource);

        } catch (MalformedURLException e) {
            throw new AppBadException("Download error");
        }
    }

    // --------------------------- PAGINATION ---------------------------
    public PaginationResponseDTO<AttachDTO> pagination(int page, int size) {
        Page<AttachEntity> entityPage = attachRepository.findAll(
                PageRequest.of(page, size, Sort.by("createdDate").descending())
        );

        List<AttachDTO> dtoList = entityPage.map(this::toDTO).toList();

        return new PaginationResponseDTO<>(dtoList, entityPage.getTotalElements());
    }

    // --------------------------- DELETE ---------------------------
    @Transactional
    public String delete(String id) {
        AttachEntity entity = get(id);

        try {
            Files.deleteIfExists(Paths.get(getFullPath(entity)));
            attachRepository.delete(entity);

            return "Deleted";

        } catch (IOException e) {
            throw new AppBadException("Delete error");
        }
    }

    // --------------------------- HELPERS ---------------------------
    private AttachEntity get(String id) {
        return attachRepository.findById(id)
                .orElseThrow(() -> new AppBadException("Attach not found"));
    }

    private String getFullPath(AttachEntity entity) {
        return folderName + "/" + entity.getPath() + "/" + entity.getFileName();
    }

    private String getFolderPath() {
        LocalDateTime now = LocalDateTime.now();
        return now.getYear() + "/" + now.getMonthValue() + "/" + now.getDayOfMonth();
    }

    private String getExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf('.') + 1);
    }

    private AttachDTO toDTO(AttachEntity entity) {
        AttachDTO dto = new AttachDTO();
        dto.setId(entity.getId());
        dto.setOriginName(entity.getOriginalName());
        dto.setSize(entity.getSize());
        dto.setExtension(entity.getExtension());
        dto.setCreatedDate(entity.getCreatedDate());
        dto.setUrl(domainName + "/api/v1/attach/open/" + entity.getId());
        return dto;
    }
}
