package de.telekom.todomanager.springboot.controller;


import de.telekom.todomanager.springboot.model.Task;
import de.telekom.todomanager.springboot.repositories.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping(value = "/tasks")
public class TaskController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    private TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    @GetMapping(value = "/")
    public String getOverview() {
        logger.debug("Startseite wird aufgerufen.");
        return "overview";
    }

    /***
     * This method is called when the client is requesting the url "tasks/new" and returns the view with
     * form to create a new task.
     *
     * @return String newTask (View)
     */
    @RequestMapping(value = "/new")
    public String getCreatingTaskView() {
        return "newTask";
    }


    @RequestMapping(value = "/new", method = RequestMethod.POST)
    public ResponseEntity createTask(Task task) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        ResponseEntity responseEntity = null;
        task.setCreated(new Date());
        if (!task.isValid()) {
            Exception error = new IllegalArgumentException("Es wurden nicht alle Felder ausgefüllt oder einige Eingaben entsprechen nicht den Anforderungen.");
            responseEntity = ResponseEntity.badRequest().body(error.getMessage());
        } else {
            taskRepository.saveAndFlush(task);
            responseEntity = ResponseEntity.ok().body("Der Task wurde erfolgreich angelegt");
        }
        return responseEntity;                                                     //redirect back to the Task overview page
    }

    @RequestMapping(value = "/new/{id}")
    public String createTaskWithPrefilledParentTask(@PathVariable(value = "id") String id, ModelMap model) {
        int parentId = Integer.parseInt(id);
        Task parent = taskRepository.findById(parentId);
        model.addAttribute("parent", parent);
        return "newTaskParentPrefilled";
    }

    /***
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/task/{id}")
    public String getTask(@PathVariable(value = "id") String id, ModelMap model) {
        int taskId = Integer.parseInt(id);
        Task task = taskRepository.findById(taskId);

        if (task == null) {
            throw new IllegalArgumentException("Der gesuchte Task ist nicht vorhanden.");
        }

        if (task.isChildTask()) {
            List<Task> parentTasks = taskRepository.findAllParentTasks();
            model.addAttribute("parentTasks", parentTasks);
        }

        model.addAttribute("task", task);
        model.addAttribute("childs", task.getChildren());

        return "edit";
    }


    /***
     * @param
     * @return
     */
    @RequestMapping(value = "/task/{id}", method = RequestMethod.POST)
    public String editTask(@PathVariable(value = "id") String taskId, Task task) {
        int id = Integer.parseInt(taskId);
        task.setId(id);
        taskRepository.saveAndFlush(task);                                                                    //calls the repositoryClass to create a new task in the Database
        return "redirect:/tasks/";

    }

    @RequestMapping("/delete/{id}")
    public String deleteTask(@PathVariable(value = "id") String id) {

        int taskId = Integer.parseInt(id);
        taskRepository.delete(taskRepository.findById(taskId));
        return "redirect:/tasks/";
    }

    /***
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/task/complete/{id}", method = RequestMethod.POST)
    public String toggleTaskCompletion(@PathVariable(value = "id") String id) {
        int taskId = Integer.parseInt(id);
        Task task = taskRepository.findById(taskId);

        if (task.isChildTask()) {                                                    // if Task is a child Task
            task.setDone(!task.isDone());
            taskRepository.save(task);
            Task parent = taskRepository.findById(task.getParentTaskID()).get();   // then take the parent Task
            /*
             * check if all children of the parent are done
             * if yes -> set parent to done
             */
            boolean allChildrenDone = parent.getChildren().stream().allMatch(Task::isDone);

            parent.setDone(allChildrenDone);
            taskRepository.save(parent);
        } else {
            List<Task> children = task.getChildren();
            task.setDone(!task.isDone());
            for (Task child : children) {
                child.setDone(task.isDone());
            }
        }

        taskRepository.flush();
        return "redirect:/tasks/";
    }


    /***
     * This method returns an overview of the Task which was clicked on.
     * @param id
     * @param model
     * @return
     */
    @RequestMapping("/{id}")
    public String showTask(@PathVariable(value = "id") int id, ModelMap model) {

        Task task = taskRepository.findById(id);
        model.addAttribute("task", task);

        //adding parentTask if available. if not - adding subtasks to the model
        Task parent = null;
        if (task.isChildTask()) {                                              //checking if Task is a children
            parent = taskRepository.findById(task.getParentTaskID()).get();
            model.addAttribute("parent", parent);
        } else {
            //adding all childrenTasks, if children attribute is null null will be added.
            model.addAttribute("childs", task.getChildren());

            //compute finish-progress
            List<Task> subtasks = task.getChildren();
            float numberOfSubtasks = subtasks.size();
            float numberOfFinishedSubtasks = subtasks.stream().filter(Task::isDone).count();
            float finishProgress = 0;

            if (numberOfSubtasks > 0) {
                finishProgress = (numberOfFinishedSubtasks / numberOfSubtasks) * 100;
            }
            int progress = (int) (finishProgress);
            model.addAttribute("finishProgress", progress);
        }
        return "task";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class,
                new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true, 10));
    }
}
