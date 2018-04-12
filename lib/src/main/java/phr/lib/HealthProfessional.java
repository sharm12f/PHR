package phr.lib;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * Created by Anupam on 29-Jan-18.
 *
 * The health professional object inherets from the user
 *
 * the patient list is not a part of the db and needs to be generated using other library functions
 *
 */

public class HealthProfessional extends User implements Serializable{
    private String organization;
    private String department;
    private String health_professional;
    private ArrayList<Patient> patients;

    public HealthProfessional(String name, String email, Timestamp create, String role){
        super(name,email,create,role);
        this.patients = new ArrayList<Patient>();
    }

    public String getOrganization(){return this.organization;}
    public String getDepartment(){return this.department;}
    public String getHealthProfessional(){return this.health_professional;}
    public ArrayList<Patient> getPatient(){
        return this.patients;
    }

    public void setOrganization(String organization){this.organization=organization;}
    public void setDepartment(String department){this.department=department;}
    public void setHealthProfessional(String health_professional){this.health_professional=health_professional;}
    public void setPatient(ArrayList<Patient> patients){
        this.patients = patients;
    }

    public void addPatient(Patient patient){
        this.patients.add(patient);
    }

    public void printPatients(){
        for(int i = 0; i<this.patients.size(); i++){
            System.out.println(this.patients.get(i).toString());
        }
    }

    @Override
    public String toString() {
        return "HealthProfessional{" +
                "organization='" + organization + '\'' +
                ", department='" + department + '\'' +
                ", health_professional='" + health_professional + '\'' +
                ", patients=" + patients +
                super.toString() +
                '}';
    }
}
