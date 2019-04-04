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

public final class SkyPostParserTest extends ParserTest {
    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("skypost.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://skypost.ulifestyle.com.hk/article/2224904/冷鋒來襲連凍9日 今低見17℃");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("https://skypost.ulifestyle.com.hk/article/2224904/冷鋒來襲連凍9日 今低見17℃");

                final Item updatedItem = new SkyPostParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "<h4>有雨兼潮濕 下周新界或9℃</h4><br>今冬第一波冷峰來襲，「急凍」香港。天文台預料，與冷峰相關的東北季候風今起影響廣東沿岸，今日（7日）最低氣溫會降至17度，下周初中期氣溫進一步下調，市區最低僅12度，北區更可能低見9度；且期內有雨潮濕。有戶外用品店料羽絨銷量增10%；亦有保暖產品店指暖手蛋及保暖頸巾已增50%貨應急；另有火鍋店稱訂位已爆滿。<br>天文台預計，本港今起天氣顯著轉涼，最低氣溫跌至17度，且多雲有一兩陣雨。至下周日（9日）最低氣溫會再下調至13度，下周一（10日）跌至12度，屆時北區打鼓嶺更可能低見9度；至下周六（15日）氣溫才稍回升至16度，即最少「凍足一星期」。天文台科學主任莊思寧指，今年入冬不算太遲，「按過去記錄，12月時常錄得逾20度，今冬未算反常。」但她提醒，因氣溫驟降，且預算寒冷時間較長，身體未必能馬上適應，需做好保暖措施。<br><h4>保暖產品料暢旺 訂貨多 5成</h4><br>保暖產品銷情料暢旺。金輝戶外運動用品負責人羅小姐指，今年凍得較早，料可拉長銷售期，料羽絨銷量會有10%增長。JC Shop負責人莊天麟指，上周知悉本港將轉冷後，已大手加訂。「例如售880元的第二代發熱頸巾，去年入了800條，今年加至1,500條；暖手蛋亦加碼50%，訂了300個。」其集團旗下的「重慶劉一手火鍋」訂位亦告爆滿，莊說︰「旺角及銅鑼灣店留了一半位供預訂，約250個位已全數爆滿，最快要3周後才有空位。現已多訂了50%食材應急，料麻辣、黑椒豬肚胡椒雞等暖身湯底會較受歡迎。」<br><h4>寒天愛吃煲仔飯 料日賣400個</h4><br>新光酒樓集團常務董事總經理胡珠亦指，因大閘蟹今年問題多多，不少客人轉食蛇、羊腩及臘味，「火鍋、煲仔飯都受歡迎，會加訂食材，料生意額可升10%。」大埔陳漢記負責人亦指，該店煲仔飯一向熱賣，天氣轉凍料銷售速度更快，「師傅人手有限，料可以一日賣出約400個煲仔飯。」<br>一田百貨行政總裁黃思麗亦指，料本周末天氣轉冷，生意額會較平日周末升10%至15%︰「料火鍋相關食材銷情佳，和牛火鍋片將有8折；保暖內衣及被子，尤其是小童被料銷情強勁。」<br>記者︰脫芷晴<br>編輯：梁偉澄<br>美術：陳超雄<br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 5, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "<span>▲</span>天文台指，今年最低溫度在2月創下，當時本港僅錄得6.8度。今冬能否再下一城，尚待觀察。（冼偉倫攝）", updatedItem.getImages().get(1).getDescription());
                Assert.assertEquals("Incorrect image URL", "https://resource01.ulifestyle.com.hk/res/v3/image/content/2220000/2224904/20181207JAA002__20181207_S.jpg", updatedItem.getImages().get(1).getImageUrl());
            }
        }
    }
}
