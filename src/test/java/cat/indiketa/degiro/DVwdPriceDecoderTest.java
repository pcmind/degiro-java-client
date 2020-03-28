package cat.indiketa.degiro;

import cat.indiketa.degiro.exceptions.SessionExpiredException;
import cat.indiketa.degiro.model.DPrice;
import cat.indiketa.degiro.model.raw.DRawVwdPrice;
import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DVwdPriceDecoderTest {
    private static final Type rawPriceData = new TypeToken<List<DRawVwdPrice>>() {
    }.getType();

    private static List<DRawVwdPrice> fromJson(String reply) throws IOException {
        return new DJsonDecoder().fromJson(reply, rawPriceData);
    }

    @Test
    void decodeCorrectPriceData() throws IOException, SessionExpiredException {
        //given
        String data = "[{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.BidPrice\",5822]}," +
                "{\"m\":\"ue\",\"v\":[5822]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.AskPrice\",5823]}," +
                "{\"m\":\"ue\",\"v\":[5823]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.LastPrice\",5824]}," +
                "{\"m\":\"un\",\"v\":[5824,11.275000]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.OpenPrice\",33530]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.HighPrice\",33528]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.LowPrice\",33529]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.PreviousClosePrice\",5825]}," +
                "{\"m\":\"un\",\"v\":[5825,11.240000]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.BidVolume\",276893]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.AskVolume\",276892]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.CumulativeVolume\",33527]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.LastTime\",5826]}," +
                "{\"m\":\"us\",\"v\":[5826,\"20:59:47\"]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.LastDate\",5827]}," +
                "{\"m\":\"us\",\"v\":[5827,\"2020-03-20\"]}," +
                "{\"m\":\"a_req\",\"v\":[\"SPCE.BATS,E.FullName\",5828]}," +
                "{\"m\":\"us\",\"v\":[5828,\"Virgin Galactic Holdings\"]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.BidPrice\",521]}," +
                "{\"m\":\"un\",\"v\":[521,280.000000]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.AskPrice\",522]}," +
                "{\"m\":\"un\",\"v\":[522,333.900000]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.LastPrice\",523]}," +
                "{\"m\":\"un\",\"v\":[523,332.700000]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.OpenPrice\",1898]}," +
                "{\"m\":\"un\",\"v\":[1898,342.225000]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.HighPrice\",1896]}," +
                "{\"m\":\"un\",\"v\":[1896,350.060000]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.LowPrice\",1897]}," +
                "{\"m\":\"un\",\"v\":[1897,332.000000]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.PreviousClosePrice\",524]}," +
                "{\"m\":\"un\",\"v\":[524,332.700000]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.BidVolume\",134276]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.AskVolume\",134275]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.CumulativeVolume\",1895]}," +
                "{\"m\":\"un\",\"v\":[1895,1114621]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.LastTime\",525]}," +
                "{\"m\":\"us\",\"v\":[525,\"21:00:07\"]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.LastDate\",526]}," +
                "{\"m\":\"us\",\"v\":[526,\"2020-03-20\"]}," +
                "{\"m\":\"a_req\",\"v\":[\"NFLX.BATS,E.FullName\",527]}," +
                "{\"m\":\"us\",\"v\":[527,\"Netflix\"]}," +
                "{\"m\":\"un\",\"v\":[134275,100]}," +
                "{\"m\":\"un\",\"v\":[33530,11.490000]}," +
                "{\"m\":\"un\",\"v\":[33528,14.740000]}," +
                "{\"m\":\"un\",\"v\":[33529,10.990000]}," +
                "{\"m\":\"un\",\"v\":[33527,1030012]}," +
                "{\"m\":\"un\",\"v\":[134276,100]}," +
                "{\"m\":\"un\",\"v\":[134275,100]}" +
                "]";
        final DVwdPriceDecoder vwdDecoder = new DVwdPriceDecoder();

        final List<DPrice> convert = vwdDecoder.decode(fromJson(data));
        final ArrayList<DPrice> expected = Lists.newArrayList(
                new DPrice(
                        "SPCE.BATS,E",
                        null,
                        null,
                        11.275,
                        "20:59:47",
                        null,
                        11.49,
                        10.99,
                        14.74,
                        11.24,
                        null,
                        null,
                        1030012.0,
                        "2020-03-20",
                        "2020-03-20 20:59:47",
                        "Virgin Galactic Holdings",
                        LocalDateTime.parse("2020-03-20T20:59:47")
                ),

                new DPrice("NFLX.BATS," +
                        "E",
                        280.0,
                        333.9,
                        332.7,
                        "21:00:07",
                        null,
                        342.225,
                        332.0,
                        350.06,
                        332.7,
                        100.0,
                        100.0,
                        1114621.0,
                        "2020-03-20",
                        "2020-03-20 21:00:07",
                        "Netflix",
                        LocalDateTime.parse("2020-03-20T21:00:07")
                )
        );
        Assertions.assertEquals(
                expected,
                convert
        );
        Assertions.assertEquals(2, convert.size());
        String batch2 = "[{\"m\":\"un\",\"v\":[33530,1922.160000]}," +
                "{\"m\":\"un\",\"v\":[33528,15.980000]}," +
                "{\"m\":\"un\",\"v\":[276893,100]}," +
                "{\"m\":\"us\",\"v\":[5826,\"22:38:20\"]}]";
        final List<DPrice> decode = vwdDecoder.decode(fromJson(batch2));
        Assertions.assertEquals(Collections.singletonList(
                expected.get(0)
                        .withOpen(1922.160000)
                        .withHigh(15.980000)
                        .withBidVolume(100.0)
                        .withLastTime("22:38:20")
                        .withCombinedLastDateTime("2020-03-20 22:38:20")
                        .withLastDateTime(LocalDateTime.parse("2020-03-20T22:38:20") )
        ), decode);

    }

    @Test
    void decodeHidleMEssage() throws IOException, SessionExpiredException {

        final List<DRawVwdPrice> data = fromJson("[{\"m\":\"h\"}]");
        final DVwdPriceDecoder vwdDecoder = new DVwdPriceDecoder();
        final List<DPrice> decode = vwdDecoder.decode(data);
        Assertions.assertEquals(0, decode.size());
    }

    @Test
    void decodeNewSession() throws IOException {
        final List<DRawVwdPrice> data = fromJson("[{\"m\":\"sr\"}]");
        final DVwdPriceDecoder vwdDecoder = new DVwdPriceDecoder();
        Assertions.assertThrows(SessionExpiredException.class, () -> vwdDecoder.decode(data));
    }
}