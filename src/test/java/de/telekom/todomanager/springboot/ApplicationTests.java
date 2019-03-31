package de.telekom.todomanager.springboot;

import de.telekom.todomanager.springboot.model.Task;
import de.telekom.todomanager.springboot.repositories.TaskRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class ApplicationTests {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TaskRepository taskRepository;

    List<Task> testTasks = new ArrayList<Task>() {{
        add(new Task(1, "parent 1", "parent 1 desc", false, new Date(), new Date(), null));
        add(new Task(2, "child 1 of parent 1", "child 1 desc", false, new Date(), new Date(), 1));
        add(new Task(3, "child 2 of parent 1", "child 2 desc", false, new Date(), new Date(), 1));
    }};

    Task newDummyTask = new Task(4, "parent 2", "parent 2 desc", false, new Date(), new Date(), 23);


    @Test
    public void canLoadTasks() throws Exception {
        when(taskRepository.findAll()).thenReturn(testTasks);

        mockMvc.perform(get("/tasks/"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("overview"))
                .andExpect(model().attribute("tasks", hasSize(3)))
                .andExpect(model().attribute("tasks", hasItem(
                        allOf(
                                hasProperty("id", is(1)),
                                hasProperty("title", is("parent 1"))
                        )
                )))
                .andExpect(model().attribute("tasks", hasItem(
                        allOf(
                                hasProperty("id", is(2))
                        )
                )))
                .andExpect(model().attribute("tasks", hasItem(
                        allOf(
                                hasProperty("id", is(3))
                        )
                )));
    }

    @Test
    public void newTaskViewReturnsAllTasks() throws Exception {
        when(taskRepository.findAll()).thenReturn(testTasks);

        mockMvc.perform(get("/tasks/new"))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("newTask"))
                .andExpect(model().attribute("tasks", hasSize(3)))
                .andExpect(model().attribute("tasks", hasItem(
                        allOf(
                                hasProperty("id", is(1)),
                                hasProperty("title", is("parent 1"))
                        )
                )))
                .andExpect(model().attribute("tasks", hasItem(
                        allOf(
                                hasProperty("id", is(2))
                        )
                )))
                .andExpect(model().attribute("tasks", hasItem(
                        allOf(
                                hasProperty("id", is(3))
                        )
                )));
    }

    @Test
    public void createNewTaskWithAllParameters() throws Exception {
        mockMvc.perform(post("/tasks/new")
                .param("title", "taskName")
                .param("description", "description")
                .param("finishDate", "2019-03-15")
                .param("parentTaskID", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/"));

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveAndFlush(argument.capture());

        assertEquals(argument.getValue().getTitle(), "taskName");
        assertEquals(argument.getValue().getDescription(), "description");
        assertEquals(argument.getValue().getFinishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), "2019-03-15");
        assertEquals(argument.getValue().getParentTaskID(), null);
    }

    @Test
    public void createNewTaskWithoutDescriptionParam() throws Exception {
        mockMvc.perform(post("/tasks/new")
                .param("title", "taskName")
                .param("description", "")
                .param("finishDate", "2019-03-15")
                .param("parentTaskID", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/"));

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveAndFlush(argument.capture());

        assertEquals(argument.getValue().getTitle(), "taskName");
        assertEquals(argument.getValue().getDescription(), "");
        assertEquals(argument.getValue().getFinishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), "2019-03-15");
        assertEquals(argument.getValue().getParentTaskID(), null);
    }

    @Test
    public void createNewTaskWithAnyParent() throws Exception {
        mockMvc.perform(post("/tasks/new")
                .param("title", "taskName")
                .param("description", "description")
                .param("finishDate", "2019-03-15")
                .param("parentTaskID", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/"));

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveAndFlush(argument.capture());

        assertEquals(argument.getValue().getTitle(), "taskName");
        assertEquals(argument.getValue().getDescription(), "description");
        assertEquals(argument.getValue().getFinishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), "2019-03-15");
        assertEquals(argument.getValue().getParentTaskID().intValue(), 1);
        assertTrue(argument.getValue().isChildTask());
    }

    @Test
    public void createNewTaskFailsWithoutAnyPostData() throws Exception {
        mockMvc.perform(post("/tasks/new")
                .param("title", "")
                .param("description", "")
                .param("finishDate", "")
                .param("parentTaskID", ""))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void createNewTaskFailsWithMissingTitleParam() throws Exception {
        mockMvc.perform(post("/tasks/new")
                .param("title", "")
                .param("description", newDummyTask.getDescription())
                .param("finishDate", newDummyTask.getFinishDate().toString())
                .param("parentTaskID", "23"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void getEditTaskViewShowAllTaskAttributes() throws Exception {
        //when statement
        when(taskRepository.findById(newDummyTask.getId())).thenReturn(newDummyTask);

        mockMvc.perform(get("/tasks/task/" + newDummyTask.getId()))
                .andExpect(status().is2xxSuccessful())
                .andExpect(view().name("edit"))
                .andExpect(model().attribute("task", newDummyTask))
                .andExpect(model().attribute("childs", hasSize(0)));
    }

    @Test
    public void getEditTaskViewFailsWithWrongId() throws Exception {

        mockMvc.perform(get("/tasks/task/" + newDummyTask.getId()))
                .andExpect(status().is4xxClientError())
                .andExpect(content().string("Es wurden nicht alle Felder ausgefüllt."));

    }

    @Test
    public void editTaskSuccessfullyWithCorrectId() throws Exception {
        mockMvc.perform(post("/tasks/taks/" + newDummyTask.getId())
                .param("title", newDummyTask.getTitle())
                .param("description", newDummyTask.getDescription())
                .param("finishDate", newDummyTask.getFinishDate().toString())
                .param("parentTaskID", newDummyTask.getParentTaskID().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/"));

        ArgumentCaptor<Task> argument = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).saveAndFlush(argument.capture());

        assertEquals(argument.getValue().getTitle(), "taskName");
        assertEquals(argument.getValue().getDescription(), "description");
        assertEquals(argument.getValue().getFinishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(), "2019-03-15");
        assertEquals(argument.getValue().getParentTaskID().intValue(), 1);
        assertTrue(argument.getValue().isChildTask());


    }

    @Test
    public void deleteTaskSucsessfull() throws Exception {
        when(taskRepository.findById(4)).thenReturn(newDummyTask);
        mockMvc.perform(post("/tasks/delete/4"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/tasks/"));
        verify(taskRepository).delete(newDummyTask);
    }
}

