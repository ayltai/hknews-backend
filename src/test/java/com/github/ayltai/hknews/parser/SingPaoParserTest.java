package com.github.ayltai.hknews.parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ApiService;
import com.github.ayltai.hknews.net.ApiServiceFactory;
import retrofit2.Call;
import retrofit2.Response;

public final class SingPaoParserTest extends ParserTest {
    @Test
    public void testGetItems() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singpao_list.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://www.singpao.com.hk/index.php?fi=news1");
                Mockito.doReturn(response).when(call).execute();

                final Collection<Item> items = new SingPaoParser(factory, this.sourceRepository, this.itemRepository).getItems(new Category(Collections.singletonList("https://www.singpao.com.hk/index.php?fi=news1"), "港聞"));

                Assert.assertEquals(20, items.size());
                Assert.assertEquals("輕鐵屯門泳池站月台沉降 驚見石屎躉「離地」60毫米", items.iterator().next().getTitle());
            }
        }
    }

    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/singpao_details.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://www.singpao.com.hk/index.php?fi=news1&id=78082");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("https://www.singpao.com.hk/index.php?fi=news1&id=78082");

                final Item updatedItem = new SingPaoParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "港鐵再爆出鐵路站月台沉降事件，大批港鐵職員日前到輕鐵屯門泳池站檢測，事後在路軌留下多個「神秘藍線」，惹人疑竇。港鐵昨日則承認，鄰近樓盤工程去年打樁後，去年底發現月台出現沉降問題，即月台沉降至今已超過八個月，但港鐵未有透露沉降幅度，只表示少於20毫米，由於未有超標，故地盤會毋須停工，堅稱月台和路軌結構安全。港鐵雖強調月台沉降少於20毫米，但現場所見月台與地盤之間的臨時行人道石屎躉，亦懷疑因沉降出現「離地」情況，幅度卻達60毫米。 本報港聞部報道<br><br>輕鐵屯門泳池站附近有居民表示，大批港鐵職員本周一（7月30日）「勁大陣仗」到該站進行測量，其後更於站內路軌及地面噴上多個「神秘藍色」記號，有居民曾即時向港鐵職員查詢，惟對方未有回應，而該站經檢查後雖如常運作，但已驚動鄰近居民，並議論紛紛。<br><br>大陣仗測量 留神秘藍線<br><br>現場所見，緊貼屯門泳池車站旁有一個新鴻基的樓盤地盤正在施工，工地外搭建一條臨時行人通道，行人通道上蓋以石屎躉支撐。正常情況下，石屎躉應貼實地面固定，但石屎躉懷疑因地面下沉，竟出現「離地」情況，與地面形成一個相距最近達60毫米的罅隙，遠遠較一個5元為大，其中一個石屎躉更懷疑被人用「土炮」方式支撐，用木楔子「頂住先」。至於輕鐵車站的月台底部，則有出現裂縫，未知是否與沉降有關。有居民則擔心臨時行人通道的石屎躉是否穩固，以及會否波及車站。<br><br>港鐵昨日回覆《成報》查詢時承認輕鐵屯門泳池站出現沉降，指出車站旁的地盤工程去年1月開始，至去年底發現該站2號月台出現輕微沉降問題，但強調幅度一直平穩，並維持在屋宇署沿用的20毫米沉降指標以內，明言月台結構、路軌以及鐵路設施一直符合安全標準，鐵路安全運作不受影響。<br><br>港鐵稱未超標 結構安全<br><br>港鐵發言人又指出，沉降幅度雖然未超過20毫米指標，但已將有關數據通報相關政府部門，據港鐵了解，該地盤大部分地基打樁工程經已完成，現正進行上蓋建造工程，若月台沉降達停工指標，會按既定機制要求發展商暫停相關鐵路保護區內的工程，以及通報相關政府部門有關安排。<br><br>土力工程處前處長陳健碩則表示，如果月台沉降幅度不均勻，有可能會令建築物結構出現問題，「若果一邊沉、一邊不沉，是最不理想情況，這樣或會破壞結構」。他又指出，裂紋會否影響月台結構，要視乎設計而定，認為早前同樣出現沉降的天榮站以及今次的屯門泳池站，出現沉降或與月台採用淺層地基有關。<br><br>今次是港鐵第四次出現結構沉降問題，東鐵綫大圍站月台上月沉降達20毫米指標，港鐵要求旁邊新世界樓盤暫停工程；同月署理運輸及房屋局長蘇偉文在立法會上自爆，輕鐵綫天榮站月台有沉降問題，上蓋地盤工程須停工，惟港鐵亦一直沒有公布，屋宇署在查詢後始透露沉降已增至89毫米。另外，今年6月，西鐵元朗站被揭發早在2013年下旬兩個架空路軌的橋躉沉降20毫米，港鐵雖有叫停橋躉鄰近的新鴻基住宅項目工程，但被批評一直沒公布事件，而港鐵去年10月始進行預防性加固工程。<br><br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 5, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "港鐵強調輕鐵屯門泳池站月台結構、路軌及鐵路設施符", updatedItem.getImages().get(1).getDescription());
                Assert.assertEquals("Incorrect image URL", "https://www.singpao.com.hk/image_upload/1533150024.jpg", updatedItem.getImages().get(1).getImageUrl());
            }
        }
    }
}
