package CleverIT.KeepItSimple.controllers;

import CleverIT.KeepItSimple.models.Tag;
import CleverIT.KeepItSimple.models.Task;
import CleverIT.KeepItSimple.models.User;
import CleverIT.KeepItSimple.payload.request.SearchAndFilter;
import CleverIT.KeepItSimple.payload.request.TaskRequest;
import CleverIT.KeepItSimple.repository.TagRepository;
import CleverIT.KeepItSimple.repository.TaskRepository;
import CleverIT.KeepItSimple.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/tasks")
public class TaskController {
	@Autowired
	private TaskRepository taskRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TagRepository tagRepository;
	
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Content.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR')")
	public String moderatorAccess() {
		return "Moderator Board.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Board.";
	}

	@GetMapping("/")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<List<Task>> allTasks(Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		List<Task> tasks = this.taskRepository.findByUser(user);

		return ResponseEntity.ok(tasks);
	}

	@PostMapping("/")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> createTask(@RequestBody TaskRequest taskRequest, Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		Task newTask = new Task(taskRequest.getTitle(), taskRequest.getDescription(), null, null, null, user);

		if (taskRequest.getDeadline() != null) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				LocalDate localDate = LocalDate.parse(taskRequest.getDeadline(), formatter);

				newTask.setDeadline(localDate);
			} catch (java.time.format.DateTimeParseException e) {
				//logger.error(e.getMessage());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			}
		}

		if (taskRequest.getStatus() != null) {
			newTask.setStatus(taskRequest.getStatus());
		}

		if (taskRequest.getTags() != null) {
			for (String tagname : taskRequest.getTags()) {
				newTask.addTag(
						this.tagRepository.findByTagnameAndCreatedBy(tagname, user)
						.orElseGet(
								() -> this.tagRepository.save(new Tag(tagname, null, user))
						)
				);
			}
		}

		Task savedTask = this.taskRepository.save(newTask);

		for (Tag tag : savedTask.getTags()) {
			tag.addTask(savedTask);
			this.tagRepository.save(tag);
		}


		return ResponseEntity.status(HttpStatus.CREATED).body("Task created successfully. ID: " + this.taskRepository.save(newTask).getId());
	}

	@PutMapping("/{taskId}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> updateTask(@PathVariable String taskId, @RequestBody TaskRequest taskRequest, Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		Task existingTask = this.taskRepository.findByIdAndUser(taskId, user)
				.orElse(null);

		if (existingTask == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
		}

		if (!existingTask.getUser().getUsername().equals(authentication.getName())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not enough rights to update this task");
		}

		if (taskRequest.getTitle() != null) {
			existingTask.setTitle(taskRequest.getTitle());
		}

		if (taskRequest.getDescription() != null) {
			existingTask.setDescription(taskRequest.getDescription());
		}

		if (taskRequest.getDeadline() != null) {
			try {
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
				LocalDate localDate = LocalDate.parse(taskRequest.getDeadline(), formatter);

				existingTask.setDeadline(localDate);
			} catch (java.time.format.DateTimeParseException e) {
				//logger.error(e.getMessage());
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
			}
		}

		if (taskRequest.getStatus() != null) {
			existingTask.setStatus(taskRequest.getStatus());
		}

		if (taskRequest.getTags() != null) {
			existingTask.getTags().clear();

			for (String tagname : taskRequest.getTags()) {
				existingTask.addTag(
						this.tagRepository.findByTagnameAndCreatedBy(tagname, existingTask.getUser())
						.orElseGet(
								() -> this.tagRepository.save(new Tag(tagname, new ArrayList<>(List.of(existingTask)), existingTask.getUser()))
						)
				);
			}
		}

		Task updatedTask = this.taskRepository.save(existingTask);

		for (Tag tag : updatedTask.getTags()) {
			tag.addTask(updatedTask);
			this.tagRepository.save(tag);
		}


		//This could be a scheduled tasks in the database itself.
		for (Tag tag : this.tagRepository.findByCreatedBy(updatedTask.getUser())) {
			if (tag.getTasks() == null || tag.getTasks().isEmpty()) {
				tagRepository.delete(tag);
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body("Task updated successfully. ID: " + updatedTask.getId());
	}

	@DeleteMapping("/{taskId}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> deleteTask(@PathVariable String taskId, Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		Task existingTask = this.taskRepository.findByIdAndUser(taskId, user)
				.orElse(null);

		if (existingTask == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
		}

		if (!existingTask.getUser().getUsername().equals(authentication.getName())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not enough rights to delete this task");
		}

		this.taskRepository.delete(existingTask);

		for (Tag tag : this.tagRepository.findByCreatedBy(existingTask.getUser())) {
			if (tag.getTasks() == null || tag.getTasks().isEmpty()) {
				tagRepository.delete(tag);
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body("Task deleted successfully. ID: " + taskId);
	}

	@GetMapping("/{taskId}")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> getTask(@PathVariable String taskId, Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		Task existingTask = this.taskRepository.findByIdAndUser(taskId, user)
				.orElse(null);

		if (existingTask == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Task not found");
		}

		if (!existingTask.getUser().getUsername().equals(authentication.getName())) {
			return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not enough rights to get this task");
		}

		return ResponseEntity.status(HttpStatus.OK).body(existingTask);
	}

	@PostMapping("/search-by-tags")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> findTasksByTags(@RequestBody SearchAndFilter searchAndFilter, Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		List<Task> tasks = new ArrayList<>();
		if (searchAndFilter.getTags() != null && !searchAndFilter.getTags().isEmpty()) {
			for (String tagname : searchAndFilter.getTags()) {
				tagRepository.findByTagnameAndCreatedBy(tagname, user).ifPresent(tag -> tasks.addAll(tag.getTasks()));
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(tasks);
	}

	@PostMapping("/search-by-status")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> findTasksByStatus(@RequestBody SearchAndFilter searchAndFilter, Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		List<Task> tasks = new ArrayList<>();
		if (searchAndFilter.getStatus() != null && !searchAndFilter.getStatus().isEmpty()) {
			for (String status : searchAndFilter.getStatus()) {
				List<Task> tasksWithStatus = taskRepository.findByStatusAndUser(status, user);
				tasks.addAll(tasksWithStatus);
			}
		}

		return ResponseEntity.status(HttpStatus.OK).body(tasks);
	}

	@PostMapping("/search-by-title")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> findTasksByTitle(@RequestBody SearchAndFilter searchAndFilter, Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		if (searchAndFilter.getTitle() != null && !searchAndFilter.getTitle().isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(taskRepository.findByTitleAndUser(searchAndFilter.getTitle(), user));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
	}

	@PostMapping("/search-by-tagname")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public ResponseEntity<?> findTasksByTagname(@RequestBody SearchAndFilter searchAndFilter, Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		if (searchAndFilter.getTagname() != null && !searchAndFilter.getTagname().isEmpty()) {
			return ResponseEntity.status(HttpStatus.OK).body(tagRepository.findByTagnameAndCreatedBy(searchAndFilter.getTagname(), user));
		}

		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Bad request");
	}

	@GetMapping("/search-by-deadline")
	public ResponseEntity<List<Task>> findTasksWithDeadlineBefore(
			@RequestParam("deadline") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate deadline,
			Authentication authentication) {
		User user = this.userRepository.findByUsername(authentication.getName())
				.orElseThrow(() -> new UsernameNotFoundException("Username was not found"));

		List<Task> tasks = taskRepository.findTasksWithDeadlineBeforeAndUser(deadline, user);
		return new ResponseEntity<>(tasks, HttpStatus.OK);
	}

}
