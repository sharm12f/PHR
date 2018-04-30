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

import phr.lib.Note;
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

        // set any notes the user may have gotten
        setUpdateNotes();

        //gonna hide this for now, you can re-enable it and dbRegions all history notes
        notes = findViewById(R.id.view_all_records_button);
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

        // dbRegions of notes address to the user.
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpdateNotes(){

        ArrayList<Note> notes = new ArrayList<>();
        ArrayList<Note> temp = patient.getNotes();

        //check to make sure the notes are only the latest ones.
        for(int i=0;i<temp.size();i++){
            Note n = temp.get(i);
            long diff = patient.getLogout().getTime()- n.getCreateTime().getTime();
            if(diff<=0){
                notes.add(n);
            }
        }

        NoteListViewAdapter adapter = new NoteListViewAdapter(this, notes);
        notes_list_view.setAdapter(adapter);
    }
}
