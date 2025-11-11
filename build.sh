#!/bin/bash
# copy contract files to eth-dev-node directory
# Accept context as first argument, default to 'contract' if not provided
CONTEXT=${1}
cp -r $CONTEXT/contract/* $CONTEXT/eth-dev-node/contracts/

# build hardhat-testnet docker image
docker build -t hardhat-testnet $CONTEXT/eth-dev-node/.

