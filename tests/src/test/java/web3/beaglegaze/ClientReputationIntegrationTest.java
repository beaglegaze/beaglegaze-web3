package web3.beaglegaze;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.greaterThan;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import web3.beaglegaze.api.IntegrationTestBase;

/**
 * Integration test for client reputation functionality.
 * Tests that client reputation increases when payments are received.
 */
public class ClientReputationIntegrationTest extends IntegrationTestBase {

    private Beaglegaze_sol_Beaglegaze contract;
    private String contractAddress;

    @BeforeEach
    void setUp() throws Exception {
        contract = deploySmartContract();
        contractAddress = contract.getContractAddress();
        BigInteger fundingAmount = BigInteger.valueOf(1000);
        fundClient(fundingAmount, contractAddress);
    }

    private Beaglegaze_sol_Beaglegaze deploySmartContract() throws Exception {
        return Beaglegaze_sol_Beaglegaze.deploy(
                Web3j.build(new HttpService(networkAddress)),
                Credentials.create(SMART_CONTRACT_OWNER),
                new DefaultGasProvider(), BigInteger.ZERO).send();
    }

    @Test
    void shouldIncreaseClientReputationWhenReceivingFunds() throws Exception {
        Beaglegaze_sol_Beaglegaze clientContract = loadClientContract();
        
        BigInteger initialReputation = clientContract.getClientReputation().send();
        assertThat(initialReputation, is(BigInteger.ZERO));

        clientContract.consume(BigInteger.TEN).send();
        
        BigInteger reputationAfterFunding = clientContract.getClientReputation().send();
        assertThat(reputationAfterFunding, is(greaterThan(initialReputation)));
    }

    @Test
    void shouldIncreaseClientReputationWhenPurchasingSubscription() throws Exception {
        Beaglegaze_sol_Beaglegaze clientContract = loadClientContract();
        
        BigInteger initialReputation = clientContract.getClientReputation().send();
        
        BigInteger subscriptionPrice = BigInteger.valueOf(10);
        clientContract.consume(subscriptionPrice).send();
        
        BigInteger reputationAfterSubscription = clientContract.getClientReputation().send();
        assertThat(reputationAfterSubscription, is(greaterThan(initialReputation)));
    }

    @Test
    void shouldAccumulateClientReputationWithMultiplePayments() throws Exception {
        Beaglegaze_sol_Beaglegaze clientContract = loadClientContract();
        
        BigInteger initialReputation = clientContract.getClientReputation().send();
        
        // First payment
        clientContract.consume(BigInteger.TEN).send();
        BigInteger reputationAfterFirst = clientContract.getClientReputation().send();
        
        // Second payment  
        BigInteger secondPayment = BigInteger.valueOf(200);
        clientContract.consume(secondPayment).send();
        BigInteger reputationAfterSecond = clientContract.getClientReputation().send();
        
        assertThat(reputationAfterFirst, is(greaterThan(initialReputation)));
        assertThat(reputationAfterSecond, is(greaterThan(reputationAfterFirst)));
    }

    private Beaglegaze_sol_Beaglegaze loadClientContract() {
        return Beaglegaze_sol_Beaglegaze.load(
                contractAddress,
                Web3j.build(new HttpService(networkAddress)),
                Credentials.create(CLIENT_ACCOUNT),
                new DefaultGasProvider());
    }
}