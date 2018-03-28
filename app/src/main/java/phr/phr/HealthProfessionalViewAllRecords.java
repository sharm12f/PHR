package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 26-Feb-18.
 */

public class HealthProfessionalViewAllRecords extends AppCompatActivity {
    HealthProfessional healthProfessional;
    ListView record_list_view;
    ArrayList<String[]> rcs;
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.healthprofessional_view_all_records);
        record_list_view = findViewById(R.id.records_list_view);
        ArrayList<HealthProfessional> list = (ArrayList<HealthProfessional>)getIntent().getExtras().get("HP");
        healthProfessional = list.get(0);
        rcs = new ArrayList<String[]>();
        ArrayList<Patient> temp = healthProfessional.getPatient();
        for (int i=0; i<temp.size();i++){
            Patient p = temp.get(i);
            ArrayList<Record> r = p.getRecords();
            for(int j = 0; j<r.size();j++){
                String[] l = new String[4];
                l[0] = p.getName();
                l[1] = r.get(j).getName();
                l[2] = ""+i;
                l[3] = ""+j;
                rcs.add(l);
            }
        }
        HealthProfessionalRecordListViewAdapter adapter = new HealthProfessionalRecordListViewAdapter(this, rcs);
        record_list_view.setAdapter(adapter);


        record_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                int i = Integer.parseInt(rcs.get(+position)[2]);
                int j = Integer.parseInt(rcs.get(+position)[3]);
                Record Slecteditem= healthProfessional.getPatient().get(i).getRecords().get(j);
                Intent intent = new Intent(getApplicationContext(), HealthProfessionalRecordView.class);
                ArrayList<Record> list = new ArrayList<Record>();
                ArrayList<Patient> list2 = new ArrayList<Patient>();
                list2.add(healthProfessional.getPatient().get(i));
                list.add(Slecteditem);
                intent.putExtra("RECORD",list);
                intent.putExtra("USER",list2);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalAccount.class);
        ArrayList<HealthProfessional> list = new ArrayList<HealthProfessional>();
        list.add(healthProfessional);
        intent.putExtra("HP",list);
        startActivity(intent);
    }

}
