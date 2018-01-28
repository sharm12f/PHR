package phr.phr;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

import phr.lib.Lib;
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
        final Button update_button = findViewById(R.id.update_button);
        name_input.setText(user.getName());
        email_input.setText(user.getEmail());
        phone_input.setText(user.getPhone());

        update_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = name_input.getText().toString();
                final String email = email_input.getText().toString();
                final String phone = phone_input.getText().toString();
                final String region = "Windsor";
                final String province = "Ontaio";
                try{
                    Boolean Success = new AsyncTask<Void, Void, Boolean>() {
                        protected Boolean doInBackground(Void... progress) {
                            boolean result = false;
                            result = Lib.updateUser(name,email,phone,region,province);
                            return result;
                        }
                    }.execute().get();
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }
}
