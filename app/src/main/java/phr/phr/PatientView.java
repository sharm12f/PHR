package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import phr.lib.Patient;

/**
 * Created by Anupam on 27-Jan-18.
 */

public class PatientView extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.patient_view);
        ArrayList<Patient> list = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        final Patient patient = list.get(0);
        final Button account = findViewById(R.id.account_button);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MyAccount.class);
                ArrayList<Patient> list = new ArrayList<Patient>();
                list.add(patient);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });
    }
}
