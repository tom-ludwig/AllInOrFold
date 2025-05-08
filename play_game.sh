#!/bin/bash

set -e  # Exit on error

# Clean up any existing state
rm -f poker_state.json

# Build the project first
mvn clean package -DskipTests

# Find the JAR file
JAR_PATH=$(find target -name "project-*.jar" ! -name "*original*" -type f)
if [ -z "$JAR_PATH" ]; then
    echo "Error: Could not find JAR file"
    exit 1
fi

# Function to run CLI commands
run_cmd() {
    if ! java -jar "$JAR_PATH" "$@"; then
        echo "Command failed: $*"
        exit 1
    fi
    # sleep 1  # Add small delay between commands
}

echo "Starting new poker game..."
run_cmd --new-game

echo "Adding players..."
run_cmd --add-player "Alice" 1000
run_cmd --add-player "Bob" 1000
run_cmd --add-player "Charlie" 1000

echo "Setting up game parameters..."
run_cmd --set-starting-money 1000
run_cmd --set-small-blind 10
run_cmd --set-big-blind 20

echo "Starting the game..."
run_cmd --start-game

echo "Dealing first hand..."
run_cmd --deal-new-hand
run_cmd --show-dealer
run_cmd --show-pot
run_cmd --show-current-bet

echo "First round of betting..."
run_cmd --call  # First player calls
run_cmd --raise 40  # Second player raises
run_cmd --call  # Third player calls
run_cmd --call  # First player calls

echo "Showing game state..."
run_cmd --show-community-cards
run_cmd --show-pot
run_cmd --show-current-bet

echo "Second round of betting..."
run_cmd --call  # First player calls
run_cmd --raise 80  # Second player raises
run_cmd --call  # Third player calls
run_cmd --call  # First player calls

echo "Final game state..."
run_cmd --show-community-cards
run_cmd --show-pot
run_cmd --show-current-bet

echo "Game complete!"