package com.infosysproject.healthcare;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class PatientAppointments extends Fragment {

    ArrayList<PatientData> appList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerAdapter recyclerAdapter;
    RecyclerView.LayoutManager layoutManager;
    ArrayList arrayList = new ArrayList();
    Spinner spinner_doc, spinner_time;
    Button book_app;
    URL url;
    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_appointments, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerViewPat);
        spinner_doc = rootView.findViewById(R.id.spinner_doc);
        spinner_time = rootView.findViewById(R.id.spinner_time);
        book_app = rootView.findViewById(R.id.book_app);

        //displaying booked appoinments
        String res = get_patient_app();
        String[] arr = res.split("&");
        for(int i=0,j=0;j<arr.length/3;i=i+3,j++){
            PatientData patientData = new PatientData(arr[i], arr[i+1], arr[i+2]);
            appList.add(patientData);
        }

        recyclerAdapter = new RecyclerAdapter(appList, getContext());
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerAdapter);

        //setting doctor list for spinner
        get_doctor_list();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, arrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_doc.setAdapter(arrayAdapter);

        //setting time spinner
        final ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getContext(),
                R.array.app_time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_time.setAdapter(adapter);

        book_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String[] doc_data = spinner_doc.getSelectedItem().toString().split(" \\[");
                String doc = doc_data[0];
                String time = spinner_time.getSelectedItem().toString();

                String res = book_appointment(doc, time);

                if(res.startsWith("Appointment"))
                    Toast.makeText(getContext(), res, Toast.LENGTH_LONG).show();
                else
                    Toast.makeText(getContext(), "Appointment Already Booked", Toast.LENGTH_LONG).show();

                //refreshing appointment list
                appList.clear();
                String response = get_patient_app();
                String[] arr = response.split("&");
                for(int i=0,j=0;j<arr.length/3;i=i+3,j++){
                    PatientData patientData = new PatientData(arr[i], arr[i+1], arr[i+2]);
                    appList.add(patientData);
                }
                recyclerAdapter.notifyDataSetChanged();
            }
        });

        return rootView;
    }

    private String get_patient_app() {
        try {
            url = new URL("http://192.168.137.1/HealthCare/get_patient_app.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("UserId"), "UTF-8");
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
        return response;
    }

    private String book_appointment(String doc, String time) {
        try {
            url = new URL("http://192.168.137.1/HealthCare/book_appointment.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("UserId"), "UTF-8") + "&" +
                    URLEncoder.encode("doctor", "UTF-8") + "=" + URLEncoder.encode(doc, "UTF-8") + "&" +
                    URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8");
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

        return response;
    }

    private void get_doctor_list() {
        try {
            url = new URL("http://192.168.137.1/HealthCare/doctor_list.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode("", "UTF-8");
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

        String[] arr = response.split("&");
        for(int i=0,j=0;j<arr.length/2;i=i+2,j++) {
            arrayList.add(arr[i] + " [ " + arr[i+1] + " ]");
        }
    }
}
