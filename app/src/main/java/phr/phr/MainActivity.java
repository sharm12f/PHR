package phr.phr;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import phr.lib.Lib;

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
                System.out.println(Lib.login(email_input.getText().toString(), password_input.getText().toString()));
            }
        });
    }
}
