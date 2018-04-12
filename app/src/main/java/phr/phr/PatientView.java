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

import phr.lib.Lib;
import phr.lib.Patient;
import phr.lib.User;

/**
 * Created by Anupam on 27-Jan-18.
 *
 * is the patient view
 *
 * it shows the user updates and allows them to go to their account
 *
 */

public class PatientView extends AppCompatActivity {

    Button account, notes;
    ListView notes_list_view;

    Patient patient;

    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.patient_view);
        account = findViewById(R.id.account_button);
        notes_list_view = findViewById(R.id.notes_list_views);

        // get the user object
        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("USER");
        patient = (Patient)list.get(0);

        // check if the app should timeout
        /*
        boolean timeout = Lib.timeOut(patient.getSession());
        if(timeout){
            Intent intent = new Intent (getApplicationContext(), LogIn.class);
            intent.putExtra("TIMEOUT", true);
            startActivity(intent);
            finish();
        }
        else{
            patient.setSession(Lib.getTimestampNow());
        }
        */

        // set any notes the user may have gotten
        NoteListViewAdapter adapter = new NoteListViewAdapter(this, patient.getNotes());
        notes_list_view.setAdapter(adapter);

        //gonna hide this for now, you can re-enable it and list all history notes
        notes = findViewById(R.id.button2);
        notes.setVisibility(View.GONE);
        notes.setClickable(false);


        // send the user to their account page
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
                ArrayList<User> list = new ArrayList<User>();
                list.add(patient);
                intent.putExtra("USER",list);
                startActivity(intent);
                finish();
            }
        });

        // list of notes address to the user.
        notes_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), PatientNoteView.class);
                ArrayList<Patient> list = new ArrayList<Patient>();
                list.add(patient);
                intent.putExtra("USER",list);
                intent.putExtra("POS",position);
                startActivity(intent);
                finish();
            }
        });

    }

    // send the user back to the LogIn page.
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), LogIn.class);
        startActivity(intent);
        finish();
    }
}
