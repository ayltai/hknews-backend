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

public final class HkejParserTest extends ParserTest {
    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/hkej.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://www2.hkej.com/instantnews/china/article/1878965/%E9%8A%80%E4%BF%9D%E7%9B%A3%E5%90%916%E9%9A%AA%E4%BC%81%E7%99%BC%E7%9B%A3%E7%AE%A1%E5%87%BD+%E5%8A%8D%E6%8C%87%E9%81%95%E8%A6%8F%E6%8A%95%E8%B3%87");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("http://www2.hkej.com/instantnews/china/article/1878965/%E9%8A%80%E4%BF%9D%E7%9B%A3%E5%90%916%E9%9A%AA%E4%BC%81%E7%99%BC%E7%9B%A3%E7%AE%A1%E5%87%BD+%E5%8A%8D%E6%8C%87%E9%81%95%E8%A6%8F%E6%8A%95%E8%B3%87");

                final Item updatedItem = new HkejParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "中國銀保監近日向6家保險企業開出監管函，劍指保險機構違規投資。<br><br>6家險企分別為紫金財產保險、中華聯合財產保險、幸福人壽保險、眾安在線財產保險、民生通惠資產管理、安誠財產保險。<br><br>其中，紫金財險存在的問題包括股票投資執行控制不嚴格、未按規定開展保險資金運用內部審計等；眾安在線財險的問題則是超限額投資關聯方發行的金融產品、未按規定進行監管報告和對外訊息披露等。<br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 1, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "銀保監向6險企發監管函。(中新社資料圖片)", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "http://static.hkej.com/hkej/images/2018/06/29/1878965_fdfcc732e2dc7cce58cd2457528de4ae.jpg", updatedItem.getImages().get(0).getImageUrl());
            }
        }
    }
}
