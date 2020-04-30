insert into role_master (level, name, uid)
values (0, 'JDA Product Development', '90793cab8d1a469e9cc8cd4cbd4a042c'),
       (1, 'JDA Services',  '763884ec0e9f40a68212dcfce2392df6'),
       (2, 'Third Party Implementor', 'a4a15041c2ef4bbd85a5946d73f480c7'),
       (3, 'Customer', '69c0a2fceb0648669f3e41577d0da481');

insert into product_master (uid, name, config_properties, scheme, context_path, port)
values ('0ab0e1a074184fdf9eff10012e575148', 'WMS', 'TEST', 'http', 'localhost', '4500');

insert into product_properties (uid, is_primary_ref, is_secondary_ref, name, prop_value, product_id)
values ('b32db04f3ae8414b9a525f42b156dd94', 0, 1, 'WMS', '___', '0ab0e1a074184fdf9eff10012e575148'),
       ('52dc475091a3494387055b187a440bd6', 0, 1, 'WMS', null, '0ab0e1a074184fdf9eff10012e575148');

insert into product_config (uid, secondary_ref_id, version_id, role_id)
values ('f1f7d38438ca442fb85bd543b76c0609', 'b32db04f3ae8414b9a525f42b156dd94', '0', '90793cab8d1a469e9cc8cd4cbd4a042c');

insert into menu_type (uid, menu_type)
values ('0b3e6e8fd04f4482bd1a48a04d2e76ea', 'MAIN'),
       ('d2354b474bdf4791b8fe986d967bc48e', 'GLOBAL_CONTEXT'),
       ('1a44f2b67dd949f09c3d10dfda0380ca', 'FORM_CONTEXT'),
       ('275041a86a104288bbcc1caf5bc51444', 'BOTTOM_BAR');