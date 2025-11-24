package dasturlash.uz.repository;

import dasturlash.uz.entity.ProfileRoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfileRoleRepository extends JpaRepository<ProfileRoleEntity, Integer> {
}
