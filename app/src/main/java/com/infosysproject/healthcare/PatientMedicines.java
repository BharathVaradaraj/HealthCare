package com.infosysproject.healthcare;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
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

public class PatientMedicines extends Fragment {

    ArrayList arrayList = new ArrayList();
    ListView listView;
    URL url;
    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_patient_medicines, container, false);

        listView = rootView.findViewById(R.id.pat_meds);

        String res = get_pres_med_list();
        String[] arr = res.split("&");
        for (int i = 0; i < arr.length; i++)
            arrayList.add(arr[i]);

        ArrayAdapter listAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String med = ((TextView) view).getText().toString();

                String res = get_med_details(med);
                String[] arr = res.split("&");

                String data = "Drug Name : " + arr[0] + "\nNumber of Times per Day : " + arr[1]
                        + "\nConsumption Time : " + arr[2] + "Food";

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Medicine Details :");
                builder.setMessage(data);
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


        return rootView;
    }

    private String get_med_details(String med) {
        try {
            url = new URL("http://192.168.137.1/HealthCare/get_med_details.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("UserId"), "UTF-8") + "&" +
                    URLEncoder.encode("med", "UTF-8") + "=" + URLEncoder.encode(med, "UTF-8");
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

    private String get_pres_med_list(){
        try {
            url = new URL("http://192.168.137.1/HealthCare/get_pres_med_list.py");
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
}
