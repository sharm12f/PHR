package phr.phr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Lib;


/**
 * Created by Anupam on 20-Feb-18.
 */

public class HealthProfessionalAccountUpdate extends AppCompatActivity {
    EditText name_input;
    EditText email_input;
    EditText phone_input;
    Button update_button;
    Spinner regions;
    Spinner provinces;
    Boolean Success = false;
    HealthProfessional healthProfessional;
    Button update;
    protected void onCreate(Bundle SavedInstance){
        super.onCreate(SavedInstance);
        setContentView(R.layout.healthprofessional_update_info);
        name_input = findViewById(R.id.name_input);
        email_input = findViewById(R.id.email_input);
        phone_input = findViewById(R.id.phone_input);
        update_button = findViewById(R.id.update_button);
        regions = findViewById(R.id.region_spinner);
        provinces = findViewById(R.id.province_spinner);
        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        healthProfessional = list.get(0);
        update = findViewById(R.id.update_button);
        setFields();
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = name_input.getText().toString();
                final String email = email_input.getText().toString();
                final String phone = phone_input.getText().toString();
                final String region = regions.getSelectedItem().toString();
                try{
                    Success = new AsyncTask<Void, Void, Boolean>() {
                        protected Boolean doInBackground(Void... progress) {
                            boolean result = false;
                            result = Lib.HealthProfessionalUpdate(name,email,phone,region);
                            healthProfessional.setfName(name.split(" ")[0]);
                            healthProfessional.setlName(name.split(" ")[1]);
                            healthProfessional.setEmail(email);
                            healthProfessional.setPhone(phone);
                            healthProfessional.setRegion(region);
                            System.out.println("Update: " +  result);
                            return result;
                        }
                    }.execute().get();
                    if(Success){
                        finish();
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    private void setFields(){
        name_input.setText(healthProfessional.getfName()+" "+ healthProfessional.getlName());
        email_input.setText(healthProfessional.getEmail());
        phone_input.setText(healthProfessional.getPhone());
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
            int s = dataAdapter.getPosition(healthProfessional.getRegion());
            regions.setSelection(s);

        }catch (Exception e){e.printStackTrace();}
    }
}
