import android.test.suitebuilder.annotation.SmallTest;

import com.citrus.sdk.Environment;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Created by mangesh.kadam on 9/29/2015.
 */
@SmallTest
public class EnvironmentTest {

    String sandboxAnalyticsID;

    String productionAnalyticsID;

    Environment sandboxEnvironment;
    Environment productionEnvironment;

    @Before
    public void initTestData() {
        sandboxEnvironment = Environment.SANDBOX;
        productionEnvironment = Environment.PRODUCTION;
        sandboxAnalyticsID = "UA-33514461-4";
        productionAnalyticsID = "UA-33514461-5";

    }

    @Test
    public void testSandboxEnvironment() {
        assertTrue(sandboxEnvironment.getBaseUrl().equalsIgnoreCase("https://sandboxadmin.citruspay.com"));
        assertTrue(sandboxEnvironment.getBaseCitrusUrl().equalsIgnoreCase("https://sandbox.citruspay.com"));
        assertTrue(sandboxEnvironment.getAnalyticsID().equalsIgnoreCase(sandboxAnalyticsID));
        assertTrue(sandboxEnvironment.toString().equalsIgnoreCase("SANDBOX"));
    }


    @Test
    public void testProductionEnvironment() {
        assertTrue(productionEnvironment.getBaseUrl().equalsIgnoreCase("https://admin.citruspay.com"));
        assertTrue(productionEnvironment.getBaseCitrusUrl().equalsIgnoreCase("https://citruspay.com"));
        assertTrue(productionEnvironment.getAnalyticsID().equalsIgnoreCase(productionAnalyticsID));
        assertTrue(productionEnvironment.toString().equalsIgnoreCase("PRODUCTION"));
    }

    @Test
    public void testBinServiceURL() {
        assertTrue(sandboxEnvironment.getBinServiceURL().equalsIgnoreCase("https://citrusapi.citruspay.com"));
        assertTrue(productionEnvironment.getBinServiceURL().equalsIgnoreCase("https://citrusapi.citruspay.com"));
    }


}
