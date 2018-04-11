package phr.phr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Lib;
import phr.lib.Record;
import phr.lib.User;

public class LogIn extends AppCompatActivity {
    Boolean test = false;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey("EXIT")){
                boolean exit = (boolean)getIntent().getExtras().get("EXIT");
                if(exit)
                    finish();
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
                            if(email_input.getText().toString().equals("") || password_input.getText().toString().equals(""))
                                return null;
                            user = Lib.login(email_input.getText().toString(), password_input.getText().toString());
                            //user = Lib.login("app@app.com", "p");
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
                                } else if (role.equals("HP")) {
                                    Intent intent = new Intent(getApplicationContext(), HealthProfessionalView.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(user);
                                    intent.putExtra("HP", list);
                                    startActivity(intent);
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
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
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


        reset_password_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
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
                                } else if (role.equals("HP")) {
                                    Intent intent = new Intent(getApplicationContext(), HealthProfessionalView.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(user);
                                    intent.putExtra("HP", list);
                                    startActivity(intent);
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
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("EXIT", true);
        startActivity(intent);
    }

}