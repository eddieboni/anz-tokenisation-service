#!/bin/bash

# ANZ Tokenisation Service - Example API Calls
# Make sure the service is running on http://localhost:3000

echo "================================"
echo "ANZ Tokenisation Service API Examples"
echo "================================"
echo ""

# Example 1: Tokenise Account Numbers.
echo "1. Tokenising Account Numbers..."
echo "Request:"
echo '["4111-1111-1111-1111", "4444-3333-2222-1111", "4444-1111-2222-3333"]'
echo ""

TOKENISE_RESPONSE=$(curl -s -X POST http://localhost:3000/tokenise \
  -H "Content-Type: application/json" \
  -d '["4111-1111-1111-1111", "4444-3333-2222-1111", "4444-1111-2222-3333"]')

echo "Response:"
echo $TOKENISE_RESPONSE | jq '.'
echo ""

# Extract Tokens for Next Request (Requires jq).
if command -v jq &> /dev/null; then
    TOKEN1=$(echo $TOKENISE_RESPONSE | jq -r '.[0]')
    TOKEN2=$(echo $TOKENISE_RESPONSE | jq -r '.[1]')
    TOKEN3=$(echo $TOKENISE_RESPONSE | jq -r '.[2]')
    
    echo "================================"
    echo ""
    
    # Example 2: Detokenise Tokens.
    echo "2. Detokenising Tokens Back to Account Numbers..."
    echo "Request:"
    echo "[\"$TOKEN1\", \"$TOKEN2\", \"$TOKEN3\"]"
    echo ""
    
    DETOKENISE_RESPONSE=$(curl -s -X POST http://localhost:3000/detokenise \
      -H "Content-Type: application/json" \
      -d "[\"$TOKEN1\", \"$TOKEN2\", \"$TOKEN3\"]")
    
    echo "Response:"
    echo $DETOKENISE_RESPONSE | jq '.'
    echo ""
    
    echo "================================"
    echo ""
    
    # Example 3: Test Idempotency - Same Account Should Get Same Token.
    echo "3. Testing Idempotency (Same Account Number Should Get Same Token)..."
    echo "Request:"
    echo '["4111-1111-1111-1111"]'
    echo ""
    
    IDEMPOTENT_RESPONSE=$(curl -s -X POST http://localhost:3000/tokenise \
      -H "Content-Type: application/json" \
      -d '["4111-1111-1111-1111"]')
    
    echo "Response:"
    echo $IDEMPOTENT_RESPONSE | jq '.'
    
    IDEMPOTENT_TOKEN=$(echo $IDEMPOTENT_RESPONSE | jq -r '.[0]')
    echo ""
    echo "First Token:  $TOKEN1"
    echo "Second Token: $IDEMPOTENT_TOKEN"
    
    if [ "$TOKEN1" = "$IDEMPOTENT_TOKEN" ]; then
        echo "Tokens Match - Idempotency Passed!"
    else
        echo "Tokens Don't Match - Idempotency Failed!"
    fi
    
else
    echo "Note: Install 'jq' for better formatted output and automated testing."
    echo "You can still test manually by copying the tokens from the response above."
fi

echo ""
echo "================================"
echo "Testing Has Been Completed."
echo "================================"
