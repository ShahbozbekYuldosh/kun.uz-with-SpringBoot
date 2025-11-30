package dasturlash.uz.repository;

import dasturlash.uz.entity.ProfileEntity;
import jakarta.transaction.Transactional;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    boolean existsByUsername(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer profileId);

    @Transactional
    @Modifying
    @Query("UPDATE ProfileEntity p SET p.visible = :visible WHERE p.id = :id")
    int updateVisible(@Param("visible") Boolean visible, @Param("id") Integer id);

    @Transactional
    @Modifying
    @Query("UPDATE ProfileEntity p SET p.photoId = :newPhotoId WHERE p.id = :profileId")
    int updatePhotoId(@Param("newPhotoId") String newPhotoId, @Param("profileId") Integer profileId);


    @Transactional
    @Modifying
    @Query("UPDATE ProfileEntity p SET p.password = :newPassword WHERE p.id = :profileId")
    int updatePassword(@Param("newPassword") String newPassword, @Param("profileId") Integer profileId);
}

