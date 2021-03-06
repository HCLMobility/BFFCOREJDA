--Drop and create all audit and revision info tables along with audit sequence
DROP TABLE data_aud
GO
DROP TABLE events_aud
GO
DROP TABLE flow_aud
GO
DROP TABLE flow_permission_aud
GO
DROP TABLE form_aud
GO
DROP TABLE form_custom_component_aud
GO
DROP TABLE form_dependency_aud
GO
DROP TABLE key_code_master_aud
GO
DROP TABLE product_config_aud
GO
DROP TABLE published_form_dependency_aud
GO
DROP TABLE field_aud
GO
DROP TABLE field_values_aud
GO
DROP TABLE tabs_aud
GO
DROP TABLE revision_info
GO
DROP SEQUENCE rev_info_seq
GO

--Add 'scheme_list' column to 'api_registry' table if it doesn't exist
IF COL_LENGTH('api_registry', 'scheme_list') IS NULL
	ALTER TABLE api_registry ADD scheme_list nvarchar(255) NULL
ELSE
	ALTER TABLE api_registry ALTER COLUMN scheme_list nvarchar(255) NULL
GO

--Update scheme_list column value from api_master to api_registry correspondingly
IF COL_LENGTH('api_master', 'scheme_list') IS NOT NULL
	UPDATE ar SET ar.scheme_list = am.scheme_list 
	FROM api_registry as ar INNER JOIN api_master as am 
	ON ar.uid = am.registry_id
	WHERE am.scheme_list IS NOT NULL
GO

--Drop column scheme_list from api_master table
IF COL_LENGTH('api_master', 'scheme_list') IS NOT NULL
	ALTER TABLE api_master DROP COLUMN scheme_list
GO 
ALTER TABLE field ALTER COLUMN validate_pattern nvarchar(max)
GO
ALTER TABLE custom_field ALTER COLUMN validate_pattern nvarchar(max)
GO

DELETE FROM key_code_master
GO

IF COL_LENGTH('key_code_master', 'created_by') IS NULL
	ALTER TABLE key_code_master ADD created_by nvarchar(255) NULL
GO
IF COL_LENGTH('key_code_master', 'creation_date') IS NULL
	ALTER TABLE key_code_master ADD creation_date datetime2(7) NULL
GO
IF COL_LENGTH('key_code_master', 'last_modified_by') IS NULL
	ALTER TABLE key_code_master ADD last_modified_by nvarchar(255) NULL
GO
IF COL_LENGTH('key_code_master', 'last_modified_date') IS NULL
	ALTER TABLE key_code_master ADD last_modified_date datetime2(7) NULL
GO
IF COL_LENGTH('key_code_master', 'sequence') IS NULL
	ALTER TABLE key_code_master ADD sequence int NULL
GO

INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F1','KEYCODE_F1','','131','0','0','0','0','GLOBAL',1, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Funtion_F1','ALT + KEYCODE_F1','','131','0','0','1','0','GLOBAL',2, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_Funtion_F1','CTRL + KEYCODE_F1','','131','1','0','0','0','GLOBAL',3, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shift_Funtion_F1','SHFT + KEYCODE_F1','','131','0','1','0','0','GLOBAL',4, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F2','KEYCODE_F2','','132','0','0','0','0','GLOBAL',5, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Funtion_F2','ALT + KEYCODE_F2','','132','0','0','1','0','GLOBAL',6, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_Funtion_F2','CTRL + KEYCODE_F2','','132','1','0','0','0','GLOBAL',7, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shift_Funtion_F2','SHFT + KEYCODE_F2','','132','0','1','0','0','GLOBAL',8, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F3','KEYCODE_F3','','133','0','0','0','0','GLOBAL',9, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Funtion_F3','ALT + KEYCODE_F3','','133','0','0','1','0','GLOBAL',10, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_Funtion_F3','CTRL + KEYCODE_F3','','133','1','0','0','0','GLOBAL',11, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shift_Funtion_F3','SHFT + KEYCODE_F3','','133','0','1','0','0','GLOBAL',12, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F4','KEYCODE_F4','','134','0','0','0','0','GLOBAL',13, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Funtion_F4','ALT + KEYCODE_F4','','134','0','0','1','0','GLOBAL',14, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_Funtion_F4','CTRL + KEYCODE_F4','','134','1','0','0','0','GLOBAL',15, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shift_Funtion_F4','SHFT + KEYCODE_F4','','134','0','1','0','0','GLOBAL',16, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F5','KEYCODE_F5','','135','0','0','0','0','GLOBAL',17, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Funtion_F5','ALT + KEYCODE_F5','','135','0','0','1','0','GLOBAL',18, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_Funtion_F5','CTRL + KEYCODE_F5','','135','1','0','0','0','GLOBAL',19, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shift_Funtion_F5','SHFT + KEYCODE_F5','','135','0','1','0','0','GLOBAL',20, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F6','KEYCODE_F6','','136','0','0','0','0','GLOBAL',21, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F7','KEYCODE_F7','','137','0','0','0','0','GLOBAL',22, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F8','KEYCODE_F8','','138','0','0','0','0','GLOBAL',23, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F9','KEYCODE_F9','','139','0','0','0','0','GLOBAL',24, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F10','KEYCODE_F10','','140','0','0','0','0','GLOBAL',25, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F11','KEYCODE_F11','','141','0','0','0','0','GLOBAL',26, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Funtion_F12','KEYCODE_F12','','142','0','0','0','0','GLOBAL',27, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'V','V','','50','0','0','0','0','GLOBAL',28, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_V','ALT+V','','50','0','0','1','0','GLOBAL',29, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_V','SHFT+V','','50','0','1','0','0','GLOBAL',30, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_V','CTRL+V','','50','1','0','0','0','GLOBAL',31, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'W','W','','51','0','0','0','0','GLOBAL',32, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_W','ALT+W','','51','0','0','1','0','GLOBAL',33, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_W','CTRL+W','','51','1','0','0','0','GLOBAL',34, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'X','X','','52','0','0','0','0','GLOBAL',35, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_X','ALT+X','','52','0','0','1','0','GLOBAL',36, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_X','SHFT+X','','52','0','1','0','0','GLOBAL',37, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_X','CTRL+X','','52','1','0','0','0','GLOBAL',38, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Y','Y','','53','0','0','0','0','GLOBAL',39, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Y','ALT+Y','','53','0','0','1','0','GLOBAL',40, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_Y','SHFT+Y','','53','0','1','0','0','GLOBAL',41, 'SUPER', GETDATE())
GO
 

-- Script for Hot Key Context

INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'A','A','','29','0','0','0','0','CONTEXT',1, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_A','ALT+A','','29','0','0','1','0','CONTEXT',2, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_A','SHFT+A','','29','0','1','0','0','CONTEXT',3, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_A','CTRL+A','','29','1','0','0','0','CONTEXT',4, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'B','B','','30','0','0','0','0','CONTEXT',5, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_B','ALT+B','','30','0','0','1','0','CONTEXT',6, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_B','SHFT+B','','30','0','1','0','0','CONTEXT',7, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_B','CTRL+B','','30','1','0','0','0','CONTEXT',8, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'C','C','','31','0','0','0','0','CONTEXT',9, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_C','ALT+C','','31','0','0','1','0','CONTEXT',10, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_C','SHFT+C','','31','0','1','0','0','CONTEXT',11, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_C','CTRL+C','','31','1','0','0','0','CONTEXT',12, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'D','D','','32','0','0','0','0','CONTEXT',13, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_D','ALT+D','','32','0','0','1','0','CONTEXT',14, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_D','SHFT+D','','32','0','1','0','0','CONTEXT',15, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_D','CTRL+D','','32','1','0','0','0','CONTEXT',16, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_E','ALT+E','','33','0','0','1','0','CONTEXT',17, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_E','SHFT+E','','33','0','1','0','0','CONTEXT',18, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_E','CTRL+E','','33','1','0','0','0','CONTEXT',19, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Meta_E','META+E','','33','0','0','0','1','CONTEXT',20, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'F','F','','34','0','0','0','0','CONTEXT',21, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_F','ALT+F','','34','0','0','1','0','CONTEXT',22, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_F','SHFT+F','','34','0','1','0','0','CONTEXT',23, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_F','CTRL+F','','34','1','0','0','0','CONTEXT',24, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'G','G','','35','0','0','0','0','CONTEXT',25, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_G','ALT+G','','35','0','0','1','0','CONTEXT',26, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_G','SHFT+G','','35','0','1','0','0','CONTEXT',27, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_G','CTRL+G','','35','1','0','0','0','CONTEXT',28, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'H','H','','36','0','0','0','0','CONTEXT',29, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_H','ALT+H','','36','0','0','1','0','CONTEXT',30, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_H','SHFT+H','','36','0','1','0','0','CONTEXT',31, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_H','CTRL+H','','36','1','0','0','0','CONTEXT',32, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'I','I','','37','0','0','0','0','CONTEXT',33, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_I','ALT+I','','37','0','0','1','0','CONTEXT',34, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_I','SHFT+I','','37','0','1','0','0','CONTEXT',35, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_I','CTRL+I','','37','1','0','0','0','CONTEXT',36, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'J','J','','38','0','0','0','0','CONTEXT',37, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_J','ALT+J','','38','0','0','1','0','CONTEXT',38, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_J','SHFT+J','','38','0','1','0','0','CONTEXT',39, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_J','CTRL+J','','38','1','0','0','0','CONTEXT',40, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'K','K','','39','0','0','0','0','CONTEXT',41, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_K','ALT+K','','39','0','0','1','0','CONTEXT',42, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_K','SHFT+K','','39','0','1','0','0','CONTEXT',43, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_K','CTRL+K','','39','1','0','0','0','CONTEXT',44, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_L','ALT+L','','40','0','0','1','0','CONTEXT',45, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_L','SHFT+L','','40','0','1','0','0','CONTEXT',46, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_L','CTRL+L','','40','1','0','0','0','CONTEXT',47, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Meta_L','META+L','','40','0','0','0','1','CONTEXT',48, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'M','M','','41','0','0','0','0','CONTEXT',49, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_M','ALT+M','','41','0','0','1','0','CONTEXT',50, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_M','SHFT+M','','41','0','1','0','0','CONTEXT',51, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_M','CTRL+M','','41','1','0','0','0','CONTEXT',52, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'N','N','','42','0','0','0','0','CONTEXT',53, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_N','ALT+N','','42','0','0','1','0','CONTEXT',54, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_N','SHFT+N','','42','0','1','0','0','CONTEXT',55, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_N','CTRL+N','','42','1','0','0','0','CONTEXT',56, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'O','O','','43','0','0','0','0','CONTEXT',57, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_O','ALT+O','','43','0','0','1','0','CONTEXT',58, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_O','SHFT+O','','43','0','1','0','0','CONTEXT',59, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_O','CTRL+O','','43','1','0','0','0','CONTEXT',60, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'P','P','','44','0','0','0','0','CONTEXT',61, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_P','ALT+P','','44','0','0','1','0','CONTEXT',62, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_P','SHFT+P','','44','0','1','0','0','CONTEXT',63, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_P','CTRL+P','','44','1','0','0','0','CONTEXT',64, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Q','Q','','45','0','0','0','0','CONTEXT',65, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Q','ALT+Q','','45','0','0','1','0','CONTEXT',66, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_Q','SHFT+Q','','45','0','1','0','0','CONTEXT',67, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_Q','CTRL+Q','','45','1','0','0','0','CONTEXT',68, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_R','ALT+R','','46','0','0','1','0','CONTEXT',69, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_R','SHFT+R','','46','0','1','0','0','CONTEXT',70, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_R','CTRL+R','','46','1','0','0','0','CONTEXT',71, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl+Shft+R','CTRL+SHFT+R','','46','1','1','0','0','CONTEXT',72, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'S','S','','47','0','0','0','0','CONTEXT',73, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_S','ALT+S','','47','0','0','1','0','CONTEXT',74, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_S','SHFT+S','','47','0','1','0','0','CONTEXT',75, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_S','CTRL+S','','47','1','0','0','0','CONTEXT',76, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'T','T','','48','0','0','0','0','CONTEXT',77, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_T','ALT+T','','48','0','0','1','0','CONTEXT',78, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_T','SHFT+T','','48','0','1','0','0','CONTEXT',79, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_T','CTRL+T','','48','1','0','0','0','CONTEXT',80, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'U','U','','49','0','0','0','0','CONTEXT',81, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_U','ALT+U','','49','0','0','1','0','CONTEXT',82, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_U','SHFT+U','','49','0','1','0','0','CONTEXT',83, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Ctrl_U','CTRL+U','','49','1','0','0','0','CONTEXT',84, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Z','Z','','54','0','0','0','0','CONTEXT',85, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Z','ALT+Z','','54','0','0','1','0','CONTEXT',86, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Shft_Z','SHFT+Z','','54','0','1','0','0','CONTEXT',87, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'Alt_Ctrl_Z','ALT+CTRL+Z','','54','1','0','1','0','CONTEXT',88, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'VolumeUp','VolumeUp','','24','0','0','0','0','CONTEXT',89, 'SUPER', GETDATE())
GO
INSERT INTO key_code_master (uid ,key_name ,key_display_name,key_description,code, is_ctrl ,is_shift,is_alt,is_metakey ,type,sequence, CREATED_BY, CREATION_DATE) VALUES (NEWID() ,'VolumeDown','VolumeDown','','25','0','0','0','0','CONTEXT',90, 'SUPER', GETDATE())
GO