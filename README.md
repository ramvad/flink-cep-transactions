# Suspicious Account Transactions Detection using Apache Flink CEP

## Features

This is a test project to demonstrate Apache Flink based CEP processing to identify patterns from a source, in this case, a csv file which simulates having transactions. Once a specific pattern is identified and matches a criteria, an alert is generated.

Future versions will include more complex sources, patterns and alerts.

Identified suspicious account alerts are logged to a separate directory/files under transaction_alerts.log caled for easy review. 

## Prerequisites

- Java 17 or higher
- Apache Maven 3.8+
- Apache Flink 1.17.1

## Installation

1. Clone the repository
```bash
git clone [repository URL]
```

2. Navigate to project directory
```bash
cd [project-name]
```

3. Build the project
```bash
mvn clean package
```

## Usage

Run the application by providing a CSV file path:
```bash
run-flink_working.bat data/transactions.csv
```

Or use the default test file:
```bash
run-flink_working.bat
```

CSV File Format:
```csv
timestamp,accountId,transactionAmount
2025-06-13 10:00:00,acct1,400.00
2025-06-13 10:15:00,acct1,350.00
2025-06-13 10:45:00,acct1,300.00
```

Example output:
```
RAW INPUT:> timestamp,accountId,transactionAmount
ALERTS:> ALERT: acct1 total $1050.0
```

## Configuration

Key configuration files:
- `src/main/resources/log4j2.properties` - Logging configuration
- `run-flink_working.bat` - Execution script
- `data/transactions.csv` - Default test data file
- `data/transactions_bulk.csv` - Bulk test data file

## Project Structure

```
project-root/
├── data/                     # Data files directory
│   ├── transactions.csv
│   └── transactions_bulk.csv
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── example/
│       └── resources/
├── target/
└── pom.xml
```

## Building

```bash
mvn clean package
```

## Testing

```bash
mvn test
```

## Command Line Arguments

The application accepts the following command-line arguments:

- `<csv-file>`: Path to the input CSV file (optional)
  - If not provided, defaults to `data/transactions.csv`
  - Example: `run-flink_working.bat data/transactions_bulk.csv`

## Troubleshooting

Common issues and their solutions:
1. File Not Found
   - Ensure the CSV file path is correct
   - Use forward slashes (/) or escaped backslashes (\\) in the path
   - Check if the file exists in the specified location

2. Invalid CSV Format
   - Verify the CSV follows the required format
   - Check for proper date formatting (YYYY-MM-DD HH:mm:ss)
   - Ensure numeric values are properly formatted


