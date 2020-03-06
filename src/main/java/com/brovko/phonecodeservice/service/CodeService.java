package com.brovko.phonecodeservice.service;

import com.brovko.phonecodeservice.domain.Code;
import com.brovko.phonecodeservice.helper.SimpleHttpClient;
import com.brovko.phonecodeservice.repository.CodeRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

@Service
@CacheConfig(cacheNames={"codeService"})
public class CodeService {
	
	private static final String CONTRY_NAMES = "http://country.io/names.json";
	private static final String PHONE_CODES = "http://country.io/phone.json";
	
	@Autowired
	private CodeRepo codeRepo;
	
	@Cacheable
	public Code getCodeByCountryCode(String country) {
		return codeRepo.findByName(country);
	}
	
	@PostConstruct
	public void fillRepo() {
		Map<String, String> countryMap = getCountries(CONTRY_NAMES);
		Map<String, String> phoneCodeMap = getCountries(PHONE_CODES);
		HashSet<Code> codeSet = new HashSet();
		
		for (Map.Entry<String, String> entry : countryMap.entrySet()) {
			
			Code code = new Code();
			code.setName(entry.getKey());
			code.setCountry(entry.getValue());
			code.setCode(phoneCodeMap.get(entry.getKey()));
			
			codeSet.add(code);
		}
		
		codeRepo.saveAll(codeSet);
	}
	
	public Map<String, String> getCountries(String url) {
		Map<String, String> countryMap = new HashMap();
		
		try {
			String response = SimpleHttpClient.get(url);
			ObjectMapper objectMapper = new ObjectMapper();
			countryMap = objectMapper.readValue(response, new TypeReference<HashMap<String, String>>() {
			});
		} catch (Exception ex) {
			System.out.println("Exception");
		}
		
		return countryMap;
	}
	
	@CacheEvict(cacheNames = {"codeService"}, allEntries = true)
	public void refresh() {
		codeRepo.deleteAll();
		fillRepo();
	}
}
