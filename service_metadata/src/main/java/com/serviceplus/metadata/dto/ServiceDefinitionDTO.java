package com.serviceplus.metadata.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

@NotNull(message = "Service definition cannot be null")
public class ServiceDefinitionDTO {
	
	private Integer serviceId;

    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    private LogoDTO logo;

    @NotBlank(message = "Abbreviation is required")
    @Size(min = 2, max = 10, message = "Abbreviation must be between 2 and 10 characters")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Abbreviation must contain only uppercase letters and numbers")
    private String abbreviation;

    @NotNull(message = "Keywords are required")
    @Size(min = 1, max = 10, message = "keywords:At least 1 and at most 10 keywords are allowed")
    private List<@NotBlank(message = "keywords:Keyword cannot be blank") @Size(max = 50, message = "keywords:Keyword cannot exceed 50 characters") String> keywords;

    @NotNull(message = "Category is required")
    private CategoryDTO category;

    @NotNull(message = "Type is required")
    private TypeDTO type;

//    @NotBlank(message = "Register to avail is required")
//    @Pattern(regexp = "^[YN]$", message = "Register to avail must be 'Y' or 'N'")
    private String registerToAvail;

    @NotBlank(message = "Service delivery act is required")
    @Pattern(regexp = "^[YN]$", message = "Service delivery act must be 'Y' or 'N'")
    private String serviceDeliveryAct;

   
   private List<Goal> goals;

    @NotBlank(message = "FIFO is required")
    @Pattern(regexp = "^[YN]$", message = "FIFO must be 'Y' or 'N'")
    private String fifo;

    @NotBlank(message = "eKYC is required")
    @Pattern(regexp = "^[YN]$", message = "eKYC must be 'Y' or 'N'")
    private String eKYC;

    @NotBlank(message = "Feedback is required")
    @Pattern(regexp = "^[YN]$", message = "Feedback must be 'Y' or 'N'")
    private String feedback;
    
    
    @NotBlank(message = "Auto Appeal is required")
    @Pattern(regexp = "^[YN]$", message = "Auto Appeal must be 'Y' or 'N'")
    private String autoAppeal;
    
    private ServiceLevelDTO serviceLevel;

    @Size(max = 20, message = "At most 20 FAQs are allowed")
    private List<FaqDTO> faqs;
    
    private String tablist;
    private String checksum;
    private List<String> auaConfig;
    private String nonAdoptedSubmissionPolicy;

    public String getNonAdoptedSubmissionPolicy() {
        return nonAdoptedSubmissionPolicy;
    }

    public void setNonAdoptedSubmissionPolicy(String nonAdoptedSubmissionPolicy) {
        this.nonAdoptedSubmissionPolicy = nonAdoptedSubmissionPolicy;
    }

    public static class CategoryDTO {
        @NotBlank(message = "Category label is required")
        @Size(max = 100, message = "Category label cannot exceed 100 characters")
        private String label;

        @NotBlank(message = "Category value is required")
        @Pattern(regexp = "^[A-Z0-9_]+$", message = "Category value must contain uppercase letters, numbers, or underscores")
        private String value;

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
        
    }
    public static class LogoDTO {
    	
        @NotBlank(message = "Logo name is required")
        @Size(max = 100, message = "Logo name cannot exceed 100 characters")
        private String name;

        @Pattern(regexp = "^image/(png|jpeg|jpg)$", message = "Logo type must be image/png, image/jpeg, or image/jpg")
        private String type;

        private Long size;

        @NotBlank(message = "Base64 data is required")
        private String base64;

    	public String getName() {
    		return name;
    	}

    	public void setName(String name) {
    		this.name = name;
    	}

    	public String getType() {
    		return type;
    	}

    	public void setType(String type) {
    		this.type = type;
    	}

    	public Long getSize() {
    		return size;
    	}

    	public void setSize(Long size) {
    		this.size = size;
    	}

    	public String getBase64() {
    		return base64;
    	}

    	public void setBase64(String base64) {
    		this.base64 = base64;
    	}
        
        
    }

    public static class TypeDTO {
        @NotBlank(message = "Type label is required")
        @Size(max = 100, message = "Type label cannot exceed 100 characters")
        private String label;

        @NotNull(message = "Type value is required")
        @Min(value = 1, message = "Type value must be at least 1")
        private Integer value;

    	public String getLabel() {
    		return label;
    	}

    	public void setLabel(String label) {
    		this.label = label;
    	}

    	public Integer getValue() {
    		return value;
    	}

    	public void setValue(Integer value) {
    		this.value = value;
    	}
        
        
    }


    public static class ServiceLevelDTO {
        //@NotNull(message = "Service level type is required")
        private ServiceLevelTypeDTO type;

        //@NotBlank(message = "Quantity is required")
        //@Pattern(regexp = "^[0-9]+$", message = "Quantity must be a positive number")
        private String quantity;

       // @NotNull(message = "Service level unit is required")
        private ServiceLevelUnitDTO unit;

        
    	public ServiceLevelDTO() {
		super();
	}

		public ServiceLevelTypeDTO getType() {
    		return type;
    	}

    	public void setType(ServiceLevelTypeDTO type) {
    		this.type = type;
    	}

    	public String getQuantity() {
    		return quantity;
    	}

    	public void setQuantity(String quantity) {
    		this.quantity = quantity;
    	}

    	public ServiceLevelUnitDTO getUnit() {
    		return unit;
    	}

    	public void setUnit(ServiceLevelUnitDTO unit) {
    		this.unit = unit;
    	}
        
        
    }


   public static class ServiceLevelTypeDTO {
       // @NotBlank(message = "Service level type label is required")
       // @Size(max = 50, message = "Service level type label cannot exceed 50 characters")
        private String label;

      //  @NotBlank(message = "Service level type value is required")
      //  @Size(max = 50, message = "Service level type value cannot exceed 50 characters")
        private String value;

        public ServiceLevelTypeDTO() {}
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
        
        
    }


    public static class ServiceLevelUnitDTO {
        //@NotBlank(message = "Unit label is required")
        //@Size(max = 50, message = "Unit label cannot exceed 50 characters")
        private String label;

       // @NotBlank(message = "Unit value is required")
       // @Pattern(regexp = "^[a-zA-Z]+$", message = "Unit value must contain only letters")
        private String value;
        
        

    	public ServiceLevelUnitDTO() {
		super();
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
        
    }
    public  static class Goal {
        private Integer value;
        private String label;
        
		public Integer getValue() {
			return value;
		}
		public void setValue(Integer value) {
			this.value = value;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}

    }

    public static class FaqDTO {
        @NotBlank(message = "FAQ question is required")
        @Size(max = 200, message = "FAQ question cannot exceed 200 characters")
        private String question;

        @NotBlank(message = "FAQ answer is required")
        @Size(max = 1000, message = "FAQ answer cannot exceed 1000 characters")
        private String answer;
        
        

    	public FaqDTO() {
			super();
		}

		public String getQuestion() {
    		return question;
    	}

    	public void setQuestion(String question) {
    		this.question = question;
    	}

    	public String getAnswer() {
    		return answer;
    	}

    	public void setAnswer(String answer) {
    		this.answer = answer;
    	}
        
        
    }
	public Integer getServiceId() {
		return serviceId;
	}

	public void setServiceId(Integer serviceId) {
		this.serviceId = serviceId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAbbreviation() {
		return abbreviation;
	}

	public void setAbbreviation(String abbreviation) {
		this.abbreviation = abbreviation;
	}

	public LogoDTO getLogo() {
		return logo;
	}

	public void setLogo(LogoDTO logo) {
		this.logo = logo;
	}

	public List<String> getKeywords() {
		return keywords;
	}

	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	public CategoryDTO getCategory() {
		return category;
	}

	public void setCategory(CategoryDTO category) {
		this.category = category;
	}

	public TypeDTO getType() {
		return type;
	}

	public void setType(TypeDTO type) {
		this.type = type;
	}

	public String getRegisterToAvail() {
		return registerToAvail;
	}

	public void setRegisterToAvail(String registerToAvail) {
		this.registerToAvail = registerToAvail;
	}

	public String getServiceDeliveryAct() {
		return serviceDeliveryAct;
	}

	public void setServiceDeliveryAct(String serviceDeliveryAct) {
		this.serviceDeliveryAct = serviceDeliveryAct;
	}

	public List<Goal> getGoals() {
		return goals;
	}

	public void setGoals(List<Goal> goals) {
		this.goals = goals;
	}

	public String getAutoAppeal() {
		return autoAppeal;
	}

	public void setAutoAppeal(String autoAppeal) {
		this.autoAppeal = autoAppeal;
	}

	public String getFifo() {
		return fifo;
	}

	public void setFifo(String fifo) {
		this.fifo = fifo;
	}

	public String geteKYC() {
		return eKYC;
	}

	public void seteKYC(String eKYC) {
		this.eKYC = eKYC;
	}

	public String getFeedback() {
		return feedback;
	}

	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}

	public ServiceLevelDTO getServiceLevel() {
		return serviceLevel;
	}

	public void setServiceLevel(ServiceLevelDTO serviceLevel) {
		this.serviceLevel = serviceLevel;
	}

	public List<FaqDTO> getFaqs() {
		return faqs;
	}

	public void setFaqs(List<FaqDTO> faqs) {
		this.faqs = faqs;
	}

	public ServiceDefinitionDTO() {
		super();
	}
	
	public String getTablist() {
		return tablist;
	}

	public void setTablist(String tablist) {
		this.tablist = tablist;
	}

	public String getChecksum() {
		return checksum;
	}

	public void setChecksum(String checksum) {
		this.checksum = checksum;
	}

    public List<String> getAuaConfig() {
        return auaConfig;
    }

    public void setAuaConfig(List<String> auaConfig) {
        this.auaConfig = auaConfig;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ServiceDefinitionDTO historyDefinition;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ServiceDefinitionDTO oldDefinition;

    public ServiceDefinitionDTO getHistoryDefinition() {
        return historyDefinition;
    }

    public void setHistoryDefinition(ServiceDefinitionDTO historyDefinition) {
        this.historyDefinition = historyDefinition;
        if (this.oldDefinition == null) {
            this.oldDefinition = historyDefinition;
        }
    }

    public ServiceDefinitionDTO getOldDefinition() {
        return oldDefinition;
    }

    public void setOldDefinition(ServiceDefinitionDTO oldDefinition) {
        this.oldDefinition = oldDefinition;
        if (this.historyDefinition == null) {
            this.historyDefinition = oldDefinition;
        }
    }
}
