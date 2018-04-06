package com.infosysproject.healthcare;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

public class DataAccess implements Callable<String> {

    URL url;
    String data;

    public DataAccess(URL url, String data){
        this.url = url;
        this.data = data;
    }
    @Override
    public String call() throws Exception {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("POST");
        httpURLConnection.setDoOutput(true);
        //httpURLConnection.setDoInput(true);
        OutputStream OS = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(OS, "UTF-8"));

        bufferedWriter.write(data);
        bufferedWriter.flush();
        bufferedWriter.close();
        OS.close();
        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream,"iso-8859-1"));
        String response = "";
        String line = "";
        while ((line = bufferedReader.readLine())!=null)
        {
            response+= line;
        }
        bufferedReader.close();
        inputStream.close();
        httpURLConnection.disconnect();

        return response;
    }
}
