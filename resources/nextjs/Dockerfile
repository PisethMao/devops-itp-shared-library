# Use the official Node.js image from Docker Hub
FROM node:18

# Set the working directory in the container
WORKDIR /app

# Copy package.json and package-lock.json (if they exist)
COPY package*.json ./

# Install project dependencies
RUN npm install

# Copy the rest of the project files into the container
COPY . .

# Expose the port that your app will run on
EXPOSE 3000

# Start the application
CMD ["npm", "start"]
