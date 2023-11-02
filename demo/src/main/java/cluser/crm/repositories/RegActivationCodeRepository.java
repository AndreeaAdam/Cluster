package cluser.crm.repositories;

import cluser.crm.models.RegActivationCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RegActivationCodeRepository extends MongoRepository<RegActivationCode, String> {
    RegActivationCode getByRegistrationCode(String registrationCode);
    List<RegActivationCode> getAllByUserId(String userId);
}
