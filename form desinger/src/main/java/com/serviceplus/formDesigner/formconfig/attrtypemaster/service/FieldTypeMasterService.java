package com.serviceplus.formDesigner.formconfig.attrtypemaster.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.serviceplus.formDesigner.exception.SPRuntimeError;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.FieldTypeMaster;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.RegexPatternMaster;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.repository.IFieldTypeMaster;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.repository.IRegexPatternMasterRepository;
import com.serviceplus.formDesigner.util.MessageConstants;

@Service
public class FieldTypeMasterService {
	@Autowired
	private IFieldTypeMaster fieldTypeMasterRepo;
	
	@Autowired
	private IRegexPatternMasterRepository regexPatternMasterRepository;

	public ResponseEntity<?> getFieldTypeMaster(String tenantId) {
		try {
			FieldTypeMaster typeMastersList = fieldTypeMasterRepo.findByTenantId(tenantId);
			if (typeMastersList == null) {
				throw new SPRuntimeError(MessageConstants.INVALID_TENANTID.toString(), HttpStatus.BAD_REQUEST);
			}
			return ResponseEntity.ok().body(typeMastersList.getGroupsJsonData());

		} catch (Exception ex) {
			if(ex instanceof SPRuntimeError) {
				throw ex;
			}
			else {
				ex.printStackTrace();
				throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
	}

	public ResponseEntity<String> getRegexMaster() {
		try {
			List<RegexPatternMaster> list = regexPatternMasterRepository.findAll();

			String body = list.stream()
					.map(r -> "    \"" + r.getValidationType() + "\": \"" + r.getRegexPattern() + "\"")
					.collect(Collectors.joining(",\n", "{\n", "\n}"));

			return ResponseEntity.ok().contentType(MediaType.TEXT_PLAIN).body(body);
		} catch (Exception ex) {
			ex.printStackTrace();
			throw new SPRuntimeError("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

}
