package cluser.crm.repositories;

import cluser.crm.models.DocNumber;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DocNumberRepository extends MongoRepository<DocNumber, String> {
    Optional<DocNumber> getByMainId(String mainId);
}
