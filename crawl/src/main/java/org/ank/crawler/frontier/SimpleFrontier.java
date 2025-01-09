package org.ank.crawler.frontier;

import org.ank.crawler.Main;
import org.ank.crawler.fetcher.HtmlFetcher;
import org.ank.crawler.fetcher.JsoupHtmlFetcher;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.Scope;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A minimal "Frontier" that:
 * - Uses a BlockingQueue for URIs to process
 * - Spawns a fixed number of worker threads
 * - Each worker fetches + processes each URI
 * - Discovered URIs are added if they pass scope
 */
public class SimpleFrontier implements Frontier {

    private static final Logger LOGGER = Logger.getLogger(SimpleFrontier.class.getName());
    private final Set<String> visited = ConcurrentHashMap.newKeySet();
    private final BlockingQueue<String> uriQueue = new LinkedBlockingQueue<>();

    // concurrency
    private final int threadCount;
    private ExecutorService executor;

    // to signal we've started
    private final AtomicBoolean started = new AtomicBoolean(false);

    public SimpleFrontier(int threadCount) {
        this.threadCount = threadCount;
    }

    /**
     * Schedule (enqueue) a new URL to be crawled, if it hasn't already been visited.
     *
     * @param uri The URL to add to the crawl queue.
     */
    @Override
    public void schedule(String uri) {
        // Only schedule if not visited
        if (!visited.contains(uri)) {
            uriQueue.offer(uri);
        }
    }

    /**
     * Starts the crawl by launching worker threads to process URLs from the queue.
     * This method is safe to call only once; subsequent calls do nothing.
     *
     * @param processors The list of processors that parse/extract links or data from fetched pages.
     * @param scope      The scope to determine if discovered links are in scope.
     */
    @Override
    public void start(List<Processor> processors, Scope scope) {
        if (!started.compareAndSet(false, true)) {
            return; // already started
        }

        executor = Executors.newFixedThreadPool(threadCount);

        // Launch workers
        for (int i = 0; i < threadCount; i++) {
            executor.submit(() -> workerLoop(processors, scope));
        }

        // Wait until done or user decides to stop
        executor.shutdown();
        try {
            // For example, 10 minutes max
            if (!executor.awaitTermination(10, TimeUnit.MINUTES)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    /**
     * A worker loop that pulls URIs from the queue and processes them.
     * Terminates if the queue is empty for a brief period or the thread is interrupted.
     *
     * @param processors The pipeline to handle fetched content.
     * @param scope      Scope validation for discovered links.
     */
    private void workerLoop(List<Processor> processors, Scope scope) {
        // While the queue has URIs or we haven't been interrupted
        try {
            while (!Thread.currentThread().isInterrupted()) {
                String uri = uriQueue.poll(1, TimeUnit.SECONDS);
                if (uri == null) {
                    // No new URIs for a while => might be done
                    return;
                }
                processUri(uri, processors, scope);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * Processes a single URI: fetches content, applies the processor chain,
     * and enqueues discovered links if in scope and not visited yet.
     *
     * @param uri        The URL being processed.
     * @param processors The pipeline to handle fetched content.
     * @param scope      Scope validation for discovered links.
     */
    private void processUri(String uri, List<Processor> processors, Scope scope) {
        if (!visited.add(uri)) {
            // Already visited
            return;
        }
        // We fetch once, then run it through the processors
        try {
            // For demonstration, let's do a single fetch here
            HtmlFetcher fetcher = new JsoupHtmlFetcher();
            var fetchedContent = fetcher.fetch(uri);

            // Then apply each processor in sequence
            Set<String> newlyDiscovered = new HashSet<>();
            var currentContent = fetchedContent;

            for (Processor proc : processors) {
                Set<String> discovered = proc.process(currentContent, uri);
                if (discovered != null) {
                    newlyDiscovered.addAll(discovered);
                }
                // In more advanced systems, you might transform content for next processor
            }

            // If we discovered new URIs, check scope and schedule them
            for (String discoveredUri : newlyDiscovered) {
                if (scope.isInScope(discoveredUri) && !visited.contains(discoveredUri)) {
                    uriQueue.offer(discoveredUri);
                }
            }
        } catch (IOException e) {
            // Log the error message and exception (Any failures in processing URIs will be logged at WARN level)
            LOGGER.log(Level.WARNING, "Failed to fetch {0}: {1}", new Object[]{uri, e.getMessage()});
        } catch (Exception e) {
            // Log the exception stack trace (Any failures in processing URIs will be logged at WARN level)
            LOGGER.log(Level.WARNING, "Unexpected error occurred while processing URI: " + uri, e);
        }
    }

    /**
     * Returns an unmodifiable set of all URLs that have been visited or are being visited.
     *
     * @return A read-only Set of visited URLs.
     */
    @Override
    public Set<String> getVisited() {
        return Collections.unmodifiableSet(visited);
    }
}
