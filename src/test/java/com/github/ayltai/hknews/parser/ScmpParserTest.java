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

public final class ScmpParserTest extends ParserTest {
    @Test
    public void testGetItem() throws IOException {
        final ApiServiceFactory factory = Mockito.mock(ApiServiceFactory.class);
        final ApiService        service = Mockito.mock(ApiService.class);

        Mockito.doReturn(service).when(factory).create();

        try (InputStreamReader inputStreamReader = new InputStreamReader(this.getClass().getClassLoader().getResourceAsStream("testdata/scmp.html"), StandardCharsets.UTF_8)) {
            try (BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
                final Call             call     = Mockito.mock(Call.class);
                final Response<String> response = Response.success(bufferedReader.lines().collect(Collectors.joining("\n")));

                Mockito.doReturn(call).when(service).getHtml("https://www.scmp.com/business/companies/article/2156775/hong-kong-bike-sharing-start-sells-stake-crypto-coins-get-tech");
                Mockito.doReturn(response).when(call).execute();

                final Item item = new Item();
                item.setUrl("https://www.scmp.com/business/companies/article/2156775/hong-kong-bike-sharing-start-sells-stake-crypto-coins-get-tech");

                final Item updatedItem = new ScmpParser(factory, this.sourceRepository, this.itemRepository).getItem(item);

                Assert.assertEquals("Incorrect item description", "Ketch’up Bike, the smallest among five of Hong Kong’s bicycle-sharing services, has sold a majority stake in its seven-month-old operation for cryptocurrency coins, as it seeks a technological lifeline to reinvent its business while the remainder of the cash-burning industry goes through a shake-up.<br><br>The company, which operates 1,000 bicycles around Sha Tin in the New Territories, said it has sold a 60 per cent stake valued at about US$1 million to a unit of South Korean blockchain company JNU for unspecified units of two coins called JPAY and CyClean.<br><br>The transaction would marry two newfangled concepts: the mobile internet-enabled sharing economy with blockchain’s distributed ledgers, which could offer a technological“breakthrough” out of the growth quagmire besetting the bike-sharing business that no fiat currency could address, said Ketch’up co-founder and chief executive officer Kenneth Chau.<br><br>“The biggest challenge faced by bike-sharing start-ups is the manpower required to manage the bicycles that are leased out,” said Chau, who holds 40 per cent of Ketch’up after the deal with three other shareholders. “By using blockchain, every rental transaction will be recorded on the distributed ledger, including the customer’s payment track record, and the number of miles that he has travelled.”<br><br>JNU, founded in South Korea in 2015, has transformed into a blockchain company from a maker of video security recorders. Aside from CyClean, founded by JNU group chairman David Young, JNU is today focused on building a blockchain-based payment network using its JPAY cryptocurrency. It has branches in Shenzhen and Harbin, which until recently were distributing health products for the JNU group but has recently turned to developing a blockchain business.<br><br>Both of the coins will be traded exclusively starting August 1 on KocoStock, or the South Korean Coin Stock for cryptocurrency coins, which is also owned by JNU.<br><br>Based on the ethereum blockchain, CyClean claims that ethereum’s smart contract feature – a kind of digital contract that can self-execute in accordance to conditions specified in computer code – can automatically lock up the conveyance when rental fee is delayed.<br><br>One key differentiator for CyClean’s rental platform is that users who rent their vehicles will be rewarded with CyClean coins, which amount to 25 per cent of the total coins to be distributed, said the coin issuer’s strategy planner Joseph Nam. A CyClean coin is equivalent to 0.0001 ethereum, or less than 5 US cents each at current market price.<br><br>“Market analysis has shown that there is a lack of motivation for vehicle owners to move to clean energy vehicles, from petrol powered vehicles,” Nam said. “By rewarding users with CyClean coins, users will have better incentives to make the switch as they can be used to rent different kinds of products on the platform.”<br><br>CyClean plans to also include electric motorcycles and autonomous vehicles on its rental platform. By installing a meter which can track the actual distance travelled and is connected to the company’s server, users can receive accurate rewards in tokens.<br><br>Ketch’up is riding on JNU’s help to expand to Vietnam, Malaysia and Singapore through the green vehicle “smart rental platform” that CyClean, a Singapore entity controlled by JNU and the issuer of the Cyclean token sale, is hoping to build globally.<br><br>Not so fast, said Johnny Au Yeung, chief technology officer for Standard Kepler, a consultant for ICO in Hong Kong. The problem with dockless bicycle sharing is an old-world problem, for which blockchain’s smart contracts may not be the most appropriate solution, he said.<br><br>“The nature of the locking problem troubling bike-sharing platforms is that people still get charged even though they have finished using the bike and locked it up,” Au Yeung said. “This is basically a problem of false detection of the locking device, and thus false handling of the user’s usage of the bike. Hence, this is not an issue that can be solved by smart contracts.”<br><br>", updatedItem.getDescription());
                Assert.assertEquals("Incorrect image count", 3, updatedItem.getImages().size());
                Assert.assertEquals("Incorrect image description", "Ketch'Up Bike in Hong Kong. Photo: SCMP/Handout", updatedItem.getImages().get(0).getDescription());
                Assert.assertEquals("Incorrect image URL", "https://cdn1.i-scmp.com/sites/default/files/images/methode/2018/07/25/d337d59c-8fb3-11e8-ad1d-4615aa6bc452_image_hires_200347.jpg", updatedItem.getImages().get(0).getImageUrl());
                Assert.assertEquals("Incorrect video count", 1, updatedItem.getVideos().size());
                Assert.assertEquals("Incorrect video URL", "https://cf.cdn.vid.ly/5o6g9f/hd_mp4.mp4", updatedItem.getVideos().get(0).getVideoUrl());
                Assert.assertEquals("Incorrect thumbnail URL", "https://vid.ly/5o6g9f/poster_hd", updatedItem.getVideos().get(0).getImageUrl());
            }
        }
    }
}
