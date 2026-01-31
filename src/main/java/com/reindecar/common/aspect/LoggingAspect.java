package com.reindecar.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * AOP Aspect for logging method execution in Controller and Service layers.
 * Provides:
 * - Method entry/exit logging
 * - Execution time measurement
 * - Request/Response information logging
 * - Sensitive data masking
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    private static final String[] SENSITIVE_FIELDS = {
        "password", "token", "secret", "authorization", "creditCard", "cvv", "pin"
    };

    /**
     * Pointcut for all Controller methods
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerMethods() {}

    /**
     * Pointcut for all Service methods (excluding BaseService)
     */
    @Pointcut("within(@org.springframework.stereotype.Service *) && " +
              "!within(com.reindecar.common.service.BaseService)")
    public void serviceMethods() {}

    /**
     * Around advice for Controller methods
     * Logs request details, execution time, and response
     */
    @Around("controllerMethods()")
    public Object logControllerExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        HttpServletRequest request = getCurrentRequest();
        String traceId = getOrGenerateTraceId(request);

        long startTime = System.currentTimeMillis();

        log.info("[{}] [CONTROLLER] {} -> {}.{}() called with args: {}",
                traceId,
                request != null ? request.getMethod() + " " + request.getRequestURI() : "N/A",
                className,
                methodName,
                maskSensitiveData(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info("[{}] [CONTROLLER] {}.{}() completed successfully in {}ms",
                    traceId, className, methodName, executionTime);

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[{}] [CONTROLLER] {}.{}() failed after {}ms with error: {}",
                    traceId, className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    /**
     * Around advice for Service methods
     * Logs execution time and method calls
     */
    @Around("serviceMethods()")
    public Object logServiceExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        String traceId = getTraceIdFromRequest();
        long startTime = System.currentTimeMillis();

        log.debug("[{}] [SERVICE] {}.{}() started with args: {}",
                traceId, className, methodName, maskSensitiveData(joinPoint.getArgs()));

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            if (executionTime > 1000) {
                log.warn("[{}] [SERVICE] {}.{}() took {}ms (slow operation)",
                        traceId, className, methodName, executionTime);
            } else {
                log.debug("[{}] [SERVICE] {}.{}() completed in {}ms",
                        traceId, className, methodName, executionTime);
            }

            return result;
        } catch (Exception e) {
            long executionTime = System.currentTimeMillis() - startTime;
            log.error("[{}] [SERVICE] {}.{}() failed after {}ms: {}",
                    traceId, className, methodName, executionTime, e.getMessage());
            throw e;
        }
    }

    /**
     * Get current HTTP request from RequestContextHolder
     */
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Get or generate trace ID for request tracking
     */
    private String getOrGenerateTraceId(HttpServletRequest request) {
        if (request != null) {
            String traceId = request.getHeader("X-Trace-Id");
            if (traceId == null || traceId.isEmpty()) {
                traceId = java.util.UUID.randomUUID().toString().substring(0, 8);
                request.setAttribute("traceId", traceId);
            }
            return traceId;
        }
        return "NO-TRACE";
    }

    /**
     * Get trace ID from current request
     */
    private String getTraceIdFromRequest() {
        HttpServletRequest request = getCurrentRequest();
        if (request != null) {
            Object traceId = request.getAttribute("traceId");
            if (traceId != null) {
                return traceId.toString();
            }
            return getOrGenerateTraceId(request);
        }
        return "NO-TRACE";
    }

    /**
     * Mask sensitive data in method arguments
     */
    private String maskSensitiveData(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }
                    String argString = arg.toString();
                    // Check if argument contains sensitive field names
                    for (String sensitiveField : SENSITIVE_FIELDS) {
                        if (argString.toLowerCase().contains(sensitiveField.toLowerCase())) {
                            return "[MASKED]";
                        }
                    }
                    // Limit string length to avoid huge logs
                    if (argString.length() > 200) {
                        return argString.substring(0, 200) + "...";
                    }
                    return argString;
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
