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

public final class AppleDailyParserTest extends ParserTest {
    @Test
    public void testGetItems() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/appledaily_list.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://hk.appledaily.com/video/videolist/20180918/local/home/0");
                Mockito.doReturn(response).when(call).execute();

                final Collection<Item> items = new AppleDailyParser(factory, this.sourceRepository, this.itemRepository).getItems(new Category(Collections.singletonList("https://hk.appledaily.com/video/videolist/20180918/local/home/0"), "即時港聞"));

                Assert.assertEquals("Incorrect item count", 20, items.size());
                Assert.assertEquals("Incorrect item title", "【山竹襲港】唔肯宣佈停工　奶媽死撐叫蟻民互諒互讓", items.iterator().next().getTitle());
            }
        }
    }

    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/appledaily_details.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://hk.news.appledaily.com/local/daily/article/20180918/20502187");
                Mockito.doReturn(response).when(call).execute();
            }
        }

        final Item item = new Item();
        item.setUrl("https://hk.news.appledaily.com/local/daily/article/20180918/20502187");

        final Item updatedItem = new AppleDailyParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

        Assert.assertEquals("Incorrect item description", "<p>                                【板前糾紛】<br />【本報訊】板前壽司創辦人之一、人稱Ricky San的鄭威濤，因品牌糾紛遭股東入稟索償，更就賠償金額未有定案而纏訟多時，高院昨繼續審理。早前確診患胰臟癌的鄭威濤出庭作供，自言本不欲出售公司股份，因公司是他製造出來的「BB」，但自2017年確診患癌後，很多醫生「都覺得我唔知仲有幾耐命」，令他改變主意，決定賣股。他離庭時稱作供一整天令他甚疲倦，又透露已先後接受多種治療，目前病情仍未受控。                                    <!--please only add this icon at the end of the article -->                                    </p><h3>稱曾赴日接受幹細胞治療</h3>                                        <p>                                鄭威濤昨乘私人座駕，由司機接載到高院。雖然天氣已回暖，但鄭仍身穿厚長外套，繫上頸巾，面容略見憔悴。他甫步上證人台，未及宣誓，便從大袋中拿出三個保溫瓶放在枱上，作供期間逐瓶飲用。作供一小時後，他突然表示身體不適，要求稍作休息。小休期間，他表示感覺疲倦和「好凍」，因而要戴頸巾。及後再作供時，鄭的精神越來越差，更一度要求法庭繙譯「講慢啲」。至接近中午時，他終向法官表示「唔得喇，我要食藥」，法庭遂提早午休。經過一整天作供，鄭終能離開法庭。他自言疲倦，又謂自從確診患癌後，曾接受過化療、標靶藥物和免疫治療，又到日本接受幹細胞治療。由於仍要繼續療程，他已將公司部份生意交給同事打理。<br /><br />鄭威濤是板前壽司創辦人之一。他之所以牽涉本案，源於持有首間板前的駿濤有限公司，其中一名股東潘嘉聞質疑鄭擅自以「板前」名義開分店，又自創「板長」品牌，與板前爭生意，違反對駿濤的董事受信責任。潘入稟控告鄭，鄭被判敗訴；鄭上訴至終院，三年前被駁回。法庭下令鄭要與駿濤股東分享因違反受信責任而獲得的利潤。<br /><br />法庭原本要就評定賠償金額開庭聆訊，惟駿濤於2017年8月召開特別股東大會，大比數通過接受鄭提出的4,000萬元賠償方案。潘嘉聞不接納投票結果，認為鄭雖不能投票，卻假裝將股份售予兩名股東麥建成和黃銳韜，旨在令二人在股東會上投票支持賠償方案。高院因而展開本訴訟，以決定當日議決結果是否有效。<br /><br />鄭威濤昨接受潘的一方盤問時，否認虛假交易之說，並稱駿濤是他製造出來的「BB」，他極不願出售。惟直至他發現患癌，改變想法，並重申不想把官司留給家人，才會答應麥、黃的買股建議。鄭謂：「可能潘生（潘嘉聞）叫我賣畀佢，我都賣。」更稱當時不知二人最終會如何投票，但若賠償方案獲通過，除了對他個人是好事，亦能讓小股東獲分應得利益。雙方今結案陳詞。<br />案件編號：HCA304/11<br />■記者蔡少玲                                    <!--please only add this icon at the end of the article -->                                                                                                            </p>", updatedItem.getDescription());
        Assert.assertEquals("Incorrect image count", 1, updatedItem.getImages().size());
        Assert.assertEquals("Incorrect image description", "鄭威濤昨身穿厚長褸、繫上頸巾出庭作供。李潤芳攝", updatedItem.getImages().get(0).getDescription());
        Assert.assertEquals("Incorrect image URL", "https://static.appledaily.hk/images/apple-photos/apple/20190418/large/18la6p05.jpg", updatedItem.getImages().get(0).getImageUrl());
        Assert.assertEquals("Incorrect video count", 1, updatedItem.getVideos().size());
        Assert.assertEquals("Incorrect video URL", "https://video.appledaily.com.hk/mcp/encode/2019/04/18/3814831/20190417_news_06AD_w.mp4", updatedItem.getVideos().get(0).getVideoUrl());
        Assert.assertEquals("Incorrect video thumb URL", "https://static.appledaily.hk/images/apple-photos/video/20190418/392pix/1555527325_39a5.jpg", updatedItem.getVideos().get(0).getImageUrl());
    }
}
