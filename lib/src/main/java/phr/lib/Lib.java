package phr.lib;

import com.sun.org.apache.regexp.internal.RE;

import java.security.spec.ECField;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.*;

public class Lib {
    public static User login(String email, String password){
        boolean checkUsername = checkEmail(email);
        boolean checkPassword = checkString(password);

        if(!checkPassword || !checkUsername){
            return null;
        }

        boolean isPatient = Auth_Access.userExists(email);
        boolean isPhysician = Auth_Access.healthUserExists(email);
        if(isPatient){
            Patient patient = null;
            if(Auth_Access.isUser(email, password)) {
                patient = makeUser(email);
            }
            else{
                return null;
            }
            return patient;
        }
        else if(isPhysician){
            HealthProfessional healthProfessional = null;
            if(Auth_Access.isHealthProfessional(email, password)) {
                healthProfessional = makeHealthProfessional(email);
            }
            else{
                return null;
            }
            return healthProfessional;
        }
        return null;
    }

    public static Patient makeUser (String email){

        boolean checkEmail = checkEmail(email);
        if(!checkEmail)
            return null;

        Patient patient = null;
        String db_email, db_role, db_create, db_name, db_phone, db_region, db_province;
        int db_id;
        String responce = Auth_Access.getUsersByEmail(email);
        if(responce=="error"){
            return null;
        }
        JSONObject obj = new JSONObject(responce);
        db_id = obj.getInt("id");
        db_create = obj.getString("create_time");
        db_email = obj.getString("email");
        db_role = obj.getString("user_role");
        db_name = obj.getString("name");
        db_phone = obj.getString("phone");
        db_region = obj.getString("region");
        db_province = obj.getString("province");
        Timestamp creattime = stringToTimestamp(db_create);
        patient = new Patient(db_name, db_email, creattime, db_role,getTimestampNow());
        patient.setId(db_id);
        patient.setPhone(db_phone);
        patient.setRegion(db_region);
        patient.setProvince(db_province);

        String records = Auth_Access.getUserHealthRecordByEmail(db_email);
        if(records=="error"){
            return null;
        }

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

        boolean checkEmail = checkEmail(email);
        if(!checkEmail)
            return null;

        HealthProfessional healthProfessional = null;
        String db_email, db_role, db_create, db_name, db_phone, db_region, db_organization, db_department, db_health;
        int db_id;
        String responce = Auth_Access.getHealthProfessionalUsersByEmail(email);
        if(responce=="error"){
            return null;
        }
        JSONObject obj = new JSONObject(responce);
        db_id = obj.getInt("id");
        db_create = obj.getString("create_time");
        db_email = obj.getString("email");
        db_role = obj.getString("user_role");
        db_name = obj.getString("name");
        db_phone = obj.getString("phone");
        db_region = obj.getString("region");
        db_organization = obj.getString("organization");
        db_department = obj.getString("department");
        db_health = obj.getString("health_professional");
        Timestamp creattime = stringToTimestamp(db_create);
        healthProfessional = new HealthProfessional(db_name, db_email, creattime, db_role,getTimestampNow());
        healthProfessional.setId(db_id);
        healthProfessional.setPhone(db_phone);
        healthProfessional.setRegion(db_region);
        healthProfessional.setOrganization(db_organization);
        healthProfessional.setDepartment(db_department);
        healthProfessional.setHealthProfessional(db_health);

        HashMap<Integer, ArrayList<Record>> healthProfessionalPatientRecordOnly = new HashMap<Integer, ArrayList<Record>>();
        String records = Auth_Access.getHealthProfessionalRecordsById(db_id);
        if(records=="error"){
            return null;
        }
        JSONArray str = new JSONArray(records);
        for (int i=0;i<str.length(); i++) {
            int rid;
            JSONObject result = str.getJSONObject(i);
            rid = result.getInt("rid");
            Record r = makeRecord(rid);
            if(healthProfessionalPatientRecordOnly.containsKey(r.getUser_id())) {
                ArrayList<Record> tmp = healthProfessionalPatientRecordOnly.get(r.getUser_id());
                tmp.add(r);
                healthProfessionalPatientRecordOnly.put(r.getUser_id(), tmp);
            }
            else{
                ArrayList<Record> tmp = new ArrayList<Record>();
                tmp.add(r);
                healthProfessionalPatientRecordOnly.put(r.getUser_id(), tmp);
            }
        }
        Iterator<Map.Entry<Integer, ArrayList<Record>>> it = healthProfessionalPatientRecordOnly.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry<Integer, ArrayList<Record>> entry = it.next();
            int uid = entry.getKey();
            ArrayList<Record> rcs = entry.getValue();
            Patient p = makeHealthProfessionalPatient(uid);
            p.setRecords(rcs);
            healthProfessional.addPatient(p);
        }
        return healthProfessional;
    }

    public static Patient makeHealthProfessionalPatient (int id){

        if(id < 0 )
            return null;

        Patient patient = null;
        String db_email, db_role, db_create, db_name, db_phone, db_region, db_province;
        int db_id;
        String responce = Auth_Access.getUsersById(id);
        if(responce=="error"){
            return null;
        }
        JSONObject obj = new JSONObject(responce);
        db_id = obj.getInt("id");
        db_create = obj.getString("create_time");
        db_email = obj.getString("email");
        db_role = obj.getString("user_role");
        db_name = obj.getString("name");
        db_phone = obj.getString("phone");
        db_region = obj.getString("region");
        db_province = obj.getString("province");
        Timestamp creattime = stringToTimestamp(db_create);
        patient = new Patient(db_name, db_email, creattime, db_role,getTimestampNow());
        patient.setId(db_id);
        patient.setPhone(db_phone);
        patient.setRegion(db_region);
        patient.setProvince(db_province);
        return patient;
    }

    public static Record makeRecord (int id){

        if(id < 0)
            return null;

        Record r = null;
        String records = Auth_Access.getUserHealthRecordById(id);
        if(records=="error") {
            return null;
        }
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
            r = new Record(name, record, uid, create_time);
            r.setId(rid);
        }
        return r;
    }

    public static boolean checkEmail(String str){
        if(str.matches("^[A-Za-z0-9._%+-]{2,}@[A-Za-z0-9_-]{2,}.[A-Za-z.]{2,7}$"))
            return true;
        return false;
    }

    public static boolean checkString(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches("[a-z A-Z0-9+=*/^()_-]+"))
            return true;
        return false;
    }
    public static boolean checkStringZero(String str){
        if(str.length() > 128)
            return false;
        if(str.matches("[a-z A-Z0-9+=*/^()_-]+"))
            return true;
        return false;
    }

    public static boolean checkPhone(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches("[a-z A-Z0-9+=*/^()_-]+"))
            return true;
        return false;
    }


    public static boolean PatientUpdateRecord(String name, String description, int rid){

        boolean checkName = checkString(name);
        boolean checkDescription = checkStringZero(description);
        if(rid < 0 || !checkDescription || !checkName)
            return false;
        return Auth_Access.updateRecord(name, description, rid);
    }

    public static boolean insertIntoRecord(String name, String description, int uid){
        boolean checkName = checkString(name);
        boolean checkDescription = checkStringZero(description);
        if(uid < 0 || !checkDescription || !checkName) {
            return false;
        }
        boolean result  = Auth_Access.insertIntoRecord(name, description, uid);
        System.out.println("the result in lib: " + result);
        return  result;
    }

    public static Timestamp stringToTimestamp(String string){
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

    public static Timestamp getTimestampNow(){
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    public static boolean PatientRegister(String name, String email, String password, String re_password, String phone, String region, String province){

        boolean checkName = checkString(name);
        boolean checkEmail = checkEmail(email);
        boolean checkPassword = checkString(password);
        boolean checkRePassword = checkString(re_password);
        boolean checkPhone = checkPhone(phone);
        boolean checkRegion = checkString(region);
        boolean checkProvince = checkString(province);

        boolean passwordMatch = false;
        if(checkPassword || checkRePassword)
            passwordMatch = password.equals(re_password);

        if(!checkName || !checkEmail || !passwordMatch || !checkPhone || !checkRegion || !checkProvince)
            return false;

        try{
            if(Auth_Access.userExists(email)) {
                return false;
            }
            if(Auth_Access.insertIntoUsers(name.toUpperCase(), email.toUpperCase(), password,  phone.toUpperCase(), region, province)) {
                return true;
            }
            else
                return false;
        }catch(Exception e){e.printStackTrace();}
        return false;
    }

    public static boolean healthProfessionalRegister(String name, String email, String password, String re_password, String phone, String region, String organization, String department, String health_professional){

        boolean checkName = checkString(name);
        boolean checkEmail = checkEmail(email);
        boolean checkPassword = checkString(password);
        boolean checkRePassword = checkString(re_password);
        boolean checkPhone = checkPhone(phone);
        boolean checkRegion = checkString(region);
        boolean checkOrganization = checkString(organization);
        boolean checkDepartment = checkString(department);
        boolean checkHealthProfessional = checkString(health_professional);

        boolean passwordMatch = false;
        if(checkPassword || checkRePassword)
            passwordMatch = password.equals(re_password);

        if(!checkName || !checkEmail || !passwordMatch || !checkPhone || !checkRegion || !checkOrganization || !checkDepartment || !checkHealthProfessional)
            return false;

        try{
            if(Auth_Access.healthUserExists(email))
                return false;
            if(Auth_Access.insertIntoHealthProfessional(name.toUpperCase(), email.toUpperCase(), password,  phone.toUpperCase(), region, organization, department, health_professional))
                return true;
            else
                return false;
        }catch(Exception e){e.printStackTrace();}
        return false;
    }

    public static boolean PatientUpdate(String name, String email, String phone, String region, String province, int id){
        boolean checkName = checkString(name);
        boolean checkEmail = checkEmail(email);
        boolean checkPhone = checkPhone(phone);
        boolean checkRegion = checkString(region);
        boolean checkProvince = checkString(province);

        if(!checkName || !checkEmail || !checkPhone || !checkRegion || !checkProvince || id < 0)
            return false;

        return Auth_Access.PatientUpdate(name,email,phone,region,province, id);

    }

    public static boolean HealthProfessionalUpdate(String name, String email, String phone, String region, int id){

        boolean checkName = checkString(name);
        boolean checkEmail = checkEmail(email);
        boolean checkPhone = checkPhone(phone);
        boolean checkRegion = checkString(region);


        if(!checkName || !checkEmail || !checkPhone || !checkRegion || id < 0)
            return false;

        return Auth_Access.HealthProfessionalUpdate(name,email,phone,region,id);

    }

    public static ArrayList<String> getRegions(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getRegions();
        if(responce=="error"){
            return null;
        }
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
        if(responce=="error"){
            return null;
        }
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
        if(responce=="error"){
            return null;
        }
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
        if(responce=="error"){
            return null;
        }
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
        if(responce=="error"){
            return null;
        }
        JSONArray array = new JSONArray(responce);
        for(int i=0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            list.add(obj.getString("name"));
        }
        return list;
    }
}
