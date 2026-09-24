 # Copilot Tool Strategy

## Evidence boundary

This strategy is based only on the recorded activity in `PROMPTS.md` and the
current repository. The repository is a Java 17 Spring Boot/Maven application
with Project, Audit, and Notification slices. The prompt record shows Ask Mode
for reviews and documentation, and Agent Mode for scoped implementation and
tests. It does not show a JWT implementation, a lint or coverage plugin, or a
ten-handler application. Those absences are treated as limits, not as evidence
that the controls exist elsewhere.

## Copilot usage entries

### 1. Repository guidance and architecture baseline

- **Task:** Draft `.github/copilot-instructions.md` for the multi-tenant
	TaskBridge API, including Java/Spring layering, DTOs, trusted identity,
	validation, authorization, transactions, logging, and testing.
- **Reason:** Establish explicit repository constraints before code generation
	or review, especially for tenant isolation and security-sensitive changes.
- **Outcome:** Ask Mode produced the repository guidance. No implementation
	files were changed, and the prompt record says no corrections were required.
- **Human verification:** The guidance is present in the repository and was
	reviewed against the product requirements and existing architecture.

### 2. Initial Project generation followed by evidence-based review

- **Task:** Generate a Project model and service with persistence, then review
	the unmodified model and service for security and architecture defects.
- **Reason:** Use a deliberately low-effort draft as a review target before
	accepting generated CRUD and identify concrete risks rather than assuming
	generated code is production-ready.
- **Outcome:** The draft was saved unchanged. Ask Mode then recorded confirmed
	findings in `REVIEW.md`, including unscoped repository access, client-
	controlled identity, missing validation, missing transactions, and direct
	entity contracts.
- **Human verification:** The findings were accepted as confirmed review
	issues; the record distinguishes visible omissions from controls that the
	repository cannot establish.

### 3. Focused method and decision-boundary review

- **Task:** Review `deleteProject`, then identify decisions Copilot could not
	safely infer for tenant trust, status transitions, deletion, and concurrency.
- **Reason:** Keep security analysis narrow and force product/security owners
	to decide policy where the source code does not define it.
- **Outcome:** The focused review documented the caller-supplied ID and absent
	tenant, authorization, transaction, validation, and integrity checks.
	A separate Ask activity recorded the unresolved policy decisions in
	`REVIEW.md` and `evidence.md` instead of guessing.
- **Human verification:** Human review confirmed the findings and accepted the
	separation between confirmed omissions and unverifiable external controls.

### 4. Project remediation implementation

- **Task:** Implement the approved Project remediation: organisation-scoped
	repositories, trusted organisation context, permission checks, DTOs,
	validation, status transitions, transactions, errors, logging, and tests.
- **Reason:** Convert the reviewed risks into the smallest approved code
	slice without modifying Notification or Audit code.
- **Outcome:** Agent Mode implemented the Project controller/service/model
	changes, including `DRAFT -> ACTIVE -> COMPLETED -> ARCHIVED`, optimistic
	versioning, centralized errors, and focused service tests. The activity
	record reports six passing Project tests and a passing Maven suite at that
	stage.
- **Human verification:** The final evidence review confirmed trusted tenant
	resolution, DTO separation, authorization, transactions, status rules, test
	coverage, and that Notification/Audit files remained untouched.

### 5. Notification and Audit persistence, services, and API

- **Task:** Implement the specified Audit and Notification persistence layer,
	services, lifecycle integration, controllers, DTO validation, recipient
	ownership, deduplication, and organisation-scoped access.
- **Reason:** Add the approved lifecycle tracking workflow without exposing
	entities or allowing client-supplied organisation or actor identity.
- **Outcome:** Agent Mode added tenant-scoped repositories and services,
	immutable audit records, equal team fan-out, notification read ownership,
	filtered history, API error mapping, and idempotent deduplication. The API
	activity covers `POST /api/audit`, audit history, notification retrieval,
	and mark-as-read.
- **Human verification:** Controller tests passed with seven tests and the
	full suite passed with 16 tests at the API-layer step. Expanded service tests
	then passed with 18 focused tests and 28 total tests; the later approved
	milestone/IP change passed with 33 focused tests and `git diff --check`.

### 6. Test expansion around observable security behavior

- **Task:** Add JUnit 5/Mockito coverage for notification fan-out, audit
	lifecycle events, append-only behavior, filters, cross-organisation access,
	unread retrieval, and authorized versus unauthorized read operations.
- **Reason:** Verify externally observable behavior and tenant boundaries rather
	than merely verifying mock calls or application startup.
- **Outcome:** Agent Mode expanded the service tests without weakening
	production code. Later tests also covered milestone reopen behavior, trusted
	remote-address capture, forwarding-header rejection, and API omission of
	actor IP.
- **Human verification:** The prompt record reports 28 passing tests for the
	expanded suite and 33 passing tests after the approved reopen/IP extension,
	with no reported production diagnostics in the touched files.

### 7. Impact analysis before a privacy-sensitive extension

- **Task:** Analyze adding `MILESTONE_REOPENED` and actor IP capture before
	implementation, including migration, privacy, retention, trust, rollback,
	and tests.
- **Reason:** Make a security and privacy-sensitive change decision explicit
	before changing the domain, persistence, API, or operational behavior.
- **Outcome:** Ask Mode produced `IMPACT_ANALYSIS.md`; Agent Mode then applied
	the approved narrow extension using the server remote address, nullable
	compatibility, no forwarding-header trust, no ordinary-log IP, and no API
	exposure.
- **Human verification:** The diff was shown before application; focused tests,
	production diagnostics, and `git diff --check` were reported as passing.

## Required scenario responses

### 600-line legacy service

The repository does not contain a recorded 600-line service scenario. The
closest actual activity was the Project model/service review and the focused
`deleteProject` review. The demonstrated strategy is to ask for an evidence-
only review first, split the service by concern and method, record confirmed
risks, then implement one approved slice and test it. A future 600-line review
should preserve that sequence and require a human-approved slice map before
large edits.

### Validation across ten handlers

The repository does not contain ten handlers; the implementation activity
covers the visible Project, Audit, and Notification controllers and their
focused tests. The actual strategy was to use request DTO validation,
validated path/query parameters, trusted identity context, centralized error
mapping, and MockMvc tests for the API layer. For ten handlers, repeat that
matrix per handler and add a shared contract test, but do not claim that this
was completed here.

### JWT expiry and tampering

No JWT parser, token provider, Spring Security configuration, expiry test, or
tampering test is present in the recorded activity or visible repository. The
implemented code uses an authenticated principal/organisation context as the
trusted boundary and explicitly avoids client-supplied identity. That is not
JWT verification. A real JWT scenario requires an integration test with an
expired token, a tampered signature, wrong claims, and a valid token, backed by
the production authentication configuration.

### Lint and coverage enforcement

The actual validation activity used Maven tests, workspace diagnostics, and
`git diff --check`. The `pom.xml` contains no JaCoCo, Checkstyle, SpotBugs, PMD,
or coverage threshold configuration, so no lint gate or coverage percentage
was enforced. A future enforcement change should add the chosen plugins and
CI failure thresholds, then run them alongside `./mvnw test -q`; this document
does not present those unavailable gates as completed work.

### Contractor service review

The closest recorded activity is the human security and architecture review
of the unmodified Project service, including IDOR, tenant scope, validation,
transactions, concurrency, and unbounded queries. For a contractor-submitted
service, the same Ask Mode evidence review should be run before accepting code,
with file/method evidence and a human owner deciding policy gaps. The recorded
review outcome was concrete findings in `REVIEW.md`, not an assertion that all
external controls were present.

### Consistent tenant isolation

This is the strongest demonstrated use case. The remediation changed Project,
Audit, and Notification access to use trusted organisation context and
organisation-scoped repository operations. It added permission checks,
recipient ownership checks, cross-organisation rejection/concealment, and
tests for cross-organisation audit access and notification reads. Human
validation accepted the implementation and the recorded focused suites passed.
The evidence still does not establish production-wide security configuration
or every possible route, so consistency means consistency across the visible
implemented slices, not a claim about an unseen deployment.

## Genuine limitations and corrections

### 1. Authentication boundary was stronger than the evidence

- **Prompt/activity:** Project and Audit/Notification implementation prompts
	required trusted principal and organisation context; the PR evidence also
	lists real JWT/Principal integration as a known gap.
- **Problem:** The work demonstrates service-layer trust in an authenticated
	context but does not prove JWT expiry, signature tampering, claim validation,
	or production authentication wiring.
- **Detection:** Repository inspection found no JWT configuration or token tests,
	and the known-gaps section records the missing integration coverage.
- **Correction:** The implementation did not invent JWT behavior; it kept the
	visible principal/context boundary and documented the gap.
- **Improved future approach:** Add the real security configuration first, then
	integration tests for expiry, tampering, wrong organisation claims, and
	authorization outcomes at the HTTP boundary.

### 2. Quality gates were not enforced by the build

- **Prompt/activity:** The prompt record reports Maven tests, diagnostics, and
	`git diff --check` as validation; no lint or coverage command is recorded.
- **Problem:** Passing tests do not establish style compliance or a minimum
	coverage level, and the build has no configured static-analysis or coverage
	threshold gate.
- **Detection:** `pom.xml` contains only the Spring Boot Maven plugin, and
	repository search found no JaCoCo, Checkstyle, SpotBugs, PMD, or lint setup.
- **Correction:** The strategy treats test counts as test evidence only and
	explicitly declines to claim lint or coverage enforcement.
- **Improved future approach:** Select and configure the project-approved
	analysis tools, fail CI below an agreed coverage threshold, and record the
	exact commands and reports with each implementation slice.

### 3. Early generation required human correction

- **Prompt/activity:** The low-effort Project generation was saved unchanged;
	subsequent Ask Mode security and architecture reviews found multiple
	confirmed defects.
- **Problem:** Generated CRUD initially trusted entities and IDs, used
	unscoped access, lacked validation and transactions, and had no focused
	regression tests.
- **Detection:** The review compared the generated code with the repository
	instructions and documented exact evidence in `REVIEW.md` and `evidence.md`.
- **Correction:** The approved remediation replaced the unsafe boundary with
	DTOs, trusted tenant context, authorization, validation, transactions,
	domain errors, and tests rather than accepting the initial draft.
- **Improved future approach:** For legacy or generated code, require an
	evidence-only review and failing security/tenant tests before implementation;
	keep generated output provisional until a human approves the remediation
	scope and policy decisions.
