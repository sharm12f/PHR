package phr.phr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.ArrayList;

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
        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("USER");
        User user = list.get(0);
        name.setText(user.getName());
        email.setText(user.getEmail());
        phone.setText(user.getPhone());
        region.setText(user.getRegion());
        province.setText(user.getProvince());
    }
}
