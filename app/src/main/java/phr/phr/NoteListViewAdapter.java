package phr.phr;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import phr.lib.Note;
import phr.lib.Record;

/**
 * Created by Anupam on 28-Jan-18.
 */

public class NoteListViewAdapter extends ArrayAdapter<Record>{
    private final Activity context;
    ArrayList<Note> notes;

    public NoteListViewAdapter(Activity context, ArrayList<Note> notes) {
        super(context, R.layout.text_list_view);
        // TODO Auto-generated constructor stub
        this.context=context;
        this.notes=notes;
    }

    public int getCount(){
        return notes.size();
    }

    public View getView(int position, View view, ViewGroup parent){

        View rowView=LayoutInflater.from(getContext()).inflate(R.layout.text_list_view, parent, false);
        TextView name = (TextView) rowView.findViewById(R.id.name);
        Note n = notes.get(position);
        name.setText(n.getName());
        return rowView;
    }
}
