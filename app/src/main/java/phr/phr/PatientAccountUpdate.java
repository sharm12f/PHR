package phr.phr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import phr.lib.Lib;
import phr.lib.Patient;

/**
 * Created by Anupam on 26-Jan-18.
 */

public class PatientAccountUpdate extends AppCompatActivity {
    EditText name_input;
    EditText email_input;
    EditText phone_input;
    Button update_button;
    Spinner regions;
    Spinner provinces;
    Boolean Success = false;
    Patient patient =null;
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
        ArrayList<Patient> list = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        patient = list.get(0);
        setFields();
        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = name_input.getText().toString();
                final String email = email_input.getText().toString();
                final String phone = phone_input.getText().toString();
                final String region = regions.getSelectedItem().toString();
                final String province = provinces.getSelectedItem().toString();
                try{
                    Success = new AsyncTask<Void, Void, Boolean>() {
                        protected Boolean doInBackground(Void... progress) {
                            boolean result = false;
                            result = Lib.PatientUpdate(name,email,phone,region,province);
                            patient.setfName(name.split(" ")[0]);
                            patient.setlName(name.split(" ")[1]);
                            patient.setEmail(email);
                            patient.setPhone(phone);
                            patient.setRegion(region);
                            patient.setProvince(province);
                            System.out.println("Update: " +  result);
                            return result;
                        }
                    }.execute().get();
                    if(Success){
                        System.out.println("Finish");
                        finish();
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    private void setFields(){
        name_input.setText(patient.getfName()+" "+ patient.getlName());
        email_input.setText(patient.getEmail());
        phone_input.setText(patient.getPhone());
        try {
            ArrayList<String> list = new AsyncTask<Void, Void, ArrayList<String>>() {
                protected ArrayList<String> doInBackground(Void... progress) {
                    ArrayList<String> list = Lib.getRegions();
                    return list;
                }
            }.execute().get();
            ArrayList<String> list2 = new AsyncTask<Void, Void, ArrayList<String>>() {
                protected ArrayList<String> doInBackground(Void... progress) {
                    ArrayList<String> list = Lib.getProvinces();
                    return list;
                }
            }.execute().get();

            loadSpinners(list,list2);
        }catch (Exception e){e.printStackTrace();}
    }

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
