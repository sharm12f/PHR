package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;
import phr.lib.HealthProfessional;
import phr.lib.Lib;

/**
 * Created by Anupam on 29-Jan-18.
 *
 * This file the logic for the HealthProfessional Account - they get to this by select "my account" on the health professional view page
 *
 * List the users name and email at the top, the patients that they have access to, with the list of those patients in the list view, and two buttons at the bottom to view all records they have access to and edit their user detail
 *
 *
 */

public class HealthProfessionalAccount extends AppCompatActivity {

    //Variables used in this class, these are from the layout.
    TextView name_text, email_text;
    Button edit_button, view_all_records;
    HealthProfessional healthProfessional;
    ListView patients_listview;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.healthprofessional_account);
        name_text = findViewById(R.id.name_text);
        email_text = findViewById(R.id.email_text);
        edit_button = findViewById(R.id.edit_button);
        view_all_records = findViewById(R.id.view_all_records_button);
        patients_listview = findViewById(R.id.patients_list_view);

        //ensure that you get a valid healthprofessional object when this activity is called, go back to login activity if not
        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("HP");
        healthProfessional= list.get(0);
        if(healthProfessional==null){
            Toast toast = Toast.makeText(getApplicationContext(), "Error Making User", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
        }

        HealthProfessionalPatientListViewAdapter adapter = new HealthProfessionalPatientListViewAdapter(this, healthProfessional.getPatient());
        patients_listview.setAdapter(adapter);

        //open the health professional account edit activity
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccountUpdate.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(healthProfessional);
                intent.putExtra("HP",list);
                startActivity(intent);
            }
        });

        //open the view all records activity for the health professional
        view_all_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalViewAllRecords.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(healthProfessional);
                intent.putExtra("HP",list);
                startActivity(intent);
            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        try{
            AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(HealthProfessionalAccount.this);
                protected void onPreExecute(){
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setIndeterminate(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.show();
                }
                protected Void doInBackground(Void... progress) {
                    healthProfessional = Lib.makeHealthProfessional(healthProfessional.getEmail());
                    return null;
                }
                protected void onPostExecute(Void Void){
                    super.onPostExecute(Void);
                    p.dismiss();
                    if(healthProfessional==null) {
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
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalView.class);
        ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
        list.add(healthProfessional);
        intent.putExtra("HP",list);
        startActivity(intent);
    }

    private void setFields(){
        name_text.setText(healthProfessional.getName());
        email_text.setText(healthProfessional.getEmail());
    }
}
