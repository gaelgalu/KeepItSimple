package CleverIT.KeepItSimple.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import CleverIT.KeepItSimple.models.ERole;
import CleverIT.KeepItSimple.models.Role;

public interface RoleRepository extends MongoRepository<Role, String> {
  Optional<Role> findByName(ERole name);
}
