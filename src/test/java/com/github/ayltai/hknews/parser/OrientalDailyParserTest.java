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

public final class OrientalDailyParserTest extends ParserTest {
    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("orientaldaily.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://orientaldaily.on.cc/cnt/news/20180720/00174_001.html");
                Mockito.doReturn(response).when(call).execute();
            }
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("orientaldaily.xml"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://orientaldaily.on.cc/cnt/keyinfo/20180720/videolist.xml");
                Mockito.doReturn(response).when(call).execute();
            }
        }

        final Item item = new Item();
        item.setUrl("http://orientaldaily.on.cc/cnt/news/20180720/00174_001.html");

        final Item updatedItem = new OrientalDailyParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

        Assert.assertEquals("Incorrect item description", "公共屋邨相繼淪為老鼠樂園！落成逾三十年的葵芳邨鼠患嚴重，有居民指三個月內捕獲一百八十隻老鼠，當中多達七成三在居住單位內捕獲，有居民更一屋捕七鼠，情況嚇人，怒斥房屋署懶理。鼠輩橫行，又相繼攻陷九龍、新界屋邨，公共衞生已響警號。衞生防護中心發現，今年首五個月，本港已發現兩宗漢坦病毒感染個案，與去年全年紀錄看齊。立法會議員郭家麒憂慮全港多個屋邨先後爆鼠患，可能衍生公共衞生危機，如漢坦病毒感染、鼠疫等，他斥責食環署及房屋署滅鼠工作欠缺協調，各自為政、作風官僚，擔心鼠患一發不可收拾。<br><br>一九八七年入伙的葵芳邨現有十二幢大廈，逾六千二百戶居民。由於歷史悠久，邨內許多樓層都使用大型塑膠垃圾桶收集垃圾，但桶蓋與桶身分離，屋邨地下垃圾房外亦有四至五間臨時房間，用來擺放大量建築廢料，對開有廢棄傢俬雜物散落一地，結果這些地方成了老鼠溫床。<br><br>居民何太憂心地說：「呢度真係好多老鼠，有時夜晚見到佢哋喺石壆上，完全唔驚人，最慘仲識捐入屋，驚佢哋咬親細路！」何太曾多次向房屋署及區議員投訴鼠患問題，但情況毫無改善：「房屋署淨係識派老鼠膠，有鬼用咩！」何太指，曾有老鼠從外面進入居住的單位，一家人要合力捉鼠。其後何先生發現老鼠是從鐵閘之間的罅隙捐入屋，遂用鐵絲網將鐵閘下半部的罅隙封密，現時邨內幾乎家家戶戶都在鐵閘上加裝鐵網。<br><br>猖獗的老鼠不但在低層為患，更直闖二十七樓，何太的一名朋友居於同幢大廈廿七樓，在家燙衫時竟見到巨鼠從外捐入屋，該名居民大驚下以手中熨斗擲向老鼠，結果熨斗砸毀報銷，老鼠卻逃之夭夭。身兼當區區議員的立法會議員梁耀忠指，過去兩、三年邨內已有鼠患，近來老鼠由低層進佔高層，「曾有住戶一屋有七、八隻老鼠，街坊都手足無措」，他斥責房署做得不夠。社區幹事梁靜珊則指，辦事處半年內收到約一百宗投訴，需要跟進的個案約三十多宗，即使居民投訴後，六、七月間續有街坊報稱老鼠入屋。<br><br>房屋署發言人指出，葵芳邨辦事處年初起每月平均接獲四宗老鼠出沒個案，已因應情況採取適切滅鼠措施，如清潔公眾地方及垃圾房，並定期與食環署及各持份者聯合滅鼠工作及檢視成效。食環署發言人則指，葵芳邨屬房屋署轄下的公共屋邨，最近該署曾聯同房屋署及該屋邨物業管理公司人員視察，期間曾發現鼠蹤，已提醒加強清潔及提供防治鼠患技術意見，會繼續留意情況及採取適當行動。<br><br>記者黃雄 、馮淑環<br><br><!--AD--><div class=\"footerAds clear\">第一手消息請下載on.cc東網<a href=\"http://itunes.apple.com/hk/app/id349812998?mt=8\" target=\"_top\">iPhone/</a><a href=\"http://itunes.apple.com/hk/app/id387862928?mt=8\" target=\"_top\">iPad/</a><a href=\"http://market.android.com/details?id=com.news.on\" target=\"_top\">Android/</a><a href=\"http://www.windowsphone.com/zh-hk/apps/30964640-6d0a-450c-b893-1e4aa6dde45f\" target=\"_top\">Windows Phone Apps</a></div><!--/AD--><br><br>", updatedItem.getDescription());
        Assert.assertEquals("Incorrect image count", 5, updatedItem.getImages().size());
        Assert.assertEquals("Incorrect image description", "有老鼠光天化日之下在平台隙縫出沒，猖狂如入無人之境。（黃雄攝）", updatedItem.getImages().get(0).getDescription());
        Assert.assertEquals("Incorrect image URL", "http://orientaldaily.on.cc/cnt/news/20180720/photo/0720-00174-001b1.jpg", updatedItem.getImages().get(0).getImageUrl());
        Assert.assertEquals("Incorrect image count", 1, updatedItem.getVideos().size());
        Assert.assertEquals("Incorrect image description", "http://video.cdn.on.cc/Video/201807/ONS180720-12652-01-M_ipad.mp4", updatedItem.getVideos().get(0).getVideoUrl());
        Assert.assertEquals("Incorrect image URL", "http://tv.on.cc/xml/Thumbnail/201807/bigthumbnail/ONS180720-12652-01-M.jpg", updatedItem.getVideos().get(0).getImageUrl());
    }
}
