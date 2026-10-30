package com.serviceplus.formDesigner.formconfig.attrtypemaster.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.FieldJSONMaster;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.repository.IFieldJSONMasterRepository;
import com.serviceplus.formDesigner.util.ApplicationConstants;
import com.serviceplus.formDesigner.util.MessageConstants;

@Service
public class FieldJSONMasterService {

	@Autowired
	private IFieldJSONMasterRepository fieldJSONMasterRepository;
	
	public ResponseEntity<?> getRecordByTypeId(Integer typeId,String tenantId) {
		try {
			if(typeId.equals(ApplicationConstants.DEFAUL_TTYPE_ID)) {
				List<FieldJSONMaster> mastersList = new ArrayList<FieldJSONMaster>();
				mastersList = fieldJSONMasterRepository.findByTenantId(tenantId);
				if(mastersList.size() > 0) {
					List<Object> responseBody = new ArrayList<Object>();
					for(FieldJSONMaster master :mastersList) {
						responseBody.add(master.getJson());
					}
					return ResponseEntity.ok().body(responseBody);
				}else {
					throw new SPRuntimeError(MessageConstants.INVALID_TENANTID.toString(), HttpStatus.BAD_REQUEST);
				}
			}else {
				FieldJSONMaster fieldJSONMaster=fieldJSONMasterRepository.findByTypeIdAndTenantId(typeId,tenantId);
				if(fieldJSONMaster != null) {			
					return ResponseEntity.ok().body(fieldJSONMaster.getJson());
				}else {
					throw new SPRuntimeError(MessageConstants.INVALID_DATA.toString(), HttpStatus.BAD_REQUEST);
				}
			}
			
		}catch (Exception ex) {
			
			if(ex instanceof SPRuntimeError) {
				throw ex;
			}
			else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	/*
	 * public ResponseEntity<?> updateFieldTypeMJson(Integer typeId) { int i = 0;
	 * try { ObjectMapper objectMapper = new ObjectMapper(); List<FieldJSONMaster>
	 * list = fieldJSONMasterRepository.findByTypeId(typeId); for (FieldJSONMaster
	 * records : list) { Map<String, Object> jsonMap = objectMapper.convertValue(
	 * records.getJson(), new TypeReference<LinkedHashMap<String, Object>>() {} );
	 * applySortOrderIfPresent(jsonMap, "displaySettings");
	 * applySortOrderIfPresent(jsonMap, "validationSettings");
	 * applySortOrderIfPresent(jsonMap, "styleSettings");
	 * applySortOrderIfPresent(jsonMap, "dataSettings");
	 * 
	 * jsonMap.remove("customJs"); if (jsonMap.containsKey("apiSettings")) {
	 * Map<String, Object> apiSettings = new LinkedHashMap<>();
	 * apiSettings.put("actionCreators", new ArrayList<>());
	 * jsonMap.put("apiSettings", apiSettings); } records.setJson(jsonMap);
	 * fieldJSONMasterRepository.save(records); ++i; } return
	 * ResponseEntity.ok("Update ::"+i); }catch (Exception e) { return
	 * ResponseEntity.ok("Not Update :: "+i); } }
	 * 
	 * public void applySortOrderIfPresent(Map<String, Object> jsonMap, String
	 * section) { if (!jsonMap.containsKey(section)) { return; } Object sectionObj =
	 * jsonMap.get(section); if (!(sectionObj instanceof Map)) { return; }
	 * 
	 * @SuppressWarnings("unchecked") Map<String, Object> sectionMap = (Map<String,
	 * Object>) sectionObj; int sortOrder = 1; for (Map.Entry<String, Object> entry
	 * : sectionMap.entrySet()) {
	 * 
	 * Object childObj = entry.getValue(); if (!(childObj instanceof Map)) {
	 * continue; }
	 * 
	 * @SuppressWarnings("unchecked") Map<String, Object> childMap = (Map<String,
	 * Object>) childObj;
	 * 
	 * childMap.put("sortOrder", sortOrder++); } }
	 */
}
