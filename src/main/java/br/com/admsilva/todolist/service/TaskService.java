package br.com.admsilva.todolist.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.admsilva.todolist.model.TaskModel;
import br.com.admsilva.todolist.repository.ITaskRepository;
import br.com.admsilva.todolist.utils.Utils;

@Service
public class TaskService {
    
    @Autowired
    private ITaskRepository taskRepository;

    public List<TaskModel> getAllTasks(UUID idUser) {
        return this.taskRepository.findByIdUser(idUser);
    }

    public TaskModel getTaskById(UUID id, UUID idUser) throws Exception {
        var task = this.taskRepository.findById(id).orElse(null);
        this.checkTaskExistsAndUserIsOwner(task, idUser);
        return task;
    }

    public void saveTask(TaskModel taskModel, UUID idUser) throws Exception {
        taskModel.setIdUser(idUser);
        this.checkConsistencyDate(taskModel);
        this.taskRepository.save(taskModel);
    }

    public TaskModel changeTask(TaskModel taskModel, UUID id, UUID idUser) throws Exception {
        var task = this.taskRepository.findById(id).orElse(null);
        this.checkTaskExistsAndUserIsOwner(task, idUser);
        Utils.copyNonNullProperties(taskModel, task);
        this.checkConsistencyDate(task);
        return this.taskRepository.save(task);
    }

    public void destroyTask(UUID id, UUID idUser) throws Exception {
        var task = this.taskRepository.findById(id).orElse(null);
        this.checkTaskExistsAndUserIsOwner(task, idUser);
        this.taskRepository.deleteById(id);
    }

    private void checkTaskExistsAndUserIsOwner(TaskModel task, UUID idUser) throws Exception {
        if (task == null) {
            throw new Exception("Task not found.");
        }
        if (!task.getIdUser().equals(idUser)) {
            throw new Exception("User not has permission to change this task.");
        }
    }

    private void checkConsistencyDate(TaskModel taskModel) throws Exception {
        var currentDate = LocalDateTime.now();
        if (currentDate.isAfter(taskModel.getStartAt())) {
            throw new Exception("The start date cannot be less than the current date.");
        }
        if (currentDate.isAfter(taskModel.getEndAt())) {
            throw new Exception("The end date cannot be less than the current date.");
        }
        if (taskModel.getStartAt().isAfter(taskModel.getEndAt())) {
            throw new Exception("The start date cannot be later than the end date.");
        }
    }
}
