package com.github.ayltai.hknews.service;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import com.github.ayltai.hknews.UnitTest;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.data.repository.SourceRepository;

@SpringBootTest
public final class SourceServiceTest extends UnitTest {
    @Autowired
    private SourceRepository sourceRepository;

    @Test
    public void given_noSource_when_getSources_then_returnSources() {
        final Page<Source> sources = new SourceService(this.sourceRepository).getAllSources(PageRequest.of(0, 20));

        Assert.assertNotNull(sources);
        Assert.assertEquals(15, sources.getTotalElements());
    }
}
