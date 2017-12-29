package phr.lib;

/**
 * Created by Anupam on 28-Dec-17.
 */

public class Users {
    private String username;
    private String email;
    private String role;
    private int id;

    public void Users(String username, String email, String role, int id){
        this.username = username;
        this.email = email;
        this.role = role;
        this.id = id;
    }

    public String getUsername(){
        return this.username;
    }
    public String getEmail(){
        return this.email;
    }
    public String getRole(){
        return this.role;
    }
    public int getId(){
        return this.id;
    }

    public void setUsername(String username){
        this.username = username;
    }
    public void setEmail(String email){
        this.email = email;
    }
    public void setRole(String role){
        this.role = role;
    }
    public void setId(int id){
        this.id = id;
    }
}
