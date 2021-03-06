package phr.phr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import phr.lib.HealthProfessional;
import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by anupam on 29/03/18.
 *
 * This page allows the user to leave a note for the patient regarding a record
 *
 * Edit text at the top of the page is the name of the note
 *
 * The patient it is being addressed to is bellow the name
 *
 * I hope to put a fixed un-editable field here that shows what note they are referring to
 *
 * Edit text for the description if at the bottom, allowing the user to write their thoughts.
 *
 * At the very bottom is the button to leave the note
 *
 */

public class HealthProfessionalLeaveNote extends AppCompatActivity {
    Patient patient;
    HealthProfessional healthProfessional;
    Record record;
    TextView note_name_text, to_from_text, note_to_from_name_text;
    EditText note_description;
    Button leave_note_button;

    String GOTO;

    int position;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.health_professional_notes_view);
        leave_note_button = findViewById(R.id.leave_note_button);
        note_description = findViewById(R.id.note_description);
        to_from_text = findViewById(R.id.to_from_text);
        note_name_text = findViewById(R.id.note_name_text);
        note_to_from_name_text = findViewById(R.id.note_to_from_name_text);

        // is used to desicde where the user came from and where to send them back
        GOTO = (String)getIntent().getExtras().get("GOTO");
        position = (int)getIntent().getExtras().get("POS");

        //get the user and record objects
        ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
        ArrayList<User> list2 = (ArrayList<User>)getIntent().getExtras().get("USER");
        record = list.get(0);
        patient = (Patient)list2.get(0);
        healthProfessional = (HealthProfessional)list2.get(1);

        // pre-fill all the fields
        setFields();

        // allows the user to leave a note
        leave_note_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog p = new ProgressDialog(HealthProfessionalLeaveNote.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setCancelable(false);
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }
                        protected Boolean doInBackground(Void... progress) {
                            Boolean result = false;
                            String name = note_name_text.getText().toString();
                            if(name.length() >=1 ) {
                                String description = note_description.getText().toString();
                                result = Lib.HealthProfessionalLeaveNote(name, description, patient.getId(), healthProfessional.getId());
                            }
                            return result;
                        }
                        protected void onPostExecute(Boolean result){
                            super.onPostExecute(result);
                            p.dismiss();
                            if(result){
                                Toast toast = Toast.makeText(getApplicationContext(), "Note Sent", Toast.LENGTH_SHORT);
                                toast.show();
                                Intent intent = new Intent(getApplicationContext(), HealthProfessionalRecordView.class);
                                ArrayList<Record> list = new ArrayList<Record>();
                                list.add(record);
                                ArrayList<User> list2 = new ArrayList<User>();
                                list2.add(patient);
                                list2.add(healthProfessional);
                                intent.putExtra("RECORD",list);
                                intent.putExtra("USER",list2);
                                intent.putExtra("GOTO", GOTO);
                                intent.putExtra("POS",position);
                                startActivity(intent);
                                finish();
                            }
                            else{
                                Toast toast = Toast.makeText(getApplicationContext(), "Could not Leave note", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    };
                    asyncTask.execute();
                }catch (Exception e){e.printStackTrace();}
            }
        });

    }

    //control the flow of the app regardless of the stack
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), HealthProfessionalRecordView.class);
        ArrayList<Record> list = new ArrayList<Record>();
        list.add(record);
        ArrayList<User> list2 = new ArrayList<User>();
        list2.add(patient);
        list2.add(healthProfessional);
        intent.putExtra("RECORD",list);
        intent.putExtra("USER",list2);
        intent.putExtra("GOTO", GOTO);
        intent.putExtra("POS",position);
        startActivity(intent);
        finish();
    }


    //pre-fill the fields with known data
    private void setFields(){
        //set the hit for note name
        note_name_text.setText("");
        note_name_text.setHint("Note Name");

        //change to_from_text to the patient
        to_from_text.setText("To: ");

        //set the name of the patient the note is for
        note_to_from_name_text.setText(patient.getName());

        //insert into the the note the record name it refers to
        note_description.setText("In reference to Record: " + record.getName());

    }
}
