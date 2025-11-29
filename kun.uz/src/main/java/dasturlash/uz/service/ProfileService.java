package dasturlash.uz.service;

import dasturlash.uz.entity.ProfileEntity;
import dasturlash.uz.exps.AppBadException;
import dasturlash.uz.repository.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProfileService {
    @Autowired
    private ProfileRepository profileRepository;

    public ProfileEntity getById(Integer profileId){
        return profileRepository.findByIdAndVisibleTrue(profileId).orElseThrow( () -> {
            throw new AppBadException("Profile Not Found");
        });
    }
}
