package phr.lib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.LinkedList;

/**
 * Created by Anupam on 28-Dec-17.
 */


public class Lib {

    public static void main(String args[]){
        //System.out.println(register ("boss2","boss2_shit@boss.com","BOSS","BOSS","user"));
        //System.out.println(deleteAccount("boss2", "BOSS"));
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
                    user = new User(db_username, db_email, db_create, db_role, db_id, getTimestampNow());
                    rs = Auth_Access.getUserHealthRecordByUsername(conn, user.getUsername());
                    LinkedList<Record> records = new LinkedList();
                    while(rs.next()){
                       Record rec = new Record(rs.getInt(1), rs.getInt(2), rs.getString(3),rs.getString(4),rs.getString(5));
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
        return new Timestamp(System.currentTimeMillis());
    }

    /*
    * input: Username, email, password, re-password (re-entered password), user role
    * output: True if all the appropriate entries are made to the databases. False otherwise
    * Note:
    */
    public static boolean register(String username, String email, String password, String re_password, String user_role){
        boolean success = false;
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
                if(Auth_Access.insertIntoUsers(username.toUpperCase(), email.toUpperCase(), password,  user_role.toUpperCase()))
                    success = true;
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
        return success;
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
}
