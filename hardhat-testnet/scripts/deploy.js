async function main() {
    console.log("Starting deployment...");

    // Get the contract factory
    const UsageContract = await ethers.getContractFactory("UsageContract");
    
    console.log("Deploying UsageContract...");
    
    // Deploy the contract
    const usageContract = await UsageContract.deploy();
    
    // Wait for the contract to be deployed
    await usageContract.waitForDeployment();
    
    const contractAddress = await usageContract.getAddress();
    
    console.log("✅ UsageContract deployed successfully!");
    console.log("📍 Contract address:", contractAddress);
    console.log("🔗 Network:", network.name);
    
    // Get deployer account
    const [deployer] = await ethers.getSigners();
    console.log("👤 Deployed by:", deployer.address);
    console.log("💰 Deployer balance:", ethers.formatEther(await ethers.provider.getBalance(deployer.address)), "ETH");
    
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