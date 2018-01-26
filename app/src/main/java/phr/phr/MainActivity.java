package phr.phr;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.lang.reflect.Array;
import java.util.ArrayList;

import phr.lib.Lib;
import phr.lib.User;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button login_button = findViewById(R.id.login_button);
        final Button reser_password_button = findViewById(R.id.reset_password_button);
        final Button password_button = findViewById(R.id.reset_password_button);
        final EditText email_input = findViewById(R.id.email_input);
        final EditText password_input=  findViewById(R.id.password_input);
        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                try {
                    User user = new AsyncTask<Void, Void, User>() {
                        protected User doInBackground(Void... progress) {
                            System.out.println("Start Login");
                            return Lib.login(email_input.getText().toString(), password_input.getText().toString());
                        }
                    }.execute().get();

                    Intent intent = new Intent(getApplicationContext(), MyAccount.class);
                    ArrayList<User> list = new ArrayList<User>();
                    list.add(user);
                    intent.putExtra("USER",list);
                    startActivity(intent);
                }catch (Exception e){e.printStackTrace();}
            }
        });
    }
}
