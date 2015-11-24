import android.test.suitebuilder.annotation.SmallTest;

import com.citrus.sdk.classes.AccessToken;
import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

/**
 * Created by mangesh.kadam on 9/30/2015.
 */
@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class AccessTokenTest {

    @Mock
    AccessToken accessToken;

    @Test
    public void testNullAccessToken() {
        assertNull(accessToken.getAccessToken());
        assertEquals(accessToken.getExpiresIn(), 0);
        assertNull(accessToken.getHeaderAccessToken());
        assertNull(accessToken.getRefreshToken());
        assertNull(accessToken.getScope());
        assertNull(accessToken.getTokenType());
    }

    @Test
    public void testSignInAccessTokenFromJSON() {

        Gson gson = new Gson();
        AccessToken token = gson.fromJson("{\n" +
                "  \"access_token\": \"1bb9ae1d-4f9e-43de-8c7d-ce5644c9b12d\",\n" +
                "  \"token_type\": \"bearer\",\n" +
                "  \"refresh_token\": \"c389dc80-c54f-47c0-8db0-84509ea21af3\",\n" +
                "  \"expires_in\": 2055554,\n" +
                "  \"scope\": \"identity prepaid prepaid_merchant profile\"\n" +
                "}", AccessToken.class);
        assertEquals(token.getAccessToken(), "1bb9ae1d-4f9e-43de-8c7d-ce5644c9b12d");
        assertEquals(token.getRefreshToken(), "c389dc80-c54f-47c0-8db0-84509ea21af3");
        assertEquals(token.getTokenType(), "bearer");
        assertEquals(token.getScope(), "identity prepaid prepaid_merchant profile");
        assertEquals(token.getExpiresIn(), 2055554);
        assertEquals(token.getHeaderAccessToken(), "Bearer " + token.getAccessToken());
    }

    @Test
    public void testSignUpAccessToken() {
        Gson gson = new Gson();
        AccessToken signUpToken = gson.fromJson("{\n" +
                "  \"access_token\": \"2b85cd7d-26e3-4a0d-804e-d8f0f4823e1e\",\n" +
                "  \"token_type\": \"bearer\",\n" +
                "  \"expires_in\": 7670457,\n" +
                "  \"scope\": \"subscription\"\n" +
                "}", AccessToken.class);

        assertNull(signUpToken.getRefreshToken());
        assertEquals(signUpToken.getScope(), "subscription");
    }
}
