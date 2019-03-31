package de.telekom.todomanager.springboot.controller;
import de.telekom.todomanager.springboot.model.Task;
import de.telekom.todomanager.springboot.repositories.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class TaskRestController {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private TaskRepository taskRepository;

    @Autowired
    public TaskRestController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping(value = "/tasks")
    public ResponseEntity<List<Task>> retrieveTasks() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<Task> tasks = taskRepository.findAll();

        return ResponseEntity.ok(tasks);
    }

    @GetMapping(value = "/parents")
    public ResponseEntity<List<Task>> retrieveParentTasksOnly() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        List<Task> tasks = taskRepository.findAllParentTasks();
        for (Task parent : tasks){
            parent.setChildren(new ArrayList<>());
        }

        return ResponseEntity.ok(tasks);
    }

    @GetMapping(value = "/task/{id}")
    public ResponseEntity<Task> retrieveTaskWithId(@PathVariable(value = "id") String id){
        Task task = taskRepository.findById(Integer.parseInt(id));

//        if (task == null){
//            return ResponseEntity.badRequest().body("Der angegebene Task konnte nicht gefunden werden.");   //geht nicht
//        }
        return ResponseEntity.ok(task);
    }
}
