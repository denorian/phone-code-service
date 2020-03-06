package com.brovko.phonecodeservice.controller;

import com.brovko.phonecodeservice.domain.Code;
import com.brovko.phonecodeservice.domain.View;
import com.brovko.phonecodeservice.service.CodeService;
import com.fasterxml.jackson.annotation.JsonView;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/rest/code")
public class CodeController {
	
	@Autowired
	private CodeService codeService;
	
	@GetMapping
	@JsonView(View.nameCode.class)
	public ResponseEntity<Object> getPhoneCode(@RequestParam String country) throws JSONException {
		
		if (country == null || country.length() != 2 || !country.equals(country.toUpperCase())) {
			JSONObject json = new JSONObject();
			json.put("error", "the country code must be two characters and be uppercase");
			return ResponseEntity
					.status(HttpStatus.BAD_REQUEST)
					.body(json.toString());
		}
		
		Code code = codeService.getCodeByCountryCode(country);
		
		if (code == null) {
			JSONObject json = new JSONObject();
			json.put("error", "country code " + country + " was not found");
			return ResponseEntity
					.status(HttpStatus.NOT_FOUND)
					.body(json.toString());
		}
		
		return ResponseEntity.ok(code);
	}
	
	@GetMapping("/refresh")
	public ResponseEntity<Object> refresh() throws JSONException {
		codeService.refresh();
		JSONObject json = new JSONObject();
		json.put("status", "cache was refreshed");
		return ResponseEntity.ok(json.toString());
	}
}
