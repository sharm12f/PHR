package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 26-Feb-18.
 *
 * This page shows the user dbRegions of all the records they have access to.
 *
 * The user can select a record to view it in detail and/or leave a note regarding the record.
 *
 */

public class HealthProfessionalViewAllRecords extends AppCompatActivity {

    HealthProfessional healthProfessional;
    ListView record_list_view;
    ArrayList<String[]> rcs;
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.healthprofessional_view_all_records);
        record_list_view = findViewById(R.id.records_list_view);

        rcs = new ArrayList<String[]>();


        //get the user object
        ArrayList<HealthProfessional> intent_extra = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        healthProfessional = intent_extra.get(0);


        //opens the selected record.
        record_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                int i = Integer.parseInt(rcs.get(+position)[2]);
                int j = Integer.parseInt(rcs.get(+position)[3]);
                ArrayList<Record> list = new ArrayList<Record>();
                ArrayList<User> list2 = new ArrayList<User>();
                Record Slecteditem= healthProfessional.getPatient().get(i).getRecords().get(j);
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalRecordView.class);
                list2.add(healthProfessional.getPatient().get(i));
                list2.add(healthProfessional);
                list.add(Slecteditem);
                intent.putExtra("POS",j);
                intent.putExtra("RECORD",list);
                intent.putExtra("USER",list2);
                //used to let the record view know to come back to this page on back press.
                intent.putExtra("GOTO","ViewAllRecords");
                startActivity(intent);
                finish();
            }
        });
    }

    //control the flow of the app regardless of the stack
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
        ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
        list.add(healthProfessional);
        intent.putExtra("USER",list);
        startActivity(intent);
        finish();
    }

    //ensure the dbRegions only has records they have access to, if access was revoked during their session.
    @Override
    protected void onResume() {
        super.onResume();
        //pull the latest patient dbRegions, ensures that if access is revoked during an active session, then they wont be able to interact with it.
        try{
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(HealthProfessionalViewAllRecords.this);
                protected void onPreExecute(){
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setCancelable(false);
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
                    setPatientListView();
                }
            };
            asyncTask.execute();
        }catch(Exception e){e.printStackTrace();}
    }


    //show the dbRegions in the dbRegions view.
    private void setPatientListView(){
        // this a rcs dbRegions is used for the dbRegions view to show patient name and note name. (easiest way i could think of doing it)
        // i and j in this dbRegions represent Ith patient's Jth record.
        ArrayList<Patient> temp = healthProfessional.getPatient();
        for (int i=0; i<temp.size();i++){
            Patient p = temp.get(i);
            ArrayList<Record> r = p.getRecords();
            for(int j = 0; j<r.size();j++){
                String[] l = new String[4];
                l[0] = p.getName();
                l[1] = r.get(j).getName();
                l[2] = ""+i;
                l[3] = ""+j;
                rcs.add(l);
            }
        }
        HealthProfessionalRecordListViewAdapter adapter = new HealthProfessionalRecordListViewAdapter(this, rcs);
        record_list_view.setAdapter(adapter);
    }

}
