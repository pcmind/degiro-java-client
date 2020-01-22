package cat.indiketa.degiro;

import cat.indiketa.degiro.model.DOrderConfirmation;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DJsonDecoderTest {
    DJsonDecoder d = new DJsonDecoder();
    @Test
    void decodeErrorMessage() throws IOException {
        final DOrderConfirmation result = d.fromJsonData(
                "{\"data\":{\"confirmationId\":\"12caf3da-c2f2-4c0a-b5c2-e5f42c04a4be\",\"transactionFees\":[{\"id\":2,\"amount\":0.04,\"currency\":\"USD\"},{\"id\":3,\"amount\":0.50,\"currency\":\"EUR\"}]}}"
                , DOrderConfirmation.class
        );
        assertEquals("12caf3da-c2f2-4c0a-b5c2-e5f42c04a4be", result.getConfirmationId());
    }
}