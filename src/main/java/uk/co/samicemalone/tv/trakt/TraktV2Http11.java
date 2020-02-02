package uk.co.samicemalone.tv.trakt;

import com.uwetrottmann.trakt5.TraktV2;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;

import java.util.Collections;

public class TraktV2Http11 extends TraktV2 {
    public TraktV2Http11(String apiKey, String clientSecret, String redirectUri) {
        super(apiKey, clientSecret, redirectUri);
    }

    @Override
    protected void setOkHttpClientDefaults(OkHttpClient.Builder builder) {
        super.setOkHttpClientDefaults(builder);
        builder.protocols(Collections.singletonList(Protocol.HTTP_1_1));
    }
}
