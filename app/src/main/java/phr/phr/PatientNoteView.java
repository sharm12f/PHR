package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.Patient;
import phr.lib.User;

/**
 * Created by Anupam on 28-Mar-18.
 *
 * This page allows the user to view a note left for them.
 *
 * Top of the page is the name of the note
 *
 * Below that is who its from
 *
 * The body of the note may dbRegions which record the note refers to.
 *
 */

public class PatientNoteView extends AppCompatActivity {
    Patient patient;
    TextView to_from_text, note_to_from_name_text;
    EditText note_description, note_name_text;
    Button leave_note_button;
    int position;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.health_professional_notes_view);
        leave_note_button = findViewById(R.id.leave_note_button);
        note_description = findViewById(R.id.note_description);
        to_from_text = findViewById(R.id.to_from_text);
        note_name_text = findViewById(R.id.note_name_text);
        note_to_from_name_text = findViewById(R.id.note_to_from_name_text);

        // get he user object
        ArrayList<Patient> list = (ArrayList<Patient>)getIntent().getExtras().get("USER");
        patient = list.get(0);
        position = (int)getIntent().getExtras().get("POS");

        //load all the fields
        setFields();

    }

    // control the flow the app regardless of the stack
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), PatientView.class);
        ArrayList<User> list = new ArrayList<User>();
        list.add(patient);
        intent.putExtra("USER", list);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void setFields(){
        //This button is not used for this view
        leave_note_button.setClickable(false);
        leave_note_button.setVisibility(View.GONE);

        //Disable the edit text because the patient should not be able to do this
        note_description.setFocusable(false);
        note_description.setText(patient.getNotes().get(+position).getDescription());

        //set the to_from_text to "from" so the user knows which health professional left the note
        to_from_text.setText("From: ");

        //set the name of the health professional who wrote the note
        note_to_from_name_text.setText(patient.getNotes().get(+position).getHealth_professional_name());

        //set the name of note
        note_name_text.setFocusable(false);
        note_name_text.setText(patient.getNotes().get(+position).getName());

    }
}
