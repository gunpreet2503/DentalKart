#!/bin/bash

# Dentalkart Automation Test Runner Script

echo "=========================================="
echo "Dentalkart Search Automation Tests"
echo "=========================================="
echo ""

# Check if Maven is installed
if ! command -v mvn &> /dev/null; then
    echo "Error: Maven is not installed. Please install Maven 3.6+ first."
    exit 1
fi

# Parse arguments
TEST_TYPE=${1:-all}

case $TEST_TYPE in
    all)
        echo "Running all tests..."
        mvn clean test
        ;;
    implant)
        echo "Running implant search test..."
        mvn test -Dtest=DentalkartSearchTest#testImplantSearch
        ;;
    scaler)
        echo "Running scaler search test..."
        mvn test -Dtest=DentalkartSearchTest#testScalerSearch
        ;;
    performance)
        echo "Running performance test..."
        mvn test -Dtest=DentalkartSearchTest#testPageLoadTime
        ;;
    report)
        echo "Generating test report..."
        mvn surefire-report:report
        echo "Report generated at: target/site/surefire-report.html"
        ;;
    *)
        echo "Usage: $0 [all|implant|scaler|performance|report]"
        echo ""
        echo "Examples:"
        echo "  $0 all           - Run all tests"
        echo "  $0 implant       - Run implant search test only"
        echo "  $0 scaler        - Run scaler search test only"
        echo "  $0 performance   - Run performance test only"
        echo "  $0 report        - Generate test report"
        exit 1
        ;;
esac

echo ""
echo "=========================================="
echo "Test execution completed"
echo "=========================================="
