//package com.serviceplus.metadata.departmentConfig;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.gson.Gson;
//import com.serviceplus.metadata.departmentConfig.dto.*;
//import com.serviceplus.metadata.departmentConfig.service.BsaConfigurationService;
//import com.serviceplus.metadata.dto.UserSessionDTO;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.eq;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@ExtendWith(MockitoExtension.class)
//public class BSAConfigurationControllerTest {
//
//    private MockMvc mockMvc;
//
//    @Mock
//    private BsaConfigurationService configurationService;
//
//    @InjectMocks
//    private BSAConfigurationController controller;
//
//    private ObjectMapper objectMapper;
//    private String userDetailsHeaderJson;
//
//    @BeforeEach
//    void setUp() {
//        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
//        objectMapper = new ObjectMapper();
//
//        UserSessionDTO session = new UserSessionDTO();
//        session.setUserID(1001L);
//        session.setUserName("dept_admin");
//        session.setEntityId(450);
//        session.setLocationId(10);
//        session.setTenantId("SP_TENANT_TEST");
//        session.setEmailId("admin@sp.gov.in");
//        session.setTopLevelAdmin(false);
//
//        userDetailsHeaderJson = new Gson().toJson(session);
//    }
//
//    @Test
//    @DisplayName("GET /spgateway/bsa/configuration - Successful retrieval")
//    void testGetConfiguration_Success() throws Exception {
//        BsaConfigurationResponseDTO responseDTO = new BsaConfigurationResponseDTO();
//        responseDTO.setDepartmentId(450L);
//        responseDTO.setTenantId("SP_TENANT_TEST");
//        responseDTO.setConfigured(true);
//        responseDTO.setServiceCode("BSA-SVC-450");
//        responseDTO.setMaskedPassword("********");
//        responseDTO.setRegistrationStatus("REGISTERED");
//
//        when(configurationService.getConfiguration(any(), eq(450L))).thenReturn(responseDTO);
//
//        mockMvc.perform(get("/spgateway/bsa/configuration")
//                        .header("USER-DETAILS", userDetailsHeaderJson)
//                        .param("departmentId", "450"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.departmentId").value(450))
//                .andExpect(jsonPath("$.serviceCode").value("BSA-SVC-450"))
//                .andExpect(jsonPath("$.maskedPassword").value("********"))
//                .andExpect(jsonPath("$.registrationStatus").value("REGISTERED"));
//    }
//
//    @Test
//    @DisplayName("GET /spgateway/bsa/configuration - Unauthorized when USER-DETAILS header is missing")
//    void testGetConfiguration_MissingHeader() throws Exception {
//        mockMvc.perform(get("/spgateway/bsa/configuration"))
//                .andExpect(status().isUnauthorized());
//    }
//
//    @Test
//    @DisplayName("GET /spgateway/bsa/configuration - Forbidden when accessing unauthorized department")
//    void testGetConfiguration_Forbidden() throws Exception {
//        when(configurationService.getConfiguration(any(), eq(999L)))
//                .thenThrow(new SecurityException("Access denied for department 999"));
//
//        mockMvc.perform(get("/spgateway/bsa/configuration")
//                        .header("USER-DETAILS", userDetailsHeaderJson)
//                        .param("departmentId", "999"))
//                .andExpect(status().isForbidden())
//                .andExpect(jsonPath("$.status").value(403))
//                .andExpect(jsonPath("$.error").value("Access denied for department 999"));
//    }
//
//    @Test
//    @DisplayName("POST /spgateway/bsa/configuration/generate - Successful registration")
//    void testGenerateConfiguration_Success() throws Exception {
//        BsaGenerateConfigurationRequestDTO requestDTO = new BsaGenerateConfigurationRequestDTO();
//        requestDTO.setDepartmentId(450L);
//        requestDTO.setDepartmentName("Department of Transport");
//
//        BsaGenerateConfigurationResponseDTO responseDTO = new BsaGenerateConfigurationResponseDTO();
//        responseDTO.setSuccess(true);
//        responseDTO.setDepartmentId(450L);
//        responseDTO.setServiceCode("BSA-SVC-450");
//        responseDTO.setRegistrationStatus("REGISTERED");
//
//        when(configurationService.generateConfiguration(any()))
//                .thenReturn(responseDTO);
//
//        mockMvc.perform(post("/spgateway/bsa/configuration/generate")
//                        .header("USER-DETAILS", userDetailsHeaderJson)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.serviceCode").value("BSA-SVC-450"));
//    }
//
//    @Test
//    @DisplayName("POST /spgateway/bsa/configuration/generate - Conflict on duplicate registration")
//    void testGenerateConfiguration_ConflictOnDuplicate() throws Exception {
//        BsaGenerateConfigurationRequestDTO requestDTO = new BsaGenerateConfigurationRequestDTO();
//        requestDTO.setDepartmentId(450L);
//
//        BsaGenerateConfigurationResponseDTO responseDTO = new BsaGenerateConfigurationResponseDTO();
//        responseDTO.setSuccess(false);
//        responseDTO.setDepartmentId(450L);
//        responseDTO.setServiceCode("BSA-SVC-450");
//        responseDTO.setMessage("Department is already registered with BSA. Service Code: BSA-SVC-450");
//
//        when(configurationService.generateConfiguration(any()))
//                .thenReturn(responseDTO);
//
//        mockMvc.perform(post("/spgateway/bsa/configuration/generate")
//                        .header("USER-DETAILS", userDetailsHeaderJson)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isConflict())
//                .andExpect(jsonPath("$.success").value(false))
//                .andExpect(jsonPath("$.message").value("Department is already registered with BSA. Service Code: BSA-SVC-450"));
//    }
//
//    @Test
//    @DisplayName("GET /spgateway/bsa/configuration/status - Successful status lookup")
//    void testGetStatus_Success() throws Exception {
//        BsaConfigurationStatusResponseDTO statusDTO = new BsaConfigurationStatusResponseDTO();
//        statusDTO.setDepartmentId(450L);
//        statusDTO.setRegistrationStatus("REGISTERED");
//        statusDTO.setServiceCode("BSA-SVC-450");
//        statusDTO.setHasConfiguration(true);
//
//        when(configurationService.getStatus(any(), eq(450L))).thenReturn(statusDTO);
//
//        mockMvc.perform(get("/spgateway/bsa/configuration/status")
//                        .header("USER-DETAILS", userDetailsHeaderJson)
//                        .param("departmentId", "450"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.hasConfiguration").value(true))
//                .andExpect(jsonPath("$.registrationStatus").value("REGISTERED"))
//                .andExpect(jsonPath("$.serviceCode").value("BSA-SVC-450"));
//    }
//
//    @Test
//    @DisplayName("POST /spgateway/bsa/configuration/resend-document - Successful document dispatch")
//    void testResendDocument_Success() throws Exception {
//        BsaResendDocumentRequestDTO requestDTO = new BsaResendDocumentRequestDTO();
//        requestDTO.setDepartmentId(450L);
//
//        BsaResendDocumentResponseDTO responseDTO = new BsaResendDocumentResponseDTO();
//        responseDTO.setSuccess(true);
//        responseDTO.setServiceCode("BSA-SVC-450");
//        responseDTO.setMessage("Document resent successfully to admin@sp.gov.in");
//
//        when(configurationService.resendDocument(any(), any(BsaResendDocumentRequestDTO.class)))
//                .thenReturn(responseDTO);
//
//        mockMvc.perform(post("/spgateway/bsa/configuration/resend-document")
//                        .header("USER-DETAILS", userDetailsHeaderJson)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestDTO)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.serviceCode").value("BSA-SVC-450"));
//    }
//}
