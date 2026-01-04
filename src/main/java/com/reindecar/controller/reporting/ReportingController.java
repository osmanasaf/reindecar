package com.reindecar.controller.reporting;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.service.reporting.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reporting", description = "Reporting and analytics endpoints")
public class ReportingController {

    private final ReportingService reportingService;

    @GetMapping("/dashboard")
    @Operation(summary = "Get dashboard stats", description = "Returns basic dashboard statistics")
    public ApiResponse<Map<String, Object>> getDashboardStats() {
        Map<String, Object> stats = reportingService.getDashboardStats();
        return ApiResponse.success(stats);
    }
}
