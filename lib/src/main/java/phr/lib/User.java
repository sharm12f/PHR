package phr.lib;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Anupam on 29-Jan-18.
 */

public class User implements Serializable{
    private String email;
    private String role;
    private String name;
    private String phone;
    private String region;
    private int id;
    private Timestamp create;
    private Timestamp login;
    private Timestamp session;
    private ArrayList<Note> notes;

    public User(String name, String email, Timestamp create, String role){
        this.name = name;
        this.email = email;
        this.role = role;
        this.create = create;
    }

    public Timestamp getLogin(){
        return this.login;
    }
    public Timestamp getCreate(){
        return this.create;
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
    public String getName(){return this.name;}
    public String getPhone(){return this.phone;}
    public String getRegion(){return this.region;}
    public ArrayList<Note> getNotes(){return this.notes;}

    public void setLogin(Timestamp login){
        this.login = login;
    }
    public void setCreate(Timestamp create){
        this.create = create;
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
    public void setName(String name){this.name=name;}
    public void setPhone(String phone){this.phone=phone;}
    public void setRegion(String region){this.region = region;}
    public void setNotes(ArrayList<Note> notes){this.notes=notes;}

    public Timestamp getSession() {
        return session;
    }

    public void setSession(Timestamp session) {
        this.session = session;
    }

    public String toString(){
        return "Name: " + this.name + " Email: " + this.email + " Role: " + this.role + " create: " + this.create + " login: " + this.login + " ID: " + this.id + " Phone: "+ this.phone + " Region: " + this.region;
    }
}
