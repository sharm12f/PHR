package phr.phr;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.Record;

/**
 * Created by Anupam on 30-Jan-18.
 */

public class HealthProfessionalRecordView extends AppCompatActivity {
    TextView record_name_text, record_description_text;
    Button leave_not_button;
    Record record;
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        ActionBar actionbar = getSupportActionBar();
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.p3hr_launcher);
        setContentView(R.layout.health_professional_view_record);
        record_name_text = findViewById(R.id.record_name_text);
        record_description_text = findViewById(R.id.record_decription_text);
        leave_not_button = findViewById(R.id.leave_note_button);
        if(getIntent().getExtras()!=null){
            if(getIntent().getExtras().containsKey("RECORD")){
                ArrayList<Record> list = (ArrayList<Record>)getIntent().getExtras().get("RECORD");
                record = list.get(0);
                record_name_text.setText(record.getName());
                record_description_text.setText(record.getRecord());
            }
        }

    }

}
