package com.application.amrs.exhibition;
/*
 * #WebClient
 * 
 * 비동기 방식
 * 
 * block()으로 동기 처리했지만 응답이 html이었을 가능성
 * 
 * 큰 데이터 처리에 약함
 * 
 * #RestTemplate
 * 
 * 동기 방식, 때문에 응답을 더 빠르게 받아 json을 안정적으로 처리
 * 
 * 큰 데이터를 안정적으로 처리함
 * 
 * */

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

@Service
public class ExhibitionService {
	
	private final RestTemplate restTemplate;
	private final RedisTemplate<String, Object> redisTemplate;
	private final String apiUrl = "http://api.kcisa.kr/openapi/API_CCA_145/request?serviceKey=948b06fb-3f09-4dcd-b4a7-c360995ff0ee";
	
	public ExhibitionService(RedisTemplate<String, Object> redisTemplate) {
		this.restTemplate = new RestTemplate();
		this.redisTemplate = redisTemplate;
	}
	
	/** 전시 상태 ENUM */
    private enum ExhibitionStatus { ERROR, ACTIVE, EXPIRED }
    
	// 전시 제목과 이미지 URL을 담기 위한 내부 클래스 정의
    public static class ExhibitionItem {
    	
    	private String localId, title, imageUrl, description, cntcInsttNm, period, eventPeriod, charge, spatialCoverage, contactPoint;
    	private boolean isExpired;

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


		public String getLocalId() {
			return localId;
		}
		
		public boolean getIsExpired() {
			return isExpired;
		}

        public ExhibitionItem(String localId, String title, String imageUrl, boolean isExpired) {
        	this.localId 	= localId;
            this.title   	= title;
            this.imageUrl 	= imageUrl;
            this.isExpired 	= isExpired;
        }
        
        public ExhibitionItem(String localId, String title, String imageUrl, String description, String cntcInsttNm, String period, String eventPeriod, String charge, String spatialCoverage, String contactPoint, boolean isExpired) {
        	this.localId 			= localId;
        	this.title 				= title;
        	this.imageUrl 			= imageUrl;
        	this.description 		= description;
        	this.cntcInsttNm 		= cntcInsttNm;
        	this.period 			= period;
        	this.eventPeriod 		= eventPeriod;   
        	this.charge 			= charge;
        	this.spatialCoverage 	= spatialCoverage;
        	this.contactPoint 		= contactPoint;
        	this.isExpired 			= isExpired;
    	}
        
    }

    /** API 데이터 호출 및 JSON 변환 공통 메서드 */
    // api 응답 캐싱하여 속도 향상
    @Cacheable(value = "exhibitionCache", key = "'exhibitions'", unless = "#result.isEmpty()")
    public List<ExhibitionItem> fetchExhibitionData() {
    	
//    	String cachedData = (String) redisTemplate.opsForValue().get("exhibitionCache");
//    	
//    	if(cachedData != null) {
//    		System.out.println("redis에서 데이터 로드");
//    		JSONArray jsonItems = new JSONArray(cachedData);
//            return parseJsonToExhibitions(jsonItems);
//    	}
    	System.out.println("Redis 캐시 없음 → API 호출 진행");
    	
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
            JSONObject jsonObject = new JSONObject(response.getBody());
            JSONArray jsonItems = jsonObject.getJSONObject("response")
            								.getJSONObject("body")
            								.getJSONObject("items")
            								.getJSONArray("item");

            //List<ExhibitionItem> exhibitionList = parseJsonToExhibitions(jsonItems);
            
//            String jsonString = jsonItems.toString();
//            redisTemplate.opsForValue().set("exhibitionCache", jsonString, Duration.ofMinutes(10));
//            System.out.println("redis 캐시 저장 완료");
//
//            return exhibitionList;
            return parseJsonToExhibitions(jsonItems);
            
        } catch (HttpStatusCodeException e) {
        	 System.err.println("🚨 API 호출 중 오류 발생: " + e.getMessage());
             return Collections.emptyList();
        }
    }
    
    /** 🔹 JSON 데이터를 ExhibitionItem 리스트로 변환 */
    private List<ExhibitionItem> parseJsonToExhibitions(JSONArray jsonItems) {
        List<ExhibitionItem> exhibitionList = new ArrayList<>();
        
        for (int i = 0; i < jsonItems.length(); i++) {
            JSONObject item = jsonItems.getJSONObject(i);
            String localId = item.optString("LOCAL_ID", "").trim();
            if (localId.isEmpty()) continue;

            String title = item.optString("TITLE", "제목 없음");
            String imageUrl = item.optString("IMAGE_OBJECT", "/bootstrap/images/img-grid-3.jpg");
            String description = item.optString("DESCRIPTION", "");
            String period = item.optString("PERIOD", " ");
            String eventPeriod = item.optString("EVENT_PERIOD", "");
            String cntcInsttNm = item.optString("CNTCINSTTNM", "");
            String charge = item.optString("CHARGE", "");
            String spatialCoverage = item.optString("SPATIAL_COVERAGE", "");
            String contactPoint= item.optString("CONTACT_POINT", "");
            ExhibitionStatus status = determineExhibitionStatus(period);
            boolean isExpired = (status == ExhibitionStatus.EXPIRED);

            exhibitionList.add(new ExhibitionItem(localId, title, imageUrl, description, period, eventPeriod, cntcInsttNm, charge, spatialCoverage, contactPoint, isExpired));
        }
        return exhibitionList;
    }

    
    // 캐시 삭제 (데이터 갱신 시 사용)
    @CacheEvict(value = "exhibitionCache", key = "'exhibitions'")
    public void clearExhibitionCache() {
        System.out.println("🚀 캐시 삭제됨: exhibitionCache");
    }

    // 캐시를 강제로 갱신 
    @CachePut(value = "exhibitionCache", key = "'exhibitions'")
    public List<ExhibitionItem> refreshExhibitionData() {
        return fetchExhibitionData();
    }
    
    /** 전시 데이터 필터링 및 리스트 반환 */
    private List<ExhibitionItem> getFilteredExhibitions(ExhibitionStatus filter) {
        List<ExhibitionItem> exhibitions = fetchExhibitionData(); // fetchExhibitionData()는 이미 List<ExhibitionItem>을 반환
        List<ExhibitionItem> filteredList = new ArrayList<>();

        for (ExhibitionItem item : exhibitions) {
            ExhibitionStatus status = item.getIsExpired() ? ExhibitionStatus.EXPIRED : ExhibitionStatus.ACTIVE;
            if (filter == ExhibitionStatus.ERROR || status == filter) {
                filteredList.add(item);
            }
        }
        return filteredList;
    }


    /** 모든 전시 조회 (진행 중 + 종료) */
    public List<ExhibitionItem> getExhibitionItems() {
        return getFilteredExhibitions(ExhibitionStatus.ERROR);
    }

    /** 진행 중인 전시 조회 */
    public List<ExhibitionItem> getShowingExhibitionItems() {
        return getFilteredExhibitions(ExhibitionStatus.ACTIVE);
    }

    /** 종료된 전시 조회 */
    public List<ExhibitionItem> getShowedExhibitionItems() {
        return getFilteredExhibitions(ExhibitionStatus.EXPIRED);
    }

    /** 랜덤 전시 리스트 */
    public List<ExhibitionItem> getRandomExhibitions(int count) {
        List<ExhibitionItem> showingItems = getShowingExhibitionItems();
        if (showingItems.isEmpty()) return Collections.emptyList();
        Collections.shuffle(showingItems);
        return showingItems.subList(0, Math.min(count, showingItems.size()));
    }

    /** 특정 전시 상세 조회 */
    public ExhibitionItem exhibitionDetail(String id) {
        List<ExhibitionItem> exhibitions = fetchExhibitionData(); // API에서 받아온 전시 리스트

        for (ExhibitionItem item : exhibitions) {
            if (item.getLocalId().equals(id)) {
                return item; // ID가 일치하는 전시 반환
            }
        }
        return null; // 찾지 못한 경우 null 반환
    }

    /** 전시 종료 여부 판단 */
    private ExhibitionStatus determineExhibitionStatus(String period) {
        if (period == null || period.trim().isEmpty() || period.equals("~") || period.matches("^\\d+$")) {
            System.out.println("잘못된 기간 데이터: " + period);
            return ExhibitionStatus.ERROR;
        }

        period = period.replaceAll("\\s+", "").trim();
        String[] periodParts;
        
        if(period.contains("~")) {
        	periodParts = period.split("~");
        } else if(period.matches("\\d{4}-\\d{2}-\\d{2}-\\d{4}-\\d{2}-\\d{2}")) {
        	periodParts = period.split("-");
            periodParts = new String[] {periodParts[0] + "-" + periodParts[1] + "-" + periodParts[2], 
                                        periodParts[3] + "-" + periodParts[4] + "-" + periodParts[5]};
        } else {
            return ExhibitionStatus.ERROR;
        }

        if (periodParts.length < 2 || periodParts[1].trim().isEmpty()) {
            return ExhibitionStatus.ERROR;
        }

        String endDateStr = periodParts[1].trim();
        try {
            LocalDate endDate = parseDate(endDateStr);
            return endDate.isBefore(LocalDate.now()) ? ExhibitionStatus.EXPIRED : ExhibitionStatus.ACTIVE;
        } catch (DateTimeParseException e) {
            return ExhibitionStatus.ERROR;
        }
    }

    /** 날짜 형식 변환 */
    private LocalDate parseDate(String dateStr) {
        dateStr = dateStr.replaceAll("00:00:00", "").trim(); // 2011-10-0800:00:00 같은 형식 처리

        if (dateStr.matches("\\d{8}")) {
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        }
        if (dateStr.matches("\\d{4}-\\d{2}-\\d{2}")) { // YYYY-MM-DD
            return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
        throw new DateTimeParseException("지원하지 않는 날짜 형식", dateStr, 0);
    }

}
