package com.bookstore.listeners;

import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.testng.*;


@Slf4j
public class TestListener implements ITestListener, ISuiteListener {

    private long suiteStartTime;

    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        log.info("====================================================");
        log.info("Starting Test Suite: {}", suite.getName());
        log.info("====================================================");
    }

    @Override
    public void onFinish(ISuite suite) {
        long duration = System.currentTimeMillis() - suiteStartTime;
        log.info("====================================================");
        log.info("Test Suite Finished: {}", suite.getName());
        log.info("Total Duration: {} ms ({} seconds)", duration, duration / 1000);
        log.info("====================================================");
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info(">>> Starting Test: {}.{}",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        log.info("✓ PASSED: {}.{} - Duration: {} ms",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName(),
                duration);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("✗ FAILED: {}.{}",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName());

        if (result.getThrowable() != null) {
            log.error("Failure Reason: {}", result.getThrowable().getMessage());

            // Attach failure details to Allure report
            saveTextLog(result.getThrowable().getMessage());
            saveStackTrace(getStackTrace(result.getThrowable()));
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("⊗ SKIPPED: {}.{}",
                result.getTestClass().getRealClass().getSimpleName(),
                result.getMethod().getMethodName());

        if (result.getThrowable() != null) {
            log.warn("Skip Reason: {}", result.getThrowable().getMessage());
        }
    }


    @Attachment(value = "Failure Log", type = "text/plain")
    private String saveTextLog(String message) {
        return message;
    }


    @Attachment(value = "Stack Trace", type = "text/plain")
    private String saveStackTrace(String stackTrace) {
        return stackTrace;
    }


    private String getStackTrace(Throwable throwable) {
        StringBuilder sb = new StringBuilder();
        sb.append(throwable.toString()).append("\n");
        for (StackTraceElement element : throwable.getStackTrace()) {
            sb.append("\tat ").append(element.toString()).append("\n");
        }
        return sb.toString();
    }
}