package phr.phr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.Record;

/**
 * Created by Anupam on 28-Jan-18.
 */

public class RecordListViewAdapter extends ArrayAdapter<Record>{
    private final Activity context;
    ArrayList<Record> records;

    public RecordListViewAdapter(Activity context, ArrayList<Record> records) {
        super(context, R.layout.text_list_view);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.records=records;
    }

    public int getCount(){
        return records.size();
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=LayoutInflater.from(getContext()).inflate(R.layout.text_list_view, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        Record r = records.get(position);
        name.setText(r.getName());
        return rowView;
    }
}
