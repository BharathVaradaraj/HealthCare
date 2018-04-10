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
import android.widget.Button;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

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

public class Medicines extends Fragment {

    ArrayList pres_med = new ArrayList();
    ArrayList medList = new ArrayList();
    RadioGroup radioGroup;
    RadioButton radioButton;
    ToggleButton toggleButton;
    ListView listView;
    Spinner meds;
    Button order;
    URL url;
    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_medicines, container, false);

        radioGroup = rootView.findViewById(R.id.radioGroup);
        toggleButton = rootView.findViewById(R.id.toggleButton);
        listView = rootView.findViewById(R.id.med_list);
        meds = rootView.findViewById(R.id.spinner_med);
        order = rootView.findViewById(R.id.button);

        //get prescribed medicine list
        String res = get_pres_med_list();
        String[] arr = res.split("&");
        for (int i = 0; i < arr.length; i++)
            pres_med.add(arr[i]);

        final ArrayAdapter listAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, pres_med);
        listView.setAdapter(listAdapter);

        //populate the medicine spinner
        res = get_med_list();
        String[] lis = res.split("&");
        for (int i = 0; i < lis.length; i++)
            medList.add(lis[i]);

        ArrayAdapter<String> spinnerApadpter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, medList);
        spinnerApadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        meds.setAdapter(spinnerApadpter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete this entry");

        //setting listview click listner
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String med = ((TextView) view).getText().toString();

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String response = delete_med(med);
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();

                        pres_med.clear();
                        //get order list
                        String res = get_pres_med_list();
                        String[] arr = res.split("&");
                        for (int j = 0; j < arr.length; j++)
                            pres_med.add(arr[j]);
                        listAdapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String med = meds.getSelectedItem().toString();
                int id = radioGroup.getCheckedRadioButtonId();
                String cons;

                radioButton = rootView.findViewById(id);

                if(toggleButton.isChecked())
                    cons = toggleButton.getTextOn().toString();
                else
                    cons = toggleButton.getTextOff().toString();

                prescribe_med(med, radioButton.getText().toString(), cons);
                pres_med.clear();
                //get prescribed medicine list
                String res = get_pres_med_list();
                String[] arr = res.split("&");
                for (int i = 0; i < arr.length; i++)
                    pres_med.add(arr[i]);

                listAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }


    private String delete_med(String med) {
        try {
            url = new URL("http://192.168.137.1/HealthCare/delete_med.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("Patient_Id"), "UTF-8") + "&" +
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

    private void prescribe_med(String med, String time, String cons) {
        try {
            url = new URL("http://192.168.137.1/HealthCare/prescribe_med.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("Patient_Id"), "UTF-8") + "&" +
                    URLEncoder.encode("med", "UTF-8") + "=" + URLEncoder.encode(med, "UTF-8") + "&" +
                    URLEncoder.encode("time", "UTF-8") + "=" + URLEncoder.encode(time, "UTF-8") + "&" +
                    URLEncoder.encode("cons", "UTF-8") + "=" + URLEncoder.encode(cons, "UTF-8");
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
    }

    private String get_pres_med_list(){
        try {
            url = new URL("http://192.168.137.1/HealthCare/get_pres_med_list.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("Patient_Id"), "UTF-8");
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

    private String get_med_list(){
        try {
            url = new URL("http://192.168.137.1/HealthCare/get_med_list.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("Patient_Id"), "UTF-8");
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
