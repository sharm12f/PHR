package phr.phr;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codekidlabs.storagechooser.StorageChooser;

import java.io.File;
import java.util.ArrayList;

import phr.lib.Patient;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 30-Jan-18.
 *
 * User can arriave to this page using two different methods
 *  1 - By selecting add record
 *  2 - By selecting a record from the dbRegions
 *
 *  1 - The page layout if they select add record is
 *      edit text at the top to allow the user to enter the record name
 *      edit text under the description to allow the user to enter a description for the record
 *      add record button at the bottom to add the record to their account.
 *
 *  2 - The page layout if they select a record is
 *      edit text at the top with the name of the record pre-filled allowing the user to update the name if needed
 *      edit text user the description pre-filled allowing the user to update the description if needed
 *      3 button
 *      1 - edit permission
 *          allowing the user to view/add/remove permission for this record
 *      2 - update record
 *          allowing the user to update any changes they made
 *      3 - delete record
 *          allowing the user to delete the record
 *
 */

public class PatientRecordView extends AppCompatActivity {
    EditText name_input, description_input;
    TextView file_name;
    Button add_update_button, browse_button, delete_record, edit_permissions;
    Record record;
    String fileSelected, fileName;
    boolean update=false;
    int id;

    Patient patient;
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
        file_name = findViewById(R.id.file_name);

        // get the user object should always be passed from activity to activity
        ArrayList<User> u = (ArrayList<User>) getIntent().getExtras().get("USER");
        patient = (Patient)u.get(0);

        // check to see the the intent was passed with information
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey("ID")){
                // the user wants to add a new record, and user id is passed to achive this
                id = (int)getIntent().getExtras().get("ID");
                delete_record.setClickable(false);
                delete_record.setVisibility(View.GONE);
                edit_permissions.setClickable(false);
                edit_permissions.setVisibility(View.GONE);
            }
            else if(getIntent().getExtras().containsKey("RECORD")){
                // the user wants to update a pre-existing record and that record is passed
                ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
                record = list.get(0);
                edit_record(record);
            }
        }

        // allows the user to edit the records permissions
        edit_permissions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PatientEditPermissionsRecord.class);
                ArrayList<Record> list = new ArrayList<>();
                list.add(record);
                ArrayList<User> list2 = new ArrayList<>();
                list2.add(patient);
                intent.putExtra("USER",list2);
                intent.putExtra("RECORD",list);
                startActivity(intent);
                finish();
            }
        });

        // allows the user to delete the record
        delete_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    AsyncTask<Void, Void, Boolean> asyncTask = new AsyncTask<Void, Void, Boolean>() {
                        private ProgressDialog p = new ProgressDialog(PatientRecordView.this);
                        protected void onPreExecute(){
                            super.onPreExecute();
                            p.setMessage("Loading");
                            p.setCancelable(false);
                            p.setIndeterminate(false);
                            p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                            p.show();
                        }
                        protected Boolean doInBackground(Void... progress) {
                            return Lib.deleteRecord(record.getId());
                        }
                        protected void onPostExecute(Boolean result){
                            super.onPostExecute(result);
                            p.dismiss();
                            if(result){
                                Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
                                ArrayList<User> list = new ArrayList<User>();
                                list.add(patient);
                                intent.putExtra("USER",list);
                                startActivity(intent);
                                finish();
                            }
                            else {
                                Toast toast = Toast.makeText(getApplicationContext(), "Delete Error", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    };
                    asyncTask.execute();
                }catch (Exception e){e.printStackTrace();}
            }
        });

        // allows the user to update and or add new record, depending on how they got here (see the first block comment)
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
                                p.setCancelable(false);
                                p.setIndeterminate(false);
                                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                p.show();
                            }
                            protected Boolean doInBackground(Void... progress) {
                                Boolean result = false;
                                if(fileSelected!=null){
                                    if(fileSelected.equals("delete")){
                                        fileName = "null";
                                    }
                                    result = Lib.PatientUpdateRecord(name, description, record.getId(), fileName);
                                }
                                else{
                                    result = Lib.PatientUpdateRecord(name, description, record.getId());
                                }
                                if(fileSelected!=null && result){
                                    if(!fileSelected.equals("delete")){
                                        Lib.sendFile(new File(fileSelected),patient);
                                    }
                                }
                                return result;
                            }
                            protected void onPostExecute(Boolean result){
                                super.onPostExecute(result);
                                p.dismiss();
                                if(result){
                                    Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(patient);
                                    intent.putExtra("USER",list);
                                    startActivity(intent);
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
                                p.setCancelable(false);
                                p.setIndeterminate(false);
                                p.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                p.show();
                            }
                            protected Boolean doInBackground(Void... progress) {
                                Boolean result = false;
                                if(fileSelected!=null){
                                    result = Lib.insertIntoRecord(name, description, id, fileName);
                                }
                                else{
                                    result = Lib.insertIntoRecord(name, description, id);
                                }
                                if(fileSelected!=null & result){
                                    Lib.sendFile(new File(fileSelected),patient);
                                }
                                return result;
                            }
                            protected void onPostExecute(Boolean result){
                                super.onPostExecute(result);
                                p.dismiss();
                                if(result){
                                    Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
                                    ArrayList<User> list = new ArrayList<User>();
                                    list.add(patient);
                                    intent.putExtra("USER",list);
                                    startActivity(intent);
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


        browse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String button_name = browse_button.getText().toString();
                if(button_name.equals("Browse")) {
                    try {
                        StorageChooser chooser = new StorageChooser.Builder()
                                .withActivity(PatientRecordView.this)
                                .withFragmentManager(getFragmentManager())
                                .withPredefinedPath(getFilesDir().getPath())
                                .withMemoryBar(true)
                                .allowCustomPath(true)
                                .setType(StorageChooser.FILE_PICKER)
                                .build();
                        chooser.show();

                        chooser.setOnSelectListener(new StorageChooser.OnSelectListener() {
                            @Override
                            public void onSelect(String path) {
                                fileSelected = path;
                                String[] temp = path.split("/");
                                fileName = temp[temp.length - 1];
                                boolean supportedType = false;
                                String[] split = fileName.split("\\.");
                                String extension = split[split.length-1];
                                for(String e:Lib.IMAGE_EXT){
                                    if(extension.equals(e))
                                        supportedType=true;
                                }
                                for(String e:Lib.FILE_EXT){
                                    if(extension.equals(e))
                                        supportedType=true;
                                }
                                if(supportedType) {
                                    boolean nameCheck = false;
                                    for(Record r:patient.getRecords()){
                                        if(!r.getFilename().equals("null")){
                                            if(fileName.equals(r.getFilename())){
                                                nameCheck=true;
                                            }
                                        }
                                    }
                                    if(!nameCheck)
                                        file_name.setText("File Name: " + fileName);
                                    else{
                                        AlertDialog.Builder builder = new AlertDialog.Builder(PatientRecordView.this);
                                        builder.setMessage("File with the same name already in database, this will override the old file.\n\nSelect 'Yes' if it is the same file.")
                                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        file_name.setText("File Name: " + fileName);
                                                    }
                                                })
                                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        fileSelected = null;
                                                        file_name.setText("File Name: ");
                                                    }
                                                });
                                        AlertDialog dialog = builder.create();
                                        dialog.setCancelable(false);
                                        dialog.show();
                                    }
                                }
                                else{
                                    Toast toast = Toast.makeText(getApplicationContext(), "File Type Not Supported", Toast.LENGTH_SHORT);
                                    toast.show();
                                    fileSelected = null;
                                    file_name.setText("File Name: ");
                                }
                            }
                        });
                        chooser.setOnCancelListener(new StorageChooser.OnCancelListener() {
                            @Override
                            public void onCancel() {
                                fileSelected = null;
                                file_name.setText("File Name: ");
                            }
                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if(button_name.equals("Delete Attachment")){
                    fileSelected = "delete";
                    file_name.setText("File Name: ");
                }
            }
        });
    }

    // set the fields of all the edit text if the thats whats happening.
    private void edit_record(Record r){
            name_input.setText(r.getName());
            description_input.setText(r.getRecord());
            if(!r.getFilename().equals("null")){
                file_name.setText("File Name: " + r.getFilename());
                browse_button.setText("Delete Attachment");
            }
            add_update_button.setText("Update Record");
            update=true;
    }

    // controls the flow of the application regarless of the stack.
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), PatientAccount.class);
        ArrayList<User> list = new ArrayList<>();
        list.add(patient);
        intent.putExtra("USER",list);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
