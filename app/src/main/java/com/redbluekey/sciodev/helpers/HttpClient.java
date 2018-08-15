package com.redbluekey.sciodev.helpers;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class HttpClient {

    public static RestTemplate client;

    static {
        client = new RestTemplate();
        client.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
    }
}
