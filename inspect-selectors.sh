#!/bin/bash

# Helper script to diagnose selector issues
# This shows you what elements exist on the page so you can fix selectors

echo "=========================================="
echo "Dentalkart Selector Inspector"
echo "=========================================="
echo ""
echo "This will open dentalkart.com and help you find the correct selectors."
echo ""
echo "Instructions:"
echo "1. A browser will open with dentalkart.com"
echo "2. Right-click on the search input → Select 'Inspect'"
echo "3. Look for the element's id, name, or class"
echo "4. Note down: <input id='...' OR class='...' OR name='...'>"
echo "5. Update SearchPage.java with the correct selector"
echo ""
echo "Common selectors to find:"
echo "  - Search input (where you type keywords)"
echo "  - Search button (to submit search)"
echo "  - Product cards/items"
echo "  - Product name/title element"
echo "  - Product price element"
echo "  - Product image element"
echo ""
echo "Press Enter to open the site, or Ctrl+C to cancel"
read

# Open in default browser
if [[ "$OSTYPE" == "darwin"* ]]; then
    open "https://www.dentalkart.com"
else
    xdg-open "https://www.dentalkart.com"
fi

echo ""
echo "Browser opened. Now:"
echo "1. Right-click on the search box → Inspect"
echo "2. Update the selectors in src/test/java/com/dentalkart/pages/SearchPage.java"
echo "3. Run: mvn test"
