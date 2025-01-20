package com.infogain.extensions;

import com.infogain.annotations.FrameworkAnnotations;
import org.junit.jupiter.api.extension.*;
import java.util.Arrays;
import java.util.Optional;

public class FilterTestsExtension implements ExecutionCondition {

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
        // Fetch filters passed via system properties
        String categoryFilter = System.getProperty("category");
        String serviceFilter = System.getProperty("service");
        String authorFilter = System.getProperty("author");

        // Fetch the FrameworkAnnotations from the test method or class
        Optional<FrameworkAnnotations> annotation = context.getElement()
                .flatMap(el -> Optional.ofNullable(el.getAnnotation(FrameworkAnnotations.class)));

        if (annotation.isPresent()) {
            FrameworkAnnotations frameworkAnnotations = annotation.get();

            // Check Category
            if (categoryFilter != null && !categoryFilter.isEmpty()) {
                if (!frameworkAnnotations.Category().name().equalsIgnoreCase(categoryFilter)) {
                    return ConditionEvaluationResult
                            .disabled("Test disabled due to category filter: " + categoryFilter);
                }
            }

            // Check Service
            if (serviceFilter != null && !serviceFilter.isEmpty()) {
                if (!frameworkAnnotations.Service().name().equalsIgnoreCase(serviceFilter)) {
                    return ConditionEvaluationResult.disabled("Test disabled due to service filter: " + serviceFilter);
                }
            }

            // Check Author
            if (authorFilter != null && !authorFilter.isEmpty()) {
                boolean authorMatches = Arrays.stream(frameworkAnnotations.Author())
                        .anyMatch(author -> author.name().equalsIgnoreCase(authorFilter));
                if (!authorMatches) {
                    return ConditionEvaluationResult.disabled("Test disabled due to author filter: " + authorFilter);
                }
            }

            return ConditionEvaluationResult.enabled("Test enabled for specified filters.");
        }

        return ConditionEvaluationResult.enabled("No FrameworkAnnotations found. Test enabled by default.");
    }
}
