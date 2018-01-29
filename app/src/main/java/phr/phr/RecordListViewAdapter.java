package phr.phr;

import android.app.Activity;
import android.widget.ArrayAdapter;

import phr.lib.Record;

/**
 * Created by Anupam on 28-Jan-18.
 */

public class RecordListViewAdapter extends ArrayAdapter<Record>{
    @Override
    public RecordListViewAdapter(Activity context, String[] itemname, Integer[] imgid) {
        super(context, R.layout.activity_listview, itemname);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.itemname=itemname;
        this.imgid=imgid;
    }
}
