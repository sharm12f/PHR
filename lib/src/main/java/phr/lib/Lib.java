package phr.lib;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.*;

public class Lib {

    public static User login(String email, String password){
        boolean checkUsername = checkEmail(email);
        boolean checkPassword = checkString(password);

        boolean isPatient = Auth_Access.userExists(email);
        boolean isPhysician = Auth_Access.healthUserExists(email);
        if(isPatient){
            Patient patient = null;
            if (!checkUsername || !checkPassword)
                System.out.println("Nope");
            else {
                if(Auth_Access.isUser(email, password)) {
                    patient = makeUser(email);
                }
                else{
                    System.out.println("Wrong email or password");
                }
            }
            return patient;
        }
        else if(isPhysician){
            HealthProfessional healthProfessional = null;
            if (!checkUsername || !checkPassword)
                System.out.println("Nope");
            else {
                if(Auth_Access.isHealthProfessional(email, password)) {
                    healthProfessional = makeHealthProfessional(email);
                }
                else{
                    System.out.println("Wrong email or password");
                }
            }
            return healthProfessional;
        }
        return null;
    }

    public static Patient makeUser (String email){
        Patient patient = null;
        String db_email, db_role, db_create, db_fname, db_lname, db_phone, db_region, db_province;
        int db_id;
        String responce = Auth_Access.getUsersByEmail(email);
        JSONObject obj = new JSONObject(responce);
        db_id = obj.getInt("id");
        db_create = obj.getString("create_time");
        db_email = obj.getString("email");
        db_role = obj.getString("user_role");
        db_fname = obj.getString("fname");
        db_lname = obj.getString("lname");
        db_phone = obj.getString("phone");
        db_region = obj.getString("region");
        db_province = obj.getString("province");
        Timestamp creattime = stringToTimestamp(db_create);
        patient = new Patient(db_fname, db_lname, db_email, creattime, db_role,getTimestampNow());
        patient.setId(db_id);
        patient.setPhone(db_phone);
        patient.setRegion(db_region);
        patient.setProvince(db_province);
        String records = Auth_Access.getUserHealthRecordByEmail(db_email);
        JSONArray str = new JSONArray(records);
        for (int i=0;i<str.length(); i++) {
            int rid, uid;
            String record, name;
            Timestamp create_time;
            JSONObject result = str.getJSONObject(i);
            create_time = stringToTimestamp(result.getString("create_time"));
            rid = result.getInt("rid");
            uid = result.getInt("uid");
            name = result.getString("name");
            record = result.getString("record");
            Record r = new Record(name, record, uid, create_time);
            r.setId(rid);
            patient.addRecord(r);
        }
        return patient;
    }

    public static HealthProfessional makeHealthProfessional (String email){
        HealthProfessional healthProfessional = null;
        String db_email, db_role, db_create, db_fname, db_lname, db_phone, db_region, db_organization, db_department, db_health;
        int db_id;
        String responce = Auth_Access.getHealthProfessionalUsersByEmail(email);
        JSONObject obj = new JSONObject(responce);
        db_id = obj.getInt("id");
        db_create = obj.getString("create_time");
        db_email = obj.getString("email");
        db_role = obj.getString("user_role");
        db_fname = obj.getString("fname");
        db_lname = obj.getString("lname");
        db_phone = obj.getString("phone");
        db_region = obj.getString("region");
        db_organization = obj.getString("organization");
        db_department = obj.getString("department");
        db_health = obj.getString("health_professional");
        Timestamp creattime = stringToTimestamp(db_create);
        healthProfessional = new HealthProfessional(db_fname, db_lname, db_email, creattime, db_role,getTimestampNow());
        healthProfessional.setId(db_id);
        healthProfessional.setPhone(db_phone);
        healthProfessional.setRegion(db_region);
        healthProfessional.setOrganization(db_organization);
        healthProfessional.setDepartment(db_department);
        healthProfessional.setHealthProfessional(db_health);
        return healthProfessional;
    }

    private static boolean checkString(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches("[a-z A-Z0-9+=*/^()_-]+"))
            return true;
        return false;
    }

    public static boolean updateUserRecord(String name, String description, int rid){
        boolean result = false;
        result = Auth_Access.updateRecord(name, description, rid);
        return result;
    }

    public static boolean insertIntoRecord(String name, String description, int uid){
        boolean result = false;
        result = Auth_Access.insertIntoRecord(name, description, uid);
        return result;
    }

    private static Timestamp stringToTimestamp(String string){
        try {
            DateFormat formatter;
            formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = (Date) formatter.parse(string);
            java.sql.Timestamp timeStampDate = new Timestamp(date.getTime());

            return timeStampDate;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static boolean checkEmail(String str){
        if(str.matches("^[A-Za-z0-9._%+-]{2,}@[A-Za-z0-9_-]{2,}.[A-Za-z.]{2,7}$"))
            return true;
        return false;
    }

    private static Timestamp getTimestampNow(){
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    public static boolean register(String fname, String lname, String email, String password, String re_password, String phone, String region, String province){
        boolean user = false;
        String error = "";
        try{
            boolean set = true;
            if(Auth_Access.userExists(email)) {
                set = false;
                error += "\tUsername already taken\n";
            }
            boolean checkFname = checkString(fname);
            boolean checkLname = checkString(lname);
            boolean checkPassword = password.equals(re_password);
            boolean checkEmail = checkEmail(email);
            if(checkEmail && checkPassword && checkFname && set && checkLname){
                if(Auth_Access.insertIntoUsers(fname.toUpperCase(), lname.toUpperCase(), email.toUpperCase(), password,  phone.toUpperCase(), region, province)) {
                    user = true;
                }
                else
                    error+="\tPatient Insertion failed\n";
            }else{
                if(!checkEmail)
                    error+="\tEmail is not valid\n";
                if(!checkFname)
                    error+="\tFirst name is not valid\n";
                if(!checkLname)
                    error+="\tLast name is not valid\n";
                if(!checkPassword)
                    error+="\tPassword's dont match\n";
            }
            if(!error.equals("")){
                throw new Exception(error);
            }
        }catch(Exception e){System.out.println(e);}
        return user;
    }

    public static boolean healthProfessionalRegister(String fname, String lname, String email, String password, String re_password, String phone, String region, String organization, String department, String health_professional){
        boolean user = false;
        String error = "";
        try{
            boolean set = true;
            if(Auth_Access.healthUserExists(email)) {
                set = false;
                error += "\tUse already taken\n";
            }
            boolean checkFname = checkString(fname);
            boolean checkLname = checkString(lname);
            boolean checkPassword = password.equals(re_password);
            boolean checkEmail = checkEmail(email);
            if(checkEmail && checkPassword && checkFname && set && checkLname){
                if(Auth_Access.insertIntoHealthProfessional(fname.toUpperCase(), lname.toUpperCase(), email.toUpperCase(), password,  phone.toUpperCase(), region, organization, department, health_professional)) {
                    user = true;
                }
                else
                    error+="\tPatient Insertion failed\n";
            }else{
                if(!checkEmail)
                    error+="\tEmail is not valid\n";
                if(!checkFname)
                    error+="\tFirst name is not valid\n";
                if(!checkLname)
                    error+="\tLast name is not valid\n";
                if(!checkPassword)
                    error+="\tPassword's dont match\n";
            }
            if(!error.equals("")){
                throw new Exception(error);
            }
        }catch(Exception e){System.out.println(e);}
        return user;
    }

    public static boolean updateUser(String name, String email, String phone, String region, String province){
        boolean result = false;
        String fname = name.split(" ")[0];
        String lname = name.split(" ")[1];
        result = Auth_Access.updateUser(fname,lname,email,phone,region,province);
        return result;
    }

    public static ArrayList<String> getRegions(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getRegions();
        JSONArray array = new JSONArray(responce);
        for(int i=0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            list.add(obj.getString("name"));
        }
        return list;
    }
    public static ArrayList<String> getProvinces(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getProvinces();
        JSONArray array = new JSONArray(responce);
        for(int i=0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            list.add(obj.getString("name"));
        }
        return list;
    }
    public static ArrayList<String> getOrganization(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getOrganization();
        JSONArray array = new JSONArray(responce);
        for(int i=0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            list.add(obj.getString("name"));
        }
        return list;
    }
    public static ArrayList<String> getDepartment(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getDepartment();
        JSONArray array = new JSONArray(responce);
        for(int i=0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            list.add(obj.getString("name"));
        }
        return list;
    }
    public static ArrayList<String> getHealthProfessional(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getHealthProfessional();
        JSONArray array = new JSONArray(responce);
        for(int i=0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            list.add(obj.getString("name"));
        }
        return list;
    }
}
