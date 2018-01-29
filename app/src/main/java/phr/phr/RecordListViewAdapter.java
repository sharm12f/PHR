package phr.phr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import phr.lib.Record;

/**
 * Created by Anupam on 28-Jan-18.
 */

public class RecordListViewAdapter extends ArrayAdapter<String>{
    private final Activity context;
    LinkedList<Record> records;

    public RecordListViewAdapter(Activity context, LinkedList<Record> records) {
        super(context, R.layout.activity_listview);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.records=records;
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.activity_listview, null,true);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        TextView id = (TextView) rowView.findViewById(R.id.hidden_text);
        Record r = records.get(position);
        name.setText(r.getName());
        id.setText(r.getId());
        return rowView;
    }
}
