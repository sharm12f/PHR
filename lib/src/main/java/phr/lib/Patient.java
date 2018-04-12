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
 *
 * The patient inherets form the user object, it only contains the records list, which if generated using other library functions
 *
 * The records are not a part of the user table.
 *
 */

public class Patient extends User implements Serializable{
    private ArrayList<Record> records;

    public Patient(String name, String email, Timestamp create, String role){
        super(name,email,create,role);
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



    @Override
    public String toString() {
        return "Patient{" +
                ", records=" + records +
                super.toString() +
                '}';
    }
}
