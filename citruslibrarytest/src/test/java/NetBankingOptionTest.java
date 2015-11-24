import android.test.suitebuilder.annotation.SmallTest;

import com.citrus.sdk.payment.NetbankingOption;
import com.citrus.test.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;


/**
 * Created by mangesh.kadam on 10/1/2015.
 */
@SmallTest
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 18, manifest = "src/main/AndroidManifest.xml")
public class NetBankingOptionTest {

    NetbankingOption nullNetBankingOption;


    @Before
    public void initNetBankingOption() {
        nullNetBankingOption = new NetbankingOption(null,null);
    }

    @Test
    public void testNullNetBankingOption() {
        assertNull(nullNetBankingOption.getBankName());
        assertNull(nullNetBankingOption.getBankCID());
        assertNull(nullNetBankingOption.getToken());
        assertNull(nullNetBankingOption.getName());
    }


    @Test
    public void testValidNetBankingOption() {
        NetbankingOption netbankingOption =  new NetbankingOption("ICICI Bank","CID001");
        assertNotNull(netbankingOption.getBankName());
        assertNotNull(netbankingOption.getBankCID());
    }



}