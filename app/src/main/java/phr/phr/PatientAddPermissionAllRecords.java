package phr.phr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Lib;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.RecordPermission;

/**
 * Created by Anupam on 10-Apr-18.
 */

public class PatientAddPermissionAllRecords extends AppCompatActivity {
    TextView record_name_text_view;
    Spinner region_spinner, organization_spinner, department_spinner, health_professional_spinner;
    Button search_button, add_perms_button, add_perms_all_button;
    ListView health_professional_list_view;

    //used for spinners
    ArrayList<String> r;
    ArrayList<String> o;
    ArrayList<String> d;
    ArrayList<String> h;


    //Other variables
    Patient patient;
    RecordPermission recordPermission;
    ArrayList<HealthProfessional> healthProfessionalsList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_add_permission);
        record_name_text_view = findViewById(R.id.textView2);
        region_spinner = findViewById(R.id.region_spinner);
        organization_spinner = findViewById(R.id.organization_spinner);
        department_spinner = findViewById(R.id.department_spinner);
        health_professional_spinner = findViewById(R.id.healthprofessional_spinner);
        search_button = findViewById(R.id.search_button);
        add_perms_button = findViewById(R.id.add_perms_button);
        add_perms_all_button = findViewById(R.id.add_perms_all_button);
        health_professional_list_view = findViewById(R.id.health_professional_list_view);

        //set the health professional list view to only one choice selection
        health_professional_list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        health_professional_list_view.setSelector(R.color.darkGray);

        record_name_text_view.setText("All Records");

        setSpinnes();

        ArrayList<Patient> list = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        patient = list.get(0);


        add_perms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean set = false;
                if(health_professional_list_view.getCheckedItemPosition() != -1)
                    set=true;
                if(set) {
                    try {
                        new AsyncTask<Void, Void, Boolean>() {
                            private ProgressDialog p = new ProgressDialog(PatientAddPermissionAllRecords.this);

                            protected void onPreExecute() {
                                super.onPreExecute();
                                p.setMessage("Loading");
                                p.setIndeterminate(false);
                                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                p.show();
                            }

                            protected Boolean doInBackground(Void... progress) {
                                boolean allSet = true;
                                ArrayList<Record> records = patient.getRecords();
                                for(int i=0; i<records.size(); i++) {
                                    boolean result = false;
                                    Record record = records.get(i);
                                    HealthProfessional hp = healthProfessionalsList.get(health_professional_list_view.getCheckedItemPosition());
                                    recordPermission = new RecordPermission(hp.getId(), hp.getName(), record.getId(), record.getName());
                                    boolean permsExist = Lib.permsExist(recordPermission);

                                    if (!permsExist)
                                        result = Lib.givePermission(recordPermission);

                                    if(!result && !permsExist)
                                        allSet=false;

                                }
                                return allSet;
                            }

                            protected void onPostExecute(Boolean result) {
                                super.onPostExecute(result);
                                p.dismiss();
                                if(result){
                                    Toast toast = Toast.makeText(getApplicationContext(), "Permissions Granted", Toast.LENGTH_SHORT);
                                    toast.show();
                                    finish();
                                }
                                else{
                                    Toast toast = Toast.makeText(getApplicationContext(), "Error, NO Permissions Granted or Partial Permissions Granted", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        }.execute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    Toast toast = Toast.makeText(getApplicationContext(), "Please select a Health Professional", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });

        add_perms_all_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog p = new ProgressDialog(PatientAddPermissionAllRecords.this);

                        protected void onPreExecute() {
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }

                        protected Boolean doInBackground(Void... progress) {
                            boolean allSet = true;
                            ArrayList<Record> records = patient.getRecords();
                            for(int j=0; j<records.size(); j++) {
                                for (int i = 0; i < healthProfessionalsList.size(); i++) {
                                    boolean result = false;
                                    HealthProfessional hp = healthProfessionalsList.get(i);
                                    Record record = records.get(j);
                                    recordPermission = new RecordPermission(hp.getId(), hp.getName(), record.getId(), record.getName());
                                    boolean permsExist = Lib.permsExist(recordPermission);

                                    if (!permsExist)
                                        result = Lib.givePermission(recordPermission);

                                    if (!result && !permsExist)
                                        allSet = false;

                                }
                            }
                            return allSet;
                        }

                        protected void onPostExecute(Boolean result) {
                            super.onPostExecute(result);
                            p.dismiss();
                            if(result){
                                Toast toast = Toast.makeText(getApplicationContext(), "Permissions Granted", Toast.LENGTH_SHORT);
                                toast.show();
                                finish();
                            }
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Error, NO Permissions Granted or Partial Permissions Granted", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        }
                    }.execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    new AsyncTask<Void, Void, Void>() {
                        private ProgressDialog p = new ProgressDialog(PatientAddPermissionAllRecords.this);

                        protected void onPreExecute() {
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }

                        protected Void doInBackground(Void... progress) {
                            String region = region_spinner.getSelectedItem().toString();
                            String organization = organization_spinner.getSelectedItem().toString();
                            String department = department_spinner.getSelectedItem().toString();
                            String healthProfessional = health_professional_spinner.getSelectedItem().toString();
                            healthProfessionalsList = Lib.searchHealthProfessionals(region, organization, department, healthProfessional);
                            return null;
                        }

                        protected void onPostExecute(Void Void) {
                            super.onPostExecute(Void);
                            p.dismiss();
                            if (healthProfessionalsList != null)
                                setSearchListView();
                        }
                    }.execute();
                }catch (Exception e){e.printStackTrace();}
            }
        });


    }

    private void setSearchListView() {
        HealthProfessionalListViewAdapter adapter = new HealthProfessionalListViewAdapter(this, healthProfessionalsList);
        health_professional_list_view.setAdapter(adapter);
    }

    private void loadSpinners(ArrayList<String> r, ArrayList<String> o, ArrayList<String> d, ArrayList<String> h) {

        ArrayAdapter<String> adr = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, r);
        adr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region_spinner.setAdapter(adr);


        ArrayAdapter<String> ado = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, o);
        ado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        organization_spinner.setAdapter(ado);


        ArrayAdapter<String> add = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, d);
        add.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department_spinner.setAdapter(add);


        ArrayAdapter<String> adh = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, h);
        adh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        health_professional_spinner.setAdapter(adh);
    }

    // get a list for all the spinners from the database
    private void setSpinnes() {
        try {
            new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(PatientAddPermissionAllRecords.this);

                protected void onPreExecute() {
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setIndeterminate(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.show();
                }

                protected Void doInBackground(Void... progress) {
                    r = Lib.getRegions();
                    o = Lib.getOrganization();
                    d = Lib.getDepartment();
                    h = Lib.getHealthProfessional();
                    //insert and empty entry into all array lists
                    r.add(0, " ");
                    o.add(0, " ");
                    d.add(0, " ");
                    h.add(0, " ");

                    return null;
                }

                protected void onPostExecute(Void Void) {
                    super.onPostExecute(Void);
                    p.dismiss();
                    loadSpinners(r, o, d, h);
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
