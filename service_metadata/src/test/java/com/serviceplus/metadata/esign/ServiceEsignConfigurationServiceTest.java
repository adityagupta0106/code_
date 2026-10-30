package com.serviceplus.metadata.esign;

import com.serviceplus.metadata.dto.UserSessionDTO;
import com.serviceplus.metadata.entity.ServiceLog;
import com.serviceplus.metadata.entity.ServiceTemplate;
import com.serviceplus.metadata.esign.dto.ServiceEsignConfigurationDTO;
import com.serviceplus.metadata.esign.dto.ServiceEsignToggleDTO;
import com.serviceplus.metadata.esign.entity.EsignTemplate;
import com.serviceplus.metadata.esign.entity.ServiceEsignConfiguration;
import com.serviceplus.metadata.esign.enums.EsignTemplateStatus;
import com.serviceplus.metadata.esign.repository.EsignTemplateRepository;
import com.serviceplus.metadata.esign.repository.ServiceEsignConfigurationRepository;
import com.serviceplus.metadata.esign.service.impl.ServiceEsignConfigurationServiceImpl;
import com.serviceplus.metadata.exception.SPRuntimeError;
import com.serviceplus.metadata.feignClient.FormDesignerFeignClient;
import com.serviceplus.metadata.repository.IServiceLogRepository;
import com.serviceplus.metadata.repository.IServiceTemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ServiceEsignConfigurationServiceTest {

//    @Mock
//    private ServiceEsignConfigurationRepository serviceEsignRepository;
//
//    @Mock
//    private EsignTemplateRepository templateRepository;
//
//    @Mock
//    private IServiceLogRepository serviceLogRepository;
//
//    @Mock
//    private IServiceTemplateRepository serviceTemplateRepository;
//
//    @Mock
//    private FormDesignerFeignClient formDesignerFeignClient;
//
//    @InjectMocks
//    private ServiceEsignConfigurationServiceImpl service;
//
//    private UserSessionDTO validUser;
//    private ServiceLog validServiceLog;
//    private EsignTemplate activeTemplate;
//    private ServiceTemplate validTaskTemplate;
//
//    @BeforeEach
//    void setUp() {
//        validUser = new UserSessionDTO();
//        validUser.setUserID(2001L);
//        validUser.setTenantId("state_up");
//
//        validServiceLog = new ServiceLog();
//        validServiceLog.setServiceId(501);
//        validServiceLog.setTenantId("state_up");
//
//        activeTemplate = new EsignTemplate();
//        activeTemplate.setId(10L);
//        activeTemplate.setTemplateName("State Active eSign");
//        activeTemplate.setTenantId("state_up");
//        activeTemplate.setStatus(EsignTemplateStatus.ACTIVE);
//
//        validTaskTemplate = new ServiceTemplate();
//        validTaskTemplate.setServiceId(501);
//        validTaskTemplate.setTaskId("task_app_01");
//        validTaskTemplate.setTemplateName("Application Form Task");
//        validTaskTemplate.setHolderId("holder_1");
//    }
//
//    @Test
//    @DisplayName("Should successfully save valid configuration with single active template")
//    void testSaveValidConfiguration() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//        when(templateRepository.findById(10L)).thenReturn(Optional.of(activeTemplate));
//        when(serviceEsignRepository.findByServiceIdAndTenantId(501, "state_up")).thenReturn(Optional.empty());
//        when(serviceEsignRepository.save(any(ServiceEsignConfiguration.class))).thenAnswer(invocation -> {
//            ServiceEsignConfiguration c = invocation.getArgument(0);
//            c.setId(100L);
//            return c;
//        });
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setPrintSignerName(true);
//        request.setBiometricEnabled(false);
//        request.setShareAadhaar(false);
//        request.setVerifySignerName(false);
//
//        ServiceEsignConfigurationDTO response = service.saveConfiguration(request, validUser);
//
//        assertNotNull(response);
//        assertEquals(100L, response.getId());
//        assertEquals(501, response.getServiceId());
//        assertTrue(response.getEsignEnabled());
//        assertEquals(10L, response.getEsignTemplateId());
//        assertEquals("State Active eSign", response.getEsignTemplateName());
//        assertTrue(response.getPrintSignerName());
//        assertFalse(response.getBiometricEnabled());
//    }
//
//    @Test
//    @DisplayName("Should reject configuration when eSign is enabled but template is missing")
//    void testRejectMissingTemplate() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(null);
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.BAD_REQUEST, error.getErrorCode());
//        assertTrue(error.getMessage().contains("template must be selected"));
//    }
//
//    @Test
//    @DisplayName("Should reject configuration when template is INACTIVE")
//    void testRejectInactiveTemplate() {
//        EsignTemplate inactiveTemplate = new EsignTemplate();
//        inactiveTemplate.setId(12L);
//        inactiveTemplate.setTemplateName("Inactive Template");
//        inactiveTemplate.setStatus(EsignTemplateStatus.INACTIVE);
//        inactiveTemplate.setTenantId("state_up");
//
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(12L, "state_up")).thenReturn(Optional.of(inactiveTemplate));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(12L);
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.BAD_REQUEST, error.getErrorCode());
//        assertTrue(error.getMessage().contains("INACTIVE"));
//    }
//
//    @Test
//    @DisplayName("Should reject configuration when template belongs to another tenant")
//    void testRejectTemplateAnotherTenant() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(99L, "state_up")).thenReturn(Optional.empty());
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(99L);
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.NOT_FOUND, error.getErrorCode());
//    }
//
//    @Test
//    @DisplayName("Should save biometric-enabled configuration with valid modes and types")
//    void testSaveBiometricConfigurationSuccess() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//        when(serviceEsignRepository.findByServiceIdAndTenantId(501, "state_up")).thenReturn(Optional.empty());
//        when(serviceEsignRepository.save(any(ServiceEsignConfiguration.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setBiometricEnabled(true);
//        request.setSubmissionModes(Arrays.asList("DEO", "ONLINE"));
//        request.setBiometricTypes(Collections.singletonList("FINGERPRINT"));
//        request.setShareAadhaar(false);
//
//        ServiceEsignConfigurationDTO response = service.saveConfiguration(request, validUser);
//
//        assertNotNull(response);
//        assertTrue(response.getBiometricEnabled());
//        assertEquals(2, response.getSubmissionModes().size());
//        assertTrue(response.getSubmissionModes().contains("DEO"));
//        assertTrue(response.getBiometricTypes().contains("FINGERPRINT"));
//    }
//
//    @Test
//    @DisplayName("Should reject biometric-enabled configuration without submission modes")
//    void testRejectBiometricWithoutSubmissionModes() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setBiometricEnabled(true);
//        request.setSubmissionModes(Collections.emptyList());
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.BAD_REQUEST, error.getErrorCode());
//        assertTrue(error.getMessage().contains("Submission modes are mandatory"));
//    }
//
//    @Test
//    @DisplayName("Should reject invalid submission mode enum")
//    void testRejectInvalidSubmissionMode() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setBiometricEnabled(true);
//        request.setSubmissionModes(Collections.singletonList("INVALID_MODE_XYZ"));
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.BAD_REQUEST, error.getErrorCode());
//        assertTrue(error.getMessage().contains("Invalid submission mode"));
//    }
//
//    @Test
//    @DisplayName("Should require identified user group when IDENTIFIED_USER_SET is selected")
//    void testRequireIdentifiedUserGroup() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setBiometricEnabled(true);
//        request.setSubmissionModes(Collections.singletonList("IDENTIFIED_USER_SET"));
//        request.setIdentifiedUserGroupTypes(Collections.emptyList()); // Empty!
//        request.setBiometricTypes(Collections.singletonList("FINGERPRINT"));
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.BAD_REQUEST, error.getErrorCode());
//        assertTrue(error.getMessage().contains("identified user group type"));
//    }
//
//    @Test
//    @DisplayName("Should accept valid identified user group when IDENTIFIED_USER_SET is selected")
//    void testAcceptIdentifiedUserGroup() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//        when(serviceEsignRepository.findByServiceIdAndTenantId(501, "state_up")).thenReturn(Optional.empty());
//        when(serviceEsignRepository.save(any(ServiceEsignConfiguration.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setBiometricEnabled(true);
//        request.setSubmissionModes(Collections.singletonList("IDENTIFIED_USER_SET"));
//        request.setIdentifiedUserGroupTypes(Collections.singletonList("CSC"));
//        request.setBiometricTypes(Arrays.asList("FINGERPRINT", "RETINA_SCANNING"));
//
//        ServiceEsignConfigurationDTO response = service.saveConfiguration(request, validUser);
//        assertNotNull(response);
//        assertTrue(response.getIdentifiedUserGroupTypes().contains("CSC"));
//        assertEquals(2, response.getBiometricTypes().size());
//    }
//
//    @Test
//    @DisplayName("Should require Aadhaar task and field when shareAadhaar is enabled")
//    void testRequireAadhaarFieldMapping() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setShareAadhaar(true);
//        request.setAadhaarTaskId(null); // Missing!
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.BAD_REQUEST, error.getErrorCode());
//        assertTrue(error.getMessage().contains("Aadhaar task selection is mandatory"));
//    }
//
//    @Test
//    @DisplayName("Should reject Aadhaar task that does not belong to the service")
//    void testRejectAadhaarTaskAnotherService() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//        when(serviceTemplateRepository.findByServiceId(501)).thenReturn(Collections.singletonList(validTaskTemplate));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setShareAadhaar(true);
//        request.setAadhaarTaskId("unrelated_foreign_task");
//        request.setAadhaarFieldId("foreign_task$holder$field");
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.BAD_REQUEST, error.getErrorCode());
//        assertTrue(error.getMessage().contains("does not belong to service"));
//    }
//
//    @Test
//    @DisplayName("Should save configuration when shareAadhaar is true with valid task and field")
//    void testSaveAadhaarFieldMappingSuccess() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//        when(serviceTemplateRepository.findByServiceId(501)).thenReturn(Collections.singletonList(validTaskTemplate));
//        when(serviceEsignRepository.findByServiceIdAndTenantId(501, "state_up")).thenReturn(Optional.empty());
//        when(serviceEsignRepository.save(any(ServiceEsignConfiguration.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(true);
//        request.setEsignTemplateId(10L);
//        request.setShareAadhaar(true);
//        request.setAadhaarTaskId("task_app_01");
//        request.setAadhaarFieldId("task_app_01$holder_1$aadhaar_no");
//
//        ServiceEsignConfigurationDTO response = service.saveConfiguration(request, validUser);
//        assertNotNull(response);
//        assertTrue(response.getShareAadhaar());
//        assertEquals("task_app_01", response.getAadhaarTaskId());
//        assertEquals("task_app_01$holder_1$aadhaar_no", response.getAadhaarFieldId());
//    }
//
//    @Test
//    @DisplayName("Should clear conditional fields when eSign is disabled")
//    void testClearConditionalFieldsWhenDisabled() {
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(serviceEsignRepository.findByServiceIdAndTenantId(501, "state_up")).thenReturn(Optional.empty());
//        when(serviceEsignRepository.save(any(ServiceEsignConfiguration.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(501);
//        request.setEsignEnabled(false);
//        request.setBiometricEnabled(true); // Should be cleared!
//        request.setSubmissionModes(Collections.singletonList("DEO"));
//        request.setShareAadhaar(true);
//
//        ServiceEsignConfigurationDTO response = service.saveConfiguration(request, validUser);
//        assertNotNull(response);
//        assertFalse(response.getEsignEnabled());
//        assertFalse(response.getBiometricEnabled());
//        assertFalse(response.getShareAadhaar());
//        assertTrue(response.getSubmissionModes().isEmpty());
//    }
//
//    @Test
//    @DisplayName("Should toggle eSign successfully if template is active")
//    void testToggleEsignSuccess() {
//        ServiceEsignConfiguration config = new ServiceEsignConfiguration();
//        config.setId(101L);
//        config.setServiceId(501);
//        config.setTenantId("state_up");
//        config.setEsignEnabled(false);
//        config.setEsignTemplateId(10L);
//
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(serviceEsignRepository.findByServiceIdAndTenantId(501, "state_up")).thenReturn(Optional.of(config));
//        when(templateRepository.findByIdAndTenantId(10L, "state_up")).thenReturn(Optional.of(activeTemplate));
//        when(serviceEsignRepository.save(any(ServiceEsignConfiguration.class))).thenAnswer(invocation -> invocation.getArgument(0));
//
//        ServiceEsignToggleDTO toggle = new ServiceEsignToggleDTO();
//        toggle.setServiceId(501);
//        toggle.setEsignEnabled(true);
//
//        ServiceEsignConfigurationDTO response = service.toggleEsign(toggle, validUser);
//        assertNotNull(response);
//        assertTrue(response.getEsignEnabled());
//    }
//
//    @Test
//    @DisplayName("Should reject toggle enabling if template is inactive")
//    void testToggleEsignInactiveTemplateFails() {
//        EsignTemplate inactive = new EsignTemplate();
//        inactive.setId(20L);
//        inactive.setStatus(EsignTemplateStatus.INACTIVE);
//
//        ServiceEsignConfiguration config = new ServiceEsignConfiguration();
//        config.setServiceId(501);
//        config.setTenantId("state_up");
//        config.setEsignTemplateId(20L);
//
//        when(serviceLogRepository.findByServiceId(501)).thenReturn(validServiceLog);
//        when(serviceEsignRepository.findByServiceIdAndTenantId(501, "state_up")).thenReturn(Optional.of(config));
//        when(templateRepository.findByIdAndTenantId(20L, "state_up")).thenReturn(Optional.of(inactive));
//
//        ServiceEsignToggleDTO toggle = new ServiceEsignToggleDTO();
//        toggle.setServiceId(501);
//        toggle.setEsignEnabled(true);
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.toggleEsign(toggle, validUser));
//        assertEquals(HttpStatus.BAD_REQUEST, error.getErrorCode());
//        assertTrue(error.getMessage().contains("INACTIVE"));
//    }
//
//    @Test
//    @DisplayName("Should enforce tenant isolation and block cross-tenant service configuration access")
//    void testCrossTenantServiceAccessDenied() {
//        ServiceLog foreignServiceLog = new ServiceLog();
//        foreignServiceLog.setServiceId(999);
//        foreignServiceLog.setTenantId("state_kerala");
//
//        when(serviceLogRepository.findByServiceId(999)).thenReturn(foreignServiceLog);
//
//        ServiceEsignConfigurationDTO request = new ServiceEsignConfigurationDTO();
//        request.setServiceId(999);
//        request.setEsignEnabled(false);
//
//        SPRuntimeError error = assertThrows(SPRuntimeError.class, () -> service.saveConfiguration(request, validUser));
//        assertEquals(HttpStatus.FORBIDDEN, error.getErrorCode());
//        assertTrue(error.getMessage().contains("Access denied"));
//    }
}
