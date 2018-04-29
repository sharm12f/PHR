package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
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
 *
 * This view shows the health professional information regarding the patient, and all the records from this patient that they have access to.
 *
 * Top section of the page is patient information such as name, email, phone, region, and province
 *
 * The dbRegions of records is towards the bottom of the page.
 *
 */

public class HealthProfessionalSinglePatient extends AppCompatActivity {
    TextView patient_name_text, patient_email_text, patient_phone_text, patient_region_text, patient_province_text;
    ListView records_list_view;
    Patient patient;
    HealthProfessional healthProfessional;
    Button add_note_button;

    int position;

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

        //may add generic note feature. (not gonna be hard to do, the already existing activity can be used for this)
        add_note_button = findViewById(R.id.button2);
        add_note_button.setClickable(false);
        add_note_button.setVisibility(View.GONE);

        //get the user object
        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("USER");
        healthProfessional= (HealthProfessional)list.get(0);
        position = (int)getIntent().getExtras().get("POS");
        patient=healthProfessional.getPatient().get(position);

        //open the selected record.
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
                intent.putExtra("POS",position);
                startActivity(intent);
            }
        });

    }

    //control the flow of the app regardless of the stack
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
        ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
        list.add(healthProfessional);
        intent.putExtra("USER",list);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //pre-fill all the fields with known data
        setFields();
    }

    private void setFields(){
        patient_name_text.setText(patient.getName());
        patient_email_text.setText(patient.getEmail());
        patient_phone_text.setText(patient.getPhone());
        patient_region_text.setText(patient.getRegion());
        patient_province_text.setText(patient.getProvince());

        //pull the latest patient dbRegions, ensures that if access is revoked during an active session, then they wont be able to interact with it.
        try{
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(HealthProfessionalSinglePatient.this);
                protected void onPreExecute(){
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setIndeterminate(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.show();
                }
                protected Void doInBackground(Void... progress) {
                    healthProfessional.setPatient(Lib.makeHealthProfessionalPatientsList(healthProfessional.getId()));
                    return null;
                }
                protected void onPostExecute(Void Void){
                    super.onPostExecute(Void);
                    p.dismiss();
                    patient=healthProfessional.getPatient().get(position);
                    setRecordListView();
                }
            };
            asyncTask.execute();
        }catch(Exception e){e.printStackTrace();}

    }

    private void setRecordListView(){
        RecordListViewAdapter adapter = new RecordListViewAdapter(this, patient.getRecords());
        records_list_view.setAdapter(adapter);
    }

}
