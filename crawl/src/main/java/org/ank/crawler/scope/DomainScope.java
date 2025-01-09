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
        this.domain = domain.toLowerCase();
    }

    @Override
    public boolean isInScope(String uri) {
        if (uri == null || uri.isBlank()) return false;
        if (!uri.startsWith("http://") && !uri.startsWith("https://")) return false;

        try {
            var host = new URL(uri).getHost().toLowerCase();
            host = host.replaceFirst("^www\\.", "");
            return host.endsWith(domain);
        } catch (MalformedURLException e) {
            LOGGER.log(Level.FINE, "Malformed URL in scope check: " + uri, e);
            return false;
        }
    }
}
