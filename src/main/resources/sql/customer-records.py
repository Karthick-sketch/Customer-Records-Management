num_contacts = 1000

field_names = 'id, email, account_id, first_name, last_name, company_name, address, city, country, state, zipcode, phone_number'
field_values = '1, "James", "Butt", "Benton, John B Jr", "6649 N Blue Gum St", "New Orleans", "Orleans", "LA", "70116", "504-845-1427"'

file_path = 'src/main/resources/sql/insert-customer-records.sql'
with open(file_path, 'w') as sql_file:
    values = []
    for i in range(1, num_contacts+1):
        email = f'jbutt{i}@email.com'
        values = f'{i}, "{email}", {field_values}'
        sql_file.write('INSERT INTO customer_records(' + field_names + ') VALUES(' + values + ');\n')

print(f'Generated custom fields queries to {file_path}')
