package phr.lib;

import java.io.Serializable;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Created by Anupam on 28-Mar-18.
 *
 * The note object, contains all relevant information regarding the note.
 *
 * The healtprofessional name is not a part of the db, however the query used to retrive the note information uses where and "and" condition to insert it.
 *
 */

public class Note implements Serializable {
    private int id, user_id, health_professional_id;
    private String name, description, health_professional_name;

    private Timestamp createTime;

    public Note(int id, int user_id, int health_professional_id, String name, String description, String health_professional_name){
        this.id=id;
        this.user_id=user_id;
        this.health_professional_name=health_professional_name;
        this.health_professional_id=health_professional_id;
        this.name=name;
        this.description=description;
    }

    public int getId() {
        return this.id;
    }
    public int getUser_id() {
        return this.user_id;
    }
    public int getHealth_professional_id() {
        return this.health_professional_id;
    }
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public String getHealth_professional_name() {
        return this.health_professional_name;
    }

    public Timestamp getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Timestamp createTime) {
        this.createTime = createTime;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }
    public void setHealth_professional_id(int health_professional_id) {
        this.health_professional_id = health_professional_id;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setHealth_professional_name(String health_professional_name) {
        this.health_professional_name = health_professional_name;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", user_id=" + user_id +
                ", health_professional_id=" + health_professional_id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", health_professional_name='" + health_professional_name + '\'' +
                '}';
    }
}
