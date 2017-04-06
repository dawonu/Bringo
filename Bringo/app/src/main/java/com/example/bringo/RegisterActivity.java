package com.example.bringo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText password1Input;
    private EditText password2Input;
    private Button registerButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailInput = (EditText) findViewById(R.id.register_email);
        password1Input = (EditText) findViewById(R.id.register_password);
        password2Input = (EditText) findViewById(R.id.register_confirm_password);

        registerButton = (Button) findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        loginButton = (Button) findViewById(R.id.sign_in_button);
        loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(loginIntent);
            }
        });
    }

    private void attemptRegister() {
        final String email = emailInput.getText().toString();
        final String password1 = password1Input.getText().toString();
        final String password2 = password2Input.getText().toString();

        emailInput.setError(null);
        password1Input.setError(null);
        View focusView;
        if (TextUtils.isEmpty(email)) {
            emailInput.setError(getString(R.string.error_field_required));
            focusView = emailInput;
            focusView.requestFocus();
            return;
        } else if (!isEmailValid(email)) {
            emailInput.setError(getString(R.string.error_invalid_email));
            focusView = emailInput;
            focusView.requestFocus();
            return;
        }
        if (!isPasswordValid(password1, password2)) {
            password1Input.setError(getString(R.string.error_incorrect_password));
            focusView = password1Input;
            focusView.requestFocus();
            return;
        }

        String jsonString = "{username: email, password: password1}";
        try {
            URL url = new URL("https://morning-waters-80123.herokuapp.com/register");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Accept", "text/json");

            conn.setDoOutput(true);
            OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
            out.write(jsonString);
            out.close();

            int status = conn.getResponseCode();
            if (status == 200) {
                Intent homeIntent = new Intent(RegisterActivity.this, HomeActivity.class);
                RegisterActivity.this.startActivity(homeIntent);
            } else {
                emailInput.setText("");
                password1Input.setText("");
                password2Input.setText("");
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }

    private boolean isPasswordValid(String password1, String password2) {
        return password1.equals(password2);
    }

}
