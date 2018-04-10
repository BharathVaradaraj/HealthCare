package com.infosysproject.healthcare;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
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

public class Tests extends Fragment {

    ArrayList orderList = new ArrayList();
    ArrayList testList = new ArrayList();
    ListView listView;
    Spinner tests;
    Button order;
    URL url;
    String data;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tests, container, false);

        listView = rootView.findViewById(R.id.test_list);
        tests = rootView.findViewById(R.id.tests);
        order = rootView.findViewById(R.id.order);

        //get order list
        String res = get_order_list();
        String[] arr = res.split("&");
        for (int i = 0; i < arr.length; i++)
            orderList.add(arr[i]);

        final ArrayAdapter listAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, orderList);
        listView.setAdapter(listAdapter);

        //populate the spinner
        res = get_test_list();
        String[] lis = res.split("&");
        for (int i = 0; i < lis.length; i++)
            testList.add(lis[i]);

        ArrayAdapter<String> spinnerApadpter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, testList);
        spinnerApadpter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tests.setAdapter(spinnerApadpter);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure you want to delete this entry");

        //setting listview click listner
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final String testname = ((TextView) view).getText().toString();

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String response = delete_test(testname);
                        Toast.makeText(getContext(), response, Toast.LENGTH_LONG).show();

                        orderList.clear();
                        //get order list
                        String res = get_order_list();
                        String[] arr = res.split("&");
                        for (int j = 0; j < arr.length; j++)
                            orderList.add(arr[j]);
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
                String testname = tests.getSelectedItem().toString();

                order_test(testname);
                orderList.clear();
                //get order list
                String res = get_order_list();
                String[] arr = res.split("&");
                for (int i = 0; i < arr.length; i++)
                    orderList.add(arr[i]);

                listAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }


    private String delete_test(String testname) {
        try {
            url = new URL("http://192.168.137.1/HealthCare/delete_test.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("Patient_Id"), "UTF-8") + "&" +
                    URLEncoder.encode("test", "UTF-8") + "=" + URLEncoder.encode(testname, "UTF-8");
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

    private void order_test(String testname) {
        try {
            url = new URL("http://192.168.137.1/HealthCare/order_test.py");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        //setting up data to be sent to the server
        try {
            data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(getArguments().getString("Patient_Id"), "UTF-8") + "&" +
                    URLEncoder.encode("test", "UTF-8") + "=" + URLEncoder.encode(testname, "UTF-8");
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

    private String get_order_list(){
        try {
            url = new URL("http://192.168.137.1/HealthCare/get_ordered_tests.py");
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

    private String get_test_list(){
        try {
            url = new URL("http://192.168.137.1/HealthCare/get_tests.py");
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
