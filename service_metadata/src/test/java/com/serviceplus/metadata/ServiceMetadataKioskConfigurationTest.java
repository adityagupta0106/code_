//package com.serviceplus.metadata;
//
//import com.serviceplus.metadata.dto.LabelValue;
//import com.serviceplus.metadata.dto.ProcessFlowDTO;
//import com.serviceplus.metadata.dto.ServiceJSONDTO;
//import com.serviceplus.metadata.dto.ServiceProcessFlowDTO;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//@DisplayName("ServiceMetadata Kiosk Configuration and Identified User Set Tests")
//public class ServiceMetadataKioskConfigurationTest {
//
//    @Test
//    @DisplayName("Should correctly serialize and retain kioskType with Identified User Set mode (value 113)")
//    void shouldRetainIdentifiedUserSetModeInProcessFlow() {
//        ProcessFlowDTO.DataDTO dataDTO = new ProcessFlowDTO.DataDTO();
//
//        LabelValue kiosk1 = new LabelValue();
//        kiosk1.setLabel("WebService");
//        kiosk1.setValue("115");
//
//        LabelValue kiosk2 = new LabelValue();
//        kiosk2.setLabel("IDENTIFEID user set");
//        kiosk2.setValue("113");
//
//        dataDTO.setKioskType(Arrays.asList(kiosk1, kiosk2));
//
//        LabelValue l1 = new LabelValue();
//        l1.setLabel("A1");
//        l1.setValue(10);
//
//        LabelValue l2 = new LabelValue();
//        l2.setLabel("A1");
//        l2.setValue(10);
//
//        dataDTO.setIdentifiedUserSetIds(Arrays.asList(l1, l2));
//
//        assertNotNull(dataDTO.getKioskType());
//        assertEquals(2, dataDTO.getKioskType().size());
//        assertEquals("113", dataDTO.getKioskType().get(1).getValue());
//        assertEquals(Arrays.asList(101, 102), dataDTO.getIdentifiedUserSetIds());
//    }
//
//    @Test
//    @DisplayName("ServiceJSONDTO should retain submissionKioskTypes and provide backwards-compatible getKioskType fallback")
//    void shouldRetainSubmissionKioskTypesInServiceJson() {
//        ServiceJSONDTO serviceJson = new ServiceJSONDTO();
//
//        LabelValue kiosk = new LabelValue();
//        kiosk.setLabel("IDENTIFEID user set");
//        kiosk.setValue("113");
//
//        serviceJson.setSubmissionKioskTypes(Arrays.asList(kiosk));
//
//        LabelValue l1 = new LabelValue();
//        l1.setLabel("A1");
//        l1.setValue(10);
//
//        LabelValue l2 = new LabelValue();
//        l2.setLabel("A1");
//        l2.setValue(10);
//
//
//        serviceJson.setSubmissionIdentifiedUserSetIds(Arrays.asList(l1, l2));
//
//        assertNotNull(serviceJson.getSubmissionKioskTypes());
//        assertEquals(1, serviceJson.getSubmissionKioskTypes().size());
//        assertEquals("113", serviceJson.getSubmissionKioskTypes().get(0).getValue());
//
//        // Test fallback getter
//        assertNotNull(serviceJson.getKioskType());
//        assertEquals("113", serviceJson.getKioskType().get(0).getValue());
//        assertEquals(Arrays.asList(55, 66), serviceJson.getSubmissionIdentifiedUserSetIds());
//    }
//
//    @Test
//    @DisplayName("ServiceProcessFlowDTO Nodes should retain task-level kioskType and identifiedUserSetIds")
//    void shouldRetainTaskLevelKioskTypeAndUserSets() {
//        ServiceProcessFlowDTO.Data.Nodes node = new ServiceProcessFlowDTO.Data.Nodes();
//        node.setId("TASK_001");
//        node.setName("Verification Task");
//
//        LabelValue kiosk = new LabelValue();
//        kiosk.setLabel("Identified User Set");
//        kiosk.setValue("113");
//
//        node.setKioskType(Arrays.asList(kiosk));
//
//        LabelValue l1 = new LabelValue();
//        l1.setLabel("A1");
//        l1.setValue(10);
//
//        LabelValue l2 = new LabelValue();
//        l2.setLabel("A1");
//        l2.setValue(10);
//
//        node.setIdentifiedUserSetIds(Arrays.asList(l1, l2));
//
//        assertNotNull(node.getKioskType());
//        assertEquals(1, node.getKioskType().size());
//        assertEquals("113", node.getKioskType().get(0).getValue());
//        assertEquals(3, node.getIdentifiedUserSetIds().size());
//        assertTrue(node.getIdentifiedUserSetIds().contains(202));
//    }
//
//    @Test
//    @DisplayName("Should safely handle null and empty kioskType and identifiedUserSetIds")
//    void shouldHandleNullAndEmptyGracefully() {
//        ServiceJSONDTO serviceJson = new ServiceJSONDTO();
//        assertNull(serviceJson.getSubmissionKioskTypes());
//        assertNull(serviceJson.getKioskType());
//        assertNull(serviceJson.getSubmissionIdentifiedUserSetIds());
//
//        ProcessFlowDTO.DataDTO dataDTO = new ProcessFlowDTO.DataDTO();
//        assertNull(dataDTO.getKioskType());
//        assertNull(dataDTO.getIdentifiedUserSetIds());
//
//        ServiceProcessFlowDTO.Data.Nodes node = new ServiceProcessFlowDTO.Data.Nodes();
//        assertNull(node.getKioskType());
//        assertNull(node.getIdentifiedUserSetIds());
//    }
//}
