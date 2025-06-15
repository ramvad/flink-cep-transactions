# Project Name

Suspicious Account Transactions Detection using Apache Flink CEP

## Features

This is a test project to demonstrate Apache Flink based CEP processing to identify patterns from a source, in this case , a csv file which simulates having trasactions. Once a specific pattern is identified and matches a criteria, an alert is generated.

Future versions will include more complex sources, patterns and alerts.

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

Run the application using:
```bash
./run-flink_working.bat
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

## Project Structure

```
project-root/
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

## Troubleshooting

Common issues and their solutions:
1. Problem: [Description]
   - Solution: [Steps to resolve]


