package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 30-Jan-18.
 *
 * This activity shows the healthprofessional the record that they select
 *
 */

public class HealthProfessionalRecordView extends AppCompatActivity {
    TextView record_name_text, record_description_text, patient_name;
    Button leave_note_button;
    Record record;
    Patient patient;
    HealthProfessional healthProfessional;
    String GOTO;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.health_professional_view_record);
        record_name_text = findViewById(R.id.record_name_text);
        record_description_text = findViewById(R.id.record_decription_text);
        leave_note_button = findViewById(R.id.leave_note_button);
        patient_name=findViewById(R.id.patient_name_text);

        GOTO = (String)getIntent().getExtras().get("GOTO");


        ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
        ArrayList<User> list2 = (ArrayList<User>)getIntent().getExtras().get("USER");
        record = list.get(0);
        patient = (Patient)list2.get(0);
        healthProfessional = (HealthProfessional)list2.get(1);
        patient_name.setText(patient.getName());
        record_name_text.setText(record.getName());
        record_description_text.setText(record.getRecord());

        leave_note_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalLeaveNote.class);
                ArrayList<Record> list = new ArrayList<Record>();
                list.add(record);
                ArrayList<User> list2 = new ArrayList<User>();
                list2.add(patient);
                list2.add(healthProfessional);
                intent.putExtra("RECORD",list);
                intent.putExtra("USER",list2);
                intent.putExtra("GOTO", GOTO);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed() {
        if(GOTO.equals("ViewAllRecords")){
            Intent intent = new Intent(getApplicationContext(), HealthProfessionalViewAllRecords.class);
            ArrayList<User> list = new ArrayList<>();
            list.add(healthProfessional);
            intent.putExtra("HP",list);
            startActivity(intent);
        }
        else if(GOTO.equals("SinglePatient")){
            Intent intent = new Intent(getApplicationContext(), HealthProfessionalSinglePatient.class);
            ArrayList<User> list = new ArrayList<>();
            list.add(healthProfessional);
            list.add(patient);
            intent.putExtra("INFO",list);
            startActivity(intent);
        }
    }

}
