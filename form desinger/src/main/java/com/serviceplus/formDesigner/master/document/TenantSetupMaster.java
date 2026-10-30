package com.serviceplus.formDesigner.master.document;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.FieldJSONMaster;
import com.serviceplus.formDesigner.formconfig.attrtypemaster.document.FieldTypeMaster;

@Document("tenantSetupMaster")
public class TenantSetupMaster {
	private String id;
	private String type;
	private RecordData records;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public RecordData getRecords() {
		return records;
	}

	public void setRecords(RecordData records) {
		this.records = records;
	}

	public static class RecordData {
		private FieldTypeMaster fieldTypeMaster;
		private List<FieldJSONMaster> fieldJSONMaster;

		public FieldTypeMaster getFieldTypeMaster() {
			return fieldTypeMaster;
		}

		public void setFieldTypeMaster(FieldTypeMaster fieldTypeMaster) {
			this.fieldTypeMaster = fieldTypeMaster;
		}

		public List<FieldJSONMaster> getFieldJSONMaster() {
			return fieldJSONMaster;
		}

		public void setFieldJSONMaster(List<FieldJSONMaster> fieldJSONMaster) {
			this.fieldJSONMaster = fieldJSONMaster;
		}

	}

}
