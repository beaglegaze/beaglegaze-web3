#!/bin/bash

# generate web3j classes from compiled contract
web3j generate solidity -b contract/beaglegaze_sol_Beaglegaze.bin -a contract/beaglegaze_sol_Beaglegaze.abi -o tests/src/main/java -p web3.beaglegaze

# run tests
cd tests || exit
mvn test