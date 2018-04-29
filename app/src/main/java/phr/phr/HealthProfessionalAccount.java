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
import android.widget.Toast;
import java.util.ArrayList;
import phr.lib.HealthProfessional;
import phr.lib.User;

/**
 * Created by Anupam on 29-Jan-18.
 *
 * This is the health professionals account page,
 * Top of the page shows the user their name and email, to indicate the right account is logged in
 *
 * A dbRegions of patients is displayed, they can select a patient to view records by this patient.
 *
 * A view all records button shows the user all the records from all the patients they have access to
 *
 * An edit user details button allows the user to edit their account infromation.
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
        view_all_records = findViewById(R.id.button2);
        patients_listview = findViewById(R.id.patients_list_view);

        //ensure that you get a valid healthProfessional object when this activity is called, go back to login activity if not
        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        healthProfessional= list.get(0);
        if(healthProfessional==null){
            Toast toast = Toast.makeText(getApplicationContext(), "Error Making User", Toast.LENGTH_SHORT);
            toast.show();
            Intent intent = new Intent(getApplicationContext(), LogIn.class);
            startActivity(intent);
            finish();
        }

        //pre-fill all the fields with known data
        setFields();


        //open the health professional account edit activity
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccountUpdate.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(healthProfessional);
                intent.putExtra("USER",list);
                startActivity(intent);
                finish();
            }
        });

        //open the view all records activity for the health professional
        view_all_records.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalViewAllRecords.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(healthProfessional);
                intent.putExtra("USER",list);
                startActivity(intent);
                finish();
            }
        });

        //open the patients view and show them information regarding the patient.
        patients_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //send the health professional and the position off the patient they selected form the dbRegions
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalSinglePatient.class);
                ArrayList<User> list = new ArrayList<User>();
                list.add(healthProfessional);
                intent.putExtra("USER",list);
                intent.putExtra("POS",+position);
                startActivity(intent);
                finish();
            }
        });
    }

    // allow the page to be refreshed once the activity is started.
    @Override
    protected void onResume() {
        super.onResume();

        setFields();
    }

    // controls where the user goes if they press the back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalView.class);
        ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
        list.add(healthProfessional);
        intent.putExtra("USER",list);
        startActivity(intent);
        finish();
    }
    // sets all the fields on the page
    private void setFields(){
        name_text.setText(healthProfessional.getName());
        email_text.setText(healthProfessional.getEmail());

        //pull the latest patient dbRegions, ensures that if access is revoked during an active session, then they wont be able to interact with it.
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

    // show the dbRegions on the dbRegions view
    private void setPatientListView(){
        HealthProfessionalPatientListViewAdapter adapter = new HealthProfessionalPatientListViewAdapter(this, healthProfessional.getPatient());
        patients_listview.setAdapter(adapter);
    }
}
