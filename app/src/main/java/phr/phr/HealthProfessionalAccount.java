package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Patient;

/**
 * Created by Anupam on 29-Jan-18.
 */

public class HealthProfessionalAccount extends AppCompatActivity {
    TextView name_text, email_text, phone_text, region_text;
    Button edit_button;
    HealthProfessional healthProfessional;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.healthprofessional_account);
        name_text = findViewById(R.id.name_text);
        email_text = findViewById(R.id.email_text);
        phone_text = findViewById(R.id.phone_text);
        region_text = findViewById(R.id.region_text);
        edit_button = findViewById(R.id.edit_button);

        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        healthProfessional= list.get(0);

        setFields();

        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setFields(){
        name_text.setText(healthProfessional.getfName() + " " + healthProfessional.getlName());
        email_text.setText(healthProfessional.getEmail());
        phone_text.setText(healthProfessional.getPhone());
        region_text.setText(healthProfessional.getRegion());
    }
}
