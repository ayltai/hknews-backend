package com.github.ayltai.hknews.parser;

import com.github.ayltai.hknews.UnitTest;
import com.github.ayltai.hknews.data.repository.ItemRepository;
import com.github.ayltai.hknews.data.repository.SourceRepository;
import com.github.ayltai.hknews.net.ApiServiceFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.lang.NonNull;

@DataMongoTest
public abstract class ParserTest extends UnitTest {
    @Autowired
    protected SourceRepository sourceRepository;

    @Autowired
    protected ItemRepository itemRepository;
}
