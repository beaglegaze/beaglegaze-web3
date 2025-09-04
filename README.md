# 💰 beaglegaze Web3 - Smart Contracts & DevContainer for the Multi-Language Web3 Fee Collection Library

> Transform your software into a monetized service with blockchain-powered micro-payments.

[![License: GPL v3](https://img.shields.io/badge/License-LGPLv3-blue.svg)](LICENSE)

**beaglegaze** enables developers to seamlessly integrate fees collection into their libraries and services using blockchain technology. Users pre-fund a smart contract to access your code, and each function call triggers automatic micro-payments to registered contributors.

## 📦 Installation

### Prerequisites

```bash
npm install --global solc
```

### Local Development Setup

1. **Start Ethereum testnet**:
   ```bash
   docker buildx build -t hardhat-testnet .
   docker run -d -p 8545:8545 --rm --name hardhat-testnet hardhat-testnet
   ```

2. **Note the contract address** from the deployment logs:
   ```
   🎉 Deployment completed successfully!
   Contract address: 0x289B72CEeaB48832261626D62E3daA87Fd90B024
   ```

## 📄 License

This project is licensed under the LGPL v3 License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">
  <p>Built with ❤️ for the Web3 community</p>
  <p>
    <a href="#top">Back to Top</a> •
    <a href="https://github.com/steffenboe/beaglegaze/issues">Report Bug</a> •
    <a href="https://github.com/steffenboe/beaglegaze/issues">Request Feature</a>
  </p>
</div>