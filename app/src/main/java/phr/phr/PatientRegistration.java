package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Lib;
import phr.lib.Patient;
import phr.lib.User;

/**
 * Created by Anupam on 26-Jan-18.
 */

public class PatientRegistration extends AppCompatActivity {
    Boolean Success = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.patient_registration);
        final EditText Ename = findViewById(R.id.name_input);
        final EditText Eemail = findViewById(R.id.email_input);
        final EditText Ephone = findViewById(R.id.phone_input);
        final EditText Epassword = findViewById(R.id.password_input);
        final EditText Ere_password = findViewById(R.id.re_pass_input);
        final Button create = findViewById(R.id.create_button);
        final Spinner Sregion = findViewById(R.id.region_spinner);
        final Spinner Sprovinces = findViewById(R.id.province_spinner);


        new AsyncTask<Void, Void, Void>(){
            private ProgressDialog p = new ProgressDialog(PatientRegistration.this);
            protected void onPreExecute(){
                super.onPreExecute();
                p.setMessage("Loading");
                p.setIndeterminate(false);
                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                p.show();
            }
            protected Void doInBackground(Void... progress){
                loadSpinners(Sregion, Sprovinces);
                return null;
            }
            protected void onPostExecute(Void Void){
                super.onPostExecute(Void);
                p.dismiss();
            }
        }.execute();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = Ename.getText().toString();
                final String email = Eemail.getText().toString();
                final String phone = Ephone.getText().toString();
                final String password = Epassword.getText().toString();
                final String re_password = Ere_password.getText().toString();
                final String region = Sregion.getSelectedItem().toString();
                final String province = Sprovinces.getSelectedItem().toString();
                try{
                    AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog p = new ProgressDialog(PatientRegistration.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }
                        protected Boolean doInBackground(Void... progress) {
                            return Lib.PatientRegister(name , email, password, re_password, phone, region, province);
                        }
                        protected void onPostExecute(Boolean result){
                            super.onPostExecute(result);
                            p.dismiss();
                            if(result){
                                Patient patient = Lib.makeUser(email);
                                if(patient!=null) {
                                    Intent intent = new Intent(getApplicationContext(), PatientView.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(patient);
                                    intent.putExtra("USER", list);
                                    startActivity(intent);
                                }
                                else{
                                    Toast toast = Toast.makeText(getApplicationContext(), "Creation Error", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Creation Error", Toast.LENGTH_SHORT);
                                toast.show();
                            }
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
        startActivity(intent);
    }

    private void loadSpinners(Spinner regions, Spinner provinces){
        ArrayList<String> list = Lib.getRegions();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regions.setAdapter(dataAdapter);

        ArrayList<String> list2 = Lib.getProvinces();
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinces.setAdapter(dataAdapter2);

    }
}
