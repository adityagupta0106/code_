package com.serviceplus.tracking.repository.specification;

import com.serviceplus.tracking.dto.ApplicationTrackingFilter;
import com.serviceplus.tracking.entity.ApplicationTracking;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public final class ApplicationTrackingSpecification {

    private ApplicationTrackingSpecification() {
    }

    public static Specification<ApplicationTracking> applicantApplications(Long userId, String tenantId, ApplicationTrackingFilter filter) {

        LocalDate fromDate = filter != null ? filter.getFromDate() : null;

        LocalDate toDate = filter != null ? filter.getToDate() : null;

        if (fromDate != null && toDate == null) {
            toDate = LocalDate.now();
        }


        return Specification.<ApplicationTracking>unrestricted()
                .and(hasUser(userId))
                .and(hasTenant(tenantId))
                .and(hasFromDate(fromDate))
                .and(hasToDate(toDate))
                .and(hasApplicationRefNo(
                        filter != null ? filter.getApplicationRefNo() : null
                ));
    }


    private static Specification<ApplicationTracking> hasUser(Long userId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("appliedUserId"),
                        userId
                );
    }


    private static Specification<ApplicationTracking> hasTenant(String tenantId) {

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(
                        root.get("tenantId"),
                        tenantId
                );
    }


    private static Specification<ApplicationTracking> hasFromDate(LocalDate fromDate) {

        if (fromDate == null) {
            return Specification.unrestricted();
        }

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.greaterThanOrEqualTo(
                        root.get("applyDate"),
                        fromDate.atStartOfDay()
                );
    }


    private static Specification<ApplicationTracking> hasToDate(LocalDate toDate) {

        if (toDate == null) {
            return Specification.unrestricted();
        }

        LocalDateTime nextDay = toDate.plusDays(1).atStartOfDay();

        return (root, query, criteriaBuilder) ->
                criteriaBuilder.lessThan(
                        root.get("applyDate"),
                        nextDay
                );
    }


    private static Specification<ApplicationTracking> hasApplicationRefNo(List<String> applicationRefNos) {

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
                root.get("applRefNo").in(validRefNos);
    }
}