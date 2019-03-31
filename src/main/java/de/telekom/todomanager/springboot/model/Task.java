package de.telekom.todomanager.springboot.model;

import org.springframework.util.StringUtils;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tasks")
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "done")
    private boolean done;

    @Column(name = "created")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @Column(name = "finishDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date finishDate;

    @Column(name = "parentTask")
    private Integer parentTaskID;

    @OneToMany(mappedBy = "parentTaskID", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> children = new ArrayList<>();

    public Task() {
        super();
    }

    public Task(int id, String title, String description, boolean done, Date created, Date finishDate, Integer parentTaskID) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.done = done;
        this.created = created;
        this.finishDate = finishDate;
        this.parentTaskID = parentTaskID;
    }

    public Task(String title, String description, boolean done, Date finishDate) {
        this.title = title;
        this.description = description;
        this.done = done;
        this.finishDate = finishDate;
    }

    public Task(String title, String description, boolean done, Date finishDate, int parentTaskID) {
        this.title = title;
        this.description = description;
        this.done = done;
        this.finishDate = finishDate;
        this.parentTaskID = parentTaskID;
    }


    public List<Task> getChildren() {
        return children;
    }

    public void setChildren(List<Task> children) {
        this.children = children;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getFinishDate() {
        return finishDate;
    }

    public void setFinishDate(Date finishDate) {
        this.finishDate = finishDate;
    }

    public Integer getParentTaskID() {
        return parentTaskID;
    }

    public void setParentTaskID(Integer parentTaskID) {
        this.parentTaskID = parentTaskID;
    }

    public boolean isValid(){
        return !StringUtils.isEmpty(this.title) &&
                !StringUtils.isEmpty(this.description) &&
                this.finishDate != null &&
                !this.finishDate.before(this.created);
    }

    // returns true if the task has parents.
    public boolean isChildTask() {
        return (this.parentTaskID != null);
    }

    public String calculateTimespan() {
        LocalDateTime finishDateTime = getFinishDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime now = LocalDateTime.now();
        long remainingTime = now.until(finishDateTime, ChronoUnit.DAYS);
        String timeLeft = "";

        if (remainingTime > 0 && remainingTime < 30) {
            timeLeft = now.until(finishDateTime, ChronoUnit.DAYS) + " days ";
        } else if (remainingTime == 0) {
            timeLeft = now.until(finishDateTime, ChronoUnit.HOURS) + " hours ";
        } else if (remainingTime < 0) {
            timeLeft = "no time ";
        } else if (remainingTime > 0 && remainingTime >= 30) {
            timeLeft = now.until(finishDateTime, ChronoUnit.MONTHS) + " month/s ";
        }


        return timeLeft;
    }
}
