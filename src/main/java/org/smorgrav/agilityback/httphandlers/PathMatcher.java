package org.smorgrav.agilityback.httphandlers;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class PathMatcher {

    private final String[] parts;
    private final Map<String, String> matched = new HashMap<>();

    PathMatcher(URI uri) {
        parts = trimSlash(uri.getPath()).split("/");
    }

    private static boolean isLastSegment(String[] parts, int index) {
        return parts.length - 1 == index;
    }

    private static String trimSlash(String path) {
        String trimmed = path.trim();
        if (trimmed.startsWith("/")) {
            trimmed = trimmed.substring(1);
        }
        if (trimmed.endsWith("/")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1);
        }
        return trimmed;
    }

    public boolean match(String template) {
        matched.clear();
        String[] templateParts = trimSlash(template).split("/");
        boolean lastSegmentIsWildcard = templateParts[templateParts.length - 1].equals("*");

        for (int i = 0; i < templateParts.length; i++) {
            if (parts.length > i) {
                if (templateParts[i].startsWith("{")) {
                    String templateKey = templateParts[i].substring(1, templateParts[i].length() - 1);
                    matched.put(templateKey, parts[i]);
                } else if (templateParts[i].equals("*")) {
                    // Template supports ignore this segment (wildcard without matching a key)
                } else if (!templateParts[i].equals(parts[i])) {
                    return false;
                }
            } else {
                // The template is longer than the path - which is good if the template is one longer and the last segment is a wildcard
                if (lastSegmentIsWildcard && isLastSegment(templateParts, i)) {
                    return true;
                } else {
                    return false;
                }
            }
        }

        // If parts are longer than template - this is valid only if the last template segment is a wildcard
        if (parts.length > templateParts.length) {
            return lastSegmentIsWildcard;
        }

        return true;
    }

    public String get(String key) {
        return matched.get(key);
    }

}
