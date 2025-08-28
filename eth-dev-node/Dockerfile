# Use Node.js as the base image
FROM node:18

# Set the working directory
WORKDIR /app

# Install Hardhat globally
RUN npm install -g hardhat

# Copy package files and install dependencies
COPY package.json ./
RUN npm install

# Copy the entire Hardhat project (including hardhat.config.js)
COPY . .

# Compile the smart contracts
RUN npx hardhat compile

# Expose Hardhat's default network port
EXPOSE 8545

# Create a deployment script that starts the network and deploys contracts
RUN echo '#!/bin/bash\n\
npx hardhat node &\n\
HARDHAT_PID=$!\n\
echo "Waiting for Hardhat network to start..."\n\
sleep 5\n\
echo "Deploying contracts..."\n\
npx hardhat run scripts/deploy.js --network localhost\n\
wait $HARDHAT_PID' > /app/start-and-deploy.sh

RUN chmod +x /app/start-and-deploy.sh

# Start the Hardhat network and deploy contracts
CMD ["/app/start-and-deploy.sh"]