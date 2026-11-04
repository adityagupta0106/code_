# ServiceMetadata `/service-data` — line-by-line explanation (Hinglish)

**Important version note:** Ye explanation us 175-line `buildServiceCatalogueData()` method ke liye hai jo tumne chat mein Codex refactor **se pehle** diya tha. Codex ne tumhare local method ko **167 lines** ka kiya; woh latest local file mere paas nahi hai. Working API ke behavior ko user ne service IDs `700001` aur `490001` par verify kiya tha.

**Microservice:** ServiceMetadata. **Controller:** `ServiceCatalogueController#getServiceCatalogueData`. **Endpoint:** `GET http://localhost:8091/services/catalogue/service-data?serviceId=700001`. No POST body.

## Key sources

| Data | Source |
|---|---|
| Service identity and description | `schm_sp.service_definition` |
| Tenant ID | `schm_sp.service_log` |
| Modes, department/state | `schm_sp.service_process_flow.process_flow_json` |
| Fallback department/state | `schm_sp.service_delivery_unit_definition.delivery_unit_json` |
| Beneficiary + availedBy | `schm_sp.service_catalogue_service_mapping` → `schm_sp.service_catalogue_configuration.data` |
| Deliverable template | `schm_sp.service_output_format.output_format_json` |

## Lines 1–10: Input validation and PostgreSQL records

- **L1:** `@Transactional(readOnly = true)` — Spring transaction read-only hai; ye method data read karta hai, DB update nahi karta.
- **L2:** `public ServiceCatalogueDataDTO buildServiceCatalogueData(Integer serviceId) {` — Public service method start: integer serviceId leta hai aur ServiceCatalogueDataDTO return karta hai.
- **L3:** `if (serviceId == null) {` — Check karta hai ki caller ne serviceId diya hai ya nahi.
- **L4:** `throw new SPRuntimeError("Service id is required", HttpStatus.BAD_REQUEST);` — Agar ID missing hai toh HTTP 400 BAD_REQUEST exception.
- **L5:** `}` — Is scope/condition/loop ko close karta hai.
- **L7:** `ServiceDefinition sd = serviceDefinitionRepository.findById(serviceId)` — service_definition table se given service ki entity read karta hai.
- **L8:** `.orElseThrow(() -> new SPRuntimeError("Service not found", HttpStatus.NOT_FOUND));` — Service record na mile toh HTTP 404 NOT_FOUND deta hai.
- **L9:** `ServiceDefinitionDTO def = sd.getDefinitionJson();` — Entity ke definition_json ko typed ServiceDefinitionDTO mein leta hai.
- **L10:** `ServiceLog log = serviceLogRepository.findByServiceId(serviceId);` — service_log se tenant_id aur related service status record fetch karta hai.

## Lines 11–39: Workflow submission roles and first department/state labels

- **L12:** `// Workflow: submission roles + department/state labels` — Is block ka purpose submission roles aur department/state labels hai.
- **L13:** `ServiceProcessFlow flow = processFlowRepository.findByServiceId(serviceId);` — service_process_flow se workflow entity nikalta hai.
- **L14:** `ProcessFlowDTO.NodeDTO start = flow == null || flow.getProcessFlowJson() == null` — Null-safety: flow ya processFlowJson absent hai kya?
- **L15:** `|| flow.getProcessFlowJson().getNodes() == null ? null` — Workflow nodes bhi missing hain toh start node ko null rakhta hai.
- **L16:** `: flow.getProcessFlowJson().getNodes().stream()` — Nodes available hon toh stream shuru karta hai.
- **L17:** `.filter(n -> n != null && "submission".equals(n.getType()))` — Sirf submission-type node filter karta hai; Java mein null nodes skip hote hain.
- **L18:** `.findFirst().orElse(null);` — Pehla matching submission node leta hai; missing ho toh null.
- **L20:** `List<String> modes = new ArrayList<>();` — Output submission roles store karne ke liye empty list initialize karta hai.
- **L21:** `String department = null;` — Department name initially unknown, isliye null.
- **L22:** `String state = null;` — State name initially unknown, isliye null.
- **L23:** `if (start != null && start.getData() != null) {` — Submission node aur uska data dono available hon tabhi read.
- **L24:** `if (start.getData().getMode() != null) {` — Submission mode list null na ho toh process karo.
- **L25:** `modes = start.getData().getMode().stream()` — Mode records ki stream start.
- **L26:** `.filter(m -> m != null && m.getLabel() != null)` — Null mode ya null label remove karta hai.
- **L27:** `.map(m -> m.getLabel()).distinct().toList();` — Mode labels leta hai, duplicates remove karke list banata hai.
- **L28:** `}` — Is scope/condition/loop ko close karta hai.
- **L29:** `if (start.getData().getEntities() != null) {` — Entities/department context present hai ya nahi check.
- **L30:** `for (ProcessFlowDTO.Entities entity : start.getData().getEntities()) {` — Har configured entity par iterate karta hai.
- **L31:** `if (entity == null) continue;` — Null entity ko skip.
- **L32:** `if (department == null && entity.getEntity() != null)` — Department abhi null ho aur entity label object present ho tabhi set.
- **L33:** `department = entity.getEntity().getLabel();` — Entity ka label actual department name hai.
- **L34:** `if (state == null && entity.getClcDetail() != null)` — State abhi null aur clcDetail present ho tabhi set.
- **L35:** `state = entity.getClcDetail().getLabel();` — clcDetail ka label actual state name hai.
- **L36:** `if (department != null && state != null) break;` — Dono names mil gaye toh loop stop.
- **L37:** `}` — Is scope/condition/loop ko close karta hai.
- **L38:** `}` — Is scope/condition/loop ko close karta hai.
- **L39:** `}` — Is scope/condition/loop ko close karta hai.

## Lines 40–55: SDU fallback

- **L41:** `// Fallback if no department/state is configured on the submission node` — Agar workflow labels missing hain toh SDU se fallback.
- **L42:** `if (department == null || state == null) {` — Department YA state missing hai kya?
- **L43:** `ServiceDeliveryUnitDefinition sdu = serviceDeliveryUnitDefinitionRepository.findByServiceId(serviceId);` — service_delivery_unit_definition entity fetch karta hai.
- **L44:** `if (sdu != null && sdu.getServiceDeliveryUnits() != null) {` — SDU aur units list present ho tabhi iterate.
- **L45:** `for (ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON unit : sdu.getServiceDeliveryUnits()) {` — Configured delivery units par loop.
- **L46:** `if (unit == null || unit.getServiceDeliveryUnit() == null) continue;` — Invalid/missing unit ko skip.
- **L47:** `var hierarchy = unit.getServiceDeliveryUnit();` — Service delivery unit ka hierarchy object reference.
- **L48:** `if (department == null && hierarchy.getEntity() != null)` — Missing department aur present entity object par condition.
- **L49:** `department = hierarchy.getEntity().getLabel();` — SDU entity label department mein use hota hai.
- **L50:** `if (state == null && hierarchy.getClcDetail() != null)` — Missing state aur present clcDetail par condition.
- **L51:** `state = hierarchy.getClcDetail().getLabel();` — SDU clcDetail label state mein use hota hai.
- **L52:** `if (department != null && state != null) break;` — Dono fields milne par loop break.
- **L53:** `}` — Is scope/condition/loop ko close karta hai.
- **L54:** `}` — Is scope/condition/loop ko close karta hai.
- **L55:** `}` — Is scope/condition/loop ko close karta hai.

## Lines 56–60: SDG goals

- **L57:** `List<String> goals = def == null || def.getGoals() == null ? List.of()` — No definition/goals => empty list; otherwise goals map karenge.
- **L58:** `: def.getGoals().stream()` — SDG goals list stream.
- **L59:** `.filter(g -> g != null && g.getLabel() != null)` — Null goals aur label-less goals skip.
- **L60:** `.map(g -> g.getLabel()).toList();` — Sirf SDG labels ki strings return karta hai.

## Lines 61–122: Beneficiary and availedBy dynamic mapping

- **L62:** `// Catalogue configuration: all enabled beneficiary fields, not just five` — Beneficiary questionnaire ki saari enabled categories read karni hain.
- **L63:** `List<String> availedBy = new ArrayList<>();` — availedBy ke liye initially empty list.
- **L64:** `List<ServiceCatalogueDataDTO.Beneficiary> beneficiary = new ArrayList<>();` — beneficiary list mein attribute + values objects jayenge.
- **L65:** `Optional<ServiceCatalogueServiceMapping> mapping = mappingRepository.findByServiceId(serviceId.longValue());` — serviceId -> configurationId mapping read karta hai (Long conversion).
- **L66:** `if (mapping.isPresent()) {` — Agar service ke liye mapping available hai.
- **L67:** `JsonNode configData = configurationRepository.findDataByConfigurationId(` — Configuration entity load nahi; repository se sirf JSON `data` fetch.
- **L68:** `mapping.get().getConfigurationId());` — Mapping ke configurationId ko query mein pass.
- **L69:** `if (configData != null) {` — Data available hai tab process; missing `status` column problem avoid hoti hai.
- **L70:** `JsonNode profile = configData.path("beneficiaryProfile");` — data.beneficiaryProfile ke andar questionnaire section.
- **L71:** `if (profile.path("enabled").asBoolean(false)) {` — Entire beneficiaryProfile enabled hai kya?
- **L72:** `JsonNode answers = profile.path("answers");` — beneficiaryProfile.answers mein category answers hain.
- **L73:** `JsonNode applicant = answers.path("applicant_type");` — applicant_type section leta hai.
- **L74:** `if (applicant.path("enabled").asBoolean(false)) {` — Applicant type category enabled hai tab.
- **L75:** `for (JsonNode v : applicant.path("who_can_apply")) {` — who_can_apply entries ke upar loop.
- **L76:** `if (v.isTextual() && !v.asText().isBlank()) availedBy.add(v.asText());` — Nonempty string entries ko availedBy mein add.
- **L77:** `}` — Is scope/condition/loop ko close karta hai.
- **L78:** `}` — Is scope/condition/loop ko close karta hai.
- **L79:** `for (java.util.Iterator<Map.Entry<String, JsonNode>> categories = answers.fields(); categories.hasNext();) {` — answers ke har category entry ko iterate karta hai.
- **L80:** `JsonNode category = categories.next().getValue();` — Current category ka JSON object.
- **L81:** `if (!category.path("enabled").asBoolean(false)) continue;` — Disabled category skip—e.g. required_documents enabled=false for service 700001.
- **L82:** `for (java.util.Iterator<Map.Entry<String, JsonNode>> fields = category.fields(); fields.hasNext();) {` — Category ke andar fields ke upar loop.
- **L83:** `Map.Entry<String, JsonNode> f = fields.next();` — Field name + JSON value ki entry leta hai.
- **L84:** `if ("enabled".equals(f.getKey())) continue;` — `enabled` metadata field output mein nahi dikhata.
- **L85:** `List<String> values = new ArrayList<>();` — Current field ke values collect karne ke liye empty list.
- **L86:** `JsonNode value = f.getValue();` — Current JSON field ka actual value.
- **L87:** `if (value.isArray()) {` — Array category value (e.g. applicable_gender) ka case.
- **L88:** `value.forEach(v -> { if (v.isValueNode() && !v.asText().isBlank()) values.add(v.asText()); });` — Array se nonblank scalar values add karta hai.
- **L89:** `} else if (value.isObject()) {` — Nested JSON object ka case (e.g. age_range/dob_range).
- **L90:** `if ("age_range".equals(f.getKey())` — Special handling for field named age_range.
- **L91:** `&& value.hasNonNull("min_age") && value.hasNonNull("max_age")) {` — Minimum aur maximum age dono present hon tab.
- **L92:** `values.add(value.path("min_age").asText() + "-"` — min_age + dash string form karna start.
- **L93:** `+ value.path("max_age").asText() + " yrs");` — max_age + yrs add, jaise 18-34 yrs.
- **L94:** `} else if ("dob_range".equals(f.getKey())` — Special handling for dob_range field.
- **L95:** `&& value.hasNonNull("born_after") && value.hasNonNull("born_before")) {` — born_after aur born_before dono set hain kya?
- **L96:** `values.add(value.path("born_after").asText() + " to "` — Start DOB range string.
- **L97:** `+ value.path("born_before").asText());` — End DOB range jodta hai.
- **L98:** `} else {` — Generic object handling jab age/dob special case nahi.
- **L99:** `value.fields().forEachRemaining(e -> {` — Object ke nested primitive fields loop.
- **L100:** `JsonNode v = e.getValue();` — Nested JSON value nikalta hai.
- **L101:** `if (v.isValueNode() && !v.isNull() && !v.asText().isBlank())` — Non-null, nonblank scalar value check.
- **L102:** `values.add(e.getKey().replace('_', ' ') + ": " + v.asText());` — `field key: value` format (underscores ko spaces) mein add.
- **L103:** `});` — Nested object iteration ka lambda close.
- **L104:** `}` — Is scope/condition/loop ko close karta hai.
- **L105:** `} else if (value.isValueNode() && !value.isNull() && !value.asText().isBlank()) {` — Scalar string/number/boolean field ka case (array/object nahi).
- **L106:** `values.add(value.asText());` — Scalar value ko text mein convert karke add.
- **L107:** `}` — Is scope/condition/loop ko close karta hai.
- **L108:** `if (!values.isEmpty()) {` — Values nonempty hai tabhi Beneficiary entry banata hai.
- **L109:** `String label = "who_can_apply".equals(f.getKey()) ? "Type of Beneficiary"` — who_can_apply ka display name "Type of Beneficiary" fixed.
- **L110:** `: java.util.Arrays.stream(f.getKey().split("_"))` — Baaki keys ko underscore par split karke readable label banata hai.
- **L111:** `.map(w -> w.substring(0, 1).toUpperCase(java.util.Locale.ROOT) + w.substring(1))` — Har word ka first letter uppercase karta hai.
- **L112:** `.collect(java.util.stream.Collectors.joining(" "));` — Words ko spaces se join karta hai; e.g. applicable_gender -> Applicable Gender.
- **L113:** `ServiceCatalogueDataDTO.Beneficiary b = new ServiceCatalogueDataDTO.Beneficiary();` — Original DTO ki default constructor se Beneficiary object banta hai.
- **L114:** `b.setAttribute(label);` — Display label set.
- **L115:** `b.setValues(values);` — Collected values list set.
- **L116:** `beneficiary.add(b);` — Beneficiary list mein entry add.
- **L117:** `}` — Is scope/condition/loop ko close karta hai.
- **L118:** `}` — Is scope/condition/loop ko close karta hai.
- **L119:** `}` — Is scope/condition/loop ko close karta hai.
- **L120:** `}` — Is scope/condition/loop ko close karta hai.
- **L121:** `}` — Is scope/condition/loop ko close karta hai.
- **L122:** `}` — Is scope/condition/loop ko close karta hai.

## Lines 123–134: Deliverable

- **L124:** `String deliverable = null;` — Deliverable initially null—agar template nahi mile toh null rahega.
- **L125:** `List<ServiceOutputFormat> outputs = serviceOutputFormatRepository.findAllDocumentByServiceId(serviceId);` — service_output_format se service ke document-output formats fetch.
- **L126:** `if (outputs != null) {` — Repository null return kare toh loop skip.
- **L127:** `for (ServiceOutputFormat output : outputs) {` — Har output format par iterate.
- **L128:** `if (output.getOutputFormatJson() != null` — output_format_json present hai kya?
- **L129:** `&& output.getOutputFormatJson().getTemplateName() != null) {` — templateName present hai kya?
- **L130:** `deliverable = output.getOutputFormatJson().getTemplateName();` — Pehle available templateName ko deliverable banata hai.
- **L131:** `break;  // New contract supports a single deliverable string` — First match ke baad stop—contract mein sirf ek deliverable string hai.
- **L132:** `}` — Is scope/condition/loop ko close karta hai.
- **L133:** `}` — Is scope/condition/loop ko close karta hai.
- **L134:** `}` — Is scope/condition/loop ko close karta hai.

## Lines 135–147: Identity DTO

- **L136:** `// Same DTO pattern as the original code: instantiate + setters.` — Response DTOs ko normal POJO setters se populate kar raha hai.
- **L137:** `ServiceCatalogueDataDTO response = new ServiceCatalogueDataDTO();` — Root DTO object banata hai.
- **L139:** `ServiceCatalogueDataDTO.ServiceIdentity identity = new ServiceCatalogueDataDTO.ServiceIdentity();` — Nested serviceIdentity object banata hai.
- **L140:** `identity.setTenantId(log == null ? null : log.getTenantId());` — tenant_id ko service_log.getTenantId se set; missing log ho toh null.
- **L141:** `identity.setBaseServiceId(sd.getBaseServiceId());` — Base service ID from service_definition.
- **L142:** `identity.setServiceId(sd.getServiceId());` — Service ID from service_definition.
- **L143:** `identity.setServiceName(def != null && def.getName() != null ? def.getName() : sd.getServiceName());` — Prefer definition_json.name; fallback service_definition.service_name.
- **L144:** `identity.setDepartment(department);` — Workflow/SDU se resolved department name set.
- **L145:** `identity.setState(state);` — Workflow/SDU se resolved state name set.
- **L146:** `identity.setStateId(sd.getStateId() == null ? null : String.valueOf(sd.getStateId()));` — stateId service_definition.clc_id se; 32 vs workflow 30 discrepancy unchanged.
- **L147:** `response.setServiceIdentity(identity);` — Identity object root response mein attach.

## Lines 148–159: Details, availedBy, modes

- **L149:** `ServiceCatalogueDataDTO.ServiceDetails details = new ServiceCatalogueDataDTO.ServiceDetails();` — Nested serviceDetails banata hai.
- **L150:** `details.setDescription(def == null ? null : def.getDescription());` — definition_json.description set; missing def par null.
- **L151:** `details.setSgdGoals(goals);` — SDG goal labels set; spelling is contract `sgdGoals`.
- **L152:** `ServiceCatalogueDataDTO.HowToApply how = new ServiceCatalogueDataDTO.HowToApply();` — Nested howToApply object banata hai.
- **L153:** `how.setMode(modes);` — Submission roles mode list mein set (Applicant/DEO/Official).
- **L154:** `details.setHowToApply(how);` — howToApply ko details mein attach.
- **L155:** `response.setServiceDetails(details);` — Details root response mein attach.
- **L157:** `response.setAvailedBy(availedBy);` — availedBy values root response mein set.
- **L158:** `response.setBeneficiary(beneficiary);` — beneficiary attribute/values entries root mein set.
- **L159:** `response.setApplyModes(modes);` — applyModes bhi same workflow roles se set—Online imply nahi karta.

## Lines 160–175: Submission, deliverable, documents and return

- **L161:** `// Business rule for submissionType not verified yet.` — submissionType ka rule verified nahi hai.
- **L162:** `response.setSubmission(new ServiceCatalogueDataDTO.Submission());` — Empty Submission object; getter ki submissionType field null rahegi.
- **L164:** `ServiceCatalogueDataDTO.WhatYouReceive what = new ServiceCatalogueDataDTO.WhatYouReceive();` — whatYouReceive object banata hai.
- **L165:** `what.setDeliverable(deliverable);` — Document template name set.
- **L166:** `what.setDeliveryModes(new ArrayList<>());` — deliveryModes source unknown, isliye [] return karta hai.
- **L167:** `response.setWhatYouReceive(what);` — whatYouReceive object root response mein attach.
- **L169:** `// Documents: senior confirmation pending.` — Documents business mapping senior se confirm honi hai.
- **L170:** `ServiceCatalogueDataDTO.Documents documents = new ServiceCatalogueDataDTO.Documents();` — Documents object banata hai.
- **L171:** `documents.setItems(new ArrayList<>());` — Items empty list [] set karta hai.
- **L172:** `response.setDocuments(documents);` — Documents object root mein attach.
- **L174:** `return response;` — Ready DTO response caller ko return karta hai.
- **L175:** `}` — Method ends.

## Points to remember

1. **State mismatch:** `stateId` comes from `service_definition.clc_id` (test value 32) while workflow `clcDetail.value` for Kerala was 30. Isko hardcode karke fix nahi karna; source decide karna padega.
2. **Roles vs online modes:** `Applicant`, `DEO`, `Official` workflow actor roles hain, not proof that mode = `Online`.
3. **Beneficiary:** Enabled fields dynamic hain; disabled category skip hogi. `required_documents` questionnaire answer documents upload specification nahi hai.
4. **Missing status:** `service_catalogue_configuration` mein `status` column missing tha, isliye `configurationRepository.findById()` ki jagah `findDataByConfigurationId()` selected JSON query chahiye.
5. **Null fields:** `serviceUrl`, `audience`, `applicantType`, `submissionType`, `deliveryModes`, `qrCode`, `documents.required` business-source confirmation pending.
6. **Existing output:** Service `700001`: 25 beneficiary entries, 4 `availedBy` entries; `490001`: 2 beneficiary entries, 1 `availedBy` entry.

## Source code snapshot (line numbered)

```java
  1     @Transactional(readOnly = true)
  2     public ServiceCatalogueDataDTO buildServiceCatalogueData(Integer serviceId) {
  3         if (serviceId == null) {
  4             throw new SPRuntimeError("Service id is required", HttpStatus.BAD_REQUEST);
  5         }
  6 
  7         ServiceDefinition sd = serviceDefinitionRepository.findById(serviceId)
  8                 .orElseThrow(() -> new SPRuntimeError("Service not found", HttpStatus.NOT_FOUND));
  9         ServiceDefinitionDTO def = sd.getDefinitionJson();
 10         ServiceLog log = serviceLogRepository.findByServiceId(serviceId);
 11 
 12         // Workflow: submission roles + department/state labels
 13         ServiceProcessFlow flow = processFlowRepository.findByServiceId(serviceId);
 14         ProcessFlowDTO.NodeDTO start = flow == null || flow.getProcessFlowJson() == null
 15                 || flow.getProcessFlowJson().getNodes() == null ? null
 16                 : flow.getProcessFlowJson().getNodes().stream()
 17                     .filter(n -> n != null && "submission".equals(n.getType()))
 18                     .findFirst().orElse(null);
 19 
 20         List<String> modes = new ArrayList<>();
 21         String department = null;
 22         String state = null;
 23         if (start != null && start.getData() != null) {
 24             if (start.getData().getMode() != null) {
 25                 modes = start.getData().getMode().stream()
 26                         .filter(m -> m != null && m.getLabel() != null)
 27                         .map(m -> m.getLabel()).distinct().toList();
 28             }
 29             if (start.getData().getEntities() != null) {
 30                 for (ProcessFlowDTO.Entities entity : start.getData().getEntities()) {
 31                     if (entity == null) continue;
 32                     if (department == null && entity.getEntity() != null)
 33                         department = entity.getEntity().getLabel();
 34                     if (state == null && entity.getClcDetail() != null)
 35                         state = entity.getClcDetail().getLabel();
 36                     if (department != null && state != null) break;
 37                 }
 38             }
 39         }
 40 
 41         // Fallback if no department/state is configured on the submission node
 42         if (department == null || state == null) {
 43             ServiceDeliveryUnitDefinition sdu = serviceDeliveryUnitDefinitionRepository.findByServiceId(serviceId);
 44             if (sdu != null && sdu.getServiceDeliveryUnits() != null) {
 45                 for (ServiceDeliveryUnitDTO.ServiceDeliveryUnitJSON unit : sdu.getServiceDeliveryUnits()) {
 46                     if (unit == null || unit.getServiceDeliveryUnit() == null) continue;
 47                     var hierarchy = unit.getServiceDeliveryUnit();
 48                     if (department == null && hierarchy.getEntity() != null)
 49                         department = hierarchy.getEntity().getLabel();
 50                     if (state == null && hierarchy.getClcDetail() != null)
 51                         state = hierarchy.getClcDetail().getLabel();
 52                     if (department != null && state != null) break;
 53                 }
 54             }
 55         }
 56 
 57         List<String> goals = def == null || def.getGoals() == null ? List.of()
 58                 : def.getGoals().stream()
 59                     .filter(g -> g != null && g.getLabel() != null)
 60                     .map(g -> g.getLabel()).toList();
 61 
 62         // Catalogue configuration: all enabled beneficiary fields, not just five
 63         List<String> availedBy = new ArrayList<>();
 64         List<ServiceCatalogueDataDTO.Beneficiary> beneficiary = new ArrayList<>();
 65         Optional<ServiceCatalogueServiceMapping> mapping = mappingRepository.findByServiceId(serviceId.longValue());
 66         if (mapping.isPresent()) {
 67             JsonNode configData = configurationRepository.findDataByConfigurationId(
 68                     mapping.get().getConfigurationId());
 69             if (configData != null) {
 70                 JsonNode profile = configData.path("beneficiaryProfile");
 71                 if (profile.path("enabled").asBoolean(false)) {
 72                     JsonNode answers = profile.path("answers");
 73                     JsonNode applicant = answers.path("applicant_type");
 74                     if (applicant.path("enabled").asBoolean(false)) {
 75                         for (JsonNode v : applicant.path("who_can_apply")) {
 76                             if (v.isTextual() && !v.asText().isBlank()) availedBy.add(v.asText());
 77                         }
 78                     }
 79                     for (java.util.Iterator<Map.Entry<String, JsonNode>> categories = answers.fields(); categories.hasNext();) {
 80                         JsonNode category = categories.next().getValue();
 81                         if (!category.path("enabled").asBoolean(false)) continue;
 82                         for (java.util.Iterator<Map.Entry<String, JsonNode>> fields = category.fields(); fields.hasNext();) {
 83                             Map.Entry<String, JsonNode> f = fields.next();
 84                             if ("enabled".equals(f.getKey())) continue;
 85                             List<String> values = new ArrayList<>();
 86                             JsonNode value = f.getValue();
 87                             if (value.isArray()) {
 88                                 value.forEach(v -> { if (v.isValueNode() && !v.asText().isBlank()) values.add(v.asText()); });
 89                             } else if (value.isObject()) {
 90                                 if ("age_range".equals(f.getKey())
 91                                         && value.hasNonNull("min_age") && value.hasNonNull("max_age")) {
 92                                     values.add(value.path("min_age").asText() + "-"
 93                                             + value.path("max_age").asText() + " yrs");
 94                                 } else if ("dob_range".equals(f.getKey())
 95                                         && value.hasNonNull("born_after") && value.hasNonNull("born_before")) {
 96                                     values.add(value.path("born_after").asText() + " to "
 97                                             + value.path("born_before").asText());
 98                                 } else {
 99                                     value.fields().forEachRemaining(e -> {
100                                         JsonNode v = e.getValue();
101                                         if (v.isValueNode() && !v.isNull() && !v.asText().isBlank())
102                                             values.add(e.getKey().replace('_', ' ') + ": " + v.asText());
103                                     });
104                                 }
105                             } else if (value.isValueNode() && !value.isNull() && !value.asText().isBlank()) {
106                                 values.add(value.asText());
107                             }
108                             if (!values.isEmpty()) {
109                                 String label = "who_can_apply".equals(f.getKey()) ? "Type of Beneficiary"
110                                         : java.util.Arrays.stream(f.getKey().split("_"))
111                                             .map(w -> w.substring(0, 1).toUpperCase(java.util.Locale.ROOT) + w.substring(1))
112                                             .collect(java.util.stream.Collectors.joining(" "));
113                                 ServiceCatalogueDataDTO.Beneficiary b = new ServiceCatalogueDataDTO.Beneficiary();
114                                 b.setAttribute(label);
115                                 b.setValues(values);
116                                 beneficiary.add(b);
117                             }
118                         }
119                     }
120                 }
121             }
122         }
123 
124         String deliverable = null;
125         List<ServiceOutputFormat> outputs = serviceOutputFormatRepository.findAllDocumentByServiceId(serviceId);
126         if (outputs != null) {
127             for (ServiceOutputFormat output : outputs) {
128                 if (output.getOutputFormatJson() != null
129                         && output.getOutputFormatJson().getTemplateName() != null) {
130                     deliverable = output.getOutputFormatJson().getTemplateName();
131                     break;  // New contract supports a single deliverable string
132                 }
133             }
134         }
135 
136         // Same DTO pattern as the original code: instantiate + setters.
137         ServiceCatalogueDataDTO response = new ServiceCatalogueDataDTO();
138 
139         ServiceCatalogueDataDTO.ServiceIdentity identity = new ServiceCatalogueDataDTO.ServiceIdentity();
140         identity.setTenantId(log == null ? null : log.getTenantId());
141         identity.setBaseServiceId(sd.getBaseServiceId());
142         identity.setServiceId(sd.getServiceId());
143         identity.setServiceName(def != null && def.getName() != null ? def.getName() : sd.getServiceName());
144         identity.setDepartment(department);
145         identity.setState(state);
146         identity.setStateId(sd.getStateId() == null ? null : String.valueOf(sd.getStateId()));
147         response.setServiceIdentity(identity);
148 
149         ServiceCatalogueDataDTO.ServiceDetails details = new ServiceCatalogueDataDTO.ServiceDetails();
150         details.setDescription(def == null ? null : def.getDescription());
151         details.setSgdGoals(goals);
152         ServiceCatalogueDataDTO.HowToApply how = new ServiceCatalogueDataDTO.HowToApply();
153         how.setMode(modes);
154         details.setHowToApply(how);
155         response.setServiceDetails(details);
156 
157         response.setAvailedBy(availedBy);
158         response.setBeneficiary(beneficiary);
159         response.setApplyModes(modes);
160 
161         // Business rule for submissionType not verified yet.
162         response.setSubmission(new ServiceCatalogueDataDTO.Submission());
163 
164         ServiceCatalogueDataDTO.WhatYouReceive what = new ServiceCatalogueDataDTO.WhatYouReceive();
165         what.setDeliverable(deliverable);
166         what.setDeliveryModes(new ArrayList<>());
167         response.setWhatYouReceive(what);
168 
169         // Documents: senior confirmation pending.
170         ServiceCatalogueDataDTO.Documents documents = new ServiceCatalogueDataDTO.Documents();
171         documents.setItems(new ArrayList<>());
172         response.setDocuments(documents);
173 
174         return response;
175     }
```