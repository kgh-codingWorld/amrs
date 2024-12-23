package com.application.amrs.exhibition;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import ch.qos.logback.core.pattern.SpacePadder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class ExhibitionService {

    // 전시 제목과 이미지 URL을 담기 위한 내부 클래스 정의
    public static class ExhibitionItem {
    	private String localId;
        private String title;
        private String imageUrl;
        private String description;
        private String cntcInsttNm;
        private String period;
        private String eventPeriod;
        private String charge;
        private String spatialCoverage;
        private String contactPoint;

        public ExhibitionItem(String localId, String title, String imageUrl) {
        	this.localId = localId;
            this.title = title;
            this.imageUrl = imageUrl;
        }
        
        public ExhibitionItem(String localId, String title, String imageUrl, String description, String cntcInsttNm, String period, String eventPeriod, String charge, String spatialCoverage, String contactPoint) {
        	this.localId = localId;
        	this.title = title;
        	this.imageUrl = imageUrl;
        	this.description = description;
        	this.cntcInsttNm = cntcInsttNm;
        	this.period = period;
        	this.eventPeriod = eventPeriod;   
        	this.charge = charge;
        	this.spatialCoverage = spatialCoverage;
        	this.contactPoint = contactPoint;
        	
    	}
        
        public String getLocalId() {
        	return localId;
        }

        public String getTitle() {
            return title;
        }

        public String getImageUrl() {
            return imageUrl;
        }
        
        public String getDescription() {
        	return description;
        }
        
        public String getCntcInsttNm() {
        	return cntcInsttNm;
        }
        
        public String getPeriod() {
        	return period;
        }
        
        public String getEventPeriod() {
        	return eventPeriod;
        }
        
        public String getCharge() {
        	return charge;
        }
        
        public String getSpatialCoverage() {
        	return spatialCoverage;
        }
        
        public String getContactPoint() {
        	return contactPoint;
        }
    }

    public List<ExhibitionItem> getExhibitionItems() {
        // String apiUrl = "http://api.kcisa.kr/openapi/API_CCA_149/request?serviceKey=493f5e47-3544-4051-a637-8d79212890ed";
        String apiUrl = "http://api.kcisa.kr/openapi/API_CCA_145/request?serviceKey=948b06fb-3f09-4dcd-b4a7-c360995ff0ee";
        
        // 중복 제거를 위한 Map, key는 전시 ID
        Map<String, ExhibitionItem> uniqueItemsMap = new HashMap<>();
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);

            // JSON 파싱 및 제목과 이미지 URL 추출
            JSONObject jsonObject = new JSONObject(response.getBody());
            JSONArray jsonItems = jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject item = jsonItems.getJSONObject(i);
                String localId = item.getString("LOCAL_ID");
                String title = item.getString("TITLE");
                String imageUrl = item.optString("IMAGE_OBJECT", "/bootstrap/images/img-grid-3.jpg");  // 이미지가 없는 경우 기본값 설정

                // 중복 확인: id가 존재하지 않을 때만 추가
                uniqueItemsMap.putIfAbsent(localId, new ExhibitionItem(localId, title, imageUrl));
            }

            // Map의 values를 ArrayList로 변환하여 반환
            return new ArrayList<>(uniqueItemsMap.values());

        } catch (HttpStatusCodeException e) {
            System.err.println("API 호출 중 오류 발생: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return new ArrayList<>(uniqueItemsMap.values()); // 오류 발생 시 빈 리스트 반환
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            return new ArrayList<>(uniqueItemsMap.values());
        }
    }

    
    public ExhibitionItem exhibitionDetail(String id) {
        String apiUrl = "http://api.kcisa.kr/openapi/API_CCA_145/request?serviceKey=948b06fb-3f09-4dcd-b4a7-c360995ff0ee";
        
        try {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
    		System.out.println(" ###### exhibitionDetail : id : " + id);

            // JSON 파싱 및 제목과 이미지 URL 추출
            JSONObject jsonObject = new JSONObject(response.getBody());
            JSONArray jsonItems = jsonObject.getJSONObject("response").getJSONObject("body").getJSONObject("items").getJSONArray("item");

            for (int i = 0; i < jsonItems.length(); i++) {
                JSONObject item = jsonItems.getJSONObject(i);
                String localId = item.getString("LOCAL_ID");
        		System.out.println(" ###### exhibitionDetail : localId : " + localId);

                if(localId.equals(id)) {
                    String title = item.getString("TITLE");
                    String imageUrl = item.optString("IMAGE_OBJECT", "/bootstrap/images/img-grid-3.jpg");  // 이미지가 없는 경우 기본값 설정
                    String description = item.optString("DESCRIPTION", "설명 없음");
                    String cntcInsttNm = item.optString("CNTC_INSTT_NM", " ");
                    String period = item.optString("PERIOD", " ");
                    String eventPeriod = item.optString("EVENT_PERIOD", " ");
                    String charge = item.optString("CHARGE", " ");
                    String spatialCoverage = item.optString("SPATIAL_COVERAGE", " ");
                    String contactPoint = item.optString("CONTACT_POINT", " ");
                    // 단일 ExhibitionItem 반환
                    return new ExhibitionItem(localId, title, imageUrl, description, cntcInsttNm, period, eventPeriod, charge, spatialCoverage, contactPoint);
                }
            }
            
            // ID에 해당하는 전시가 없을 경우 null 반환
            return null;

        } catch (HttpStatusCodeException e) {
            System.err.println("API 호출 중 오류 발생: " + e.getStatusCode() + " - " + e.getResponseBodyAsString());
            return null; // 오류 발생 시 null 반환
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            return null;
        }
    }

}
