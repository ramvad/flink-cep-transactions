import random
from datetime import datetime, timedelta

def generate_transactions():
    # Keep original transactions that trigger the alert
    original_transactions = [
        "2025-06-13 10:00:00,acct1,400.00",
        "2025-06-13 10:15:00,acct1,350.00",
        "2025-06-13 10:45:00,acct1,300.00",
        "2025-06-13 12:00:00,acct2,600.00",
        "2025-06-13 12:15:00,acct2,450.00"
    ]
    
    # Generate 1000 random transactions
    start_date = datetime(2025, 6, 13, 9, 0)
    accounts = [f"acct{i}" for i in range(1, 21)]  # 20 different accounts
    transactions = ["timestamp,accountId,transactionAmount"]
    transactions.extend(original_transactions)
    
    for _ in range(1000):
        timestamp = start_date + timedelta(minutes=random.randint(0, 24*60))
        account = random.choice(accounts)
        amount = round(random.uniform(10.0, 200.0), 2)  # Small amounts between 10 and 200
        transactions.append(f"{timestamp.strftime('%Y-%m-%d %H:%M:%S')},{account},{amount:.2f}")
    
    # Sort by timestamp
    data_rows = transactions[1:]  # Exclude header
    sorted_rows = sorted(data_rows, key=lambda x: x.split(',')[0])
    transactions = [transactions[0]] + sorted_rows
    
    return transactions

# Write to file
with open('transactions.csv', 'w') as f:
    f.write('\n'.join(generate_transactions()))