--Validate min and max alter scripts for 'field' and 'field_aud' tables
ALTER TABLE field ALTER COLUMN validate_min DECIMAL(38,19)
ALTER TABLE field ALTER COLUMN validate_max DECIMAL(38,19)
ALTER TABLE field ALTER COLUMN validate_min_length DECIMAL(38,19)
ALTER TABLE field ALTER COLUMN validate_max_length DECIMAL(38,19)

ALTER TABLE field_aud ALTER COLUMN validate_min DECIMAL(38,19)
ALTER TABLE field_aud ALTER COLUMN validate_max DECIMAL(38,19)
ALTER TABLE field_aud ALTER COLUMN validate_min_length DECIMAL(38,19)
ALTER TABLE field_aud ALTER COLUMN validate_max_length DECIMAL(38,19)

--Validate min and max alter scripts for 'custom_field' table
ALTER TABLE custom_field ALTER COLUMN validate_min DECIMAL(38,19)
ALTER TABLE custom_field ALTER COLUMN validate_max DECIMAL(38,19)
ALTER TABLE custom_field ALTER COLUMN validate_min_length DECIMAL(38,19)
ALTER TABLE custom_field ALTER COLUMN validate_max_length DECIMAL(38,19)

--Validate pattern alter scripts for 'field', 'field_aud' and 'custom_field' tables
ALTER TABLE field ALTER COLUMN validate_pattern NVARCHAR(4000)
ALTER TABLE custom_field ALTER COLUMN validate_pattern NVARCHAR(4000)
ALTER TABLE field_aud ALTER COLUMN validate_pattern NVARCHAR(4000)