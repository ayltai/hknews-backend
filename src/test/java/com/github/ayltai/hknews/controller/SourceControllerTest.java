package com.github.ayltai.hknews.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.github.ayltai.hknews.UnitTest;
import com.github.ayltai.hknews.data.model.Source;
import com.github.ayltai.hknews.service.SourceService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@WebMvcTest(
    controllers              = SourceController.class,
    excludeAutoConfiguration = EmbeddedMongoAutoConfiguration.class)
public final class SourceControllerTest extends UnitTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SourceService sourceService;

    @Test
    public void when_getSources_then_return15Sources() throws Exception {
        Mockito.when(this.sourceService.getAllSources(ArgumentMatchers.any(Pageable.class))).thenReturn(this.getSources());

        this.mockMvc
            .perform(MockMvcRequestBuilders.get("/sources"))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.content().contentTypeCompatibleWith("application/json"))
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"name\":\"蘋果日報\"")))
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"name\":\"港聞\"")))
            .andExpect(MockMvcResultMatchers.content().string(Matchers.containsString("\"urls\":[\"http://www.scmp.com/rss/92/feed\"]")));
    }

    private Page<Source> getSources() throws IOException {
        final InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("sources.json");
        if (inputStream == null) return null;

        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            List<Source> sources = new Gson().fromJson(reader, new TypeToken<List<Source>>() {}.getType());

            return new PageImpl<>(sources);
        }
    }
}
