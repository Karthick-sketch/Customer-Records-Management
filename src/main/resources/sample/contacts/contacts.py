import csv
from faker import Faker

# Create a Faker instance
fake = Faker()

# Number of contacts to generate
num_contacts = 5000

# Generate random contacts
contacts = []
for i in range(num_contacts):
    first_name = fake.first_name()
    last_name = fake.last_name()
    email = "{}.{}@email.com".format(first_name, last_name).lower()
    personal_email = email
    age = fake.random_int(21, 30)
    contacts.append({'firstName': first_name, 'lastName': last_name, 'email': email, 'personalEmail': personal_email, 'age': age})

# Save contacts to a CSV file
csv_file_path = 'src/main/resources/sample/contacts/random_contacts.csv'
fields = ['firstName', 'lastName', 'email', 'personalEmail', 'age']

with open(csv_file_path, 'w', newline='') as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=fields)
    # Write the header
    writer.writeheader()
    # Write the contacts
    writer.writerows(contacts)

print(f'Random contacts saved to {csv_file_path}')
