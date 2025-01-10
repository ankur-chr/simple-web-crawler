package org.ank.crawler.scope;

import constant.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.MalformedURLException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests {@link DomainScope}'s logic for determining if URLs are in scope
 * and extracting domains from URLs.
 */
class DomainScopeTest {

    private DomainScope domainScope;

    @BeforeEach
    void setUp() {
        // Initialize DomainScope with teya.com as the allowed domain
        domainScope = new DomainScope(TestConstants.TEYA_COM_DOMAIN);
    }

    // ----------------------- Tests for isInScope -----------------------

    @Test
    void testNullOrEmptyUrl() {
        assertFalse(domainScope.isInScope(null), "Null URL should be out of scope");
        assertFalse(domainScope.isInScope(""), "Empty URL should be out of scope");
    }

    @Test
    void testNonHttp() {
        // Non-HTTP(S) protocols like FTP or mailto should be out of scope
        assertFalse(domainScope.isInScope(TestConstants.NON_HTTP_URL_FTP), "FTP URL should be out of scope");
        assertFalse(domainScope.isInScope(TestConstants.NON_HTTP_URL_MAILTO), "Mailto URL should be out of scope");
    }

    @Test
    void testMalformedUrl() {
        // Malformed URLs should be out of scope
        assertFalse(domainScope.isInScope(TestConstants.MALFORMED_URL), "Malformed URL should be out of scope");
    }

    @Test
    void testExactDomain() {
        // Valid URLs within the exact domain
        assertTrue(domainScope.isInScope(TestConstants.VALID_SEED_URL), "Exact domain URL should be in scope");
        assertTrue(domainScope.isInScope(TestConstants.VALID_ABOUT_URL), "Exact domain URL with path should be in scope");
    }

    @Test
    void testSubdomain() {
        // Subdomains of the allowed domain should be in scope
        assertTrue(domainScope.isInScope(TestConstants.SUBDOMAIN_URL_1), "Subdomain URL should be in scope");
        assertTrue(domainScope.isInScope(TestConstants.SUBDOMAIN_URL_2), "Another subdomain URL should be in scope");
    }

    @Test
    void testDifferentDomain() {
        // URLs with different domains should be out of scope
        assertFalse(domainScope.isInScope(TestConstants.GOOGLE_SEARCH_URL_TEYA), "Different domain URL should be out of scope");
        assertFalse(domainScope.isInScope(TestConstants.TEYA_UK_URL), "Similar but different domain should be out of scope");
    }

    // ----------------------- Tests for extractBaseDomain -----------------------

    @Test
    void extractDomain_shouldReturnDomainForValidUrl() throws MalformedURLException {
        final String domain = DomainScope.extractBaseDomain(TestConstants.VALID_SEED_URL);
        assertEquals("teya.com", domain, "The extracted domain should match the expected value");
    }

    @Test
    void extractDomain_shouldHandleUrlWithSubdomain() throws MalformedURLException {
        final String domain = DomainScope.extractBaseDomain(TestConstants.SUBDOMAIN_URL_1);
        assertEquals("teya.com", domain, "The extracted domain should strip subdomains and match the base domain");
    }

    @Test
    void extractDomain_shouldHandleUrlWithoutWww() throws MalformedURLException {
        final String domain = DomainScope.extractBaseDomain(TestConstants.DOMAIN_ONLY_URL);
        assertEquals("teya.com", domain, "The extracted domain should match even without www");
    }

    @Test
    void extractDomain_shouldThrowExceptionForInvalidUrl() {
        assertThrows(MalformedURLException.class, () -> DomainScope.extractBaseDomain(TestConstants.MALFORMED_URL), "Should throw MalformedURLException for an invalid URL");
    }

    @Test
    void extractDomain_shouldHandleMultiSubdomains() throws MalformedURLException {
        final String domain = DomainScope.extractBaseDomain(TestConstants.MULTI_SUBDOMAIN_URL);
        assertEquals("teya.com", domain, "The extracted domain should match for URLs with multiple subdomains");
    }

    @Test
    void extractDomain_shouldHandleIpAddress() throws MalformedURLException {
        final String domain = DomainScope.extractBaseDomain(TestConstants.IP_ADDRESS_URL);
        assertEquals("192.168.1.1", domain, "The extracted domain should match the IP address");
    }

    @Test
    void extractDomain_shouldHandleUrlWithDifferentProtocols() throws MalformedURLException {
        final String domainFtp = DomainScope.extractBaseDomain(TestConstants.NON_HTTP_URL_FTP);
        final String domainHttp = DomainScope.extractBaseDomain(TestConstants.VALID_SEED_URL);

        assertEquals("teya.com", domainFtp, "The extracted domain should match for FTP protocol");
        assertEquals("teya.com", domainHttp, "The extracted domain should match for HTTP protocol");
    }

    @Test
    void extractDomain_shouldHandleUrlWithoutPath() throws MalformedURLException {
        final String domain = DomainScope.extractBaseDomain(TestConstants.DOMAIN_ONLY_URL);
        assertEquals("teya.com", domain, "The extracted domain should match even without a path");
    }
}
