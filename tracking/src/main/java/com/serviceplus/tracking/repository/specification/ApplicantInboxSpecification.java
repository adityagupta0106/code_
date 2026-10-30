package com.serviceplus.tracking.repository.specification;

import com.serviceplus.tracking.dto.ApplicationTrackingFilter;
import com.serviceplus.tracking.entity.ApplicantInbox;
import com.serviceplus.tracking.entity.ApplicationTracking;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class ApplicantInboxSpecification {

    private ApplicantInboxSpecification() {
    }


    public static Specification<ApplicantInbox> applicantApplications(Long userId, String tenantId, ApplicationTrackingFilter filter) {

        LocalDate fromDate = filter != null ? filter.getFromDate() : null;

        LocalDate toDate = filter != null ? filter.getToDate() : null;

        if (fromDate != null && toDate == null) {
            toDate = LocalDate.now();
        }


        return Specification
                .<ApplicantInbox>unrestricted()
                .and(hasApplicant(userId))
                .and(hasTenant(tenantId))
                .and(hasPendingStatus())
                .and(hasFromDate(fromDate))
                .and(hasToDate(toDate))
                .and(hasApplicationRefNo(
                        filter != null
                                ? filter.getApplicationRefNo()
                                : null
                ));
    }


    private static Specification<ApplicantInbox> hasApplicant(Long userId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("appliedBy"),
                        userId
                );
    }


    private static Specification<ApplicantInbox> hasTenant(String tenantId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("tenantId"),
                        tenantId
                );
    }


    private static Specification<ApplicantInbox> hasPendingStatus() {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("status"),
                        "N"
                );
    }


    private static Specification<ApplicantInbox> hasFromDate(LocalDate fromDate) {

        if (fromDate == null) {
            return null;
        }

        LocalDateTime startDate = fromDate.atStartOfDay();


        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("createdOn"),
                        startDate
                );
    }


    private static Specification<ApplicantInbox> hasToDate(LocalDate toDate) {

        if (toDate == null) {
            return null;
        }

        LocalDateTime nextDay = toDate.plusDays(1).atStartOfDay();


        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(
                        root.get("createdOn"),
                        nextDay
                );
    }


    private static Specification<ApplicantInbox> hasApplicationRefNo(List<String> applicationRefNos) {

        if (applicationRefNos == null || applicationRefNos.isEmpty()) {
            return Specification.unrestricted();
        }


        List<String> validRefNos =
                applicationRefNos.stream()
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(refNo -> !refNo.isBlank())
                        .toList();


        if (validRefNos.isEmpty()) {
            return Specification.unrestricted();
        }


        return (root, query, criteriaBuilder) ->
                root.get("applicationRefNo").in(validRefNos);
    }
}