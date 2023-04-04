package com.toygoon.safeshare.http;

import android.os.Handler;
import android.os.Looper;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkTask {
    private String url;
    private HashMap<String, String> param;
    private JSONObject json;
    private OkHttpClient client;
    private boolean isGet;

    public NetworkTask(String url, HashMap<String, String> param, String method) {
        this.url = url;
        this.param = param;
        this.json = new JSONObject();
        this.client = new OkHttpClient();
        this.isGet = method.toLowerCase().equals("GET");
    }

    public HashMap<String, String> request(int expectedResponse) {
        HashMap<String, String> result = new HashMap<>();

        try {
            if (param != null) {
                for (String k : this.param.keySet())
                    this.json.put(k, this.param.get(k));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        Request.Builder builder = new Request.Builder();
        builder.url(this.url);

        if (this.isGet)
            builder.get();
        else
            builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString()));

        Request request = builder.build();

        new Thread(() -> {
            try {
                Response response = client.newCall(request).execute();
                String body = response.body().string();

                int responseCode = response.code();
                Map<String, Object> mapping = new ObjectMapper().readValue(body, HashMap.class);

                for (String m : mapping.keySet()) {
                    result.put(m, mapping.get(m).toString());
                }

                Handler handler = new Handler(Looper.getMainLooper());
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                    }
                }, 0);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }).start();

        return result;
    }
}
