package com.serviceplus.metadata.mvel.validator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.mvel2.MVEL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO;
import com.serviceplus.metadata.mvel.dto.MvelFunctionDTO.FunctionParameter;
import com.serviceplus.metadata.mvel.dto.MvelInvalidationMSGDTO;
import com.serviceplus.metadata.mvel.entity.MvelFunction;
import com.serviceplus.metadata.mvel.repository.IMvelFunctionRepository;
import com.serviceplus.metadata.validator.ValidationUtility;

@Component
public class MvelFunctionDefValidator {
	@Autowired
	private IMvelFunctionRepository mvelFunctionRepository;
	
	public MvelInvalidationMSGDTO validate(MvelFunctionDTO target) {
		MvelInvalidationMSGDTO invalidationMsg = null;
		try {
			Serializable compiled = MVEL.compileExpression(target.getFunctionBody());
			if (compiled == null) {
				if(Objects.isNull(invalidationMsg))
					invalidationMsg = new MvelInvalidationMSGDTO();
				invalidationMsg.setFunBodyMsg("Expression could not be compiled.");
			}
		} catch (Exception ex) {
			if(Objects.isNull(invalidationMsg))
				invalidationMsg = new MvelInvalidationMSGDTO();
			
			invalidationMsg.setFunBodyMsg("Compilation failed: " + ex.getMessage());
		}
		
		if (!ValidationUtility.hasValidValue(target.getFunctionName(),ValidationUtility.REG_EX_ALPHANUMERIC)) {
			if(Objects.isNull(invalidationMsg))
				invalidationMsg = new MvelInvalidationMSGDTO();
			
			invalidationMsg.setFunDefMsg("Please enter alphaNumeric values only");
		}
		MvelFunction dbMvelFunction = mvelFunctionRepository.findByServiceIdAndName(target.getServiceId().intValue(), target.getFunctionName());
		if(!Objects.isNull(dbMvelFunction) && (target.getId() == null || !dbMvelFunction.getId().equals(target.getId()))) {	
			if(Objects.isNull(invalidationMsg))
				invalidationMsg = new MvelInvalidationMSGDTO();
			
			invalidationMsg.setFunDefMsg("Function name already exists for the Service");
		}
		return invalidationMsg;
	}

}
