# Public Tracking Verification — implementation and testing guide

## Purpose
One readable entry point for all public tracking verification methods: `MOBILE_OTP`, `EMAIL_OTP`, and `FORM_ATTRIBUTES`.

**This refactor does not merge or remove microservices.** ServiceMetadata owns configuration, FormDesigner owns matching against submitted form data, UserManagement owns OTP delivery and OTP validation, and TrackingService owns public access authorization.

## Read the code in this order
1. `controller/PublicApiController.java` — public HTTP endpoints.
2. `service/PublicTrackingVerificationService.java` — the three verification flows, configuration checks, matching and issuance of tracking proofs.
3. `service/PublicTrackingAuthorizationService.java` — Redis request state, rate limits and one-time authorization proofs.
4. `feignClient/ServiceMetadataFeignClient.java` and `feignClient/FormDesignerFeignClient.java` — existing internal dependencies.
5. UserManagement: `TrackingOtpController.java` → `TrackingOtpService.java` → `ModifyProfileRedisService.java` — OTP and SMS/email delivery.

Normal logged-in tracking remains in `ApplicationTrackingService.java`. Existing service callers can still use its former public methods; these now delegate to `PublicTrackingVerificationService`.

## Public endpoints (unchanged)
- `GET /o/appl/track/services` — configured services.
- `GET /o/appl/track/services/{serviceId}/config` — verification configuration.
- `POST /o/appl/track/otp/send` — accepts `{ "serviceId": 490001, "method": "MOBILE_OTP" | "EMAIL_OTP", "contact": "..." }`.
- `POST /o/appl/track/otp/verify` — accepts `{ "serviceId": 490001, "requestId": "...", "otp": "123456" }`.
- `POST /o/appl/track/attributes/verify` — accepts `{ "serviceId": 490001, "values": { "<attributeId>": "..." } }`.
- `POST /o/appl/track/public` — consumes scoped `verificationProof` to read authorized tracking details.

UserManagement internal endpoints are unchanged:
`POST /internal/tracking/otp/send`, `POST /internal/tracking/otp/send/email`, `POST /internal/tracking/otp/verify`, authenticated by `X-Tracking-Internal-Token`.

## Three flows
**OTP send:** TrackingService checks tenant/service/method, FormDesigner checks matching submitted applications, UserManagement generates a random 6-digit OTP and stores a HMAC-hashed challenge in Redis, ESB sends SMS/email. Public response is intentionally generic for unmatched contact.

**OTP verify:** TrackingService loads its `requestId` state and compares configuration. UserManagement validates challenge HMAC, limits attempts, and **atomically** consumes the verified Redis challenge. TrackingService rechecks matching applications, atomically consumes the public request, and issues an expiring proof for each authorized application.

**Attributes:** TrackingService loads configuration and asks FormDesigner to match the exact configured fields (3–5, as currently implemented). Only one matching application produces an authorized proof; otherwise the API returns `additionalVerificationRequired`. UserManagement is not called for FORM_ATTRIBUTES.

`requestId` (public request), `challengeId` (internal OTP), and `verificationProof` (application access) are deliberately different security tokens.

## Required environment and dependencies
- Run TrackingService, ServiceMetadata, FormDesigner, UserManagement, and their existing Redis/databases.
- Set `tracking.public.enabled=true` only in a prepared environment.
- Configure `TRACKING_INTERNAL_TOKEN` consistently on all internal callers and endpoints. Never expose the token through public requests or logs.
- UserManagement must have `tracking.otp.hmac-secret` (>=32 UTF-8 bytes), Redis, tenant MAIL/SMS configuration, ESB, and approved SMS provider / DLT template for SMS.
- Public state expiry defaults from `tracking.public.request-ttl-seconds` (300 seconds) and `tracking.public.proof-ttl-seconds` (180 seconds); Tracking OTP validity defaults to 5 minutes. Verify active environment overrides.
- The UserManagement repo also has a separate Modify Profile Mobile OTP feature branch. **Integrate/merge that work explicitly when deploying both features**, resolving any shared Redis class changes. Neither feature branch automatically includes the other.

## Tomorrow's verification checklist
1. Build both microservices in STS; check for missing dependencies/compile errors (public GitHub copies currently lack a build manifest).
2. Start dependencies and confirm internal auth works; wrong/missing internal token must return 403.
3. Fetch configured service metadata; check configured `MOBILE_OTP`, `EMAIL_OTP`, and `FORM_ATTRIBUTES`.
4. Send SMS OTP to an application-matched contact; confirm real ESB acceptance and actual handset delivery.
5. Send EMAIL OTP; check real mailbox delivery.
6. Verify correct OTP; receive application proofs; use proof once to fetch tracking details.
7. Verify incorrect/expired OTP, attempt exhaustion, resend, and repeated challenge verification.
8. Send two concurrent verify requests for the **same valid challenge**: only one can consume it.
9. Verify nonmatching contact returns a generic response and cannot enumerate applications.
10. Verify `FORM_ATTRIBUTES` exact configured fields: one unique match succeeds; wrong, ambiguous, incomplete or unconfigured fields must not expose details.
11. Verify cross-tenant/service request, replayed `requestId`, used proof, and expired proof fail.
12. Confirm normal authenticated tracking, existing Email OTP, and Modify Profile OTP remain working.
13. Test slow/large FormDesigner scans: the matcher still pages through submitted applications and can be expensive; this refactor does not optimize its query.
14. Review attribute strength: knowing name/DOB/address alone may not prove ownership. Set stronger configured attributes or a second factor before production use where needed.
15. Check reverse-proxy/client-IP behavior for `request.getRemoteAddr()` based rate limits.

## Deployment and rollback
- Deploy only after both repositories are built and integration tested together. The corresponding feature branches should be integrated into the deployment baseline as a coordinated change.
- No schema migration or new external dependency was introduced by this refactor.
- Rollback both feature changes together if internal endpoint behavior fails.
- No production release is implied by these GitHub commits.
