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

import phr.lib.Patient;

/**
 * Created by Anupam on 27-Jan-18.
 */

public class PatientView extends AppCompatActivity {

    Button account, notes;
    ListView notes_list_view;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.patient_view);
        ArrayList<Patient> list = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        final Patient patient = list.get(0);
        account = findViewById(R.id.account_button);
        notes_list_view = findViewById(R.id.notes_list_views);

        NoteListViewAdapter adapter = new NoteListViewAdapter(this, patient.getNotes());
        notes_list_view.setAdapter(adapter);

        //gonna hide this for now, you can re-enable it and list all history notes
        notes = findViewById(R.id.button2);
        notes.setVisibility(View.GONE);
        notes.setClickable(false);

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
                ArrayList<Patient> list = new ArrayList<Patient>();
                list.add(patient);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });

        notes_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PatientNoteView.class);
                ArrayList<Patient> list = new ArrayList<Patient>();
                list.add(patient);
                intent.putExtra("USER",list);
                intent.putExtra("POS",position);
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
