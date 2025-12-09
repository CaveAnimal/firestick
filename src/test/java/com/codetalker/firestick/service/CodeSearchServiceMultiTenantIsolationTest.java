package com.codetalker.firestick.service;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Verifies logical multi-tenancy isolation in {@link CodeSearchService}.
 * Indexes identical search terms under different app names and ensures
 * search is restricted when a non-default app is specified, but aggregated
 * when app is null or "default" (backward compatible behavior).
 */
@SpringBootTest
@ActiveProfiles("test")
class CodeSearchServiceMultiTenantIsolationTest {

    @Autowired
    private CodeSearchService codeSearchService;

    @Test
    void searchRestrictedToSpecifiedNonDefaultApp() throws Exception {
        // Index same term under two different apps
        codeSearchService.indexCode("A1", "alpha", "public class AlphaOne { void m(){ /* SharedTerm */ } }");
        codeSearchService.indexCode("A2", "alpha", "public class AlphaTwo { void m(){ /* SharedTerm */ } }");
        codeSearchService.indexCode("B1", "beta", "public class BetaOne { void m(){ /* SharedTerm */ } }");
        codeSearchService.indexCode("B2", "beta", "public class BetaTwo { void m(){ /* SharedTerm */ } }");

        // Searches scoped by app
        List<String> alphaHits = codeSearchService.searchCode("SharedTerm", "alpha");
        List<String> betaHits = codeSearchService.searchCode("SharedTerm", "beta");

        // Backward-compatible wildcard behavior for null / default app
        List<String> nullAppHits = codeSearchService.searchCode("SharedTerm"); // null app -> all
        List<String> defaultAppHits = codeSearchService.searchCode("SharedTerm", "default");

        // Unknown app should yield no results
        List<String> unknownAppHits = codeSearchService.searchCode("SharedTerm", "nosuchapp");

        assertThat(alphaHits).containsExactlyInAnyOrder("A1", "A2");
        assertThat(betaHits).containsExactlyInAnyOrder("B1", "B2");
        assertThat(nullAppHits).containsExactlyInAnyOrder("A1", "A2", "B1", "B2");
        assertThat(defaultAppHits).containsExactlyInAnyOrder("A1", "A2", "B1", "B2");
        assertThat(unknownAppHits).isEmpty();
    }
}
