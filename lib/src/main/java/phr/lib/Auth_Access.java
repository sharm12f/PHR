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

    private static final String IP = "http://sharm12f.myweb.cs.uwindsor.ca";
    //private static final String IP = "http://10.0.2.2";

    protected static String getUsersByEmail(String email){
       String responce = makeGet(IP+"/PHR_AUTH/get_user_by_email.php?email="+email);
       return responce;
    }

    protected static String getHealthProfessionalUsersByEmail(String email){
        String responce = makeGet(IP+"/PHR_AUTH/get_health_professional_by_email.php?email="+email);
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

    protected static boolean isHealthProfessional(String email, String password){
        boolean is_user=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/is_health_professional.php?email="+email+"&password="+password);
        if (responce.equals("true"))
            is_user=true;
        return is_user;
    }

    protected static boolean insertIntoUsers(String fname, String lname,  String email, String password, String phone, String region, String province){
        boolean success=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/patient_registration.php?email="+email+"&password="+password+"&phone="+phone+"&fname="+fname+"&lname="+lname+"&region="+region+"&province="+province);
        if (responce.equals("true"))
            success=true;
        else{
            System.out.println(responce);
        }
        return success;
    }

    protected static boolean insertIntoHealthProfessional(String fname, String lname,  String email, String password, String phone, String region, String organization, String department, String health_professional){
        boolean success=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/health_professional_registration.php?email="+email+"&password="+password+"&phone="+phone+"&fname="+fname+"&lname="+lname+"&region="+region+"&organization="+organization+"&department="+department+"&health_professional="+health_professional);
        if (responce.equals("true"))
            success=true;
        else{
            System.out.println(responce);
        }
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
    protected static boolean healthUserExists(String email) {
        boolean success=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/health_user_exists.php?email="+email);
        if (responce.equals("true"))
            success=true;
        return success;
    }

    protected static boolean updateRecord(String name, String description, int rid){
        boolean result = false;
        String responce = makeGet(IP+"/PHR_AUTH/update_record.php?name="+name+"&description="+description+"&rid="+rid);
        if(responce.equals("true"))
            result=true;
        return result;
    }

    protected static boolean insertIntoRecord(String name, String description, int uid){
        boolean result = false;
        String responce = makeGet(IP+"/PHR_AUTH/insert_into_record.php?name="+name+"&description="+description+"&uid="+uid);
        if(responce.equals("true"))
            result=true;
        return result;
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

    protected static String getOrganization(){
        String responce = makeGet(IP+"/PHR_AUTH/get_organization.php");
        System.out.println(responce);
        return responce;
    }

    protected static String getDepartment(){
        String responce = makeGet(IP+"/PHR_AUTH/get_department.php");
        System.out.println(responce);
        return responce;
    }

    protected static String getHealthProfessional(){
        String responce = makeGet(IP+"/PHR_AUTH/get_health_professional.php");
        System.out.println(responce);
        return responce;
    }

    protected static boolean PatientUpdate(String fname, String lname, String email, String phone, String region, String province){
        boolean result = false;
        String responce = makeGet(IP+"/PHR_AUTH/update_user.php?email="+email+"&phone="+phone+"&fname="+fname+"&lname="+lname+"&region="+region+"&province="+province);
        if(responce.equals("true"))
            result=true;
        return result;
    }

    protected static boolean HealthProfessionalUpdate(String fname, String lname, String email, String phone, String region){
        boolean result = false;
        String responce = makeGet(IP+"/PHR_AUTH/health_professional_update.php?email="+email+"&phone="+phone+"&fname="+fname+"&lname="+lname+"&region="+region);
        if(responce.equals("true"))
            result=true;
        return result;
    }

}