package com.reindecar.controller.reporting;

import com.reindecar.common.dto.ApiResponse;
import com.reindecar.dto.reporting.RevenueByMonthResponse;
import com.reindecar.dto.reporting.UpcomingReturnResponse;
import com.reindecar.service.reporting.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/upcoming-returns")
    @Operation(summary = "Get upcoming returns", description = "Returns rentals with end dates within the next N days")
    public ApiResponse<List<UpcomingReturnResponse>> getUpcomingReturns(@RequestParam("days") int days) {
        List<UpcomingReturnResponse> returns = reportingService.getUpcomingReturns(days);
        return ApiResponse.success(returns);
    }

    @GetMapping("/revenue")
    @Operation(summary = "Get revenue by month", description = "Returns revenue totals for the last N months")
    public ApiResponse<List<RevenueByMonthResponse>> getRevenue(@RequestParam("months") int months) {
        List<RevenueByMonthResponse> revenue = reportingService.getRevenueByMonths(months);
        return ApiResponse.success(revenue);
    }
}
