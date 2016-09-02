package com.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by aldortuno on 02/09/2016.
 */
@Service
public class BuscarDomicilioService {

    private static final Logger log = LoggerFactory.getLogger(BuscarDomicilioService.class);

    public List<Map<String, Object>> buscarPorProvincia(String direccion, String provincia) {
        RestTemplate restTemplate = new RestTemplate();
        Map<String,Object> req = new HashMap<String,Object>();
                log.info(direccion + " - " + provincia);

        if (provincia != null) {
            req = (Map<String, Object>) restTemplate.getForObject("http://maps.googleapis.com/maps/api/geocode/json?address=" + direccion.replace(' ', '+') + "&components=administrative_area:" + provincia + "|country:AR", Object.class);
        } else {
            req = (Map<String, Object>) restTemplate.getForObject("http://maps.googleapis.com/maps/api/geocode/json?address=" + direccion.replace(' ', '+') + "&components=country:AR", Object.class);
        }

        String status = (String)req.get("status");

        List<Map<String, Object>> addresses = new ArrayList<Map<String, Object>>();

        if("OK".equals(status)) {
            List<Map<String,Object>> results = (List<Map<String, Object>>) req.get("results");
            for(Map<String, Object> result : results){
                addresses.add(result);
            }
        }
        return addresses;
    }

}
