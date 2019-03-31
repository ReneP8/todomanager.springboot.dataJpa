package de.telekom.todomanager.springboot.repositories;

import de.telekom.todomanager.springboot.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    Task findFirstByParentTaskIDIsNull();

    @Query("select t FROM Task t where t.parentTaskID = :parentId")
    List<Task> getAllChildren(@Param("parentId") int parentId);

    @Query("select t from Task t order by t.created")
    List<Task> findAllOrderByCreated();

    @Query("select t FROM Task t where t.id = :id")
    Task findById(@Param("id") int id);

    @Query("select t FROM Task t where t.parentTaskID = null")
    List<Task> findAllParentTasks();

    @Query("select t From Task t where t.parentTaskID = null")
    List<Task> findAll();

}
