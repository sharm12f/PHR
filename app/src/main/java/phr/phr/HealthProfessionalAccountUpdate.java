package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

import phr.lib.HealthProfessional;


/**
 * Created by Anupam on 20-Feb-18.
 *
 *  This file is the logic for the health professional account update - they get here by pressing update account info
 *
 *
 */

public class HealthProfessionalAccountUpdate extends AppCompatActivity {
    EditText name_input;
    EditText email_input;
    EditText phone_input;
    Button update_button;
    Spinner regionsSpinner;
    Spinner provincesSpinner;
    Spinner organizationSpinner;
    Spinner departmentSpinner;
    Spinner healthprofessionalSpinner;

    Boolean Success = false;
    HealthProfessional healthProfessional;
    Button update;
    ArrayList<String> dbRegions, dbProvince, dbOrganization, dbDepartment, dbHealthProfessionalProfession;
    protected void onCreate(Bundle SavedInstance){
        super.onCreate(SavedInstance);
        setContentView(R.layout.healthprofessional_update_info);
        name_input = findViewById(R.id.name_input);
        email_input = findViewById(R.id.email_input);
        phone_input = findViewById(R.id.phone_input);
        update_button = findViewById(R.id.update_button);
        regionsSpinner = findViewById(R.id.region_spinner);
        provincesSpinner = findViewById(R.id.province_spinner);
        organizationSpinner = findViewById(R.id.organization_spinner);
        departmentSpinner = findViewById(R.id.department_spinner);
        healthprofessionalSpinner = findViewById(R.id.healthprofessional_spinner);
        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        healthProfessional = list.get(0);
        //ensure that a hp object is recieved into the activity
        if(healthProfessional == null) {
            Toast toast = Toast.makeText(getApplicationContext(), "Error Making User", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
        }

        update = findViewById(R.id.update_button);

        // fill all the fields with the users infor that we already have in the database
        setFields();

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = name_input.getText().toString();
                final String email = email_input.getText().toString();
                final String phone = phone_input.getText().toString();
                final String region = regionsSpinner.getSelectedItem().toString();
                final String province = provincesSpinner.getSelectedItem().toString();
                final String organizaton = organizationSpinner.getSelectedItem().toString();
                final String department = departmentSpinner.getSelectedItem().toString();
                final String healthprofessionalProfession = healthprofessionalSpinner.getSelectedItem().toString();
                // the following async task tries to update the users info
                try{
                    AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog p = new ProgressDialog(HealthProfessionalAccountUpdate.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setIndeterminate(false);
                            p.setCancelable(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();

                        }
                        protected Boolean doInBackground(Void... progress) {
                            Boolean result = Lib.HealthProfessionalUpdate(name,email,phone,region,province,organizaton,department,healthprofessionalProfession,healthProfessional.getId());
                            return result;
                        }
                        protected void onPostExecute(Boolean result){
                            super.onPostExecute(result);
                            p.dismiss();
                            // if the update is success full then go back to the account page
                            if(result){
                                healthProfessional.setName(name);
                                healthProfessional.setEmail(email);
                                healthProfessional.setPhone(phone);
                                healthProfessional.setRegion(region);
                                healthProfessional.setProvince(province);
                                healthProfessional.setOrganization(organizaton);
                                healthProfessional.setDepartment(department);
                                healthProfessional.setHealthProfessional(healthprofessionalProfession);
                                Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
                                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                                list.add(healthProfessional);
                                intent.putExtra("USER",list);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Update Error", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    };
                    asyncTask.execute();
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    // set all the fields with the users pre-exesting info
    private void setFields(){
        name_input.setText(healthProfessional.getName());
        email_input.setText(healthProfessional.getEmail());
        phone_input.setText(healthProfessional.getPhone());
        try {
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(HealthProfessionalAccountUpdate.this);
                protected void onPreExecute(){
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setCancelable(false);
                    p.setIndeterminate(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.show();
                }
                protected Void doInBackground(Void... progress) {
                    dbRegions = Lib.getRegions();
                    dbProvince = Lib.getProvinces();
                    dbOrganization = Lib.getOrganization();
                    dbDepartment = Lib.getDepartment();
                    dbHealthProfessionalProfession = Lib.getHealthProfessional();
                    return null;
                }
                protected void onPostExecute(Void Void){
                    super.onPostExecute(Void);
                    p.dismiss();
                    loadSpinners(dbRegions,dbProvince,dbOrganization,dbDepartment,dbHealthProfessionalProfession);
                }
            };
            asyncTask.execute();
        }catch (Exception e){e.printStackTrace();}
    }

    // control where the user goes when they press the back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
        ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
        list.add(healthProfessional);
        intent.putExtra("USER",list);
        startActivity(intent);
        finish();
    }


    // sets the values for all the drop down options
    private void loadSpinners(ArrayList<String> dbRegions, ArrayList<String> dbProvince, ArrayList<String> dbOrganization, ArrayList<String> dbDepartment, ArrayList<String> dbHealthProfessionalProfession){
        try {

            ArrayAdapter<String> regionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbRegions);
            regionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            regionsSpinner.setAdapter(regionAdapter);
            regionsSpinner.setSelection(regionAdapter.getPosition(healthProfessional.getRegion()));

            ArrayAdapter<String> provinceAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbProvince);
            provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            provincesSpinner.setAdapter(provinceAdapter);
            provincesSpinner.setSelection(provinceAdapter.getPosition(healthProfessional.getProvince()));

            ArrayAdapter<String> organizationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbOrganization);
            organizationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            organizationSpinner.setAdapter(organizationAdapter);
            organizationSpinner.setSelection(organizationAdapter.getPosition(healthProfessional.getOrganization()));

            ArrayAdapter<String> departmentAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbDepartment);
            departmentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            departmentSpinner.setAdapter(departmentAdapter);
            departmentSpinner.setSelection(departmentAdapter.getPosition(healthProfessional.getDepartment()));

            ArrayAdapter<String> hpAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbHealthProfessionalProfession);
            hpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            healthprofessionalSpinner.setAdapter(hpAdapter);
            healthprofessionalSpinner.setSelection(hpAdapter.getPosition(healthProfessional.getHealthProfessional()));

        }catch (Exception e){e.printStackTrace();}
    }
}
