package phr.phr;

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

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Lib;
import phr.lib.Patient;
import phr.lib.User;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                    /*
                    Patient patient = new AsyncTask<Void, Void, Patient>() {
                        protected Patient doInBackground(Void... progress) {
                            System.out.println("Start Login");
                            return Lib.login("app@app.com", "password");
                        }
                    }.execute().get();
                    */
                    if(email_input.getText().toString().equals("") || password_input.getText().toString().equals(""))
                        return;

                    User user = new AsyncTask<Void, Void, User>() {
                        protected User doInBackground(Void... progress) {
                            System.out.println("Start Login");
                            User test = Lib.login(email_input.getText().toString(), password_input.getText().toString());
                            return test;
                        }
                    }.execute().get();

                    String role = user.getRole();
                    if(role.equals("USER")){
                        Intent intent = new Intent(getApplicationContext(), PatientView.class);
                        ArrayList<User> list = new ArrayList<User>();
                        list.add(user);
                        intent.putExtra("USER",list);
                        startActivity(intent);
                    }
                    else if(role.equals("HP")){
                        Intent intent = new Intent(getApplicationContext(), HealthProfessionalView.class);
                        ArrayList<User> list = new ArrayList<User>();
                        list.add(user);
                        intent.putExtra("USER",list);
                        startActivity(intent);
                    }

                }catch (Exception e){e.printStackTrace();}
            }
        });
        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
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
    }
}