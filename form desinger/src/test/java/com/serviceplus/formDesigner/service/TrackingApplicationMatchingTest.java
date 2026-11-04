package com.serviceplus.formDesigner.service;

import com.serviceplus.formDesigner.controller.ApplicationDataController;
import com.serviceplus.formDesigner.repository.*;
import com.serviceplus.formDesigner.feignClient.ServiceMataDataFeignClient;
import com.serviceplus.formDesigner.returnToEdit.entity.FormDataReference;
import com.serviceplus.formDesigner.returnToEdit.repository.FormDataReferenceRepository;
import com.serviceplus.formDesigner.request.model.*;
import com.serviceplus.formDesigner.util.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.data.domain.*;
import org.springframework.test.util.ReflectionTestUtils;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

class TrackingApplicationMatchingTest {
    ApplicationDataRepository repo;
    ApplicationTemplateRepository templates;
    FormEncryptionKeyRepository keys;
    ApplicationDataCommonUtil crypto;
    ServiceMataDataFeignClient metadata;
    FormDataReferenceRepository references;
    ApplicationDataService service;
    ApplicationTemplate template;
    ApplicationData app;
    Map<String,Object> config;
    @BeforeEach void setup() {
        repo=mock(ApplicationDataRepository.class);templates=mock(ApplicationTemplateRepository.class);
        keys=mock(FormEncryptionKeyRepository.class);crypto=mock(ApplicationDataCommonUtil.class);metadata=mock(ServiceMataDataFeignClient.class);
        references=mock(FormDataReferenceRepository.class);
        service=new ApplicationDataService(repo,templates,null,null,null,keys,null,crypto,null,null,metadata,null,null,null,null,references);
        ReflectionTestUtils.setField(service,"trackingMaxScanRecords",10000);
        ReflectionTestUtils.setField(service,"trackingStrongAttributes","tenant:7:secret");
        ReflectionTestUtils.setField(service,"trackingInternalToken","test-internal-token");
        config=new HashMap<>();config.put("serviceId",7);config.put("trackingTypes",List.of("MOBILE_OTP","EMAIL_OTP","FORM_ATTRIBUTES"));
        config.put("mobileAttribute",field("mobile",1));config.put("emailAttribute",field("email",3));
        config.put("formAttributes",List.of(field("secret",1),field("dob",8),field("amount",2)));
        when(metadata.getPublicTrackingConfiguration("test-internal-token",7L,"tenant")).thenReturn(config);
        template=new ApplicationTemplate();template.setFormId("form");template.setTenantId("tenant");
        template.setFormJsonData(Map.of("globalSetting",Map.of(),"formElements",List.of(
            element("mobile",1),element("email",3),element("secret",1),element("dob",8),element("amount",2))));
        when(templates.findByFormId("form")).thenReturn(template);
        when(keys.findByFormIdAndServiceIdAndTaskId("form",7,"s_1")).thenReturn(Optional.empty());
        app=data("app");
        when(repo.findTrackingApplicationData("tenant",7,"app")).thenReturn(List.of(app));
        when(repo.findSubmittedTrackingApplications(eq("tenant"),eq(7),any())).thenAnswer(inv->new PageImpl<>(List.of(app),inv.getArgument(2),1));
    }
    Map<String,Object> field(String id,int type){return Map.of("attributeId",id,"typeId",type);}
    Map<String,Object> element(String id,int type){return Map.of("uniqueId",id,"typeId",type);}
    ApplicationData data(String id) {
        ApplicationData data=new ApplicationData();data.setApplId(id);data.setDataId("data-"+id);data.setServiceId(7);
        data.setTenantId("tenant");data.setTaskId("s_1");data.setFormId("form");
        data.setFormData(new HashMap<>(Map.of("mobile","+91 99999-00000","email","SYNTHETIC@example.invalid",
            "secret","ABCD  efgh123456","dob","2001-02-03","amount","001.00")));
        return data;
    }
    Map<String,Object> lookup(String method) {return new HashMap<>(Map.of("tenantId","tenant","serviceId",7,"method",method));}
    Map<String,Object> attributes(){Map<String,Object> request=lookup("FORM_ATTRIBUTES");request.put("values",Map.of("secret"," abcd EFGH123456 ","dob","03/02/2001","amount",1));return request;}
    @ParameterizedTest @CsvSource({"MOBILE_OTP,919999900000","EMAIL_OTP,synthetic@example.invalid"})
    void contactsResolveOnlyMatchingSubmittedApplications(String method,String contact) {
        Map<String,Object> body=lookup(method);body.put("contact",contact);
        Map<String,Object> result=service.matchTrackingApplications(body);
        assertEquals(List.of("app"),result.get("applicationIds"));assertFalse(result.containsKey("attributeValues"));
    }
    @Test void attributesUseAllConfiguredFieldsAndTypes(){assertEquals(List.of("app"),service.matchTrackingApplications(attributes()).get("applicationIds"));}
    @Test void missingContactDoesNotMatch(){Map<String,Object> body=lookup("EMAIL_OTP");body.put("contact","other@example.invalid");assertEquals(List.of(),service.matchTrackingApplications(body).get("applicationIds"));}
    @Test void weakAttributesNeverAuthorizeOrEnumerate(){ReflectionTestUtils.setField(service,"trackingStrongAttributes","");Map<String,Object> result=service.matchTrackingApplications(attributes());assertEquals(false,result.get("sufficient"));verify(repo,never()).findSubmittedTrackingApplications(any(),any(),any());}
    @Test void incorrectAttributesDoNotAuthorize(){Map<String,Object> body=attributes();body.put("values",Map.of("secret","wrong","dob","2001-02-03","amount",1));assertEquals(false,service.matchTrackingApplications(body).get("sufficient"));}
    @Test void extraAttributesRejected(){Map<String,Object> body=attributes();body.put("values",Map.of("secret","a","dob","2001-02-03","amount",1,"injected","x"));assertThrows(IllegalArgumentException.class,()->service.matchTrackingApplications(body));}
    @Test void missingAttributesRejected(){Map<String,Object> body=attributes();body.put("values",Map.of("secret","a","dob","2001-02-03"));assertThrows(IllegalArgumentException.class,()->service.matchTrackingApplications(body));}
    @Test void duplicateApplicationMatchesRequireAdditionalVerification(){ApplicationData other=data("other");when(repo.findTrackingApplicationData("tenant",7,"other")).thenReturn(List.of(other));when(repo.findSubmittedTrackingApplications(eq("tenant"),eq(7),any())).thenAnswer(i->new PageImpl<>(List.of(app,other),i.getArgument(2),2));assertEquals(false,service.matchTrackingApplications(attributes()).get("sufficient"));}
    @Test void contactMayOwnMultipleApplications(){ApplicationData other=data("other");when(repo.findTrackingApplicationData("tenant",7,"other")).thenReturn(List.of(other));when(repo.findSubmittedTrackingApplications(eq("tenant"),eq(7),any())).thenAnswer(i->new PageImpl<>(List.of(app,other),i.getArgument(2),2));Map<String,Object> body=lookup("EMAIL_OTP");body.put("contact","synthetic@example.invalid");assertEquals(List.of("app","other"),service.matchTrackingApplications(body).get("applicationIds"));}
    @Test void duplicateSubmissionDataFailsClosed(){when(repo.findTrackingApplicationData("tenant",7,"app")).thenReturn(List.of(app,app));assertThrows(IllegalStateException.class,()->service.matchTrackingApplications(attributes()));}
    @Test void wrongTenantRejected(){app.setTenantId("other");assertThrows(IllegalStateException.class,()->service.matchTrackingApplications(attributes()));}
    @Test void wrongServiceRejected(){app.setServiceId(9);assertThrows(IllegalStateException.class,()->service.matchTrackingApplications(attributes()));}
    @Test void wrongFormTenantRejected(){template.setTenantId("other");assertThrows(IllegalStateException.class,()->service.matchTrackingApplications(attributes()));}
    @Test void modifiedFieldTypeNeverMatches(){config.put("formAttributes",List.of(field("secret",2),field("dob",8),field("amount",2)));Map<String,Object> body=attributes();body.put("values",Map.of("secret",1,"dob","2001-02-03","amount",1));assertEquals(false,service.matchTrackingApplications(body).get("sufficient"));}
    @Test void disabledMethodRejected(){config.put("trackingTypes",List.of("MOBILE_OTP"));assertThrows(IllegalArgumentException.class,()->service.matchTrackingApplications(attributes()));}
    @Test void unknownServiceFailsClosed(){Map<String,Object> body=attributes();body.put("serviceId",99);assertThrows(IllegalArgumentException.class,()->service.matchTrackingApplications(body));}
    @Test void metadataFailureFailsClosed(){when(metadata.getPublicTrackingConfiguration(7L,"tenant")).thenThrow(new IllegalStateException());assertThrows(IllegalStateException.class,()->service.matchTrackingApplications(attributes()));}
    @Test void scanLimitNeverReturnsPartialMatches(){ReflectionTestUtils.setField(service,"trackingMaxScanRecords",0);assertThrows(IllegalStateException.class,()->service.matchTrackingApplications(attributes()));}
    @Test void encryptedDataUsesExistingDecryptorOnCopy() throws Exception {
        template.setEncryptionKeyAtRest("synthetic-key.iv");template.setFormJsonData(Map.of("globalSetting",Map.of("isEncryptionDataAtRest",true),"formElements",List.of()));
        ApplicationData result=service.getTrackingApplicationData("tenant",7,"app");
        assertNotSame(app,result);verify(crypto).decryptApplicationAttributeData(eq(template),eq(result),eq("synthetic-key.iv"),eq("form"),eq(false));
    }
    @Test void encryptionFailureFailsClosed() throws Exception {
        template.setEncryptionKeyAtRest("synthetic-key.iv");template.setFormJsonData(Map.of("globalSetting",Map.of("isEncryptionDataAtRest",true),"formElements",List.of()));
        doThrow(new IllegalStateException()).when(crypto).decryptApplicationAttributeData(any(),any(),any(),any(),any());
        assertThrows(IllegalStateException.class,()->service.getTrackingApplicationData("tenant",7,"app"));
    }
    @ParameterizedTest @CsvSource({"1,'  Alpha   BETA  ',alpha beta","2,001.000,1","8,03/02/2001,2001-02-03","3,TEST@example.invalid,test@example.invalid","40,Code,Code"})
    void normalization(int type,String input,String expected){assertEquals(expected,ApplicationDataService.normalizeTrackingValue(input,type));}
    @Test void invalidDatesNullBlankAndComplexValuesRejected(){assertNull(ApplicationDataService.normalizeTrackingValue("31/02/2001",8));assertNull(ApplicationDataService.normalizeTrackingValue(" ",1));assertNull(ApplicationDataService.normalizeTrackingValue(null,1));assertNull(ApplicationDataService.normalizeTrackingValue(Map.of("x","y"),1));assertNull(ApplicationDataService.normalizeTrackingValue("doc",45));}

    @Test void configuredNumericMobileFieldResolvesAuthoritativeNumber() {
        config.put("mobileAttribute",field("mobile",2));
        template.setFormJsonData(Map.of("globalSetting",Map.of(),"formElements",List.of(element("mobile",2))));
        app.setFormData(Map.of("mobile",new java.math.BigDecimal("919999900000.0")));
        Map<String,Object> body=lookup("MOBILE_OTP");body.put("contact","+91 99999 00000");
        assertEquals(List.of("app"),service.matchTrackingApplications(body).get("applicationIds"));
    }
    @Test void encryptedSubmittedContactIsMatchedUsingRealExistingCipher() throws Exception {
        String key="abcdefghijklmnop",iv="qrstuvwxyzABCDEF";
        String encrypted=CommonUtil.encryptClientData("synthetic@example.invalid",key,iv);
        app.setFormData(Map.of("email",encrypted));template.setEncryptionKeyAtRest(key+"."+iv);
        template.setFormJsonData(Map.of("globalSetting",Map.of("isEncryptionDataAtRest",true),"formElements",List.of(element("email",3))));
        FormRedisService cache=mock(FormRedisService.class);
        com.serviceplus.formDesigner.dto.FormProcessCacheDTO cached=new com.serviceplus.formDesigner.dto.FormProcessCacheDTO();
        cached.setAttributes(List.of(new com.serviceplus.formDesigner.dto.FormProcessCacheDTO.Attribute("email",null,"Email",3,"email",true,null,null,null,null,null,null,null,List.of(),true)));
        when(cache.dataExists("form")).thenReturn(true);when(cache.getDataFromRedis("form")).thenReturn(cached);
        ApplicationDataCommonUtil actualCrypto=new ApplicationDataCommonUtil();ReflectionTestUtils.setField(actualCrypto,"formRedisService",cache);
        ReflectionTestUtils.setField(service,"applicationDataCommonUtil",actualCrypto);
        Map<String,Object> body=lookup("EMAIL_OTP");body.put("contact","synthetic@example.invalid");
        assertEquals(List.of("app"),service.matchTrackingApplications(body).get("applicationIds"));
        assertEquals(encrypted,((Map<?,?>)app.getFormData()).get("email"));
    }

    @Test void configuredCheckboxCodesMatchAsExactUnorderedSets() {
        assertEquals(ApplicationDataService.normalizeTrackingValue(List.of("B","A"),7),ApplicationDataService.normalizeTrackingValue(List.of("A","B"),7));
        assertNull(ApplicationDataService.normalizeTrackingValue(List.of("A","A"),7));assertNull(ApplicationDataService.normalizeTrackingValue(List.of(),7));
        assertNotEquals(ApplicationDataService.normalizeTrackingValue(List.of("A","B"),7),ApplicationDataService.normalizeTrackingValue(List.of("a","B"),7));
    }
    @Test void richTextMatchesVisibleNormalizedContentOnly() {
        assertEquals("alpha beta & gamma",ApplicationDataService.normalizeTrackingValue("<p>Alpha <b>Beta</b> &amp; Gamma</p>",46));
        assertEquals("alpha beta",ApplicationDataService.normalizeTrackingValue("<p>Alpha</p><p>Beta</p>",46));
    }

    @Test void mismatchedMetadataScopeFailsClosed(){config.put("serviceId",8);assertThrows(IllegalArgumentException.class,()->service.matchTrackingApplications(attributes()));}
    @Test void bothTrackingOperationsRequireInternalAuthentication(){ApplicationDataService service=mock(ApplicationDataService.class);ApplicationDataController controller=new ApplicationDataController();ReflectionTestUtils.setField(controller,"applicationDataService",service);ReflectionTestUtils.setField(controller,"trackingInternalToken","synthetic-token");assertEquals(403,controller.matchTrackingApplications(Map.of(),null).getStatusCode().value());assertEquals(403,controller.getTrackingApplicationData(Map.of(),"wrong").getStatusCode().value());verifyNoInteractions(service);}

    @Test
    void strongFieldPolicyMustMatchTenantServiceAndField() {
        ReflectionTestUtils.setField(service, "trackingStrongAttributes", "other:7:secret;tenant:8:secret");
        Map<String, Object> result = service.matchTrackingApplications(attributes());
        assertEquals(false, result.get("sufficient"));
        verify(repo, never()).findSubmittedTrackingApplications(any(), any(), any());
    }

    @Test
    void multipleStrongFieldPolicyEntriesAreSupported() {
        ReflectionTestUtils.setField(service, "trackingStrongAttributes", "other:7:secret;tenant:7:secret");
        assertEquals(List.of("app"), service.matchTrackingApplications(attributes()).get("applicationIds"));
    }

    @Test
    void sharedFormRequiresExactSubmittedReference() {
        template.setTenantId("form-owner");
        FormDataReference ref = new FormDataReference();
        ref.setTenantId("tenant");
        ref.setServiceId(7);
        ref.setApplicationId("app");
        ref.setDataId("data-app");
        ref.setFormId("form");
        ref.setTaskId("s_1");
        when(references.findByServiceIdAndApplicationId(7, "app")).thenReturn(List.of(ref));
        assertNotNull(service.getTrackingApplicationData("tenant", 7, "app"));
    }

    @Test
    void incorrectSharedFormReferenceIsRejected() {
        template.setTenantId("form-owner");
        FormDataReference ref = new FormDataReference();
        ref.setTenantId("other-tenant");
        ref.setServiceId(7);
        ref.setApplicationId("app");
        ref.setDataId("data-app");
        ref.setFormId("form");
        ref.setTaskId("s_1");
        when(references.findByServiceIdAndApplicationId(7, "app")).thenReturn(List.of(ref));
        assertThrows(IllegalStateException.class, () -> service.getTrackingApplicationData("tenant", 7, "app"));
    }

    @Test
    void unreadableSubmissionMustNeverProducePartialPublicMatches() {
        ApplicationData corrupted = data("corrupted");
        when(repo.findTrackingApplicationData("tenant", 7, "corrupted")).thenReturn(List.of(corrupted, corrupted));
        when(repo.findSubmittedTrackingApplications(eq("tenant"), eq(7), any()))
                .thenAnswer(inv -> new PageImpl<>(List.of(app, corrupted), inv.getArgument(2), 2));
        assertThrows(IllegalStateException.class, () -> service.matchTrackingApplications(attributes()));
    }

    @Test
    void contactMatchesAlsoFailClosedIfSearchIsIncomplete() {
        ApplicationData corrupted = data("corrupted");
        when(repo.findTrackingApplicationData("tenant", 7, "corrupted")).thenReturn(List.of(corrupted, corrupted));
        when(repo.findSubmittedTrackingApplications(eq("tenant"), eq(7), any()))
                .thenAnswer(inv -> new PageImpl<>(List.of(app, corrupted), inv.getArgument(2), 2));
        Map<String, Object> body = lookup("EMAIL_OTP");
        body.put("contact", "synthetic@example.invalid");
        assertThrows(IllegalStateException.class, () -> service.matchTrackingApplications(body));
    }
}
