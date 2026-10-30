package com.serviceplus.formDesigner.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class LinkAttributeNodeDTO {

		private Integer typeId;
	    private String type;
	    private String label;
	    private String value;
	    @JsonInclude(JsonInclude.Include.NON_EMPTY)
	    private List<LinkAttributeNodeDTO> children;
	    
		public Integer getTypeId() {
			return typeId;
		}
		public void setTypeId(Integer typeId) {
			this.typeId = typeId;
		}
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		public String getValue() {
			return value;
		}
		public void setValue(String value) {
			this.value = value;
		}
		public List<LinkAttributeNodeDTO> getChildren() {
			return children;
		}
		public void setChildren(List<LinkAttributeNodeDTO> children) {
			this.children = children;
		}

}
