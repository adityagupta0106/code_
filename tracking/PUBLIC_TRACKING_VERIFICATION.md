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
- The UserManagement hardening branch is based on the combined Modify Profile Mobile OTP + Tracking OTP integration branch. If mobile OTP is already committed in GitLab, preserve those changes when copying Tracking OTP files.

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

---

## 2026-11-04 Production-hardening review and GitLab handoff

### Source feature branches (DO NOT merge to GitLab main without tests)
- TrackingService + ServiceMetadata + FormDesigner are in `adityagupta0106/code_`, branch `hardening/public-tracking-20261104`.
- UserManagement is in `adityagupta0106/usermanage`, branch `hardening/public-tracking-20261104`, based on the branch that combines Modify Profile Mobile OTP and Tracking OTP.
- The GitHub mirrors group several microservices; your actual GitLab repositories are **separate projects**. Do not cherry-pick the monorepo commits directly into an unrelated GitLab repository. Copy and review **only that microservice's files**, using your existing local GitLab working tree as the baseline.
- No change was made to either GitHub `main` branch.

### Exact files per local microservice (copy from GitHub path into the corresponding project src tree)

**TrackingService (GitHub root `tracking/`):**
- `src/main/java/com/serviceplus/tracking/service/PublicTrackingVerificationService.java` — new extraction and public verification flow.
- `src/main/java/com/serviceplus/tracking/service/ApplicationTrackingService.java` — delegates public methods to new service.
- `src/main/java/com/serviceplus/tracking/controller/PublicApiController.java` — guest HTTP endpoints.
- `src/main/java/com/serviceplus/tracking/feignClient/ServiceMetadataFeignClient.java` — now sends the internal token for Metadata calls.
- Required existing files: `PublicTrackingAuthorizationService.java`, `FormDesignerFeignClient.java`, `UserManagmentFeignClient.java`, and `ApplicationTrackingRequest.java`. Confirm that your GitLab checkout already has these; they may not appear in the new hardening diff.

**ServiceMetadata (GitHub root `service_metadata/`):**
- `src/main/java/com/serviceplus/metadata/controller/ServiceMetadataController.java` — internal tracking endpoints now require `X-Tracking-Internal-Token`.
- `src/test/java/com/serviceplus/metadata/controller/TrackingInternalAuthorizationTest.java` — new auth regression tests.
- Required existing components: `ServiceMetadataService.java` (save/get/delete/public tracking config), `ServiceTrackingConfig.java`, `ServiceTrackingConfigDTO.java`, `IServiceTrackingConfigRepository.java`, `schm_sp.tracking_configuration`. If missing locally, import and review the related code and schema before enabling.

**FormDesigner (GitHub root `form desinger/`; preserve your local project naming):**
- `src/main/java/com/serviceplus/formDesigner/service/ApplicationDataService.java` — strong attribute policy, fail-closed scanning, historical shared-form scope validation.
- `src/main/java/com/serviceplus/formDesigner/repository/ApplicationDataRepository.java` — scan only submitted `s_1` rows.
- `src/main/java/com/serviceplus/formDesigner/feignClient/ServiceMataDataFeignClient.java` — passes the internal token to Metadata.
- `src/test/java/com/serviceplus/formDesigner/service/TrackingApplicationMatchingTest.java` — additional security regression tests.
- Required existing files: `ApplicationDataController.java` and `FormDataReferenceRepository.java`. They expose the protected internal matcher and the persisted reference used to authorize historical shared forms.

**UserManagement (GitHub root `src/`):**
- `src/main/java/com/serviceplus/UserManagement/Service/TrackingOtpService.java` — atomic consumption of verified Tracking OTP.
- `src/main/java/com/serviceplus/UserManagement/profile/service/ModifyProfileRedisService.java` — tracking challenge storage/consume Lua script, string serializer, and Modify Profile Mobile OTP functions.
- `src/main/java/com/serviceplus/UserManagement/profile/service/BucketService.java` — existing Email/Tracking buckets preserved; Mobile OTP additions.
- `src/main/java/com/serviceplus/UserManagement/Config/Bucket4jRedisConfig.java` — pass configured Redis password and database to Bucket4j's Jedis client.
- Required existing files: `Controller/TrackingOtpController.java`, `Config/RedisConfiguration.java`. If you also merge the complete Modify Profile Mobile OTP feature, include `profile/controller/ModifyProfileController.java`, `profile/service/ModifyProfileService.java`, and `profile/dto/ModifyMobileOtpDTO.java`. The combined Redis service imports the DTO, so it must exist locally.

### Security changes and remaining limitations
1. **FORM_ATTRIBUTES policy (FormDesigner):** `tracking.public.strong-attributes` is a server-side allowlist. Syntax: `tenantId:serviceId:attributeId`; multiple entries separated by commas, semicolons or line breaks. Example for synthetic test tenant: `tenant:7:secret`. This must identify a **genuinely high-entropy, applicant-known secret**, not just a sensitive-looking label. If no eligible strong field is configured, attribute-only verification fails closed with `additionalVerificationRequired`. Name, DOB, age, address, etc. must not be assumed strong. For services without approved attributes, use an OTP verification method.
2. **Incomplete scans fail closed (FormDesigner):** missing/decryption-failed/ambiguous submission, missing form, or other data integrity failures must never be silently skipped while issuing proofs.
3. **Cross-tenant shared form protection (FormDesigner):** a template owned by a different tenant requires an exact matching `schm_sp.form_data_reference` row including tenant/service/application/data/form/task. Legacy shared-form submissions without this reference will now be denied; validate sample historical shared-form cases before deployment or implement an approved historical mapping migration.
4. **Internal Metadata auth:** ServiceMetadata validates `X-Tracking-Internal-Token` in constant time for its two internal tracking routes; TrackingService and FormDesigner forward it. Deploy these three code changes **together** with the same secret. Block direct public ingress to all `/internal/tracking/**` endpoints.
5. **Redis Bucket4j:** UserManagement now uses configured `spring.data.redis.password` and `spring.data.redis.database`; confirm TLS/ACL and network requirements of your actual environment separately. Profile Email OTP behavior is retained.
6. **Scale limitation (not fixed):** FormDesigner still scans/decrypts application submissions in pages (200 records/page) with `tracking.public.max-scan-records` default 10000. Filtering to `s_1` reduces unnecessary work but does **not** provide an indexed contact/value lookup. A large service can still time out or reach scan limit. Load-test real service sizes and design a privacy-preserving indexed lookup if needed.
7. **Client IP limits:** TrackingService limits by `request.getRemoteAddr()`. Behind a gateway this may identify the proxy, not the real client; configure trusted proxy identity handling **without trusting spoofable client headers** and load-test limits.
8. **Gateway trust:** An external client must not be able to inject `X-Tracking-Internal-Token` or impersonate `USER-DETAILS`. Authenticate at ingress, strip untrusted identity headers, and restrict direct service access.
9. **Production readiness is unverified:** This is a code review and static compatibility update, **not a successful build/deployment claim**. The GitHub mirrors do not expose Maven build manifests here; actual Java compilation, Mongo/Redis integration, ESB delivery, service activation, and multi-tenant tests must run in the user's local GitLab projects.

### Required runtime configuration
- **All four:** same non-empty `TRACKING_INTERNAL_TOKEN` secret, configured in secret management (not committed).
- **TrackingService:** `tracking.public.enabled=true` only after coordinated deployment; configure `tracking.public.request-ttl-seconds`, `tracking.public.proof-ttl-seconds`, `tracking.public.requests-per-minute`.
- **FormDesigner:** `tracking.public.strong-attributes` per tenant/service (do not enable attribute-only verification for ordinary personal information), `tracking.public.max-scan-records`, internal token.
- **UserManagement:** `tracking.otp.hmac-secret` at least 32 UTF-8 bytes; `tracking.otp.validity-minutes`; Redis host/port/password/database; tenant MAIL and SMS configuration, approved SMS DLT template and ESB.
- **ServiceMetadata:** internal token and existing database table/repository configuration.

### Suggested local GitLab commit workflow (repeat separately per microservice)
1. `git status --short` and `git remote -v`. Never overwrite existing local work; back up/stash intentionally.
2. Compare the matching files in your **local GitLab checkout** to the corresponding GitHub feature branch. Your local baseline and branch names may differ from GitHub's. Save any existing in-progress changes before copying files.
3. Copy only that service's required source/tests into its existing package paths. For a file with local uncommitted changes, merge the specific method changes rather than replacing the whole file.
4. `git diff --check`; `git diff --name-only`; stage explicit intended paths in STS Git Staging, not `git add .`.
5. Run `mvn clean verify` from that local microservice's actual build directory (or the project's configured Maven build in STS). Run startup smoke tests and all acceptance tests below.
6. Use a feature branch and review the staged diff. Commit only after tests, then request review before merging to deployment branch.

### Release acceptance tests (must pass before production sign-off)
- Start all four services with the final environment configuration; public `/o/` endpoints must work without login and internal endpoints must reject missing/incorrect token with 403.
- Confirm service ID and tenant ID are checked in both metadata and matching; attempts from another tenant fail; unconfigured/disabled methods fail.
- Verify MOBILE_OTP and EMAIL_OTP for a real submitted application with **actual** delivery through ESB, plus invalid, expired, reused and concurrent attempts.
- Verify Redis OTP TTL, rate limits and atomic one-time consumption with production-equivalent Redis credentials and database.
- Verify FORM_ATTRIBUTES with approved strong field, with only weak fields, wrong fields, wrong types, changed configuration, ambiguous applications, and malformed input.
- Verify encrypted submissions, unreadable/duplicated rows, and deliberately missing form templates fail closed.
- Verify same-tenant and legitimate cross-tenant shared-form submissions against existing `form_data_reference`, including historical samples.
- Verify each `verificationProof` is single-use, short-lived, scoped to service/tenant/application and does not leak other applications.
- Load-test representative large services; the scanning limit and trusted-proxy rate limit must not block ordinary users.
- Run regression tests for logged-in tracking, Modify Profile Email OTP, and Modify Profile Mobile OTP.
- Confirm gateway strips spoofed identity/secret headers and logs redact OTPs, secrets and sensitive attribute values.
- Perform rollback rehearsal. Keep `tracking.public.enabled=false` until **all** critical release gates pass.

### Go / no-go
**Current status: CODE HARDENED; PRODUCTION NOT CERTIFIED.** Do not claim this is safe to deploy until real builds, integration/security tests, large-dataset performance, gateway trust, and actual notification delivery are verified.
