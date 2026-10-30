package com.serviceplus.formDesigner.formconfig.attrtypemaster.service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.FieldJSONMaster;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.FieldTypeMaster;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.repository.IFieldJSONMasterRepository;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.repository.IFieldTypeMaster;
import com.serviceplus.formDesigner.master.document.ITenantSetupMasterRepository;
import com.serviceplus.formDesigner.master.document.TenantSetupMaster;
import com.serviceplus.formDesigner.master.enums.MasterType;

@Service
public class TenantInitializationService {

	@Value("${master.createdBy}")
	private String createdBy;

	@Autowired
	private ITenantSetupMasterRepository masterRepository;
	@Autowired
	private IFieldTypeMaster fieldTypeMasterRepo;
	@Autowired
	private IFieldJSONMasterRepository fieldJSONMasterRepo;

	private static final Logger LOGGER = LogManager.getLogger(TenantInitializationService.class);

	public void initializeTenant(String tenantId) {

		LOGGER.info("========================================================");
		LOGGER.info("Tenant initialization started. TenantId={}", tenantId);

		if(!fieldTypeMasterRepo.existsByTenantId(tenantId)) {
			copyFieldTypes(tenantId);
			copyFieldJson(tenantId);
			
			LOGGER.info("Tenant initialization completed successfully. TenantId={}", tenantId);
			LOGGER.info("========================================================");
		}else {
			LOGGER.info("Records already present for TenantId={}", tenantId);
		}

	}

	private void copyFieldTypes(String tenantId) {

		LOGGER.info("Initializing FieldTypeMaster for tenant {}.", tenantId);
		Optional<TenantSetupMaster> dbData = masterRepository.findByType(MasterType.FIELDTYPE.getValue());
		if (dbData.isEmpty()) {
			LOGGER.info("Master Data not present for FieldTypeMaster.");
			return;
		}

		FieldTypeMaster fieldTypeMaster = dbData.get().getRecords().getFieldTypeMaster();
		fieldTypeMaster.setCreatedBy(createdBy);
		fieldTypeMaster.setTenantId(tenantId);
		Date cDate = new Date();
		fieldTypeMaster.setCreatedon(cDate.toString());
		fieldTypeMasterRepo.save(fieldTypeMaster);

		LOGGER.info("FieldTypeMaster initialized successfully.");
		return;
	}

	private void copyFieldJson(String tenantId) {

		LOGGER.info("Initializing FieldJSONMaster for tenant {}.", tenantId);

		Optional<TenantSetupMaster> dbData = masterRepository.findByType(MasterType.FIELDJSON.getValue());
		if (dbData.isEmpty()) {
			LOGGER.info("Master Data not present for FieldTypeMaster.");
			return;
		}
		List<FieldJSONMaster> fieldJSONMasterList = dbData.get().getRecords().getFieldJSONMaster();
		for (FieldJSONMaster fieldJSONMaster : fieldJSONMasterList) {
			fieldJSONMaster.setCreatedBy(createdBy);
			fieldJSONMaster.setTenantId(tenantId);
			Date cDate = new Date();
			fieldJSONMaster.setCreatedon(cDate.toString());
		}
		fieldJSONMasterRepo.saveAll(fieldJSONMasterList);

		LOGGER.info("FieldJSONMaster initialized successfully.");
	}
}
