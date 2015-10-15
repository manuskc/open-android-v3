import android.test.suitebuilder.annotation.SmallTest;

import com.citrus.sdk.classes.Amount;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by mangesh.kadam on 9/30/2015.
 */
@SmallTest
@RunWith(MockitoJUnitRunner.class)
public class AmountTest {

    @Mock
    Amount nullAmount;

    @Test
    public void testNullAmount() {
        assertNull(nullAmount.getValue());
        assertNull(nullAmount.getCurrency());
    }

    @Test
    public void testAmountObject() {
        Amount amount = new Amount("5");
        assertTrue(amount.getValue() == "5");
        assertTrue(amount.getCurrency() == "INR");
        assertTrue(amount.getValueAsDouble() == 5);

        JSONObject amountObject = mock(JSONObject.class);

        when(amountObject.toString()).thenReturn("{\n" +
                "  \"value\": 5,\n" +
                "  \"currency\": \"INR\"\n" +
                "}");
            when(amountObject.optString("value")).thenReturn("5");
            when(amountObject.optString("currency")).thenReturn("INR");

        Amount duplicateAmount = Amount.fromJSONObject(amountObject);
        assertTrue(amount.toString().equalsIgnoreCase( duplicateAmount.toString()));


        Amount amountUS = new Amount("5", "$");
        assertTrue(amountUS.getValue() == "5");
        assertTrue(amountUS.getCurrency() == "$");
    }


}
