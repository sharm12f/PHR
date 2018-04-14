package phr.lib;

import com.sun.org.apache.xpath.internal.SourceTree;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
/**
 * Created by Anupam on 28-Mar-18.
 *
 * This is a protected library that is designed to interact with the PHP webserver.
 *
 */


public class Auth_Access{

    private static final String IP = "http://sharm12f.myweb.cs.uwindsor.ca";
    //private static final String IP = "http://10.0.2.2";

    //get the users information using an email address
    protected static String getUsersByEmail(String email){
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        return makePost(IP+"/PHR_AUTH/get_user_by_email.php", postData);
    }
    // get the user information using a known unique id for the user.
    protected static String getUsersById(int id){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        return makePost(IP+"/PHR_AUTH/get_user_by_id.php", postData);
    }
    //get the health professional using an email address
    protected static String getHealthProfessionalUsersByEmail(String email){
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        return makePost(IP+"/PHR_AUTH/get_health_professional_by_email.php", postData);
    }
    //get all the records for a user using their email
    protected static String getUserHealthRecordByEmail(String email){
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        return makePost(IP+"/PHR_AUTH/get_user_health_record_by_email.php", postData);
    }
    //get all the records for a user using their known unique id
    protected static String getUserHealthRecordById(int id){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        return makePost(IP+"/PHR_AUTH/get_user_health_record_by_id.php", postData);
    }
    //get all the recirds a health professional has access to given the health professional known unique id
    protected static String getHealthProfessionalRecordsById(int id){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        return makePost(IP+"/PHR_AUTH/get_health_professional_records_by_id.php", postData);
    }
    //authenticates the user given their email and password (used for login)
    protected static boolean isUser(String email, String password){
        boolean is_user=false;
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        postData.put("password", password);
        String responce = makePost(IP+"/PHR_AUTH/is_user.php", postData);
        if (responce.equals("true"))
            is_user=true;
        return is_user;
    }
    //authenticate the health professional give then email and password (used for login)
    protected static boolean isHealthProfessional(String email, String password){
        boolean is_user=false;
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        postData.put("password", password);
        String responce = makePost(IP+"/PHR_AUTH/is_health_professional.php", postData);
        if (responce.equals("true"))
            is_user=true;
        return is_user;
    }
    //add a new user into the database (used for patient registration)
    protected static boolean insertIntoUsers(String name,  String email, String password, String phone, String region, String province){
        boolean success=false;
        email = email.toUpperCase();
        if(userExists(email) || healthUserExists(email))
            return false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        postData.put("name", name);
        postData.put("password", password);
        postData.put("phone", phone);
        postData.put("region", region);
        postData.put("province", province);
        String responce = makePost(IP+"/PHR_AUTH/patient_registration.php", postData);
        if (responce.equals("true"))
            success=true;
        return success;
    }
    //add a new health professional into the database (used for health professional registration)
    protected static boolean insertIntoHealthProfessional(String name,  String email, String password, String phone, String region, String province, String organization, String department, String health_professional){
        boolean success=false;
        email = email.toUpperCase();
        if(userExists(email) || healthUserExists(email))
            return false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("name",name);
        postData.put("email", email);
        postData.put("password", password);
        postData.put("phone", phone);
        postData.put("region", region);
        postData.put("province", province);
        postData.put("organization", organization);
        postData.put("department", department);
        postData.put("health_professional", health_professional);
        String responce = makePost(IP+"/PHR_AUTH/health_professional_registration.php", postData);
        if (responce.equals("true"))
            success=true;
        return success;
    }
    //return true if the user email provided exists in the database.
    protected static boolean userExists(String email) {
        boolean success=false;
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        String responce = makePost(IP+"/PHR_AUTH/user_exists.php",postData);
        if (responce.equals("true"))
            success=true;
        return success;
    }
    //return true if the health professional email provided exists in the database.
    protected static boolean healthUserExists(String email) {
        boolean success=false;
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        String responce = makePost(IP+"/PHR_AUTH/health_user_exists.php", postData);
        if (responce.equals("true"))
            success=true;
        return success;
    }
    //update a user record given the known unique record id
    protected static boolean updateRecord(String name, String description, int rid){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("name", name);
        postData.put("description", description);
        postData.put("rid", rid+"");
        String responce = makePost(IP+"/PHR_AUTH/update_record.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //insert a new record given the known unique user id
    protected static boolean insertIntoRecord(String name, String description, int uid){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("name", name);
        postData.put("description", description);
        postData.put("uid", uid+"");
        String responce = makePost(IP+"/PHR_AUTH/insert_into_record.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //insert a new note given the known unique user and health professional id
    protected static boolean insertIntoNotes(String name, String description, int uid, int hpid){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("name", name);
        postData.put("description", description);
        postData.put("user_id", uid+"");
        postData.put("health_professional_id", hpid+"");
        String responce = makePost(IP+"/PHR_AUTH/insert_into_notes.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //get all notes addressed to a user given the known unique user id
    protected static String getNotesForPatient(int user_id){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("user_id", user_id+"");
        String responce = makePost(IP+"/PHR_AUTH/get_notes.php", postData);
        return responce;
    }
    //remove a record give the known unique record id
    protected static boolean deleteRecord(int id){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/delete_record.php", postData);
        if(responce.equals("true"))
            result = true;
        return result;
    }
    //get the permissions for all the record for a user given the known unique user id
    protected static String getRecordPerms(int id){
        String result = "";
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/get_record_perms.php", postData);
        if(!responce.equals("error")){
            result = responce;
        }
        return result;
    }
    //insert new permission for a record give the known unique health professional and record id.
    protected static boolean givePermission(int hpid, int rid){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("hpid", hpid+"");
        postData.put("rid", rid+"");
        String responce = makePost(IP+"/PHR_AUTH/insert_into_health_professional_has_user_health_record.php", postData);
        if(responce.equals("true"))
            result = true;
        return result;
    }
    //remove permission for a record given the known unique permission id (the permissions themselves also have unique id's)
    protected static boolean revokePermission(int id){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/delete_from_health_professional_has_user_health_record.php", postData);
        if(responce.equals("true"))
            result = true;
        return result;
    }
    //return true if the permissions already exists in the database given the known unique health professional and record id's.
    protected static boolean permissionsExist(int hpid, int rid){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("hpid", hpid+"");
        postData.put("rid", rid+"");
        String responce = makePost(IP+"/PHR_AUTH/permission_exists.php", postData);
        if(responce.equals("true"))
            result = true;
        return result;
    }
    //set the login time stamp for a user given the known unique user id.
    protected static boolean setLoginUser(int id, Timestamp login){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        postData.put("login", login+"");
        String responce = makePost(IP+"/PHR_AUTH/set_login_user.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //set the logout time stamp for a user given the known unique user id.
    protected static boolean setLogoutUser(int id, Timestamp login){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        postData.put("login", login+"");
        String responce = makePost(IP+"/PHR_AUTH/set_logout_user.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //set the login time stamp for a health professional given the known unique health professional id.
    protected static boolean setLoginHealthProfessional(int id, Timestamp login){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        postData.put("login", login+"");
        String responce = makePost(IP+"/PHR_AUTH/set_login_health_professional.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //set the logout time stamp for a healthprofessional given the known unique health professional id.
    protected static boolean setLogoutHealthProfessional(int id, Timestamp login){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        postData.put("login", login+"");
        String responce = makePost(IP+"/PHR_AUTH/set_logout_health_professional.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //get the login and logout time of a user give the known unique user id
    protected static String getLoginLogoutUser(int id){
        String result = "";
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/get_loginout_user.php", postData);
        if(!responce.equals("error"))
            result=responce;
        return result;
    }
    //get the login and logout time of a health professional give the known unique health professional id
    protected static String getLoginLogoutHealthProfessional(int id){
        String result = "";
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/get_loginout_health_professional.php", postData);
        if(!responce.equals("error"))
            result=responce;
        return result;
    }
    //serach for a health professional using they attributes.
    protected static String searchHealthProfessionals(String region, String province, String organization, String department, String healthProfessional){
        HashMap<String, String> postData = new HashMap<>();
        if(!region.equals("Any")){
            postData.put("region", region);
        }
        if(!province.equals("Any")){
            postData.put("province", province);
        }
        if(!organization.equals("Any")){
            postData.put("organization", organization);
        }
        if(!department.equals("Any")){
            postData.put("department", department);
        }
        if(!healthProfessional.equals("Any")){
            postData.put("healthProfessional", healthProfessional);
        }
        String responce = makePost(IP+"/PHR_AUTH/get_health_professional_by_search.php", postData);
        if(responce.equals("error"))
           return null;
        return responce;
    }
    //get all the regions in the database.
    protected static String getRegions(){
        String responce = makeGet(IP+"/PHR_AUTH/get_regions.php");
        return responce;
    }
    //get all the provinces in the database.
    protected static String getProvinces(){
        String responce = makeGet(IP+"/PHR_AUTH/get_provinces.php");
        return responce;
    }
    //get all the organizations in the database.
    protected static String getOrganization(){
        String responce = makeGet(IP+"/PHR_AUTH/get_organization.php");
        return responce;
    }
    //get all the departments in the database.
    protected static String getDepartment(){
        String responce = makeGet(IP+"/PHR_AUTH/get_department.php");
        return responce;
    }
    //get all the health professional attributes in the databse (this is they perticular profession)
    protected static String getHealthProfessional(){
        String responce = makeGet(IP+"/PHR_AUTH/get_health_professional.php");
        return responce;
    }
    //update the patient information given the known unique user id
    protected static boolean PatientUpdate(String name, String email, String phone, String region, String province, int id){
        boolean result = false;
        email=email.toUpperCase();
        name=name.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        postData.put("name", name);
        postData.put("phone", phone);
        postData.put("region", region);
        postData.put("province", province);
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/update_user.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //update the health professional information give the known unique health professional id
    protected static boolean HealthProfessionalUpdate(String name, String email, String phone, String region, String province, String organization, String department, String healthprofessinalProfession, int id){
        boolean result = false;
        email=email.toUpperCase();
        name=name.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        postData.put("name", name);
        postData.put("phone", phone);
        postData.put("region", region);
        postData.put("province", province);
        postData.put("organization", organization);
        postData.put("department", department);
        postData.put("health_professional", healthprofessinalProfession);
        postData.put("id",id+"");
        String responce = makePost(IP+"/PHR_AUTH/health_professional_update.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
    }
    //get server time, used to ensure the time the app uses when sending to the server is the same.
    protected static String getServerTime(){
        String responce = makeGet(IP+"/PHR_AUTH/get_time_now_server.php");
        return responce;
    }

    //last two can only be used from this library
    //i would like to make these two use HTTPS for added security.

    //make a get call to a url (only used if there is no information being passed to the webserver)
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
    //make a post call to a url (used when information is being passed to the webserver)
    private static String makePost(String ip, HashMap<String, String> postData){
        String string = "";
        URL url;
        HttpURLConnection urlConnection = null;
        try {
            url = new URL(ip);
            urlConnection = (HttpURLConnection) url
                    .openConnection();
            urlConnection.setConnectTimeout(2000);
            urlConnection.setRequestMethod("POST");
            String urlParamaters="";
            int i =0;
            Iterator it = postData.entrySet().iterator();
            while(it.hasNext()){
                Map.Entry<String, String> entry = (Map.Entry) it.next();
                String key = entry.getKey();
                String value = entry.getValue();
                if(i==0){
                    urlParamaters+=key+"="+value;
                    i++;
                }
                else{
                    urlParamaters+="&"+key+"="+value;
                }

            }
            urlConnection.setDoOutput(true);
            DataOutputStream outputPost = new DataOutputStream(urlConnection.getOutputStream());
            outputPost.writeBytes(urlParamaters);
            outputPost.flush();

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
        return  string;
    }

}