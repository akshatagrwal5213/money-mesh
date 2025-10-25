package com.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bank.dto.PreferencesRequest;
import com.bank.model.UserPreferences;
import com.bank.service.PreferencesService;

@RestController
@RequestMapping("/api/preferences")
@CrossOrigin(origins = "*")
public class PreferencesController {
    
    @Autowired
    private PreferencesService preferencesService;
    
    @GetMapping
    public ResponseEntity<UserPreferences> getPreferences(Authentication authentication) {
        String username = authentication.getName();
        UserPreferences preferences = preferencesService.getPreferences(username);
        return ResponseEntity.ok(preferences);
    }
    
    @PutMapping
    public ResponseEntity<UserPreferences> updatePreferences(
            @RequestBody PreferencesRequest request,
            Authentication authentication) {
        String username = authentication.getName();
        UserPreferences preferences = preferencesService.updatePreferences(username, request);
        return ResponseEntity.ok(preferences);
    }
    
    @PostMapping("/reset")
    public ResponseEntity<Void> resetToDefaults(Authentication authentication) {
        String username = authentication.getName();
        preferencesService.resetToDefaults(username);
        return ResponseEntity.ok().build();
    }
}
