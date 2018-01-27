package phr.phr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

import phr.lib.Lib;
import phr.lib.User;

/**
 * Created by Anupam on 26-Jan-18.
 */

public class PatientRegistration extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.patient_registration);
        final EditText Efname = findViewById(R.id.fname_input);
        final EditText Elname = findViewById(R.id.lname_input);
        final EditText Eemail = findViewById(R.id.email_input);
        final EditText Ephone = findViewById(R.id.phone_input);
        final EditText Epassword = findViewById(R.id.password_input);
        final EditText Ere_password = findViewById(R.id.re_pass_input);
        final Button create = findViewById(R.id.create_button);
        final Spinner Sregion = findViewById(R.id.region_spinner);
        final Spinner Sprovinces = findViewById(R.id.province_spinner);


        new AsyncTask<Void, Void, Void>(){
            protected Void doInBackground(Void... progress){
                loadSpinners(Sregion, Sprovinces);
                return null;
            }
        }.execute();

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fname = Efname.getText().toString();
                final String lname = Elname.getText().toString();
                final String email = Eemail.getText().toString();
                final String phone = Ephone.getText().toString();
                final String password = Epassword.getText().toString();
                final String re_password = Ere_password.getText().toString();
                final String region = Sregion.getSelectedItem().toString();
                final String province = Sprovinces.getSelectedItem().toString();
                try{
                    Boolean success = new AsyncTask<Void, Void, Boolean>() {
                        protected Boolean doInBackground(Void... progress) {
                            System.out.println("Start Registration");
                            return Lib.register(fname, lname , email, password, re_password, phone, region, province);
                        }
                    }.execute().get();
                    if(success == true) {
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Could not create user", Toast.LENGTH_LONG).show();
                        Efname.setText("");
                        Elname.setText("");
                        Eemail.setText("");
                        Ephone.setText("");
                        Epassword.setText("");
                        Ere_password.setText("");
                    }
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }

    private void loadSpinners(Spinner regions, Spinner provinces){
        ArrayList<String> list = Lib.getRegions();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        regions.setAdapter(dataAdapter);

        ArrayList<String> list2 = Lib.getProvinces();
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        provinces.setAdapter(dataAdapter2);

    }
}
