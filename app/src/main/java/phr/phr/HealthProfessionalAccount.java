package phr.phr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Lib;
import phr.lib.Patient;

/**
 * Created by Anupam on 29-Jan-18.
 */

public class HealthProfessionalAccount extends AppCompatActivity {
    TextView name_text, email_text, phone_text, region_text;
    Button edit_button, view_all_records;
    HealthProfessional healthProfessional;
    ListView patients_listview;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.healthprofessional_account);
        name_text = findViewById(R.id.name_text);
        email_text = findViewById(R.id.email_text);
        phone_text = findViewById(R.id.phone_text);
        region_text = findViewById(R.id.region_text);
        edit_button = findViewById(R.id.edit_button);
        view_all_records = findViewById(R.id.view_all_records_button);
        patients_listview = findViewById(R.id.patients_list_view);

        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        healthProfessional= list.get(0);

        HealthProfessionalPatientListViewAdapter adapter = new HealthProfessionalPatientListViewAdapter(this, healthProfessional.getPatient());
        patients_listview.setAdapter(adapter);

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccountUpdate.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(healthProfessional);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });

        view_all_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalViewAllRecords.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(healthProfessional);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        try{
            healthProfessional = new AsyncTask<Void, Void, HealthProfessional>() {
                protected HealthProfessional doInBackground(Void... progress) {
                    HealthProfessional result = null;
                    result = Lib.makeHealthProfessional(healthProfessional.getEmail());
                    return result;
                }
            }.execute().get();

        }catch(Exception e){e.printStackTrace();}

        setFields();
    }

    private void setFields(){
        name_text.setText(healthProfessional.getName());
        email_text.setText(healthProfessional.getEmail());
        phone_text.setText(healthProfessional.getPhone());
        region_text.setText(healthProfessional.getRegion());
    }
}
