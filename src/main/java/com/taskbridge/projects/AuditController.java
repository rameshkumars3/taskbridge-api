package com.taskbridge.projects;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.validation.annotation.Validated;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@Validated
@RequestMapping("/api/audit")
@SecurityRequirement(name = "bearerAuth")
public class AuditController {
    private final AuditService auditService;

    public AuditController(AuditService auditService) {
        this.auditService = auditService;
    }

    @PostMapping
    public ResponseEntity<AuditResponse> create(@Valid @RequestBody AuditRequest request) {
        AuditLog audit = auditService.record(request.projectId(), request.eventType(), request.previousStatus(),
                request.newStatus(), request.message(), request.deduplicationKey());
        return ResponseEntity.status(HttpStatus.CREATED).body(AuditResponse.from(audit));
    }

    @GetMapping("/{projectId}")
    public List<AuditResponse> history(@PathVariable @Positive Long projectId,
            @RequestParam(required = false) Instant from, @RequestParam(required = false) Instant to,
            @RequestParam(required = false) ProjectEventType eventType) {
        return auditService.history(projectId, from, to, eventType);
    }
}
