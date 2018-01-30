package phr.lib;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by Anupam on 29-Jan-18.
 */

public class HealthProfessional extends User implements Serializable{
    private String organization;
    private String department;
    private String health_professional;

    public HealthProfessional(String fname, String lname, String email, Timestamp create, String role, Timestamp session){
        super(fname,lname,email,create,role,session);
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
    public String getOrganization(){return this.organization;}
    public String getDepartment(){return this.department;}
    public String getHealthProfessional(){return this.health_professional;}

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
    public void setOrganization(String organization){this.organization=organization;}
    public void setDepartment(String department){this.department=department;}
    public void setHealthProfessional(String health_professional){this.health_professional=health_professional;}

    public String toString(){
        return super.toString() + " Organization: "+ this.organization+ " Department: "+ this.department+ " Health Professional: "+ this.health_professional;
    }
}
