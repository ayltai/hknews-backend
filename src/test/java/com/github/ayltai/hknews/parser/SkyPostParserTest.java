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

public final class SkyPostParserTest extends ParserTest {
    @Test
    public void testGetItems() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("skypost_list.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://skypost.ulifestyle.com.hk/sras001");
                Mockito.doReturn(response).when(call).execute();

                final Collection<Item> items = new SkyPostParser(factory, this.sourceRepository, this.itemRepository).getItems(new Category(Collections.singletonList("https://skypost.ulifestyle.com.hk/sras001"), "港聞"));

                Assert.assertEquals("Incorrect item count", 22, items.size());
                Assert.assertEquals("Incorrect item title", "垃圾徵費欠細節 各界憂執行混亂", items.iterator().next().getTitle());
            }
        }
    }

    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("skypost_details.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://skypost.ulifestyle.com.hk/article/2333915/%E5%9E%83%E5%9C%BE%E5%BE%B5%E8%B2%BB%E6%AC%A0%E7%B4%B0%E7%AF%80%20%E5%90%84%E7%95%8C%E6%86%82%E5%9F%B7%E8%A1%8C%E6%B7%B7%E4%BA%82");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("https://skypost.ulifestyle.com.hk/article/2333915/%E5%9E%83%E5%9C%BE%E5%BE%B5%E8%B2%BB%E6%AC%A0%E7%B4%B0%E7%AF%80%20%E5%90%84%E7%95%8C%E6%86%82%E5%9F%B7%E8%A1%8C%E6%B7%B7%E4%BA%82");

                final Item updatedItem = new SkyPostParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "港府銳意明年底實施垃圾徵費，雖然未知相關草案能否順利通過，但適逢不少大廈管理公司的2年期清潔合約最近陸續更新，物管業界、清潔公司、大廈業主立案法團均批評，政府至今未主動提供指引和釐清執法責任。有清潔公司坦言要「見招拆招」，入標時訂明「因應情況額外收費」來自保；物管公司則指，執法非其職能，或變成「磨心」，處理大批棄置垃圾；法團則推算，未來清潔費最少增1成，令管理費勢增。<br>環境局局長黃錦星多番公開強調，最早在2020年底實施垃圾徵費。現時距離預定實施時間僅1年多，但業主、清潔公司及物管公司均反映對執行細節「一無所知」，恐埋下執行大混亂的「計時炸彈」。<br><h4>無講違規垃圾袋 如何處置</h4><br>環境衞生業界大聯盟召集人甄瑞嫻指，實施徵費涉及諸多工序，前綫清潔工需揪出違規垃圾袋，勢花大量人力和時間，「惟有見招拆招，有行家在新合約寫明實施徵費後，可因應情況額外收費。」她批評，環境局的說明偏「理論性」，「只講好表面嘅嘢，如違規垃圾袋不收，但無講怎處理違規垃圾，要靠業界發揮小宇宙。」<br>香港物業管理公司協會會長陳志球批評，政府推行減廢僅「靠罰」，但管理公司非「偵探」，無權亦不應對違規住戶「查案咁查」，最終只能做「負責角色」，「一個屋苑動輒有數百戶，管理公司無資源去捉違規者，最後惟有代為買垃圾袋『負責』，管理費勢必增加。」<br><h4>物管公司恐「上身」 要代買袋</h4><br>香港業主會會長佘慶雲炮轟，政府高估社會的配合程度，「不難想像有人會千百方計慳垃圾費，像內地已有冒牌垃圾袋出現，但官員僅指真袋有防偽標示，無考慮清潔工能否逐袋確認。」他推算，以每層共用1個20公升垃圾袋（2.2元）為例，30層大廈1日需花66元，「有61座的太古城，每月便花上10多萬元；而公家地方，如商場、會所用的垃圾袋，業主更難監測實際用袋量，只能『硬食』費用。」<br>中西區半山業主聯會主席黃美慧以其居所寧養台為例，「2座大樓共300戶，現每月已花10多萬元清潔費，未來最少要多付10%費用，不排除或出現『天文數字』。」她指，重新招標的2年清潔合約，條款暫「按兵不動」，「因大家都不知政府想點，無清晰指引恐致擾民。」黃續指，當局從未主動召開簡介會為業主解難，反而法團欲自發要求加入先導計劃，「不然好難預算每月會用幾多垃圾袋，應收多少費用才合理。」<br><h4>環境局：舉報黑點 以便巡查</h4><br>環境局回覆指，相關條例草案正由立法會審議，望在2020年底前實施。局方稱屆時會以「風險為本」模式針對黑點執法，初步估算需數百名執法人員，並擬設立減少都市固體廢物辦公室統籌相關工作。至於清潔和管理公司責任，局方稱前綫人員如發現違規廢物，應向上司舉報，再由公司向環保署專門熱綫通報，局方會據此製作「違法黑點」名單，以便巡查及執法之用，強調收到投訴後不一定立即行動。<br>記者︰脫芷晴<br>編輯：梁偉澄<br>美術：陳超雄<br>攝影：冼偉倫<br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 3, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "<span>▲</span>屋苑大廈每日處理大量垃圾，未來按照不同「入袋」方式，衍生的垃圾費用和處理成本難以估計，勢增市民負擔。", updatedItem.getImages().get(1).getDescription());
                Assert.assertEquals("Incorrect image URL", "https://resource01.ulifestyle.com.hk/res/v3/image/content/2330000/2333915/20190424JAA001_xxx_20190424_S.jpg", updatedItem.getImages().get(1).getImageUrl());
            }
        }
    }
}
