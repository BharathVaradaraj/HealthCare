package com.infosysproject.healthcare;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

public class PatientHomePage extends Fragment {

    URL url;
    String data, user_id;
    TextView patient_id, patient_name, address, age, mob, bloodtype;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_home_page, container, false);

        patient_id = rootView.findViewById(R.id.patient_id);
        patient_name = rootView.findViewById(R.id.name);
        address = rootView.findViewById(R.id.address);
        age = rootView.findViewById(R.id.age);
        mob = rootView.findViewById(R.id.mob);
        bloodtype = rootView.findViewById(R.id.bloodtype);

        user_id = getArguments().getString("UserId");

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

        return rootView;
    }
}
