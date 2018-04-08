package com.infosysproject.healthcare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class LoginActivity extends AppCompatActivity {

    private EditText user, password;
    private Button login_bt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        user = findViewById(R.id.userid);
        password = findViewById(R.id.password);
        login_bt = findViewById(R.id.login_bt);

        login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                URL url = null;
                String data = null;
                String user_id = user.getText().toString();
                String pwd = password.getText().toString();
                String check = user_id.subSequence(0, 2).toString();

                String patient_login = "http://192.168.137.1/HealthCare/patient_login.py";
                String doctor_login = "http://192.168.137.1/HealthCare/doctor_login.py";

                Log.d("userid", pwd + " " +check);
                //assign proper url according to the user_id
                try {
                    if (check.equals("HC"))
                        url = new URL(patient_login);
                    else if(check.equals("DC"))
                        url = new URL(doctor_login);
                    else
                        Toast.makeText(getApplicationContext(), "Incorrect UserId", Toast.LENGTH_LONG).show();

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                //setting up data to be sent to the server
                try {
                    data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                            URLEncoder.encode("password", "UTF-8") + "=" + URLEncoder.encode(pwd, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

                //using ExecutorService to get data from database
                ExecutorService executorService = Executors.newCachedThreadPool();
                Future<String> future = executorService.submit(new DataAccess(url, data));
                executorService.shutdown();
                String response = "";
                try {
                    response = future.get(5, TimeUnit.SECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }

                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG).show();
                if (response.startsWith("Patient")) {
                    Intent intent = new Intent(LoginActivity.this, Patient.class);
                    intent.putExtra("UserId", user_id);
                    startActivity(intent);
                } else if (response.startsWith("Doctor")) {
                    Intent intent = new Intent(LoginActivity.this, Doctor.class);
                    intent.putExtra("UserId", user_id);
                    startActivity(intent);
                }
            }
        });
    }
}
