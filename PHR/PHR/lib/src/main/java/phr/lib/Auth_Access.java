package phr.lib;

import java.sql.*;
import java.util.Hashtable;
import java.util.Set;

public class Auth_Access {

    public static void main(String args[]){
        Connection con = getConnection();
        Hashtable hash = new Hashtable();
        ResultSet rs = getUsersByHash(con, hash);
        printResultSet(rs);
        closeConnection(con);
    }

    /*
    * input: ResultSet (this is the result of the query from the DB)
    * output: Prints out all the rows and columns of the rest.
    * Note: none
     */
    public static void printResultSet(ResultSet rs){
        try{
            ResultSetMetaData rsmd = rs.getMetaData();
            int len=rsmd.getColumnCount();
            while(rs.next()) {
                for (int i = 1; i <= len; i++)
                    System.out.print(rs.getString(i) + ' ');
                System.out.println("");
            }
        }catch (Exception e){System.out.println("Something wrong with the result set  ");}
    }

    /*
     * input: none
     * output: connection to the auth database
     * Note: none
     */
    public static Connection getConnection(){
        Connection con = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/phr_auth", "app", "password");
        }catch(Exception e){System.out.println("Could not connect to db " + e);}
        return con;
    }

    /*
     * input: an open connection
     * output: none
     * Note: This closes the connection to the auth database
     */
    public static void closeConnection (Connection con){
        try{
            con.close();
        }catch(Exception e){System.out.println("Could not close connection");}

    }

    /*
     * input: Connection to the auth database and a hash table
     * output: ResultSet (This is the results of the database query)
     * Note: see appendix 1 at the end
     *       searches the users table, appendix 1 explains the where clause using hash table
     */
    public static ResultSet getUsersByHash(Connection con, Hashtable hash){
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
    public static ResultSet getUserHealthRecordByUsername(Connection con, String user_name){
        ResultSet rs = null;
        try{
            String q = "select H.id from user_health_record as H, users as U where U.id = H.user_id and U.user_name LIKE ?";
            PreparedStatement que = con.prepareStatement(q);
            que.setString(1, "%"+user_name+"%");
            rs = que.executeQuery();
        }catch(Exception e){System.out.println("Could not create query health record " + e);}
        return rs;
    }

    /*
     * input: Connection to the auth database, and hash table
     * output: ResultSet (This is the results of the database query)
     * Note: see appendix 1 at the end
     *       searches the attribute table, appendix 1 explains the where clause using hash table
     */
    public static ResultSet getAttributesByHash(Connection con, Hashtable hash){
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
    public static ResultSet getUserHasAttributSetByHash(Connection con, Hashtable hash){
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
}




/*
* APPENDIX 1:
*       The hash table are used to generate the where clause for the table search.
*       Keys represent the column name, and the values are to be used as search criteria.
*       This does not allow the user to pick what columns are shown in the results, only used in where to search.
*
 */
