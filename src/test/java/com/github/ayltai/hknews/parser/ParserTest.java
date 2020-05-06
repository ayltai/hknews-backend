package com.github.ayltai.hknews.parser;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import com.github.ayltai.hknews.UnitTest;
import com.github.ayltai.hknews.data.repository.SourceRepository;

@DataMongoTest
public abstract class ParserTest extends UnitTest {
    @Autowired
    protected SourceRepository sourceRepository;
}
