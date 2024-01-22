import csv
from faker import Faker

# Create a Faker instance
fake = Faker()

# Number of contacts to generate
num_contacts = 10

# Generate random contacts
contacts = []
for i in range(num_contacts):
    email = fake.email()
    contacts.append({'email': email})

# Save contacts to a CSV file
csv_file_path = 'random_contacts.csv'
fields = ['email']

with open(csv_file_path, 'w', newline='') as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=fields)

    # Write the header
    writer.writeheader()

    # Write the contacts
    writer.writerows(contacts)

print(f'Random contacts saved to {csv_file_path}')
