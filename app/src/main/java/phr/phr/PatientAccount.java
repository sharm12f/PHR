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

import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 25-Jan-18.
 *
 * This is the users account, main features of the app start from here
 *
 * At the top the user is shows their name and email as a confermation of the account they are logged in as
 *
 * All of the users records are listed in the dbRegions view.
 *
 * Add record button allows the user to add a new record
 *
 * Edit permission button allows the user to give permission to healthprofessionals for all their records.
 *
 * Edit user details button allows the user to edit their own information
 *
 */

public class PatientAccount extends AppCompatActivity {
    TextView name;
    TextView email;
    Button edit_user, edit_perms_button;
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
        edit_perms_button = findViewById(R.id.edit_permissions_button);
        record_list_view = findViewById(R.id.records_list_view);

        // get the patient object
        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("USER");
        patient = (Patient)list.get(0);

        // if the patient object is null, then the user is sent back to the login page.
        if(patient==null){
            Toast toast = Toast.makeText(getApplicationContext(), "Error Making user", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        }
        try{
            edit_user.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PatientAccountUpdate.class);
                    ArrayList<User> list = new ArrayList<>();
                    list.add(patient);
                    intent.putExtra("USER",list);
                    startActivity(intent);
                    finish();
                }
            });

            add_record.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PatientRecordView.class);
                    ArrayList<User> list  = new ArrayList<>();
                    list.add(patient);
                    intent.putExtra("USER", list);
                    intent.putExtra("ID",patient.getId());
                    startActivity(intent);
                    finish();
                }
            });

            edit_perms_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), PatientAddPermissionAllRecords.class);
                    ArrayList<User> list = new ArrayList<>();
                    list.add(patient);
                    intent.putExtra("USER",list);
                    startActivity(intent);
                    finish();
                }
            });

            record_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO Auto-generated method stub
                    Record Slecteditem= patient.getRecords().get(+position);
                    Intent intent = new Intent(getApplicationContext(), PatientRecordView.class);
                    ArrayList<Record> list = new ArrayList<>();
                    list.add(Slecteditem);
                    ArrayList<User> list2  = new ArrayList<>();
                    list2.add(patient);
                    intent.putExtra("USER", list2);
                    intent.putExtra("RECORD",list);
                    startActivity(intent);
                    finish();
                }
            });
        }catch(Exception e){e.printStackTrace();}


    }

    //send the user back to the patient view, this is used to control the flow of the app regardless of the stack
    @Override
    public void onBackPressed() {
        System.out.println("Back Press"+patient.toString());
        Intent intent = new Intent(getApplicationContext(), PatientView.class);
        ArrayList<User> list = new ArrayList<>();
        list.add(patient);
        intent.putExtra("USER",list);
        startActivity(intent);
        finish();
    }

    // resets the patient records, gets the latest copy from the db so that any changes they may have made are reflected
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
                    patient.setRecords(Lib.getPatientRecords(patient.getEmail()));
                    return null;
                }
                protected void onPostExecute(Void Void){
                    super.onPostExecute(Void);
                    p.dismiss();
                    setFields();
                }
            };
            asyncTask.execute();
        }catch (Exception e){e.printStackTrace();}

    }

    private void setFields(){
        name.setText(patient.getName());
        email.setText(patient.getEmail());
        RecordListViewAdapter adapter = new RecordListViewAdapter(this, patient.getRecords());
        record_list_view.setAdapter(adapter);
    }
}
