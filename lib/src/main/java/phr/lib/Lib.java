package phr.lib;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.json.*;

public class Lib {

    public static void main(String args[]){
        System.out.println(login("app", "password"));
    }

    public static User login(String email, String password){
        /*
        User user = new User("Application Test","app@app.com",getTimestampNow(),"USER",getTimestampNow());
        user.setId(1);
        user.setProvince("ON");
        user.setPhone("999-999-9999");
        user.setRegion("Windsor");
       */
        boolean checkUsername = checkEmail(email);
        boolean checkPassword = checkString(password);
        User user = null;
        if (!checkUsername || !checkPassword)
            System.out.println("Nope");
        else {
            if(Auth_Access.isUser(email, password)) {
                user = makeUser(email);
            }
            else{
                System.out.println("Wrong email or password");
            }
        }
        return user;
    }

    public static User makeUser (String email){
        User user = null;
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
        user = new User(db_fname, db_lname, db_email, creattime, db_role,getTimestampNow());
        user.setId(db_id);
        user.setPhone(db_phone);
        user.setRegion(db_region);
        user.setProvince(db_province);
        String records = Auth_Access.getUserHealthRecordByEmail(db_email);
        JSONArray str = new JSONArray(records);
        for (int i=0;i<str.length(); i++) {
            int rid, uid;
            String cypertext_policy, cypertext_record, cypertext_record_ref, name;
            Timestamp create_time;
            JSONObject record = str.getJSONObject(i);
            create_time = stringToTimestamp(record.getString("create_time"));
            rid = record.getInt("rid");
            uid = record.getInt("uid");
            name = record.getString("name");
            cypertext_policy = record.getString("cypertext_policy");
            cypertext_record = record.getString("cypertext_record");
            cypertext_record_ref = record.getString("cypertext_record_ref");
            Record r = new Record(name, cypertext_policy, cypertext_record, cypertext_record_ref, uid, create_time);
            r.setId(rid);
            user.addRecord(r);
        }
        return user;
    }

    private static boolean checkString(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches("[a-z A-Z0-9+=*/^()_-]+"))
            return true;
        return false;
    }

    public ArrayList<Record> getRecordByEmail(String email){
        ArrayList<Record> list = new ArrayList<Record>();
        String records = Auth_Access.getUserHealthRecordByEmail(email);
        JSONArray str = new JSONArray(records);
        for (int i=0;i<str.length(); i++) {
            int rid, uid;
            String cypertext_policy, cypertext_record, cypertext_record_ref, name;
            Timestamp create_time;
            JSONObject record = str.getJSONObject(i);
            create_time = stringToTimestamp(record.getString("create_time"));
            rid = record.getInt("rid");
            uid = record.getInt("uid");
            name = record.getString("name");
            cypertext_policy = record.getString("cypertext_policy");
            cypertext_record = record.getString("cypertext_record");
            cypertext_record_ref = record.getString("cypertext_record_ref");
            Record r = new Record(name, cypertext_policy, cypertext_record, cypertext_record_ref, uid, create_time);
            r.setId(rid);
            list.add(r);
        }
        return list;
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
                    error+="\tUser Insertion failed\n";
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

    public static boolean deleteAccount(String username, String password){
        if(Auth_Access.userExists(username)) {
            if (Auth_Access.isUser(username, password)) {
                if (Auth_Access.deleteUser(username))
                    return true;
                else
                    return false;
            } else {
                return false;
            }
        }
        else
            return false;
    }

    public static Record addRecord(String policy, String record, String record_ref, int user_id) {
        Record result = null;
        boolean checkPolicy = checkString(policy);
        boolean checkRecord = checkString(record);
        boolean checkRecordRef = checkString(record_ref);
        String error = "";
        try {
            if (checkPolicy && checkRecord && checkRecordRef) {
                if (Auth_Access.insertIntoRecord(policy, record, record_ref, user_id)) {

                }
            }else {
                if (!checkPolicy)
                    error += "\tPolicy not valid\n";
                if (!checkRecord)
                    error += "\tRecord not valid\n";
                if (!checkRecordRef)
                    error += "\tRecord Ref not valid\n";
            }
            if (!error.equals("")) {
                throw new Exception(error);
            }
        }catch (Exception e){System.out.println(e);}
        return result;
    }

    public static boolean deleteRecord(int id){
        return Auth_Access.deleteUserRecord(id);
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
}
