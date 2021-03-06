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

/**
 * Created by Anupam on 26-Jan-18.
 */

public class PatientRegistration extends AppCompatActivity {
    Boolean Success = false;

    ArrayList<String> r, pro;

    Spinner Sregion;
    Spinner Sprovinces;
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
        Sregion = findViewById(R.id.region_spinner);
        Sprovinces = findViewById(R.id.province_spinner);

        setFields();

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
                            p.setCancelable(false);
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
                                Toast toast = Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_LONG);
                                toast.show();
                                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                startActivity(intent);
                                finish();
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

    private void  setFields(){
        new AsyncTask<Void, Void, Void>(){
            private ProgressDialog p = new ProgressDialog(PatientRegistration.this);
            protected void onPreExecute(){
                super.onPreExecute();
                p.setMessage("Loading");
                p.setCancelable(false);
                p.setIndeterminate(false);
                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                p.show();
            }
            protected Void doInBackground(Void... progress){
                r = Lib.getRegions();
                pro = Lib.getProvinces();
                return null;
            }
            protected void onPostExecute(Void Void) {
                super.onPostExecute(Void);
                p.dismiss();
                loadSpinners(r,pro);
            }
        }.execute();
    }

    private void loadSpinners(ArrayList<String> r, ArrayList<String> pro){

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, r);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sregion.setAdapter(dataAdapter);


        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, pro);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sprovinces.setAdapter(dataAdapter2);

    }
}
