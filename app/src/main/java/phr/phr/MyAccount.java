package phr.phr;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import phr.lib.Lib;
import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 25-Jan-18.
 */

public class MyAccount extends AppCompatActivity {
    TextView name;
    TextView email;
    TextView phone;
    TextView region;
    TextView province;
    Button edit_user ;
    ListView record_list_view;
    User user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        region = findViewById(R.id.region);
        province = findViewById(R.id.province);
        edit_user = findViewById(R.id.edit_user_button);
        record_list_view = findViewById(R.id.records_list_view);
        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("USER");
        user = list.get(0);
        RecordListViewAdapter adapter = new RecordListViewAdapter(this, user.getRecords());
        record_list_view.setAdapter(adapter);

        edit_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UpdateUserInfo.class);
                ArrayList<User> list = new ArrayList<User>();
                list.add(user);
                intent.putExtra("USER",list);
                startActivity(intent);
            }
        });

        record_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                String Slecteditem= user.getRecords().get(+position).toString();
                Toast.makeText(getApplicationContext(), Slecteditem, Toast.LENGTH_SHORT).show();

            }
        });
    }
    @Override
    protected void onResume(){
        super.onResume();
        try{
            user = new AsyncTask<Void, Void, User>() {
                protected User doInBackground(Void... progress) {
                    User result = null;
                    result = Lib.makeUser(user.getEmail());
                    return result;
                }
            }.execute().get();

        }catch(Exception e){e.printStackTrace();}

        setFields();
    }

    private void setFields(){
        name.setText(user.getfName()+" "+user.getlName());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        region.setText(user.getRegion());
        province.setText(user.getProvince());
    }
    private void setRecords(){

    }
}
