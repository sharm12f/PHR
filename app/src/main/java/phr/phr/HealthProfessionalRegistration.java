package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Anupam on 28-Jan-18.
 *
 * This the registration activity for the health professional
 *
 */

public class HealthProfessionalRegistration extends AppCompatActivity {
    Boolean Success = false;

    //These ar the drop down options for the various sub-section that the HP belongs to. This will help narrow the search for the HP when the patient is looking to give them permissions
    Spinner Sregion;
    Spinner Sprovince;
    Spinner Sorganization;
    Spinner Sdepartment;
    Spinner Shealthprofessional;

    // These lists are created after the dbRegions of avalible sub-section is retrieved from the database, the spinner adapters get these lists
    ArrayList<String> r;
    ArrayList<String> pro;
    ArrayList<String> o;
    ArrayList<String> d;
    ArrayList<String> h;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.healthprofessional_registration);
        // all the fiels in the layout are defined here
        final EditText Ename = findViewById(R.id.name_input);
        final EditText Eemail = findViewById(R.id.email_input);
        final EditText Ephone = findViewById(R.id.phone_input);
        final EditText Epassword = findViewById(R.id.password_input);
        final EditText Ere_password = findViewById(R.id.re_pass_input);
        final Button create = findViewById(R.id.create_button);
        Sregion = findViewById(R.id.region_spinner);
        Sprovince = findViewById(R.id.province_spinner);
        Sorganization = findViewById(R.id.organization_spinner);
        Sdepartment = findViewById(R.id.department_spinner);
        Shealthprofessional = findViewById(R.id.healthprofessional_spinner);


        // this loads only the spinners with the avalible lists from the database
        setFields();

        // the button to create the user
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get all the users input
                final String name = Ename.getText().toString();
                final String email = Eemail.getText().toString();
                final String phone = Ephone.getText().toString();
                final String password = Epassword.getText().toString();
                final String re_password = Ere_password.getText().toString();
                final String region = Sregion.getSelectedItem().toString();
                final String province = Sprovince.getSelectedItem().toString();
                final String organization = Sorganization.getSelectedItem().toString();
                final String department = Sdepartment.getSelectedItem().toString();
                final String healthprofessional = Shealthprofessional.getSelectedItem().toString();

                // the following async task will try to register the users account
                try{
                    AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog p = new ProgressDialog(HealthProfessionalRegistration.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setCancelable(false);
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }
                        protected Boolean doInBackground(Void... progress) {
                            return Lib.healthProfessionalRegister(name , email, password, re_password, phone, region, province, organization, department, healthprofessional);
                        }
                        protected void onPostExecute(Boolean result){
                            super.onPostExecute(result);
                            p.dismiss();
                            // show the user the account is created, and re-direct them to the login page
                            if(result){
                                Toast toast = Toast.makeText(getApplicationContext(), "Account Created", Toast.LENGTH_LONG);
                                toast.show();
                                Intent intent = new Intent(getApplicationContext(), LogIn.class);
                                startActivity(intent);
                                finish();
                            }
                            //tell the user there was an error, this error is very generic, and can be updated in the future if needed
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Creation Error", Toast.LENGTH_SHORT);
                                toast.show();
                            }

                        }
                    };
                    asyncTask.execute();
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    //control where the user goes when they press the back button
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        startActivity(intent);
    }

    // set the spinners with the values retrieved from the database
    private void loadSpinners(ArrayList<String> r, ArrayList<String> p, ArrayList<String> o, ArrayList<String> d, ArrayList<String>h){

        ArrayAdapter<String> adr = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, r);
        adr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sregion.setAdapter(adr);

        ArrayAdapter<String> adp = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, p);
        adp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sprovince.setAdapter(adp);


        ArrayAdapter<String> ado = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, o);
        ado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sorganization.setAdapter(ado);


        ArrayAdapter<String> add = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, d);
        add.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Sdepartment.setAdapter(add);


        ArrayAdapter<String> adh = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, h);
        adh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Shealthprofessional.setAdapter(adh);
    }

    // get a dbRegions for all the spinners from the database
    private void setFields(){
        new AsyncTask<Void, Void, Void>(){
            private ProgressDialog p = new ProgressDialog(HealthProfessionalRegistration.this);
            protected void onPreExecute(){
                super.onPreExecute();
                p.setMessage("Loading");
                p.setCancelable(false);
                p.setIndeterminate(false);
                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                p.show();
            }
            protected Void doInBackground(Void... progress){
                r = Lib.getRegions();
                pro = Lib.getProvinces();
                o = Lib.getOrganization();
                d = Lib.getDepartment();
                h = Lib.getHealthProfessional();
                return null;
            }
            protected void onPostExecute(Void Void) {
                super.onPostExecute(Void);
                p.dismiss();
                loadSpinners(r,pro,o,d,h);
            }
        }.execute();

    }
}
