package phr.phr;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.EditText;

import java.util.ArrayList;

import phr.lib.User;

/**
 * Created by Anupam on 26-Jan-18.
 */

public class UpdateUserInfo extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstance){
        super.onCreate(savedInstance);
        setContentView(R.layout.update_user_info);
        ArrayList<User> list = (ArrayList<User>)getIntent().getExtras().get("USER");
        User user = list.get(0);
        final EditText name_input = findViewById(R.id.name_input);
        final EditText email_input = findViewById(R.id.email_input);
        final EditText phone_input = findViewById(R.id.phone_input);
        name_input.setText(user.getName());
        email_input.setText(user.getEmail());
        phone_input.setText(user.getPhone());
    }
}
