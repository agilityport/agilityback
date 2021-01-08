package org.smorgrav.agilityback.jobs;

/**
 * Check for stale or deleted competitions
 * <p>
 * Eg.
 * 1 Check stale competitions (could be deleted at source, or we may assume that they are cancelled or finished)
 * 2 Check competitions with multiple sourceids that they don't have duplicates with individual sourceids
 * 3 Check for potential duplicates or other database issues
 */
public class CompetitionMaintenance {
}
