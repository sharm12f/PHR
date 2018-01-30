package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Patient;

/**
 * Created by Anupam on 29-Jan-18.
 */

public class HealthProfessionalView extends AppCompatActivity {
    Button account_button;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.healthprofessional_view);
        account_button = findViewById(R.id.myaccount_button);

        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        final HealthProfessional physician = list.get(0);

        account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(physician);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });

    }
}
