package com.github.ayltai.hknews.parser;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ApiService;
import com.github.ayltai.hknews.net.ApiServiceFactory;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public final class TheStandardParserTest extends ParserTest {
    @Test
    public void testGetItems() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/thestandard_list.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).postHtml("http://www.thestandard.com.hk/ajax_sections_list.php", 4, 1);
                Mockito.doReturn(response).when(call).execute();

                final Collection<Item> items = new TheStandardParser(factory, this.sourceRepository, this.itemRepository).getItems(new Category(Collections.singletonList("http://www.thestandard.com.hk/ajax_sections_list.php?sid=4"), "即時港聞"));

                Assert.assertEquals("Incorrect image count", 20, items.size());
                Assert.assertEquals("Incorrect item description", "National Party gets more time to file objections ", items.toArray(new Item[0])[1].getTitle());
            }
        }
    }

    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/thestandard_details.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://www.thestandard.com.hk/breaking-news.php?id=111401&story_id=111401&d_str=20180801&sid=4");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("http://www.thestandard.com.hk/breaking-news.php?id=111401&story_id=111401&d_str=20180801&sid=4");

                final Item updatedItem = new TheStandardParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "The Security Bureau has extended the deadline for the Hong Kong National Party to respond to the government&#039;s plan to ban it.<br><br>Earlier, the police recommended to the bureau to outlaw the pro-independence group, citing the Societies Ordinance, RTHK reports.<br><br>The party was originally given 21 days to respond, and the deadline was August 7. But Secretary for Security, John Lee Ka-chiu, has now given it until September 4.<br><br>The party&#039;s convener, Chan Ho-tin, earlier called on the government to move the deadline to October, saying he needed more time to prepare a response.<br><br>Chan said earlier that the police had spent two years keeping him under surveillance and had compiled an 800-page report on his activities, so it was unfair to require him to respond within such a short period of time.<br><br>Article 8 of the Societies Ordinance stipulates that officials may recommend an order prohibiting the operation of a society out of national security or public safety concerns, or the protection of the rights and freedoms of others.<br><br>The ordinance also states that the security chief may ban the operation of a group if it has a \"connection with a foreign political organisation or a political organisation of Taiwan.&#039;&#039;<br><br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 1, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "The National Party convener, Chan Ho-tin, had called on the government to move the deadline to respond to October.", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "http://www.thestandard.com.hk/images/instant_news/20180801/20180801111009111401contentPhoto1.jpg", updatedItem.getImages().get(0).getImageUrl());
            }
        }
    }
}
