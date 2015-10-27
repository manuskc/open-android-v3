import android.test.suitebuilder.annotation.SmallTest;

import com.citrus.sdk.classes.Amount;
import com.citrus.sdk.payment.PaymentBill;
import com.citrus.test.BuildConfig;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by mangesh.kadam on 10/5/2015.
 */

@SmallTest
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = "src/main/AndroidManifest.xml")
public class PaymentBillTest {

    @Test
    public void testNullPaymentBill() {
        PaymentBill paymentBill = PaymentBill.fromJSON(null);
        assertNull(paymentBill);
        /*assertNull(paymentBill.getAmount());
        assertNull(paymentBill.getMerchantAccessKey());
        assertNull(paymentBill.getMerchantTransactionId());
        assertNull(paymentBill.getReturnUrl());
        assertNull(paymentBill.getRequestSignature());
        assertNull(paymentBill.getCustomParametersMap());
        assertNull(paymentBill.getNotifyUrl());*/
    }

    @Test
    public void testValidPaymentBil() {
        PaymentBill paymentBill = PaymentBill.fromJSON("{\"merchantTxnId\":\"144402901140520\"," +
                "\"amount\":{\"value\":\"3.0\",\"currency\":\"INR\"},\"requestSignature\":\"e91d69e94f559e9372a15d38e8a74e75fc3f8959\"," +
                "\"merchantAccessKey\":\"F2VZD1HBS2VVXJPMWO77\",\"returnUrl\":\"https:\\/\\/salty-plateau-1529.herokuapp.com\\/redirectURL.sandbox.php\"," +
                "\"notifyUrl\":\"https:\\/\\/salty-plateau-1529.herokuapp.com\\/notifyUrl.sandbox.php\"," +
                "\"customParameters\":{\"param1\":\"1000\",\"param2\":\"CitrusTestSDK\"}}");

        //check parameters are not null
        assertNotNull(paymentBill);
        assertNotNull(paymentBill.getAmount());
        assertNotNull(paymentBill.getMerchantAccessKey());
        assertNotNull(paymentBill.getMerchantTransactionId());
        assertNotNull(paymentBill.getReturnUrl());
        assertNotNull(paymentBill.getRequestSignature());
        assertNotNull(paymentBill.getCustomParametersMap());
        assertNotNull(paymentBill.getNotifyUrl());

        assertEquals(paymentBill.getAmount(), new Amount("3.0"));
        assertEquals(paymentBill.getMerchantAccessKey(), "F2VZD1HBS2VVXJPMWO77");
        assertEquals(paymentBill.getMerchantTransactionId(), "144402901140520");
        assertEquals(paymentBill.getReturnUrl(), "https:\\/\\/salty-plateau-1529.herokuapp.com\\/redirectURL.sandbox.php".replaceAll("\\\\",""));
        assertEquals(paymentBill.getRequestSignature(), "e91d69e94f559e9372a15d38e8a74e75fc3f8959");
        Map<String, String> customParametersMap = new HashMap<>();
        customParametersMap.put("param1", "1000");
        customParametersMap.put("param2", "CitrusTestSDK");
        assertEquals(paymentBill.getCustomParametersMap(), customParametersMap);
        assertEquals(paymentBill.getNotifyUrl(), "https:\\/\\/salty-plateau-1529.herokuapp.com\\/notifyUrl.sandbox.php".replaceAll("\\\\", ""));

    }


    @Test
    public void testValidPaymentBillWithAdditionalParameters() {
        PaymentBill paymentBill = PaymentBill.fromJSON("{\"merchantTxnId\":\"144402901140520\"," +
                "\"amount\":{\"value\":\"3.0\",\"currency\":\"INR\"},\"requestSignature\":\"e91d69e94f559e9372a15d38e8a74e75fc3f8959\"," +
                "\"merchantAccessKey\":\"F2VZD1HBS2VVXJPMWO77\",\"returnUrl\":\"https:\\/\\/salty-plateau-1529.herokuapp.com\\/redirectURL.sandbox.php\"," +
                "\"notifyUrl\":\"https:\\/\\/salty-plateau-1529.herokuapp.com\\/notifyUrl.sandbox.php\"," +
                "\"serverURL\":\"https:\\/\\/salty-plateau-1529.herokuapp.com\\/notifyUrl.sandbox.php\"," +//this is extra
                "\"customParameters\":{\"param1\":\"1000\",\"param2\":\"CitrusTestSDK\"}}");

        //check parameters are not null
        assertNotNull(paymentBill);
        assertNotNull(paymentBill.getAmount());
        assertNotNull(paymentBill.getMerchantAccessKey());
        assertNotNull(paymentBill.getMerchantTransactionId());
        assertNotNull(paymentBill.getReturnUrl());
        assertNotNull(paymentBill.getRequestSignature());
        assertNotNull(paymentBill.getCustomParametersMap());
        assertNotNull(paymentBill.getNotifyUrl());

        assertEquals(paymentBill.getAmount(), new Amount("3.0"));
        assertEquals(paymentBill.getMerchantAccessKey(), "F2VZD1HBS2VVXJPMWO77");
        assertEquals(paymentBill.getMerchantTransactionId(), "144402901140520");
        assertEquals(paymentBill.getReturnUrl(), "https:\\/\\/salty-plateau-1529.herokuapp.com\\/redirectURL.sandbox.php".replaceAll("\\\\",""));
        assertEquals(paymentBill.getRequestSignature(), "e91d69e94f559e9372a15d38e8a74e75fc3f8959");
        Map<String, String> customParametersMap = new HashMap<>();
        customParametersMap.put("param1", "1000");
        customParametersMap.put("param2", "CitrusTestSDK");
        assertEquals(paymentBill.getCustomParametersMap(), customParametersMap);
        assertEquals(paymentBill.getNotifyUrl(), "https:\\/\\/salty-plateau-1529.herokuapp.com\\/notifyUrl.sandbox.php".replaceAll("\\\\", ""));

    }
}
