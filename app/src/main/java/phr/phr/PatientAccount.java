package phr.phr;

import android.app.ProgressDialog;
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
import android.widget.Toast;

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
        edit_user = findViewById(R.id.edit_user_button);
        add_record = findViewById(R.id.add_record_button);
        record_list_view = findViewById(R.id.records_list_view);
        ArrayList<Patient> list = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        patient = list.get(0);
        if(patient==null){
            Toast toast = Toast.makeText(getApplicationContext(), "Error Making user", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
        }

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
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(PatientAccount.this);
                protected void onPreExecute(){
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setIndeterminate(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.show();
                }
                protected Void doInBackground(Void... progress) {
                    patient = Lib.makeUser(patient.getEmail());
                    return null;
                }
                protected void onPostExecute(Void Void){
                    super.onPostExecute(Void);
                    p.dismiss();
                    if(patient==null){
                        Toast toast = Toast.makeText(getApplicationContext(), "Error Making user", Toast.LENGTH_SHORT);
                        toast.show();
                        Intent intent = new Intent(getApplicationContext(), LogIn.class);
                        startActivity(intent);
                    }
                    else{
                        setFields();
                    }
                }
            };
            asyncTask.execute();

        }catch(Exception e){e.printStackTrace();}
    }

    private void setFields(){
        name.setText(patient.getName());
        email.setText(patient.getEmail());
        RecordListViewAdapter adapter = new RecordListViewAdapter(this, patient.getRecords());
        record_list_view.setAdapter(adapter);
    }
}
