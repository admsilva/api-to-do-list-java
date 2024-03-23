package br.com.admsilva.todolist.controller;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.admsilva.todolist.model.TaskModel;
import br.com.admsilva.todolist.service.TaskService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@OpenAPIDefinition(info = @Info(title = "Tasks", version = "v1"))
@SecurityRequirement(name = "basicAuth")
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger LOG = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;
 
    @GetMapping("/")
    public ResponseEntity<List<TaskModel>> list(HttpServletRequest request) {
        var idUser = request.getAttribute("idUser");
        var tasks = this.taskService.getAllTasks((UUID) idUser);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> listById(HttpServletRequest request, @PathVariable UUID id) {
        try {
            var idUser = request.getAttribute("idUser");
            var task = this.taskService.getTaskById(id, (UUID) idUser);
            return ResponseEntity.status(HttpStatus.OK).body(task);
        } catch (Exception exception) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity<String> create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        try {
            var idUser = request.getAttribute("idUser");
            this.taskService.saveTask(taskModel, (UUID) idUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(null);
        } catch (Exception exception) {
            LOG.error("Exception occurred", exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Exception occurred");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        try {
            var idUser = request.getAttribute("idUser");
            var task = this.taskService.changeTask(taskModel, id, (UUID) idUser);
            return ResponseEntity.status(HttpStatus.OK).body(task);
        } catch (Exception exception) {
            LOG.error("Exception occurred", exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Exception occurred");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id, HttpServletRequest request) {
        try {
            var idUser = request.getAttribute("idUser");
            this.taskService.destroyTask(id, (UUID) idUser);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
        } catch (Exception exception) {
            LOG.error("Exception occurred", exception);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Exception occurred");
        }
    }
}
