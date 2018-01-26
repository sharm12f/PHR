package phr.lib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import org.json.*;

public class Lib {

    public static void main(String args[]){
        System.out.println(login("app", "password"));
    }

    public static User login(String username, String password){
       User user = new User("app","app@app.com",getTimestampNow(),"USER",getTimestampNow());
       user.setId(1);
       user.setName("Application Test");
       user.setProvince("ON");
       user.setPhone("999-999-9999");
       user.setRegion("Windsor");
        /*
        boolean checkUsername = checkString(username);
        boolean checkPassword = checkString(password);
        User user = null;
        if (!checkUsername || !checkPassword)
            System.out.println("Nope");
        else {
            if(Auth_Access.isUser(username, password)) {
                String db_email, db_username, db_role, db_create, db_name, db_phone, db_region, db_province;
                int db_id;
                String responce = Auth_Access.getUsersByUsername(username);
                JSONObject obj = new JSONObject(responce);
                db_id = obj.getInt("id");
                db_create = obj.getString("create_time");
                db_email = obj.getString("email");
                db_username = obj.getString("username");
                db_role = obj.getString("user_role");
                db_name = obj.getString("name");
                db_phone = obj.getString("phone");
                db_region = obj.getString("region");
                db_province = obj.getString("province");
                Timestamp creattime = stringToTimestamp(db_create);
                user = new User(db_username, db_email, creattime, db_role,getTimestampNow());
                user.setId(db_id);
                user.setName(db_name);
                user.setPhone(db_phone);
                user.setRegion(db_region);
                user.setProvince(db_province);
                String records = Auth_Access.getUserHealthRecordByUsername(db_username);
                JSONArray str = new JSONArray(records);
                for (int i=0;i<str.length(); i++){
                    int rid, uid;
                    String cypertext_policy, cypertext_record, cypertext_record_ref;
                    Timestamp create_time;
                    JSONObject record = str.getJSONObject(i);
                    create_time = stringToTimestamp(record.getString("create_time"));
                    rid = record.getInt("rid");
                    uid = record.getInt("uid");
                    cypertext_policy = record.getString("cypertext_policy");
                    cypertext_record = record.getString("cypertext_record");
                    cypertext_record_ref = record.getString("cypertext_record_ref");
                    Record r = new Record(cypertext_policy,cypertext_record,cypertext_record_ref,uid,create_time);
                    r.setId(rid);
                    user.addRecord(r);
                }
            }
            else{
                System.out.println("Wrong username or password");
            }
        }

        */
        return user;
    }

    private static boolean checkString(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches("[a-zA-Z0-9+=*/^()_-]+"))
            return true;
        return false;
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

    public static boolean register(String username, String email, String password, String re_password, String user_role){
        boolean user = false;
        String error = "";
        try{
            boolean set = true;
            if(Auth_Access.userExists(username)) {
                set = false;
                error += "\tUsername already taken\n";
            }
            boolean checkUsername = checkString(username);
            boolean checkPassword = password.equals(re_password);
            boolean checkEmail = checkEmail(email);
            boolean checkUser_role = checkString(user_role);
            if(checkEmail && checkPassword && checkUser_role && checkUsername && set){
                if(Auth_Access.insertIntoUsers(username.toUpperCase(), email.toUpperCase(), password,  user_role.toUpperCase())) {
                    user = true;
                }
                else
                    error+="\tUser Insertion failed\n";
            }else{
                if(!checkEmail)
                    error+="\tEmail is not valid\n";
                if(!checkUsername)
                    error+="\tUsername is not valid\n";
                if(!checkPassword)
                    error+="\tPassword's dont match\n";
                if(!checkUser_role)
                    error+="\tUser_role is not valid\n";
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
                    Timestamp ts = getTimestampNow();
                    result = new Record(policy, record, record_ref, user_id, ts);
                    System.out.println("TS: " + ts);
                    Connection conn = Auth_Access.getConnection();
                    String t = ts.toString().substring(0,ts.toString().lastIndexOf("."));
                    ResultSet rs = Auth_Access.getUserHealthRecordIDByUseridAndTimestamp(conn,user_id,t);
                    while(rs.next())
                        result.setId(rs.getInt(1));
                    Auth_Access.closeConnection(conn);
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
}
