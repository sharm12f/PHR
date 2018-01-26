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

    protected static void printResultSet(ResultSet rs){
        try{
            ResultSetMetaData rsmd = rs.getMetaData();
            int len=rsmd.getColumnCount();
            while(rs.next()) {
                for (int i = 1; i <= len; i++)
                    System.out.print(rs.getString(i) + ' ');
                System.out.println("\n");
            }
        }catch (Exception e){System.out.println("Something wrong with the result set  ");}
    }

    protected static Connection getConnection(){
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/phr_auth?autoReconnect=false&useSSL=false", "app", "password");
        }catch(Exception e){System.out.println("Could not connect to db " + e);}
        return con;
    }

    protected static void closeConnection (Connection con){
        try{
            con.close();
        }catch(Exception e){System.out.println("Could not close connection " + e);}

    }

    protected static String getUsersByUsername(String username){
       String responce = makeGet(IP+"/PHR_AUTH/get_user_by_username.php?username="+username);
       return responce;
    }

    protected static String getUserHealthRecordByUsername(String username){
        return makeGet(IP+"/PHR_AUTH/get_user_health_record_by_username.php?username="+username);
    }

    protected static ResultSet getUserHealthRecordIDByUseridAndTimestamp(Connection con, int user_id, String rec_create){
        ResultSet rs = null;
        try{
            String q = "select id from user_health_record where user_id = ? and create_time = ?";
            PreparedStatement que = con.prepareStatement(q);
            que.setInt(1, user_id);
            que.setString(2, rec_create);
            rs = que.executeQuery();
        }catch(Exception e){System.out.println("Could not create query health Record " + e);}
        return rs;
    }

    protected static ResultSet getAttributesByHash(Connection con, Hashtable hash){
        ResultSet rs = null;
        String q = "select * from attributes";
        String where = " where ";
        int it = 0;
        Set<String> keys = hash.keySet();
        for (String key : keys){
            if (it==0){
                where += key + " LIKE ?";
                it++;
            }
            else{
                where += "and " + key + " LIKE ?";
            }
        }
        if(it>0){
            q = q+where;
        }
        try{
            int index = 1;
            PreparedStatement que = con.prepareStatement(q);
            if (it > 0){
                for(String key : keys){
                    que.setString(index, "%"+hash.get(key).toString()+"%");
                    index++;
                }
            }
            rs = que.executeQuery();
        }catch(Exception e){System.out.println("Could not create query attributes " + e);}
        return rs;
    }

    protected static ResultSet getUserHasAttributSetByHash(Connection con, Hashtable hash){
        ResultSet rs = null;
        String q = "select * from user_has_attribute_set";
        String where = " where ";
        int it = 0;
        Set<String> keys = hash.keySet();
        for (String key : keys){
            if (it==0){
                where += key + " = ?";
                it++;
            }
            else{
                where += "and " + key + " = ?";
            }
        }
        if(it>0){
            q = q+where;
        }
        try{
            int index = 1;
            PreparedStatement que = con.prepareStatement(q);
            if (it > 0){
                for(String key : keys){
                    que.setInt(index, (int)hash.get(key));
                    index++;
                }
            }
            rs = que.executeQuery();
        }catch(Exception e){System.out.println("Could not create query user_has_attribute_set " + e);}
        return rs;
    }

    protected static boolean isUser(String username, String password){
        boolean is_user=false;
        username = username.toUpperCase();
        String responce = makeGet(IP+"/PHR_AUTH/is_user.php?username="+username+"&password="+password);
        if (responce.equals("true"))
            is_user=true;
        return is_user;
    }

    protected static boolean insertIntoUsers(String user_name, String email, String password, String user_role){
        try{
            Connection con = getConnection();
            PreparedStatement que = con.prepareStatement("insert into users (user_name, email, password, user_role) values (?,?,?,?)");
            que.setString(1, user_name);
            que.setString(2, email);
            que.setString(3, password);
            que.setString(4, user_role);
            que.executeUpdate();
            closeConnection(con);
            return true;
        }catch (Exception e){System.out.println("Could not insert new user " + e); return false;}
    }

    protected static boolean deleteUser(String user_name){
        try{
            Connection con = getConnection();
            PreparedStatement que = con.prepareStatement("delete from users where user_name=?");
            que.setString(1, user_name);
            que.executeUpdate();
            closeConnection(con);
            return true;
        }catch (Exception e){System.out.println("Could not delete new user " + e); return false;}
    }

    protected static boolean userExists(String user_name) {
        try{
            Connection con = getConnection();
            PreparedStatement que = con.prepareStatement("select count(user_name) from users where user_name=?");
            que.setString(1, user_name);
            ResultSet rs = que.executeQuery();
            rs.next();
            int count = rs.getInt(1);
            closeConnection(con);
            if (count >= 1)
                return true;
            else
                return false;
        }catch (Exception e){System.out.println("Something went wrong, userExists " + e); return true;}

    }

    protected static boolean insertIntoRecord(String policy, String record, String record_ref, int user_id){
        try{
            Connection con = getConnection();
            PreparedStatement que = con.prepareStatement("insert into user_health_record (cypertext_policy, cypertext_record, cypertext_record_ref, user_id) values (?,?,?,?)");
            que.setString(1, policy);
            que.setString(2, record);
            que.setString(3, record_ref);
            que.setInt(4, user_id);
            que.executeUpdate();
            closeConnection(con);
            return true;
        }catch (Exception e){System.out.println("Could not insert new record " + e); return false;}
    }

    protected static boolean deleteUserRecord(int id){
        try{
            Connection con = getConnection();
            PreparedStatement que = con.prepareStatement("delete from user_health_record where id=?");
            que.setInt(1, id);
            que.executeUpdate();
            closeConnection(con);
            return true;
        }catch (Exception e){System.out.println("Could not delete record " + e); return false;}
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

}