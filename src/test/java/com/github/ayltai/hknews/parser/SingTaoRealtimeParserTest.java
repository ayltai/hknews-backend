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

public final class SingTaoRealtimeParserTest extends ParserTest {
    @Test
    public void testGetItems() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("singtao_realtime_list.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://std.stheadline.com/instant/articles/listview/%E9%A6%99%E6%B8%AF/");
                Mockito.doReturn(response).when(call).execute();

                final Collection<Item> items = new SingTaoRealtimeParser(factory, this.sourceRepository, this.itemRepository).getItems(new Category(Collections.singletonList("http://std.stheadline.com/instant/articles/listview/%E9%A6%99%E6%B8%AF/"), "即時港聞"));

                Assert.assertEquals("Incorrect image count", 33, items.size());
                Assert.assertEquals("Incorrect item description", "【奪命車禍】好爸爸與親友「做節」後返家途中炒車亡 遺下愛妻兩稚女", items.iterator().next().getTitle());
            }
        }
    }

    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("singtao_realtime_details.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("http://std.stheadline.com/instant/articles/detail/821515-%E9%A6%99%E6%B8%AF-%E3%80%90%E5%A5%AA%E5%91%BD%E8%BB%8A%E7%A6%8D%E3%80%91%E5%A5%BD%E7%88%B8%E7%88%B8%E8%88%87%E8%A6%AA%E5%8F%8B%E3%80%8C%E5%81%9A%E7%AF%80%E3%80%8D%E5%BE%8C%E8%BF%94%E5%AE%B6%E9%80%94%E4%B8%AD%E7%82%92%E8%BB%8A%E4%BA%A1+%E9%81%BA%E4%B8%8B%E6%84%9B%E5%A6%BB%E5%85%A9%E7%A8%9A%E5%A5%B3");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("http://std.stheadline.com/instant/articles/detail/821515-%E9%A6%99%E6%B8%AF-%E3%80%90%E5%A5%AA%E5%91%BD%E8%BB%8A%E7%A6%8D%E3%80%91%E5%A5%BD%E7%88%B8%E7%88%B8%E8%88%87%E8%A6%AA%E5%8F%8B%E3%80%8C%E5%81%9A%E7%AF%80%E3%80%8D%E5%BE%8C%E8%BF%94%E5%AE%B6%E9%80%94%E4%B8%AD%E7%82%92%E8%BB%8A%E4%BA%A1+%E9%81%BA%E4%B8%8B%E6%84%9B%E5%A6%BB%E5%85%A9%E7%A8%9A%E5%A5%B3");

                final Item updatedItem = new SingTaoRealtimeParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "43歲姓李鐵騎士與親友「做節」後，返家途中駛經粉嶺公路時突失事遇車禍身亡，遺下父母、妻子及一對年幼女兒。死者既是好丈夫亦好爸爸，非常愛錫妻兒，又孝順父母。他生前在地盤任職吊臂車司機，為家庭經濟支柱，雖然收份不多，但全數都幾乎用作家用。家人暫時仍未將噩耗告知兩名幼女，擔心影響她們上課。<br />死者李朝邦生前與妻女同住觀塘塘翠屏邨一公屋單位。事發昨晚11時許，李男與親戚在上水晚飯後，駕駛綿羊仔沿粉嶺公路回家，駛至九龍坑大窩村附近時突然切線，由快線切向中線再切入慢線，期間疑失控撞向路邊水馬及隔音屏石壆，李男應聲倒地，頭盔飛脫重傷昏迷。救護員到場將李男送往那打素醫院搶救，惜最終不治。<br />新界北總區交通部意外調查隊正跟進調查案件。任何人如目睹意外發生或有資料提供，請致電3661 3800與調查人員聯絡。<br><br><br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 3, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "警員在場調查及檢走死者頭盔。", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "http://static.stheadline.com/stheadline/inewsmedia/20180919/_2018091915552338242.jpg", updatedItem.getImages().get(0).getImageUrl());
            }
        }
    }
}
