package cluser.crm.repositories;

import cluser.crm.models.UserSetting;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserSettingRepository extends MongoRepository<UserSetting,String> {
    UserSetting getByMainId(String mainId);
    
    
}
