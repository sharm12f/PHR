package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
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
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.RecordPermission;
import phr.lib.User;

/**
 * Created by Anupam on 10-Apr-18.
 *
 * This page is very similar to how a user may add permission to a single record.
 * This page allows the user to search for the health professional they want to grand permission to. Duplicate permissions are ignored
 *
 * This page indicates that all records are going to be effected at the top.
 *
 * The spinners to filter the health professional are provided. Any is select by default
 *
 * The dbRegions view is populated once the user clicks the search button
 *
 * After searching for the health professional
 *      they user can select one from the dbRegions, and add permission to grand them access
 *      or
 *      they can select give to all and grant everyone on the dbRegions access
 *
 */

public class PatientAddPermissionAllRecords extends AppCompatActivity {
    TextView record_name_text_view;
    Spinner region_spinner, province_spinner, organization_spinner, department_spinner, health_professional_spinner;
    Button search_button, add_perms_button, add_perms_all_button;
    ListView health_professional_list_view;

    //used for spinners
    ArrayList<String> r;
    ArrayList<String> pro;
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
        province_spinner = findViewById(R.id.province_spinner);
        organization_spinner = findViewById(R.id.organization_spinner);
        department_spinner = findViewById(R.id.department_spinner);
        health_professional_spinner = findViewById(R.id.healthprofessional_spinner);
        search_button = findViewById(R.id.search_button);
        add_perms_button = findViewById(R.id.add_perms_button);
        add_perms_all_button = findViewById(R.id.add_perms_all_button);
        health_professional_list_view = findViewById(R.id.health_professional_list_view);

        //set the health professional dbRegions view to only one choice selection
        health_professional_list_view.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        health_professional_list_view.setSelector(R.color.darkGray);

        record_name_text_view.setText("All Records");

        // set all the spinner with database data
        setSpinnes();

        // get the user object
        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("USER");
        patient = (Patient)list.get(0);

        // allow the user to add permission for a selected health professional
        add_perms_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean set = false;
                //ensure a health professional is selected from the dbRegions
                if(health_professional_list_view.getCheckedItemPosition() != -1)
                    set=true;
                if(set) {
                    try {
                        new AsyncTask<Void, Void, Boolean>() {
                            private ProgressDialog p = new ProgressDialog(PatientAddPermissionAllRecords.this);

                            protected void onPreExecute() {
                                super.onPreExecute();
                                p.setMessage("Loading");
                                p.setCancelable(false);
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
                                    Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(patient);
                                    intent.putExtra("USER",list);
                                    startActivity(intent);
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
                            p.setCancelable(false);
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
                                Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
                                ArrayList<User> list = new ArrayList<User>();
                                list.add(patient);
                                intent.putExtra("USER",list);
                                startActivity(intent);
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
                            p.setCancelable(false);
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }

                        protected Void doInBackground(Void... progress) {
                            String region = region_spinner.getSelectedItem().toString();
                            String province = province_spinner.getSelectedItem().toString();
                            String organization = organization_spinner.getSelectedItem().toString();
                            String department = department_spinner.getSelectedItem().toString();
                            String healthProfessional = health_professional_spinner.getSelectedItem().toString();
                            healthProfessionalsList = Lib.searchHealthProfessionals(region, province, organization, department, healthProfessional);
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

    //set the search dbRegions view with the health professionals
    private void setSearchListView() {
        HealthProfessionalListViewAdapter adapter = new HealthProfessionalListViewAdapter(this, healthProfessionalsList);
        health_professional_list_view.setAdapter(adapter);
    }

    // set all the spinners
    private void loadSpinners() {

        ArrayAdapter<String> adr = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, r);
        adr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        region_spinner.setAdapter(adr);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, pro);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        province_spinner.setAdapter(adp);


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

    // get a dbRegions for all the spinners from the database
    private void setSpinnes() {
        try {
            new AsyncTask<Void, Void, Void>() {
                private ProgressDialog p = new ProgressDialog(PatientAddPermissionAllRecords.this);

                protected void onPreExecute() {
                    super.onPreExecute();
                    p.setMessage("Loading");
                    p.setCancelable(false);
                    p.setIndeterminate(false);
                    p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    p.show();
                }

                protected Void doInBackground(Void... progress) {
                    r = Lib.getRegions();
                    pro = Lib.getProvinces();
                    o = Lib.getOrganization();
                    d = Lib.getDepartment();
                    h = Lib.getHealthProfessional();
                    //insert and empty entry into all array lists
                    r.add(0, "Any");
                    pro.add(0,"Any");
                    o.add(0, "Any");
                    d.add(0, "Any");
                    h.add(0, "Any");

                    return null;
                }

                protected void onPostExecute(Void Void) {
                    super.onPostExecute(Void);
                    p.dismiss();
                    loadSpinners();
                }
            }.execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
        ArrayList<User> list = new ArrayList<User>();
        list.add(patient);
        intent.putExtra("USER",list);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
