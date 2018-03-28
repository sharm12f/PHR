package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import javax.security.auth.login.LoginException;

import phr.lib.HealthProfessional;

/**
 * Created by Anupam on 29-Jan-18.
 *
 * This is the health professional first view, they can see all records from here, or go to their account if they want to see
 *
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

        //not sure if this button is need at all so its hidden for now.
        view_all_record_button.setVisibility(View.GONE);
        view_all_record_button.setClickable(false);

        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("HP");
        final HealthProfessional physician = list.get(0);

        my_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(physician);
                intent.putExtra("HP",list);
                startActivity(intent);
            }
        });

        view_all_record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalViewAllRecords.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(physician);
                intent.putExtra("HP",list);
                startActivity(intent);
            }
        });

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        startActivity(intent);
    }
}
