package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import phr.lib.HealthProfessional;

/**
 * Created by Anupam on 29-Jan-18.
 */

public class HealthProfessionalView extends AppCompatActivity {
    Button my_account_button, view_all_record_button;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.healthprofessional_view);
        my_account_button = findViewById(R.id.myaccount_button);
        view_all_record_button = findViewById(R.id.all_records_button);

        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        final HealthProfessional physician = list.get(0);

        my_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(physician);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });

        view_all_record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalViewAllRecords.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(physician);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });

    }
}
