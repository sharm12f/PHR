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
 *  This is the list view that the user sees with all the patients on the hp account page
 *
 *  I have reused the patients list view since there is only one text item that needs to be displayed for the list, but in this case that text is set to the patients name
 *
 */

public class HealthProfessionalPatientListViewAdapter extends ArrayAdapter<Record>{
    private final Activity context;
    ArrayList<Patient> patients;

    public HealthProfessionalPatientListViewAdapter(Activity context, ArrayList<Patient> patients) {
        super(context, R.layout.patient_record_listview);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.patients =patients;
    }

    public int getCount(){
        return patients.size();
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=LayoutInflater.from(getContext()).inflate(R.layout.patient_record_listview, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        Patient p = patients.get(position);
        name.setText(p.getName());
        return rowView;
    }
}
