package org.ank.crawler.frontier;

import constant.TestConstants;
import org.ank.crawler.processor.Processor;
import org.ank.crawler.scope.Scope;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for {@link SimpleFrontier}
 */
class SimpleFrontierTest {

    private SimpleFrontier frontier;
    private Scope mockScope;
    private Processor mockProcessor;

    @BeforeEach
    void setUp() {
        frontier = new SimpleFrontier(2);

        mockScope = mock(Scope.class);
        when(mockScope.isInScope(anyString())).thenReturn(true);

        mockProcessor = mock(Processor.class);
        when(mockProcessor.process(any(), any())).thenReturn(Set.of());
    }

    @Test
    void testSingleSeedNoDiscoveries() throws InterruptedException {
        frontier.schedule(TestConstants.VALID_SEED_URL);

        frontier.start(List.of(mockProcessor), mockScope);

        TimeUnit.SECONDS.sleep(2);
        Set<String> visited = frontier.getVisited();

        assertEquals(1, visited.size());
        assertTrue(visited.contains(TestConstants.VALID_SEED_URL));
    }

    @Test
    void testDiscoverNewLinks() throws InterruptedException {
        when(mockProcessor.process(any(), any()))
                .thenReturn(Set.of(TestConstants.CHILD_URL_1, TestConstants.CHILD_URL_2));

        frontier.schedule(TestConstants.VALID_SEED_URL);
        frontier.start(List.of(mockProcessor), mockScope);

        TimeUnit.SECONDS.sleep(2);

        Set<String> visited = frontier.getVisited();
        assertTrue(visited.contains(TestConstants.VALID_SEED_URL));
        assertTrue(visited.contains(TestConstants.CHILD_URL_1));
        assertTrue(visited.contains(TestConstants.CHILD_URL_2));
        assertEquals(3, visited.size());
    }

    @Test
    void testScopeFiltering() throws InterruptedException {
        when(mockScope.isInScope(TestConstants.VALID_SEED_URL)).thenReturn(true);
        when(mockScope.isInScope(TestConstants.OUT_OF_SCOPE_URL)).thenReturn(false);

        when(mockProcessor.process(any(), any()))
                .thenReturn(Set.of(TestConstants.OUT_OF_SCOPE_URL));

        frontier.schedule(TestConstants.VALID_SEED_URL);
        frontier.start(List.of(mockProcessor), mockScope);

        TimeUnit.SECONDS.sleep(2);
        Set<String> visited = frontier.getVisited();

        assertEquals(1, visited.size());
        assertTrue(visited.contains(TestConstants.VALID_SEED_URL));
        assertFalse(visited.contains(TestConstants.OUT_OF_SCOPE_URL));
    }
}
