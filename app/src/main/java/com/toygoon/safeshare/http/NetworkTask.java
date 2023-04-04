package com.toygoon.safeshare.http;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetworkTask implements Supplier<HashMap<String, String>> {
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
        this.isGet = method.equalsIgnoreCase("GET");
    }

    @Override
    public HashMap<String, String> get() {
        // HttpRequest 결과를 저장할 HashMap
        HashMap<String, String> result = new HashMap<>();

        try {
            if (param != null) {
                // POST 데이터를 전달할 JSON 생성
                for (String k : this.param.keySet())
                    this.json.put(k, this.param.get(k));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // Request를 위한 builder
        Request.Builder builder = new Request.Builder();
        // url 지정
        builder.url(this.url);

        // Request type 지정
        if (this.isGet)
            builder.get();
        else
            builder.post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString()));

        // Request를 build
        Request request = builder.build();

        try {
            // 결과를 받아올 Response 및 요청 실행
            Response response = client.newCall(request).execute();
            // Response body
            String body = response.body().string();
            // JSON 결과를 저장하기 위한 map
            Map<String, Object> map = new ObjectMapper().readValue(body, HashMap.class);

            // HashMap으로 저장
            for (String m : map.keySet())
                result.put(m, Objects.requireNonNull(map.get(m)).toString());

            // Response code 추가
            result.put("response_code", String.valueOf(response.code()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return result;
    }
}
