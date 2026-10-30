CREATE FUNCTION schm_sp.fnc_generate_seq(tabcode integer) RETURNS numeric
    LANGUAGE plpgsql
    AS $_$

declare 
	lastid numeric;
	update_count numeric;
	db_link text:= '';
begin

	IF $1 = 11 THEN
		select nextval('schm_sp.appln_details') into lastid;
	ELSIF $1 = 77 THEN
		select nextval('schm_sp.appln_current_process') into lastid;
	ELSIF $1 = 80 THEN
		select nextval('schm_sp.appl_fieldset_detail') into lastid;
	ELSIF $1 = 73 THEN
		select nextval('schm_sp.off_param_detail') into lastid;
	ELSIF $1 = 109 THEN
	
	select 'dbname='||db_name||' port='||port||' host='||host||' user='||user_name||' password='||db_password from schm_sp.dblink_details a into db_link;
	
		SELECT * FROM dblink(db_link,'select nextval(''schm_sp.user_location_designation_user_loc_desig_id_seq'')') as cte(nextval bigint) into lastid;
		
	ELSIF $1 = 111 THEN
		select nextval('schm_sp.application_detail_pages_id_seq') into lastid;		
	ELSIF $1 = 114 THEN
		select nextval('schm_sp.esign_seq') into lastid;
	ELSIF $1 = 115 THEN
		select nextval('schm_sp.uid_seq') into lastid;
	ELSIF $1 = 124 THEN
		select nextval('schm_sp.rem_notification_id_seq') into lastid;
	ELSIF $1 = 26 THEN
		select nextval('schm_sp.citizen_document_id_seq') into lastid;
	ELSIF $1 = 16 THEN
	
		select 'dbname='||db_name||' port='||port||' host='||host||' user='||user_name||' password='||db_password from schm_sp.dblink_details a into db_link;
	
		SELECT * FROM dblink(db_link,'select nextval(''schm_sp.user_registration_id_seq'')') as cte(nextval bigint) into lastid;

	ELSIF $1 = 102 THEN
		select nextval('schm_sp.cert_listbox_details_id_seq') into lastid;
	ELSIF $1 = 125 THEN
		select nextval('schm_sp.hibernate_sequence') into lastid;
	ELSIF $1 = 66 THEN
	
	select 'dbname='||db_name||' port='||port||' host='||host||' user='||user_name||' password='||db_password from schm_sp.dblink_details a into db_link;
	
		SELECT * FROM dblink(db_link,'select nextval(''schm_sp.mdds_address_id_seq'')') as cte(nextval bigint) into lastid;		
	ELSE
		select "spdi_last_num" into lastid from "schm_sp"."spe_sequence_last_no" where "spdi_sequence_id"=tabcode for update of "spe_sequence_last_no";	
		
		if lastid is null then
			lastid:=0;
		end if;

		lastid=lastid+1;

		update "schm_sp"."spe_sequence_last_no" set "spdi_last_num"=lastid where "spdi_sequence_id"=tabcode;

		GET DIAGNOSTICS update_count=ROW_COUNT;

		if update_count=0 then

			insert into "schm_sp"."spe_sequence_last_no" values (tabcode, 'System Generated', 1, 'system', current_date);
		
		end if;
		
		
	END IF;
        return lastid;
end;
$_$;

