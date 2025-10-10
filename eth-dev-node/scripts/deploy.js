async function main() {
    console.log("Starting deployment...");

    // Choose a prefunded account from hardhat.config.js or via env var
    const PREFUNDED_KEYS = [
        "0xaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"
    ];
    const PRIVATE_KEY = process.env.DEPLOYER_PRIVATE_KEY || PREFUNDED_KEYS[0];

    if (!PRIVATE_KEY || PRIVATE_KEY.length !== 66 || !PRIVATE_KEY.startsWith("0x")) {
        throw new Error("Invalid or missing deployer private key. Set DEPLOYER_PRIVATE_KEY or update PREFUNDED_KEYS.");
    }

    const wallet = new ethers.Wallet(PRIVATE_KEY, ethers.provider);
    console.log("👤 Using deployer:", wallet.address);
    console.log("🔗 Network:", network.name);
    const balance = await ethers.provider.getBalance(wallet.address);
    console.log("💰 Deployer balance:", ethers.formatEther(balance), "ETH");

    // Get the contract factory connected to the chosen signer
    const UsageContract = await ethers.getContractFactory("Beaglegaze", wallet);
    console.log("Deploying beaglegaze contract...");

    // Set the subscription price in wei (example: 0.01 ETH)
    const subscriptionPriceInWei = ethers.parseEther("0.01");

    // Deploy the contract with constructor argument
    const usageContract = await UsageContract.deploy(subscriptionPriceInWei);

    // Wait for the contract to be deployed
    await usageContract.waitForDeployment();

    const contractAddress = await usageContract.getAddress();

    console.log("✅ UsageContract deployed successfully!");
    console.log("📍 Contract address:", contractAddress);

    return contractAddress;
}

main()
    .then((address) => {
        console.log("🎉 Deployment completed successfully!");
        console.log("Contract address:", address);
        process.exit(0);
    })
    .catch((error) => {
        console.error("❌ Deployment failed:");
        console.error(error);
        process.exit(1);
    });