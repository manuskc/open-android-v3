import android.test.suitebuilder.annotation.SmallTest;

import com.citrus.analytics.PaymentType;
import com.citrus.sdk.classes.Month;
import com.citrus.sdk.classes.Year;
import com.citrus.sdk.payment.CardOption;
import com.citrus.sdk.payment.DebitCardOption;
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
public class DebitCardOptionTest {

    DebitCardOption nullDebitCardOption;


    @Before
    public void initDebitCardOption() {

        String token = null;
        String cvv = null;
        nullDebitCardOption = new DebitCardOption(token, cvv);
    }

    @Test
    public void testNullDebitCardOption() {
        assertNull(nullDebitCardOption.getCardCVV());
        assertNull(nullDebitCardOption.getCardExpiry());
        assertNull(nullDebitCardOption.getCardExpiryMonth());
        assertNull(nullDebitCardOption.getCardHolderName());
        assertNull(nullDebitCardOption.getNickName());
        assertNull(nullDebitCardOption.getCardNumber());
    }

    @Test
    public void testValidVisaDebitCardOption() {
        DebitCardOption validDebitCardOption = new DebitCardOption("Mangesh Kadam", "4111111111111111","123", Month.APR, Year._2017);

        assertEquals((validDebitCardOption.getCardExpiry()),("04/2017"));
        assertEquals(validDebitCardOption.getCardHolderName(), "Mangesh Kadam");
        assertEquals(validDebitCardOption.getCardCVV(), "123");
        assertEquals(validDebitCardOption.getCardNumber(), "4111111111111111");
        assertEquals(validDebitCardOption.getCardExpiryMonth(), Month.APR.toString());

        assertEquals(validDebitCardOption.getAnalyticsPaymentType(), PaymentType.DEBIT_CARD);

        assertEquals(validDebitCardOption.getCardScheme(), CardOption.CardScheme.VISA);

        assertEquals(validDebitCardOption.getCVVLength(), 3);

        assertTrue(validDebitCardOption.validateCard());

        assertTrue(validDebitCardOption.validateCardNumber());

        assertTrue(validDebitCardOption.validateCVV());

        assertTrue(validDebitCardOption.validateExpiryDate());
    }



    @Test
    public void testValidDebitCardOption() {
        DebitCardOption validDebitCardOption = new DebitCardOption("Mangesh Kadam", "4111111111111111","123", Month.APR, Year._2017);

        assertEquals((validDebitCardOption.getCardExpiry()),("04/2017"));
        assertEquals(validDebitCardOption.getCardHolderName(), "Mangesh Kadam");
        assertEquals(validDebitCardOption.getCardCVV(), "123");
        assertEquals(validDebitCardOption.getCardNumber(), "4111111111111111");
        assertEquals(validDebitCardOption.getCardExpiryMonth(), Month.APR.toString());

        assertEquals(validDebitCardOption.getAnalyticsPaymentType(), PaymentType.DEBIT_CARD);

        assertEquals(validDebitCardOption.getCardScheme(), CardOption.CardScheme.VISA);

        assertEquals(validDebitCardOption.getCVVLength(), 3);


        DebitCardOption validDebitCardOption1 = new DebitCardOption(null, "4111111111111111","123", Month.APR, Year._2016);
        assertTrue(validDebitCardOption1.validateCard());
        assertNotNull(validDebitCardOption1.getCardHolderName());
    }


    @Test
    public void testValidDebitCardOptionScheme() {

        DebitCardOption validDebitCardOption = new DebitCardOption("Mangesh Kadam", "4111111111111111","123", Month.APR, Year._2017);
        assertEquals(validDebitCardOption.getCardScheme(), CardOption.CardScheme.VISA);
        assertEquals(validDebitCardOption.getCVVLength(), 3);

        DebitCardOption validDebitCardOption1 = new DebitCardOption("Mangesh Kadam", "5555555555554444","123", Month.APR, Year._2017);
        assertEquals(validDebitCardOption1.getCardScheme(), CardOption.CardScheme.MASTER_CARD);
        assertEquals(validDebitCardOption1.getCVVLength(), 3);

        DebitCardOption validDebitCardOption2 = new DebitCardOption("Mangesh Kadam", "6762407506473539","123", Month.APR, Year._2017);
        assertEquals(validDebitCardOption2.getCardScheme(), CardOption.CardScheme.MAESTRO);
        assertEquals(validDebitCardOption2.getCVVLength(), 3);


    }


    @Test
    public void testInValidDebitCardOption() {

        DebitCardOption validDebitCardOption = new DebitCardOption("Mangesh Kadam", "4111111111111111","123", Month.APR, Year._2015);
        assertFalse(validDebitCardOption.validateCard());
        assertTrue(!validDebitCardOption.validateExpiryDate());
        assertEquals(validDebitCardOption.getCardValidityFailureReasons(), " Invalid Expiry Date. ");


        DebitCardOption validDebitCardOption1 = new DebitCardOption("Mangesh Kadam", "4111111111111111","12", Month.APR, Year._2016);
        assertFalse(validDebitCardOption1.validateCard());
        assertTrue(validDebitCardOption1.validateExpiryDate());
        assertEquals(validDebitCardOption1.getCardValidityFailureReasons(), " Invalid CVV. ");


        DebitCardOption validDebitCardOption2 = new DebitCardOption("Mangesh Kadam", "","123", Month.APR, Year._2016);
        assertFalse(validDebitCardOption2.validateCard());
        assertTrue(validDebitCardOption2.validateExpiryDate());
        assertEquals(validDebitCardOption2.getCardValidityFailureReasons(), " Invalid Card Number. ");

    }
}
