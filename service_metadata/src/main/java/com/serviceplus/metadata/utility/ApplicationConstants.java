package com.serviceplus.metadata.utility;

import java.util.Map;

public class ApplicationConstants {

	public static final String SP_SCHEMA_NAME = "schm_sp";	
	public static final String SUBMISSION_NODE_ID = "s_1";	
	public static final String SUBMISSION_NODE_TYPE = "submission";	
	public static final String SUBMISSION_NODE_NAME = "Service Initiation";	
	public static final String GATEWAY_NODE_TYPE = "gateway";	
	public static final Map<Integer, String> ROLE_TO_SUBMISSION_MODE = Map.of(
		    6, "112",   
		    24, "112",  
		    7, "113",   
		    25, "113",  
		    14, "111",  
		    26, "111",  
		    5, "114"
		);
	public static final String ONLINE = "Online";
	public static final String ACTIVATED = "Activated";
	public static final String ATTRIBUTE="Attribute";
	public static final String TASK="Task";
	public static final String ENCLOSURE="Enclosure";
	public static final String SYSTEM_ATTRIBUTE="System Attribute";
    public static final String WORKFLOW_ASSIGNMENT_TOKEN_PREFIX="WFT";
    public static final String STATIC_AUTH_TOKEN ="046a347d723b94c639fe19bbde2f1f5ce8b3ffd68c299fb75813bf766c508661";
    public static final Integer ROLE_EXECUTIVE=101;
    public static final String OUTBOX_TOKEN_PREFIX="OB";
    public static final String OUTBOX_STATUS_PENDING="PENDING";
    public static final String OUTBOX_STATUS_PROCESSED="PROCESSED";
    public static final Integer ROLE_WORKFLOW_OFFICER = 5;
    public static final Integer ROLE_DESIGNATED_OFFICER = 3;
    public static final Integer ROLE_DATA_ENTRY_OPPERATOR = 14;

    public static final String TYPE_GATEWAY = "gateway";
    public static final String TYPE_TASK = "task";
    public static final String GATEWAY_BEHAVIOUR_EXCLUSIVE_DIVERGENT = "ED";
    public static final String GATEWAY_BEHAVIOUR_EXCLUSIVE_CONVERGENT = "EC";
    public static final String GATEWAY_BEHAVIOUR_INCLUSIVE_DIVERGENT = "ID";
    public static final String GATEWAY_BEHAVIOUR_INCLUSIVE_CONVERGENT = "IC";
    public static final String GATEWAY_BEHAVIOUR_PARALLEL_DIVERGENT = "PD";
    public static final String GATEWAY_BEHAVIOUR_PARALLEL_CONVERGENT = "PC";
    public static final Integer SUBMISSION_MODE_DEO = 111;
    public static final Integer SUBMISSION_MODE_APPLICANT = 112;
    public static final Integer SUBMISSION_MODE_CLOSED_USER_GROUP = 113;
    public static final Integer SUBMISSION_MODE_OFFICIAL = 114;
    public static final Integer SUBMISSION_MODE_WEBSERVICE = 115;
    
    public static final String APPLICANT_EMAIL_SYSTEM_VARIABLE = "Sys_2111";
    public static final String DO_EMAIL_SYSTEM_VARIABLE = "Sys_1338";
    public static final String NOTIFICATION_TO_MASTER = "Notification To";
	public static final String REDIS_KEY_OF_SERVICEMETADATA = "ServiceMetaData_";
	public static final String PATTERN_ATTRIBUTE_DELIMITER = "PATTERN_";
	public static final String PATTERN_ATTRIBUTE = "Pattern Attribute";

}
