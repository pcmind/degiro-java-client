package cat.indiketa.degiro.http;

import lombok.Value;

@Value
public class DResponse {

    private final int status;
    private final String url;
    private final String method;
    private final String text;

    public DResponse(int status, String url, String method, String text) {
        this.status = status;
        this.url = url;
        this.method = method;
        this.text = text;
    }
}
