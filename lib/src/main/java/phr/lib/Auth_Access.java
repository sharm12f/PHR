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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


public class Auth_Access{

    private static final String IP = "http://sharm12f.myweb.cs.uwindsor.ca";
    //private static final String IP = "http://10.0.2.2";

    protected static String getUsersByEmail(String email){
        String responce = makeGet(IP+"/PHR_AUTH/get_user_by_email.php?email="+email);
        return responce;
    }

    protected static String getUsersById(int id){
        String responce = makeGet(IP+"/PHR_AUTH/get_user_by_id.php?id="+id);
        return responce;
    }

    protected static String getHealthProfessionalUsersByEmail(String email){
        String responce = makeGet(IP+"/PHR_AUTH/get_health_professional_by_email.php?email="+email);
        return responce;
    }

    protected static String getUserHealthRecordByEmail(String email){
        return makeGet(IP+"/PHR_AUTH/get_user_health_record_by_email.php?email="+email);
    }

    protected static String getUserHealthRecordById(int id){
        return makeGet(IP+"/PHR_AUTH/get_user_health_record_by_id.php?id="+id);
    }

    protected static String getHealthProfessionalRecordsById(int id){
        return makeGet(IP+"/PHR_AUTH/get_health_professional_records_by_id.php?id="+id);
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
        String responce = makeGet(IP+"/PHR_AUTH/is_health_professional.php?email="+email+"&password="+password);
        if (responce.equals("true"))
            is_user=true;
        return is_user;
    }

    protected static boolean insertIntoUsers(String name,  String email, String password, String phone, String region, String province){
        boolean success=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/patient_registration.php?email="+email+"&password="+password+"&phone="+phone+"&name="+name+"&region="+region+"&province="+province);
        if (responce.equals("true"))
            success=true;
        return success;
    }

    protected static boolean insertIntoHealthProfessional(String name,  String email, String password, String phone, String region, String organization, String department, String health_professional){
        boolean success=false;
        email = email.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/health_professional_registration.php?email="+email+"&password="+password+"&phone="+phone+"&name="+name+"&region="+region+"&organization="+organization+"&department="+department+"&health_professional="+health_professional);
        if (responce.equals("true"))
            success=true;
        else{
            System.out.println(responce);
        }
        return success;
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
        System.out.println(responce);
        if(responce.equals("true"))
            result=true;
        return result;
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

    protected static boolean PatientUpdate(String name, String email, String phone, String region, String province){
        boolean result = false;
        String responce = makeGet(IP+"/PHR_AUTH/update_user.php?email="+email+"&phone="+phone+"&name="+name+"&region="+region+"&province="+province);
        System.out.println(responce);
        if(responce.equals("true"))
            result=true;
        return result;
    }

    protected static boolean HealthProfessionalUpdate(String name, String email, String phone, String region, int id){
        boolean result = false;
        String responce = makeGet(IP+"/PHR_AUTH/health_professional_update.php?email="+email+"&phone="+phone+"&name="+name+"&region="+region+"&id="+id);
        if(responce.equals("true"))
            result=true;
        return result;
    }

    private static String makeGet(String getcall){
        String string="Error";
        System.out.println(getcall);
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
        System.out.println(ip);
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
            System.out.println(urlParamaters);
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
            System.out.println(string);
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