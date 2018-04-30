package phr.phr;

import java.io.File;
import java.io.FileOutputStream;
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
import android.os.*;

import phr.lib.Auth_Access;
import phr.lib.HealthProfessional;
import phr.lib.Note;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.RecordPermission;
import phr.lib.User;

/**
 * Created by Anupam on 28-Mar-18.
 *
 * This is the main library for the app. Logic is the only thing done in activity where as input sanatization, and other security measures are taken here
 *
 * This is the meat of the app (relies on the  auth access to retrieve the data)
 *
 * Async task needs to be used when calling a function in this library that calls auth access.
 *      EX: the login function calls auth access multiple times
 *          so when an activity calls login it needs to be done from a async task. (a loading progress dialog is shown until the task is complete)
 */

public class Lib {

    //private final varibles used within the library

    //used to ensure all names meet requirement
    private static final String REGXNAME = "[a-z 0-9A-Z-]+";
    //things such as descriptions use this to meet requirement
    private static final String REGXOTHER = "[a-z A-Z0-9+=*/^():\\s\\S_-]+";
    //validate phone numbers
    private static final String REGXPHONE = "[0-9-]+";

    //used to set the session timeout limit
    //private static final long SESSION_TIMEOUT = 120000;
    private static final long SESSION_TIMEOUT = 5000;

    public static final String[] IMAGE_EXT = {"jpg","jpeg","png"};
    public static final String[] FILE_EXT = {"doc","docx","pdf"};



    // creates and returns a user object if login is success full given the correct email and password for the user
    //(can log in both patients and health professionals)
    public static User login(String email, String password){
        boolean checkUsername = checkStringEmail(email);
        boolean checkPassword = checkStringName(password);

        if(!checkPassword || !checkUsername){
            return null;
        }

        boolean isPatient = Auth_Access.userExists(email);
        boolean isPhysician = Auth_Access.healthUserExists(email);
        if(isPatient){
            Patient patient = null;
            if(Auth_Access.isUser(email, password)) {
                patient = makeUser(email);
                Timestamp login = getTimestampNow();
                Auth_Access.setLoginUser(patient.getId(),login);
                patient.setLogin(login);
                patient.setLogout(getLogoutUser(patient.getId()));
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
                Timestamp login = getTimestampNow();
                Auth_Access.setLoginHealthProfessional(healthProfessional.getId(),login);
                healthProfessional.setLogin(login);
                healthProfessional.setLogout(getLogoutHealthProfessional(healthProfessional.getId()));
            }
            else{
                return null;
            }
            return healthProfessional;
        }
        return null;
    }
    //Creates and returns Patient object given the email
    public static Patient makeUser (String email){
        boolean checkEmail = checkStringEmail(email);
        if(!checkEmail)
            return null;

        Patient patient = null;
        String db_email, db_role, db_create, db_name, db_phone, db_region, db_province;
        int db_id;
        String responce = Auth_Access.getUsersByEmail(email);
        if(responce=="error"){
            return null;
        }
        try {
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
            patient = new Patient(db_name, db_email, creattime, db_role);
            patient.setId(db_id);
            patient.setPhone(db_phone);
            patient.setRegion(db_region);
            patient.setProvince(db_province);
            patient.setRecords(getPatientRecords(db_email));
            ArrayList<Note> notes = makePatientNotes(patient.getId());
            patient.setNotes(notes);
        }catch (Exception e){e.printStackTrace();}
        return patient;
    }
    //Creates and returns Health Professional object given the email
    public static HealthProfessional makeHealthProfessional (String email){

        boolean checkEmail = checkStringEmail(email);
        if(!checkEmail)
            return null;

        HealthProfessional healthProfessional = null;
        String db_email, db_role, db_create, db_name, db_phone, db_region,db_province, db_organization, db_department, db_health;
        int db_id;
        String responce = Auth_Access.getHealthProfessionalUsersByEmail(email);
        if(responce=="error"){
            return null;
        }
        try{
            JSONObject obj = new JSONObject(responce);
            db_id = obj.getInt("id");
            db_create = obj.getString("create_time");
            db_email = obj.getString("email");
            db_role = obj.getString("user_role");
            db_name = obj.getString("name");
            db_phone = obj.getString("phone");
            db_region = obj.getString("region");
            db_province = obj.getString("province");
            db_organization = obj.getString("organization");
            db_department = obj.getString("department");
            db_health = obj.getString("health_professional");
            Timestamp creattime = stringToTimestamp(db_create);
            healthProfessional = new HealthProfessional(db_name, db_email, creattime, db_role);
            healthProfessional.setId(db_id);
            healthProfessional.setPhone(db_phone);
            healthProfessional.setRegion(db_region);
            healthProfessional.setProvince(db_province);
            healthProfessional.setOrganization(db_organization);
            healthProfessional.setDepartment(db_department);
            healthProfessional.setHealthProfessional(db_health);

            healthProfessional.setPatient(makeHealthProfessionalPatientsList(db_id));
        }catch (Exception e){e.printStackTrace();}
        return healthProfessional;
    }
    //Creates and returns a list of patients given the known unique health professional id. These patients have given access to the health professional to one or more records.
    public static ArrayList<Patient> makeHealthProfessionalPatientsList(int id){
        ArrayList<Patient> list = new ArrayList<>();
        HashMap<Integer, ArrayList<Record>> healthProfessionalPatientRecordOnly = new HashMap<Integer, ArrayList<Record>>();
        String records = Auth_Access.getHealthProfessionalRecordsById(id);
        if(records=="error"){
            return null;
        }
        try{
            JSONArray str = new JSONArray(records);
            for (int i=0;i<str.length(); i++) {
                int rid;
                JSONObject result = str.getJSONObject(i);
                rid = result.getInt("rid");
                Record r = makeRecordByRecordId(rid);
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
                list.add(p);
            }
        }catch (Exception e){e.printStackTrace();}
        return list;
    }
    //Creates and returns a patient given the known unique user id. This does not contain the records of the patient, just patient information, the records are populated when the health professional is made.
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
        try{
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
            patient = new Patient(db_name, db_email, creattime, db_role);
            patient.setId(db_id);
            patient.setPhone(db_phone);
            patient.setRegion(db_region);
            patient.setProvince(db_province);
        }catch (Exception e){e.printStackTrace();}
        return patient;
    }
    //creates and returns a list of notes address to a patient given the known unique user id.
    public static ArrayList<Note> makePatientNotes(int patient_id){
        ArrayList<Note> notes = new ArrayList<>();
        String responce = Auth_Access.getNotesForPatient(patient_id);
        if(responce.equals(""))
            return null;
        try{
            JSONArray str = new JSONArray(responce);
            for (int i=0;i<str.length(); i++) {
                int id, uid, hpid;
                String name, desc, hpname;
                Timestamp create;
                JSONObject result = str.getJSONObject(i);
                id = result.getInt("id");
                create = stringToTimestamp(result.getString("create_time"));
                uid = result.getInt("user_id");
                hpid = result.getInt("health_professional_id");
                name = result.getString("name");
                desc = result.getString("description");
                hpname = result.getString("health_professional_name");
                Note n = new Note(id,uid,hpid,name,desc,hpname);
                n.setCreateTime(create);
                notes.add(n);
            }
        }catch (Exception e){e.printStackTrace();}
        return notes;
    }
    //creates and returns a record given the known and unique record id.
    public static Record makeRecordByRecordId(int id){

        if(id < 0)
            return null;

        Record r = null;
        String records = Auth_Access.getUserHealthRecordById(id);
        if(records=="error") {
            return null;
        }
        try{
            JSONArray str = new JSONArray(records);
            for (int i=0;i<str.length(); i++) {
                int rid, uid;
                String record, name, filename;
                Timestamp create_time;
                JSONObject result = str.getJSONObject(i);
                create_time = stringToTimestamp(result.getString("create_time"));
                rid = result.getInt("rid");
                uid = result.getInt("uid");
                name = result.getString("name");
                record = result.getString("record");
                filename = result.getString("filename");
                r = new Record(name, record, uid, create_time);
                r.setFilename(filename);
                r.setId(rid);
            }
        }catch (Exception e){e.printStackTrace();}
        return r;
    }
    //checks and returns true if the email string meets requirements
    public static boolean checkStringEmail(String str){
        if(str.matches("^[A-Za-z0-9._%+-]{2,}@[A-Za-z0-9_-]{2,}.[A-Za-z.]{2,7}$"))
            return true;
        return false;
    }
    //checks and returns true if the string meets requirements
    public static boolean checkStringName(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches(REGXNAME))
            return true;
        return false;
    }
    //checks and returns true if the other string meets requirements
    public static boolean checkStringZero(String str){
        if(str.matches(REGXOTHER) || str.length() == 0)
            return true;
        return false;
    }
    //checks and returns true if the phone string meets requirements
    public static boolean checkPhone(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches(REGXPHONE))
            return true;
        return false;
    }
    //updates the users record given the known unique record id.
    public static boolean PatientUpdateRecord(String name, String description, int rid){
        boolean checkName = checkStringName(name);
        boolean checkDescription = checkStringZero(description);
        if(rid < 0 || !checkDescription || !checkName) {
            return false;
        }
        return Auth_Access.updateRecord(name, description, rid);
    }
    //updates the users record given the known unique record id also adds filename if one is attached.
    public static boolean PatientUpdateRecord(String name, String description, int rid, String fileName){
        boolean checkName = checkStringName(name);
        boolean checkDescription = checkStringZero(description);
        if(rid < 0 || !checkDescription || !checkName) {
            return false;
        }
        return Auth_Access.updateRecord(name, description, rid, fileName);
    }
    //deletes a record given its known unique record id
    public static boolean deleteRecord(int id){
        if(id < 0){
            return false;
        }
        return Auth_Access.deleteRecord(id);
    }
    //creates and returns a list of record permissions given the known unique user id
    public static ArrayList<RecordPermission> getRecordPerms(int id){
        ArrayList<RecordPermission> result = new ArrayList<RecordPermission>();
        if(id < 0){
            return null;
        }
        String responce = Auth_Access.getRecordPerms(id);
        if(responce.equals(""))
            return null;
        try{
            JSONArray array = new JSONArray(responce);
            for(int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                int r_id, hp_id, pid;
                String r_name, hp_name;
                Timestamp create = stringToTimestamp(obj.getString("create_time"));
                r_id = Integer.parseInt(obj.getString("rid"));
                hp_id = Integer.parseInt(obj.getString("hpid"));
                pid = Integer.parseInt(obj.getString("pid"));
                r_name = obj.getString("rname");
                hp_name = obj.getString("hpname");
                RecordPermission perm = new RecordPermission(hp_id, hp_name,r_id,r_name,pid);
                perm.setCreate(create);
                result.add(perm);
            }
        }catch (Exception e){e.printStackTrace();}
        return result;
    }
    //creates and returns a list of health professionals based on the search parameters
    public static ArrayList<HealthProfessional> searchHealthProfessionals(String region, String province, String organization, String department, String healthProfessional){
        ArrayList<HealthProfessional> healthProfessionalArrayList = new ArrayList<>();

        String responce = Auth_Access.searchHealthProfessionals(region, province, organization, department, healthProfessional);

        if(responce == null){
            healthProfessionalArrayList = null;
        }
        else{
            try{
                JSONArray str = new JSONArray(responce);
                for (int i=0;i<str.length(); i++) {
                    String email;
                    JSONObject result = str.getJSONObject(i);
                    email = result.getString("email");
                    HealthProfessional hp = Lib.makeHealthProfessional(email);
                    healthProfessionalArrayList.add(hp);
                }
            }catch (Exception e){e.printStackTrace();}
        }
        return healthProfessionalArrayList;
    }
    //gives permission to a health professional.
    public static boolean givePermission(RecordPermission recordPermission){
        boolean result= false;
        result = Auth_Access.givePermission(recordPermission.getHp_id(),recordPermission.getR_id());
        return result;
    }
    //checks if the permissions already exist in the database
    public static boolean permsExist(RecordPermission recordPermission){
        boolean result= false;
        result = Auth_Access.permissionsExist(recordPermission.getHp_id(),recordPermission.getR_id());
        return result;
    }
    //revokes the permission of a health professional
    public static boolean revokePermission(RecordPermission recordPermission){
        boolean result= false;
        result = Auth_Access.revokePermission(recordPermission.getId());
        return result;
    }
    //creates and returns a list of records given a user's email
    public static ArrayList<Record> getPatientRecords (String email){
        ArrayList<Record> records = null;
        boolean checkEmail = checkStringEmail(email);
        if(!checkEmail)
            return records;

        String responce = Auth_Access.getUserHealthRecordByEmail(email);
        if(responce=="error"){
            return null;
        }
        records = new ArrayList<>();
        try{
            JSONArray str = new JSONArray(responce);
            for (int i=0;i<str.length(); i++) {
                int rid, uid;
                String record, name, filename;
                Timestamp create_time;
                JSONObject result = str.getJSONObject(i);
                create_time = stringToTimestamp(result.getString("create_time"));
                rid = result.getInt("rid");
                uid = result.getInt("uid");
                name = result.getString("name");
                record = result.getString("record");
                filename = result.getString("filename");

                /*
                if(!filename.equals("null")){
                    getFile(email,filename);
                }
                */

                Record r = new Record(name, record, uid, create_time);
                r.setFilename(filename);
                r.setId(rid);
                records.add(r);
            }
        }catch (Exception e){e.printStackTrace();}
        return records;
    }
    //inserts into records a new record given the known unique user id
    public static boolean insertIntoRecord(String name, String description, int uid){
        boolean checkName = checkStringName(name);
        boolean checkDescription = checkStringZero(description);
        if(uid < 0 || !checkDescription || !checkName) {
            return false;
        }
        boolean result  = Auth_Access.insertIntoRecord(name, description, uid);
        return  result;
    }
    //inserts into records a new record given the known unique user id, also adds the file name if one is attached
    public static boolean insertIntoRecord(String name, String description, int uid, String fileName){
        boolean checkName = checkStringName(name);
        boolean checkDescription = checkStringZero(description);
        if(uid < 0 || !checkDescription || !checkName) {
            return false;
        }
        boolean result  = Auth_Access.insertIntoRecord(name, description, uid, fileName);
        return  result;
    }
    //is used to convers the db time stamp into time stamp since it is returned as a sting
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
    //creates the current time stamp
    public static Timestamp getTimestampNow(){
        return stringToTimestamp(Auth_Access.getServerTime());
    }
    //registers a new patient
    public static boolean PatientRegister(String name, String email, String password, String re_password, String phone, String region, String province){

        boolean checkName = checkStringName(name);
        boolean checkEmail = checkStringEmail(email);
        boolean checkPassword = checkStringName(password);
        boolean checkRePassword = checkStringName(re_password);
        boolean checkPhone = checkPhone(phone);
        boolean checkRegion = checkStringName(region);
        boolean checkProvince = checkStringName(province);

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
    //registers a new health professional
    public static boolean healthProfessionalRegister(String name, String email, String password, String re_password, String phone, String region, String province, String organization, String department, String health_professional){

        boolean checkName = checkStringName(name);
        boolean checkEmail = checkStringEmail(email);
        boolean checkPassword = checkStringName(password);
        boolean checkRePassword = checkStringName(re_password);
        boolean checkPhone = checkPhone(phone);
        boolean checkRegion = checkStringName(region);
        boolean checkProvince = checkStringName(province);
        boolean checkOrganization = checkStringName(organization);
        boolean checkDepartment = checkStringName(department);
        boolean checkHealthProfessional = checkStringName(health_professional);

        boolean passwordMatch = false;
        if(checkPassword || checkRePassword)
            passwordMatch = password.equals(re_password);

        if(!checkName || !checkEmail || !passwordMatch || !checkPhone || !checkRegion || !checkProvince || !checkOrganization || !checkDepartment || !checkHealthProfessional)
            return false;

        try{
            if(Auth_Access.healthUserExists(email))
                return false;
            if(Auth_Access.insertIntoHealthProfessional(name.toUpperCase(), email.toUpperCase(), password,  phone.toUpperCase(), region, province, organization, department, health_professional))
                return true;
            else
                return false;
        }catch(Exception e){e.printStackTrace();}
        return false;
    }
    //allows the user to update this account information given the know unique user id.
    public static boolean PatientUpdate(String name, String email, String phone, String region, String province, int id){
        boolean checkName = checkStringName(name);
        boolean checkEmail = checkStringEmail(email);
        boolean checkPhone = checkPhone(phone);
        boolean checkRegion = checkStringName(region);
        boolean checkProvince = checkStringName(province);

        if(!checkName || !checkEmail || !checkPhone || !checkRegion || !checkProvince || id < 0)
            return false;

        return Auth_Access.PatientUpdate(name,email,phone,region,province, id);
    }
    //allows the health professional to leave a note for a user given the known unique user and health professional id's
    public static boolean HealthProfessionalLeaveNote(String name, String description, int uid, int hpid){
        boolean responce = false;
        boolean checkName = checkStringName(name);
        boolean checkDesc = checkStringZero(description);
        if(!checkName || !checkDesc || uid < 0 || hpid < 0) {
            return false;
        }
        responce = Auth_Access.insertIntoNotes(name,description,uid,hpid);
        return responce;
    }
    // allows the health professional to update their account information given the known unique health professional id
    public static boolean HealthProfessionalUpdate(String name, String email, String phone, String region, String province, String organization, String department, String healthprofessinalProfession, int id){

        boolean checkName = checkStringName(name);
        boolean checkEmail = checkStringEmail(email);
        boolean checkPhone = checkPhone(phone);

        if(!checkName || !checkEmail || !checkPhone || id < 0)
            return false;

        return Auth_Access.HealthProfessionalUpdate(name,email,phone,region,province,organization,department,healthprofessinalProfession,id);

    }
    //gets the login time for a user given the known unique user id
    public static Timestamp getLoginUser(int id){
        Timestamp login = null;
        String responce = Auth_Access.getLoginLogoutUser(id);
        if(!responce.equals("")){
            try{
                JSONArray array = new JSONArray(responce);
                for(int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    login = stringToTimestamp(obj.getString("login"));
                }
            }catch (Exception e){e.printStackTrace();}
        }
        return login;
    }
    //gets the logout time for a user given the known unique user id
    public static Timestamp getLogoutUser(int id){
        Timestamp logout = null;
        String responce = Auth_Access.getLoginLogoutUser(id);
        if(!responce.equals("")){
            try{
                JSONArray array = new JSONArray(responce);
                for(int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    logout = stringToTimestamp(obj.getString("logout"));
                }
            }catch (Exception e){e.printStackTrace();}
        }
        return logout;
    }
    //gets the login time for a user given the known unique health professional id
    public static Timestamp getLoginHealthProfessional(int id){
        Timestamp login = null;
        String responce = Auth_Access.getLoginLogoutHealthProfessional(id);
        if(!responce.equals("")){
            try{
                JSONArray array = new JSONArray(responce);
                for(int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    login = stringToTimestamp(obj.getString("login"));
                }
            }catch (Exception e){e.printStackTrace();}
        }
        return login;
    }
    //gets the logout time for a user given the known unique health professional id
    public static Timestamp getLogoutHealthProfessional(int id){
        Timestamp logout = null;
        String responce = Auth_Access.getLoginLogoutHealthProfessional(id);
        if(!responce.equals("")){
            try{
                JSONArray array = new JSONArray(responce);
                for(int i=0; i<array.length(); i++){
                    JSONObject obj = array.getJSONObject(i);
                    logout = stringToTimestamp(obj.getString("logout"));
                }
            }catch (Exception e){e.printStackTrace();}
        }
        return logout;
    }
    //returns true if the session should time out given the session start time
    public static boolean timeOut(Timestamp session){
        boolean result = false;
        Timestamp now = getTimestampNow();
        long diff = now.getTime() - session.getTime();
        if(diff > SESSION_TIMEOUT){
            result = true;
        }
        return result;
    }


    public static boolean sendFile(File file, User user){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("user_name",user.getEmail());
        String responce = Auth_Access.sendFile(file,postData);
        if (responce.equals("complete")){
            result = true;
        }
        return result;
    }

    public static void getFile(String email, String fileName){
        try {
            String path = "/PHR_AUTH/uploads/"+email+"/"+fileName;
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/phr_saved_data");
            myDir.mkdirs();
            File file = new File(myDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            Auth_Access.getFile(path, fos);
            fos.flush();
            fos.close();
        }catch (Exception e){e.printStackTrace();}
    }

    //the following return a list of their respective attributes.
    public static ArrayList<String> getRegions(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getRegions();
        if(responce=="error"){
            return null;
        }
        try{
            JSONArray array = new JSONArray(responce);
            for(int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                list.add(obj.getString("name"));
            }
        }catch (Exception e){e.printStackTrace();}
        return list;
    }
    public static ArrayList<String> getProvinces(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getProvinces();
        if(responce=="error"){
            return null;
        }
        try{
            JSONArray array = new JSONArray(responce);
            for(int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                list.add(obj.getString("name"));
            }
        }catch (Exception e){e.printStackTrace();}
        return list;
    }
    public static ArrayList<String> getOrganization(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getOrganization();
        if(responce=="error"){
            return null;
        }
        try{
            JSONArray array = new JSONArray(responce);
            for(int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                list.add(obj.getString("name"));
            }
        }catch (Exception e){e.printStackTrace();}
        return list;
    }
    public static ArrayList<String> getDepartment(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getDepartment();
        if(responce=="error"){
            return null;
        }
        try{
            JSONArray array = new JSONArray(responce);
            for(int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                list.add(obj.getString("name"));
            }
        }catch (Exception e){e.printStackTrace();}
        return list;
    }
    public static ArrayList<String> getHealthProfessional(){
        ArrayList<String> list = new ArrayList<String>();
        String responce = Auth_Access.getHealthProfessional();
        if(responce=="error"){
            return null;
        }
        try{
            JSONArray array = new JSONArray(responce);
            for(int i=0;i<array.length();i++){
                JSONObject obj = array.getJSONObject(i);
                list.add(obj.getString("name"));
            }
        }catch (Exception e){e.printStackTrace();}
        return list;
    }
}
