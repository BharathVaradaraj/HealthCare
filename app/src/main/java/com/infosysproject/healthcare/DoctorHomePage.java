package com.infosysproject.healthcare;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class DoctorHomePage extends Fragment implements View.OnClickListener {

    URL url;
    String data, user_id;
    TextView patient_id, patient_name, address, age, mob, bloodtype;
    EditText pat_add, pat_mob, pat_age, pat_blood;
    Button logout, update;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_home_page, container, false);

        context = container.getContext();

        patient_id = rootView.findViewById(R.id.patient_id);
        patient_name = rootView.findViewById(R.id.name);
        address = rootView.findViewById(R.id.address);
        age = rootView.findViewById(R.id.age);
        mob = rootView.findViewById(R.id.mob);
        bloodtype = rootView.findViewById(R.id.bloodtype);
        pat_add = rootView.findViewById(R.id.editaddress);
        pat_age = rootView.findViewById(R.id.editage);
        pat_mob = rootView.findViewById(R.id.editmob);
        pat_blood = rootView.findViewById(R.id.editbloodtype);
        logout = rootView.findViewById(R.id.pat_logout);
        update = rootView.findViewById(R.id.update);

        //setting listners for button
        logout.setOnClickListener(this);
        update.setOnClickListener(this);
        address.setOnClickListener(this);
        age.setOnClickListener(this);
        mob.setOnClickListener(this);
        bloodtype.setOnClickListener(this);

        display_patient_data();

        return rootView;
    }

    private void display_patient_data() {
        user_id = getArguments().getString("Patient_Id");

        try {
            url = new URL("http://192.168.137.1/HealthCare/patient_data.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8");
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

        String[] array = new String[7];
        array = response.split("&");

        patient_id.setText(array[0]);
        patient_name.setText(array[2]);
        address.setText(array[3]);
        age.setText(array[4]);
        mob.setText(array[5]);
        bloodtype.setText(array[6]);

        pat_add.setText(array[3]);
        pat_age.setText(array[4]);
        pat_mob.setText(array[5]);
        pat_blood.setText(array[6]);
    }

    //setting listner for logout button

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.pat_logout: Intent intent = new Intent(context.getApplicationContext(), LoginActivity.class);
                                  context.startActivity(intent);
                                  break;

            case R.id.update: update_patient_data();
                              display_patient_data();
                              break;

            case R.id.address: address.setVisibility(View.INVISIBLE);
                               pat_add.setVisibility(View.VISIBLE);
                               break;

            case R.id.age: age.setVisibility(View.INVISIBLE);
                           pat_age.setVisibility(View.VISIBLE);
                           break;

            case R.id.mob: mob.setVisibility(View.INVISIBLE);
                           pat_mob.setVisibility(View.VISIBLE);
                           break;

            case R.id.bloodtype: bloodtype.setVisibility(View.INVISIBLE);
                                 pat_blood.setVisibility(View.VISIBLE);
                                 break;
        }
    }

    private void update_patient_data() {

        String add = pat_add.getText().toString();
        String p_age = pat_age.getText().toString();
        String p_mob = pat_mob.getText().toString();
        String bl_type = pat_blood.getText().toString();

        try {
            url = new URL("http://192.168.137.1/HealthCare/patient_update.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(user_id, "UTF-8") + "&" +
                    URLEncoder.encode("address", "UTF-8") + "=" + URLEncoder.encode(add, "UTF-8") + "&" +
                    URLEncoder.encode("age", "UTF-8") + "=" + URLEncoder.encode(p_age, "UTF-8") + "&" +
                    URLEncoder.encode("mob", "UTF-8") + "=" + URLEncoder.encode(p_mob, "UTF-8") + "&" +
                    URLEncoder.encode("blood_type", "UTF-8") + "=" + URLEncoder.encode(bl_type, "UTF-8");
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
        Toast.makeText(context, response, Toast.LENGTH_LONG).show();

        //setting editText visibility
        address.setVisibility(View.VISIBLE);
        age.setVisibility(View.VISIBLE);
        mob.setVisibility(View.VISIBLE);
        bloodtype.setVisibility(View.VISIBLE);
        pat_add.setVisibility(View.INVISIBLE);
        pat_age.setVisibility(View.INVISIBLE);
        pat_mob.setVisibility(View.INVISIBLE);
        pat_blood.setVisibility(View.INVISIBLE);
    }
}
