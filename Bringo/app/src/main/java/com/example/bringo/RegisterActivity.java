package com.example.bringo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        final EditText emailInput = (EditText) findViewById(R.id.register_email);
        final EditText password1Input = (EditText) findViewById(R.id.register_password);
        final EditText password2Input = (EditText) findViewById(R.id.register_confirm_password);

        final Button registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String email = emailInput.getText().toString();
                final String password1 = password1Input.getText().toString();
                final String password2 = password2Input.getText().toString();
            }
        });

        final Button loginButton = (Button) findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(loginIntent);
            }
        });
    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }

    private boolean isPasswordValid(String password1, String password2) {
        return password1.length() >= 3 && password1.equals(password2);
    }

}
