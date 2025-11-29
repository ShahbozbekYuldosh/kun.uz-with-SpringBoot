package dasturlash.uz.repository;

import dasturlash.uz.entity.ProfileEntity;
import org.jspecify.annotations.Nullable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<ProfileEntity, Integer> {

    Optional<ProfileEntity> findByUsernameAndVisibleTrue(String username);

    boolean existsByUsername(String username);

    Optional<ProfileEntity> findByIdAndVisibleTrue(Integer profileId);

    void updateVisible(Integer id, boolean b);

    void updatePhotoId(Integer currentUserId, String attachId);

    void updatePassword(Integer currentUserId, @Nullable String encode);
}

