package phr.lib;

import java.security.spec.ECField;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by Anupam on 28-Dec-17.
 */


public class Lib {

    public static void main(String args[]){
        /*
        try {
            Connection conn = Auth_Access.getConnection();
            Timestamp ts = Timestamp.valueOf("2017-12-30 12:57:45");
            ResultSet rs = Auth_Access.getUserHealthRecordIDByUseridAndTimestamp(conn, 3, ts);
            while (rs.next())
                System.out.println(rs.getInt(1));
            Auth_Access.closeConnection(conn);
        }catch(Exception e){}

        */
        System.out.println("Start -- ");
        System.out.println("Login");
        User user = login("Anu","what");
        if(user==null){System.out.println("No");}else{System.out.println("Yes");}
        System.out.println(user.toString());
        user.printRecords();
        System.out.println("Adding Record");
        Record rec = addRecord("user","painpills","pills",user.getId());
        if(rec==null){System.out.println("No");}else{System.out.println("Yes");user.addRecord(rec);}
        user.printRecords();
        System.out.println("-- End ");
    }

    /*
    * input: Username and Password of the user loggin in
    * output: a user object or null;
    * Note: the user object contains the users information (including all records) and other session information
    */
    public static User login(String username, String password){
        boolean checkUsername = checkString(username);
        boolean checkPassword = checkString(password);
        User user = null;
        if (!checkUsername || !checkPassword)
            System.out.println("Nope");
        else {
            if(Auth_Access.isUser(username, password)){
                String db_email, db_username, db_role;
                Timestamp db_create;
                int db_id;
                try{
                    Connection conn = Auth_Access.getConnection();
                    Hashtable hash = new Hashtable();
                    hash.put("user_name",username);
                    ResultSet rs = Auth_Access.getUsersByHash(conn,hash);
                    rs.next();
                    db_id = rs.getInt(1);
                    db_username = rs.getString(2);
                    db_email = rs.getString(3);
                    db_create = rs.getTimestamp(4);
                    db_role = rs.getString(5);
                    user = new User(db_username, db_email, db_create, db_role, getTimestampNow());
                    user.setId(db_id);
                    rs = Auth_Access.getUserHealthRecordByUsername(conn, user.getUsername());
                    LinkedList<Record> records = new LinkedList();
                    while(rs.next()){
                        int rec_id = rs.getInt(1);
                        int rec_user_id = rs.getInt(2);
                        String rec_policy = rs.getString(3);
                        String rec_record = rs.getString(4);
                        String rec_record_ref = rs.getString(5);
                        Timestamp rec_create = rs.getTimestamp(6);
                       Record rec = new Record(rec_policy, rec_record,rec_record_ref,rec_user_id,rec_create);
                       rec.setId(rec_id);
                       records.addLast(rec);
                    }
                    user.setRecords(records);
                    Auth_Access.closeConnection(conn);
                }catch(Exception e){System.out.println("Could not log in, no DB Connection" + e);}
            }
        }
        return user;
    }

    /*
    * input: String, this verifies if the string is valid, only contains specified chars, and ensure the string is not longer and 128 and not shorter than 1
    * output: boolean answer indicating true or false, true for it being valid
    * Note: is not avalible outside this class
    */
    private static boolean checkString(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches("[a-zA-Z0-9+=*/^()_-]+"))
            return true;
        return false;
    }

    /*
    * input:    string
    * output:   true or false if the string is a valid email address
    * Note: is not avalible outside this class
    */
    private static boolean checkEmail(String str){
        if(str.matches("^[A-Za-z0-9._%+-]{2,}@[A-Za-z0-9_-]{2,}.[A-Za-z.]{2,7}$"))
            return true;
        return false;
    }

    /*
    * input: none
    * output: Timestamp
    * Note: Gives the current timestamp, is not avalible outside this class
    */
    private static Timestamp getTimestampNow(){
        Date date = new Date();
        return new Timestamp(date.getTime());
    }

    /*
    * input: Username, email, password, re-password (re-entered password), user role
    * output: True if all the appropriate entries are made to the databases. False otherwise
    * Note:
    */
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
