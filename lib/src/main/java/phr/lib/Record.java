package phr.lib;

import java.sql.Timestamp;
import java.io.Serializable;

/**
 * Created by Anupam on 29-Dec-17.
 */

public class Record implements Serializable{
    private int id, user_id;
    private String policy, record, record_ref, name;
    private Timestamp create;
    public Record(String name, String policy, String record, String record_ref,int user_id, Timestamp create){
        this.user_id = user_id;
        this.policy = policy;
        this.record = record;
        this.record_ref = record_ref;
        this.create = create;
        this.name= name;
    }
    public void setId(int id){
        this.id = id;
    }
    public void setUser_id(int user_id){
        this.user_id = user_id;
    }
    public void setPolicy(String policy){
        this.policy = policy;
    }
    public void setName(String name){this.name=name;}
    public void setRecord(String record){
        this.record = record;
    }
    public void setRecord_ref(String record_ref){
        this.record_ref = record_ref;
    }
    public int getId(){
        return this.id;
    }
    public int getUser_id(){
        return this.user_id;
    }
    public String getPolicy(){
        return this.policy;
    }
    public String getRecord(){
        return this.record;
    }
    public String getName(){return this.name;}
    public String getRecord_ref(){
        return this.record_ref;
    }
    public String toString(){
        return "name: "+this.name+"user_id: " + this.user_id + " policy: " + this.policy + " record: " + this.record + " record_ref: " + this.record_ref + " ID: " + this.id;
    }
}
