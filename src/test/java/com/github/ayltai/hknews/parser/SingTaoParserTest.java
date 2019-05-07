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
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public final class SingTaoParserTest extends ParserTest {
    @Test
    public void testGetItems() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("singtao_list.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://std.stheadline.com/daily/section-list.php?cat=12");
                Mockito.doReturn(response).when(call).execute();

                final Collection<Item> items = new SingTaoParser(factory, this.sourceRepository, this.itemRepository).getItems(new Category("http://std.stheadline.com/daily/section-list.php?cat=12", "港聞"));

                Assert.assertEquals("Incorrect item count", 13, items.size());
                Assert.assertEquals("Incorrect item description", "美擬加碼徵25%關稅 中方：必然反制", items.iterator().next().getTitle());
            }
        }
    }

    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("singtao_details.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://std.stheadline.com/daily/news-content.php?id=1848076&target=2");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("http://std.stheadline.com/daily/news-content.php?id=1848076&target=2");

                final Item updatedItem = new SingTaoParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "　　(星島日報報道)中美貿易戰不斷升級？！彭博社引述消息人士指，之前美國特朗普政府擬向兩千億美元中國進口商品，加徵關稅百分之十，但現在特朗普的顧問有意將關稅加碼到百分之二十五，一反前日有傳重啟貿易談判的消息。《華爾街日報》指加關稅是為抵銷人民幣近月來迅速貶值的影響，而兩國再展談判的機會，仍在非常初級的磋商階段。中國外交部發言人耿爽回應，美方施壓和訛詐不會起作用，如果美方有升級舉動，中方必然反制。<br /><br />　　七月六日，美國向三百四十億美元的中國貨品，加徵兩成半關稅後。中國隨即反擊，向同等價值的美國貨品加關稅，尤其針對美國大豆，轉用南美大豆作替代品，以針對美國總統特朗普的農民「票倉」。自此，中美貿易戰升溫，而美方沒有停手，一百六十億美元中國貨品的第二輪徵稅措施，將於本星期開始實施。<br /><br />　　特朗普曾威脅向兩千億美元中國商品，加徵一成關稅，並擬定商品清單，涉及蔬果、手袋、家庭電器、衣物和體育用品等日常用品。該加徵關稅的商品清單正處於公眾諮詢期，聽證會將在八月二十至二十三日舉行，事成則在九月會實施關稅。<br /><br />　　彭博社引述消息人士表示，特朗普或會在未來的數日內，發布聯邦公報，宣布將該筆兩千億美元的中國商品關稅提高到兩成五，已指示貿易代表萊蒂澤負責相關工作。《華爾街日報》分析指，是次加徵關稅是為抵銷人民幣近月來迅速貶值的影響，自五月三十日以來，人民幣兌美元已累計下跌百分之六。<br /><br />　　此舉一反前日貿易戰緩和消息。彭博社前天報道，中美有意重啟貿易談判，避免爆發全面貿易戰，美國財政部長努欽和中國副總理劉鶴正私下對話，令疲弱的人民幣一度抽升。《華爾街日報》報道指，特朗普對中國增購美國貨的讓步「不收貨」，中美貿易談判幾無進展，並引述兩國政府官員表示，努欽與劉鶴以及他們的談判人員，仍就潛在會晤進行磋商，但此磋商仍處於非常初級的階段。<br /><br />　　特朗普早前在佛羅里達州出席一個活動時，就感謝當地農民在貿易戰的犧牲。他表示：「我想多謝我們的農民，我們的農民是真正的愛國者，因為被中國和其他國家針對，中國和其他國家，緊記，一直針對我們的農夫不好、不好，但你知道我們的農夫在說甚麼？『不要緊』。」他的講話，沒有釋出任何貿易戰緩和的徵兆。<br /><br />　　中國外交部發言人耿爽在昨例行記者會表示，中方在中美經貿問題立場堅定明確，美方的施壓和訛詐不會起作用。「如果美方採取進一步升級舉動，中方必然會予以反制，堅決維護我們的正當合法權益」。他強調，中方始終主張以對話協商處理貿易摩擦，但「對話必須建立在相互尊重和平等的基礎上，建立在規則之上，建立在信用之上」，並指「單方面威脅和施壓只會適得其反」。<br /><br /><br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 1, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "中國外交部發言人耿爽警告，若美再加徵關稅，中方必然反制。", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "http://static.stheadline.com/stheadline/news_res/2018/08/02/339484/i_390x283_009236162.jpg", updatedItem.getImages().get(0).getImageUrl());
            }
        }
    }
}
