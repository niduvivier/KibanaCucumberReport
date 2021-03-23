package be.automatiqa.infra;

import be.automatiqa.plugin.CucumberReport;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.LinkedHashMap;

public class RestClient {

    private final RestTemplate restTemplate;

    private final String elkUri = "http://localhost:9200";

    public RestClient(){
        restTemplate = new RestTemplate();
    }

    private <RESULT> RESULT doPost(String url, Object body, Class<RESULT> resultClazz){
        return exchange(restTemplate, url, HttpMethod.POST, createHttpEntity(body), resultClazz);
    }

    private <RESULT> RESULT exchange(RestTemplate restTemplate, String url, HttpMethod method, HttpEntity httpEntity, Class<RESULT> resultClazz){
        try{
            RESULT result = restTemplate.exchange(url, method, httpEntity, resultClazz).getBody();
            return result;
        } catch (HttpClientErrorException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(String.format("Failed to %s on url %s with request entity: \n%s\nReason: %s", method,
                    url, httpEntity.getBody(), e.getMessage()));
        }
    }

    private <T> HttpEntity<T> createHttpEntity(T body){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity(body, httpHeaders);
    }

    public void postReportToELK(CucumberReport cucumberReport){
        doPost(elkUri + "/cucumber-report/message", cucumberReport, Object.class);
    }


    public Double getLastExecutionId(){
        String jsonString = "{ \"aggs\": {\"maxId\": { \"max\": { \"field\": \"executionId\" } } } }";
        JSONParser parser = new JSONParser();
        JSONObject object = null;
        try {
            object = (JSONObject)parser.parse(jsonString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        JSONObject result = doPost(elkUri + "/cucumber-report/_search?size=0", object, JSONObject.class);
        @SuppressWarnings("unchecked")
        Double id = (Double)((LinkedHashMap<String, Object>)((LinkedHashMap<String, Object>)result.get("aggregations")).get("maxId")).get("value");
        return id;
    }


}
