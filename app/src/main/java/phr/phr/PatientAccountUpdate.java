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

import phr.lib.Patient;

/**
 * Created by Anupam on 26-Jan-18.
 *
 * This page allows the user to update their account information
 *
 * The page as edit text entries pre-filled for the following:
 *      Name, email, phone
 * Spinners with this current option as the selection allowing them to change it to another if needed
 * The spinners are for region and province
 *
 */

public class PatientAccountUpdate extends AppCompatActivity {
    EditText name_input;
    EditText email_input;
    EditText phone_input;
    Button update_button;
    Spinner regions;
    Spinner provinces;
    Patient patient =null;
    ArrayList<String> list;
    ArrayList<String> list2;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.patient_update_info);
        name_input = findViewById(R.id.name_input);
        email_input = findViewById(R.id.email_input);
        phone_input = findViewById(R.id.phone_input);
        update_button = findViewById(R.id.update_button);
        regions = findViewById(R.id.region_spinner);
        provinces = findViewById(R.id.province_spinner);

        // get the user object
        ArrayList<Patient> list = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        patient = list.get(0);

        //pre-fill all the options with what is already in the database about the user.
        setFields();

        // the user wants to update their data
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get all the information from all the edit text entries and spinners
                final String name = name_input.getText().toString();
                final String email = email_input.getText().toString();
                final String phone = phone_input.getText().toString();
                final String region = regions.getSelectedItem().toString();
                final String province = provinces.getSelectedItem().toString();
                try{
                    AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog p = new ProgressDialog(PatientAccountUpdate.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setCancelable(false);
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }
                        protected Boolean doInBackground(Void... progress) {
                            Boolean result;
                            result = Lib.PatientUpdate(name,email,phone,region,province,patient.getId());

                            return result;
                        }
                        protected void onPostExecute(Boolean result){
                            super.onPostExecute(result);
                            p.dismiss();
                            if(result) {
                                patient.setName(name);
                                patient.setEmail(email);
                                patient.setPhone(phone);
                                patient.setRegion(region);
                                patient.setProvince(province);
                                Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
                                ArrayList<Patient> list = new ArrayList<Patient>();
                                list.add(patient);
                                intent.putExtra("USER",list);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Error Updating User", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    };
                    asyncTask.execute();
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }


    //control the flow of the app regardless of the stack
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
        ArrayList<Patient> list = new ArrayList<Patient>();
        list.add(patient);
        intent.putExtra("USER",list);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // retrieve the spinner data from the database, and set the edit text of with user data
    private void setFields(){
        name_input.setText(patient.getName());
        email_input.setText(patient.getEmail());
        phone_input.setText(patient.getPhone());
        try {
            final AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(PatientAccountUpdate.this);
                protected void onPreExecute(){
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setCancelable(false);
                    p.setIndeterminate(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.show();
                }
                protected Void doInBackground(Void... progress) {
                    list = Lib.getRegions();
                    list2 = Lib.getProvinces();
                    return null;
                }
                protected void onPostExecute(Void Void){
                    super.onPostExecute(Void);
                    p.dismiss();
                    loadSpinners(list,list2);
                }
            };
            asyncTask.execute();
        }catch (Exception e){e.printStackTrace();}
    }

    // load all the spinners.
    private void loadSpinners(ArrayList<String> list, ArrayList<String> list2){
        try {
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            regions.setAdapter(dataAdapter);
            int s = dataAdapter.getPosition(patient.getRegion());
            regions.setSelection(s);

            ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, list2);
            dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            provinces.setAdapter(dataAdapter2);
            int p = dataAdapter2.getPosition(patient.getProvince());
            provinces.setSelection(p);
        }catch (Exception e){e.printStackTrace();}
    }
}
