package com.example.dashboard;

import java.lang.management.ManagementFactory;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemMetricsController {

    private final HealthEndpoint healthEndpoint;

    public SystemMetricsController(HealthEndpoint healthEndpoint) {
        this.healthEndpoint = healthEndpoint;
    }

    @GetMapping("/api/metrics")
    public Map<String, Object> getMetrics() {

        Map<String, Object> metrics = new LinkedHashMap<>();

        com.sun.management.OperatingSystemMXBean osBean =
                (com.sun.management.OperatingSystemMXBean)
                        ManagementFactory.getOperatingSystemMXBean();

        // CPU
        double cpuUsage = osBean.getCpuLoad() * 100;

        // Memory
        long totalMemory = osBean.getTotalMemorySize();
        long freeMemory = osBean.getFreeMemorySize();
        long usedMemory = totalMemory - freeMemory;

        double memoryUsage = totalMemory > 0
                ? (usedMemory * 100.0 / totalMemory)
                : 0;

        // Disk
        java.io.File root = new java.io.File("/");

        long totalDisk = root.getTotalSpace();
        long freeDisk = root.getFreeSpace();
        long usedDisk = totalDisk - freeDisk;

        double diskUsage = totalDisk > 0
                ? (usedDisk * 100.0 / totalDisk)
                : 0;

        // Application status
        String applicationStatus =
                healthEndpoint.health().getStatus().getCode();

        // Application uptime
        long uptimeMillis =
                ManagementFactory.getRuntimeMXBean().getUptime();

        long uptimeSeconds = uptimeMillis / 1000;

        metrics.put("cpuUsage", round(cpuUsage));

        metrics.put("memoryUsedMB",
                usedMemory / (1024 * 1024));

        metrics.put("memoryTotalMB",
                totalMemory / (1024 * 1024));

        metrics.put("memoryUsage",
                round(memoryUsage));

        metrics.put("diskUsedGB",
                round(usedDisk / (1024.0 * 1024.0 * 1024.0)));

        metrics.put("diskTotalGB",
                round(totalDisk / (1024.0 * 1024.0 * 1024.0)));

        metrics.put("diskUsage",
                round(diskUsage));

        metrics.put("applicationStatus",
                applicationStatus);

        metrics.put("uptimeSeconds",
                uptimeSeconds);

        return metrics;
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}