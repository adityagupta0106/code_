CREATE DATABASE sp_form_mgmt;
CREATE SCHEMA IF NOT EXISTS schm_sp;
CREATE TABLE schm_sp.form_data_reference
(
    base_service_id BIGINT NOT NULL,
    service_id      BIGINT NOT NULL,
    task_id         CHARACTER VARYING NOT NULL,
    application_id  CHARACTER VARYING NOT NULL,
    holder_id       CHARACTER VARYING NOT NULL,
    form_id         CHARACTER VARYING NOT NULL,
    data_id         CHARACTER VARYING NOT NULL,
    user_id         BIGINT,
    tenant_id       CHARACTER VARYING NOT NULL,
    cr_date         TIMESTAMP WITHOUT TIME ZONE,

    CONSTRAINT form_data_reference_pkey
        PRIMARY KEY (application_id, data_id)
);