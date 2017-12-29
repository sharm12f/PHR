package phr.lib;

/**
 * Created by Anupam on 28-Dec-17.
 */


public class lib {

    public static void main(String args[]){
        login("","APPPASSWORD");
    }

    /*
    * input: Username and Password of the user loggin in
    * output: a user object or null; (Note: this is to be done later right now its just a function) ***************
    * Note: the user object will contain the users information and other session information
    */
    public static void login(String username, String password){
        boolean checkUsername = checkString(username);
        boolean checkPassword = checkString(password);

        if (!checkUsername || !checkPassword)
            System.out.println("Nope");
        else {
            System.out.println(Auth_Access.isUser(username, password));
        }
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
