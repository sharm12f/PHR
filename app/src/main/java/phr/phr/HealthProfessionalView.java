package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 29-Jan-18.
 *
 *  This is the main view, the user can go to their account, view any new updates for them.
 *
 */

public class HealthProfessionalView extends AppCompatActivity {
    Button my_account_button, view_all_record_button;
    ListView record_list_view;
    HealthProfessional healthProfessional;

    ArrayList<String[]> rcs = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.healthprofessional_view);
        my_account_button = findViewById(R.id.myaccount_button);
        view_all_record_button = findViewById(R.id.all_records_button);
        record_list_view = findViewById(R.id.records_list_view);

        //not sure if this button is need at all so its hidden for now.
        view_all_record_button.setVisibility(View.GONE);
        view_all_record_button.setClickable(false);

        //get he user object
        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("USER");
        healthProfessional = list.get(0);

        setUpdateListView();

        //send the user to their account page
        my_account_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(healthProfessional);
                intent.putExtra("USER",list);
                startActivity(intent);
                finish();
            }
        });

        //this button is not used on this screen since there is a redundent button on the account screen
        view_all_record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalViewAllRecords.class);
                ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
                list.add(healthProfessional);
                intent.putExtra("USER",list);
                startActivity(intent);
                finish();
            }
        });


        //opens the selected record.
        record_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                int i = Integer.parseInt(rcs.get(+position)[2]);
                int j = Integer.parseInt(rcs.get(+position)[3]);
                ArrayList<Record> list = new ArrayList<Record>();
                ArrayList<User> list2 = new ArrayList<User>();
                Record Slecteditem= healthProfessional.getPatient().get(i).getRecords().get(j);
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalRecordView.class);
                list2.add(healthProfessional.getPatient().get(i));
                list2.add(healthProfessional);
                list.add(Slecteditem);
                intent.putExtra("POS",j);
                intent.putExtra("RECORD",list);
                intent.putExtra("USER",list2);
                //used to let the record view know to come back to this page on back press.
                intent.putExtra("GOTO","HealthProfessionalView");
                startActivity(intent);
                finish();
            }
        });

    }

    //control the flow of the app regardless of the stack.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setUpdateListView(){

        ArrayList<Patient> temp = healthProfessional.getPatient();
        for (int i=0; i<temp.size();i++){
            Patient p = temp.get(i);
            ArrayList<Record> r = p.getRecords();
            for(int j = 0; j<r.size();j++){
                long diff = healthProfessional.getLogout().getTime() - r.get(j).getCreate().getTime();
                if(diff <=0 ){
                    String[] l = new String[4];
                    l[0] = p.getName();
                    l[1] = r.get(j).getName();
                    l[2] = ""+i;
                    l[3] = ""+j;
                    rcs.add(l);
                }
            }
        }
        HealthProfessionalRecordListViewAdapter adapter = new HealthProfessionalRecordListViewAdapter(this, rcs);
        record_list_view.setAdapter(adapter);
    }

}
