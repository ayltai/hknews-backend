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

public final class MingPaoParserTest extends ParserTest {
    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("mingpao.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://news.mingpao.com/pns/%e6%b8%af%e8%81%9e/article/20181217/s00002/1544984739431/%e9%a7%95%e8%b6%85%e8%b7%91%e9%87%8d%e8%bf%94%e6%b7%b1%e6%b0%b4%e5%9f%97%e6%b4%be%e9%8c%a2%e9%81%ad%e8%ad%a6%e6%88%aa-%e6%b6%89%e7%ad%96%e5%8a%83%e6%92%92%e9%8c%a2-%e3%80%8c%e5%b9%a3%e5%b0%91%e7%88%ba%e3%80%8d%e8%a2%ab%e6%8d%95");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("https://news.mingpao.com/pns/%e6%b8%af%e8%81%9e/article/20181217/s00002/1544984739431/%e9%a7%95%e8%b6%85%e8%b7%91%e9%87%8d%e8%bf%94%e6%b7%b1%e6%b0%b4%e5%9f%97%e6%b4%be%e9%8c%a2%e9%81%ad%e8%ad%a6%e6%88%aa-%e6%b6%89%e7%ad%96%e5%8a%83%e6%92%92%e9%8c%a2-%e3%80%8c%e5%b9%a3%e5%b0%91%e7%88%ba%e3%80%8d%e8%a2%ab%e6%8d%95");

                final Item updatedItem = new MingPaoParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "<p>【明報專訊】深水埗福榮街周六（15日）發生天降鈔票事件，網名「幣少爺」的黃鉦傑事前站在超級跑車旁高呼：「錢係可以從天而降嘅！」他昨午再度現身福榮街派錢，聲稱準備前往購買10萬元飯券，警員其後截停車輛將他拘捕，並帶返警署扣查。</p><br><br><p>被捕的黃鉦傑24歲，涉嫌策劃公眾地方擾亂秩序，由探員帶返深水埗警署扣查。昨晚7時許，黃被鎖上手銬由探員押至跑車停泊處，他站於車旁，探員則將車門打開搜查，其間檢走貼在擋風玻璃的過期行車證及行車記錄儀，約15分鐘後將黃帶上警車離去。黃其後再被押往西貢田石路的別墅蒐證。</p><p>「幣少爺」於社交網站預告昨午4時到深水埗「明哥北河行動」，購買飯票派給老人家，但飯店負責人明哥向記者表示，無人直接聯絡會大量購飯票，但如果派飯好像前日派錢般擾亂社會，希望他可以返回正路。</p><p>前日有「紅衫魚」空中飄揚的福榮街與桂林街交界，昨早仍有零星百元鈔票隨風飄落，有市民只敢用手機拍攝，但亦有人攀上高處尋找「漏網之魚」，在場警員沒收市民拾獲的鈔票及登記資料。昨午2時許，黃駕跑車現身於福榮街，有市民發現後即追上，有數人伸手入車窗取得500元鈔票，黃亦向追訪記者聲稱準備10萬元往捐錢、捐飯券。車輛駛近南昌街時被警員截停，黃手持一疊約3吋厚的500元鈔票落車，引來大批市民圍觀及拍照，他隨即被拘捕，並被帶上警車離開。</p><p>警：拾獲財物應交出</p><p>深水埗警區莊翹偉總督察表示，前日下午2時許，福榮街153號附近有大量鈔票由大廈天台撒落，大部分為100元紙幣，事件引起大批市民搶錢及聚集，對附近交通及途人造成混亂，警方接報到場管制人群，約30分鐘後逐步控制現場，至昨日警方共檢獲60張100元鈔票。警方昨午3時許於福榮街近南昌街拘捕一名涉嫌策劃事件的姓黃男子，案件將循不同方向調查，包括在公開媒體蒐證，不排除稍後有更多人被捕，由深水埗警區刑事調查隊跟進，其動機有待深入調查。警方重申，市民在任何地方拾獲財物，應將財物交予警方處理，以免觸犯盜竊罪。</p><p>事發於前日中午2時許，網名為「幣少爺」的黃鉦傑，於深水埗福華街站在一輛林寶堅尼跑車旁高呼：「錢係可以從天而降嘅！」隨即有大量鈔票由附近大廈天台灑落，他亦曾於網上聲稱行動要「劫富濟貧」。</p><p>■明報報料熱線﹕<a href=\"mailto:inews@mingpao.com\">inews@mingpao.com</a>/ 9181 4676</p>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 2, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "涉策劃撒錢事件被捕的「幣少爺」（藍白色外套），昨晚被探員扣上手銬押至超級跑車搜查。（蘇智鑫攝）", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "https://fs.mingpao.com/pns/20181217/s00007/32a7a1fd042e1ed247d4d8c66d684936.jpg", updatedItem.getImages().get(0).getImageUrl());
                Assert.assertEquals("Incorrect video count", 2, updatedItem.getVideos().size());
                Assert.assertEquals("Incorrect video URL", "http://video3.mingpao.com/inews/201812/20181216coin.mp4", updatedItem.getVideos().get(0).getVideoUrl());
                Assert.assertEquals("Incorrect thumbnail URL", "http://video3.mingpao.com/inews/201812/20181216coin.jpg", updatedItem.getVideos().get(0).getImageUrl());
            }
        }
    }
}
