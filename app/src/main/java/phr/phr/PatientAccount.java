package phr.phr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.Lib;
import phr.lib.Patient;
import phr.lib.Record;

/**
 * Created by Anupam on 25-Jan-18.
 */

public class PatientAccount extends AppCompatActivity {
    TextView name;
    TextView email;
    TextView phone;
    TextView region;
    TextView province;
    Button edit_user ;
    Button add_record;
    ListView record_list_view;
    Patient patient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.patient_account);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        region = findViewById(R.id.region);
        province = findViewById(R.id.province);
        edit_user = findViewById(R.id.edit_user_button);
        add_record = findViewById(R.id.add_record_button);
        record_list_view = findViewById(R.id.records_list_view);
        ArrayList<Patient> list = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        patient = list.get(0);
        RecordListViewAdapter adapter = new RecordListViewAdapter(this, patient.getRecords());
        record_list_view.setAdapter(adapter);

        edit_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientAccountUpdate.class);
                ArrayList<Patient> list = new ArrayList<Patient>();
                list.add(patient);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });

        add_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientRecordView.class);
                intent.putExtra("ID",patient.getId());
                startActivity(intent);
            }
        });

        record_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                Record Slecteditem= patient.getRecords().get(+position);
                Intent intent = new Intent(getApplicationContext(), PatientRecordView.class);
                ArrayList<Record> list = new ArrayList<Record>();
                list.add(Slecteditem);
                intent.putExtra("RECORD",list);
                startActivity(intent);

            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        try{
            patient = new AsyncTask<Void, Void, Patient>() {
                protected Patient doInBackground(Void... progress) {
                    Patient result = null;
                    result = Lib.makeUser(patient.getEmail());
                    return result;
                }
            }.execute().get();

        }catch(Exception e){e.printStackTrace();}

        setFields();
    }

    private void setFields(){
        name.setText(patient.getName());
        email.setText(patient.getEmail());
        phone.setText(patient.getPhone());
        region.setText(patient.getRegion());
        province.setText(patient.getProvince());
        RecordListViewAdapter adapter = new RecordListViewAdapter(this, patient.getRecords());
        record_list_view.setAdapter(adapter);
    }
}