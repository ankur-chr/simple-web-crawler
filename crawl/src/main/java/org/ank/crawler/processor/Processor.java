package org.ank.crawler.processor;

import org.ank.crawler.fetcher.FetchedContent;

import java.util.Set;

/**
 * A "Processor" is a stage in the pipeline that processes the fetched content.
 * Could do extraction, analysis, storage, etc.
 * <p>
 * In a real application, "Processor" can be one in a chain: parse HTML, extract links, store data, etc.
 */
public interface Processor {

    /**
     * Processes the content and returns any discovered URIs.
     * This is where you'd parse the content, extract outlinks, etc.
     *
     * @param fetchedContent The raw or structured content from fetcher
     * @param sourceUri      The URI that led to this content
     * @return A set of discovered URIs that might be scheduled
     */
    Set<String> process(FetchedContent fetchedContent, String sourceUri);
}
