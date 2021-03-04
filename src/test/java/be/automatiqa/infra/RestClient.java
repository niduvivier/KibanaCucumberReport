package be.automatiqa.infra;

import be.automatiqa.plugin.CucumberReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

public class RestClient {

    private RestTemplate restTemplate;

    private String elkUri = "http://localhost:9200";

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


}
