package com.application.amrs.exhibition;

import java.util.List;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.application.amrs.exhibition.ExhibitionService.ExhibitionItem;

@RestController
@RequestMapping("/api/exhibitions")
public class ExhibitionRestController {

	@Autowired
	private ExhibitionService exhibitionService;
	
    @GetMapping("/cached")
    public List<ExhibitionItem> getCachedExhibitions() {
        return exhibitionService.fetchExhibitionData();
    }

    @GetMapping("/refresh")
    public List<ExhibitionItem> refreshCache() {
        return exhibitionService.refreshExhibitionData();
    }

    @GetMapping("/clear-cache")
    public String clearCache() {
        exhibitionService.clearExhibitionCache();
        return "캐시가 삭제되었습니다!";
    }
}
