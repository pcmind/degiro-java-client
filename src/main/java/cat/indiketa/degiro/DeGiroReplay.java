package cat.indiketa.degiro;

import cat.indiketa.degiro.http.DResponse;
import cat.indiketa.degiro.http.IDCommunication;
import cat.indiketa.degiro.model.DLogin;
import cat.indiketa.degiro.session.DSession;
import cat.indiketa.degiro.utils.DCredentials;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.Value;
import org.apache.http.Header;
import org.apache.http.impl.cookie.BasicClientCookie;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

/**
 * Record interactions with degiro and then reply them without the need to be connected to real platform.
 */
public final class DeGiroReplay {
    public static final DLogin DEFAULT_USER_PASS = new DLogin("abc", "pass123");

    private DeGiroReplay() {
        //na
    }

    public static DeGiro record(DeGiroHost degiroHost, DCredentials credentials, DSession session, IDCommunication comm, Consumer<Consumer<OutputStream>> rec) {
        session.clearSession(); //ensure new login is requested
        final RecodingCom recodingCom = new RecodingCom(comm, session, rec);
        return new DeGiroImpl(degiroHost, credentials, session, recodingCom);
    }

    /**
     * @param degiroHost
     * @param credentials
     * @param session
     * @param in
     * @param fallback fallback to extend static functionality. May be null
     * @return
     */
    public static DeGiro play(DeGiroHost degiroHost, DCredentials credentials, DSession session, InputStream in, Fallback fallback) {
        session.clearSession(); //ensure new login is requested
        final List<RequestResponse> records = new Gson().fromJson(new InputStreamReader(in), new TypeToken<List<RequestResponse>>() {
        }.getType());
        final IDCommunication recodingCom = ReplayCpm.createReplayCpm(records, session, wrapInternalServerError(fallback));
        return new DeGiroImpl(degiroHost, credentials, session, recodingCom);
    }

    private static Fallback wrapInternalServerError(Fallback fallback) {
        if (fallback == null) {
            return DeGiroReplay::internalServerError;
        } else {
            return (base, uri, data, headers, method) -> {
                final DResponse urlData = fallback.getUrlData(base, uri, data, headers, method);
                if (urlData == null) {
                    return internalServerError(base, uri, data, headers, method);
                }
                return urlData;
            };
        }
    }

    private static DResponse internalServerError(String base, String uri, Object data, List<Header> headers, String method) {
        return new DResponse(500, base + uri, toNonNullMethod(data, method), "No recorded data");
    }

    private static class KeyGenerator {
        private final Gson gson = new Gson();

        public String toRequestKey(String base, String uri, Object data, List<Header> headers, String method) {
            final Map<Object, Object> map = new HashMap<>();
            map.put("base", base);
            map.put("uri", uri);
            map.put("data", data);
            map.put("headers", headers);
            map.put("method", toNonNullMethod(data, method));
            return gson.toJson(map);
        }

    }

    @Value
    public static class RequestResponse {
        String key;
        List<BasicClientCookie> responseCookiesAdded;
        DResponse response;

        public DResponse fetch(DSession session) {
            if (responseCookiesAdded != null && !responseCookiesAdded.isEmpty()) {
                if (session.getCookies() == null) {
                    session.setCookies(new ArrayList<>());
                }
                session.getCookies().addAll(responseCookiesAdded);
            }
            return response;
        }
    }

    private static class ReplayCpm implements IDCommunication {
        private final KeyGenerator keyGenerator = new KeyGenerator();
        private final Map<String, Playing> play;
        private final DSession session;
        private final Fallback fallback;
        private final AtomicLong validResponseSent = new AtomicLong();

        public ReplayCpm(Map<String, Playing> play, DSession session, Fallback fallback) {
            this.play = play;
            this.session = session;
            this.fallback = fallback;
        }

        public static IDCommunication createReplayCpm(List<RequestResponse> records, DSession session, Fallback fallback) {
            Map<String, Playing> play = new HashMap<>();
            for (RequestResponse record : records) {
                play.computeIfAbsent(record.key, k -> new Playing()
                ).records.add(record);
            }
            return new ReplayCpm(play, session, fallback);
        }

        @Override
        public DResponse getUrlData(String base, String uri, Object data, List<Header> headers, String method) throws IOException {
            final String key = keyGenerator.toRequestKey(base, uri, data, headers, method);
            final Playing playing = play.get(key);
            if (playing == null) {
                String m = toNonNullMethod(data, method);
                //handle any user password as default password. treat "invalid" password text as invalid for to test authentication failure
                if ("/login/secure/login".equals(uri) && data != null) {
                    final DLogin data1 = (DLogin) data;
                    if (!"abc".equals(data1.getUsername())) {
                        return getUrlData(base, uri, DEFAULT_USER_PASS, headers, method);
                    } else if ("invalid".equalsIgnoreCase(data1.getPassword())) {
                        return new DResponse(400, base + uri, m, "Invalid credentials");
                    }
                }
                //until first ok response is sent; send Unauthenticated
                if (validResponseSent.get() == 0) {
                    return new DResponse(401, base + uri, m, "HTTP Status 401 – Unauthorized");
                }
                return fallback.getUrlData(base, uri, data, headers, method);
            }
            validResponseSent.incrementAndGet();
            return playing.fetch(session);
        }

        private static class Playing {
            private final List<RequestResponse> records = new ArrayList<>();
            private int playedIndex = -1;

            public DResponse fetch(DSession session) {
                if (++playedIndex >= records.size()) {
                    return records.get(records.size() - 1).fetch(session); //Playing exist only if records do exist
                } else {
                    return records.get(playedIndex).fetch(session);
                }
            }
        }
    }

    private static String toNonNullMethod(Object data, String method) {
        return method == null ? (data == null ? "GET" : "POST") : method;
    }

    private static class RecodingCom implements IDCommunication {
        private final KeyGenerator keyGenerator = new KeyGenerator();
        private final DSession session;
        private final Consumer<Consumer<OutputStream>> rec;

        private final List<RequestResponse> recording = new ArrayList<>();
        private final IDCommunication comm;
        private Future<?> f;

        public RecodingCom(IDCommunication comm, DSession session, Consumer<Consumer<OutputStream>> rec) {
            this.comm = comm;
            this.session = session;
            this.rec = rec;
        }

        @Override
        public DResponse getUrlData(String base, String uri, Object data, List<Header> headers, String method) throws IOException {
            final Set<BasicClientCookie> before = Sets.newHashSet(MoreObjects.firstNonNull(session.getCookies(), Collections.emptyList()));
            final DResponse urlResponse = comm.getUrlData(base, uri, data, headers, method);
            final Set<BasicClientCookie> after = Sets.newHashSet(MoreObjects.firstNonNull(session.getCookies(), Collections.emptyList()));
            final Sets.SetView<BasicClientCookie> difference = Sets.difference(after, before);
            List<BasicClientCookie> cookies = null;
            if (!difference.isEmpty()) {
                cookies = new ArrayList<>(difference);
            }
            if ("/login/secure/login".equals(uri) && data != null) {
                //dot not keep original user password
                data = DEFAULT_USER_PASS;
            }
            final String key = keyGenerator.toRequestKey(base, uri, data, headers, method);
            final RequestResponse e = new RequestResponse(
                    key,
                    cookies,
                    urlResponse
            );
            synchronized (recording) {
                recording.add(e);
            }
            rec.accept(out -> {
                synchronized (recording) {
                    final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    try (OutputStreamWriter outputStreamWriter = new OutputStreamWriter(out)) {
                        gson.toJson(recording, outputStreamWriter);
                        outputStreamWriter.flush();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                    ;
                }
            });
            return urlResponse;
        }
    }

    public interface Fallback {
        DResponse getUrlData(String base, String uri, Object data, List<Header> headers, String method)
                throws IOException;
    }
}
