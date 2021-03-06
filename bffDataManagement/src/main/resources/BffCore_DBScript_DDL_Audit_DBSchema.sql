USE [<BFFCOREDB_NAME>]
GO
/****** Object:  Table [dbo].[data_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

/****** Object:  Sequence [dbo].[rev_info_seq]    Script Date: 3/31/2020 9:11:03 AM ******/
CREATE SEQUENCE [dbo].[rev_info_seq] 
 AS [bigint]
 START WITH 1
 INCREMENT BY 10
 MINVALUE -9223372036854775808
 MAXVALUE 9223372036854775807
 CACHE 
GO

/****** Object:  Table [dbo].[data_aud]    Script Date: 3/31/2020 9:11:03 AM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[data_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[data_label] [nvarchar](255) NULL,
	[data_value] [nvarchar](max) NULL,
	[extended_parent_data_id] [binary](16) NULL,
	[field_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[events_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[events_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[action] [nvarchar](max) NULL,
	[event] [nvarchar](45) NULL,
	[extended_parent_event_id] [binary](16) NULL,
	[field_id] [binary](16) NULL,
	[form_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[field_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[field_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[add_another] [nvarchar](30) NULL,
	[add_another_position] [nvarchar](30) NULL,
	[add_filter] [bit] NULL,
	[add_pagination] [bit] NULL,
	[add_sorting] [bit] NULL,
	[alignment] [nvarchar](45) NULL,
	[allow_input] [bit] NULL,
	[always_enabled] [bit] NULL,
	[api_data_source] [nvarchar](max) NULL,
	[auto_adjust] [bit] NULL,
	[autocomplete_api] [nvarchar](max) NULL,
	[autocorrect] [bit] NULL,
	[background_color] [nvarchar](45) NULL,
	[bold] [bit] NULL,
	[bordered] [bit] NULL,
	[button_size] [nvarchar](45) NULL,
	[button_type] [nvarchar](45) NULL,
	[capitalization] [bit] NULL,
	[clear_on_hide] [bit] NULL,
	[condensed] [bit] NULL,
	[currdatedefset] [bit] NULL,
	[currtimedefset] [bit] NULL,
	[custom_class] [nvarchar](255) NULL,
	[custom_format] [nvarchar](255) NULL,
	[datepicker_maxdate] [datetime2](7) NULL,
	[datepicker_mindate] [datetime2](7) NULL,
	[decimal_places] [varchar](45) NULL,
	[default_api_value] [nvarchar](max) NULL,
	[default_static_value] [nvarchar](max) NULL,
	[default_value] [nvarchar](max) NULL,
	[default_value_type] [nvarchar](45) NULL,
	[description] [nvarchar](255) NULL,
	[disable_adding_removing_rows] [bit] NULL,
	[disable_limit] [bit] NULL,
	[enable_date] [bit] NULL,
	[extended_parent_field_id] [binary](16) NULL,
	[field_dependency_disable_condition] [nvarchar](255) NULL,
	[field_dependency_disabled] [bit] NULL,
	[field_dependency_enable_condition] [nvarchar](255) NULL,
	[field_dependency_hidden] [bit] NULL,
	[field_dependency_hide_condition] [nvarchar](255) NULL,
	[field_dependency_required] [bit] NULL,
	[field_dependency_required_condition] [nvarchar](255) NULL,
	[set_value] [nvarchar](max) NULL,
	[field_dependency_show_condition] [nvarchar](255) NULL,
	[font_color] [nvarchar](45) NULL,
	[font_size] [varchar](255) NULL,
	[font_type] [varchar](255) NULL,
	[format] [nvarchar](255) NULL,
	[header_label] [nvarchar](45) NULL,
	[height] [nvarchar](45) NULL,
	[hide_label] [bit] NULL,
	[hide_on_children_hidden] [bit] NULL,
	[hot_key_name] [nvarchar](255) NULL,
	[icon] [bit] NULL,
	[icon_alignment] [nvarchar](45) NULL,
	[icon_code] [nvarchar](50) NULL,
	[icon_name] [nvarchar](255) NULL,
	[image_source] [nvarchar](255) NULL,
	[inline] [bit] NULL,
	[input] [bit] NULL,
	[input_type] [nvarchar](255) NULL,
	[italic] [bit] NULL,
	[keys] [nvarchar](45) NULL,
	[label] [nvarchar](45) NULL,
	[lazy_load] [bit] NULL,
	[line_break_mode] [varchar](255) NULL,
	[linked_component_id] [binary](16) NULL,
	[list_image_alignment] [varchar](45) NULL,
	[mask] [bit] NULL,
	[max_date] [nvarchar](45) NULL,
	[min_date] [nvarchar](45) NULL,
	[modify_status] [bit] NULL,
	[number_of_rows] [nvarchar](5) NULL,
	[offset] [varchar](255) NULL,
	[placeholder] [nvarchar](255) NULL,
	[prefix] [nvarchar](45) NULL,
	[product_config_id] [binary](16) NULL,
	[pull] [nvarchar](255) NULL,
	[push] [nvarchar](255) NULL,
	[radius] [nvarchar](45) NULL,
	[reference] [bit] NULL,
	[remove_placement] [nvarchar](30) NULL,
	[rows] [int] NULL,
	[select_values] [nvarchar](45) NULL,
	[selected] [bit] NULL,
	[sequence] [int] NULL,
	[sort] [nvarchar](45) NULL,
	[striped] [bit] NULL,
	[style] [nvarchar](45) NULL,
	[style_background_color] [nvarchar](45) NULL,
	[style_font_color] [nvarchar](45) NULL,
	[style_font_size] [nvarchar](45) NULL,
	[style_font_type] [nvarchar](45) NULL,
	[style_font_weight] [nvarchar](45) NULL,
	[style_height] [nvarchar](45) NULL,
	[style_margin] [nvarchar](45) NULL,
	[style_padding] [nvarchar](45) NULL,
	[style_type] [nvarchar](30) NULL,
	[style_width] [nvarchar](45) NULL,
	[suffix] [nvarchar](45) NULL,
	[table_view] [bit] NULL,
	[text_area_height] [int] NULL,
	[type] [nvarchar](45) NULL,
	[underline] [bit] NULL,
	[validate_integer] [nvarchar](45) NULL,
	[validate_max] [decimal](38, 19) NULL,
	[validate_max_date] [nvarchar](45) NULL,
	[validate_max_length] [decimal](38, 19) NULL,
	[validate_max_row] [nvarchar](10) NULL,
	[validate_max_time] [nvarchar](45) NULL,
	[validate_min] [decimal](38, 19) NULL,
	[validate_min_date] [nvarchar](45) NULL,
	[validate_min_length] [decimal](38, 19) NULL,
	[validate_min_row] [nvarchar](10) NULL,
	[validate_min_time] [nvarchar](45) NULL,
	[validate_pattern] [nvarchar](4000) NULL,
	[value_property] [nvarchar](45) NULL,
	[width] [nvarchar](45) NULL,
	[form_id] [binary](16) NULL,
	[parent_field_un_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[field_values_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[field_values_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[extended_parent_field_value_id] [binary](16) NULL,
	[label] [nvarchar](255) NULL,
	[value] [nvarchar](255) NULL,
	[field_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[flow_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[flow_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[default_form_id] [binary](16) NULL,
	[default_form_tabbed] [bit] NULL,
	[default_modal_form] [bit] NULL,
	[description] [nvarchar](255) NULL,
	[extended_parent_flow_id] [binary](16) NULL,
	[extended_parent_flow_name] [varchar](255) NULL,
	[extended_parent_flow_version] [bigint] NULL,
	[is_disabled] [bit] NULL,
	[is_ext_disabled] [bit] NULL,
	[is_published] [bit] NULL,
	[name] [nvarchar](255) NULL,
	[published_default_form_id] [binary](16) NULL,
	[published_flow] [bit] NULL,
	[tag] [nvarchar](45) NULL,
	[version] [bigint] NULL,
	[product_config_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[flow_permission_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[flow_permission_aud](
	[id] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[permission] [nvarchar](255) NULL,
	[flow_uuid] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[apply_to_all_clones] [bit] NULL,
	[description] [nvarchar](255) NULL,
	[ext_field_all_disabled] [bit] NULL,
	[extended_parent_form_id] [binary](16) NULL,
	[form_template] [nvarchar](255) NULL,
	[form_title] [nvarchar](255) NULL,
	[gs1_form] [nvarchar](max) NULL,
	[hide_bottom_navigation] [bit] NULL,
	[hide_gs1_bar_code] [bit] NULL,
	[hide_left_navigation] [bit] NULL,
	[hide_toolbar] [bit] NULL,
	[is_cloneable] [bit] NULL,
	[is_disabled] [bit] NULL,
	[is_ext_disabled] [bit] NULL,
	[is_orphan] [bit] NULL,
	[is_published] [bit] NULL,
	[is_tabbed_form] [bit] NULL,
	[modal_form] [bit] NULL,
	[name] [nvarchar](255) NULL,
	[parent_form_id] [binary](16) NULL,
	[product_config_id] [binary](16) NULL,
	[published_form] [varbinary](max) NULL,
	[show_once] [bit] NULL,
	[tag] [nvarchar](45) NULL,
	[flow_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_custom_component_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_custom_component_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[custom_component_id] [binary](16) NULL,
	[form_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_dependency_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_dependency_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[inbound_form_id] [binary](16) NULL,
	[outbound_flow_id] [binary](16) NULL,
	[outbound_form_id] [binary](16) NULL,
	[inbound_flow_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[key_code_master_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[key_code_master_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[is_alt] [bit] NULL,
	[code] [nvarchar](45) NULL,
	[is_ctrl] [bit] NULL,
	[key_description] [nvarchar](255) NULL,
	[key_display_name] [nvarchar](255) NULL,
	[key_name] [nvarchar](255) NULL,
	[is_metakey] [bit] NULL,
	[sequence] [int] NULL,
	[is_shift] [bit] NULL,
	[type] [varchar](45) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_config_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_config_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[primary_ref_id] [binary](16) NULL,
	[secondary_ref_id] [binary](16) NULL,
	[version_id] [int] NULL,
	[role_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[published_form_dependency_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[published_form_dependency_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[inbound_form_id] [binary](16) NULL,
	[outbound_flow_id] [binary](16) NULL,
	[outbound_form_id] [binary](16) NULL,
	[inbound_flow_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[revision_info]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[revision_info](
	[id] [int] NOT NULL,
	[date] [datetime2](7) NULL,
	[user_name] [varchar](255) NULL,
PRIMARY KEY CLUSTERED 
(
	[id] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[tabs_aud]    Script Date: 4/21/2020 12:29:27 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tabs_aud](
	[uid] [binary](16) NOT NULL,
	[rev] [int] NOT NULL,
	[revtype] [smallint] NULL,
	[is_default] [bit] NULL,
	[linked_form_id] [binary](16) NULL,
	[linked_form_name] [nvarchar](255) NULL,
	[sequence] [int] NULL,
	[tab_name] [nvarchar](255) NULL,
	[form_id] [binary](16) NULL,
PRIMARY KEY CLUSTERED 
(
	[uid] ASC,
	[rev] ASC
)WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[data_aud]  WITH CHECK ADD  CONSTRAINT [FKsdn6nimyqumxgtao3k6tb1qee] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[data_aud] CHECK CONSTRAINT [FKsdn6nimyqumxgtao3k6tb1qee]
GO
ALTER TABLE [dbo].[events_aud]  WITH CHECK ADD  CONSTRAINT [FK2un44oimycqjqv958c6b679q5] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[events_aud] CHECK CONSTRAINT [FK2un44oimycqjqv958c6b679q5]
GO
ALTER TABLE [dbo].[field_aud]  WITH CHECK ADD  CONSTRAINT [FK9oir0lgwpsusqu4v7sr6981hs] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[field_aud] CHECK CONSTRAINT [FK9oir0lgwpsusqu4v7sr6981hs]
GO
ALTER TABLE [dbo].[field_values_aud]  WITH CHECK ADD  CONSTRAINT [FK4iits70slb6efbmt4fw69g6fx] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[field_values_aud] CHECK CONSTRAINT [FK4iits70slb6efbmt4fw69g6fx]
GO
ALTER TABLE [dbo].[flow_aud]  WITH CHECK ADD  CONSTRAINT [FKt8dt2i887w8i6y5m0g9ej1umv] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[flow_aud] CHECK CONSTRAINT [FKt8dt2i887w8i6y5m0g9ej1umv]
GO
ALTER TABLE [dbo].[flow_permission_aud]  WITH CHECK ADD  CONSTRAINT [FKkc8ki7qmjyf84h6ptrfqgeeoy] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[flow_permission_aud] CHECK CONSTRAINT [FKkc8ki7qmjyf84h6ptrfqgeeoy]
GO
ALTER TABLE [dbo].[form_aud]  WITH CHECK ADD  CONSTRAINT [FKthwdb2q2l2u2xnkce7snp10vy] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[form_aud] CHECK CONSTRAINT [FKthwdb2q2l2u2xnkce7snp10vy]
GO
ALTER TABLE [dbo].[form_custom_component_aud]  WITH CHECK ADD  CONSTRAINT [FKlp48l8ieoo87tubs5kc99w5dn] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[form_custom_component_aud] CHECK CONSTRAINT [FKlp48l8ieoo87tubs5kc99w5dn]
GO
ALTER TABLE [dbo].[form_dependency_aud]  WITH CHECK ADD  CONSTRAINT [FKc96euscsltrtxq2shamf2jl1t] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[form_dependency_aud] CHECK CONSTRAINT [FKc96euscsltrtxq2shamf2jl1t]
GO
ALTER TABLE [dbo].[key_code_master_aud]  WITH CHECK ADD  CONSTRAINT [FKp361knfvtejrootbp23dmukaf] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[key_code_master_aud] CHECK CONSTRAINT [FKp361knfvtejrootbp23dmukaf]
GO
ALTER TABLE [dbo].[product_config_aud]  WITH CHECK ADD  CONSTRAINT [FK43ukm34yfpc2rtinagpaf73mp] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[product_config_aud] CHECK CONSTRAINT [FK43ukm34yfpc2rtinagpaf73mp]
GO
ALTER TABLE [dbo].[published_form_dependency_aud]  WITH CHECK ADD  CONSTRAINT [FKsy6cwvrxcmbushv30k0x3exr2] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[published_form_dependency_aud] CHECK CONSTRAINT [FKsy6cwvrxcmbushv30k0x3exr2]
GO
ALTER TABLE [dbo].[tabs_aud]  WITH CHECK ADD  CONSTRAINT [FKjitn5bbqdg8enfrhg7qrm8k1e] FOREIGN KEY([rev])
REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[tabs_aud] CHECK CONSTRAINT [FKjitn5bbqdg8enfrhg7qrm8k1e]
GO
