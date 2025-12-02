package com.codetalker.firestick.repository;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.codetalker.firestick.model.IndexingObject;

@SpringBootTest
@ActiveProfiles("test")
public class IndexingObjectRepositoryTest {

    @Autowired
    private IndexingObjectRepository repository;

    @Test
    void save_and_find_by_job_id() {
        IndexingObject o = new IndexingObject();
        o.setJobId(999L);
        o.setObjectType("FILE");
        o.setObjectName("src/Some.java");
        o.setStartedAt(Instant.now());
        o.setElapsedMs(123L);
        repository.save(o);

        var list = repository.findByJobId(999L);
        assertThat(list).isNotEmpty();
        var found = list.stream().filter(x -> "src/Some.java".equals(x.getObjectName())).findFirst();
        assertThat(found).isPresent();
    }
}
