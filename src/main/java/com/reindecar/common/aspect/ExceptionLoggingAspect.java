package com.reindecar.common.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * AOP Aspect for exception logging across all application layers.
 * Provides:
 * - Detailed exception logging with stack trace
 * - Correlation ID for request tracking
 * - Context information (class, method, arguments)
 * - Separate logging for different exception types
 */
@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    /**
     * Pointcut for all Controller methods
     */
    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerLayer() {}

    /**
     * Pointcut for all Service methods
     */
    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceLayer() {}

    /**
     * Pointcut for all Repository methods
     */
    @Pointcut("within(@org.springframework.stereotype.Repository *) || " +
              "within(org.springframework.data.jpa.repository.JpaRepository+)")
    public void repositoryLayer() {}

    /**
     * After throwing advice for Controller layer
     * Logs exceptions with HTTP request context
     */
    @AfterThrowing(pointcut = "controllerLayer()", throwing = "exception")
    public void logControllerException(JoinPoint joinPoint, Throwable exception) {
        HttpServletRequest request = getCurrentRequest();
        String traceId = getTraceId(request);
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.error("""
                [{}] [CONTROLLER EXCEPTION] Exception in {}.{}()
                  HTTP Method: {}
                  Request URI: {}
                  Parameters: {}
                  Exception Type: {}
                  Exception Message: {}
                  Method Arguments: {}""",
                traceId,
                className,
                methodName,
                request != null ? request.getMethod() : "N/A",
                request != null ? request.getRequestURI() : "N/A",
                request != null ? getQueryString(request) : "N/A",
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                formatArguments(joinPoint.getArgs()),
                exception);
    }

    /**
     * After throwing advice for Service layer
     * Logs business logic exceptions
     */
    @AfterThrowing(pointcut = "serviceLayer()", throwing = "exception")
    public void logServiceException(JoinPoint joinPoint, Throwable exception) {
        String traceId = getTraceIdFromRequest();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.error("""
                [{}] [SERVICE EXCEPTION] Exception in {}.{}()
                  Exception Type: {}
                  Exception Message: {}
                  Method Arguments: {}""",
                traceId,
                className,
                methodName,
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                formatArguments(joinPoint.getArgs()),
                exception);
    }

    /**
     * After throwing advice for Repository layer
     * Logs data access exceptions
     */
    @AfterThrowing(pointcut = "repositoryLayer()", throwing = "exception")
    public void logRepositoryException(JoinPoint joinPoint, Throwable exception) {
        String traceId = getTraceIdFromRequest();
        
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String className = signature.getDeclaringType().getSimpleName();
        String methodName = signature.getName();

        log.error("""
                [{}] [REPOSITORY EXCEPTION] Data access exception in {}.{}()
                  Exception Type: {}
                  Exception Message: {}
                  Method Arguments: {}""",
                traceId,
                className,
                methodName,
                exception.getClass().getSimpleName(),
                exception.getMessage(),
                formatArguments(joinPoint.getArgs()),
                exception);
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
     * Get trace ID from request attribute
     */
    private String getTraceId(HttpServletRequest request) {
        if (request != null) {
            Object traceId = request.getAttribute("traceId");
            if (traceId != null) {
                return traceId.toString();
            }
            String headerTraceId = request.getHeader("X-Trace-Id");
            if (headerTraceId != null && !headerTraceId.isEmpty()) {
                return headerTraceId;
            }
        }
        return "NO-TRACE";
    }

    /**
     * Get trace ID from current request context
     */
    private String getTraceIdFromRequest() {
        HttpServletRequest request = getCurrentRequest();
        return getTraceId(request);
    }

    /**
     * Get query string from request
     */
    private String getQueryString(HttpServletRequest request) {
        String queryString = request.getQueryString();
        return queryString != null ? queryString : "none";
    }

    /**
     * Format method arguments for logging
     */
    private String formatArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        return Arrays.stream(args)
                .map(arg -> {
                    if (arg == null) {
                        return "null";
                    }
                    String argString = arg.toString();
                    // Limit string length to avoid huge logs
                    if (argString.length() > 300) {
                        return argString.substring(0, 300) + "... [truncated]";
                    }
                    return argString;
                })
                .collect(Collectors.joining(", ", "[", "]"));
    }
}
