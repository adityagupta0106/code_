CREATE TABLE IF NOT EXISTS schm_sp.user_inbox
(
    inbox_id bigserial,
    appl_id character varying COLLATE pg_catalog."default",
    appl_ref_no character varying COLLATE pg_catalog."default",
    service_id integer,
    task_id character varying COLLATE pg_catalog."default",
    form_id character varying COLLATE pg_catalog."default",
    current_process_id character varying COLLATE pg_catalog."default",
    user_token character varying COLLATE pg_catalog."default",
    service_name character varying COLLATE pg_catalog."default",
    task_name character varying COLLATE pg_catalog."default",
    appl_recieved_on timestamp without time zone,
    last_action_on timestamp without time zone,
    base_service_id integer,
    location_id integer,
    tenant_id character varying COLLATE pg_catalog."default",
    user_id integer,
    CONSTRAINT pkey PRIMARY KEY (inbox_id)
)

CREATE INDEX IF NOT EXISTS user_inbox_idx   ON schm_sp.user_inbox USING btree(appl_id, appl_ref_no, service_id, task_id, base_service_id,user_id, user_token );


-- Table: schm_sp.application_tracking

-- DROP TABLE IF EXISTS schm_sp.application_tracking;

CREATE TABLE IF NOT EXISTS schm_sp.application_tracking
(
    process_id character varying COLLATE pg_catalog."default" NOT NULL,
    appl_id character varying COLLATE pg_catalog."default" NOT NULL,
    appl_ref_no character varying COLLATE pg_catalog."default" NOT NULL,
    service_id integer NOT NULL,
    base_service_id integer NOT NULL,
    service_name character varying COLLATE pg_catalog."default",
    task_id character varying COLLATE pg_catalog."default" NOT NULL,
    task_name character varying COLLATE pg_catalog."default",
    previous_task_name character varying COLLATE pg_catalog."default",
    previous_task_id character varying COLLATE pg_catalog."default",
    action_code integer,
    action_taken character(1) COLLATE pg_catalog."default",
    applied_user_id bigint,
    beneficiary_user_name character varying COLLATE pg_catalog."default",
    apply_date date,
    holder_id character varying COLLATE pg_catalog."default",
    form_id character varying COLLATE pg_catalog."default",
    data_id character varying COLLATE pg_catalog."default",
    action_on timestamp with time zone,
    tenant_id character varying COLLATE pg_catalog."default",
    created_on timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT application_tracking_pkey PRIMARY KEY (process_id)
)

-- Table: schm_sp.application_tracking_document

-- DROP TABLE IF EXISTS schm_sp.application_tracking_document;

CREATE TABLE IF NOT EXISTS schm_sp.application_tracking_document
(
    id bigint NOT NULL DEFAULT nextval('schm_sp.application_tracking_document_id_seq'::regclass),
    process_id character varying COLLATE pg_catalog."default" NOT NULL,
    document_id character varying COLLATE pg_catalog."default" NOT NULL,
    document_name character varying COLLATE pg_catalog."default",
    document_type character varying COLLATE pg_catalog."default",
    tenant_id character varying COLLATE pg_catalog."default",
    created_on timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT application_tracking_document_pkey PRIMARY KEY (id),
    CONSTRAINT fk_tracking_document_process FOREIGN KEY (process_id)
        REFERENCES schm_sp.application_tracking (process_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE CASCADE
)

-----NEXT ------------------------
-- Table: schm_sp.user_sentbox

-- DROP TABLE IF EXISTS schm_sp.user_sentbox;

CREATE TABLE schm_sp.user_sentbox
(
    id BIGSERIAL PRIMARY KEY,
    process_id CHARACTER VARYING NOT NULL,
    base_service_id INTEGER NOT NULL,
    service_id INTEGER NOT NULL,
    service_name CHARACTER VARYING,
    appl_id CHARACTER VARYING NOT NULL,
    appl_ref_no CHARACTER VARYING NOT NULL,
    submission_date TIMESTAMP WITH TIME ZONE,
    task_id CHARACTER VARYING,
    task_name CHARACTER VARYING,
    calledback BOOLEAN,
    holder_id CHARACTER VARYING NOT NULL,
    action_code INTEGER,
    action_user_id INTEGER,
    action_on TIMESTAMP WITH TIME ZONE,
    action_on_date TIMESTAMP WITH TIME ZONE,
    parent_ref_no CHARACTER VARYING,
    created_on TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tenant_id CHARACTER VARYING NOT NULL
);


CREATE TABLE schm_sp.application_tracking_holder
(
    id BIGSERIAL PRIMARY KEY,
    process_id CHARACTER VARYING NOT NULL,
    holder_id CHARACTER VARYING NOT NULL,
    location_id INTEGER,
    location_name CHARACTER VARYING,
    tenant_id CHARACTER VARYING NOT NULL,
    created_on TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

alter table schm_sp.application_tracking add column action_name character varying;

CREATE TABLE IF NOT EXISTS schm_sp.message_box
(
    message_id bigserial,
    application_id character varying COLLATE pg_catalog."default",
    appl_ref_no character varying COLLATE pg_catalog."default",
    service_id integer,
    base_service_id integer,
    process_id character varying COLLATE pg_catalog."default",
    associated_task_id character varying COLLATE pg_catalog."default",
    current_task_id character varying COLLATE pg_catalog."default",
    current_task_name character varying COLLATE pg_catalog."default",
    service_name character varying COLLATE pg_catalog."default",
    holder_id character varying COLLATE pg_catalog."default",
    location_id integer,
    allow_application_view boolean DEFAULT true,
    allow_history_view boolean DEFAULT true,
    is_read boolean DEFAULT false,
    read_on timestamp with time zone,
    auto_clear boolean DEFAULT false,
    cleared boolean DEFAULT false,
    cleared_on timestamp with time zone,
    created_on timestamp with time zone DEFAULT CURRENT_TIMESTAMP,
    tenant_id character varying COLLATE pg_catalog."default",
    CONSTRAINT message_box_pkey PRIMARY KEY (message_id)
);

ALTER TABLE schm_sp.application_tracking_document ADD COLUMN view_permission JSONB;
ALTER TABLE IF EXISTS schm_sp.user_inbox ADD COLUMN is_priority boolean  NOT NULL DEFAULT false;


---------- inbox-sentbox-filter -----------------------------------
CREATE TABLE IF NOT EXISTS schm_sp.service_filter_config
(
    id bigserial NOT NULL,
    filter_id bigint NOT NULL,
    service_id bigint NOT NULL,
    base_service_id bigint NOT NULL,
	task_id character varying COLLATE pg_catalog."default" NOT NULL,
    attr_form_id character varying COLLATE pg_catalog."default",
    tenant_id character varying COLLATE pg_catalog."default" NOT NULL,
    attr_type character(1) COLLATE pg_catalog."default" NOT NULL,
    attr_id character varying COLLATE pg_catalog."default" NOT NULL,
    attr_label character varying,
    filter_type character(1) NOT NULL;
    attr_order bigint NOT NULL,
    filter_condition character varying COLLATE pg_catalog."default",
    cr_date timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    up_date timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT service_filter_config_pkey PRIMARY KEY (id),
    CONSTRAINT unq_service_filter_config_filter_id UNIQUE (filter_id,task_id,attr_id,attr_type)
);


CREATE TABLE IF NOT EXISTS schm_sp.service_filter_input_data
(
    id bigserial NOT NULL,
    filter_id bigint NOT NULL,
    base_service_id bigint NOT NULL,
    service_id bigint NOT NULL,
    application_id character varying COLLATE pg_catalog."default" NOT NULL,
    attr1_value character varying COLLATE pg_catalog."default",
    attr2_value character varying COLLATE pg_catalog."default",
    attr3_value character varying COLLATE pg_catalog."default",
    attr4_value character varying COLLATE pg_catalog."default",
    attr5_value character varying COLLATE pg_catalog."default",
    attr6_value character varying COLLATE pg_catalog."default",
    attr7_value character varying COLLATE pg_catalog."default",
    attr8_value character varying COLLATE pg_catalog."default",
    attr9_value character varying COLLATE pg_catalog."default",
    attr10_value character varying COLLATE pg_catalog."default",
    cr_date timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    up_date timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT service_filter_input_data_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schm_sp.service_filter_output_data
(
    id bigserial NOT NULL,
    filter_id bigint NOT NULL,
    base_service_id bigint NOT NULL,
    service_id bigint NOT NULL,
    application_id character varying COLLATE pg_catalog."default" NOT NULL,
    attr1_value character varying COLLATE pg_catalog."default",
    attr2_value character varying COLLATE pg_catalog."default",
    attr3_value character varying COLLATE pg_catalog."default",
    attr4_value character varying COLLATE pg_catalog."default",
    attr5_value character varying COLLATE pg_catalog."default",
    attr6_value character varying COLLATE pg_catalog."default",
    attr7_value character varying COLLATE pg_catalog."default",
    attr8_value character varying COLLATE pg_catalog."default",
    attr9_value character varying COLLATE pg_catalog."default",
    attr10_value character varying COLLATE pg_catalog."default",
    cr_date timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    up_date timestamp with time zone NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT service_filter_output_data_pkey PRIMARY KEY (id)
);

ALTER TABLE schm_sp.user_sentbox
    ALTER COLUMN action_on_date TYPE date;


-------------------- 14/09/2026---------------------


-- Table: schm_sp.applicant_inbox

-- DROP TABLE IF EXISTS schm_sp.applicant_inbox;

CREATE TABLE IF NOT EXISTS schm_sp.applicant_inbox
(
    id bigserial,
    application_id character varying COLLATE pg_catalog."default" NOT NULL,
    application_ref_no character varying COLLATE pg_catalog."default",
    service_id integer,
    base_service_id integer,
    service_name character varying COLLATE pg_catalog."default",
    form_id character varying COLLATE pg_catalog."default",
    task_id character varying COLLATE pg_catalog."default" NOT NULL,
    task_name character varying COLLATE pg_catalog."default",
    current_process_id character varying COLLATE pg_catalog."default" NOT NULL,
    tenant_id character varying COLLATE pg_catalog."default",
    applied_by bigint,
    beneficiary_name character varying COLLATE pg_catalog."default",
    submission_to_same_official boolean NOT NULL DEFAULT false,
    requires_payment boolean NOT NULL DEFAULT false,
    requires_form boolean NOT NULL DEFAULT false,
    status character varying COLLATE pg_catalog."default",
    apply_date timestamp with time zone,
    cr_date timestamp with time zone,
    up_date timestamp with time zone,
    CONSTRAINT applicant_inbox_pkey PRIMARY KEY (id),
    CONSTRAINT applicant_inbox_current_process_id_key UNIQUE (current_process_id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS schm_sp.applicant_inbox
    OWNER to postgres;
    
CREATE TABLE IF NOT EXISTS schm_sp.task_master
(
    id bigserial,
    task_id character varying COLLATE pg_catalog."default" NOT NULL,
    service_id integer NOT NULL,
    base_service_id integer NOT NULL,
    task_name character varying COLLATE pg_catalog."default" NOT NULL,
    tenant_id character varying COLLATE pg_catalog."default" NOT NULL,
    task_property jsonb,
    CONSTRAINT task_master_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS schm_sp.application_document_tiny_url
(
    id BIGSERIAL NOT NULL,
    application_id character varying COLLATE pg_catalog."default" NOT NULL,
    reference_no character varying COLLATE pg_catalog."default",
    process_id character varying COLLATE pg_catalog."default" NOT NULL,
    tiny_url character varying COLLATE pg_catalog."default" NOT NULL,
    url_key character varying COLLATE pg_catalog."default" NOT NULL,
    access_flag boolean NOT NULL DEFAULT false,
    tenant_id character varying COLLATE pg_catalog."default" NOT NULL,
    cr_date timestamp with time zone NOT NULL,
    CONSTRAINT application_document_tiny_url_pkey
        PRIMARY KEY (id)
);


CREATE TABLE IF NOT EXISTS schm_sp.application_action_tiny_url
(
    id BIGSERIAL NOT NULL,
    application_id VARCHAR NOT NULL,
    service_id INTEGER NOT NULL,
    application_ref_no VARCHAR,
    tiny_url VARCHAR NOT NULL,
    url_key VARCHAR NOT NULL,
    tenant_id VARCHAR NOT NULL,
    cr_date TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT application_action_tiny_url_pkey
        PRIMARY KEY (id)
);


ALTER TABLE schm_sp.application_tracking
ADD COLUMN IF NOT EXISTS action_remarks VARCHAR(500);

ALTER TABLE schm_sp.application_document_tiny_url 
ADD COLUMN IF NOT EXISTS document_id VARCHAR;