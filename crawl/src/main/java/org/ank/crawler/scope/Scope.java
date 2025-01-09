package org.ank.crawler.scope;

/**
 * "Scope" decides if a given URI is included or excluded from the crawl.
 */
public interface Scope {

    /**
     * Determines whether the given URI is in scope.
     */
    boolean isInScope(String uri);
}
