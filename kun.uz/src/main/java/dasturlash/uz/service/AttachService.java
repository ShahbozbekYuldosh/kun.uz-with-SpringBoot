package dasturlash.uz.service;

import dasturlash.uz.dto.AttachDTO;
import dasturlash.uz.dto.PaginationResponseDTO;
import dasturlash.uz.entity.AttachEntity;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.AttachRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AttachService {

    @Autowired
    private AttachRepository attachRepository;

    @Value("${attach.upload.folder}")
    private String folderName;
    @Value("${attach.file.access.prefix}")
    private String attachFileAccessPrefix;
    @Value("${server.domain.name}")
    private String domainName;

// --- 1. UPLOAD (ANY) -------------------------------------------------------------------------------------------------

    public AttachDTO upload(MultipartFile file) {
        if (file.isEmpty()) {
            throw new AppBadException("File not found");
        }

        try {
            String pathFolder = getYmDString(); // 2024/11/05
            String key = UUID.randomUUID().toString();
            String extension = getExtension(Objects.requireNonNull(file.getOriginalFilename()));

            // Papka yaratish
            File folder = new File(folderName + "/" + pathFolder);
            if (!folder.exists()) {
                if (!folder.mkdirs()) {
                    throw new IOException("Failed to create directories: " + folder.getAbsolutePath());
                }
            }

            // Fayl tizimiga saqlash
            String fileName = key + "." + extension;
            Path path = Paths.get(folderName + "/" + pathFolder + "/" + fileName);
            Files.write(path, file.getBytes());

            // DBga saqlash
            AttachEntity entity = new AttachEntity();
            entity.setFileName(fileName);
            entity.setPath(pathFolder);
            entity.setSize(file.getSize());
            entity.setOriginalName(file.getOriginalFilename());
            entity.setExtension(extension);
            entity.setVisible(true);
            attachRepository.save(entity);

            return toDTO(entity);
        } catch (IOException e) {
            throw new AppBadException("File upload failed: " + e.getMessage());
        }
    }

// --- 2. OPEN (BY ID) -------------------------------------------------------------------------------------------------

    public ResponseEntity<Resource> open(String attachId) {
        AttachEntity entity = get(attachId);
        String filePathString = getPath(entity);

        Path filePath = Paths.get(filePathString);
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new AppBadException("File not found on system: " + attachId);
            }

            String contentType = Files.probeContentType(filePath);
            if (contentType == null) {
                contentType = "application/octet-stream";
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .contentLength(resource.contentLength())
                    .body(resource);
        } catch (IOException e) {
            throw new AppBadException("Error opening file: " + e.getMessage());
        }
    }

// --- 3. DOWNLOAD (BY ID WITH ORIGIN NAME) -----------------------------------------------------------------------------

    public ResponseEntity<Resource> download(String attachId) {
        AttachEntity entity = get(attachId);
        String filePathString = getPath(entity);
        Path filePath = Paths.get(filePathString);

        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new AppBadException("File not found on system: " + attachId);
            }

            // Faylni yuklab olishga majburlovchi header
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("application/octet-stream"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + entity.getFileName() + "\"")
                    .body(resource);
        } catch (MalformedURLException e) {
            throw new AppBadException("Error preparing file for download: " + e.getMessage());
        }
    }

// --- 4. PAGINATION (ADMIN) -------------------------------------------------------------------------------------------

    public PaginationResponseDTO<AttachDTO> getPagination(int page, int size) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdDate");
        Pageable pageable = PageRequest.of(page, size, sort);

        // Agar faqat visible=true larni olmoqchi bo'lsangiz, Repositoryda usul bo'lishi kerak.
        Page<AttachEntity> entityPage = attachRepository.findAll(pageable);

        List<AttachDTO> dtoList = entityPage.getContent().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return new PaginationResponseDTO<>(
                dtoList,
                entityPage.getTotalElements()
        );
    }

// --- 5. DELETE (ADMIN) -----------------------------------------------------------------------------------------------

    @Transactional
    public String delete(String attachId) {
        AttachEntity entity = get(attachId);
        String filePathString = getPath(entity);

        try {
            // 1. Faylni fayl tizimidan o'chirish
            Files.deleteIfExists(Paths.get(filePathString));

            // 2. DB'dan o'chirish
            attachRepository.delete(entity);

            return "File deleted successfully: " + attachId;

        } catch (IOException e) {
            // Agar fayl tizimidan o'chirishda xato bo'lsa
            throw new AppBadException("Failed to delete file from system: " + e.getMessage());
        }
    }

// --- HELPER METHODS --------------------------------------------------------------------------------------------------

    // AttachEntity'ni ID bo'yicha topuvchi yordamchi metod
    private AttachEntity get(String attachId) {
        return attachRepository.findById(attachId)
                .orElseThrow(() -> new AppBadException("Attach not found: " + attachId));
    }

    private String getPath(AttachEntity entity) {
        return folderName + "/" + entity.getPath() + "/" + entity.getFileName();
    }

    // Yil/Oy/Kun formatini qaytaradi (Masalan: 2024/11/5)
    private String getYmDString() {
        int year = Calendar.getInstance().get(Calendar.YEAR);
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DATE);
        return year + "/" + month + "/" + day;
    }

    // Fayl kengaytmasini ajratib oladi
    private String getExtension(String fileName) {
        int lastIndex = fileName.lastIndexOf(".");
        if (lastIndex == -1) {
            return "";
        }
        return fileName.substring(lastIndex + 1);
    }

    private String openURL(String fileName) {
        return domainName + attachFileAccessPrefix + "/open/" + fileName;
    }

    private AttachDTO toDTO(AttachEntity entity) {
        AttachDTO attachDTO = new AttachDTO();
        attachDTO.setId(entity.getFileName());
        attachDTO.setOriginName(entity.getOriginalName());
        attachDTO.setSize(entity.getSize());
        attachDTO.setExtension(entity.getExtension());
        attachDTO.setCreatedData(entity.getCreatedDate());
        attachDTO.setUrl(openURL(entity.getFileName()));
        return attachDTO;
    }

//    -----------------------------------------------------------------------------------------------------------------

}