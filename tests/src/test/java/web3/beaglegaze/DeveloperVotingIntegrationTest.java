package web3.beaglegaze;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.DefaultGasProvider;

import web3.beaglegaze.api.IntegrationTestBase;

/**
 * Integration test for the two-phase voting mechanism for developer
 * registration.
 * Tests that new developers must be voted in by existing developers with at
 * least 51% approval.
 */
public class DeveloperVotingIntegrationTest extends IntegrationTestBase {

    private Beaglegaze_sol_Beaglegaze owner;
    private String contractAddress;
    private static final String EXISTING_DEVELOPER_1 = DEVELOPER_ACCOUNT;

    @BeforeEach
    void setUp() throws Exception {
        owner = deploySmartContract();
        contractAddress = owner.getContractAddress();
    }

    private Beaglegaze_sol_Beaglegaze deploySmartContract() throws Exception {
        return Beaglegaze_sol_Beaglegaze.deploy(
                Web3j.build(new HttpService(networkAddress)),
                Credentials.create(SMART_CONTRACT_OWNER),
                new DefaultGasProvider(), BigInteger.ZERO).send();
    }

    @Test
    void shouldRegisterContractOwnerAsFirstDeveloper() throws Exception {
        assertTrue(owner.isDeveloper().send());
    }

    @Test
    void shouldRegisterSecondDeveloperWithOwnerApproval() throws Exception {
        Beaglegaze_sol_Beaglegaze requestingDeveloper = loadContract(EXISTING_DEVELOPER_1);
        requestingDeveloper.requestDeveloperRegistration().send();
        assertFalse(requestingDeveloper.isDeveloper().send(),
                "Existing developer should not be registered until voted in");

        owner.voteForDeveloper(
                "0xe8acf143AFbF8B1371A20ea934D334180190Eac1", true).send();

        assertTrue(requestingDeveloper.isDeveloper().send(),
                "Existing developer should not be registered until voted in");
    }

    @Test
    void shouldNotRegisterDeveloperWithoutOwnerApproval() throws Exception {
        Beaglegaze_sol_Beaglegaze requestingDeveloper = loadContract(EXISTING_DEVELOPER_1);
        requestingDeveloper.requestDeveloperRegistration().send();

        owner.voteForDeveloper(
                "0xe8acf143AFbF8B1371A20ea934D334180190Eac1", false).send();

        assertFalse(requestingDeveloper.isDeveloper().send(),
                "Existing developer should not be registered until voted in");
    }

    @Test
    void shouldRequireMajorityApprovalForDeveloperRegistration() throws Exception {
        Beaglegaze_sol_Beaglegaze requestingDeveloper1 = loadContract(EXISTING_DEVELOPER_1);
        requestingDeveloper1.requestDeveloperRegistration().send();
        assertFalse(requestingDeveloper1.isDeveloper().send(),
                "Existing developer should not be registered until voted in");

        owner.voteForDeveloper(
                "0xe8acf143AFbF8B1371A20ea934D334180190Eac1", true).send();

        Beaglegaze_sol_Beaglegaze requestingDeveloper2 = loadContract(CLIENT_ACCOUNT);
        requestingDeveloper2.requestDeveloperRegistration().send();
        assertFalse(requestingDeveloper2.isDeveloper().send(),
                "New developer should not be registered until voted in");
        owner.voteForDeveloper("0x88f9B82462f6C4bf4a0Fb15e5c3971559a316e7f", true).send();
        requestingDeveloper1.voteForDeveloper(
                "0x88f9B82462f6C4bf4a0Fb15e5c3971559a316e7f", true).send();
        assertTrue(requestingDeveloper2.isDeveloper().send(),
                "New developer should be registered after majority approval");
    }

    @Test
    void shouldNotBeRegisteredIfMajorityApprovalForDeveloperRegistrationFails() throws Exception {
        Beaglegaze_sol_Beaglegaze requestingDeveloper1 = loadContract(EXISTING_DEVELOPER_1);
        requestingDeveloper1.requestDeveloperRegistration().send();
        assertFalse(requestingDeveloper1.isDeveloper().send(),
                "Existing developer should not be registered until voted in");

        owner.voteForDeveloper(
                "0xe8acf143AFbF8B1371A20ea934D334180190Eac1", true).send();

        Beaglegaze_sol_Beaglegaze requestingDeveloper2 = loadContract(CLIENT_ACCOUNT);
        requestingDeveloper2.requestDeveloperRegistration().send();
        assertFalse(requestingDeveloper2.isDeveloper().send(),
                "New developer should not be registered until voted in");
        owner.voteForDeveloper("0x88f9B82462f6C4bf4a0Fb15e5c3971559a316e7f", true).send();
        assertFalse(requestingDeveloper2.isDeveloper().send(),
                "New developer should not be registered until voted in");
        requestingDeveloper1.voteForDeveloper(
                "0x88f9B82462f6C4bf4a0Fb15e5c3971559a316e7f", false).send();
        assertFalse(requestingDeveloper2.isDeveloper().send(),
                "New developer should be registered after majority approval");
    }

    private Beaglegaze_sol_Beaglegaze loadContract(String privateKey) {
        return Beaglegaze_sol_Beaglegaze.load(
                contractAddress,
                Web3j.build(new HttpService(networkAddress)),
                Credentials.create(privateKey),
                new DefaultGasProvider());
    }
}
