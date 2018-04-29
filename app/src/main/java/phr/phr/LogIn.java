package phr.phr;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import phr.lib.User;

/**
 * Created by Anupam on 27-Jan-18.
 *
 *  is the login page for the app
 *  it allows the user to:
 *      login
 *      register
 *
 */


public class LogIn extends AppCompatActivity {

    // Variable user, can be both patient or healthProfessional
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //check user permissions
        if (ContextCompat.checkSelfPermission(LogIn.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(LogIn.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(LogIn.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},0x4);
            }
        }

        // if they press the back button on the login page, the stack is going to be cleared and the app will exit. (i do this, because previously the app would go back on the last activity on the stack)
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey("EXIT")){
                boolean exit = (boolean)getIntent().getExtras().get("EXIT");
                if(exit)
                    finish();
            }
            // check if this activity was started because of a timeout
            if(getIntent().getExtras().containsKey("TIMEOUT")){
                boolean exit = (boolean)getIntent().getExtras().get("TIMEOUT");
                if(exit) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                    builder.setMessage("Session Timeout")
                            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        }

        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.activity_main);
        final Button login_button = findViewById(R.id.login_button);
        final Button reset_password_button = findViewById(R.id.reset_password_button);
        final Button register_button = findViewById(R.id.register_button);
        final EditText email_input = findViewById(R.id.email_input);
        final EditText password_input=  findViewById(R.id.password_input);

        // The login button will.. well log in  the user
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try {
                    AsyncTask<Void, Void, Void> asyncTask =  new AsyncTask<Void, Void, Void>() {
                        private ProgressDialog p = new ProgressDialog(LogIn.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }
                        protected Void doInBackground(Void... progress) {
                            // get the users credentials and try to login them in

                            //if(email_input.getText().toString().equals("") || password_input.getText().toString().equals(""))
                            //    return null;
                            //user = Lib.login(email_input.getText().toString(), password_input.getText().toString());
                            user = Lib.login("app@app.com", "p");
                            user.setSession(Lib.getTimestampNow());
                            return null;
                        }
                        protected void onPostExecute(Void Void){
                            super.onPostExecute(Void);
                            if(user!=null) {
                                // set the session timestart, and check if the user is a patient or healthProfessional
                                String role = user.getRole();
                                if (role.equals("USER")) {
                                    Intent intent = new Intent(getApplicationContext(), PatientView.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(user);
                                    intent.putExtra("USER", list);
                                    startActivity(intent);
                                    finish();
                                } else if (role.equals("HP")) {
                                    Intent intent = new Intent(getApplicationContext(), HealthProfessionalView.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(user);
                                    intent.putExtra("USER", list);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else{
                                Toast toast = Toast.makeText(LogIn.this, "Invalid Username or Password", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            p.dismiss();
                        }
                    };
                    asyncTask.execute();

                }catch (Exception e){e.printStackTrace();}
            }
        });

        //allow the user to register
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    // show an alert asking them what type of account they want.
                    AlertDialog.Builder builder = new AlertDialog.Builder(LogIn.this);
                    builder.setMessage("Are you a Patient of a Physician")
                            .setPositiveButton("Patient", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent (getApplicationContext(), PatientRegistration.class);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("Physician", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Intent intent = new Intent (getApplicationContext(), HealthProfessionalRegistration.class);
                                    startActivity(intent);
                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }catch(Exception e){e.printStackTrace();}
            }
        });

        // not yet implemented however I use it as a quick login as a healthProfessional
        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(LogIn.this, "Under Construction", Toast.LENGTH_SHORT);
                toast.show();
                /*try {
                    AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                        private ProgressDialog p = new ProgressDialog(LogIn.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }
                        protected Void doInBackground(Void... progress) {
                            user = Lib.login("hp@hp.com", "p");
                            user.setSession(Lib.getTimestampNow());
                            return null;
                        }
                        protected void onPostExecute(Void Void){
                            super.onPostExecute(Void);
                            if(user!=null) {
                                String role = user.getRole();
                                if (role.equals("USER")) {
                                    Intent intent = new Intent(getApplicationContext(), PatientView.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(user);
                                    intent.putExtra("USER", list);
                                    startActivity(intent);
                                    finish();
                                } else if (role.equals("HP")) {
                                    Intent intent = new Intent(getApplicationContext(), HealthProfessionalView.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(user);
                                    intent.putExtra("USER", list);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                            else{
                                Toast toast = Toast.makeText(LogIn.this, "Invalid Username or Password", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                            p.dismiss();
                        }
                    };
                    asyncTask.execute();
                }catch (Exception e){e.printStackTrace();}*/
            }
        });
    }

    //if the back button is pressed again during this activity, the stack is cleared and the app is told to exit.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }

}