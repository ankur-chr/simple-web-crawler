package org.ank.crawler.scope;

import constant.TestConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests DomainScope's logic for determining if URLs are in scope
 * (i.e., belong to a specified domain).
 */
class DomainScopeTest {

    private DomainScope domainScope;

    @BeforeEach
    void setUp() {
        // Suppose we only allow teya.com (including subdomains)
        domainScope = new DomainScope(TestConstants.TEYA_COM_DOMAIN);
    }

    @Test
    void testNullOrEmptyUrl() {
        assertFalse(domainScope.isInScope(null), "Null URL should be out of scope");
        assertFalse(domainScope.isInScope(""), "Empty URL should be out of scope");
    }

    @Test
    void testNonHttp() {
        // e.g., ftp:// or mailto: => out of scope
        assertFalse(domainScope.isInScope(TestConstants.NON_HTTP_URL_FTP), "FTP URL should be out of scope");
        assertFalse(domainScope.isInScope(TestConstants.NON_HTTP_URL_MAILTO), "Mailto URL should be out of scope");
    }

    @Test
    void testMalformedUrl() {
        // "htp:/' => definitely malformed
        assertFalse(domainScope.isInScope(TestConstants.MALFORMED_URL), "Malformed URL should be out of scope");
    }

    @Test
    void testExactDomain() {
        // Should pass: "teya.com", or "teya.com/about"
        assertTrue(domainScope.isInScope(TestConstants.VALID_SEED_URL), "Exact domain URL should be in scope");
        assertTrue(domainScope.isInScope(TestConstants.VALID_ABOUT_URL), "Exact domain URL with path should be in scope");
    }

    @Test
    void testSubdomain() {
        // Should pass: "help.teya.com/docs" or "portal.teya.com"
        assertTrue(domainScope.isInScope(TestConstants.SUBDOMAIN_URL_2), "Subdomain URL should be in scope");
        assertTrue(domainScope.isInScope(TestConstants.SUBDOMAIN_URL_1), "Portal subdomain URL should be in scope");
    }

    @Test
    void testDifferentDomain() {
        // Should fail: google.com or teya.co.uk
        assertFalse(domainScope.isInScope(TestConstants.GOOGLE_SEARCH_URL_TEYA), "Different domain URL should be out of scope");
        assertFalse(domainScope.isInScope(TestConstants.TEYA_UK_URL), "Similar but different domain should be out of scope");
    }
}
