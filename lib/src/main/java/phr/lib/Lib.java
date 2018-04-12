package phr.lib;

import com.sun.org.apache.regexp.internal.RE;

import java.awt.image.AreaAveragingScaleFilter;
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

import javax.xml.bind.util.JAXBSource;

public class Lib {

    private static String REGXNAME = "[a-z 0-9A-Z-]+";
    private static String REGXOTHER = "[a-z A-Z0-9+=*/^():\\s\\S_-]+";
    private static String REGXPHONE = "[0-9-]+";
    //private static long SESSION_TIMEOUT = 120000;
    private static long SESSION_TIMEOUT = 5000;

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
                Timestamp login = getTimestampNow();
                Auth_Access.setLoginUser(patient.getId(),login);
                patient.setLogin(login);
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
        patient = new Patient(db_name, db_email, creattime, db_role);
        patient.setId(db_id);
        patient.setPhone(db_phone);
        patient.setRegion(db_region);
        patient.setProvince(db_province);


        patient.setRecords(getPatientRecords(db_email));


        ArrayList<Note> notes = makePatientNotes(patient.getId());
        patient.setNotes(notes);

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
        healthProfessional = new HealthProfessional(db_name, db_email, creattime, db_role);
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

    public static ArrayList<Note> makePatientNotes(int patient_id){
        ArrayList<Note> notes = new ArrayList<>();
        String responce = Auth_Access.getNotesForPatient(patient_id);
        if(responce.equals(""))
            return null;
        System.out.println(responce);
        JSONArray str = new JSONArray(responce);
        for (int i=0;i<str.length(); i++) {
            int id, uid, hpid;
            String name, desc, hpname;
            JSONObject result = str.getJSONObject(i);
            id = result.getInt("id");
            uid = result.getInt("user_id");
            hpid = result.getInt("health_professional_id");
            name = result.getString("name");
            desc = result.getString("description");
            hpname = result.getString("health_professional_name");
            Note n = new Note(id,uid,hpid,name,desc,hpname);
            notes.add(n);
        }
        return notes;
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
        patient = new Patient(db_name, db_email, creattime, db_role);
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
        if(str.matches(REGXNAME))
            return true;
        return false;
    }

    public static boolean checkStringZero(String str){
        if(str.matches(REGXOTHER) || str.length() == 0)
            return true;
        return false;
    }

    public static boolean checkPhone(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches(REGXPHONE))
            return true;
        return false;
    }

    public static boolean PatientUpdateRecord(String name, String description, int rid){
        boolean checkName = checkString(name);
        boolean checkDescription = checkStringZero(description);
        if(rid < 0 || !checkDescription || !checkName) {
            return false;
        }
        return Auth_Access.updateRecord(name, description, rid);
    }

    public static boolean deleteRecord(int id){
        if(id < 0){
            return false;
        }
        return Auth_Access.deleteRecord(id);
    }

    public static ArrayList<RecordPermission> getRecordPerms(int id){
        ArrayList<RecordPermission> result = new ArrayList<RecordPermission>();
        if(id < 0){
            return null;
        }
        String responce = Auth_Access.getRecordPerms(id);
        if(responce.equals(""))
            return null;
        JSONArray array = new JSONArray(responce);
        for(int i=0;i<array.length();i++){
            JSONObject obj = array.getJSONObject(i);
            int r_id, hp_id, pid;
            String r_name, hp_name;
            r_id = Integer.parseInt(obj.getString("rid"));
            hp_id = Integer.parseInt(obj.getString("hpid"));
            pid = Integer.parseInt(obj.getString("pid"));
            r_name = obj.getString("rname");
            hp_name = obj.getString("hpname");
            RecordPermission perm = new RecordPermission(hp_id, hp_name,r_id,r_name,pid);
            result.add(perm);
        }

        return result;
    }

    public static ArrayList<HealthProfessional> searchHealthProfessionals(String region, String organization, String department, String healthProfessional){
        ArrayList<HealthProfessional> healthProfessionalArrayList = new ArrayList<>();

        String responce = Auth_Access.searchHealthProfessionals(region, organization, department, healthProfessional);

        if(responce == null){
            healthProfessionalArrayList = null;
        }
        else{
            JSONArray str = new JSONArray(responce);
            for (int i=0;i<str.length(); i++) {
                String email;
                JSONObject result = str.getJSONObject(i);
                email = result.getString("email");
                HealthProfessional hp = Lib.makeHealthProfessional(email);
                healthProfessionalArrayList.add(hp);
            }
        }
        return healthProfessionalArrayList;
    }

    public static boolean givePermission(RecordPermission recordPermission){
        boolean result= false;
        result = Auth_Access.givePermission(recordPermission.getHp_id(),recordPermission.getR_id());
        return result;
    }

    public static boolean permsExist(RecordPermission recordPermission){
        boolean result= false;
        result = Auth_Access.permissionsExist(recordPermission.getHp_id(),recordPermission.getR_id());
        return result;
    }

    public static boolean revokePermission(RecordPermission recordPermission){
        boolean result= false;
        result = Auth_Access.revokePermission(recordPermission.getId());
        return result;
    }

    public static ArrayList<Record> getPatientRecords (String email){
        ArrayList<Record> records = null;
        boolean checkEmail = checkEmail(email);
        if(!checkEmail)
            return records;

        String responce = Auth_Access.getUserHealthRecordByEmail(email);
        if(responce=="error"){
            return null;
        }
        records = new ArrayList<>();
        JSONArray str = new JSONArray(responce);
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
            records.add(r);
        }

        return records;
    }

    public static boolean insertIntoRecord(String name, String description, int uid){
        boolean checkName = checkString(name);
        boolean checkDescription = checkStringZero(description);
        if(uid < 0 || !checkDescription || !checkName) {
            return false;
        }
        boolean result  = Auth_Access.insertIntoRecord(name, description, uid);
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

    public static boolean HealthProfessionalLeaveNote(String name, String description, int uid, int hpid){
        boolean responce = false;
        boolean checkName = checkString(name);
        boolean checkDesc = checkStringZero(description);
        if(!checkName || !checkDesc || uid < 0 || hpid < 0) {
            System.out.println("Not pass");
            return false;
        }
        responce = Auth_Access.insertIntoNotes(name,description,uid,hpid);
        return responce;
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

    public static Timestamp getLoginUser(int id){
        Timestamp login = null;
        String responce = Auth_Access.getLoginLogoutUser(id);
        if(!responce.equals("")){
            JSONArray array = new JSONArray(responce);
            for(int i=0; i<array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                login = stringToTimestamp(obj.getString("login"));
            }
        }
        return login;
    }

    public static Timestamp getLoginHealthProfessional(int id){
        Timestamp login = null;
        String responce = Auth_Access.getLoginLogoutHealthProfessional(id);
        if(!responce.equals("")){
            JSONArray array = new JSONArray(responce);
            for(int i=0; i<array.length(); i++){
                JSONObject obj = array.getJSONObject(i);
                login = stringToTimestamp(obj.getString("login"));
            }
        }
        return login;
    }

    public static boolean timeOut(Timestamp login){
        boolean result = false;
        Timestamp now = getTimestampNow();
        long diff = now.getTime() - login.getTime();
        if(diff > SESSION_TIMEOUT){
            result = true;
        }
        return result;
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
