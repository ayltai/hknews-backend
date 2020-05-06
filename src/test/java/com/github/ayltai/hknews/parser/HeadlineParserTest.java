package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ApiService;
import com.github.ayltai.hknews.net.ApiServiceFactory;
import retrofit2.Call;
import retrofit2.Response;

public final class HeadlineParserTest extends ParserTest {
    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/headline.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://hd.stheadline.com/news/daily/hk/702787/");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("http://hd.stheadline.com/news/daily/hk/702787/");

                final Item updatedItem = new HeadlineParser(factory, this.sourceRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;超強颱風「山竹」蹂躪本港，沿岸受「小海嘯」沖擊，風暴過後，災情浮現。鴨脷洲污水處理廠一座維修工場，受不住強風巨浪襲擊，海堤崩塌，整座有倒塌入海的險情。<br /><br />　　「山竹」風力所向披靡，渠務署位於鴨脷洲利南道的污水處理廠，疑抵不住狂風巨浪，一段海堤崩塌，上面作為維修工場的一棟建築物，向海傾側，有倒塌墮海之險，當局封鎖現場，緊急維修。<br /><br />　　渠務署技術秘書余鎮城表示，受山竹吹襲影響，鴨脷洲污水處理廠部份設施受到破壞，包括污水處理廠旁的維修工場及一段海堤，而該廠設施經緊急搶修後，大致恢復運作，署方將盡快跟進受破壞的海堤及維修工場，事件中沒有人員受傷。 <br /><br />　　另外，西貢海邊受嚴重破壞，多艘漁船及遊艇被吹翻及擱淺，一艘價值千萬的豪華遊艇擱淺防波堤，亦有大量塑膠廢物在海上飄浮，海事處外判工人昨早到場清理。近岸行人路磚塊亦被翻起，四處大樹倒塌。<br /><br />", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 1, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "風暴海潮引發的危機未止，鴨脷洲污水處理廠一棟建築物向海傾側。", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "http://static.stheadline.com/stheadline/news_res/2018/09/19/384643/wnnp001p01a.jpg", updatedItem.getImages().get(0).getImageUrl());
            }
        }
    }
}
