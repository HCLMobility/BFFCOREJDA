-- IMPORTANT INFORMATION ABOUT LITERAL GUIDs IN T-SQL SCRIPTS
-- Unique identifiers stored in binary(16) columns in SQLServer have
-- some of their bytes stored in the opposite order than Java expects.
-- Example: This literal GUID '0ab0e1a0-7418-4fdf-9eff-10012e575148'
-- will be stored as this binary value: 0xA0E1B00A1874DF4F9EFF10012E575148
-- When hibernate parses these bytes it creates a UUID value of
-- a0e1b00a-1874-df4f-9eff-10012e575148. Notice the byte order.
-- That means if you want to use any literal GUIDs in this file in
-- migration data that is parsed directly by Java code, you can't.
-- Follow this pattern to figure out the literal value to use:
-- 1st segment: [ab][cd][ef][gh] -> [gh][ef][cd][ab]
-- 2nd segment: [ab][cd] -> [cd][ab]
-- 3rd segment: [ab][cd] -> [cd][ab]
-- 4th segment: [ab][cd] -> [ab][cd]
-- 5th segment: [ab][cd][ef][gh][ij] -> [ab][cd][ef][gh][ij]

CREATE TABLE #UIDS
(
    NAME VARCHAR(50) PRIMARY KEY,
    ID   UNIQUEIDENTIFIER
)
GO

INSERT INTO #UIDS (NAME, ID)
VALUES ('wms_product', '0ab0e1a0-7418-4fdf-9eff-10012e575148'), -- a0e1b00a-1874-df4f-9eff-10012e575148
       ('wms_product_data_source', '962a4e3c-0716-42d3-a004-52fb9ff86823'), -- 3c4e2a96-1607-d342-a004-52fb9ff86823
       ('wms_product_properties', 'b32db04f-3ae8-414b-9a52-5f42b156dd94'), -- 4fb02db3-e83a-4b41-9052-5f42b156dd94

       ('pd_role', '90793cab-8d1a-469e-9cc8-cd4cbd4a042c'), -- ab3c7990-1a8d-9e46-9cc8-cd4cbd4a042c
       ('ds_role', '763884ec-0e9f-40a6-8212-dcfce2392df6'),
       ('3p_role', 'a4a15041-c2ef-4bbd-85a5-946d73f480c7'),
       ('customer_role', '69c0a2fc-eb06-4866-9f3e-41577d0da481'),

       ('wms_pd_product_config', 'f1f7d384-38ca-442f-b85b-d543b76c0609'), -- 84d3f7f1-ca38-2f44-b85b-d543b76c0609
       ('wms_ds_product_config', '27d18f3e-d785-4ae7-9d32-23ebaec72cc4'),
       ('wms_3p_product_config', '43593ce0-402a-4964-a2f6-ff32394db028'),
       ('wms_customer_product_config', '8b020e05-58d5-41af-ba83-3cee45ed42b6')
GO

DECLARE @wms_product_id UNIQUEIDENTIFIER = (SELECT ID
                                            FROM #UIDS
                                            WHERE NAME = 'wms_product')

--Product base tables
INSERT INTO PRODUCT_MASTER (UID, NAME, CONFIG_PROPERTIES, SCHEME, CONTEXT_PATH, PORT)
VALUES (@wms_product_id, 'WMS', 'TEST', 'http', 'localhost', '4500')

INSERT INTO PRODUCT_DATA_SOURCE_MASTER (UID, NAME, BASE_PATH, CONTEXT_PATH, PORT, PRODUCT_ID)
VALUES ((SELECT ID FROM #UIDS WHERE NAME = 'wms_product_data_source'), 'test_name', '10.0.0.0', '/test', 8080,
        @wms_product_id)

INSERT INTO PRODUCT_PROPERTIES (UID, IS_PRIMARY_REF, IS_SECONDARY_REF, NAME, PROP_VALUE, PRODUCT_ID)
VALUES ((SELECT ID FROM #UIDS WHERE NAME = 'wms_product_properties'), 0, 1, 'WMS', '___', @wms_product_id),
       ('52dc4750-91a3-4943-8705-5b187a440bd6', 0, 1, 'WMS', NULL, @wms_product_id) -- 0547dc52-a391-4349-8705-5b187a440bd6
GO

--MASTER LIST OF ROLES
INSERT INTO ROLE_MASTER (UID, LEVEL, NAME)
VALUES ((SELECT ID FROM #UIDS WHERE NAME = 'pd_role'), 0, 'JDA Product Development'),
       ((SELECT ID FROM #UIDS WHERE NAME = 'ds_role'), 1, 'JDA Services'),
       ((SELECT ID FROM #UIDS WHERE NAME = '3p_role'), 2, 'Third Party Implementor'),
       ((SELECT ID FROM #UIDS WHERE NAME = 'customer_role'), 3, 'Customer')
GO

--Product config
DECLARE @wms_product_properties_id UNIQUEIDENTIFIER = (SELECT ID
                                                       FROM #UIDS
                                                       WHERE NAME = 'wms_product_properties')

INSERT INTO PRODUCT_CONFIG (UID, SECONDARY_REF_ID, VERSION_ID, ROLE_ID)
VALUES ((SELECT id FROM #uids where name = 'wms_pd_product_config'),
        @wms_product_properties_id, '0',
        (SELECT ID FROM #UIDS WHERE NAME = 'pd_role')),

       ((SELECT id FROM #uids where name = 'wms_ds_product_config'),
        @wms_product_properties_id, '0',
        (SELECT ID FROM #UIDS WHERE NAME = 'ds_role')),

       ((SELECT id FROM #uids where name = 'wms_3p_product_config'),
        @wms_product_properties_id, '0',
        (SELECT ID FROM #UIDS WHERE NAME = '3p_role')),

       ((SELECT id FROM #uids where name = 'wms_customer_product_config'),
        @wms_product_properties_id, '0',
        (SELECT ID FROM #UIDS WHERE NAME = 'customer_role'))
GO

--Master user
INSERT INTO MASTER_USER
    (uid, user_id)
VALUES (NEWID(), 'SUPER')
GO

--Privilege master table

DECLARE @create_formflow UNIQUEIDENTIFIER = 'a78ecf61-cd77-4830-a5cd-658083f931fb'
DECLARE @read_formflow UNIQUEIDENTIFIER = 'b9a81569-2d88-4cfb-825f-3e2879e8696c'
DECLARE @update_formflow UNIQUEIDENTIFIER = 'ed44917b-568d-4590-8a2b-d5eed59d6a0c'
DECLARE @delete_formflow UNIQUEIDENTIFIER = '7d72c700-ebeb-4bcb-9851-f7c9c77fdec9'
DECLARE @publish_formflow UNIQUEIDENTIFIER = 'ac916c4e-544f-4ce1-9391-9c1485ea730f'
DECLARE @create_form UNIQUEIDENTIFIER = 'bd57b56b-6d76-45a5-ae25-be169b41e6a9'
DECLARE @read_form UNIQUEIDENTIFIER = '48e5d92d-4085-4c4a-8bbb-1cd69130cf1d'
DECLARE @update_form UNIQUEIDENTIFIER = '6dd32657-ccfb-4f6a-a409-818fe7ac0e64'
DECLARE @delete_form UNIQUEIDENTIFIER = '50023b26-98aa-420c-be40-ba59d5a80175'
DECLARE @publish_form UNIQUEIDENTIFIER = '77864f09-15dd-445b-bc3e-4b2c28bffbe8'
DECLARE @create_menu UNIQUEIDENTIFIER = '2e273e41-1500-412a-8ccf-726d0352989b'
DECLARE @read_menu UNIQUEIDENTIFIER = '05536359-a224-47d8-b3fa-bcbf4bc629f4'
DECLARE @update_menu UNIQUEIDENTIFIER = 'ac6f497a-0eab-4d5c-b27d-9d5a1f2d41f5'
DECLARE @delete_menu UNIQUEIDENTIFIER = '58815b9e-92a0-4276-8109-a571006241a1'
DECLARE @create_cc UNIQUEIDENTIFIER = 'f9b7f17c-5532-4238-b050-c1ab5781d945'
DECLARE @read_cc UNIQUEIDENTIFIER = '275c55e7-49df-4545-990a-5989d2d26872'
DECLARE @update_cc UNIQUEIDENTIFIER = '92c18c14-6733-45aa-91f6-977ba03d8b8f'
DECLARE @delete_cc UNIQUEIDENTIFIER = '8ceb8162-dc72-435d-9c07-977236bea6d7'
DECLARE @extend UNIQUEIDENTIFIER = '8ee09061-4e50-4c8c-b448-923cea8636bb'

INSERT INTO PRIVILEGE_MASTER (UID, NAME)
VALUES (@create_formflow, 'CREATE_FORMFLOW'),
       (@read_formflow, 'READ_FORMFLOW'),
       (@update_formflow, 'UPDATE_FORMFLOW'),
       (@delete_formflow, 'DELETE_FORMFLOW'),
       (@publish_formflow, 'PUBLISH_FORMFLOW'),
       (@create_form, 'CREATE_FORM'),
       (@read_form, 'READ_FORM'),
       (@update_form, 'UPDATE_FORM'),
       (@delete_form, 'DELETE_FORM'),
       (@publish_form, 'PUBLISH_FORM'),
       (@create_menu, 'CREATE_MENU'),
       (@read_menu, 'READ_MENU'),
       (@update_menu, 'UPDATE_MENU'),
       (@delete_menu, 'DELETE_MENU'),
       (@create_cc, 'CREATE_CUSTOM_CONTROL'),
       (@read_cc, 'READ_CUSTOM_CONTROL'),
       (@update_cc, 'UPDATE_CUSTOM_CONTROL'),
       (@delete_cc, 'DELETE_CUSTOM_CONTROL'),
       (@extend, 'EXTEND_FORMFLOW')
GO

--Role-Privilege mapping master table

DECLARE @pd_role UNIQUEIDENTIFIER = (SELECT ID
                                     FROM #UIDS
                                     WHERE NAME = 'pd_role')
DECLARE @ds_role UNIQUEIDENTIFIER = (SELECT ID
                                     FROM #UIDS
                                     WHERE NAME = 'ds_role')
DECLARE @3p_role UNIQUEIDENTIFIER = (SELECT ID
                                     FROM #UIDS
                                     WHERE NAME = '3p_role')
DECLARE @customer_role UNIQUEIDENTIFIER = (SELECT ID
                                           FROM #UIDS
                                           WHERE NAME = 'customer_role')

DECLARE @create_formflow UNIQUEIDENTIFIER = 'a78ecf61-cd77-4830-a5cd-658083f931fb'
DECLARE @read_formflow UNIQUEIDENTIFIER = 'b9a81569-2d88-4cfb-825f-3e2879e8696c'
DECLARE @update_formflow UNIQUEIDENTIFIER = 'ed44917b-568d-4590-8a2b-d5eed59d6a0c'
DECLARE @delete_formflow UNIQUEIDENTIFIER = '7d72c700-ebeb-4bcb-9851-f7c9c77fdec9'
DECLARE @publish_formflow UNIQUEIDENTIFIER = 'ac916c4e-544f-4ce1-9391-9c1485ea730f'
DECLARE @create_form UNIQUEIDENTIFIER = 'bd57b56b-6d76-45a5-ae25-be169b41e6a9'
DECLARE @read_form UNIQUEIDENTIFIER = '48e5d92d-4085-4c4a-8bbb-1cd69130cf1d'
DECLARE @update_form UNIQUEIDENTIFIER = '6dd32657-ccfb-4f6a-a409-818fe7ac0e64'
DECLARE @delete_form UNIQUEIDENTIFIER = '50023b26-98aa-420c-be40-ba59d5a80175'
DECLARE @publish_form UNIQUEIDENTIFIER = '77864f09-15dd-445b-bc3e-4b2c28bffbe8'
DECLARE @create_menu UNIQUEIDENTIFIER = '2e273e41-1500-412a-8ccf-726d0352989b'
DECLARE @read_menu UNIQUEIDENTIFIER = '05536359-a224-47d8-b3fa-bcbf4bc629f4'
DECLARE @update_menu UNIQUEIDENTIFIER = 'ac6f497a-0eab-4d5c-b27d-9d5a1f2d41f5'
DECLARE @delete_menu UNIQUEIDENTIFIER = '58815b9e-92a0-4276-8109-a571006241a1'
DECLARE @create_cc UNIQUEIDENTIFIER = 'f9b7f17c-5532-4238-b050-c1ab5781d945'
DECLARE @read_cc UNIQUEIDENTIFIER = '275c55e7-49df-4545-990a-5989d2d26872'
DECLARE @update_cc UNIQUEIDENTIFIER = '92c18c14-6733-45aa-91f6-977ba03d8b8f'
DECLARE @delete_cc UNIQUEIDENTIFIER = '8ceb8162-dc72-435d-9c07-977236bea6d7'
DECLARE @extend UNIQUEIDENTIFIER = '8ee09061-4e50-4c8c-b448-923cea8636bb'

INSERT INTO ROLE_PRIVILEGE (UID, ROLE_ID, PRIVILEGE_ID)
VALUES (NEWID(), @pd_role, @create_formflow),
       (NEWID(), @pd_role, @read_formflow),
       (NEWID(), @pd_role, @update_formflow),
       (NEWID(), @pd_role, @delete_formflow),
       (NEWID(), @pd_role, @publish_formflow),
       (NEWID(), @pd_role, @create_form),
       (NEWID(), @pd_role, @read_form),
       (NEWID(), @pd_role, @update_form),
       (NEWID(), @pd_role, @delete_form),
       (NEWID(), @pd_role, @publish_form),
       (NEWID(), @pd_role, @create_menu),
       (NEWID(), @pd_role, @read_menu),
       (NEWID(), @pd_role, @update_menu),
       (NEWID(), @pd_role, @delete_menu),
       (NEWID(), @pd_role, @create_cc),
       (NEWID(), @pd_role, @read_cc),
       (NEWID(), @pd_role, @update_cc),
       (NEWID(), @pd_role, @delete_cc),
       (NEWID(), @pd_role, @extend),

       (NEWID(), @ds_role, @create_formflow),
       (NEWID(), @ds_role, @read_formflow),
       (NEWID(), @ds_role, @update_formflow),
       (NEWID(), @ds_role, @delete_formflow),
       (NEWID(), @ds_role, @publish_formflow),
       (NEWID(), @ds_role, @create_form),
       (NEWID(), @ds_role, @read_form),
       (NEWID(), @ds_role, @update_form),
       (NEWID(), @ds_role, @delete_form),
       (NEWID(), @ds_role, @publish_form),
       (NEWID(), @ds_role, @create_menu),
       (NEWID(), @ds_role, @read_menu),
       (NEWID(), @ds_role, @update_menu),
       (NEWID(), @ds_role, @delete_menu),
       (NEWID(), @ds_role, @create_cc),
       (NEWID(), @ds_role, @read_cc),
       (NEWID(), @ds_role, @update_cc),
       (NEWID(), @ds_role, @delete_cc),
       (NEWID(), @ds_role, @extend),

       (NEWID(), @3p_role, @create_formflow),
       (NEWID(), @3p_role, @read_formflow),
       (NEWID(), @3p_role, @update_formflow),
       (NEWID(), @3p_role, @delete_formflow),
       (NEWID(), @3p_role, @publish_formflow),
       (NEWID(), @3p_role, @create_form),
       (NEWID(), @3p_role, @read_form),
       (NEWID(), @3p_role, @update_form),
       (NEWID(), @3p_role, @delete_form),
       (NEWID(), @3p_role, @publish_form),
       (NEWID(), @3p_role, @create_menu),
       (NEWID(), @3p_role, @read_menu),
       (NEWID(), @3p_role, @update_menu),
       (NEWID(), @3p_role, @delete_menu),
       (NEWID(), @3p_role, @create_cc),
       (NEWID(), @3p_role, @read_cc),
       (NEWID(), @3p_role, @update_cc),
       (NEWID(), @3p_role, @delete_cc),
       (NEWID(), @3p_role, @extend),

       (NEWID(), @customer_role, @create_formflow),
       (NEWID(), @customer_role, @read_formflow),
       (NEWID(), @customer_role, @update_formflow),
       (NEWID(), @customer_role, @delete_formflow),
       (NEWID(), @customer_role, @publish_formflow),
       (NEWID(), @customer_role, @create_form),
       (NEWID(), @customer_role, @read_form),
       (NEWID(), @customer_role, @update_form),
       (NEWID(), @customer_role, @delete_form),
       (NEWID(), @customer_role, @publish_form),
       (NEWID(), @customer_role, @create_menu),
       (NEWID(), @customer_role, @read_menu),
       (NEWID(), @customer_role, @update_menu),
       (NEWID(), @customer_role, @delete_menu),
       (NEWID(), @customer_role, @create_cc),
       (NEWID(), @customer_role, @read_cc),
       (NEWID(), @customer_role, @update_cc),
       (NEWID(), @customer_role, @delete_cc),
       (NEWID(), @customer_role, @extend)

GO

--Menu Types

DECLARE @main_menu UNIQUEIDENTIFIER = '0b3e6e8f-d04f-4482-bd1a-48a04d2e76ea' -- 8f6e3e0b-4fd0-8244-bd1a-48a04d2e76ea
DECLARE @global_context_menu UNIQUEIDENTIFIER = 'd2354b47-4bdf-4791-b8fe-986d967bc48e'
DECLARE @form_context_menu UNIQUEIDENTIFIER = '1a44f2b6-7dd9-49f0-9c3d-10dfda0380ca'
DECLARE @bottom_bar_menu UNIQUEIDENTIFIER = '275041a8-6a10-4288-bbcc-1caf5bc51444'

INSERT INTO MENU_TYPE (UID, MENU_TYPE)
VALUES (@main_menu, 'MAIN'),
       (@global_context_menu, 'GLOBAL_CONTEXT'),
       (@form_context_menu, 'FORM_CONTEXT'),
       (@bottom_bar_menu, 'BOTTOM_BAR')

GO

-- APP Config predefined entries for Mobile configuration

DECLARE @home_flow_id UNIQUEIDENTIFIER = 'bed6cf52-acdd-4a25-91bf-5eea55ea0af2'
DECLARE @default_flow_id UNIQUEIDENTIFIER = '1b4c90dc-d508-499e-8809-545146fa1390'
DECLARE @placeholder_font UNIQUEIDENTIFIER = 'ca3482fe-5ff3-426a-a394-b4f5a013d6ec'
DECLARE @placeholder_color UNIQUEIDENTIFIER = 'd4e47dcf-54a8-4f6d-b85d-97a1a84a4d0f'
DECLARE @error_font UNIQUEIDENTIFIER = '4bb628be-d811-4461-993b-966f4e8ff2e4'
DECLARE @error_color UNIQUEIDENTIFIER = '88835093-8161-4145-a7ef-425da88f0968'
DECLARE @label_font UNIQUEIDENTIFIER = '864eb8d6-5c3b-49bb-aca0-aa644b6334ef'
DECLARE @label_color UNIQUEIDENTIFIER = '92722da8-3324-48b7-bb3a-7835a4e75e99'
DECLARE @hint_font UNIQUEIDENTIFIER = '2df6ff47-2cdc-4685-9e61-7bc72f80262c'
DECLARE @hint_color UNIQUEIDENTIFIER = 'd585aba9-63a3-414d-a4ec-2d74b9a9a704'
DECLARE @left_padding UNIQUEIDENTIFIER = '5907b9a8-9201-47a7-8c7e-c6db08ebcccc'
DECLARE @right_padding UNIQUEIDENTIFIER = 'aadcb503-087d-4578-b9c8-466002cc1dad'
DECLARE @center_padding UNIQUEIDENTIFIER = '9e0dbc0f-c723-417f-a224-1b2e78c459ab'
DECLARE @spacing_lblnctrl UNIQUEIDENTIFIER = 'f9d25a32-1847-4ceb-81db-4a082cf8b0d8'
DECLARE @control_spacing UNIQUEIDENTIFIER = '834b2dc5-7a26-4d9f-bbaf-9c473b06233b'
DECLARE @img_cache_duration UNIQUEIDENTIFIER = '8c65f571-9ccd-43fe-88ca-8be724581d5c'
DECLARE @separator_height UNIQUEIDENTIFIER = '0e5687a4-38ea-4e44-a134-0a4524c3aee9'
DECLARE @separator_color UNIQUEIDENTIFIER = 'b66a5c01-ed26-4dc5-bbec-f56ed6a5a4b7'
DECLARE @separator_spacing UNIQUEIDENTIFIER = 'f7ef5d37-8ccd-4cbb-95ff-ef801543a810'
DECLARE @top_padding UNIQUEIDENTIFIER = 'd24492fc-07bf-4417-b12c-5b5aec68bdac'
DECLARE @bottom_padding UNIQUEIDENTIFIER = 'e42adec1-ad39-435c-a32d-5dd1b2d5f945'
DECLARE @top_margin UNIQUEIDENTIFIER = 'aa2b0760-2873-469d-9ae0-4b50e35425c4'
DECLARE @bottom_margin UNIQUEIDENTIFIER = '59e2c203-fc09-4961-a111-aed33256a6ec'
DECLARE @left_margin UNIQUEIDENTIFIER = 'ed2b6452-bf3d-41b9-b81a-959d9d6a5544'
DECLARE @right_margin UNIQUEIDENTIFIER = 'e85a8e87-daf2-48a6-8b91-9c4bcbf179e4'
DECLARE @floating_lbl_font_size UNIQUEIDENTIFIER = '472e2c83-d21b-467b-9d6b-255a61f39404'
DECLARE @floating_lbl_color UNIQUEIDENTIFIER = 'ced0e257-f117-42c5-95f2-83028dc75bbe'
DECLARE @style UNIQUEIDENTIFIER = 'd820626c-6473-451e-b455-b5d91096500e'
DECLARE @warehouse_id UNIQUEIDENTIFIER = '35a0b562-9036-4880-a0b1-bdb1e01c7303'
DECLARE @device_id UNIQUEIDENTIFIER = '58bc5931-5233-4c89-b767-6d39f2d17e88'

INSERT INTO APP_CONFIG_MASTER (UID, CONFIG_NAME, CONFIG_TYPE)
VALUES (@home_flow_id, 'HOME_FLOW_ID', 'APPLICATION'),
       (@default_flow_id, 'DEFAULT_FLOW_ID', 'APPLICATION'),
       (@placeholder_font, 'PLACEHOLDERFONT', 'APPLICATION'),
       (@placeholder_color, 'PLACEHOLDERCOLOR', 'APPLICATION'),
       (@error_font, 'ERRORFONT', 'APPLICATION'),
       (@error_color, 'ERRORCOLOR', 'APPLICATION'),
       (@label_font, 'LABELFONT', 'APPLICATION'),
       (@label_color, 'LABELCOLOR', 'APPLICATION'),
       (@hint_font, 'HINTFONT', 'APPLICATION'),
       (@hint_color, 'HINTCOLOR', 'APPLICATION'),
       (@left_padding, 'LEFTPADDING', 'APPLICATION'),
       (@right_padding, 'RIGHTPADDING', 'APPLICATION'),
       (@center_padding, 'CENTREPADDING', 'APPLICATION'),
       (@spacing_lblnctrl, 'SPACING_LABELNCONTROL', 'APPLICATION'),
       (@control_spacing, 'CONTROLSPACING', 'APPLICATION'),
       (@img_cache_duration, 'IMAGECACHEDURATION', 'APPLICATION'),
       (@separator_height, 'SEPARATORHEIGHT', 'APPLICATION'),
       (@separator_color, 'SEPARATORCOLOR', 'APPLICATION'),
       (@separator_spacing, 'SEPARATORSPACING', 'APPLICATION'),
       (@top_padding, 'TOPPADDING', 'APPLICATION'),
       (@bottom_padding, 'BOTTOMPADDING', 'APPLICATION'),
       (@top_margin, 'TOPMARGIN', 'APPLICATION'),
       (@bottom_margin, 'BOTTOMMARGIN', 'APPLICATION'),
       (@left_margin, 'LEFTMARGIN', 'APPLICATION'),
       (@right_margin, 'RIGHTMARGIN', 'APPLICATION'),
       (@floating_lbl_font_size, 'FLOATINGLABEL_FONTSIZE', 'APPLICATION'),
       (@floating_lbl_color, 'FLOATINGLABEL_COLOR', 'APPLICATION'),
       (@style, 'STYLE', 'APPLICATION'),
       (@warehouse_id, 'WarehouseName', 'GLOBAL'),
       (@device_id, 'DeviceID', 'GLOBAL')

DECLARE @home_flow_value UNIQUEIDENTIFIER = 'bad2f4db-010c-419b-98a0-26347cdaf35a'
DECLARE @default_flow_value UNIQUEIDENTIFIER = 'e5d60c28-fb5f-4a33-bc86-0a4887caf60a'
DECLARE @placeholder_font_value UNIQUEIDENTIFIER = '7ea866c8-1030-420f-8603-5a71000bc17f'
DECLARE @placeholder_color_value UNIQUEIDENTIFIER = '189a41aa-81cd-4f21-849a-d0526c443e99'
DECLARE @error_font_value UNIQUEIDENTIFIER = '2a6ad27e-3718-4be4-95fa-f7f1116bf614'
DECLARE @error_color_value UNIQUEIDENTIFIER = '72494bb7-2fdc-4c7c-9e5a-cb51ae7ca16c'
DECLARE @label_font_value UNIQUEIDENTIFIER = 'e3d648a2-eaf9-46d3-9dc1-d9d2f9bd6579'
DECLARE @label_color_value UNIQUEIDENTIFIER = 'e5d76140-0a1d-4765-aeb4-720c30aa903d'
DECLARE @hint_font_value UNIQUEIDENTIFIER = '095a6d9a-f690-4959-8b35-ed1607b60ecb'
DECLARE @hint_color_value UNIQUEIDENTIFIER = '12192cbd-5fce-44b4-abcd-73c91a52e568'
DECLARE @left_padding_value UNIQUEIDENTIFIER = '08338f2c-ec65-42f1-9971-68c022679959'
DECLARE @right_padding_value UNIQUEIDENTIFIER = 'd1feb0ed-b0f4-4e1b-9a3b-264ce9109cd4'
DECLARE @center_padding_value UNIQUEIDENTIFIER = 'f569e34c-0381-427a-a292-2ac60893f9e8'
DECLARE @spacing_lblnctrl_value UNIQUEIDENTIFIER = '1380649b-8b6e-445f-a56d-36f2cc5311e8'
DECLARE @control_spacing_value UNIQUEIDENTIFIER = 'c8df00fc-c1b5-4249-b33e-5a6953a6d4dd'
DECLARE @img_cache_duration_value UNIQUEIDENTIFIER = '02f543bb-ee9c-4021-829c-aae34377d860'
DECLARE @separator_height_value UNIQUEIDENTIFIER = 'bb9f013a-f5f4-4dbc-828e-e7b76ee6cdbc'
DECLARE @separator_color_value UNIQUEIDENTIFIER = '4df607cf-f65f-4a18-a8af-090d13b6057b'
DECLARE @separator_spacing_value UNIQUEIDENTIFIER = '9d8206e9-be4c-4fc7-84af-6eeeda0ff2da'
DECLARE @top_padding_value UNIQUEIDENTIFIER = 'df1af9ce-6763-4f4f-9390-8c8f30303086'
DECLARE @bottom_padding_value UNIQUEIDENTIFIER = 'baf334ab-8e3d-42bb-a650-34aa5e0f7838'
DECLARE @top_margin_value UNIQUEIDENTIFIER = '680c7a05-bca2-4e7a-a4c7-127d62321eb6'
DECLARE @bottom_margin_value UNIQUEIDENTIFIER = 'd97433f0-35b3-4f4d-8a9c-9222257484ca'
DECLARE @left_margin_value UNIQUEIDENTIFIER = '4e4d5c58-2169-414c-83d8-7c383391a24f'
DECLARE @right_margin_value UNIQUEIDENTIFIER = '45ab6245-af84-4e04-b912-afdbb473457c'
DECLARE @floating_lbl_font_size_value UNIQUEIDENTIFIER = '3687adb0-7c85-4053-816a-8075aa0f7b48'
DECLARE @floating_lbl_color_value UNIQUEIDENTIFIER = 'f8dcbe6b-245d-4148-a462-6e80f3a719d7'
DECLARE @style_value UNIQUEIDENTIFIER = 'f96f4d4a-6218-476a-937d-3f5bb26a43d3'

INSERT INTO APP_CONFIG_DETAIL (UID, CONFIG_VALUE, APP_CONFIG_MASTER_UID)
VALUES (@home_flow_value, null, @home_flow_id),
       (@default_flow_value, null, @default_flow_id),
       (@placeholder_font_value, '16', @placeholder_font),
       (@placeholder_color_value, '#A9A9A9', @placeholder_color),
       (@error_font_value, '16', @error_font),
       (@error_color_value, '#FF0000', @error_color),
       (@label_font_value, '16', @label_font),
       (@label_color_value, '#0000FF', @label_color),
       (@hint_font_value, '14', @hint_font),
       (@hint_color_value, '#A9A9A9', @hint_color),
       (@left_padding_value, '10', @left_padding),
       (@right_padding_value, '10', @right_padding),
       (@center_padding_value, '10', @center_padding),
       (@spacing_lblnctrl_value, '10', @spacing_lblnctrl),
       (@control_spacing_value, '30', @control_spacing),
       (@img_cache_duration_value, '45', @img_cache_duration),
       (@separator_height_value, '1', @separator_height),
       (@separator_color_value, '#D3D3D3', @separator_color),
       (@separator_spacing_value, '10', @separator_spacing),
       (@top_padding_value, '10', @top_padding),
       (@bottom_padding_value, '10', @bottom_padding),
       (@top_margin_value, '10', @top_margin),
       (@bottom_margin_value, '10', @bottom_margin),
       (@left_margin_value, '10', @left_margin),
       (@right_margin_value, '10', @right_margin),
       (@floating_lbl_font_size_value, '14', @floating_lbl_font_size),
       (@floating_lbl_color_value, '#0000FF', @floating_lbl_color),
       (@style_value, 'Outlined', @style)

GO

--Orchestration Registries

DECLARE @pd_orchestration UNIQUEIDENTIFIER = '383b64e2-89b4-45c7-be4a-a63cc885722a'
DECLARE @ds_orchestration UNIQUEIDENTIFIER = '5c464687-99ca-4507-9e9b-8f5c073fd613'
DECLARE @3p_orchestration UNIQUEIDENTIFIER = 'a0cf8be0-c56d-4887-a0df-8b7247941979'
DECLARE @customer_orchestration UNIQUEIDENTIFIER = '59e2c203-fc09-4961-a111-aed33256a6ec'

DECLARE @pd_role UNIQUEIDENTIFIER = (SELECT ID
                                     FROM #UIDS
                                     WHERE NAME = 'pd_role')
DECLARE @ds_role UNIQUEIDENTIFIER = (SELECT ID
                                     FROM #UIDS
                                     WHERE NAME = 'ds_role')
DECLARE @3p_role UNIQUEIDENTIFIER = (SELECT ID
                                     FROM #UIDS
                                     WHERE NAME = '3p_role')
DECLARE @customer_role UNIQUEIDENTIFIER = (SELECT ID
                                           FROM #UIDS
                                           WHERE NAME = 'customer_role')

INSERT INTO API_REGISTRY
( UID
, API_TYPE
, API_VERSION
, BASE_PATH
, CONTEXT_PATH
, CREATED_BY
, CREATION_DATE
, HELPER_CLASS
, LAST_MODIFIED_BY
, LAST_MODIFIED_DATE
, NAME
, PORT
, VERSION_ID
, ROLE_ID)
VALUES (@pd_orchestration, 'ORCHESTRATION', '1.0', 'http://0.0.0.0', '/test', 'SUPER', GETDATE(),
        null, 'SUPER', GETDATE(), 'Orchestration', '80', '1', @pd_role),
       (@ds_orchestration, 'ORCHESTRATION', '1.0', 'http://0.0.0.0', '/test', 'SUPER', GETDATE(),
        null, 'SUPER', GETDATE(), 'Orchestration', '80', '1', @ds_role),
       (@3p_orchestration, 'ORCHESTRATION', '1.0', 'http://0.0.0.0', '/test', 'SUPER', GETDATE(),
        null, 'SUPER', GETDATE(), 'Orchestration', '80', '1', @3p_role),
       (@customer_orchestration, 'ORCHESTRATION', '1.0', 'http://0.0.0.0', '/test', 'SUPER', GETDATE(),
        null, 'SUPER', GETDATE(), 'Orchestration', '80', '1', @customer_role);
GO

-- Hot Keys
INSERT INTO KEY_CODE_MASTER (UID, KEY_NAME, KEY_DISPLAY_NAME, KEY_DESCRIPTION, CODE, IS_CTRL, IS_SHIFT, IS_ALT,
                             IS_METAKEY, TYPE, SEQUENCE)
VALUES
    -- Globals
    (NEWID(), 'Funtion_F1', 'KEYCODE_F1', '', '131', '0', '0', '0', '0', 'GLOBAL', 1),
    (NEWID(), 'Alt_Funtion_F1', 'ALT + KEYCODE_F1', '', '131', '0', '0', '1', '0', 'GLOBAL', 2),
    (NEWID(), 'Ctrl_Funtion_F1', 'CTRL + KEYCODE_F1', '', '131', '1', '0', '0', '0', 'GLOBAL', 3),
    (NEWID(), 'Shift_Funtion_F1', 'SHFT + KEYCODE_F1', '', '131', '0', '1', '0', '0', 'GLOBAL', 4),
    (NEWID(), 'Funtion_F2', 'KEYCODE_F2', '', '132', '0', '0', '0', '0', 'GLOBAL', 5),
    (NEWID(), 'Alt_Funtion_F2', 'ALT + KEYCODE_F2', '', '132', '0', '0', '1', '0', 'GLOBAL', 6),
    (NEWID(), 'Ctrl_Funtion_F2', 'CTRL + KEYCODE_F2', '', '132', '1', '0', '0', '0', 'GLOBAL', 7),
    (NEWID(), 'Shift_Funtion_F2', 'SHFT + KEYCODE_F2', '', '132', '0', '1', '0', '0', 'GLOBAL', 8),
    (NEWID(), 'Funtion_F3', 'KEYCODE_F3', '', '133', '0', '0', '0', '0', 'GLOBAL', 9),
    (NEWID(), 'Alt_Funtion_F3', 'ALT + KEYCODE_F3', '', '133', '0', '0', '1', '0', 'GLOBAL', 10),
    (NEWID(), 'Ctrl_Funtion_F3', 'CTRL + KEYCODE_F3', '', '133', '1', '0', '0', '0', 'GLOBAL', 11),
    (NEWID(), 'Shift_Funtion_F3', 'SHFT + KEYCODE_F3', '', '133', '0', '1', '0', '0', 'GLOBAL', 12),
    (NEWID(), 'Funtion_F4', 'KEYCODE_F4', '', '134', '0', '0', '0', '0', 'GLOBAL', 13),
    (NEWID(), 'Alt_Funtion_F4', 'ALT + KEYCODE_F4', '', '134', '0', '0', '1', '0', 'GLOBAL', 14),
    (NEWID(), 'Ctrl_Funtion_F4', 'CTRL + KEYCODE_F4', '', '134', '1', '0', '0', '0', 'GLOBAL', 15),
    (NEWID(), 'Shift_Funtion_F4', 'SHFT + KEYCODE_F4', '', '134', '0', '1', '0', '0', 'GLOBAL', 16),
    (NEWID(), 'Funtion_F5', 'KEYCODE_F5', '', '135', '0', '0', '0', '0', 'GLOBAL', 17),
    (NEWID(), 'Alt_Funtion_F5', 'ALT + KEYCODE_F5', '', '135', '0', '0', '1', '0', 'GLOBAL', 18),
    (NEWID(), 'Ctrl_Funtion_F5', 'CTRL + KEYCODE_F5', '', '135', '1', '0', '0', '0', 'GLOBAL', 19),
    (NEWID(), 'Shift_Funtion_F5', 'SHFT + KEYCODE_F5', '', '135', '0', '1', '0', '0', 'GLOBAL', 20),
    (NEWID(), 'Funtion_F6', 'KEYCODE_F6', '', '136', '0', '0', '0', '0', 'GLOBAL', 21),
    (NEWID(), 'Funtion_F7', 'KEYCODE_F7', '', '137', '0', '0', '0', '0', 'GLOBAL', 22),
    (NEWID(), 'Funtion_F8', 'KEYCODE_F8', '', '138', '0', '0', '0', '0', 'GLOBAL', 23),
    (NEWID(), 'Funtion_F9', 'KEYCODE_F9', '', '139', '0', '0', '0', '0', 'GLOBAL', 24),
    (NEWID(), 'Funtion_F10', 'KEYCODE_F10', '', '140', '0', '0', '0', '0', 'GLOBAL', 25),
    (NEWID(), 'Funtion_F11', 'KEYCODE_F11', '', '141', '0', '0', '0', '0', 'GLOBAL', 26),
    (NEWID(), 'Funtion_F12', 'KEYCODE_F12', '', '142', '0', '0', '0', '0', 'GLOBAL', 27),
    (NEWID(), 'V', 'V', '', '50', '0', '0', '0', '0', 'GLOBAL', 28),
    (NEWID(), 'Alt_V', 'ALT+V', '', '50', '0', '0', '1', '0', 'GLOBAL', 29),
    (NEWID(), 'Shft_V', 'SHFT+V', '', '50', '0', '1', '0', '0', 'GLOBAL', 30),
    (NEWID(), 'Ctrl_V', 'CTRL+V', '', '50', '1', '0', '0', '0', 'GLOBAL', 31),
    (NEWID(), 'W', 'W', '', '51', '0', '0', '0', '0', 'GLOBAL', 32),
    (NEWID(), 'Alt_W', 'ALT+W', '', '51', '0', '0', '1', '0', 'GLOBAL', 33),
    (NEWID(), 'Ctrl_W', 'CTRL+W', '', '51', '1', '0', '0', '0', 'GLOBAL', 34),
    (NEWID(), 'X', 'X', '', '52', '0', '0', '0', '0', 'GLOBAL', 35),
    (NEWID(), 'Alt_X', 'ALT+X', '', '52', '0', '0', '1', '0', 'GLOBAL', 36),
    (NEWID(), 'Shft_X', 'SHFT+X', '', '52', '0', '1', '0', '0', 'GLOBAL', 37),
    (NEWID(), 'Ctrl_X', 'CTRL+X', '', '52', '1', '0', '0', '0', 'GLOBAL', 38),
    (NEWID(), 'Y', 'Y', '', '53', '0', '0', '0', '0', 'GLOBAL', 39),
    (NEWID(), 'Alt_Y', 'ALT+Y', '', '53', '0', '0', '1', '0', 'GLOBAL', 40),
    (NEWID(), 'Shft_Y', 'SHFT+Y', '', '53', '0', '1', '0', '0', 'GLOBAL', 41),
    -- Context
    (NEWID(), 'A', 'A', '', '29', '0', '0', '0', '0', 'CONTEXT', 1),
    (NEWID(), 'Alt_A', 'ALT+A', '', '29', '0', '0', '1', '0', 'CONTEXT', 2),
    (NEWID(), 'Shft_A', 'SHFT+A', '', '29', '0', '1', '0', '0', 'CONTEXT', 3),
    (NEWID(), 'Ctrl_A', 'CTRL+A', '', '29', '1', '0', '0', '0', 'CONTEXT', 4),
    (NEWID(), 'B', 'B', '', '30', '0', '0', '0', '0', 'CONTEXT', 5),
    (NEWID(), 'Alt_B', 'ALT+B', '', '30', '0', '0', '1', '0', 'CONTEXT', 6),
    (NEWID(), 'Shft_B', 'SHFT+B', '', '30', '0', '1', '0', '0', 'CONTEXT', 7),
    (NEWID(), 'Ctrl_B', 'CTRL+B', '', '30', '1', '0', '0', '0', 'CONTEXT', 8),
    (NEWID(), 'C', 'C', '', '31', '0', '0', '0', '0', 'CONTEXT', 9),
    (NEWID(), 'Alt_C', 'ALT+C', '', '31', '0', '0', '1', '0', 'CONTEXT', 10),
    (NEWID(), 'Shft_C', 'SHFT+C', '', '31', '0', '1', '0', '0', 'CONTEXT', 11),
    (NEWID(), 'Ctrl_C', 'CTRL+C', '', '31', '1', '0', '0', '0', 'CONTEXT', 12),
    (NEWID(), 'D', 'D', '', '32', '0', '0', '0', '0', 'CONTEXT', 13),
    (NEWID(), 'Alt_D', 'ALT+D', '', '32', '0', '0', '1', '0', 'CONTEXT', 14),
    (NEWID(), 'Shft_D', 'SHFT+D', '', '32', '0', '1', '0', '0', 'CONTEXT', 15),
    (NEWID(), 'Ctrl_D', 'CTRL+D', '', '32', '1', '0', '0', '0', 'CONTEXT', 16),
    (NEWID(), 'Alt_E', 'ALT+E', '', '33', '0', '0', '1', '0', 'CONTEXT', 17),
    (NEWID(), 'Shft_E', 'SHFT+E', '', '33', '0', '1', '0', '0', 'CONTEXT', 18),
    (NEWID(), 'Ctrl_E', 'CTRL+E', '', '33', '1', '0', '0', '0', 'CONTEXT', 19),
    (NEWID(), 'Meta_E', 'META+E', '', '33', '0', '0', '0', '1', 'CONTEXT', 20),
    (NEWID(), 'F', 'F', '', '34', '0', '0', '0', '0', 'CONTEXT', 21),
    (NEWID(), 'Alt_F', 'ALT+F', '', '34', '0', '0', '1', '0', 'CONTEXT', 22),
    (NEWID(), 'Shft_F', 'SHFT+F', '', '34', '0', '1', '0', '0', 'CONTEXT', 23),
    (NEWID(), 'Ctrl_F', 'CTRL+F', '', '34', '1', '0', '0', '0', 'CONTEXT', 24),
    (NEWID(), 'G', 'G', '', '35', '0', '0', '0', '0', 'CONTEXT', 25),
    (NEWID(), 'Alt_G', 'ALT+G', '', '35', '0', '0', '1', '0', 'CONTEXT', 26),
    (NEWID(), 'Shft_G', 'SHFT+G', '', '35', '0', '1', '0', '0', 'CONTEXT', 27),
    (NEWID(), 'Ctrl_G', 'CTRL+G', '', '35', '1', '0', '0', '0', 'CONTEXT', 28),
    (NEWID(), 'H', 'H', '', '36', '0', '0', '0', '0', 'CONTEXT', 29),
    (NEWID(), 'Alt_H', 'ALT+H', '', '36', '0', '0', '1', '0', 'CONTEXT', 30),
    (NEWID(), 'Shft_H', 'SHFT+H', '', '36', '0', '1', '0', '0', 'CONTEXT', 31),
    (NEWID(), 'Ctrl_H', 'CTRL+H', '', '36', '1', '0', '0', '0', 'CONTEXT', 32),
    (NEWID(), 'I', 'I', '', '37', '0', '0', '0', '0', 'CONTEXT', 33),
    (NEWID(), 'Alt_I', 'ALT+I', '', '37', '0', '0', '1', '0', 'CONTEXT', 34),
    (NEWID(), 'Shft_I', 'SHFT+I', '', '37', '0', '1', '0', '0', 'CONTEXT', 35),
    (NEWID(), 'Ctrl_I', 'CTRL+I', '', '37', '1', '0', '0', '0', 'CONTEXT', 36),
    (NEWID(), 'J', 'J', '', '38', '0', '0', '0', '0', 'CONTEXT', 37),
    (NEWID(), 'Alt_J', 'ALT+J', '', '38', '0', '0', '1', '0', 'CONTEXT', 38),
    (NEWID(), 'Shft_J', 'SHFT+J', '', '38', '0', '1', '0', '0', 'CONTEXT', 39),
    (NEWID(), 'Ctrl_J', 'CTRL+J', '', '38', '1', '0', '0', '0', 'CONTEXT', 40),
    (NEWID(), 'K', 'K', '', '39', '0', '0', '0', '0', 'CONTEXT', 41),
    (NEWID(), 'Alt_K', 'ALT+K', '', '39', '0', '0', '1', '0', 'CONTEXT', 42),
    (NEWID(), 'Shft_K', 'SHFT+K', '', '39', '0', '1', '0', '0', 'CONTEXT', 43),
    (NEWID(), 'Ctrl_K', 'CTRL+K', '', '39', '1', '0', '0', '0', 'CONTEXT', 44),
    (NEWID(), 'Alt_L', 'ALT+L', '', '40', '0', '0', '1', '0', 'CONTEXT', 45),
    (NEWID(), 'Shft_L', 'SHFT+L', '', '40', '0', '1', '0', '0', 'CONTEXT', 46),
    (NEWID(), 'Ctrl_L', 'CTRL+L', '', '40', '1', '0', '0', '0', 'CONTEXT', 47),
    (NEWID(), 'Meta_L', 'META+L', '', '40', '0', '0', '0', '1', 'CONTEXT', 48),
    (NEWID(), 'M', 'M', '', '41', '0', '0', '0', '0', 'CONTEXT', 49),
    (NEWID(), 'Alt_M', 'ALT+M', '', '41', '0', '0', '1', '0', 'CONTEXT', 50),
    (NEWID(), 'Shft_M', 'SHFT+M', '', '41', '0', '1', '0', '0', 'CONTEXT', 51),
    (NEWID(), 'Ctrl_M', 'CTRL+M', '', '41', '1', '0', '0', '0', 'CONTEXT', 52),
    (NEWID(), 'N', 'N', '', '42', '0', '0', '0', '0', 'CONTEXT', 53),
    (NEWID(), 'Alt_N', 'ALT+N', '', '42', '0', '0', '1', '0', 'CONTEXT', 54),
    (NEWID(), 'Shft_N', 'SHFT+N', '', '42', '0', '1', '0', '0', 'CONTEXT', 55),
    (NEWID(), 'Ctrl_N', 'CTRL+N', '', '42', '1', '0', '0', '0', 'CONTEXT', 56),
    (NEWID(), 'O', 'O', '', '43', '0', '0', '0', '0', 'CONTEXT', 57),
    (NEWID(), 'Alt_O', 'ALT+O', '', '43', '0', '0', '1', '0', 'CONTEXT', 58),
    (NEWID(), 'Shft_O', 'SHFT+O', '', '43', '0', '1', '0', '0', 'CONTEXT', 59),
    (NEWID(), 'Ctrl_O', 'CTRL+O', '', '43', '1', '0', '0', '0', 'CONTEXT', 60),
    (NEWID(), 'P', 'P', '', '44', '0', '0', '0', '0', 'CONTEXT', 61),
    (NEWID(), 'Alt_P', 'ALT+P', '', '44', '0', '0', '1', '0', 'CONTEXT', 62),
    (NEWID(), 'Shft_P', 'SHFT+P', '', '44', '0', '1', '0', '0', 'CONTEXT', 63),
    (NEWID(), 'Ctrl_P', 'CTRL+P', '', '44', '1', '0', '0', '0', 'CONTEXT', 64),
    (NEWID(), 'Q', 'Q', '', '45', '0', '0', '0', '0', 'CONTEXT', 65),
    (NEWID(), 'Alt_Q', 'ALT+Q', '', '45', '0', '0', '1', '0', 'CONTEXT', 66),
    (NEWID(), 'Shft_Q', 'SHFT+Q', '', '45', '0', '1', '0', '0', 'CONTEXT', 67),
    (NEWID(), 'Ctrl_Q', 'CTRL+Q', '', '45', '1', '0', '0', '0', 'CONTEXT', 68),
    (NEWID(), 'Alt_R', 'ALT+R', '', '46', '0', '0', '1', '0', 'CONTEXT', 69),
    (NEWID(), 'Shft_R', 'SHFT+R', '', '46', '0', '1', '0', '0', 'CONTEXT', 70),
    (NEWID(), 'Ctrl_R', 'CTRL+R', '', '46', '1', '0', '0', '0', 'CONTEXT', 71),
    (NEWID(), 'Ctrl+Shft+R', 'CTRL+SHFT+R', '', '46', '1', '1', '0', '0', 'CONTEXT', 72),
    (NEWID(), 'S', 'S', '', '47', '0', '0', '0', '0', 'CONTEXT', 73),
    (NEWID(), 'Alt_S', 'ALT+S', '', '47', '0', '0', '1', '0', 'CONTEXT', 74),
    (NEWID(), 'Shft_S', 'SHFT+S', '', '47', '0', '1', '0', '0', 'CONTEXT', 75),
    (NEWID(), 'Ctrl_S', 'CTRL+S', '', '47', '1', '0', '0', '0', 'CONTEXT', 76),
    (NEWID(), 'T', 'T', '', '48', '0', '0', '0', '0', 'CONTEXT', 77),
    (NEWID(), 'Alt_T', 'ALT+T', '', '48', '0', '0', '1', '0', 'CONTEXT', 78),
    (NEWID(), 'Shft_T', 'SHFT+T', '', '48', '0', '1', '0', '0', 'CONTEXT', 79),
    (NEWID(), 'Ctrl_T', 'CTRL+T', '', '48', '1', '0', '0', '0', 'CONTEXT', 80),
    (NEWID(), 'U', 'U', '', '49', '0', '0', '0', '0', 'CONTEXT', 81),
    (NEWID(), 'Alt_U', 'ALT+U', '', '49', '0', '0', '1', '0', 'CONTEXT', 82),
    (NEWID(), 'Shft_U', 'SHFT+U', '', '49', '0', '1', '0', '0', 'CONTEXT', 83),
    (NEWID(), 'Ctrl_U', 'CTRL+U', '', '49', '1', '0', '0', '0', 'CONTEXT', 84),
    (NEWID(), 'Z', 'Z', '', '54', '0', '0', '0', '0', 'CONTEXT', 85),
    (NEWID(), 'Alt_Z', 'ALT+Z', '', '54', '0', '0', '1', '0', 'CONTEXT', 86),
    (NEWID(), 'Shft_Z', 'SHFT+Z', '', '54', '0', '1', '0', '0', 'CONTEXT', 87),
    (NEWID(), 'Alt_Ctrl_Z', 'ALT+CTRL+Z', '', '54', '1', '0', '1', '0', 'CONTEXT', 88),
    (NEWID(), 'VolumeUp', 'VolumeUp', '', '24', '0', '0', '0', '0', 'CONTEXT', 89),
    (NEWID(), 'VolumeDown', 'VolumeDown', '', '25', '0', '0', '0', '0', 'CONTEXT', 90)
GO

-- Locales

DECLARE @fr_locale UNIQUEIDENTIFIER = '182608bb-cd58-4add-a454-99956065233c'
DECLARE @en_locale UNIQUEIDENTIFIER = '26fed643-8f1a-4dad-8d13-5eded9769141'

INSERT INTO LOCALE (UID, LOCALE_CODE)
VALUES (@fr_locale, 'FR'),
       (@en_locale, 'EN')

GO

-- Script for Version Master and Version Mapping for BFF Core , Admin and Mobile application

INSERT VERSION_MASTER (UID, CREATED_BY, CREATION_DATE, CHANNEL, ACTIVE, VERSION)
VALUES (NEWID(), 'SUPER', SYSDATETIME(), 'MOBILE_RENDERER', 0, '1'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'MOBILE_RENDERER', 0, '1.0'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'MOBILE_RENDERER', 0, '1.0.1'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'MOBILE_RENDERER', 0, '1.0.2'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'MOBILE_RENDERER', 0, '1.0.0'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'MOBILE_RENDERER', 0, '1.0.0.0'),

       (NEWID(), 'SUPER', SYSDATETIME(), 'ADMIN_UI', 0, '1'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'ADMIN_UI', 0, '1.0'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'ADMIN_UI', 0, '1.0.1'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'ADMIN_UI', 0, '1.0.2'),

       (NEWID(), 'SUPER', SYSDATETIME(), 'BFFCORE', 1, '1'),
       (NEWID(), 'SUPER', SYSDATETIME(), 'BFFCORE', 0, '1.0.0')

INSERT VERSION_MAPPING (UID, CREATED_BY, CREATION_DATE, BFFCORE_VERSION_ID, MAPPED_APP_ID)
VALUES (NEWID(), 'SUPER', SYSDATETIME(),
        (select uid from version_master where channel = 'BFFCORE' and active = 1),
        (select uid from version_master where channel = 'MOBILE_RENDERER' and version = '1')),
       (NEWID(), 'SUPER', SYSDATETIME(),
        (select uid from version_master where channel = 'BFFCORE' and active = 1),
        (select uid from version_master where channel = 'MOBILE_RENDERER' and version = '1.0')),
       (NEWID(), 'SUPER', SYSDATETIME(),
        (select uid from version_master where channel = 'BFFCORE' and active = 1),
        (select uid from version_master where channel = 'MOBILE_RENDERER' and version = '1.0.1')),
       (NEWID(), 'SUPER', SYSDATETIME(),
        (select uid from version_master where channel = 'BFFCORE' and active = 1),
        (select uid from version_master where channel = 'MOBILE_RENDERER' and version = '1.0.0')),
       (NEWID(), 'SUPER', SYSDATETIME(),
        (select uid from version_master where channel = 'BFFCORE' and active = 1),
        (select uid from version_master where channel = 'MOBILE_RENDERER' and version = '1.0.0.0')),
       (NEWID(), 'SUPER', SYSDATETIME(),
        (select uid from version_master where channel = 'BFFCORE' and active = 1),
        (select uid from version_master where channel = 'ADMIN_UI' and version = '1')),
       (NEWID(), 'SUPER', SYSDATETIME(),
        (select uid from version_master where channel = 'BFFCORE' and active = 1),
        (select uid from version_master where channel = 'ADMIN_UI' and version = '1.0')),
       (NEWID(), 'SUPER', SYSDATETIME(),
        (select uid from version_master where channel = 'BFFCORE' and active = 1),
        (select uid from version_master where channel = 'ADMIN_UI' and version = '1.0.1'))
GO

DROP TABLE #uids
GO

-- Localized Mobile text en-us
INSERT INTO resource_bundle (uid, locale, resource_key, resource_value, type, created_by, creation_date)
VALUES (NEWID(), 'en-US', 'ACTIVE_SESSION', 'Do you want to resume from one of your previous active session?', 'MOBILE',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'ALERT', 'ALERT', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'Alert_Message', 'Do you really want to exit the application?', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'Authentication_Failed', 'Authentication failed', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'CANCEL', 'CANCEL', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'BARCODE_ERROR', 'GS1 barcode scanning is not supported', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'CHECKBOX_ERROR_MESSAGE', 'Please select the checkbox', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'COMPONENT_ERROR', 'Invalid Format of Json!', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'CONNECTION_ERROR', 'Network unavailable! Please check the connection.', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', 'DATEPICKER_ERROR_MESSAGE', 'Please select date', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'DEFAULTFORM_ERROR', 'Form is not available', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'DEFAULT_FORM_ERROR', 'Configure Default Form ID', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'DROPDOWN_ERROR_MESSAGE', 'Please select any value', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'ENTER_WAREHOUSE_NAME', 'Please Enter Valid Warehouse name', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'ENTRY_EMPTY_MESSAGE', 'Please enter the text', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'ERROR', 'Error', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'FIELD_IS_REQUIRED', 'This field is required', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'Home', 'Home', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'INFO', 'INFO', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'INVALID_EMAIL', 'Invalid Email Id Pattern', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'INVALID_IMAGE_URL', 'Invalid image url', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'INVALID_NUMBER', 'Invalid Number Pattern', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'INVALID_TEXT', 'Invalid Text Pattern', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'LISTVIEW_ALERT', 'ListView does not have any values', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'Logout_Alert', 'Do you really want to logout the application?', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', 'MAXIMUM_LENGTH_MESSAGE', 'Value cannot exceed the maximum length', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', 'MAXIMUM_VALUE_MESSAGE', 'Value cannot exceed the maximum value of', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', 'MAXROWS', 'Maximum rows count already reached, could not add more rows', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', 'MINIMUMTEXT', 'Field should match the minimum text length', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'MINIMUM_LENGTH_MESSAGE', 'Value should match the minimum length', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', 'MINIMUM_VALUE_MESSAGE', 'Value should match the minimum value', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', 'MINROWS', 'Minimum rows requirement is not met', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'NO', 'No', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'OK_Message', 'OK', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'PAGE_ERROR', 'Server Error! Please try again later', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'QUESTIONNAIRE_INVALID_INPUT', 'Please enter valid input', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'Required_FIELDS', 'Please fill the fields', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'SELECT_RADIO', 'Select radio', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'TIMEPICKER_ERROR_MESSAGE', 'Please select time', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'TIME_MATCH_MAX', 'Time should match the maximum value', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'TIME_MAX_MESSAGE', 'Time should match the maximum value', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'TIME_MIN_MESSAGE', 'Time should match the minimum value', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'TIME_VALUE_MESSAGE', 'Time should match the minimum value and maximum value', 'MOBILE',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'WARNING', 'WARNING', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'Welcome', 'Welcome to application', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'DECIMAL_VALUE', 'Enter Valid Decimal Value', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'YES', 'Yes', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'Active_Sessions', 'Active Sessions', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'DeviceID', 'DeviceID:', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'UserName', 'UserName', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', 'LastAccessTime', 'LastAccessTime', 'MOBILE', 'SUPER', GETDATE());;

GO

-- Localized Mobile text fr-fr
INSERT INTO resource_bundle (uid, locale, resource_key, resource_value, type, created_by, creation_date)
VALUES (NEWID(), 'fr-FR', 'ACTIVE_SESSION', 'Do you want to resume from one of your previous active session?_FR',
        'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'ALERT', 'ALERT_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'Alert_Message', 'Do you really want to exit the application?_FR', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', 'Authentication_Failed', 'Authentication failed_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'CANCEL', 'CANCEL_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'BARCODE_ERROR', 'GS1 barcode scanning is not supported_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'CHECKBOX_ERROR_MESSAGE', 'Please select the checkbox_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'COMPONENT_ERROR', 'Invalid Format of Json!_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'CONNECTION_ERROR', 'Network unavailable! Please check the connection._FR', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', 'DATEPICKER_ERROR_MESSAGE', 'Please select date_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'DEFAULTFORM_ERROR', 'Form is not available_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'DEFAULT_FORM_ERROR', 'Configure Default Form ID_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'DROPDOWN_ERROR_MESSAGE', 'Please select any value_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'ENTER_WAREHOUSE_NAME', 'Please Enter Valid Warehouse name_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'ENTRY_EMPTY_MESSAGE', 'Please enter the text_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'ERROR', 'Error_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'FIELD_IS_REQUIRED', 'This field is required_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'Home', 'Home_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'INFO', 'INFO_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'INVALID_EMAIL', 'Invalid Email Id Pattern_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'INVALID_IMAGE_URL', 'Invalid image url_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'INVALID_NUMBER', 'Invalid Number Pattern_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'INVALID_TEXT', 'Invalid Text Pattern_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'LISTVIEW_ALERT', 'ListView does not have any values_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'Logout_Alert', 'Do you really want to logout the application?_FR', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', 'MAXIMUM_LENGTH_MESSAGE', 'Value cannot exceed the maximum length_FR', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', 'MAXIMUM_VALUE_MESSAGE', 'Value cannot exceed the maximum value of_FR', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', 'MAXROWS', 'Maximum rows count already reached, could not add more rows_FR', 'MOBILE',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'MINIMUMTEXT', 'Field should match the minimum text length_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'MINIMUM_LENGTH_MESSAGE', 'Value should match the minimum length_FR', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', 'MINIMUM_VALUE_MESSAGE', 'Value should match the minimum value_FR', 'MOBILE', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', 'MINROWS', 'Minimum rows requirement is not met_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'NO', 'No_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'OK_Message', 'OK_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'PAGE_ERROR', 'Server Error! Please try again later_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'QUESTIONNAIRE_INVALID_INPUT', 'Please enter valid input_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'Required_FIELDS', 'Please fill the fields_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'SELECT_RADIO', 'Select radio_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'TIMEPICKER_ERROR_MESSAGE', 'Please select time_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'TIME_MATCH_MAX', 'Time should match the maximum value_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'TIME_MAX_MESSAGE', 'Time should match the maximum value_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'TIME_MIN_MESSAGE', 'Time should match the minimum value_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'TIME_VALUE_MESSAGE', 'Time should match the minimum value and maximum value_FR', 'MOBILE',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'WARNING', 'WARNING_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'Welcome', 'Welcome to application_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'DECIMAL_VALUE', 'Enter Valid Decimal Value_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'YES', 'Yes_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'Active_Sessions', 'Active Sessions_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'DeviceID', 'DeviceID:_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'UserName', 'UserName_FR', 'MOBILE', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', 'LastAccessTime', 'LastAccessTime_FR', 'MOBILE', 'SUPER', GETDATE());

GO

-- Localized status text en-us
INSERT INTO resource_bundle (uid, locale, resource_key, resource_value, type, created_by, creation_date)
VALUES (NEWID(), 'en-US', '5001', 'Login authentication success', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5002', 'User logged in is authenticated', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9001', 'Login authentication failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9002', 'User access is not authorized', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9043', 'Login authentication failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9044', 'User authentication cannot be completed Authentication provider not responding.',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6003', 'Login authentication failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6004', 'Authentication process failed to set/validate credentials', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9810', '{0} version {1} is not compatible with {2} version {3}', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5003', 'Flow created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5004', 'Flow created successfully with flowId : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9003', 'Flow creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9004', '{0} is not unique', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6005', 'Flow creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6006', 'Flow creation failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9005', 'Flow creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9006', 'Flow creation failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5005', 'Flow updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5006', 'Flow updated successfully for id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6007', 'Flow update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6008', 'Flow update failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9007', 'Flow update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9008', '{0} is not unique', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9009', 'Flow update failed.Form name not unique', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9010', '{0} already exists. Form name is not unique for the flow.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9011', 'Flow update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9012', 'Flow update failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5007', 'Flow retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5008', 'Flow retrieved successfully for id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6009', 'Flow not found', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6010', 'Flow not available for id : {0} ', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9013', 'Flow not found.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9014', 'Flow not available for id : {0} ', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9163', 'Flow is not published yet', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9164', 'Flow is disabled', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9180', 'Default form is disabled for this flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9181', 'Default form is not published yet for this flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9182', 'Flow has no default forms', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9183', 'Form not found', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5009', 'Flow status count retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5010', 'Flow status count retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6011', 'Flow status count retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6012', 'Flow status count retrieval failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9015', 'Flow status count retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9016', 'Flow status count retrieval failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5011', 'Flow list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5012', 'Flow list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6013', 'Flow retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6014', 'Flow retrieval failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9017', 'Flow retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9018', 'Flow retrieval failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5013', 'Flow unique validation done successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5014', '{0} is unique.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5015', 'Successful checking of flow: ', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5016', '{0} is not unique.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6015', 'Flow unique check validation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6016', 'Flow unique check validation failed for flow name : {0} due to database error',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5017', 'Flow deleted successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5018', 'Flow deleted successfully for flow id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6017', 'Flow deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6018', 'Flow deletion failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9019', 'Flow deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9020', 'Flow deletion failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5051', 'Form created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5052', 'Form created successfully with name : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9101', 'Form creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9102', 'Form name : {0} is not unique', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9103', 'Form creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9104', 'Form creation failed for name : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6101', 'Form creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6102', 'Form creation failed for name : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5053', 'Form updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5054', 'Form updated successfully for name : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9105', 'Form update is unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9106', 'Form cannot be update.Form name : {0} is not unique.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9107', 'Form update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9108', 'Form update failed for name : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6103', 'Form update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6104', 'Form update failed for name : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5055', 'Form retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5056', 'Form retrieved successfully for Id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9109', 'Form retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9110', 'Form retrieved for Id : {0} due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6105', 'Form retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6106', 'Form retrieval failed for Id : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5057', 'Form deleted successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5058', 'Form deleted successfully for Id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9111', 'Form deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9112', 'Form deletion failed for Id : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9113', 'Form deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9114', 'Form not available for Id : {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6107', 'Form deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6108', 'Form deletion failed for Id : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5101', 'Menu created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5102', 'Menu created successfully for Product Id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5103', 'Menu deleted successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5104', 'Menu deleted successfully for Product Id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5115', 'Menu updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5116', 'Menu updated successfully for Product Id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5107', 'No Menus are found', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5108', 'No Menus are found', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6201', 'Menu creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6202', 'Menu creation failed for Product Id : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9201', 'Menu creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9202', 'Menu creation failed for Product Id : {0} due to database error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5105', 'Menu retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5106', 'Menu retrieved successfully for Id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5109', 'Menu retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5110', 'No Menus available', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6203', 'Menu retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6204', 'Menu retrieved failed for Id : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9203', 'Menu retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9204', 'Menu retrieval failed for Id : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9205', 'Menu name is not unique', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9206', 'Menu name is already used in the warehouse', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9207', 'Menu deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9208', 'Menu deletion failed for menuId : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6205', 'Invalid data supplied', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6206', 'Invalid Menu Type supplied', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6207', 'Invalid : Menu Trigger Action', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6208', 'Menu Unique Name Check encountered DataBase error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6209', 'Menu Trigger Action retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6210', 'Menu Trigger Action encountered DataBase error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6211', 'Menu Unique Name check retrieve unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6212', 'Trigger Action retrieval successful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6213', 'Menu Trigger Action retrieve unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6217', 'Menu deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6218', 'Menu deletion failed for menu Id : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '9209', 'Menu Unique Name Check encountered  error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9210', 'Menu Trigger Action retrieval unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9211', 'Menu Trigger Action encountered  error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9213', 'Menu Unique Name check retrieval unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5151', 'Layer list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5152', 'Layer list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6301', 'Layer list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6302', 'Layer list retrieval failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5153', 'User added to Layer successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5154', 'User added to Layer successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9251', 'User and Layer mapping failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9252', 'User and Layer mapping failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6303', 'User and Layer mapping failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6304', 'User and Layer mapping failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6913', 'User is already mapped to a Layer', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6914', 'User is already mapped to {0} Layer', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9954', 'User role mapping actionType is invalid', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9955', 'User role mapping actionType is invalid', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5656', 'User sucessfully mapped as a super user', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5657', 'User sucessfully mapped as a super user', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6915', 'User is already mapped as a super user', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6916', 'User is already mapped as a super user', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5181', 'User and Layer association deleted successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5182', 'User and Layer association deleted successfully', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5155', 'User and Layer association updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5156', 'User and Layer association updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9253', 'User and Layer mapping update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9254', 'User and Layer mapping update failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6305', 'User and Layer mapping update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6306', 'User and Layer mapping update failed due to  database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5157', 'User Role retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5158', 'User Role retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9255', 'User Role retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9256', 'User Role retrieval failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5159', 'User Permissions retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5160', 'User Permissions retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9257', 'User Permissions retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9258', 'Permissions retrieval failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'en-US', '5201', 'Application Config retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5202', 'Application Config retrieved successfully for : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9301', 'Application Config retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9302', 'Application Config retrieval failed for : {0} due to system error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6351', 'Application Config retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6352', 'Application Config retrieval failed for : {0} due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5203', 'Dashboard flows retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5204', 'Dashboard flows retrieved successfully for : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9303', 'Dashboard flows retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9304', 'Dashboard flows retrieval failed for : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6353', 'Dashboard flows retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6354', 'Dashboard flows retrieval failed for : {0} due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5205', 'Product Configuration retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5206', 'ProductConfig Id retrieved successfully for : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9305', 'Product Configuration retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9306', 'Product Configuration retrieval failed for : {0} due to system error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6355', 'Product Configuration retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6356', 'Product Configuration retrieval failed for : {0} due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5251', 'Custom Component created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5252', 'Custom Component created successfully with id : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9401', 'Custom Component creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9402', 'Custom Component creation failed for : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6401', 'Custom Component creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6402', 'Custom Component creation failed for : {0} due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5253', 'Custom Component list retrieved Successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5254', 'Custom Component list retrieved Successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9403', 'Custom Component retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9404', 'Custom Component retrieval failed for : {0} due to system error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6403', 'Custom Component retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6404', 'Custom Component retrieval failed for : {0} due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5255', 'Custom Component updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5256', 'Custom Component updated successfully for id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9405', 'Custom Component update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9406', 'Custom Component update failed for : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6405', 'Custom Component update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6406', 'Custom Component update failed for : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5257', 'Custom Component deleted successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5258', 'Custom Component deleted successfully for id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9407', 'Custom Component deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9408', 'Custom Component deletion failed for : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6407', 'Custom Component deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6408', 'Custom Component deletion failed for : {0} due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5299', 'Registry list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5300', 'Registry list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9501', 'Registry list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9502', 'Registry list retrieval failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6501', 'Registry list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6502', 'Registry list retrieval failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5301', 'Registry retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5302', 'Registry retrieved successfully for id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9503', 'Registry retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9504', 'Registry retrieval failed for Id : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6503', 'Registry retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6504', 'Registry retrieval failed for Id : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5303', 'Registry created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5304', 'Registry created for id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9505', 'Registry creation is unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9506',
        'Registry creation failed. Registry name {0} is not unique for the given API type and current layer',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9507', 'Registry creation is unsuccessful.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9508', 'Registry creation failed for : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6505', 'Registry creation is unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6506', 'Registry creation failed for : {0}', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5305', 'Registry updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5306', 'Registry updated for id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9509', 'Registry update is unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9510',
        'Registry update failed. Registry name {0} is not unique for the given API type and current layer', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9511', 'Registry update is unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9512', 'Registry update failed for : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6507', 'Registry update is unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6508', 'Registry update failed for : {0}', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5307', 'Registry deleted successfully for action {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5308', 'Registry deleted for id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9513', 'Registry deletion is unsuccessful for action {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9514', 'Registry deletion failed for : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6509', 'Registry deletion is unsuccessful for action {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6510', 'Registry deletion failed for : {0}', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5309', 'API Master Registry list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5310', 'API Master Registry list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9515', 'API Master Registry list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9516', 'API Master Registry list retrieval failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6511', 'API Master Registry list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6512', 'API Master Registry list retrieval failed due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5311', 'API Registry retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5312', 'API Registry retrieved successfully for registry Id : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9517', 'API Registry retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9518', 'API Registry retrieval failed for registry Id : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6513', 'API Registry retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6514', 'API Registry retrieval failed for registry Id : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5313', 'APIs retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5314', 'APIs retrieved successfully for id  : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9519', 'APIs retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9520', 'APIs retrieval failed for Id : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6515', 'APIs retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6516', 'APIs retrieval failed for Id : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

--validateUser
       (NEWID(), 'en-US', '5161', 'User validated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5162', 'User is a valid user', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9259', 'User validation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9260', 'User ID is invalid', 'INTERNAL', 'SUPER', GETDATE()),

--fetchAllForms
       (NEWID(), 'en-US', '5065', 'Form list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5066', 'Form list retrieved successfully for flow Id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9125', 'Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9126', 'Form list retrieval failed for Id : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6115', 'Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6116', 'Form list retrieval failed for Id : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

--createDefaultForm
       (NEWID(), 'en-US', '5063', 'Form set as default successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5064', 'Form {0} set as default successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6113', 'Form set as default failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6114', 'Form set as default failed for id : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9123', 'Form set as default failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9124', 'Form set as default failed for id : {0} due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),

--fetchOrphanForms
       (NEWID(), 'en-US', '5059', 'Orphan Form list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5060', 'Orphan Form list retrieved successfully for flow Id : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6109', 'Orphan Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6110', 'Orphan Form list retrieval failed for Id : {0} due to database error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9119', 'Orphan Form list retrieval is unsuccessful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9120', 'Orphan Form list retrieval is unsuccessful for Id : {0} encountered Database error.',
        'INTERNAL', 'SUPER', GETDATE()),

--fetchUnpublishForms
       (NEWID(), 'en-US', '5061', 'Unpublished Form list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5062', 'Unpublished Form list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9121', 'Unpublished Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9122', 'Unpublished Form list retrieval failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6111', 'Unpublished Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6112', 'Unpublished Form list retrieval failed due to Database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5069', 'Form list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5070', 'Form list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6119', 'Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6120', 'Form list retrieval failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9149', 'Form retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9150', 'Form retrieval failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5351', 'Registry created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5352', 'Registry created successfully with registryId : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '9601', 'Registry creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9602', 'Registry creation failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '9956', 'Registry type is invalid', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9957', 'Registry type is invalid', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '6601', 'Registry creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6602', 'Registry creation failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5353', 'Registry updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5354', 'Registry updated successfully for registry Id : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '9603', 'Registry update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9604', 'Registry update failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '6603', 'Registry update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6604', 'Registry update failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5359', 'APIs override successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5360', 'APIs override successfully with below data', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '9605', 'APIs override failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9606', 'APIs override failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '6607', 'APIs override failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6608', 'APIs override failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5355', 'Registry name is unique', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5356', '{0} is unique', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '6917', 'Registry name is not unique', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6918',
        'Registry creation failed. Registry name {0} is not unique for the given API type and current layer',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5400', 'Resource bundle retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5401', 'Resource bundle entries retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '6605', 'Registry name unique check failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6606', 'Registry name : {0} unique check failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5163', 'User Layer retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5164', 'User Layer retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6309', 'User Layer retrival failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6310', 'User Layer retrival failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9261', 'User layer retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9262', 'User is not assigned with any layer', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '9807', 'Authentication failed for channel : {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9808', 'Authentication failed due to invalid channel : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9021', 'Flow update failed for flow name : {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9022', 'Flow update failed due to invalid action type : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5019', 'Flow validated successfully for flow id : {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5020',
        'Unpublished forms can be present in this flow. Still Do you want publish form flow? ', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5021', 'Flow published successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5022', 'Flow published successfully with flow id : {0}', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'en-US', '9023', 'Flow retrieval unsuccessful.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9024', 'Flow retrieval unsuccessful as the flow with flowId: {0} is disabled.', 'INTERNAL',
        'SUPER', GETDATE()),


       (NEWID(), 'en-US', '5207', 'Defaultflow and Homeflow retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5208', 'Defaultflow and Homeflow retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9307', 'Defaultflow and Homeflow retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9308', 'Defaultflow and Homeflow retrieval failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6357', 'Defaultflow and Homeflow retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6358', 'Defaultflow and Homeflow retrieval failed due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '6359', 'Home Flow not found', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6360', 'Home flow is disabled', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6361', 'Home flow is not published yet', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6362', 'Default form not found for home flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6363', 'Default form is disabled for home flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6364', 'Default form is not yet published for home flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6365', 'Default flow is disabled', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6366', 'Default flow is not published yet', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6367', 'Default form not found for default flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6368', 'Default form is disabled for default flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6369', 'Default form is not yet published for default flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6372', 'Default Flow not found', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6373', 'Default form of default flow is deleted', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6374', 'Default from of home flow is deleted', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'en-US', '5023', 'Flow disabled successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5024', 'Flow disabled successfully with flowId :{0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9025', 'Disable flow failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9026', 'Disable flow failed as the flow is the Default Flow.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9027', 'Disable flow failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9028', 'Disable flow failed as the flow is the Home Flow.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9029', 'Disable flow failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9030', 'Disable flow failed as the request type is invalid.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9031', 'disable flow failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9032', 'Disable flow failed as there are Forms under this flow.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6019', 'Disable flow failed.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6020', 'Disable flow failed due to database error.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9033', 'Disable flow failed.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9034', 'Disable flow failed due to system error.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9160', 'Form is not published yet', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9161', 'Form is disabled', 'INTERNAL', 'SUPER', GETDATE()),


--appconfig Creations

       (NEWID(), 'en-US', '5601', 'Context configurations created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5602', 'Context configurations created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5607', 'Global configurations created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5608', 'Global configurations created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5609', 'Application configurations created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5610', 'Application configurations created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5611', 'Internal configurations created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5612', 'Internal configurations created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9901', 'Application configurations creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9902', 'Application configurations creation failed due to system  error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6901', 'Application configurations creation failed due to database error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6902', 'Application configurations creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6907', 'Application configuration creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6908', 'Application configuration already exists for the supplied config type and name',
        'INTERNAL', 'SUPER', GETDATE()),

--fetch appconfig based on type and name

       (NEWID(), 'en-US', '5603', 'Application configuration retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5604', 'Application configuration retrieved successfully for Type and Name : {0} {1}',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9903', 'Application configuration retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9904', 'Application configuration retrieval failed for : {0} {1} due to system error',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6903', 'Application configuration retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6904', 'Application configuration retrieval failed for : {0} {1} due to database error',
        'INTERNAL', 'SUPER', GETDATE()),

--fetch appconfig based on type

       (NEWID(), 'en-US', '5605', 'Application configuration list retrieved successfully', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5606', 'Application configuration list retrieved successfully for type : {0}', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9905', 'Application configuration list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9906', 'Application configuration list retrieval failed for Type : {0} due to system error',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6905', 'Application configuration list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6906',
        'Application configuration list retrieval failed for Type : {0} due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

--autocomplete appconfig based on type

       (NEWID(), 'en-US', '5402', 'Application configuration retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5403',
        'Application configuration retrieved successfully based on ConfigType Type : {0} search', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9703', 'Application configuration search failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9704', 'Application configuration search failed for Type : {0} due to system error',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6703', 'Application configuration search failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6704', 'Application configuration search failed for config type : {0} due to database error',
        'INTERNAL', 'SUPER', GETDATE()),

--update appconfig Value


       (NEWID(), 'en-US', '5613', 'Application configuration values updated successfully', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5614', 'Application configuration values updated successfully', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6909', 'Application configuration values update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6910', 'Application configuration values update failed due to database error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9909', 'Application configuration values update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9910', 'Application configuration values update failed due to system error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9913', 'Application configuration values update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9914',
        'Application configuration values update failed as the name and type combination does not exist', 'INTERNAL',
        'SUPER', GETDATE()),

-- ClearConfig Value
       (NEWID(), 'en-US', '5615', 'Application configuration values cleared successfully', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5616', 'Application configuration values cleared successfully', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6911', 'Clearing Application configuration values failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6912', 'Clearing ConfigValue failed due to database error.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9911', 'Clearing Application configuration values failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9912', 'Clearing Application configuration values failed due to system error', 'INTERNAL',
        'SUPER', GETDATE()),

--Product API invocation
       (NEWID(), 'en-US', '5651', 'Product API invocation successful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5652', 'Product API invocation successful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9950', 'Product API invocation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9951', 'Product API invocation unsuccessful due to {0}', 'INTERNAL', 'SUPER', GETDATE()),

--fetchUnpublishOrphanForms
       (NEWID(), 'en-US', '5071', 'Unpublished Forms retrieval successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5072', 'Unpublished Forms retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5073', 'Orphan Forms retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5074', 'Orphan Forms retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9151', 'Form retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9152', 'Form retrieval failed as the request type is invalid', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9153', 'Form retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9154', 'Form retrieval failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6121', 'Form retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6122', 'Form retrieval failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),

--warehouselistapi
       (NEWID(), 'en-US', '5170', 'Warehouse list is retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5171', 'Warehouse details are fetched', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6370', 'User Id is not present', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6371', 'Warehouse retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9165', 'Warehouse list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9166', 'Warehouse list encountered error.', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '6123', 'Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6124', 'Form list retrieval failed as the Flow with flow id : {0} is not available',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6125', 'Form retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6126', 'No Forms available under the Flow', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'en-US', '9170', 'Invalid Action type', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9171', 'Form Action type is invalid: {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9172', 'Flow Action type is invalid: {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9133', 'Invalid identifier', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9134', 'Form Identifier is invalid: {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9174', 'Flow Identifier is invalid: {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9135', 'Flow has default form already', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9136', 'Flow has default form already. Do you want to override: {0}', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9137', 'Form cannot be disabled', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9138', 'Default form cannot be disabled', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9145', 'Form validated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9146', 'Do you want to publish form ?', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9173', 'Form is disabled. Do you still want to publish the form?', 'INTERNAL', 'SUPER',
        GETDATE()),

--publish forms
       (NEWID(), 'en-US', '5067', 'Form published successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5068', 'Form Published successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6117', 'Form publish failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6118', 'Form publish failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9147', 'Form publish failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9148', 'Form publish failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '9414', 'Custom Control creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9415', 'Custom Control name is not unique : {0}', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'en-US', '9080', 'Flow deletion failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9081', 'Default FormFlow cannot be deleted', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9082', 'Home FormFlow cannot be deleted.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9083',
        'Formflow deletion will delete the forms inside it. Menus associated to the Formflow should be updated manually',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9084', 'Formflow validated successfully', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5029', 'Flow Extended successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5030', 'Flow Extended successfully with flowId : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5047', 'Flow cloned successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5048', 'Flow cloned successfully with flowId : {0}', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '9046', 'Flow Extension creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9047', '{0}, Extended Flow Name is not unique', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6025', 'Flow Extension creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6026', 'Flow Extension creation failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '6033', 'Cloning of flow failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6034', 'Cloning of flow failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '9048', 'Flow Extension creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9049', 'Flow Extension creation failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9066', 'Cloning of flow failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9067', 'Cloning of flow failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9070', 'Flow version creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9071', 'Flow version created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9072', 'Flow version creation failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9073', 'Flow version creation failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5035', 'Form Extended successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5036', 'Form Extended successfully with formId : {0} within the same flow', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5037', 'Form Extended successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5038', 'Form Extended successfully with formId : {0} in the different flow', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9054', 'Extending component Unsuccessful.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9055', 'Extending component Unsuccessful as the request type is Invalid.', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9056', 'Form Extension creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9057', '{0}, Extended form Name is not unique', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5031', 'Flow Extension retrieved successfully.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5032', 'Flow Extension difference retrieved successfully for parent : {0}', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5033', 'Flow Extension cannot retrieved successfully.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5034', 'Flow Extension difference cannot be retrieved for parent : {0}', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9050', 'Flow Extension history retrieval is encountered error.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9051', 'Flow Extension retrieval is unsuccessful for extension type : {0}', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6027', 'Flow Extension history retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6028', 'Flow Extension history retrieval failed for object id : {0} due to database error',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9052', 'Flow Extension history retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9053', 'Flow Extension history retrieval failed for object id : {0} due to system error',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5452', 'Resource bundle retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5453', 'Resource bundles based on locale and type retrieved successfully', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9803', 'Resource bundle retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9804', 'Resource bundle retrieval failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6803', 'Resource bundle retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6804', 'Resource bundle retrieval failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5450', 'Resource bundle created successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5451', 'Resource bundle created successfully with id : {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6801', 'Resource bundle creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6802', 'Resource bundle creation failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9801', 'Resource bundle creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9802', 'Resource bundle creation failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '6807', 'Resource bundle creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6808', 'Resource bundles based on locale and key pair is already exist', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '6809', 'Resource bundle creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6810', 'Resource bundles resource key is not allowed to empty string', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '9129', 'Invalid Identifier', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9130', 'Invalid Identifier passed while marking default', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9128', 'Flow has already default form associated', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9127', 'Creating default form failed', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5653', 'File imported successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5654', 'All Files Imported Successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5659', 'Imported data saved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5660', 'All files imported are saved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5661', 'All files imported are published successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9952', 'File import failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9953', 'File import failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9960', 'File import failed for {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9961', 'File Import failed for {0}. Empty Upload file is not allowed', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9967', 'Data save failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9968', 'Imported data failed during save due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9969', 'Import data check violated', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9970', 'Rules to import data violated. {0}', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9971', 'File Import should be from root folder', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9972',
        'Nested folder file import is not allowed, File import should be from root folder. {0}', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5701', 'Comparison disallowed for {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5702',
        'This {0} requested for comparison is newly added in the extended form and doesn’t exist in the parent form',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5709', 'Comparison failed for {0}.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5710', 'Parent field is missing to compare.', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5039', 'Flow basic details retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5040', 'Flow basic details retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9058', 'Flow basic details retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9059', 'Flow basic details retrieval failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6029', 'Flow basic details retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6030', 'Flow basic details retrieval failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5801', 'Locale updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5802', 'Locale updated successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5803', 'Invalid Locale supplied.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5804', 'Invalid Locale supplied.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5805', 'Locale update encountered database error.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5806', 'Locale update encountered database error.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5807', 'Locale update failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5808', 'Locale update failed due to database error', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5500', 'Flow is deleted', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5502', 'Form is deleted.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5504', 'Default form is not available for this flow', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5506', 'Custom Control is deleted', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'en-US', '5111', 'Invalid product name', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5112', 'Warehouse name is not valid', 'INTERNAL', 'SUPER', GETDATE()),


--fetchOrphanForms
       (NEWID(), 'en-US', '5080', 'Custom Component Form list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5081', 'Custom Component Form list retrieved successfully for flow Id : {0}', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6150', 'Custom Component Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6151', 'Custom Component Form list retrieval failed for Id : {0} due to database error',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9155', 'Custom Component Form list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9156', 'Custom Component Form list retrieval failed for Id : {0} due to system error.',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5025', 'Logout successful.', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5026', 'User Logged out Successfully', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '6214', 'Invalid Request', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6215', 'Product Name or Default warehouse is mandatory', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '9409', 'Custom Control validation successful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9410', 'Custom Control is linked with forms , Do you still want to delete ?', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9411', 'Custom Control validation successful', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9412', 'Do you want to delete ?', 'INTERNAL', 'SUPER', GETDATE()),

--fetch hotkeys

       (NEWID(), 'en-US', '5703', 'Hot key list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5704', 'Hot key list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9958', 'Hot key list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9959', 'Hot key list retrieval failed due to system error', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6919', 'Hot key list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6920', 'Hot key list retrieval failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),


--fetch user language codes

       (NEWID(), 'en-US', '5705', 'User language code list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5706', 'User language code list retrieved successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9962', 'User language code list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9963', 'User language code list retrieval failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6921', 'User language code list retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6922', 'User language code list retrieval failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),

-- Pre and Post processor file upload
       (NEWID(), 'en-US', '9964', 'Both pre and post processor files are empty', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9965', '{0} processor file upload failed due to system error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6923', '{0} processor file upload failed due to database error', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5707', '{0} processor file uploaded successfully', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9966', 'API has already {0} processor file', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '5708', 'API do not have any {0} processor file', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5041', 'Flow with Default Form details retrieval Successful.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5042', 'Flow with Default Form details retrieved Successfully.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '9060', 'Flow with Default Form details retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9061', 'Flow with Default Form details retrieval failed as Flow Permissions are Invalid',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9062', 'Flow with Default Form details retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9063', 'Flow with Default Form details retrieval failed due to system error', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9064', 'Form details retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '9065', 'Form details retrieval Unsuccessful as the Flow with id : {0} has No Permissions.',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6031', 'Flow with Default Form details retrieval failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6032', 'Flow with Default Form details retrieval failed due to database error', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'en-US', '5043', 'Tenant session inactive period retrival Successful.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5044', 'Tenant session inactive period retrived Successfully.', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '5045', 'Tenant session inactive period modified Successful.', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '5046', 'Tenant session inactive period modified Successfully.', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'en-US', '6609', 'Registry creation failed', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'en-US', '6610',
        'Uploading Swagger file is either in Invalid Format or Open API Version is not supported ', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'en-US', '6611', 'Third party API - Host is mandatory', 'INTERNAL', 'SUPER', GETDATE())
GO

-- Localized status text fr-fr
INSERT INTO resource_bundle (uid, locale, resource_key, resource_value, type, created_by, creation_date)
VALUES (NEWID(), 'fr-FR', '5001', 'Login authentication success_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5002', 'User logged in is authenticated_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9001', 'Login authentication failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9002', 'User access is not authorized_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9043', 'Login authentication failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9044', 'User authentication cannot be completed Authentication provider not responding._FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6003', 'Login authentication failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6004', 'Authentication process failed to set/validate credentials_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9810', '{0} version {1} is not compatible with {2} version {3}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5003', 'Flow created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5004', 'Flow created successfully with flowId : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9003', 'Flow creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9004', '{0} is not unique_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6005', 'Flow creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6006', 'Flow creation failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9005', 'Flow creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9006', 'Flow creation failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5005', 'Flow updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5006', 'Flow updated successfully for id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6007', 'Flow update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6008', 'Flow update failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9007', 'Flow update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9008', '{0} is not unique_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9009', 'Flow update failed.Form name not unique_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9010', '{0} already exists. Form name is not unique for the flow._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9011', 'Flow update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9012', 'Flow update failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5007', 'Flow retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5008', 'Flow retrieved successfully for id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6009', 'Flow not found_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6010', 'Flow not available for id : {0} _FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9013', 'Flow not found._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9014', 'Flow not available for id : {0} _FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9163', 'Flow is not published yet_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9164', 'Flow is disabled_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9180', 'Default form is disabled for this flow_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9181', 'Default form is not published yet for this flow_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9182', 'Flow has no default forms_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9183', 'Form not found_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5009', 'Flow status count retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5010', 'Flow status count retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6011', 'Flow status count retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6012', 'Flow status count retrieval failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9015', 'Flow status count retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9016', 'Flow status count retrieval failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5011', 'Flow list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5012', 'Flow list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6013', 'Flow retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6014', 'Flow retrieval failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9017', 'Flow retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9018', 'Flow retrieval failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5013', 'Flow unique validation done successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5014', '{0} is unique._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5015', 'Successful checking of flow: _FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5016', '{0} is not unique._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6015', 'Flow unique check validation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6016', 'Flow unique check validation failed for flow name : {0} due to database error_FR',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5017', 'Flow deleted successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5018', 'Flow deleted successfully for flow id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6017', 'Flow deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6018', 'Flow deletion failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9019', 'Flow deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9020', 'Flow deletion failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5051', 'Form created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5052', 'Form created successfully with name : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9101', 'Form creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9102', 'Form name : {0} is not unique_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9103', 'Form creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9104', 'Form creation failed for name : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6101', 'Form creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6102', 'Form creation failed for name : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5053', 'Form updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5054', 'Form updated successfully for name : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9105', 'Form update is unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9106', 'Form cannot be update.Form name : {0} is not unique._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9107', 'Form update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9108', 'Form update failed for name : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6103', 'Form update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6104', 'Form update failed for name : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5055', 'Form retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5056', 'Form retrieved successfully for Id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9109', 'Form retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9110', 'Form retrieved for Id : {0} due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6105', 'Form retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6106', 'Form retrieval failed for Id : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5057', 'Form deleted successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5058', 'Form deleted successfully for Id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9111', 'Form deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9112', 'Form deletion failed for Id : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9113', 'Form deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9114', 'Form not available for Id : {0}._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6107', 'Form deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6108', 'Form deletion failed for Id : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5101', 'Menu created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5102', 'Menu created successfully for Product Id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5103', 'Menu deleted successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5104', 'Menu deleted successfully for Product Id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5115', 'Menu updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5116', 'Menu updated successfully for Product Id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5107', 'No Menus are found_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5108', 'No Menus are found_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6201', 'Menu creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6202', 'Menu creation failed for Product Id : {0} due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9201', 'Menu creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9202', 'Menu creation failed for Product Id : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5105', 'Menu retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5106', 'Menu retrieved successfully for Id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5109', 'Menu retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5110', 'No Menus available_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6203', 'Menu retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6204', 'Menu retrieved failed for Id : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9203', 'Menu retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9204', 'Menu retrieval failed for Id : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9205', 'Menu name is not unique_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9206', 'Menu name is already used in the warehouse_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9207', 'Menu deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9208', 'Menu deletion failed for menuId : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6205', 'Invalid data supplied_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6206', 'Invalid Menu Type supplied_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6207', 'Invalid : Menu Trigger Action_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6208', 'Menu Unique Name Check encountered DataBase error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6209', 'Menu Trigger Action retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6210', 'Menu Trigger Action encountered DataBase error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6211', 'Menu Unique Name check retrieve unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6212', 'Trigger Action retrieval successful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6213', 'Menu Trigger Action retrieve unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6217', 'Menu deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6218', 'Menu deletion failed for menu Id : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '9209', 'Menu Unique Name Check encountered  error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9210', 'Menu Trigger Action retrieval unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9211', 'Menu Trigger Action encountered  error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9213', 'Menu Unique Name check retrieval unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5151', 'Layer list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5152', 'Layer list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6301', 'Layer list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6302', 'Layer list retrieval failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5153', 'User added to Layer successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5154', 'User added to Layer successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9251', 'User and Layer mapping failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9252', 'User and Layer mapping failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6303', 'User and Layer mapping failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6304', 'User and Layer mapping failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6913', 'User is already mapped to a Layer_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6914', 'User is already mapped to {0} Layer_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9954', 'User role mapping actionType is invalid_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9955', 'User role mapping actionType is invalid_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5656', 'User sucessfully mapped as a super user_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5657', 'User sucessfully mapped as a super user_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6915', 'User is already mapped as a super user_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6916', 'User is already mapped as a super user_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5181', 'User and Layer association deleted successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5182', 'User and Layer association deleted successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5155', 'User and Layer association updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5156', 'User and Layer association updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9253', 'User and Layer mapping update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9254', 'User and Layer mapping update failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6305', 'User and Layer mapping update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6306', 'User and Layer mapping update failed due to  database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5157', 'User Role retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5158', 'User Role retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9255', 'User Role retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9256', 'User Role retrieval failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5159', 'User Permissions retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5160', 'User Permissions retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9257', 'User Permissions retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9258', 'Permissions retrieval failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),


       (NEWID(), 'fr-FR', '5201', 'Application Config retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5202', 'Application Config retrieved successfully for : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9301', 'Application Config retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9302', 'Application Config retrieval failed for : {0} due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6351', 'Application Config retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6352', 'Application Config retrieval failed for : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5203', 'Dashboard flows retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5204', 'Dashboard flows retrieved successfully for : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9303', 'Dashboard flows retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9304', 'Dashboard flows retrieval failed for : {0} due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6353', 'Dashboard flows retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6354', 'Dashboard flows retrieval failed for : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5205', 'Product Configuration retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5206', 'ProductConfig Id retrieved successfully for : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9305', 'Product Configuration retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9306', 'Product Configuration retrieval failed for : {0} due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6355', 'Product Configuration retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6356', 'Product Configuration retrieval failed for : {0} due to database error_FR',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5251', 'Custom Component created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5252', 'Custom Component created successfully with id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9401', 'Custom Component creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9402', 'Custom Component creation failed for : {0} due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6401', 'Custom Component creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6402', 'Custom Component creation failed for : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5253', 'Custom Component list retrieved Successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5254', 'Custom Component list retrieved Successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9403', 'Custom Component retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9404', 'Custom Component retrieval failed for : {0} due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6403', 'Custom Component retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6404', 'Custom Component retrieval failed for : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5255', 'Custom Component updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5256', 'Custom Component updated successfully for id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9405', 'Custom Component update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9406', 'Custom Component update failed for : {0} due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6405', 'Custom Component update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6406', 'Custom Component update failed for : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5257', 'Custom Component deleted successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5258', 'Custom Component deleted successfully for id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9407', 'Custom Component deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9408', 'Custom Component deletion failed for : {0} due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6407', 'Custom Component deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6408', 'Custom Component deletion failed for : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5299', 'Registry list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5300', 'Registry list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9501', 'Registry list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9502', 'Registry list retrieval failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6501', 'Registry list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6502', 'Registry list retrieval failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5301', 'Registry retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5302', 'Registry retrieved successfully for id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9503', 'Registry retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9504', 'Registry retrieval failed for Id : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6503', 'Registry retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6504', 'Registry retrieval failed for Id : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5303', 'Registry created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5304', 'Registry created for id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9505', 'Registry creation is unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9506',
        'Registry creation failed. Registry name {0} is not unique for the given API type and current layer_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9507', 'Registry creation is unsuccessful._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9508', 'Registry creation failed for : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6505', 'Registry creation is unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6506', 'Registry creation failed for : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5305', 'Registry updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5306', 'Registry updated for id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9509', 'Registry update is unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9510',
        'Registry update failed. Registry name {0} is not unique for the given API type and current layer_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9511', 'Registry update is unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9512', 'Registry update failed for : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6507', 'Registry update is unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6508', 'Registry update failed for : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5307', 'Registry deleted successfully for action {0}._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5308', 'Registry deleted for id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9513', 'Registry deletion is unsuccessful for action {0}._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9514', 'Registry deletion failed for : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6509', 'Registry deletion is unsuccessful for action {0}._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6510', 'Registry deletion failed for : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5309', 'API Master Registry list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5310', 'API Master Registry list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9515', 'API Master Registry list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9516', 'API Master Registry list retrieval failed due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6511', 'API Master Registry list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6512', 'API Master Registry list retrieval failed due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5311', 'API Registry retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5312', 'API Registry retrieved successfully for registry Id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9517', 'API Registry retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9518', 'API Registry retrieval failed for registry Id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6513', 'API Registry retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6514', 'API Registry retrieval failed for registry Id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5313', 'APIs retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5314', 'APIs retrieved successfully for id  : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9519', 'APIs retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9520', 'APIs retrieval failed for Id : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6515', 'APIs retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6516', 'APIs retrieval failed for Id : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

--validateUser
       (NEWID(), 'fr-FR', '5161', 'User validated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5162', 'User is a valid user_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9259', 'User validation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9260', 'User ID is invalid_FR', 'INTERNAL', 'SUPER', GETDATE()),

--fetchAllForms
       (NEWID(), 'fr-FR', '5065', 'Form list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5066', 'Form list retrieved successfully for flow Id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9125', 'Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9126', 'Form list retrieval failed for Id : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6115', 'Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6116', 'Form list retrieval failed for Id : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

--createDefaultForm
       (NEWID(), 'fr-FR', '5063', 'Form set as default successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5064', 'Form {0} set as default successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6113', 'Form set as default failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6114', 'Form set as default failed for id : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9123', 'Form set as default failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9124', 'Form set as default failed for id : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

--fetchOrphanForms
       (NEWID(), 'fr-FR', '5059', 'Orphan Form list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5060', 'Orphan Form list retrieved successfully for flow Id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6109', 'Orphan Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6110', 'Orphan Form list retrieval failed for Id : {0} due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9119', 'Orphan Form list retrieval is unsuccessful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9120',
        'Orphan Form list retrieval is unsuccessful for Id : {0} encountered Database error._FR', 'INTERNAL', 'SUPER',
        GETDATE()),

--fetchUnpublishForms
       (NEWID(), 'fr-FR', '5061', 'Unpublished Form list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5062', 'Unpublished Form list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9121', 'Unpublished Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9122', 'Unpublished Form list retrieval failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6111', 'Unpublished Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6112', 'Unpublished Form list retrieval failed due to Database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5069', 'Form list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5070', 'Form list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6119', 'Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6120', 'Form list retrieval failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9149', 'Form retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9150', 'Form retrieval failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5351', 'Registry created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5352', 'Registry created successfully with registryId : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '9601', 'Registry creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9602', 'Registry creation failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '9956', 'Registry type is invalid_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9957', 'Registry type is invalid_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6601', 'Registry creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6602', 'Registry creation failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5353', 'Registry updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5354', 'Registry updated successfully for registry Id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '9603', 'Registry update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9604', 'Registry update failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6603', 'Registry update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6604', 'Registry update failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5359', 'APIs override successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5360', 'APIs override successfully with below data_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '9605', 'APIs override failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9606', 'APIs override failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6607', 'APIs override failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6608', 'APIs override failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5355', 'Registry name is unique_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5356', '{0} is unique_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6917', 'Registry name is not unique_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6918',
        'Registry creation failed. Registry name {0} is not unique for the given API type and current layer_FR',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5400', 'Resource bundle retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5401', 'Resource bundle entries retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6605', 'Registry name unique check failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6606', 'Registry name : {0} unique check failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5163', 'User Layer retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5164', 'User Layer retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6309', 'User Layer retrival failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6310', 'User Layer retrival failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9261', 'User layer retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9262', 'User is not assigned with any layer_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '9807', 'Authentication failed for channel : {0}._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9808', 'Authentication failed due to invalid channel : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9021', 'Flow update failed for flow name : {0}._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9022', 'Flow update failed due to invalid action type : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5019', 'Flow validated successfully for flow id : {0}._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5020',
        'Unpublished forms can be present in this flow. Still Do you want publish form flow? _FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5021', 'Flow published successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5022', 'Flow published successfully with flow id : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'fr-FR', '9023', 'Flow retrieval unsuccessful._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9024', 'Flow retrieval unsuccessful as the flow with flowId: {0} is disabled._FR',
        'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'fr-FR', '5207', 'Defaultflow and Homeflow retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5208', 'Defaultflow and Homeflow retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9307', 'Defaultflow and Homeflow retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9308', 'Defaultflow and Homeflow retrieval failed due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6357', 'Defaultflow and Homeflow retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6358', 'Defaultflow and Homeflow retrieval failed due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6359', 'Home Flow not found_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6360', 'Home flow is disabled_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6361', 'Home flow is not published yet_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6362', 'Default form not found for home flow_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6363', 'Default form is disabled for home flow_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6364', 'Default form is not yet published for home flow_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6365', 'Default flow is disabled_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6366', 'Default flow is not published yet_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6367', 'Default form not found for default flow_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6368', 'Default form is disabled for default flow_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6369', 'Default form is not yet published for default flow_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6372', 'Default Flow not found_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6373', 'Default form of default flow is deleted_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6374', 'Default from of home flow is deleted_FR', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'fr-FR', '5023', 'Flow disabled successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5024', 'Flow disabled successfully with flowId :{0}._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9025', 'Disable flow failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9026', 'Disable flow failed as the flow is the Default Flow._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9027', 'Disable flow failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9028', 'Disable flow failed as the flow is the Home Flow._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9029', 'Disable flow failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9030', 'Disable flow failed as the request type is invalid._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9031', 'disable flow failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9032', 'Disable flow failed as there are Forms under this flow._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6019', 'Disable flow failed._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6020', 'Disable flow failed due to database error._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9033', 'Disable flow failed._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9034', 'Disable flow failed due to system error._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9160', 'Form is not published yet_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9161', 'Form is disabled_FR', 'INTERNAL', 'SUPER', GETDATE()),


--appconfig Creations

       (NEWID(), 'fr-FR', '5601', 'Context configurations created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5602', 'Context configurations created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5607', 'Global configurations created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5608', 'Global configurations created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5609', 'Application configurations created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5610', 'Application configurations created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5611', 'Internal configurations created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5612', 'Internal configurations created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9901', 'Application configurations creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9902', 'Application configurations creation failed due to system  error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6901', 'Application configurations creation failed due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6902', 'Application configurations creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6907', 'Application configuration creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6908', 'Application configuration already exists for the supplied config type and name_FR',
        'INTERNAL', 'SUPER', GETDATE()),

--fetch appconfig based on type and name

       (NEWID(), 'fr-FR', '5603', 'Application configuration retrieved successfully_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5604', 'Application configuration retrieved successfully for Type and Name : {0} {1}_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9903', 'Application configuration retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9904', 'Application configuration retrieval failed for : {0} {1} due to system error_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6903', 'Application configuration retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6904', 'Application configuration retrieval failed for : {0} {1} due to database error_FR',
        'INTERNAL', 'SUPER', GETDATE()),

--fetch appconfig based on type

       (NEWID(), 'fr-FR', '5605', 'Application configuration list retrieved successfully_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5606', 'Application configuration list retrieved successfully for type : {0}_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9905', 'Application configuration list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9906',
        'Application configuration list retrieval failed for Type : {0} due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6905', 'Application configuration list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6906',
        'Application configuration list retrieval failed for Type : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

--autocomplete appconfig based on type

       (NEWID(), 'fr-FR', '5402', 'Application configuration retrieved successfully_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5403',
        'Application configuration retrieved successfully based on ConfigType Type : {0} search_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9703', 'Application configuration search failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9704', 'Application configuration search failed for Type : {0} due to system error_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6703', 'Application configuration search failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6704',
        'Application configuration search failed for config type : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

--update appconfig Value


       (NEWID(), 'fr-FR', '5613', 'Application configuration values updated successfully_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5614', 'Application configuration values updated successfully_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6909', 'Application configuration values update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6910', 'Application configuration values update failed due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9909', 'Application configuration values update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9910', 'Application configuration values update failed due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9913', 'Application configuration values update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9914',
        'Application configuration values update failed as the name and type combination does not exist_FR', 'INTERNAL',
        'SUPER', GETDATE()),

-- ClearConfig Value
       (NEWID(), 'fr-FR', '5615', 'Application configuration values cleared successfully_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5616', 'Application configuration values cleared successfully', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6911', 'Clearing Application configuration values failed_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6912', 'Clearing ConfigValue failed due to database error._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9911', 'Clearing Application configuration values failed_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9912', 'Clearing Application configuration values failed due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

--Product API invocation
       (NEWID(), 'fr-FR', '5651', 'Product API invocation successful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5652', 'Product API invocation successful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9950', 'Product API invocation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9951', 'Product API invocation unsuccessful due to {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),

--fetchUnpublishOrphanForms
       (NEWID(), 'fr-FR', '5071', 'Unpublished Forms retrieval successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5072', 'Unpublished Forms retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5073', 'Orphan Forms retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5074', 'Orphan Forms retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9151', 'Form retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9152', 'Form retrieval failed as the request type is invalid_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9153', 'Form retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9154', 'Form retrieval failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6121', 'Form retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6122', 'Form retrieval failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),

--warehouselistapi
       (NEWID(), 'fr-FR', '5170', 'Warehouse list is retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5171', 'Warehouse details are fetched_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6370', 'User Id is not present_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6371', 'Warehouse retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9165', 'Warehouse list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9166', 'Warehouse list encountered error._FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6123', 'Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6124', 'Form list retrieval failed as the Flow with flow id : {0} is not available_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6125', 'Form retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6126', 'No Forms available under the Flow_FR', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'fr-FR', '9170', 'Invalid Action type_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9171', 'Form Action type is invalid: {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9172', 'Flow Action type is invalid: {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9133', 'Invalid identifier_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9134', 'Form Identifier is invalid: {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9174', 'Flow Identifier is invalid: {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9135', 'Flow has default form already_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9136', 'Flow has default form already. Do you want to override: {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9137', 'Form cannot be disabled_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9138', 'Default form cannot be disabled_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9145', 'Form validated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9146', 'Do you want to publish form ?_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9173', 'Form is disabled. Do you still want to publish the form?_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

--publish forms
       (NEWID(), 'fr-FR', '5067', 'Form published successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5068', 'Form Published successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6117', 'Form publish failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6118', 'Form publish failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9147', 'Form publish failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9148', 'Form publish failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '9414', 'Custom Control creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9415', 'Custom Control name is not unique : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'fr-FR', '9080', 'Flow deletion failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9081', 'Default FormFlow cannot be deleted_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9082', 'Home FormFlow cannot be deleted._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9083',
        'Formflow deletion will delete the forms inside it. Menus associated to the Formflow should be updated manually_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9084', 'Formflow validated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5029', 'Flow Extended successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5030', 'Flow Extended successfully with flowId : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5047', 'Flow cloned successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5048', 'Flow cloned successfully with flowId : {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '9046', 'Flow Extension creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9047', '{0}, Extended Flow Name is not unique_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6025', 'Flow Extension creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6026', 'Flow Extension creation failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '6033', 'Cloning of flow failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6034', 'Cloning of flow failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '9048', 'Flow Extension creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9049', 'Flow Extension creation failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9066', 'Cloning of flow failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9067', 'Cloning of flow failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9070', 'Flow version creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9071', 'Flow version created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9072', 'Flow version creation failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9073', 'Flow version creation failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5035', 'Form Extended successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5036', 'Form Extended successfully with formId : {0} within the same flow_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5037', 'Form Extended successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5038', 'Form Extended successfully with formId : {0} in the different flow_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9054', 'Extending component Unsuccessful._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9055', 'Extending component Unsuccessful as the request type is Invalid._FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9056', 'Form Extension creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9057', '{0}, Extended form Name is not unique_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5031', 'Flow Extension retrieved successfully._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5032', 'Flow Extension difference retrieved successfully for parent : {0}_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5033', 'Flow Extension cannot retrieved successfully._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5034', 'Flow Extension difference cannot be retrieved for parent : {0}_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9050', 'Flow Extension history retrieval is encountered error._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9051', 'Flow Extension retrieval is unsuccessful for extension type : {0}_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6027', 'Flow Extension history retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6028',
        'Flow Extension history retrieval failed for object id : {0} due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9052', 'Flow Extension history retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9053', 'Flow Extension history retrieval failed for object id : {0} due to system error_FR',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5452', 'Resource bundle retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5453', 'Resource bundles based on locale and type retrieved successfully_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9803', 'Resource bundle retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9804', 'Resource bundle retrieval failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6803', 'Resource bundle retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6804', 'Resource bundle retrieval failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5450', 'Resource bundle created successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5451', 'Resource bundle created successfully with id : {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6801', 'Resource bundle creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6802', 'Resource bundle creation failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9801', 'Resource bundle creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9802', 'Resource bundle creation failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '6807', 'Resource bundle creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6808', 'Resource bundles based on locale and key pair is already exist_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6809', 'Resource bundle creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6810', 'Resource bundles resource key is not allowed to empty string_FR', 'INTERNAL',
        'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '9129', 'Invalid Identifier_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9130', 'Invalid Identifier passed while marking default_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9128', 'Flow has already default form associated_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9127', 'Creating default form failed_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5653', 'File imported successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5654', 'All Files Imported Successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5659', 'Imported data saved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5660', 'All files imported are saved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5661', 'All files imported are published successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9952', 'File import failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9953', 'File import failed due to system error_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9960', 'File import failed for {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9961', 'File Import failed for {0}. Empty Upload file is not allowed_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9967', 'Data save failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9968', 'Imported data failed during save due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9969', 'Import data check violated_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9970', 'Rules to import data violated. {0}_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9971', 'File Import should be from root folder_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9972',
        'Nested folder file import is not allowed, File import should be from root folder. {0}_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5701', 'Comparison disallowed for {0}._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5702',
        'This {0} requested for comparison is newly added in the extended form and doesn’t exist in the parent form_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5709', 'Comparison failed for {0}._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5710', 'Parent field is missing to compare._FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5039', 'Flow basic details retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5040', 'Flow basic details retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9058', 'Flow basic details retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9059', 'Flow basic details retrieval failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6029', 'Flow basic details retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6030', 'Flow basic details retrieval failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5801', 'Locale updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5802', 'Locale updated successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5803', 'Invalid Locale supplied._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5804', 'Invalid Locale supplied._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5805', 'Locale update encountered database error._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5806', 'Locale update encountered database error._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5807', 'Locale update failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5808', 'Locale update failed due to database error_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5500', 'Flow is deleted_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5502', 'Form is deleted._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5504', 'Default form is not available for this flow_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5506', 'Custom Control is deleted_FR', 'INTERNAL', 'SUPER', GETDATE()),


       (NEWID(), 'fr-FR', '5111', 'Invalid product name_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5112', 'Warehouse name is not valid_FR', 'INTERNAL', 'SUPER', GETDATE()),


--fetchOrphanForms
       (NEWID(), 'fr-FR', '5080', 'Custom Component Form list retrieved successfully_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5081', 'Custom Component Form list retrieved successfully for flow Id : {0}_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6150', 'Custom Component Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6151', 'Custom Component Form list retrieval failed for Id : {0} due to database error_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9155', 'Custom Component Form list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9156', 'Custom Component Form list retrieval failed for Id : {0} due to system error._FR',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5025', 'Logout successful._FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5026', 'User Logged out Successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '6214', 'Invalid Request_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6215', 'Product Name or Default warehouse is mandatory_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '9409', 'Custom Control validation successful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9410', 'Custom Control is linked with forms , Do you still want to delete ?_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9411', 'Custom Control validation successful_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9412', 'Do you want to delete ?_FR', 'INTERNAL', 'SUPER', GETDATE()),

--fetch hotkeys

       (NEWID(), 'fr-FR', '5703', 'Hot key list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5704', 'Hot key list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9958', 'Hot key list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9959', 'Hot key list retrieval failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6919', 'Hot key list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6920', 'Hot key list retrieval failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),


--fetch user language codes

       (NEWID(), 'fr-FR', '5705', 'User language code list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5706', 'User language code list retrieved successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9962', 'User language code list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9963', 'User language code list retrieval failed due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6921', 'User language code list retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6922', 'User language code list retrieval failed due to database error_FR', 'INTERNAL',
        'SUPER', GETDATE()),

-- Pre and Post processor file upload
       (NEWID(), 'fr-FR', '9964', 'Both pre and post processor files are empty_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9965', '{0} processor file upload failed due to system error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6923', '{0} processor file upload failed due to database error_FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5707', '{0} processor file uploaded successfully_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9966', 'API has already {0} processor file_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '5708', 'API do not have any {0} processor file_FR', 'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5041', 'Flow with Default Form details retrieval Successful._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5042', 'Flow with Default Form details retrieved Successfully._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '9060', 'Flow with Default Form details retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9061', 'Flow with Default Form details retrieval failed as Flow Permissions are Invalid_FR',
        'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9062', 'Flow with Default Form details retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9063', 'Flow with Default Form details retrieval failed due to system error_FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9064', 'Form details retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '9065',
        'Form details retrieval Unsuccessful as the Flow with id : {0} has No Permissions._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '6031', 'Flow with Default Form details retrieval failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6032', 'Flow with Default Form details retrieval failed due to database error_FR',
        'INTERNAL', 'SUPER', GETDATE()),

       (NEWID(), 'fr-FR', '5043', 'Tenant session inactive period retrival Successful._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5044', 'Tenant session inactive period retrived Successfully._FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '5045', 'Tenant session inactive period modified Successful._FR', 'INTERNAL', 'SUPER',
        GETDATE()),
       (NEWID(), 'fr-FR', '5046', 'Tenant session inactive period modified Successfully._FR', 'INTERNAL', 'SUPER',
        GETDATE()),

       (NEWID(), 'fr-FR', '6609', 'Registry creation failed_FR', 'INTERNAL', 'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6610',
        'Uploading Swagger file is either in Invalid Format or Open API Version is not supported _FR', 'INTERNAL',
        'SUPER', GETDATE()),
       (NEWID(), 'fr-FR', '6611', 'Third party API - Host is mandatory_FR', 'INTERNAL', 'SUPER', GETDATE());
GO