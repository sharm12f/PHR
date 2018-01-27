package phr.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.Hashtable;
import java.util.Set;


public class Auth_Access{

    private static final String IP = "http://10.0.2.2";

    protected static String getUsersByUsername(String email){
       String responce = makeGet(IP+"/PHR_AUTH/get_user_by_email.php?email="+email);
       return responce;
    }

    protected static String getUserHealthRecordByEmail(String email){
        return makeGet(IP+"/PHR_AUTH/get_user_health_record_by_email.php?email="+email);
    }

    protected static boolean isUser(String email, String password){
        boolean is_user=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/is_user.php?email="+email+"&password="+password);
        if (responce.equals("true"))
            is_user=true;
        return is_user;
    }

    protected static boolean insertIntoUsers(String name, String email, String password, String phone, String region, String province){
        boolean success=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/patient_registration.php?email="+email+"&password="+password+"&phone="+phone+"&name="+name+"&region="+region+"&province="+province);
        if (responce.equals("true"))
            success=true;
        return success;
    }

    protected static boolean deleteUser(String user_name){
        //("delete from users where user_name=?");
        return false;

    }

    protected static boolean userExists(String email) {
        boolean success=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/user_exists.php?email="+email);
        if (responce.equals("true"))
            success=true;
        return success;
    }

    protected static boolean insertIntoRecord(String policy, String record, String record_ref, int user_id){
        //("insert into user_health_record (cypertext_policy, cypertext_record, cypertext_record_ref, user_id) values (?,?,?,?)")
        return false;
    }

    protected static boolean deleteUserRecord(int id){
        //("delete from user_health_record where id=?");
        return false;
    }

    private static String makeGet(String getcall){
        String string="Error";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(getcall);
            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setConnectTimeout(2000);
            InputStream in = urlConnection.getInputStream();
            InputStreamReader isw = new InputStreamReader(in);
            int data = isw.read();
            String responce="";
            while (data != -1) {
                char current = (char) data;
                data = isw.read();
                responce+=current;
                string = responce;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return string;
    }

    protected static String getRegions(){
        String responce = makeGet(IP+"/PHR_AUTH/get_regions.php");
        System.out.println(responce);
        return responce;
    }

    protected static String getProvinces(){
        String responce = makeGet(IP+"/PHR_AUTH/get_provinces.php");
        System.out.println(responce);
        return responce;
    }

}