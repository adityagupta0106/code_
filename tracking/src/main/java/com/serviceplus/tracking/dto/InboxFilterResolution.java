package com.serviceplus.tracking.dto;

import java.util.List;
import java.util.Map;

public class InboxFilterResolution {
    private final List<String> applicationIds;
    private final Map<String, List<MatchedOutputAttr>> outputAttrsByApplicationId;

    public InboxFilterResolution(List<String> applicationIds, Map<String, List<MatchedOutputAttr>> outputAttrsByApplicationId) {
        this.applicationIds = applicationIds;
        this.outputAttrsByApplicationId = outputAttrsByApplicationId;
    }
    
	public List<String> getApplicationIds() {
		return applicationIds;
	}

	public Map<String, List<MatchedOutputAttr>> getOutputAttrsByApplicationId() {
		return outputAttrsByApplicationId;
	}
    
}
