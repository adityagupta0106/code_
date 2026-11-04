package com.serviceplus.metadata.controller;

import com.serviceplus.metadata.service.ServiceMetadataService;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class TrackingInternalAuthorizationTest {

    private ServiceMetadataController controller(ServiceMetadataService service) {
        ServiceMetadataController controller = new ServiceMetadataController();
        ReflectionTestUtils.setField(controller, "serviceMetadataService", service);
        ReflectionTestUtils.setField(controller, "trackingInternalToken", "test-only-shared-token");
        return controller;
    }

    @Test
    void configuredServicesRejectMissingOrIncorrectInternalToken() {
        ServiceMetadataService service = mock(ServiceMetadataService.class);
        ServiceMetadataController controller = controller(service);
        assertEquals(HttpStatus.FORBIDDEN, controller.getConfigureServices("tenant", null).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN, controller.getConfigureServices("tenant", "wrong").getStatusCode());
        verifyNoInteractions(service);
    }

    @Test
    void configurationRejectsMissingOrIncorrectInternalToken() {
        ServiceMetadataService service = mock(ServiceMetadataService.class);
        ServiceMetadataController controller = controller(service);
        assertEquals(HttpStatus.FORBIDDEN,
                controller.getPublicTrackingConfiguration(7L, "tenant", null).getStatusCode());
        assertEquals(HttpStatus.FORBIDDEN,
                controller.getPublicTrackingConfiguration(7L, "tenant", "wrong").getStatusCode());
        verifyNoInteractions(service);
    }

    @Test
    void trustedTokenCanFetchTenantScopedData() {
        ServiceMetadataService service = mock(ServiceMetadataService.class);
        when(service.getConfiguredServices("tenant")).thenReturn(List.of(Map.of("serviceId", 7L)));
        when(service.getPublicTrackingConfig(7L, "tenant")).thenReturn(Map.of("serviceId", 7L));
        ServiceMetadataController controller = controller(service);

        assertEquals(HttpStatus.OK,
                controller.getConfigureServices("tenant", "test-only-shared-token").getStatusCode());
        assertNotNull(controller.getPublicTrackingConfiguration(
                7L, "tenant", "test-only-shared-token").getBody());
        verify(service).getConfiguredServices("tenant");
        verify(service).getPublicTrackingConfig(7L, "tenant");
    }
}
