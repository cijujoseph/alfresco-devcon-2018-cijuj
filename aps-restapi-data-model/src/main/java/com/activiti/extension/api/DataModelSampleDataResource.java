package com.activiti.extension.api;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.activiti.model.editor.datamodel.DataModelAttributeRepresentation;
import com.activiti.model.editor.datamodel.DataModelDefinitionRepresentation;
import com.activiti.model.editor.datamodel.DataModelEntityRepresentation;
import com.activiti.service.editor.AlfrescoDataModelService;
import com.codahale.metrics.annotation.Timed;

@RestController
public class DataModelSampleDataResource {

	protected static final Logger logger = LoggerFactory.getLogger(DataModelSampleDataResource.class);

	@Autowired
	protected AlfrescoDataModelService alfrescoDataModelService;

	@RequestMapping(value = "/enterprise/custom-api/datamodels/sample-data/{modelId}/entities/{entityName}", method = RequestMethod.GET, produces = "application/json")
	@Timed
	public Map<String, Object> lookupDataModel(@PathVariable Long modelId, @PathVariable String entityName) {
		try {
			
			DataModelDefinitionRepresentation dm = alfrescoDataModelService.getDataModel(modelId)
					.getDataModelDefinition();
			DataModelEntityRepresentation entityDefinition = dm.findEntity(entityName);
			Map<String, Object> map = new HashMap<String, Object>();
			for (DataModelAttributeRepresentation attribute : entityDefinition.getAttributes()) {
				if(attribute.getType().equals("number")){
					map.put(attribute.getName(), 100);
				} else {
					map.put(attribute.getName(), attribute.getName()+" - sample");
				}
			}
			return map;

		} catch (Exception e) {
			logger.error("Error while doing datamodel lookup ", e);
			return null;
		}
	}

}