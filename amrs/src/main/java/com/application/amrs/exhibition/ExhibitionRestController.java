package com.application.amrs.exhibition;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/exhibition")
public class ExhibitionRestController {

    @GetMapping("/exhibitionList")
    public ResponseEntity<String> getDataFromExternalApi(
            @RequestParam("numOfRows") int numOfRows, 
            @RequestParam("pageNo") int pageNo) {
        
        String apiUrl = "http://api.kcisa.kr/openapi/API_CCA_145/request?serviceKey=948b06fb-3f09-4dcd-b4a7-c360995ff0ee&numOfRows=" + numOfRows + "&pageNo=" + pageNo;

        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            return response;

        } catch (HttpStatusCodeException e) {
            System.err.println("API 호출 중 오류 발생: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body("외부 API 호출 중 오류 발생");

        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            return ResponseEntity.status(500).body("서버 내부 오류 발생: " + e.getMessage());
        }
    }
    
    
}