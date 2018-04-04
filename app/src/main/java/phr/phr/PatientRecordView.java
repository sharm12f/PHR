package phr.phr;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.sql.SQLOutput;
import java.util.ArrayList;

import phr.lib.Lib;
import phr.lib.Record;

/**
 * Created by Anupam on 30-Jan-18.
 */

public class PatientRecordView extends AppCompatActivity {
    EditText name_input, description_input;
    Button add_update_button, browse_button, delete_record, edit_permissions;
    Record record;
    boolean update=false;
    int id;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.patient_add_record);
        name_input = findViewById(R.id.name_input);
        description_input = findViewById(R.id.description_input);
        add_update_button = findViewById(R.id.add_update_button);
        delete_record = findViewById(R.id.delete_record_button);
        edit_permissions = findViewById(R.id.edit_permissions_button);
        browse_button = findViewById(R.id.browse_button);

        // not gonna use this button for now, its so that that the user can attach files
        browse_button.setClickable(false);
        browse_button.setVisibility(View.GONE);

        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey("ID")){
                id = (int)getIntent().getExtras().get("ID");

                //If the user is adding a new button they dont need to see the permission or the delete record button
                delete_record.setClickable(false);
                delete_record.setVisibility(View.GONE);
                edit_permissions.setClickable(false);
                edit_permissions.setVisibility(View.GONE);


            }
            else if(getIntent().getExtras().containsKey("RECORD")){
                ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
                record = list.get(0);
                edit_record(record);
            }
        }
        add_update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(update){
                    final String name = name_input.getText().toString();
                    final String description = description_input.getText().toString();
                    try{
                        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                            private ProgressDialog p = new ProgressDialog(PatientRecordView.this);
                            protected void onPreExecute(){
                                super.onPreExecute();
                                p.setMessage("Loading");
                                p.setIndeterminate(false);
                                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                p.show();
                            }
                            protected Boolean doInBackground(Void... progress) {
                                return Lib.PatientUpdateRecord(name, description, record.getId());
                            }
                            protected void onPostExecute(Boolean result){
                                super.onPostExecute(result);
                                if(result){
                                    finish();
                                }
                                else {
                                    Toast toast = Toast.makeText(getApplicationContext(), "Update Error", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        };
                        asyncTask.execute();
                    }catch (Exception e){e.printStackTrace();}
                }
                else{
                    final String name = name_input.getText().toString();
                    final String description = description_input.getText().toString();
                    try{
                        AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                            private ProgressDialog p = new ProgressDialog(PatientRecordView.this);
                            protected void onPreExecute(){
                                super.onPreExecute();
                                p.setMessage("Loading");
                                p.setIndeterminate(false);
                                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                p.show();
                            }
                            protected Boolean doInBackground(Void... progress) {
                                Boolean result = false;
                                result = Lib.insertIntoRecord(name, description, id);
                                return result;
                            }
                            protected void onPostExecute(Boolean result){
                                super.onPostExecute(result);
                                p.dismiss();
                                if(result){
                                    finish();
                                }
                                else{
                                    Toast toast = Toast.makeText(getApplicationContext(), "Add Error", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        };
                        asyncTask.execute();
                    }catch (Exception e){e.printStackTrace();}
                }
            }
        });
    }

    private void edit_record(Record r){
            name_input.setText(r.getName());
            description_input.setText(r.getRecord());
            add_update_button.setText("Update Record");
            update=true;
    }
}
