package phr.phr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.Patient;
import phr.lib.Record;

/**
 * Created by Anupam on 28-Jan-18.
 *
 *  This is the listview adapter for the health professional when they select view all record.
 *
 */

public class HealthProfessionalRecordListViewAdapter extends ArrayAdapter<String[]>{
    private final Activity context;
    ArrayList<String[]> records;

    public HealthProfessionalRecordListViewAdapter(Activity context, ArrayList<String[]> records) {
        super(context, R.layout.patient_record_listview);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.records=records;
    }

    public int getCount(){
        return records.size();
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=LayoutInflater.from(getContext()).inflate(R.layout.health_professional_records_listview, parent, false);
        TextView patient_name = (TextView) rowView.findViewById(R.id.patient_name_text);
        TextView record_name = (TextView) rowView.findViewById(R.id.record_name_text);
        String[] r = records.get(position);
        patient_name.setText(r[0]);
        record_name.setText(r[1]);
        return rowView;
    }
}
