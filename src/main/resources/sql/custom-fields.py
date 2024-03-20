num_contacts = 1000

field_names = 'id, customer_record_id, account_id, field1, field2, field3, field4, field5, field6, field7, field8, field9, field10'
field_values = '1, "cf1_value", "cf2_value", "cf3_value", "cf4_value", "cf5_value", "cf6_value", "cf7_value", "cf8_value", "cf9_value", "cf10_value"'

file_path = 'src/main/resources/sql/insert-custom-fields.sql'
with open(file_path, 'w') as sql_file:
    for i in range(1, num_contacts+1):
        values = f'{i}, {i}, {field_values}'
        sql_file.write('INSERT INTO custom_fields(' + field_names + ') VALUES(' + values + ');\n')

print(f'Generated custom fields queries to {file_path}')
