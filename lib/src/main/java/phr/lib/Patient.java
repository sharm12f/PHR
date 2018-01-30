package phr.lib;

import com.sun.org.apache.regexp.internal.RE;

import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.io.Serializable;

import javax.sound.sampled.ReverbType;

/**
 * Created by Anupam on 28-Dec-17.
 */

public class Patient extends User implements Serializable{
    private String province;
    private ArrayList<Record> records;

    public Patient(String fname, String lname, String email, Timestamp create, String role, Timestamp session){
        super(fname,lname,email,create,role,session);
        this.records = new ArrayList<Record>();
    }


    public void addRecord(Record record){
        this.records.add(record);
    }
    public void removeRecord(int i){
        this.records.remove(i);
    }
    public ArrayList<Record> getRecords(){
        return this.records;
    }
    public void setRecords(ArrayList<Record> records){
        this.records = records;
    }
    public void printRecords(){
        for(int i=0; i<this.records.size(); i++){
            System.out.println(this.records.get(i).toString());
        }
    }

    public String getProvince(){return this.province;}

    public void setProvince(String province){this.province=province;}

    public String toString(){
        return super.toString() + " Province: "+ this.province;
    }
}
