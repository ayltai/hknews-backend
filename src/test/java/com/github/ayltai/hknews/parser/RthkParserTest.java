package com.github.ayltai.hknews.parser;

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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public final class RthkParserTest extends ParserTest {
    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/rthk.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://news.rthk.hk/rthk/ch/component/k2/1404750-20180702.htm");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("http://news.rthk.hk/rthk/ch/component/k2/1404750-20180702.htm");

                final Item updatedItem = new RthkParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "香港航空宣布，昨日受熱帶風暴派比安影響而延誤的4班往來香港至沖繩航班，將改為今日出發。<br />\n<br />\n受影響的航班包括HX6652、HX6653、HX658、HX659，更改後的航班編號為HX6652D、HX6653D、HX658D、HX659D。", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 1, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "香港航空宣布，昨日延誤的4班往來香港至沖繩航班，將改為今日出發。（港台圖片）", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "http://newsstatic.rthk.hk/images/mfile_1404750_1_20180702130147.jpg", updatedItem.getImages().get(0).getImageUrl());
            }
        }
    }
}
