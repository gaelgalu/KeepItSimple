package CleverIT.KeepItSimple.repository;

import CleverIT.KeepItSimple.models.Task;
import CleverIT.KeepItSimple.models.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

public interface TaskRepository extends MongoRepository<Task, String> {
    Optional<Task> findByTitleAndUser(String title, User user);
    List<Task> findByStatusAndUser(String status, User user);

    Optional<Task> findByIdAndUser(String id, User user);

    @Query("{'deadline' : { $lt : ?0 }}")
    List<Task> findTasksWithDeadlineBeforeAndUser(LocalDate deadline, User user);

    List<Task> findByUser(User user);
}