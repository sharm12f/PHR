package phr.phr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import phr.lib.Record;

/**
 * Created by Anupam on 30-Jan-18.
 */

public class RecordView extends AppCompatActivity {
    EditText name_input, description_input;
    Button add_update_button;
    Record record;
    int id;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.add_record);
        name_input = findViewById(R.id.name_input);
        description_input = findViewById(R.id.description_input);
        add_update_button = findViewById(R.id.add_update_button);
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey("ID")){
                id = (int)getIntent().getExtras().get("ID");
            }
            else if(getIntent().getExtras().containsKey("RECORD")){
                ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
                record = list.get(0);
                edit_record(record);
            }
        }
        
    }

    private void edit_record(Record r){
            name_input.setText(r.getName());
            description_input.setText(r.getRecord());
            add_update_button.setText("Update Record");
    }
}
