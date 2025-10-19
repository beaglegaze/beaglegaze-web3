#!/bin/bash
# copy contract files to eth-dev-node directory
cp -r contract/* eth-dev-node/contracts/

# build hardhat-testnet docker image
docker build -t hardhat-testnet eth-dev-node/.

