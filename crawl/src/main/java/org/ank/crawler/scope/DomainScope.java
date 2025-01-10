package org.ank.crawler.scope;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Basic Domain scope: only allow URIs in a certain domain (including subdomains).
 * <p>
 * "scope" can be more generalized with multiple rules,
 * but here I illustrate a single domain-based approach.
 */
public class DomainScope implements Scope {

    private static final Logger LOGGER = Logger.getLogger(DomainScope.class.getName());
    private final String domain; // e.g. "teya.com"

    public DomainScope(String domain) {
        if (domain == null || domain.isBlank()) {
            throw new IllegalArgumentException("Domain cannot be null or empty");
        }
        this.domain = domain.toLowerCase();
    }

    @Override
    public boolean isInScope(String uri) {
        if (uri == null || uri.isBlank()) return false;
        if (!uri.startsWith("http://") && !uri.startsWith("https://")) return false;

        try {
            final String baseDomain = extractBaseDomain(uri);
            return domain.equals(baseDomain);
        } catch (MalformedURLException e) {
            return false; // Skip malformed URLs
        }
    }

    /**
     * Extracts the base domain (e.g., "teya.com") from a given URL.
     *
     * @param url The input URL.
     * @return The base domain.
     * @throws MalformedURLException If the URL is invalid.
     */
    public static String extractBaseDomain(String url) throws MalformedURLException {
        final String host = new URL(url).getHost().toLowerCase();
        final String[] parts = host.split("\\.");
        if (parts.length > 2 && !Character.isDigit(parts[0].charAt(0))) {
            // Remove the first segment (subdomain) if it's not an IP address
            return String.join(".", parts[parts.length - 2], parts[parts.length - 1]);
        }
        return host;
    }

}
