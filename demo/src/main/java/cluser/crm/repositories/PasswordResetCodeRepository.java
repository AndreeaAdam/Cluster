package cluser.crm.repositories;

import cluser.crm.models.PasswordResetCode;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PasswordResetCodeRepository extends MongoRepository<PasswordResetCode, String> {
    Optional<PasswordResetCode> getByPasswordResetCode(String code);
}
