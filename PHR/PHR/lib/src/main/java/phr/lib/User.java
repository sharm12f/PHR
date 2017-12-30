package phr.lib;

import com.sun.org.apache.regexp.internal.RE;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;

import javax.sound.sampled.ReverbType;

/**
 * Created by Anupam on 28-Dec-17.
 */

public class User {
    private String username;
    private String email;
    private String role;
    private int id;
    private Timestamp create;
    private LinkedList<Record> records;
    private Timestamp session;

    public User(String username, String email, Timestamp create, String role, int id, Timestamp session){
        this.username = username;
        this.email = email;
        this.role = role;
        this.id = id;
        this.create = create;
        this.session = session;
        this.records = new LinkedList<Record>();
    }


    public void addRecord(Record record){
        this.records.addLast(record);
    }
    public void removeRecord(int i){
        this.records.remove(i);
    }
    public LinkedList<Record> getRecords(){
        return this.records;
    }
    public void setRecords(LinkedList<Record> records){
        this.records = records;
    }
    public void printRecords(){
        Iterator<Record> itr = records.iterator();
        while(itr.hasNext()){
            System.out.println(itr.next().toString());
        }
    }

    public Timestamp getSession(){
        return this.session;
    }
    public Timestamp getCreate(){
        return this.create;
    }
    public String getUsername(){
        return this.username;
    }
    public String getEmail(){
        return this.email;
    }
    public String getRole(){
        return this.role;
    }
    public int getId(){
        return this.id;
    }

    public void setSession(Timestamp session){
        this.session = session;
    }
    public void setCreate(Timestamp create){
        this.create = create;
    }
    public void setUsername(String username){
        this.username = username;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setRole(String role){
        this.role = role;
    }
    public void setId(int id){
        this.id = id;
    }

    public String toString(){
        return "Username: " + this.username + " Email: " + this.email + " Role: " + this.role + " create: " + this.create + " session: " + this.session + " ID: " + this.id;
    }
}
