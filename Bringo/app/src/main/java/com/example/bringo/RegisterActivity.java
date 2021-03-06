package com.example.bringo;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.bringo.database.UserDB;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

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

//        String password = String.valueOf(password1.hashCode());
        String password = SHA256password(password1);
        System.out.println("hashed password in SHA256: " + password);
        if (password != null) {
            new AsyncGetRegister().execute(email, password);
        }
    }

    private boolean isEmailValid(String email){
        return email.contains("@");
    }

    private boolean isPasswordValid(String password1, String password2) {
        return (password1.length() >= 3) && (password1.equals(password2));
    }

    private static String SHA256password(String password) {
        MessageDigest md;
        try {
            byte[] passwordByte = password.getBytes();
            md = MessageDigest.getInstance("SHA-256");
            byte[] digestResult = md.digest(passwordByte);
            BigInteger bI = new BigInteger(digestResult);
            String hashedPassword = bI.toString();
            return hashedPassword;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    private class AsyncGetRegister extends AsyncTask<String,String,Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            String jsonString = "{\"username\": " + params[0] + ", \"password\": " + params[1] + "}";
            System.out.println(jsonString);
            try {
                URL url = new URL("https://morning-waters-80123.herokuapp.com/register");
                HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Accept", "text/json");

                conn.setDoOutput(true);
                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());
                out.write(jsonString);
                out.close();

                int status = conn.getResponseCode();
                System.out.println("status = " + status);
                if (status == 200) {
                    System.out.println("succeed!");
                    // delete all testing records from data base
                    UserDB.deleteAll(UserDB.class);
                    // register, create a new one
                    UserDB userDB = new UserDB(params[0]);
                    userDB.save();
                    return true;
                }
                System.out.println("fail!");
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return false;
        }

        @Override
        protected void onPostExecute(final Boolean success) {

            if (success) {
                Intent settingsIntent = new Intent(RegisterActivity.this, SettingsActivity.class);
                RegisterActivity.this.startActivity(settingsIntent);
            } else {
                emailInput.setError("Fail to register");
                emailInput.requestFocus();
            }
        }
    }

}
