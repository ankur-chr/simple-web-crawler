package org.ank.crawler.controller;

import constant.TestConstants;
import org.ank.crawler.frontier.Frontier;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests CrawlController's high-level orchestration
 * (seed scheduling, scope checks, frontier start, etc.).
 */
class CrawlControllerTest {

    private Scope mockScope;
    private Frontier mockFrontier;
    private CrawlController controller;

    @BeforeEach
    void setUp() {
        mockScope = mock(Scope.class);
        mockFrontier = mock(Frontier.class);
        final Processor mockProcessor = mock(Processor.class);

        // Our controller uses just 1 processor for simplicity
        controller = new CrawlController(mockScope, List.of(mockProcessor), mockFrontier);
    }

    @Test
    void testSeedOutOfScope() {
        when(mockScope.isInScope(TestConstants.OUT_OF_SCOPE_URL)).thenReturn(false);

        // Attempt to crawl => out of scope => no frontier scheduling
        controller.beginCrawl(TestConstants.OUT_OF_SCOPE_URL);
        verify(mockFrontier, never()).schedule(anyString());
        verify(mockFrontier, never()).start(anyList(), any());
    }

    @Test
    void testSeedInScope() {
        when(mockScope.isInScope(TestConstants.VALID_SEED_URL)).thenReturn(true);

        controller.beginCrawl(TestConstants.VALID_SEED_URL);

        // We expect the frontier to be scheduled & started
        verify(mockFrontier).schedule(TestConstants.VALID_SEED_URL);
        verify(mockFrontier).start(anyList(), eq(mockScope));
    }

    @Test
    void testGetVisitedUris() {
        // Suppose the frontier already visited some URLs
        Set<String> visitedMock = Set.of(TestConstants.VALID_SEED_URL, TestConstants.VALID_PORTAL_URL);
        when(mockFrontier.getVisited()).thenReturn(visitedMock);

        // The controller delegates to frontier
        Set<String> visited = controller.getVisitedUris();
        assertEquals(2, visited.size());
        assertTrue(visited.contains(TestConstants.VALID_SEED_URL));
        assertTrue(visited.contains(TestConstants.VALID_PORTAL_URL));
    }
}
