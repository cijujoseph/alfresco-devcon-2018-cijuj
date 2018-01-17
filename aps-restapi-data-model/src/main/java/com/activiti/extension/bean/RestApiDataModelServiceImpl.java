package com.activiti.extension.bean;

import com.activiti.api.datamodel.AlfrescoCustomDataModelService;
import com.activiti.model.editor.datamodel.DataModelDefinitionRepresentation;
import com.activiti.model.editor.datamodel.DataModelEntityRepresentation;
import com.activiti.runtime.activiti.bean.datamodel.AttributeMappingWrapper;
import com.activiti.variable.VariableEntityWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class RestApiDataModelServiceImpl implements AlfrescoCustomDataModelService {

	private static Logger logger = LoggerFactory.getLogger(RestApiDataModelServiceImpl.class);

	@Autowired
	protected ObjectMapper objectMapper;

	@Autowired
	CustomRestClient customRestClient;

	public static final String ID_KEY_NAME = "Id";

	@Override
	public ObjectNode getMappedValue(DataModelEntityRepresentation entityDefinition, String fieldName,
			Object fieldValue) {

		try {
			JsonNode responseNode;
			if (fieldName.equals(ID_KEY_NAME)) {
				responseNode = customRestClient.getEntityDetails(entityDefinition.getName(), fieldName,
						(String) fieldValue);
				return (ObjectNode) objectMapper.readTree(objectMapper.writeValueAsString(responseNode));
			} else {
				responseNode = customRestClient.getEntityViaQueryParams(entityDefinition.getName(), fieldName,
						(String) fieldValue);
				if (responseNode.size() > 0) {
					return (ObjectNode) objectMapper.readTree(objectMapper.writeValueAsString(responseNode.get(0)));
				} else {
					return null;
				}
			}

		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public VariableEntityWrapper getVariableEntity(String keyValue, String variableName, String processDefinitionId,
			DataModelEntityRepresentation entityDefinition) {
		
		if (keyValue != null) {
			try {

				JsonNode responseNode = customRestClient.getEntityDetails(entityDefinition.getName(), ID_KEY_NAME,
						(String) keyValue);
				ObjectNode dataNode = (ObjectNode) objectMapper.readTree(objectMapper.writeValueAsString(responseNode));

				VariableEntityWrapper variableEntityWrapper = new VariableEntityWrapper(variableName,
						processDefinitionId, entityDefinition);
				variableEntityWrapper.setEntity(dataNode);
				variableEntityWrapper.setKey(keyValue);
				return variableEntityWrapper;
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	@Override
	public String storeEntity(List<AttributeMappingWrapper> attributeDefinitionsAndValues,
			DataModelEntityRepresentation entityDefinition, DataModelDefinitionRepresentation dataModel) {

		String idValue = null;
		// Set up a map of all the column names and values
		Map<String, Object> parameters = new HashMap<String, Object>();
		for (AttributeMappingWrapper attributeMappingWrapper : attributeDefinitionsAndValues) {
			logger.info(attributeMappingWrapper.getAttribute().getName());
			logger.info(attributeMappingWrapper.getValue().toString());

			if (attributeMappingWrapper.getAttribute().getName().equals(ID_KEY_NAME)
					&& attributeMappingWrapper.getValue() != null) {
				idValue = (String) attributeMappingWrapper.getValue();
			}

			parameters.put(attributeMappingWrapper.getAttribute().getName(), attributeMappingWrapper.getValue());

		}

		try {
			if (idValue != null) {
				customRestClient.updateEntity(entityDefinition.getName(), parameters, idValue);
				return idValue;
			} else {
				JsonNode responseNode = customRestClient.createEntity(entityDefinition.getName(), parameters);
				return responseNode.get(ID_KEY_NAME).textValue();
			}
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}