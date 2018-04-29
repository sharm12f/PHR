package phr.lib;

import java.sql.Timestamp;
import java.io.Serializable;

/**
 * Created by Anupam on 29-Dec-17.
 *
 * This is the record object. It contains all the information that is found in its corospoinding database table.
 *
 */

public class Record implements Serializable{
    private int id, user_id;
    private String record, name, filename;
    private Timestamp create;
    public Record(String name, String record, int user_id, Timestamp create){
        this.user_id = user_id;
        this.record = record;
        this.create = create;
        this.name= name;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setUser_id(int user_id){
        this.user_id = user_id;
    }
    public void setName(String name){this.name=name;}
    public void setRecord(String record){
        this.record = record;
    }
    public int getId(){
        return this.id;
    }
    public int getUser_id(){
        return this.user_id;
    }
    public String getRecord(){
        return this.record;
    }
    public String getName(){return this.name;}

    public String getFilename(){return this.filename;}

    public void setFilename(String filename){this.filename = filename;}

    public Timestamp getCreate() {
        return create;
    }

    public void setCreate(Timestamp create) {
        this.create = create;
    }

    public String toString(){
        return "name: "+this.name+"user_id: " + this.user_id + " record: " + this.record + " ID: " + this.id;
    }
}
