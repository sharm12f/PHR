package phr.lib;

import com.sun.org.apache.regexp.internal.RE;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.Serializable;

import javax.sound.sampled.ReverbType;

/**
 * Created by Anupam on 28-Dec-17.
 */

public class Patient extends User implements Serializable{
    private String province;
    private LinkedList<Record> records;

    public Patient(String fname, String lname, String email, Timestamp create, String role, Timestamp session){
        super(fname,lname,email,create,role,session);
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
        return super.getSession();
    }
    public Timestamp getCreate(){
        return super.getCreate();
    }
    public String getEmail(){
        return super.getEmail();
    }
    public String getRole(){
        return super.getRole();
    }
    public int getId(){
        return super.getId();
    }
    public String getfName(){return super.getfName();}
    public String getlName(){return super.getlName();}
    public String getPhone(){return super.getPhone();}
    public String getRegion(){return super.getRegion();}
    public String getProvince(){return this.province;}

    public void setSession(Timestamp session){
        super.setSession(session);
    }
    public void setCreate(Timestamp create){
        super.setCreate(create);
    }
    public void setEmail(String email){
        super.setEmail(email);
    }
    public void setRole(String role){
        super.setRole(role);
    }
    public void setId(int id){
        super.setId(id);
    }
    public void setfName(String fname){super.setfName(fname);}
    public void setlName(String lname){super.setlName(lname);}
    public void setPhone(String phone){super.setPhone(phone);}
    public void setRegion(String region){super.setRegion(region);}
    public void setProvince(String province){this.province=province;}

    public String toString(){
        return super.toString() + " Province: "+ this.province;
    }
}
