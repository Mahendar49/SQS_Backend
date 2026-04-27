package com.smartqueue.queue.util;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public final class AuthRoleUtil {

    private AuthRoleUtil() {
    }

    public static Set<String> parseRoles(String rolesHeader) {
        if (rolesHeader == null || rolesHeader.isBlank()) {
            return Set.of();
        }

        String cleaned = rolesHeader.replace("[", "").replace("]", "");

        return Arrays.stream(cleaned.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .collect(Collectors.toSet());
    }
}
