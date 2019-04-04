package com.github.ayltai.hknews.parser;

import com.github.ayltai.hknews.data.model.Category;
import com.github.ayltai.hknews.data.model.Item;
import com.github.ayltai.hknews.net.ApiService;
import com.github.ayltai.hknews.net.ApiServiceFactory;

import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.lang.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Response;

public final class AppleDailyParserTest extends ParserTest {
    @Test
    public void testGetItems() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("appledaily_list.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://hk.appledaily.com/video/videolist/20180918/local/home/0");
                Mockito.doReturn(response).when(call).execute();

                final Collection<Item> items = new AppleDailyParser(factory, this.sourceRepository, this.itemRepository).getItems(new Category("https://hk.appledaily.com/video/videolist/20180918/local/home/0", "即時港聞"));

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

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("appledaily_details.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://hk.news.appledaily.com/local/daily/article/20180918/20502187");
                Mockito.doReturn(response).when(call).execute();
            }
        }

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("appledaily_video.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://hk.video.appledaily.com/video/videoplayer/20180918/daily/daily/20502187/20191240/0/0/0");
                Mockito.doReturn(response).when(call).execute();
            }
        }

        final Item item = new Item();
        item.setUrl("https://hk.news.appledaily.com/local/daily/article/20180918/20502187");

        final Item updatedItem = new AppleDailyParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

        Assert.assertEquals("Incorrect item description", "<p class=\"ArticleIntro\">                                【本報訊】「山竹」遠離香港，市民還未回氣，就因特首林鄭月娥拒絕港府帶頭全港停工，數十萬打工仔昨早要在無巴士、火車、道路充滿倒塌大樹情況下趕上班，結果引爆交通大混亂，港鐵大圍站更一度有逾萬人等車。林鄭死撐決定無錯，又聲稱已通知全體公務員可彈性上班，但隨即有公務員踢爆她講大話，根本沒收過通知。民主、建制議員狠批林鄭「堅離地」，有打工仔更怒斥「攞市民條命較飛」。<br/><br/>記者：林俊謙                                                                </p><p>                                早在周日晚上，民主黨及工黨等已呼籲政府非緊急服務帶頭停工，但政府未有理會，只呼籲僱主「以互諒互讓態度」處理員工上班，結果全港數十萬打工仔為了「準時返工」而淪為「上班難民」，在巴士停駛、東鐵綫局部停駛下，港鐵大圍站一度有逾萬人等車；有市民為趕上班不惜以近1,500元乘搭Uber；更有市民要爬過倒塌大樹、走過充滿玻璃雜物的大街徒步上班，他們不約而同大罵政府未有體察民情，明知公共交通未恢復，仍不肯帶頭停工。到晚上下班時間，由於東鐵綫開始回復正常、巴士也提供有限度服務，令等車情況稍有改善，但不少市民仍要用較平日更長時間回家。                                    <!--please only add this icon at the end of the article -->                                                                    </p><h3>運輸署：今晨開通幹道</h3>                                        <p>                                面對交通大混亂，逾8,000市民在林鄭facebook留言，絕大部份均是不滿林鄭未有宣佈停工，有人指「停工得罪商界，當然理你蟻民死」；亦有人批評林鄭「攞市民條命較飛」。但林鄭昨晨在政總會見傳媒時，只是不斷吹噓政府「防備工作係卓有成效」，對打工仔轟政府拒絕宣佈停工，林鄭仍堅持由僱主、員工「互諒互讓更加適合香港情況」，呼籲僱主「以互諒互讓態度」看待僱員昨日或因交通問題而遲到或缺勤；又聲稱公務員內部已發通告，指如因交通工具問題未能上班，只需致電上司就可，並呼籲僱主以體諒態度安排僱員上班。<br />不過林鄭說法隨即遭公務員踢爆講大話。有前線公務員向《蘋果》表示，林鄭口中電郵其實是放工前不足3小時，才有高級公務員收到，一般前線公務員事先並不知情；有公務員接受電視訪問，更稱他本人及其上司及上司的上司，根本沒收到有關通知。<br />面對市民狂轟政府風災善後工作失當，雖然天文台昨晚7時10分宣佈取消所有熱帶氣旋警告信號，但教育局稱為確保學生安全，今日所有學校繼續停課。運輸署署長陳美寶昨日見記者，表明仍有過千處路段受塌樹影響，政府希望今日早上5時前能清理所有主要交通幹道。<br />陳又承認600條巴士路線中，只有120條恢復，各區車廠附近路段皆有塌樹，因此阻礙巴士進出，以致未能提供服務。當陳被問到昨日路面情況是否適合復工，她未有正面回應，只說透過各路面監察器監察，認為各路段路面暢通情況可以接受，較嚴重擠塞為東鐵綫。                                    <!--please only add this icon at the end of the article -->                                                                    </p><h3>公民黨：放假有法可依</h3>                                        <p>                                多個民主派政黨譴責林鄭做法。民主黨主席胡志偉批評，政府昨拒絕宣佈停工，令上班市民及無法上班的市民均怨聲載道，該黨譴責特區政府沒有認真評估市面情況。公民黨則批評政府安排失當，「草菅香港人命」，要求政府向香港市民致歉，並盡快制訂災後應變機制。<br />前晚未見高調要求林鄭宣佈停工的建制派政黨，昨早亦加入圍攻行列。其中民建聯立法會議員陳克勤直指政府今次「堅離地」，「冇車叫人點返工？」工聯會會長吳秋北亦狠評政府特區政府高層仍未察覺到颱風所帶來的災情和對公共交通的破壞，呼籲特區政府馬上重新評估情況，考慮今日宣佈停工。<br />本身為資深大律師的行會成員湯家驊則透過facebook為林鄭「護航」，指特首無權作出宣佈停工決定，不過公民黨立法會法律界議員郭榮鏗反駁有關說法，指香港法例第241章《緊急情況規例條例》訂明，「在行政長官會同行政會議認為屬緊急情況或危害公安的情況時，行政長官會同行政會議可訂立任何他認為合乎公眾利益的規例」；由於條文指特首可以就「緊急情況」訂立規例，當中並沒有訂明範圍有多闊，故他認為特首絕對可以宣佈將昨天訂為公眾假期。                                    <!--please only add this icon at the end of the article -->                                                                        <img src=\"https://staticlayout.appledaily.hk/web_images/layout/art_end.gif\" />                                                                    </p>", updatedItem.getDescription());
        Assert.assertEquals("Incorrect image count", 5, updatedItem.getImages().size());
        Assert.assertEquals("Incorrect image description", "大批上水居民昨在無巴士、火車及道路充滿倒塌大樹情況下趕上班，結果引爆交通大混亂。謝志輝攝", updatedItem.getImages().get(0).getDescription());
        Assert.assertEquals("Incorrect image URL", "https://static.appledaily.hk/images/apple-photos/apple/20180918/large/18la1p1.jpg", updatedItem.getImages().get(0).getImageUrl());
        Assert.assertEquals("Incorrect image count", 1, updatedItem.getVideos().size());
        Assert.assertEquals("Incorrect image description", "https://video.appledaily.com.hk/mcp/encode/2018/09/18/3696914/20180917_news_02v3AD_mp4_w.mp4", updatedItem.getVideos().get(0).getVideoUrl());
        Assert.assertEquals("Incorrect image URL", "https://static.appledaily.hk/images/apple-photos/video/20180918/org/1537208110_61db.jpg", updatedItem.getVideos().get(0).getImageUrl());
    }
}
