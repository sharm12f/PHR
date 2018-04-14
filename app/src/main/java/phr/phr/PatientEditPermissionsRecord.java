package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PrivilegedAction;
import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Lib;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.RecordPermission;
import phr.lib.User;

/**
 * Created by Anupam on 10-Apr-18.
 *
 * This view allows the user to edit the permission for a specific record
 *
 * At the top of the view is the name of the record
 * a dbRegions of health professional's with access to the record is shown
 *
 * The user can take the following actions
 *  1 - grand permission to a new health professional
 *  2 - revoke permission from a health professional
 *
 *  To Add permission they simply select the add permission button
 *
 *  To revoke permission they have to select a health professional from the dbRegions and then select revoke permission
 */

public class PatientEditPermissionsRecord extends AppCompatActivity {
    TextView record_name, health_professionla_list_text_view;
    Button select, add_perms, revoke_perms;
    ListView perms_list_view;
    Record record;
    ArrayList<RecordPermission> perms_list;

    Patient patient;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_permissions_page);

        ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
        record = list.get(0);

        ArrayList<Patient> list2 = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        patient = list2.get(0);

        record_name = findViewById(R.id.title_text_view);
        select = findViewById(R.id.button1);
        add_perms = findViewById(R.id.button2);
        revoke_perms = findViewById(R.id.button3);
        perms_list_view = findViewById(R.id.perms_list_view);
        health_professionla_list_text_view = findViewById(R.id.text_view1);

        //set the perms dbRegions view to only one choice selection
        perms_list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        perms_list_view.setSelector(R.color.darkGray);

        // this button is not needed for this view
        select.setClickable(false);
        select.setVisibility(View.GONE);

        //Set a text view to indicate this is dbRegions of Health Proofessional
        health_professionla_list_text_view.setText("Health Professionals with Permissions");

        //set the record name
        record_name.setText(record.getName());

        // set the dbRegions of health professional's with access to this record
        setPermsListView();

        // take the user to the add permission page so they can find the health professional they want to grand permission to
        add_perms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientAddPermissionRecord.class);
                ArrayList<Record> list = new ArrayList<>();
                list.add(record);
                ArrayList<User> list2 = new ArrayList<>();
                list2.add(patient);
                intent.putExtra("USER",list2);
                intent.putExtra("RECORD", list);
                startActivity(intent);
                finish();
            }
        });


        // revoke the permission from the health professional selected from the dbRegions.
        revoke_perms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ensure that a selection is made
                final int position = perms_list_view.getCheckedItemPosition();
                if(position != -1){
                    AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog p = new ProgressDialog(PatientEditPermissionsRecord.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }
                        protected Boolean doInBackground(Void... progress) {
                            boolean result = false;
                            RecordPermission recordPermission = perms_list.get(position);
                            boolean permsExist = Lib.permsExist(recordPermission);
                            if(permsExist)
                                result=Lib.revokePermission(recordPermission);
                            return result;
                        }
                        protected void onPostExecute(Boolean result){
                            super.onPostExecute(result);
                            p.dismiss();
                            // reload the dbRegions view after the permissions have been revoked.
                            if(result){
                                setPermsListView();
                                Toast toast = Toast.makeText(getApplicationContext(), "Permission Revoked", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        }
                    };
                    asyncTask.execute();
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select a Health Professional", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

    }

    // ensure the dbRegions is always uptodate evenduring the same session
    @Override
    protected void onResume() {
        super.onResume();

        setPermsListView();
    }

    //get the permisson for the record from the database.
    private void setPermsListView() {
        //get all the perms for this record
        try{
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(PatientEditPermissionsRecord.this);
                protected void onPreExecute(){
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setIndeterminate(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.show();
                }
                protected Void doInBackground(Void... progress) {
                    perms_list = Lib.getRecordPerms(record.getId());
                    return null;
                }
                protected void onPostExecute(Void Void){
                    super.onPostExecute(Void);
                    p.dismiss();
                    if(perms_list==null){
                        Toast toast = Toast.makeText(getApplicationContext(), "Error getting permissions", Toast.LENGTH_SHORT);
                        toast.show();
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
        //set the listView
        PermissionListViewAdapter adapter = new PermissionListViewAdapter(this, perms_list);
        perms_list_view.setAdapter(adapter);
    }

    // control the flow of the app regardless of the stack
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), PatientRecordView.class);
        ArrayList<Record> list = new ArrayList<>();
        list.add(record);
        ArrayList<User> list2  = new ArrayList<>();
        list2.add(patient);
        intent.putExtra("USER", list2);
        intent.putExtra("RECORD",list);
        startActivity(intent);
        finish();
    }

}
