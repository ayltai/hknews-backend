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

public final class HeadlineRealtimeParserTest extends ParserTest {
    @Test
    public void testGetItems() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/headline_list.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://hd.stheadline.com/news/realtime/hk/");
                Mockito.doReturn(response).when(call).execute();

                final Collection<Item> items = new HeadlineRealtimeParser(factory, this.sourceRepository).getItems(new Category(Collections.singletonList("http://hd.stheadline.com/news/realtime/hk/"), "即時港聞"));

                Assert.assertEquals("Incorrect item count", 10, items.size());
                Assert.assertEquals("Incorrect item title", "【許金山案】法官完成引導 陪審團開始退庭商議", items.iterator().next().getTitle());
            }
        }
    }

    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/headline_details.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://hd.stheadline.com/news/realtime/hk/1321287/");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("http://hd.stheadline.com/news/realtime/hk/1321287/");

                final Item updatedItem = new HeadlineRealtimeParser(factory, this.sourceRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "<p>中文大學醫學院麻醉及深切治療學系副教授許金山涉以充滿一氧化碳的瑜伽健身球殺害妻子黃秀芬及次女許儷玲案今於高等法院再續，控辯雙方上周已完成結案陳詞，主審此案的張慧玲法官引導陪審團近一小時後，由5男4女組成的9人陪審團於今天約上午11時開始退庭商議。<br /><br />張慧玲法官引導陪審團時重申控辯雙方承認事實中指出案發現場即停泊西貢西沙路西澳村巴士站的私家車，與許家位於西貢西沙路大洞村的寓所之距離為1.6公里。而被告妻子黃秀芬於上午7時30分駕車送三女兒及幼子到保良局蔡繼有學校上課，而從許家位於西貢西沙路大洞村的寓所，駕駛至位於琵琶山郝德傑道的保良局蔡繼有學校需時約半小時。而黃秀芬當日取消其羽毛球運動，根據許家印傭憶述事發當日早上8時30分黃秀芬送畢子女到校後便回到家中休息，回家後在花園照料花草，躺在沙發上玩電話，再回到房中休息。<br /><br />張官指黃秀芬到家時身體狀態無異常並像常人般行動自如，三女兒及幼子亦無恙，可推斷出早於案發當日上午7時30分的車內並未有一氧化碳充斥車廂，故陪審團可考慮該涉案瑜珈球是在黃到家休息至下午2時左右駕車外出接子女放學期間放入車尾箱，但不排除亦有可能是瑜珈球一早已放在車尾箱內，只是黃在家休息並將會獨自駕駛以接子女回家的準確的時間內被拔去膠塞。<br /><br />張官指黃秀芬並不知道瑜珈球內有一氧化碳的事實，而且患有焦慮症及抑鬱症的黃秀芬精神狀態已大大改善並變得開朗起來，更與友人討論數月後的旅行計劃，故可以合理地排除黃秀芬為放瑜珈球到車尾箱的人。而當日在家的印傭並不能任意開車門，而每次洗車時均需拜託黃秀芬以車匙開啟車門以進內洗車，故亦可排除印傭放球的可能性。而只剩被告許金山及其次女許儷玲有可能放置瑜珈球到車尾箱內，但許儷玲對未來有憧憬並一直開朗樂觀，故可排除她以瑜珈球自殺的可能性，但或許她不知道一氧化碳有多危險並會致命，或會單憑其父親告訴她球內有毒氣來滅鼠，便把瑜珈球放到車尾箱以殺滅車內蛇蟲鼠蟻，以意外地令自己及母親不幸身亡。<br /><br />而且根據證物警員所述車內並沒有任何瑜珈球膠塞，如許儷玲放瑜珈球到車尾箱內，該膠塞理應仍在車內。但陪審員需自行判斷是否信納證物警員，皆因該證物警員曾把證物號碼調亂。涉案瑜珈球被檢獲時已被完全拔塞，而且更找不到該膠塞。<br /><br />被告家屬包括其被告長女、三女及幼子等人在陪審團退庭商議後，到庭外會議室等候，各人表現從容自如，有說有笑地聊天。<br /><br />法庭記者：劉曉曦</p>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 1, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "許金山涉以充滿一氧化碳的瑜伽健身球殺害妻子黃秀芬及次女。資料圖片", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "http://static.stheadline.com/stheadline/inewsmedia/20180919/_2018091911152271911.jpg", updatedItem.getImages().get(0).getImageUrl());
            }
        }
    }
}
