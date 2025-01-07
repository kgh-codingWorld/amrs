package com.application.amrs.community;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/community")
public class CommunityRestController {
    
    @Autowired
    private CommunityService communityService;
    
    @PostMapping("/removeCommunity/{communityId}")
    public ResponseEntity<String> removeCommunity(@PathVariable("communityId") int communityId) {
        try {
        	communityService.removeCommunity(communityId);
            return ResponseEntity.status(HttpStatus.OK).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
