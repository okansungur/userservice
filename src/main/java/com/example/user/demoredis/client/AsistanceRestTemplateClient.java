package com.example.user.demoredis.client;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class AsistanceRestTemplateClient {

    @Autowired
    RestTemplate restTemplate;

    public Object getAsistant(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity(headers);
        ResponseEntity<Object> response = null;
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://asistanceservice/getall");
        try {
            response = restTemplate.exchange(builder.toUriString(),
                    HttpMethod.GET,
                    entity, Object.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response.getBody();
    }

}
