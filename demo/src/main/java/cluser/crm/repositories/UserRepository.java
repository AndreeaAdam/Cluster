package cluser.crm.repositories;

import cluser.crm.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<User, String> {
    User getByEmailIgnoreCase(String email);
    List<User> getByMainId(String mainId);

}
