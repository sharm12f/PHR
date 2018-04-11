package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 28-Mar-18.
 */

public class HealthProfessionalSinglePatient extends AppCompatActivity {
    TextView patient_name_text, patient_email_text, patient_phone_text, patient_region_text, patient_province_text;
    ListView records_list_view;
    Patient patient;
    HealthProfessional healthProfessional;
    Button add_note_button;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.healthprofessional_single_patient);
        patient_name_text = findViewById(R.id.patient_name_text);
        patient_email_text = findViewById(R.id.patient_email_text);
        patient_phone_text = findViewById(R.id.patient_phone_text);
        patient_region_text = findViewById(R.id.patient_region_text);
        patient_province_text = findViewById(R.id.patient_province_text);
        records_list_view = findViewById(R.id.records_list_view);

        //not sure if this button is needed so gonna hide it for now
        add_note_button = findViewById(R.id.button2);
        add_note_button.setClickable(false);
        add_note_button.setVisibility(View.GONE);

        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("INFO");
        healthProfessional= (HealthProfessional)list.get(0);
        patient = (Patient)list.get(1);

        setFields();

        records_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Record Slecteditem= patient.getRecords().get(+position);
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalRecordView.class);
                ArrayList<Record> list = new ArrayList<Record>();
                ArrayList<User> list2 = new ArrayList<User>();
                list2.add(patient);
                list2.add(healthProfessional);
                list.add(Slecteditem);
                intent.putExtra("RECORD",list);
                intent.putExtra("USER",list2);
                intent.putExtra("GOTO","SinglePatient");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
        ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
        list.add(healthProfessional);
        intent.putExtra("HP",list);
        startActivity(intent);
    }

    private void setFields(){
        patient_name_text.setText(patient.getName());
        patient_email_text.setText(patient.getEmail());
        patient_phone_text.setText(patient.getPhone());
        patient_region_text.setText(patient.getRegion());
        patient_province_text.setText(patient.getProvince());

        RecordListViewAdapter adapter = new RecordListViewAdapter(this, patient.getRecords());
        records_list_view.setAdapter(adapter);


    }
}
