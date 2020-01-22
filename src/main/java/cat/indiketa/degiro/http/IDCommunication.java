package cat.indiketa.degiro.http;

import org.apache.http.Header;

import java.io.IOException;
import java.util.List;

public interface IDCommunication {

    default DResponse getUrlData(String base, String uri, Object data) throws IOException {
        return getUrlData(base, uri, data, null, null);
    }

    default DResponse getUrlData(String base, String uri, Object data, List<Header> headers) throws IOException {
        return getUrlData(base, uri, data, headers, null);
    }

    DResponse getUrlData(String base, String uri, Object data, List<Header> headers, String method)
        throws IOException;
}
