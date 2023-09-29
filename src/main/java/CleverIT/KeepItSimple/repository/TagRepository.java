package CleverIT.KeepItSimple.repository;

import java.util.List;
import java.util.Optional;

import CleverIT.KeepItSimple.models.Tag;
import CleverIT.KeepItSimple.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface TagRepository extends MongoRepository<Tag, String> {
    Optional<Tag> findByTagnameAndCreatedBy(String tagname, User createdBy);

    List<Tag> findByCreatedBy(User createdBy);
}
