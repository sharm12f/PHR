package phr.lib;

import java.sql.*;
import java.util.Hashtable;
import java.util.Set;

public class Auth_Access {
    /*
    * input: ResultSet (this is the result of the query from the DB)
    * output: Prints out all the rows and columns of the rest.
    * Note: none
     */
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

    /*
     * input: none
     * output: connection to the auth database
     * Note: none
     */
    protected static Connection getConnection(){
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://10.0.2.2:3306/phr_auth?autoReconnect=false&useSSL=false", "app", "password");
        }catch(Exception e){System.out.println("Could not connect to db " + e);}
        return con;
    }

    /*
     * input: an open connection
     * output: none
     * Note: This closes the connection to the auth database
     */
    protected static void closeConnection (Connection con){
        try{
            con.close();
        }catch(Exception e){System.out.println("Could not close connection " + e);}

    }

    /*
     * input: Connection to the auth database and a hash table
     * output: ResultSet (This is the results of the database query)
     * Note: see appendix 1 at the end
     *       searches the users table, appendix 1 explains the where clause using hash table
     */
    protected static ResultSet getUsersByHash(Connection con, Hashtable hash){
        ResultSet rs = null;
        String q = "select id, user_name, email, create_time, user_role from users";
        String where = " where ";
        int it = 0;
        Set<String> keys = hash.keySet();
        for (String key : keys){
            if(!key.equals("password")) {
                if (it == 0) {
                    where += key + " LIKE ?";
                    it++;
                } else {
                    where += "and " + key + " LIKE ?";
                }
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
                    if(!key.equals("password")) {
                        que.setString(index, "%"+hash.get(key).toString()+"%");
                        index++;
                    }
                }
            }
            rs = que.executeQuery();
        }catch(Exception e){System.out.println("Could not create query users " + e);}
        return rs;
    }

    /*
     * input: Connection to the auth database, and the user name
     * output: ResultSet (This is the results of the database query)
     * Note: gets the user records for a particular username
     */
    protected static ResultSet getUserHealthRecordByUsername(Connection con, String user_name){
        ResultSet rs = null;
        try{
            String q = "select H.id, H.user_id, H.cypertext_policy, H.cypertext_record, H.cypertext_record_ref, H.create_time from user_health_record as H, users as U where U.id = H.user_id and U.user_name = ?";
            PreparedStatement que = con.prepareStatement(q);
            que.setString(1, user_name);
            rs = que.executeQuery();
        }catch(Exception e){System.out.println("Could not create query health Record " + e);}
        return rs;
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

    /*
     * input: Connection to the auth database, and hash table
     * output: ResultSet (This is the results of the database query)
     * Note: see appendix 1 at the end
     *       searches the attribute table, appendix 1 explains the where clause using hash table
     */
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

    /*
     * input: Connection to the auth database, and hash table
     * output: ResultSet (This is the results of the database query)
     * Note: see appendix 1 at the end
     *       searches the user has attribute set table, appendix 1 explains the where clause using hash table
     */
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

    /*
     * input: username and password
     * output: true or false if they match the database
     * Note: This tells if a person can log in
     *
     */
    protected static boolean isUser(String username, String password){
        boolean is_user=false;
        username = username.toUpperCase();
        Connection con = getConnection();
        ResultSet rs = null;
        String q = "select password, user_name from users where user_name = ?";
        try{
            PreparedStatement que = con.prepareStatement(q);
            que.setString(1, username);
            String db_username = "";
            String db_password= "";
            rs = que.executeQuery();
            int index = 0;
            while (rs.next()){
                if(index == 0) {
                    db_password = rs.getString(1);
                    db_username = rs.getString(2);
                }
                index++;
            }
            if(index == 1){
                if(username.equals(db_username) && password.equals(db_password)){
                    is_user=true;
                }
            }
            else{
                System.out.println("Too many users with that username?");
                is_user=false;
            }
        }catch(Exception e){System.out.println("Could not create query is user " + e);}
        closeConnection(con);
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
}
/*
* APPENDIX 1:
*       The hash table are used to generate the where clause for the table search.
*       Keys represent the column name, and the values are to be used as search criteria.
*       This does not allow the user to pick what columns are shown in the results, only used in where to search.
*
*/