package org.smorgrav.agilityback.jobs;

import org.junit.Ignore;
import org.junit.Test;
import org.smorgrav.agilityback.sources.sagik.SagikSource;
import org.smorgrav.agilityback.storage.MockStorage;
import org.smorgrav.agilityback.storage.StorageValues;

import java.io.IOException;

public class ImportNewCompetitionsTest {

    @Ignore
    @Test
    public void manual_test_of_import_from_sagik() throws IOException {
        StorageValues config = new StorageValues("sagik")
                .set("from", "2020-10-01")
                .set("enabled", true);
        ImportNewCompetitions.importNewCompetitions(new SagikSource(), new MockStorage(), 100);
    }

}