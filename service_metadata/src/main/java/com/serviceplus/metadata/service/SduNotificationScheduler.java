package com.serviceplus.metadata.service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.serviceplus.metadata.entity.PendingAdoptionApplication;
import com.serviceplus.metadata.entity.ServiceDesignatedOfficerAssignment;
import com.serviceplus.metadata.repository.IPendingAdoptionApplicationRepository;
import com.serviceplus.metadata.repository.ServiceDesignatedOfficerAssignmentRepository;

@Component
public class SduNotificationScheduler {

    private static final Logger LOG = LogManager.getLogger("SduNotificationScheduler");

    private final IPendingAdoptionApplicationRepository pendingRepo;
    private final ServiceDesignatedOfficerAssignmentRepository designatedOfficerRepo;
    private final SduNotificationService notificationService;

    @Value("${sdu.pending.reminder.interval-hours:24}")
    private long reminderIntervalHours;

    @Autowired
    public SduNotificationScheduler(IPendingAdoptionApplicationRepository pendingRepo,
                                    ServiceDesignatedOfficerAssignmentRepository designatedOfficerRepo,
                                    SduNotificationService notificationService) {
        this.pendingRepo = pendingRepo;
        this.designatedOfficerRepo = designatedOfficerRepo;
        this.notificationService = notificationService;
    }

    //@Scheduled(fixedDelayString = "${sdu.pending.reminder.poll-delay-ms:3600000}")
    @Transactional
    public void processPendingReminders() {
        try {
            long intervalMillis = reminderIntervalHours * 3600 * 1000L;
            Date thresholdDate = new Date(System.currentTimeMillis() - intervalMillis);

            List<PendingAdoptionApplication> applications = pendingRepo.findPendingApplicationsDueForReminder(thresholdDate);
            if (applications == null || applications.isEmpty()) {
                return;
            }

            // Group by tenantId + serviceId + submissionLocationId to send 1 aggregated reminder per location
            Map<String, List<PendingAdoptionApplication>> grouped = new HashMap<>();
            for (PendingAdoptionApplication app : applications) {
                String key = app.getTenantId() + "#" + app.getServiceId() + "#" + app.getSubmissionLocationId();
                grouped.computeIfAbsent(key, k -> new java.util.ArrayList<>()).add(app);
            }

            Date now = new Date();
            for (Map.Entry<String, List<PendingAdoptionApplication>> entry : grouped.entrySet()) {
                List<PendingAdoptionApplication> apps = entry.getValue();
                if (apps.isEmpty()) {
                    continue;
                }
                PendingAdoptionApplication sample = apps.get(0);
                String tenantId = sample.getTenantId();
                Integer serviceId = sample.getServiceId();
                Integer sduLocationId = sample.getSduLocationId();
                Integer submissionLocationId = sample.getSubmissionLocationId();

                Long designatedOfficerId = null;
                List<ServiceDesignatedOfficerAssignment> doAssignments =
                        designatedOfficerRepo.findByTenantIdAndServiceId(tenantId, serviceId);
                if (doAssignments != null && !doAssignments.isEmpty()) {
                    designatedOfficerId = doAssignments.get(0).getUserId();
                }

                notificationService.sendPendingApplicationsReminder(
                        tenantId, serviceId, sduLocationId, submissionLocationId, apps.size(), designatedOfficerId);

                for (PendingAdoptionApplication app : apps) {
                    app.setReminderCount(app.getReminderCount() + 1);
                    app.setLastReminderDate(now);
                    app.setModifiedOn(now);
                    pendingRepo.save(app);
                }
            }
        } catch (Exception ex) {
            LOG.error("Error occurred in SduNotificationScheduler", ex);
        }
    }
}
