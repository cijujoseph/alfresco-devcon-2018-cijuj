package com.activiti.extension.bean;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component("customRestClient")
public class CustomRestClient {
	
	protected static final Logger logger = LoggerFactory.getLogger(CustomRestClient.class);

	@Autowired
	RestTemplate restTemplate;
	
	@Autowired
	protected ObjectMapper objectMapper;

	public static final String API_BASE_URL = "http://0.0.0.0:8088/api/datamodels/";
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		RestTemplate template = builder.build();
		return template;
	}
	

	public JsonNode getEntity(String entityName, String keyName, String keyValue) throws JsonProcessingException {

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(API_BASE_URL + entityName)
				.queryParam(keyName, keyValue);
		
		return restTemplate.getForObject(uriBuilder.build().toUri(), JsonNode.class);

	}

	public JsonNode createEntity(String entityName, Map<String, Object> requestBody) throws JsonProcessingException {

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(API_BASE_URL + entityName);
		HttpEntity<String> request = new HttpEntity<String>(objectMapper.writeValueAsString(requestBody));

		return restTemplate.postForObject(uriBuilder.build().toUri(), request, JsonNode.class);
	}

	public void updateEntity(String entityName, Map<String, Object> requestBody, String idValue)
			throws JsonProcessingException {

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(API_BASE_URL + entityName);
		HttpEntity<String> request = new HttpEntity<String>(objectMapper.writeValueAsString(requestBody));

		restTemplate.put(uriBuilder.build().toUri(), request);
	}
}