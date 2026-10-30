CREATE TABLE schm_sp.module_master
(
    module_id bigint NOT NULL,
    module_name character varying NOT NULL,
    status boolean NOT NULL,
    PRIMARY KEY (module_id)
);

	
CREATE TABLE schm_sp.system_variable_master
(
    attribute_id character varying NOT NULL,
    attribute_label character varying,
    module_id bigint,
    PRIMARY KEY (attribute_id),
    CONSTRAINT fk_module_master_id FOREIGN KEY (module_id)
        REFERENCES schm_sp.module_master (module_id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
);

INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1', 'Application Date', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2', 'Service Completion Date', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_3', 'Application Ref No', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_6', 'State(Local Language)', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_16', 'Expiry Date', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_17', 'Service Due Date', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_18', 'Certificate Validity Period', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1332', 'Total Amount', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1333', 'Bar Code', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1335', 'DO Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1336', 'DO Address', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1338', 'DO Email Address', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1339', 'DO Phone No', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1340', 'Application Received On', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1341', 'Designation Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1342', 'Action On Date', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1343', 'Designation', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2022', 'Facsimile', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2023', 'Kiosk Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2024', 'Kiosk Registration No', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2025', 'Service Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2026', 'Payment Reference Number', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2027', 'Payment Date', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2029', 'Application Submission Mode', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2031', 'Dsc Position', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2032', 'Payment Mode', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2033', 'Payment Ref No. with Label', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2034', 'Applicant Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2035', 'State', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2036', 'Task Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2037', 'Task Type Id', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2038', 'Zip File', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2039', 'Payment Done', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2040', 'Application ID', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2041', 'Payment Details', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2042', 'WFPs location', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2056', 'Array of Enclosures', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2057', 'RAS', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2058', 'Kiosk Details', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2059', 'Primary Identifier', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2060', 'Bank CIN No', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2061', 'Dept DDO', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2062', 'Full Payment Details', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2064', 'Reason', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2065', 'Remarks', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2066', 'Status', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2067', 'Doc Id', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2068', 'Logo Position', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2070', 'Refund Amount', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2071', 'Refund Date', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2072', 'Refund Mode', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2073', 'Refund Reference No', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2074', 'Refund Task', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2075', 'Certificate Token Number', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2076', 'UNIQUE', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2077', 'Tiny Url', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2078', 'Print Certificate Token Number', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2079', 'Edit Application virtual Task', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2080', 'Bunched Application Detail', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2081', 'Action Tiny URL', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2083', 'System Generated', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2084', 'Service LOGO', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2085', 'Service Description', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2086', 'Instance ID', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2087', 'Service Owner', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2088', 'Service Category', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2089', 'Service FAQ', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2090', 'Instance API', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2091', 'Service Tiny URL', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2092', 'Secret Key', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2093', 'Service Status', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2094', 'Service Version', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2095', 'Suspended On', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2096', 'Suspend Reason', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2097', 'Service ID', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2098', 'Department Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2099', 'Service Aliases', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2100', 'Multilingual Service Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2101', 'Multilingual Department Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2103', 'Random String', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2104', 'Coverage Location ID', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2105', 'User ID', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2107', 'Certificate Number', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2108', 'Digilocker ID', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2109', 'Digilocker Reference Key', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2111', 'Applicant Email ID', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2112', 'Applicant Mobile No', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2113', 'Applicant State Code', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2114', 'Applicant State Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2115', 'Applicant Gender', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2116', 'Applicant Marital Status', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2117', 'Applicant Date of Birth', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2118', 'Applicant Address', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2119', 'Applicant Country Code', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2120', 'Applicant Country Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2121', 'Applicant District Code', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2122', 'Applicant District Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2123', 'Applicant Pin Code', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2124', 'Applicant Landline', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2125', 'Helpline Email Address', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2126', 'Helpline Contact Number', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2127', 'Helpline Details', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2128', 'Application Date Time', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2129', 'AutoGrievance', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2130', 'Grievance Tiny URL', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1046', 'Amount', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_1344', 'User Name', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2028', 'AttachDetailWithRefNo(ZIP)', NULL);
INSERT INTO schm_sp.system_variable_master VALUES ('Sys_2131', 'eSign Position', NULL);

update schm_sp.system_variable_master set attribute_id=replace(attribute_id,'sys','Sys')