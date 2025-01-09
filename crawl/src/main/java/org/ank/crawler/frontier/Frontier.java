package org.ank.crawler.frontier;

import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.Scope;

import java.util.List;
import java.util.Set;

/**
 * The Frontier manages scheduling of URIs to be fetched and processed.
 * It can typically handle concurrency, politeness, etc.
 */
public interface Frontier {

    /**
     * Schedule (enqueue) a URI for processing if it hasn't been scheduled before.
     */
    void schedule(String uri);

    /**
     * Start the frontier's processing loop, fetching URIs,
     * applying Processors, and scheduling new ones as discovered.
     */
    void start(List<Processor> processors, Scope scope);

    /**
     * Get all visited URIs after the crawl.
     */
    Set<String> getVisited();
}
