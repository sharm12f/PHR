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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Auth_Access{

    private static final String IP = "http://sharm12f.myweb.cs.uwindsor.ca";
    //private static final String IP = "http://10.0.2.2";

    protected static String getUsersByEmail(String email){
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        return makePost(IP+"/PHR_AUTH/get_user_by_email.php", postData);
    }

    protected static String getUsersById(int id){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        return makePost(IP+"/PHR_AUTH/get_user_by_id.php", postData);
    }

    protected static String getHealthProfessionalUsersByEmail(String email){
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        return makePost(IP+"/PHR_AUTH/get_health_professional_by_email.php", postData);
    }

    protected static String getUserHealthRecordByEmail(String email){
        email = email.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        return makePost(IP+"/PHR_AUTH/get_user_health_record_by_email.php", postData);
    }

    protected static String getUserHealthRecordById(int id){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        return makePost(IP+"/PHR_AUTH/get_user_health_record_by_id.php", postData);
    }

    protected static String getHealthProfessionalRecordsById(int id){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        return makePost(IP+"/PHR_AUTH/get_health_professional_records_by_id.php", postData);
    }

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
        System.out.println(responce);
        if (responce.equals("true"))
            success=true;
        return success;
    }

    protected static boolean insertIntoHealthProfessional(String name,  String email, String password, String phone, String region, String organization, String department, String health_professional){
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
        postData.put("organization", organization);
        postData.put("department", department);
        postData.put("health_professional", health_professional);
        String responce = makePost(IP+"/PHR_AUTH/health_professional_registration.php", postData);
        System.out.println(responce);
        if (responce.equals("true"))
            success=true;
        return success;
    }

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

    protected static boolean insertIntoNotes(String name, String description, int uid, int hpid){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("name", name);
        postData.put("description", description);
        postData.put("user_id", uid+"");
        postData.put("health_professional_id", hpid+"");
        String responce = makePost(IP+"/PHR_AUTH/insert_into_notes.php", postData);
        System.out.println(responce);
        if(responce.equals("true"))
            result=true;
        return result;
    }

    protected static String getNotesForPatient(int user_id){
        HashMap<String, String> postData = new HashMap<>();
        postData.put("user_id", user_id+"");
        String responce = makePost(IP+"/PHR_AUTH/get_notes.php", postData);
        return responce;
    }

    protected static boolean deleteRecord(int id){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/delete_record.php", postData);
        if(responce.equals("true"))
            result = true;
        return result;
    }

    protected static String getRecordPerms(int id){
        String result = "";
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/get_record_perms.php", postData);
        System.out.println("Responce11: "+responce);
        if(!responce.equals("error")){
            result = responce;
        }
        return result;
    }

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

    protected static boolean revokePermission(int id){
        boolean result = false;
        HashMap<String, String> postData = new HashMap<>();
        postData.put("id", id+"");
        String responce = makePost(IP+"/PHR_AUTH/delete_from_health_professional_has_user_health_record.php", postData);
        if(responce.equals("true"))
            result = true;
        return result;
    }

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

    protected static String searchHealthProfessionals(String region, String organization, String department, String healthProfessional){
        HashMap<String, String> postData = new HashMap<>();
        boolean set = false;
        if(!region.equals(" ")){
            postData.put("region", region);
            set = true;
        }
        if(!organization.equals(" ")){
            postData.put("organization", organization);
            set = true;
        }
        if(!department.equals(" ")){
            postData.put("department", department);
            set = true;
        }
        if(!healthProfessional.equals(" ")){
            postData.put("healthProfessional", healthProfessional);
            set = true;
        }
        if(!set){
            return null;
        }
        String responce = makePost(IP+"/PHR_AUTH/get_health_professional_by_search.php", postData);
        System.out.println(responce);
        if(responce.equals("error"))
           return null;
        return responce;
    }

    protected static String getRegions(){
        String responce = makeGet(IP+"/PHR_AUTH/get_regions.php");
        return responce;
    }

    protected static String getProvinces(){
        String responce = makeGet(IP+"/PHR_AUTH/get_provinces.php");
        return responce;
    }

    protected static String getOrganization(){
        String responce = makeGet(IP+"/PHR_AUTH/get_organization.php");
        return responce;
    }

    protected static String getDepartment(){
        String responce = makeGet(IP+"/PHR_AUTH/get_department.php");
        return responce;
    }

    protected static String getHealthProfessional(){
        String responce = makeGet(IP+"/PHR_AUTH/get_health_professional.php");
        return responce;
    }

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

    protected static boolean HealthProfessionalUpdate(String name, String email, String phone, String region, int id){
        boolean result = false;
        email=email.toUpperCase();
        name=name.toUpperCase();
        HashMap<String, String> postData = new HashMap<>();
        postData.put("email", email);
        postData.put("name", name);
        postData.put("phone", phone);
        postData.put("region", region);
        postData.put("id",id+"");
        String responce = makePost(IP+"/PHR_AUTH/health_professional_update.php", postData);
        if(responce.equals("true"))
            result=true;
        return result;
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