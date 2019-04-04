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

public final class HketParserTest extends ParserTest {
    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("hket.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://topick.hket.com/article/2124495");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("https://topick.hket.com/article/2124495");

                final Item updatedItem = new HketParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "在大嶼山玩滑翔傘失蹤6天的男子鍾旭華今日中午在大東山被尋回，並證實已經死亡。香港飛行服務隊發放現場最新圖片，並指發現鍾旭華的位置在雲層底的一個陡峭山坡，令搜救行動更困難。香港飛行服務隊協助消防員及民安隊人員降落現場，現場消息指鍾旭華已死亡，暫時仍待安排。<br>飛行服務隊表示，約今午12時於大東山以南500米發現疑似鍾旭華使用的滑翔傘，顏色與早前提供的資料相符，並發現鍾旭華倒臥在一個陡陗的山坡上，接近雲層，令援救工作困難，其後派出消防員及民安隊到達現場搜救。<br>飛行服務隊指，截至下午12時半，飛行服務隊共派出11部超級美洲豹直升機及12架EC155直升機進行搜救工作，共動用35名機師及40名空勤主任，而飛行搜救時間為22.75小時。<br>44歲的鍾旭華星期日下午，與十多人在伯公坳對上山頭玩滑翔降傘，遇上天氣突變之後失蹤，至今已第6日。當日玩滑傘另有2人遇險，其中一人報警被困大東山上；另一人則滑翔至貝澳一處田地緊急降落。<br>警方早前在星期一（23日）追查到失蹤事主的手機訊號，救援人員兵分多路通宵集中在芝麻灣及鳳凰山一帶搜索，至星期二（24日）深夜香港滑翔傘協會發言人表示，早前確認鍾旭華的電話信號已經消失，估計是因為電話無電。<br>&nbsp;<br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 2, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "政府飛行服務隊發放圖片，指鍾旭華被發現在雲底陡峻山坡。（政府飛行服務隊圖片）", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "https://topick.hket.com/res/v3/image/content/2120000/2124495/1_1024.JPG", updatedItem.getImages().get(0).getImageUrl());
            }
        }
    }
}
