package phr.phr;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

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
    Button leave_not_button;
    Record record;
    Patient patient;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.health_professional_view_record);
        record_name_text = findViewById(R.id.record_name_text);
        record_description_text = findViewById(R.id.record_decription_text);
        leave_not_button = findViewById(R.id.leave_note_button);
        patient_name=findViewById(R.id.patient_name_text);

        if(getIntent().getExtras()!=null){
            ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
            ArrayList<Patient> list2 = (ArrayList<Patient>)getIntent().getExtras().get("USER");
            record = list.get(0);
            patient = list2.get(0);
            patient_name.setText(patient.getName());
            record_name_text.setText(record.getName());
            record_description_text.setText(record.getRecord());
        }
    }

}
