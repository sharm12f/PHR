package phr.phr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.Record;
import phr.lib.RecordPermission;

/**
 * Created by Anupam on 10-Apr-18.
 */
public class PermissionListViewAdapter extends ArrayAdapter<String> {
    private final Activity context;
    ArrayList<RecordPermission> list;

    public PermissionListViewAdapter(Activity context, ArrayList<RecordPermission> list) {
        super(context, R.layout.text_list_view);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.list=list;
    }

    public int getCount(){
        return list.size();
    }

    public View getView(int position, View view, ViewGroup parent){
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=LayoutInflater.from(getContext()).inflate(R.layout.text_list_view, parent, false);

        TextView name = (TextView) rowView.findViewById(R.id.name);
        String r = list.get(position).getHp_name();
        name.setText(r);
        return rowView;
    }
}

