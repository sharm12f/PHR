package phr.phr;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;

import phr.lib.Record;
import phr.lib.User;

/**
 * Created by Anupam on 25-Jan-18.
 */

public class MyAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_account);
        final TextView name = findViewById(R.id.name);
        final TextView email = findViewById(R.id.email);
        final TextView phone = findViewById(R.id.phone);
        final TextView region = findViewById(R.id.region);
        final TextView province = findViewById(R.id.province);
        final Button edit_user = findViewById(R.id.edit_user_button);
        final ListView record_list_view = findViewById(R.id.records_list_view);
        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("USER");
        final User user = list.get(0);
        name.setText(user.getName());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        region.setText(user.getRegion());
        province.setText(user.getProvince());

        setRecords(record_list_view, user.getRecords());

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
    }
    private void setRecords(ListView records_list_view, LinkedList<Record> records){

    }
}
