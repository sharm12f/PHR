package phr.phr;

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

import phr.lib.Lib;

/**
 * Created by Anupam on 28-Jan-18.
 */

public class HealthProfessionalRegistration extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.healthprofessional_registration);
        final EditText Ename = findViewById(R.id.name_input);
        final EditText Eemail = findViewById(R.id.email_input);
        final EditText Ephone = findViewById(R.id.phone_input);
        final EditText Epassword = findViewById(R.id.password_input);
        final EditText Ere_password = findViewById(R.id.re_pass_input);
        final Button create = findViewById(R.id.create_button);
        final Spinner Sregion = findViewById(R.id.region_spinner);
        final Spinner Sorganization = findViewById(R.id.organization_spinner);
        final Spinner Sdepartment = findViewById(R.id.department_spinner);
        final Spinner Shealthprofessional = findViewById(R.id.healthprofessional_spinner);



        new AsyncTask<Void, Void, Void>(){
            protected Void doInBackground(Void... progress){
                loadSpinners(Sregion,Sorganization,Sdepartment,Shealthprofessional);
                return null;
            }
        }.execute();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = Ename.getText().toString();
                final String email = Eemail.getText().toString();
                final String phone = Ephone.getText().toString();
                final String password = Epassword.getText().toString();
                final String re_password = Ere_password.getText().toString();
                final String region = Sregion.getSelectedItem().toString();
                final String organization = Sorganization.getSelectedItem().toString();
                final String department = Sdepartment.getSelectedItem().toString();
                final String healthprofessional = Shealthprofessional.getSelectedItem().toString();
                try{
                    Boolean success = new AsyncTask<Void, Void, Boolean>() {
                        protected Boolean doInBackground(Void... progress) {
                            System.out.println("Start Registration");
                            return Lib.healthProfessionalRegister(name , email, password, re_password, phone, region, organization, department, healthprofessional);
                        }
                    }.execute().get();
                    if(success == true) {
                        Intent intent = new Intent(getApplicationContext(), LogIn.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Could not create patient", Toast.LENGTH_LONG).show();
                        Ename.setText("");
                        Eemail.setText("");
                        Ephone.setText("");
                        Epassword.setText("");
                        Ere_password.setText("");
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    private void loadSpinners(Spinner regions, Spinner organization, Spinner department, Spinner healthprofessional){
        ArrayList<String> r = Lib.getRegions();
        ArrayAdapter<String> adr = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, r);
        adr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regions.setAdapter(adr);

        ArrayList<String> o = Lib.getOrganization();
        ArrayAdapter<String> ado = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, o);
        ado.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        organization.setAdapter(ado);

        ArrayList<String> d = Lib.getDepartment();
        ArrayAdapter<String> add = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, d);
        add.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        department.setAdapter(add);

        ArrayList<String> h = Lib.getHealthProfessional();
        ArrayAdapter<String> adh = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, h);
        adh.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        healthprofessional.setAdapter(adh);

    }
}
