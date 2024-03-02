import csv, random, string

# Number of contacts to generate
num_contacts = 1000

default_fields = ['firstName', 'lastName', 'email', 'companyName', 'address', 'city', 'country', 'state', 'zipcode', 'phoneNumber']
custom_fields = [('cf'+str(i)) for i in range(1, 11)]

# Generate random contacts
contacts = []
for i in range(num_contacts):
    default_field_dict = {
        'firstName': 'James',
        'lastName': 'Butt',
        'email': f'jbutt{i}@email.com',
        'companyName': 'Benton, John B Jr',
        'address': '6649 N Blue Gum St',
        'city': 'New Orleans',
        'country': 'Orleans',
        'state': 'LA',
        'zipcode': '70116',
        'phoneNumber': '504-845-1427'
    }

    custom_field_dict = {}
    for cf in custom_fields:
        custom_field_dict[cf] = cf+"_value"

    contacts.append({**default_field_dict, **custom_field_dict})

# Save contacts to a CSV file
csv_file_path = 'src/main/resources/sample/contacts/random_contacts.csv'
fields = default_fields + custom_fields

with open(csv_file_path, 'w', newline='') as csvfile:
    writer = csv.DictWriter(csvfile, fieldnames=fields)
    writer.writeheader()
    writer.writerows(contacts)

print(f'Random contacts saved to {csv_file_path}')
