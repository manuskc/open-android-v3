import android.test.suitebuilder.annotation.SmallTest;

import com.citrus.analytics.PaymentType;
import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.CreditCardOption;
import com.citrus.test.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by mangesh.kadam on 10/1/2015.
 */
@SmallTest
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = "src/main/AndroidManifest.xml")
public class CreditCardOptionTest {

    CreditCardOption nullCreditCardOption;


    @Before
    public void initDebitCardOption() {

        String token = null;
        String cvv = null;
        nullCreditCardOption = new CreditCardOption(token, cvv);
    }

    @Test
    public void testNullDebitCardOption() {
        assertNull(nullCreditCardOption.getCardCVV());
        assertNull(nullCreditCardOption.getCardExpiry());
        assertNull(nullCreditCardOption.getCardExpiryMonth());
        assertNull(nullCreditCardOption.getCardHolderName());
        assertNull(nullCreditCardOption.getNickName());
        assertNull(nullCreditCardOption.getCardNumber());
    }

    @Test
    public void testValidAMEXCreditCardOption() {
        CreditCardOption validCreditCardOption = new CreditCardOption("Mangesh Kadam", "378282246310005", "1234", Month.APR, Year._2017);

        assertEquals((validCreditCardOption.getCardExpiry()), ("04/2017"));
        assertEquals(validCreditCardOption.getCardHolderName(), "Mangesh Kadam");
        assertEquals(validCreditCardOption.getCardCVV(), "1234");
        assertEquals(validCreditCardOption.getCardNumber(), "378282246310005");
        assertEquals(validCreditCardOption.getCardExpiryMonth(), Month.APR.toString());

        assertEquals(validCreditCardOption.getAnalyticsPaymentType(), PaymentType.CREDIT_CARD);

        assertEquals(validCreditCardOption.getCardScheme(), CardOption.CardScheme.AMEX);

        assertEquals(validCreditCardOption.getCVVLength(), 4);

        assertTrue(validCreditCardOption.validateCard());

        assertTrue(validCreditCardOption.validateCardNumber());

        assertTrue(validCreditCardOption.validateCVV());

        assertTrue(validCreditCardOption.validateExpiryDate());
    }


    @Test
    public void testValidCreditCardOption() {
        CreditCardOption validCreditCardOption = new CreditCardOption("Mangesh Kadam", "371449635398431", "1234", Month.APR, Year._2017);

        assertEquals((validCreditCardOption.getCardExpiry()), ("04/2017"));
        assertEquals(validCreditCardOption.getCardHolderName(), "Mangesh Kadam");
        assertEquals(validCreditCardOption.getCardCVV(), "1234");
        assertEquals(validCreditCardOption.getCardNumber(), "371449635398431");
        assertEquals(validCreditCardOption.getCardExpiryMonth(), Month.APR.toString());

        assertEquals(validCreditCardOption.getAnalyticsPaymentType(), PaymentType.CREDIT_CARD);

        assertEquals(validCreditCardOption.getCardScheme(), CardOption.CardScheme.AMEX);

        assertEquals(validCreditCardOption.getCVVLength(), 4);


        CreditCardOption validCreditCardOption1 = new CreditCardOption(null, "371449635398431", "1234", Month.APR, Year._2016);
        assertTrue(validCreditCardOption1.validateCard());
        assertNotNull(validCreditCardOption1.getCardHolderName());
    }


    @Test
    public void testValidCreditCardOptionScheme() {

        CreditCardOption validCreditCardOption = new CreditCardOption("Mangesh Kadam", "4111111111111111", "123", Month.APR, Year._2017);
        assertEquals(validCreditCardOption.getCardScheme(), CardOption.CardScheme.VISA);
        assertEquals(validCreditCardOption.getCVVLength(), 3);

    }


    @Test
    public void testInValidCreditCardOption() {

        CreditCardOption validCreditCardOption = new CreditCardOption("Mangesh Kadam", "4111111111111111", "123", Month.APR, Year._2015);
        assertFalse(validCreditCardOption.validateCard());
        assertTrue(!validCreditCardOption.validateExpiryDate());
        assertEquals(validCreditCardOption.getCardValidityFailureReasons(), " Invalid Expiry Date. ");


        CreditCardOption validCreditCardOption1 = new CreditCardOption("Mangesh Kadam", "4111111111111111", "12", Month.APR, Year._2016);
        assertFalse(validCreditCardOption1.validateCard());
        assertTrue(validCreditCardOption1.validateExpiryDate());
        assertEquals(validCreditCardOption1.getCardValidityFailureReasons(), " Invalid CVV. ");


        CreditCardOption validCreditCardOption2 = new CreditCardOption("Mangesh Kadam", "", "123", Month.APR, Year._2016);
        assertFalse(validCreditCardOption2.validateCard());
        assertTrue(validCreditCardOption2.validateExpiryDate());
        assertEquals(validCreditCardOption2.getCardValidityFailureReasons(), " Invalid Card Number. ");

    }
}

