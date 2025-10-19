package web3.beaglegaze;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigInteger;

import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import web3.beaglegaze.api.IntegrationTestBase;

public class NFTSubscriptionTierIntegrationTest extends IntegrationTestBase {

    private static final String CLIENT_ACCOUNT_PRIV_KEY = "0xbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb";
    private static final String SMART_CONTRACT_OWNER_PRIV_KEY = "0xaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    private final BigInteger ONE_ETH_IN_WEI = BigInteger.valueOf(1000000000000000000L);
    private final BigInteger TWO_ETH_IN_WEI = BigInteger.valueOf(2000000000000000000L);

    @Test
    void shouldSetSubscriptionTierAndEnforcePrice() throws Exception {
        Beaglegaze_sol_Beaglegaze contract = deployContract(networkAddress,
                TWO_ETH_IN_WEI);
        Beaglegaze_sol_Beaglegaze clientContract = loadContractAsClient(contract.getContractAddress());

        assertThrows(Exception.class, () -> {
            clientContract.purchaseSubscription(ONE_ETH_IN_WEI).send();
        });

        TransactionReceipt purchaseReceipt = clientContract.purchaseSubscription(TWO_ETH_IN_WEI).send();
        assertThat(purchaseReceipt.isStatusOK(), is(true));

        boolean hasValidSubscription = clientContract.hasValidSubscription().send();
        assertThat(hasValidSubscription, is(true));
    }

    private Beaglegaze_sol_Beaglegaze loadContractAsClient(String contractAddress) {
        return loadContract(networkAddress, contractAddress, CLIENT_ACCOUNT_PRIV_KEY);
    }

    private Beaglegaze_sol_Beaglegaze deployContract(String networkAddress,
            BigInteger withSubscriptionFee) throws Exception {
        return Beaglegaze_sol_Beaglegaze.deploy(
                Web3j.build(new HttpService(networkAddress)),
                Credentials.create(SMART_CONTRACT_OWNER_PRIV_KEY),
                new DefaultGasProvider(), withSubscriptionFee).send();
    }

    private Beaglegaze_sol_Beaglegaze loadContract(String networkAddress, String contractAddress,
            String account) {
        return Beaglegaze_sol_Beaglegaze.load(
                contractAddress,
                Web3j.build(new HttpService(networkAddress)),
                Credentials.create(account),
                new DefaultGasProvider());
    }
}
