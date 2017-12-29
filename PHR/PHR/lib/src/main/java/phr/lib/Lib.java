package phr.lib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Hashtable;

/**
 * Created by Anupam on 28-Dec-17.
 */


public class Lib {

    public static void main(String args[]){
        Users app = login("app","APPPASSWORD");
        System.out.println(app.toString());
    }

    /*
    * input: Username and Password of the user loggin in
    * output: a user object or null; (Note: this is to be done later right now its just a function) ***************
    * Note: the user object will contain the users information and other session information
    */
    public static Users login(String username, String password){
        boolean checkUsername = checkString(username);
        boolean checkPassword = checkString(password);
        Users user = null;
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
                    user = new Users(db_username, db_email, db_create, db_role, db_id);
                }catch(Exception e){System.out.println("Could not log in, no DB Connection" + e);}
            }
        }
        return user;
    }

    /*
    * input: String, this verifies if the string is valid, only contains specified chars, and ensure the string is not longer and 128 and not shorter than 1
    * output: boolean answer indicating true or false, true for it being valid
    * Note: none
    */
    private static boolean checkString(String str){
        if(str.length() > 128 || str.length() < 1)
            return false;
        if(str.matches("[a-zA-Z0-9+=*/^()_-]+"))
            return true;
        return false;
    }
}
