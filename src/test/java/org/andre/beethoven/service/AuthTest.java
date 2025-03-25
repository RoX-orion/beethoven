package org.andre.beethoven.service;

import jakarta.annotation.Resource;
import okhttp3.OkHttpClient;
import org.beethoven.BeethovenApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Copyright (c) 2024 Andre Lina. All rights reserved.
 *
 * @description:
 * @author: Andre Lina
 * @date: 2024-12-15
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = BeethovenApplication.class)
public class AuthTest {

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private OkHttpClient httpClient;

    @Test
    public void githubLogin() {
        /*
        {"clientId":"Iv23liwRggbRlFoMX4L9","redirectUri":"http://localhost:12345","state":"516708"}
         */
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("client_id", "Iv23liwRggbRlFoMX4L9");
        requestBody.put("client_secret", "5dcba8cc1d8e218006bd9177c166170ea3d6e95c");
        requestBody.put("code", "635768b9bec407f2840c");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("User-Agent", "PostmanRuntime-ApipostRuntime/1.1.0");
        httpHeaders.add("Host", "github.com");
        HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, httpHeaders);
        ParameterizedTypeReference<HashMap<String, String>> typeRef = new ParameterizedTypeReference<>() {};
        ResponseEntity<Object> response = restTemplate.exchange("https://github.com/login/oauth/access_token", HttpMethod.POST, request, Object.class);
        System.out.println(response);

//        RequestBody formBody = new FormBody.Builder()
//                .add("client_id", "Iv23liwRggbRlFoMX4L9")
//                .add("client_secret", "5dcba8cc1d8e218006bd9177c166170ea3d6e95c")
//                .add("code", "cad38dc87da6472eac74")
//                .build();
//        Request request = new Request.Builder()
//                .url("https://github.com/login/oauth/access_token")
////                .headers(headers)
//                .post(formBody)
//                .build();
//        try (Response response = httpClient.newCall(request).execute()) {
//            ResponseBody body = response.body();
//            System.out.println(body);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }
}
