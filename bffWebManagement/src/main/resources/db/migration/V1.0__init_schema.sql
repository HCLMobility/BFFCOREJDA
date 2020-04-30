/****** Object:  Table [dbo].[api_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[api_master]
(
    [uid]                [binary](16)     NOT NULL,
    [created_by]         [nvarchar](255)  NULL,
    [creation_date]      [datetime2](7)   NULL,
    [last_modified_by]   [nvarchar](255)  NULL,
    [last_modified_date] [datetime2](7)   NULL,
    [name]               [nvarchar](255)  NOT NULL,
    [orchestration_name] [nvarchar](100)  NULL,
    [post_processor]     [varbinary](max) NULL,
    [pre_processor]      [varbinary](max) NULL,
    [request_body]       [nvarchar](max)  NULL,
    [request_endpoint]   [nvarchar](max)  NOT NULL,
    [request_method]     [nvarchar](10)   NOT NULL,
    [request_pathparams] [nvarchar](max)  NULL,
    [request_preproc]    [nvarchar](50)   NULL,
    [request_query]      [nvarchar](max)  NULL,
    [response_posrproc]  [nvarchar](255)  NULL,
    [response_schema]    [nvarchar](max)  NOT NULL,
    [rule_content]       [varbinary](max) NULL,
    [version]            [nvarchar](100)  NOT NULL,
    [registry_id]        [binary](16)     NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[api_registry]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[api_registry]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [api_type]           [nvarchar](20)  NOT NULL,
    [api_version]        [nvarchar](5)   NOT NULL,
    [base_path]          [nvarchar](max) NULL,
    [context_path]       [nvarchar](max) NULL,
    [helper_class]       [nvarchar](max) NULL,
    [name]               [nvarchar](255) NOT NULL,
    [port]               [nvarchar](45)  NULL,
    [scheme_list]        [nvarchar](255) NULL,
    [version_id]         [int]           NULL,
    [role_id]            [binary](16)    NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [idx_unqreg] UNIQUE NONCLUSTERED
        (
         [name] ASC,
         [api_type] ASC,
         [role_id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[app_config_detail]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[app_config_detail]
(
    [uid]                   [binary](16)    NOT NULL,
    [created_by]            [nvarchar](255) NULL,
    [creation_date]         [datetime2](7)  NULL,
    [last_modified_by]      [nvarchar](255) NULL,
    [last_modified_date]    [datetime2](7)  NULL,
    [config_value]          [nvarchar](45)  NULL,
    [description]           [nvarchar](max) NULL,
    [device_name]           [nvarchar](255) NULL,
    [flow_id]               [binary](16)    NULL,
    [user_id]               [nvarchar](45)  NULL,
    [app_config_master_uid] [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[app_config_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[app_config_master]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [config_name]        [nvarchar](25)  NOT NULL,
    [config_type]        [nvarchar](45)  NOT NULL,
    [raw_value]          [nvarchar](45)  NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [idx_name_type] UNIQUE NONCLUSTERED
        (
         [config_name] ASC,
         [config_type] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[custom_component_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[custom_component_master]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [description]        [nvarchar](max) NULL,
    [form_title]         [nvarchar](255) NULL,
    [isdisabled]         [bit]           NULL,
    [name]               [nvarchar](255) NOT NULL,
    [product_config_id]  [binary](16)    NOT NULL,
    [visibility]         [bit]           NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[custom_data]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[custom_data]
(
    [uid]        [binary](16)    NOT NULL,
    [data_label] [nvarchar](45)  NULL,
    [data_value] [nvarchar](255) NULL,
    [field_id]   [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[custom_events]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[custom_events]
(
    [uid]      [binary](16)    NOT NULL,
    [action]   [nvarchar](max) NULL,
    [event]    [nvarchar](45)  NULL,
    [field_id] [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[custom_field]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[custom_field]
(
    [uid]                                 [binary](16)      NOT NULL,
    [created_by]                          [nvarchar](255)   NULL,
    [creation_date]                       [datetime2](7)    NULL,
    [last_modified_by]                    [nvarchar](255)   NULL,
    [last_modified_date]                  [datetime2](7)    NULL,
    [add_another]                         [nvarchar](30)    NULL,
    [add_another_position]                [nvarchar](30)    NULL,
    [add_filter]                          [bit]             NULL,
    [add_pagination]                      [bit]             NULL,
    [add_sorting]                         [bit]             NULL,
    [alignment]                           [nvarchar](45)    NULL,
    [allow_input]                         [bit]             NULL,
    [always_enabled]                      [bit]             NULL,
    [api_data_source]                     [nvarchar](max)   NULL,
    [auto_adjust]                         [bit]             NULL,
    [autocomplete_api]                    [nvarchar](max)   NULL,
    [autocorrect]                         [bit]             NULL,
    [background_color]                    [nvarchar](45)    NULL,
    [bold]                                [bit]             NULL,
    [bordered]                            [bit]             NULL,
    [button_size]                         [nvarchar](45)    NULL,
    [button_type]                         [nvarchar](45)    NULL,
    [capitalization]                      [bit]             NULL,
    [clear_on_hide]                       [bit]             NULL,
    [condensed]                           [bit]             NULL,
    [currdatedefset]                      [bit]             NULL,
    [currtimedefset]                      [bit]             NULL,
    [custom_class]                        [nvarchar](45)    NULL,
    [custom_format]                       [nvarchar](45)    NULL,
    [datepicker_maxdate]                  [datetime2](7)    NULL,
    [datepicker_mindate]                  [datetime2](7)    NULL,
    [decimal_places]                      [varchar](45)     NULL,
    [default_api_value]                   [nvarchar](max)   NULL,
    [default_static_value]                [nvarchar](max)   NULL,
    [default_value]                       [nvarchar](max)   NULL,
    [default_value_type]                  [nvarchar](45)    NULL,
    [description]                         [nvarchar](255)   NULL,
    [disable_adding_removing_rows]        [bit]             NULL,
    [disable_limit]                       [bit]             NULL,
    [enable_date]                         [bit]             NULL,
    [field_dependency_disable_condition]  [nvarchar](255)   NULL,
    [field_dependency_disabled]           [bit]             NULL,
    [field_dependency_enable_condition]   [nvarchar](255)   NULL,
    [field_dependency_hidden]             [bit]             NULL,
    [field_dependency_hide_condition]     [nvarchar](255)   NULL,
    [field_dependency_required]           [bit]             NULL,
    [field_dependency_required_condition] [nvarchar](255)   NULL,
    [set_value]                           [nvarchar](max)   NULL,
    [field_dependency_show_condition]     [nvarchar](255)   NULL,
    [font_color]                          [nvarchar](45)    NULL,
    [font_size]                           [nvarchar](255)   NULL,
    [font_type]                           [nvarchar](255)   NULL,
    [format]                              [nvarchar](255)   NULL,
    [header_label]                        [nvarchar](45)    NULL,
    [height]                              [nvarchar](45)    NULL,
    [hide_label]                          [bit]             NULL,
    [hide_on_children_hidden]             [bit]             NULL,
    [hot_key_name]                        [nvarchar](255)   NULL,
    [icon]                                [bit]             NULL,
    [icon_alignment]                      [nvarchar](45)    NULL,
    [icon_code]                           [nvarchar](50)    NULL,
    [icon_name]                           [nvarchar](255)   NULL,
    [image_source]                        [nvarchar](255)   NULL,
    [inline]                              [bit]             NULL,
    [input]                               [bit]             NULL,
    [input_type]                          [nvarchar](255)   NULL,
    [italic]                              [bit]             NULL,
    [keys]                                [nvarchar](45)    NULL,
    [label]                               [nvarchar](45)    NULL,
    [lazy_load]                           [bit]             NULL,
    [line_break_mode]                     [nvarchar](255)   NULL,
    [list_image_alignment]                [varchar](45)     NULL,
    [mask]                                [bit]             NULL,
    [max_date]                            [nvarchar](45)    NULL,
    [min_date]                            [nvarchar](45)    NULL,
    [number_of_rows]                      [nvarchar](5)     NULL,
    [offset_by]                           [nvarchar](255)   NULL,
    [parent_field_id]                     [binary](16)      NULL,
    [placeholder]                         [nvarchar](255)   NULL,
    [prefix]                              [nvarchar](45)    NULL,
    [product_config_id]                   [binary](16)      NOT NULL,
    [pull]                                [nvarchar](255)   NULL,
    [push]                                [nvarchar](255)   NULL,
    [radius]                              [nvarchar](45)    NULL,
    [reference]                           [bit]             NULL,
    [remove_placement]                    [nvarchar](30)    NULL,
    [rows]                                [int]             NULL,
    [select_values]                       [nvarchar](45)    NULL,
    [selected]                            [bit]             NULL,
    [sequence]                            [int]             NULL,
    [sort]                                [nvarchar](45)    NULL,
    [striped]                             [bit]             NULL,
    [style]                               [nvarchar](45)    NULL,
    [style_background_color]              [nvarchar](45)    NULL,
    [style_font_color]                    [nvarchar](45)    NULL,
    [style_font_size]                     [nvarchar](45)    NULL,
    [style_font_type]                     [nvarchar](45)    NULL,
    [style_font_weight]                   [nvarchar](45)    NULL,
    [style_height]                        [nvarchar](45)    NULL,
    [style_margin]                        [nvarchar](45)    NULL,
    [style_padding]                       [nvarchar](45)    NULL,
    [style_type]                          [nvarchar](30)    NULL,
    [style_width]                         [nvarchar](45)    NULL,
    [suffix]                              [nvarchar](45)    NULL,
    [table_view]                          [bit]             NULL,
    [text_area_height]                    [int]             NULL,
    [type]                                [nvarchar](45)    NULL,
    [underline]                           [bit]             NULL,
    [validate_integer]                    [nvarchar](45)    NULL,
    [validate_max]                        [decimal](38, 19) NULL,
    [validate_max_date]                   [nvarchar](45)    NULL,
    [validate_max_length]                 [decimal](38, 19) NULL,
    [validate_max_row]                    [nvarchar](10)    NULL,
    [validate_max_time]                   [nvarchar](45)    NULL,
    [validate_min]                        [decimal](38, 19) NULL,
    [validate_min_date]                   [nvarchar](45)    NULL,
    [validate_min_length]                 [decimal](38, 19) NULL,
    [validate_min_row]                    [nvarchar](10)    NULL,
    [validate_min_time]                   [nvarchar](45)    NULL,
    [validate_pattern]                    [nvarchar](4000)  NULL,
    [value_property]                      [nvarchar](45)    NULL,
    [width]                               [nvarchar](45)    NULL,
    [custom_component_id]                 [binary](16)      NULL,
    [linked_component_id]                 [binary](16)      NULL,
    [parent_field_un_id]                  [binary](16)      NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[custom_field_values]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[custom_field_values]
(
    [uid]      [binary](16)    NOT NULL,
    [label]    [nvarchar](255) NULL,
    [value]    [nvarchar](255) NULL,
    [field_id] [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[data]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[data]
(
    [uid]                     [binary](16)    NOT NULL,
    [data_label]              [nvarchar](255) NULL,
    [data_value]              [nvarchar](max) NULL,
    [extended_parent_data_id] [binary](16)    NULL,
    [field_id]                [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[entity_tag]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[entity_tag]
(
    [id]        [binary](16)   NOT NULL,
    [entity_id] [int]          NOT NULL,
    [tag]       [nvarchar](45) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[events]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[events]
(
    [uid]                      [binary](16)    NOT NULL,
    [created_by]               [nvarchar](255) NULL,
    [creation_date]            [datetime2](7)  NULL,
    [last_modified_by]         [nvarchar](255) NULL,
    [last_modified_date]       [datetime2](7)  NULL,
    [action]                   [nvarchar](max) NULL,
    [event]                    [nvarchar](45)  NULL,
    [extended_parent_event_id] [binary](16)    NULL,
    [field_id]                 [binary](16)    NULL,
    [form_id]                  [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[extended_data]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[extended_data]
(
    [uid]               [binary](16)    NOT NULL,
    [data_label]        [nvarchar](255) NULL,
    [data_value]        [nvarchar](max) NULL,
    [is_compared]       [bit]           NULL,
    [extended_field_id] [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[extended_events]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[extended_events]
(
    [uid]               [binary](16)    NOT NULL,
    [action]            [nvarchar](max) NULL,
    [event]             [nvarchar](45)  NULL,
    [extended_field_id] [binary](16)    NULL,
    [extended_form_id]  [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[extended_field_base]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[extended_field_base]
(
    [uid]                                 [binary](16)    NOT NULL,
    [created_by]                          [nvarchar](255) NULL,
    [creation_date]                       [datetime2](7)  NULL,
    [last_modified_by]                    [nvarchar](255) NULL,
    [last_modified_date]                  [datetime2](7)  NULL,
    [add_another]                         [nvarchar](30)  NULL,
    [add_another_position]                [nvarchar](30)  NULL,
    [add_filter]                          [bit]           NULL,
    [add_pagination]                      [bit]           NULL,
    [add_sorting]                         [bit]           NULL,
    [alignment]                           [nvarchar](45)  NULL,
    [allow_input]                         [bit]           NULL,
    [always_enabled]                      [bit]           NULL,
    [api_data_source]                     [nvarchar](max) NULL,
    [auto_adjust]                         [bit]           NULL,
    [autocomplete_api]                    [nvarchar](max) NULL,
    [autocorrect]                         [bit]           NULL,
    [background_color]                    [nvarchar](45)  NULL,
    [bold]                                [bit]           NULL,
    [bordered]                            [bit]           NULL,
    [button_size]                         [nvarchar](45)  NULL,
    [button_type]                         [nvarchar](45)  NULL,
    [capitalization]                      [bit]           NULL,
    [clear_on_hide]                       [bit]           NULL,
    [condensed]                           [bit]           NULL,
    [currdatedefset]                      [bit]           NULL,
    [currtimedefset]                      [bit]           NULL,
    [custom_class]                        [nvarchar](255) NULL,
    [custom_format]                       [nvarchar](255) NULL,
    [datepicker_maxdate]                  [datetime2](7)  NULL,
    [datepicker_mindate]                  [datetime2](7)  NULL,
    [decimal_places]                      [varchar](45)   NULL,
    [default_api_value]                   [nvarchar](max) NULL,
    [default_static_value]                [nvarchar](max) NULL,
    [default_value]                       [nvarchar](max) NULL,
    [default_value_type]                  [nvarchar](45)  NULL,
    [description]                         [nvarchar](255) NULL,
    [disable_adding_removing_rows]        [bit]           NULL,
    [disable_limit]                       [bit]           NULL,
    [enable_date]                         [bit]           NULL,
    [extended_parent_field_id]            [binary](16)    NULL,
    [field_dependency_disable_condition]  [nvarchar](max) NULL,
    [field_dependency_disabled]           [bit]           NULL,
    [field_dependency_enable_condition]   [nvarchar](max) NULL,
    [field_dependency_hidden]             [bit]           NULL,
    [field_dependency_hide_condition]     [nvarchar](max) NULL,
    [field_dependency_required]           [bit]           NULL,
    [field_dependency_required_condition] [nvarchar](max) NULL,
    [set_value]                           [nvarchar](max) NULL,
    [field_dependency_show_condition]     [nvarchar](max) NULL,
    [font_color]                          [nvarchar](45)  NULL,
    [font_size]                           [nvarchar](255) NULL,
    [font_type]                           [nvarchar](255) NULL,
    [format]                              [nvarchar](255) NULL,
    [header_label]                        [nvarchar](45)  NULL,
    [height]                              [nvarchar](45)  NULL,
    [hide_label]                          [bit]           NULL,
    [hide_on_children_hidden]             [bit]           NULL,
    [hot_key_name]                        [nvarchar](255) NULL,
    [icon]                                [bit]           NULL,
    [icon_alignment]                      [nvarchar](45)  NULL,
    [icon_code]                           [nvarchar](255) NULL,
    [icon_name]                           [nvarchar](255) NULL,
    [image_source]                        [nvarchar](255) NULL,
    [inline]                              [bit]           NULL,
    [input]                               [bit]           NULL,
    [input_type]                          [nvarchar](255) NULL,
    [italic]                              [bit]           NULL,
    [keys]                                [nvarchar](45)  NULL,
    [label]                               [nvarchar](45)  NULL,
    [lazy_load]                           [bit]           NULL,
    [line_break_mode]                     [nvarchar](255) NULL,
    [linked_component_id]                 [binary](16)    NULL,
    [list_image_alignment]                [varchar](45)   NULL,
    [mask]                                [bit]           NULL,
    [max_date]                            [nvarchar](45)  NULL,
    [min_date]                            [nvarchar](45)  NULL,
    [modify_status]                       [bit]           NULL,
    [number_of_rows]                      [nvarchar](5)   NULL,
    [offset_by]                           [nvarchar](255) NULL,
    [placeholder]                         [nvarchar](255) NULL,
    [prefix]                              [nvarchar](45)  NULL,
    [product_config_id]                   [binary](16)    NOT NULL,
    [pull]                                [nvarchar](255) NULL,
    [push]                                [nvarchar](255) NULL,
    [radius]                              [nvarchar](45)  NULL,
    [reference]                           [bit]           NULL,
    [remove_placement]                    [nvarchar](30)  NULL,
    [rows]                                [int]           NULL,
    [select_values]                       [nvarchar](45)  NULL,
    [selected]                            [bit]           NULL,
    [sequence]                            [int]           NULL,
    [sort]                                [nvarchar](45)  NULL,
    [striped]                             [bit]           NULL,
    [style]                               [nvarchar](45)  NULL,
    [style_background_color]              [nvarchar](45)  NULL,
    [style_font_color]                    [nvarchar](45)  NULL,
    [style_font_size]                     [nvarchar](45)  NULL,
    [style_font_type]                     [nvarchar](45)  NULL,
    [style_font_weight]                   [nvarchar](45)  NULL,
    [style_height]                        [nvarchar](45)  NULL,
    [style_margin]                        [nvarchar](45)  NULL,
    [style_padding]                       [nvarchar](45)  NULL,
    [style_type]                          [nvarchar](30)  NULL,
    [style_width]                         [nvarchar](45)  NULL,
    [suffix]                              [nvarchar](45)  NULL,
    [table_view]                          [bit]           NULL,
    [text_area_height]                    [int]           NULL,
    [type]                                [nvarchar](45)  NULL,
    [underline]                           [bit]           NULL,
    [validate_integer]                    [nvarchar](45)  NULL,
    [validate_max]                        [int]           NULL,
    [validate_max_date]                   [nvarchar](45)  NULL,
    [validate_max_length]                 [int]           NULL,
    [validate_max_row]                    [nvarchar](10)  NULL,
    [validate_max_time]                   [nvarchar](45)  NULL,
    [validate_min]                        [int]           NULL,
    [validate_min_date]                   [nvarchar](45)  NULL,
    [validate_min_length]                 [int]           NULL,
    [validate_min_row]                    [nvarchar](10)  NULL,
    [validate_min_time]                   [nvarchar](45)  NULL,
    [validate_pattern]                    [nvarchar](200) NULL,
    [value_property]                      [nvarchar](45)  NULL,
    [width]                               [nvarchar](45)  NULL,
    [extended_form_id]                    [binary](16)    NULL,
    [parent_field_un_id]                  [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[extended_field_values]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[extended_field_values]
(
    [uid]               [binary](16)    NOT NULL,
    [is_compared]       [bit]           NULL,
    [label]             [nvarchar](255) NULL,
    [value]             [nvarchar](255) NULL,
    [extended_field_id] [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[extended_flow]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[extended_flow]
(
    [uid]                     [binary](16)    NOT NULL,
    [created_by]              [nvarchar](255) NULL,
    [creation_date]           [datetime2](7)  NULL,
    [last_modified_by]        [nvarchar](255) NULL,
    [last_modified_date]      [datetime2](7)  NULL,
    [default_form_id]         [binary](16)    NULL,
    [default_form_tabbed]     [bit]           NOT NULL,
    [description]             [nvarchar](255) NULL,
    [extended_parent_flow_id] [binary](16)    NULL,
    [is_disabled]             [bit]           NOT NULL,
    [is_ext_disabled]         [bit]           NULL,
    [is_published]            [bit]           NOT NULL,
    [name]                    [nvarchar](255) NOT NULL,
    [product_config_id]       [binary](16)    NOT NULL,
    [tag]                     [nvarchar](45)  NULL,
    [version]                 [bigint]        NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[extended_flow_permission]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[extended_flow_permission]
(
    [id]               [binary](16)   NOT NULL,
    [permission]       [nvarchar](45) NULL,
    [extended_flow_id] [binary](16)   NULL,
    PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[extended_form]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[extended_form]
(
    [uid]                     [binary](16)    NOT NULL,
    [created_by]              [nvarchar](255) NULL,
    [creation_date]           [datetime2](7)  NULL,
    [last_modified_by]        [nvarchar](255) NULL,
    [last_modified_date]      [datetime2](7)  NULL,
    [apply_to_all_clones]     [bit]           NOT NULL,
    [description]             [nvarchar](255) NULL,
    [ext_field_all_disabled]  [bit]           NULL,
    [extended_parent_form_id] [binary](16)    NULL,
    [form_template]           [nvarchar](255) NULL,
    [form_title]              [nvarchar](255) NULL,
    [gs1_form]                [nvarchar](max) NULL,
    [hide_bottom_navigation]  [bit]           NULL,
    [hide_gs1_bar_code]       [bit]           NULL,
    [hide_left_navigation]    [bit]           NULL,
    [hide_toolbar]            [bit]           NULL,
    [is_cloneable]            [bit]           NOT NULL,
    [is_disabled]             [bit]           NULL,
    [is_ext_disabled]         [bit]           NULL,
    [is_orphan]               [bit]           NULL,
    [is_published]            [bit]           NOT NULL,
    [is_tabbed_form]          [bit]           NULL,
    [modal_form]              [bit]           NULL,
    [name]                    [nvarchar](255) NOT NULL,
    [parent_form_id]          [binary](16)    NULL,
    [product_config_id]       [binary](16)    NOT NULL,
    [show_once]               [bit]           NULL,
    [tag]                     [nvarchar](45)  NULL,
    [extended_flow_id]        [binary](16)    NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[extended_tabs]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[extended_tabs]
(
    [uid]              [binary](16)    NOT NULL,
    [is_default]       [bit]           NULL,
    [linked_form_id]   [binary](16)    NULL,
    [linked_form_name] [nvarchar](255) NULL,
    [sequence]         [int]           NULL,
    [tab_name]         [nvarchar](255) NULL,
    [extended_form_id] [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[field]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[field]
(
    [uid]                                 [binary](16)      NOT NULL,
    [created_by]                          [nvarchar](255)   NULL,
    [creation_date]                       [datetime2](7)    NULL,
    [last_modified_by]                    [nvarchar](255)   NULL,
    [last_modified_date]                  [datetime2](7)    NULL,
    [add_another]                         [nvarchar](30)    NULL,
    [add_another_position]                [nvarchar](30)    NULL,
    [add_filter]                          [bit]             NULL,
    [add_pagination]                      [bit]             NULL,
    [add_sorting]                         [bit]             NULL,
    [alignment]                           [nvarchar](45)    NULL,
    [allow_input]                         [bit]             NULL,
    [always_enabled]                      [bit]             NULL,
    [api_data_source]                     [nvarchar](max)   NULL,
    [auto_adjust]                         [bit]             NULL,
    [autocomplete_api]                    [nvarchar](max)   NULL,
    [autocorrect]                         [bit]             NULL,
    [background_color]                    [nvarchar](45)    NULL,
    [bold]                                [bit]             NULL,
    [bordered]                            [bit]             NULL,
    [button_size]                         [nvarchar](45)    NULL,
    [button_type]                         [nvarchar](45)    NULL,
    [capitalization]                      [bit]             NULL,
    [clear_on_hide]                       [bit]             NULL,
    [condensed]                           [bit]             NULL,
    [currdatedefset]                      [bit]             NULL,
    [currtimedefset]                      [bit]             NULL,
    [custom_class]                        [nvarchar](255)   NULL,
    [custom_format]                       [nvarchar](255)   NULL,
    [datepicker_maxdate]                  [datetime2](7)    NULL,
    [datepicker_mindate]                  [datetime2](7)    NULL,
    [decimal_places]                      [varchar](45)     NULL,
    [default_api_value]                   [nvarchar](max)   NULL,
    [default_static_value]                [nvarchar](max)   NULL,
    [default_value]                       [nvarchar](max)   NULL,
    [default_value_type]                  [nvarchar](45)    NULL,
    [description]                         [nvarchar](255)   NULL,
    [disable_adding_removing_rows]        [bit]             NULL,
    [disable_limit]                       [bit]             NULL,
    [enable_date]                         [bit]             NULL,
    [extended_parent_field_id]            [binary](16)      NULL,
    [field_dependency_disable_condition]  [nvarchar](255)   NULL,
    [field_dependency_disabled]           [bit]             NULL,
    [field_dependency_enable_condition]   [nvarchar](255)   NULL,
    [field_dependency_hidden]             [bit]             NULL,
    [field_dependency_hide_condition]     [nvarchar](255)   NULL,
    [field_dependency_required]           [bit]             NULL,
    [field_dependency_required_condition] [nvarchar](255)   NULL,
    [set_value]                           [nvarchar](max)   NULL,
    [field_dependency_show_condition]     [nvarchar](255)   NULL,
    [font_color]                          [nvarchar](45)    NULL,
    [font_size]                           [varchar](255)    NULL,
    [font_type]                           [varchar](255)    NULL,
    [format]                              [nvarchar](255)   NULL,
    [header_label]                        [nvarchar](45)    NULL,
    [height]                              [nvarchar](45)    NULL,
    [hide_label]                          [bit]             NULL,
    [hide_on_children_hidden]             [bit]             NULL,
    [hot_key_name]                        [nvarchar](255)   NULL,
    [icon]                                [bit]             NOT NULL,
    [icon_alignment]                      [nvarchar](45)    NULL,
    [icon_code]                           [nvarchar](50)    NULL,
    [icon_name]                           [nvarchar](255)   NULL,
    [image_source]                        [nvarchar](255)   NULL,
    [inline]                              [bit]             NULL,
    [input]                               [bit]             NULL,
    [input_type]                          [nvarchar](255)   NULL,
    [italic]                              [bit]             NULL,
    [keys]                                [nvarchar](45)    NULL,
    [label]                               [nvarchar](45)    NULL,
    [lazy_load]                           [bit]             NULL,
    [line_break_mode]                     [varchar](255)    NULL,
    [linked_component_id]                 [binary](16)      NULL,
    [list_image_alignment]                [varchar](45)     NULL,
    [mask]                                [bit]             NULL,
    [max_date]                            [nvarchar](45)    NULL,
    [min_date]                            [nvarchar](45)    NULL,
    [modify_status]                       [bit]             NULL,
    [number_of_rows]                      [nvarchar](5)     NULL,
    [offset_by]                           [varchar](255)    NULL,
    [placeholder]                         [nvarchar](255)   NULL,
    [prefix]                              [nvarchar](45)    NULL,
    [product_config_id]                   [binary](16)      NOT NULL,
    [pull]                                [nvarchar](255)   NULL,
    [push]                                [nvarchar](255)   NULL,
    [radius]                              [nvarchar](45)    NULL,
    [reference]                           [bit]             NULL,
    [remove_placement]                    [nvarchar](30)    NULL,
    [rows]                                [int]             NULL,
    [select_values]                       [nvarchar](45)    NULL,
    [selected]                            [bit]             NULL,
    [sequence]                            [int]             NULL,
    [sort]                                [nvarchar](45)    NULL,
    [striped]                             [bit]             NULL,
    [style]                               [nvarchar](45)    NULL,
    [style_background_color]              [nvarchar](45)    NULL,
    [style_font_color]                    [nvarchar](45)    NULL,
    [style_font_size]                     [nvarchar](45)    NULL,
    [style_font_type]                     [nvarchar](45)    NULL,
    [style_font_weight]                   [nvarchar](45)    NULL,
    [style_height]                        [nvarchar](45)    NULL,
    [style_margin]                        [nvarchar](45)    NULL,
    [style_padding]                       [nvarchar](45)    NULL,
    [style_type]                          [nvarchar](30)    NULL,
    [style_width]                         [nvarchar](45)    NULL,
    [suffix]                              [nvarchar](45)    NULL,
    [table_view]                          [bit]             NULL,
    [text_area_height]                    [int]             NULL,
    [type]                                [nvarchar](45)    NULL,
    [underline]                           [bit]             NULL,
    [validate_integer]                    [nvarchar](45)    NULL,
    [validate_max]                        [decimal](38, 19) NULL,
    [validate_max_date]                   [nvarchar](45)    NULL,
    [validate_max_length]                 [decimal](38, 19) NULL,
    [validate_max_row]                    [nvarchar](10)    NULL,
    [validate_max_time]                   [nvarchar](45)    NULL,
    [validate_min]                        [decimal](38, 19) NULL,
    [validate_min_date]                   [nvarchar](45)    NULL,
    [validate_min_length]                 [decimal](38, 19) NULL,
    [validate_min_row]                    [nvarchar](10)    NULL,
    [validate_min_time]                   [nvarchar](45)    NULL,
    [validate_pattern]                    [nvarchar](4000)  NULL,
    [value_property]                      [nvarchar](45)    NULL,
    [width]                               [nvarchar](45)    NULL,
    [form_id]                             [binary](16)      NULL,
    [parent_field_un_id]                  [binary](16)      NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[field_values]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[field_values]
(
    [uid]                            [binary](16)    NOT NULL,
    [created_by]                     [nvarchar](255) NULL,
    [creation_date]                  [datetime2](7)  NULL,
    [last_modified_by]               [nvarchar](255) NULL,
    [last_modified_date]             [datetime2](7)  NULL,
    [extended_parent_field_value_id] [binary](16)    NULL,
    [label]                          [nvarchar](255) NULL,
    [value]                          [nvarchar](255) NULL,
    [field_id]                       [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[flow]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[flow]
(
    [uid]                          [binary](16)    NOT NULL,
    [created_by]                   [nvarchar](255) NULL,
    [creation_date]                [datetime2](7)  NULL,
    [last_modified_by]             [nvarchar](255) NULL,
    [last_modified_date]           [datetime2](7)  NULL,
    [default_form_id]              [binary](16)    NULL,
    [default_form_tabbed]          [bit]           NOT NULL,
    [default_modal_form]           [bit]           NULL,
    [description]                  [nvarchar](255) NULL,
    [extended_parent_flow_id]      [binary](16)    NULL,
    [extended_parent_flow_name]    [varchar](255)  NULL,
    [extended_parent_flow_version] [bigint]        NULL,
    [is_disabled]                  [bit]           NOT NULL,
    [is_ext_disabled]              [bit]           NULL,
    [is_published]                 [bit]           NOT NULL,
    [name]                         [nvarchar](255) NOT NULL,
    [published_default_form_id]    [binary](16)    NULL,
    [published_flow]               [bit]           NULL,
    [tag]                          [nvarchar](45)  NULL,
    [version]                      [bigint]        NULL,
    [product_config_id]            [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [idx_name] UNIQUE NONCLUSTERED
        (
         [name] ASC,
         [version] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[flow_permission]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[flow_permission]
(
    [id]         [binary](16)    NOT NULL,
    [permission] [nvarchar](255) NULL,
    [flow_uuid]  [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form]
(
    [uid]                     [binary](16)     NOT NULL,
    [created_by]              [nvarchar](255)  NULL,
    [creation_date]           [datetime2](7)   NULL,
    [last_modified_by]        [nvarchar](255)  NULL,
    [last_modified_date]      [datetime2](7)   NULL,
    [apply_to_all_clones]     [bit]            NOT NULL,
    [description]             [nvarchar](255)  NULL,
    [ext_field_all_disabled]  [bit]            NULL,
    [extended_parent_form_id] [binary](16)     NULL,
    [form_template]           [nvarchar](255)  NULL,
    [form_title]              [nvarchar](255)  NULL,
    [gs1_form]                [nvarchar](max)  NULL,
    [hide_bottom_navigation]  [bit]            NULL,
    [hide_gs1_bar_code]       [bit]            NULL,
    [hide_left_navigation]    [bit]            NULL,
    [hide_toolbar]            [bit]            NULL,
    [is_cloneable]            [bit]            NOT NULL,
    [is_disabled]             [bit]            NULL,
    [is_ext_disabled]         [bit]            NULL,
    [is_orphan]               [bit]            NULL,
    [is_published]            [bit]            NOT NULL,
    [is_tabbed_form]          [bit]            NULL,
    [modal_form]              [bit]            NULL,
    [name]                    [nvarchar](255)  NOT NULL,
    [parent_form_id]          [binary](16)     NULL,
    [product_config_id]       [binary](16)     NOT NULL,
    [published_form]          [varbinary](max) NULL,
    [show_once]               [bit]            NULL,
    [tag]                     [nvarchar](45)   NULL,
    [flow_id]                 [binary](16)     NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_custom_component]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_custom_component]
(
    [uid]                 [binary](16) NOT NULL,
    [custom_component_id] [binary](16) NULL,
    [form_id]             [binary](16) NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_dependency]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_dependency]
(
    [uid]              [binary](16) NOT NULL,
    [inbound_form_id]  [binary](16) NOT NULL,
    [outbound_flow_id] [binary](16) NULL,
    [outbound_form_id] [binary](16) NULL,
    [inbound_flow_id]  [binary](16) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_indep]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_indep]
(
    [uid]     [binary](16) NOT NULL,
    [form_id] [binary](16) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[key_code_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[key_code_master]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [is_alt]             [bit]           NOT NULL,
    [code]               [nvarchar](45)  NOT NULL,
    [is_ctrl]            [bit]           NOT NULL,
    [key_description]    [nvarchar](255) NOT NULL,
    [key_display_name]   [nvarchar](255) NOT NULL,
    [key_name]           [nvarchar](255) NOT NULL,
    [is_metakey]         [bit]           NOT NULL,
    [sequence]           [int]           NOT NULL,
    [is_shift]           [bit]           NOT NULL,
    [type]               [varchar](45)   NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [UK_ls580lyq50u0lc8xg5x35ocxl] UNIQUE NONCLUSTERED
        (
         [key_name] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[locale]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[locale]
(
    [uid]         [binary](16)   NOT NULL,
    [locale_code] [nvarchar](10) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[master_user]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[master_user]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [user_id]            [varchar](145)  NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [UK_ioi9p5ags81u08mwpoxighcek] UNIQUE NONCLUSTERED
        (
         [user_id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[menu_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[menu_master]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [action]             [nvarchar](255) NULL,
    [hot_key]            [nvarchar](max) NULL,
    [hot_key_name]       [nvarchar](255) NULL,
    [icon_alignment]     [varchar](50)   NULL,
    [icon_name]          [nvarchar](50)  NULL,
    [linked_form_id]     [binary](16)    NULL,
    [menu_name]          [nvarchar](45)  NULL,
    [parent_menu_id]     [binary](16)    NULL,
    [properties]         [nvarchar](max) NULL,
    [sequence]           [int]           NOT NULL,
    [show_in_toolbar]    [bit]           NULL,
    [menu_type_uid]      [binary](16)    NOT NULL,
    [secondary_ref_id]   [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[menu_permission]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[menu_permission]
(
    [uid]        [binary](16)   NOT NULL,
    [permission] [nvarchar](45) NULL,
    [menu_uuid]  [binary](16)   NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[menu_type]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[menu_type]
(
    [uid]       [binary](16)   NOT NULL,
    [menu_type] [varchar](255) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [UK_3axldf4r5ccxt31d0dq3eyu9r] UNIQUE NONCLUSTERED
        (
         [menu_type] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[privilege_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[privilege_master]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [name]               [varchar](255)  NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_config]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_config]
(
    [uid]              [binary](16) NOT NULL,
    [primary_ref_id]   [binary](16) NULL,
    [secondary_ref_id] [binary](16) NULL,
    [version_id]       [int]        NOT NULL,
    [role_id]          [binary](16) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_data_source_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_data_source_master]
(
    [uid]          [binary](16)    NOT NULL,
    [base_path]    [nvarchar](max) NOT NULL,
    [context_path] [nvarchar](max) NOT NULL,
    [name]         [nvarchar](255) NOT NULL,
    [port]         [int]           NOT NULL,
    [product_id]   [binary](16)    NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_master]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [config_properties]  [nvarchar](max) NULL,
    [context_path]       [nvarchar](255) NOT NULL,
    [name]               [nvarchar](255) NOT NULL,
    [port]               [nvarchar](45)  NOT NULL,
    [scheme]             [nvarchar](45)  NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_properties]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_properties]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [addl_config]        [nvarchar](max) NULL,
    [is_primary_ref]     [bit]           NULL,
    [is_secondary_ref]   [bit]           NULL,
    [name]               [nvarchar](255) NOT NULL,
    [prop_value]         [nvarchar](255) NULL,
    [product_id]         [binary](16)    NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_role_mapping]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_role_mapping]
(
    [uid]          [binary](16)   NOT NULL,
    [product_role] [nvarchar](25) NOT NULL,
    [role_id]      [binary](16)   NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_tenant_config]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_tenant_config]
(
    [uid]          [binary](16)    NOT NULL,
    [config_name]  [nvarchar](255) NOT NULL,
    [config_value] [nvarchar](255) NOT NULL,
    [tenant]       [nvarchar](150) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [idx_tenant_config] UNIQUE NONCLUSTERED
        (
         [tenant] ASC,
         [config_name] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[published_form_dependency]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[published_form_dependency]
(
    [uid]              [binary](16) NOT NULL,
    [inbound_form_id]  [binary](16) NOT NULL,
    [outbound_flow_id] [binary](16) NULL,
    [outbound_form_id] [binary](16) NULL,
    [inbound_flow_id]  [binary](16) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[resource_bundle]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[resource_bundle]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [locale]             [nvarchar](10)  NOT NULL,
    [resource_key]       [nvarchar](50)  NOT NULL,
    [resource_value]     [nvarchar](max) NULL,
    [type]               [nvarchar](50)  NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [idx_key] UNIQUE NONCLUSTERED
        (
         [resource_key] ASC,
         [locale] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[role_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[role_master]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [level]              [int]           NOT NULL,
    [name]               [nvarchar](255) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[role_privilege]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[role_privilege]
(
    [uid]          [binary](16) NOT NULL,
    [privilege_id] [binary](16) NOT NULL,
    [role_id]      [binary](16) NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[tabs]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tabs]
(
    [uid]              [binary](16)    NOT NULL,
    [is_default]       [bit]           NULL,
    [linked_form_id]   [binary](16)    NULL,
    [linked_form_name] [nvarchar](255) NULL,
    [sequence]         [int]           NULL,
    [tab_name]         [nvarchar](255) NULL,
    [form_id]          [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[user_role]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[user_role]
(
    [uid]     [binary](16)   NOT NULL,
    [user_id] [nvarchar](50) NOT NULL,
    [role_id] [binary](16)   NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY],
    CONSTRAINT [UK_apcc8lxk2xnug8377fatvbn04] UNIQUE NONCLUSTERED
        (
         [user_id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[version_mapping]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[version_mapping]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [bffcore_version_id] [binary](16)    NOT NULL,
    [mapped_app_id]      [binary](16)    NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[version_master]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[version_master]
(
    [uid]                [binary](16)    NOT NULL,
    [created_by]         [nvarchar](255) NULL,
    [creation_date]      [datetime2](7)  NULL,
    [last_modified_by]   [nvarchar](255) NULL,
    [last_modified_date] [datetime2](7)  NULL,
    [active]             [bit]           NULL,
    [channel]            [nvarchar](20)  NOT NULL,
    [version]            [nvarchar](20)  NOT NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[role_master]
    ADD DEFAULT ((0)) FOR [level]
GO
ALTER TABLE [dbo].[api_master]
    WITH CHECK ADD CONSTRAINT [FKehu8lkjgp0ssvy6jdljdm9ss7] FOREIGN KEY ([registry_id])
        REFERENCES [dbo].[api_registry] ([uid])
GO
ALTER TABLE [dbo].[api_master]
    CHECK CONSTRAINT [FKehu8lkjgp0ssvy6jdljdm9ss7]
GO
ALTER TABLE [dbo].[api_registry]
    WITH CHECK ADD CONSTRAINT [FKhcmnpxvhy0rg7u34crtp6hqbj] FOREIGN KEY ([role_id])
        REFERENCES [dbo].[role_master] ([uid])
GO
ALTER TABLE [dbo].[api_registry]
    CHECK CONSTRAINT [FKhcmnpxvhy0rg7u34crtp6hqbj]
GO
ALTER TABLE [dbo].[app_config_detail]
    WITH CHECK ADD CONSTRAINT [FKi1ayd795cc6jqxiisi19h8w0i] FOREIGN KEY ([app_config_master_uid])
        REFERENCES [dbo].[app_config_master] ([uid])
GO
ALTER TABLE [dbo].[app_config_detail]
    CHECK CONSTRAINT [FKi1ayd795cc6jqxiisi19h8w0i]
GO
ALTER TABLE [dbo].[custom_data]
    WITH CHECK ADD CONSTRAINT [FKi21w4xlmkqee8ciwhmbext844] FOREIGN KEY ([field_id])
        REFERENCES [dbo].[custom_field] ([uid])
GO
ALTER TABLE [dbo].[custom_data]
    CHECK CONSTRAINT [FKi21w4xlmkqee8ciwhmbext844]
GO
ALTER TABLE [dbo].[custom_events]
    WITH CHECK ADD CONSTRAINT [FKhrrff85cdcmh1skubcnfcc2c1] FOREIGN KEY ([field_id])
        REFERENCES [dbo].[custom_field] ([uid])
GO
ALTER TABLE [dbo].[custom_events]
    CHECK CONSTRAINT [FKhrrff85cdcmh1skubcnfcc2c1]
GO
ALTER TABLE [dbo].[custom_field]
    WITH CHECK ADD CONSTRAINT [FK1mjqh6yhbb3016por9bx8h08i] FOREIGN KEY ([linked_component_id])
        REFERENCES [dbo].[custom_component_master] ([uid])
GO
ALTER TABLE [dbo].[custom_field]
    CHECK CONSTRAINT [FK1mjqh6yhbb3016por9bx8h08i]
GO
ALTER TABLE [dbo].[custom_field]
    WITH CHECK ADD CONSTRAINT [FKfh2wd62lghl0r2d2t7i3rl9i1] FOREIGN KEY ([parent_field_un_id])
        REFERENCES [dbo].[custom_field] ([uid])
GO
ALTER TABLE [dbo].[custom_field]
    CHECK CONSTRAINT [FKfh2wd62lghl0r2d2t7i3rl9i1]
GO
ALTER TABLE [dbo].[custom_field]
    WITH CHECK ADD CONSTRAINT [FKpnlgprij28wvwv3fyl61h69h9] FOREIGN KEY ([custom_component_id])
        REFERENCES [dbo].[custom_component_master] ([uid])
GO
ALTER TABLE [dbo].[custom_field]
    CHECK CONSTRAINT [FKpnlgprij28wvwv3fyl61h69h9]
GO
ALTER TABLE [dbo].[custom_field_values]
    WITH CHECK ADD CONSTRAINT [FKr9s0wf7x2hxbkjbqjf3lecgje] FOREIGN KEY ([field_id])
        REFERENCES [dbo].[custom_field] ([uid])
GO
ALTER TABLE [dbo].[custom_field_values]
    CHECK CONSTRAINT [FKr9s0wf7x2hxbkjbqjf3lecgje]
GO
ALTER TABLE [dbo].[data]
    WITH CHECK ADD CONSTRAINT [FKef8us8yarhgt7sio8grteh1sn] FOREIGN KEY ([field_id])
        REFERENCES [dbo].[field] ([uid])
GO
ALTER TABLE [dbo].[data]
    CHECK CONSTRAINT [FKef8us8yarhgt7sio8grteh1sn]
GO
ALTER TABLE [dbo].[events]
    WITH CHECK ADD CONSTRAINT [FK39x17y94xsv6ipk1tglbh3vrb] FOREIGN KEY ([field_id])
        REFERENCES [dbo].[field] ([uid])
GO
ALTER TABLE [dbo].[events]
    CHECK CONSTRAINT [FK39x17y94xsv6ipk1tglbh3vrb]
GO
ALTER TABLE [dbo].[events]
    WITH CHECK ADD CONSTRAINT [FKrcgvd73bhj4bg0dqahicahljd] FOREIGN KEY ([form_id])
        REFERENCES [dbo].[form] ([uid])
GO
ALTER TABLE [dbo].[events]
    CHECK CONSTRAINT [FKrcgvd73bhj4bg0dqahicahljd]
GO
ALTER TABLE [dbo].[extended_data]
    WITH CHECK ADD CONSTRAINT [FKhd2sbd27c9l0vptw3bpxuvbxd] FOREIGN KEY ([extended_field_id])
        REFERENCES [dbo].[extended_field_base] ([uid])
GO
ALTER TABLE [dbo].[extended_data]
    CHECK CONSTRAINT [FKhd2sbd27c9l0vptw3bpxuvbxd]
GO
ALTER TABLE [dbo].[extended_events]
    WITH CHECK ADD CONSTRAINT [FKc9vr3ai368l76qroke0h8fwxr] FOREIGN KEY ([extended_field_id])
        REFERENCES [dbo].[extended_field_base] ([uid])
GO
ALTER TABLE [dbo].[extended_events]
    CHECK CONSTRAINT [FKc9vr3ai368l76qroke0h8fwxr]
GO
ALTER TABLE [dbo].[extended_events]
    WITH CHECK ADD CONSTRAINT [FKqau50ej77irmnkpll2elwvy95] FOREIGN KEY ([extended_form_id])
        REFERENCES [dbo].[extended_form] ([uid])
GO
ALTER TABLE [dbo].[extended_events]
    CHECK CONSTRAINT [FKqau50ej77irmnkpll2elwvy95]
GO
ALTER TABLE [dbo].[extended_field_base]
    WITH CHECK ADD CONSTRAINT [FK1kea25y3j60m015iyq84cft0e] FOREIGN KEY ([extended_form_id])
        REFERENCES [dbo].[extended_form] ([uid])
GO
ALTER TABLE [dbo].[extended_field_base]
    CHECK CONSTRAINT [FK1kea25y3j60m015iyq84cft0e]
GO
ALTER TABLE [dbo].[extended_field_base]
    WITH CHECK ADD CONSTRAINT [FK317la197pu1rwlk4up60n9bmh] FOREIGN KEY ([parent_field_un_id])
        REFERENCES [dbo].[extended_field_base] ([uid])
GO
ALTER TABLE [dbo].[extended_field_base]
    CHECK CONSTRAINT [FK317la197pu1rwlk4up60n9bmh]
GO
ALTER TABLE [dbo].[extended_field_values]
    WITH CHECK ADD CONSTRAINT [FKf1662qoslnmfk67v12gj7akla] FOREIGN KEY ([extended_field_id])
        REFERENCES [dbo].[extended_field_base] ([uid])
GO
ALTER TABLE [dbo].[extended_field_values]
    CHECK CONSTRAINT [FKf1662qoslnmfk67v12gj7akla]
GO
ALTER TABLE [dbo].[extended_flow_permission]
    WITH CHECK ADD CONSTRAINT [FKt2l3eiaei41swluvoso5pt7t0] FOREIGN KEY ([extended_flow_id])
        REFERENCES [dbo].[extended_flow] ([uid])
GO
ALTER TABLE [dbo].[extended_flow_permission]
    CHECK CONSTRAINT [FKt2l3eiaei41swluvoso5pt7t0]
GO
ALTER TABLE [dbo].[extended_form]
    WITH CHECK ADD CONSTRAINT [FKhfi5mb5ubu3y28tege786dn8b] FOREIGN KEY ([extended_flow_id])
        REFERENCES [dbo].[extended_flow] ([uid])
GO
ALTER TABLE [dbo].[extended_form]
    CHECK CONSTRAINT [FKhfi5mb5ubu3y28tege786dn8b]
GO
ALTER TABLE [dbo].[extended_tabs]
    WITH CHECK ADD CONSTRAINT [FKtq83hgqyxbdongb2x1u7m5rxt] FOREIGN KEY ([extended_form_id])
        REFERENCES [dbo].[extended_form] ([uid])
GO
ALTER TABLE [dbo].[extended_tabs]
    CHECK CONSTRAINT [FKtq83hgqyxbdongb2x1u7m5rxt]
GO
ALTER TABLE [dbo].[field]
    WITH CHECK ADD CONSTRAINT [FKbmb5pgl0uv4s9vakn9w5y7ko8] FOREIGN KEY ([form_id])
        REFERENCES [dbo].[form] ([uid])
GO
ALTER TABLE [dbo].[field]
    CHECK CONSTRAINT [FKbmb5pgl0uv4s9vakn9w5y7ko8]
GO
ALTER TABLE [dbo].[field]
    WITH CHECK ADD CONSTRAINT [FKgfxbih55n3oy3y2b1yop7s6s0] FOREIGN KEY ([parent_field_un_id])
        REFERENCES [dbo].[field] ([uid])
GO
ALTER TABLE [dbo].[field]
    CHECK CONSTRAINT [FKgfxbih55n3oy3y2b1yop7s6s0]
GO
ALTER TABLE [dbo].[field_values]
    WITH CHECK ADD CONSTRAINT [FK1unq5b47hh2ye6x0yn74dyol1] FOREIGN KEY ([field_id])
        REFERENCES [dbo].[field] ([uid])
GO
ALTER TABLE [dbo].[field_values]
    CHECK CONSTRAINT [FK1unq5b47hh2ye6x0yn74dyol1]
GO
ALTER TABLE [dbo].[flow]
    WITH CHECK ADD CONSTRAINT [FKrcihypl2dm5sfprkxn7jis02d] FOREIGN KEY ([product_config_id])
        REFERENCES [dbo].[product_config] ([uid])
GO
ALTER TABLE [dbo].[flow]
    CHECK CONSTRAINT [FKrcihypl2dm5sfprkxn7jis02d]
GO
ALTER TABLE [dbo].[flow_permission]
    WITH CHECK ADD CONSTRAINT [FKti8klibwoxqh4nftdt71q9jkj] FOREIGN KEY ([flow_uuid])
        REFERENCES [dbo].[flow] ([uid])
GO
ALTER TABLE [dbo].[flow_permission]
    CHECK CONSTRAINT [FKti8klibwoxqh4nftdt71q9jkj]
GO
ALTER TABLE [dbo].[form]
    WITH CHECK ADD CONSTRAINT [FK94s0eswek00hgsj8vc91ch876] FOREIGN KEY ([flow_id])
        REFERENCES [dbo].[flow] ([uid])
GO
ALTER TABLE [dbo].[form]
    CHECK CONSTRAINT [FK94s0eswek00hgsj8vc91ch876]
GO
ALTER TABLE [dbo].[form_custom_component]
    WITH CHECK ADD CONSTRAINT [FKbrlrf8de61d98b7yj1cc361yn] FOREIGN KEY ([form_id])
        REFERENCES [dbo].[form] ([uid])
GO
ALTER TABLE [dbo].[form_custom_component]
    CHECK CONSTRAINT [FKbrlrf8de61d98b7yj1cc361yn]
GO
ALTER TABLE [dbo].[form_custom_component]
    WITH CHECK ADD CONSTRAINT [FKst4hc6t13tkw24f7e36lus6k3] FOREIGN KEY ([custom_component_id])
        REFERENCES [dbo].[custom_component_master] ([uid])
GO
ALTER TABLE [dbo].[form_custom_component]
    CHECK CONSTRAINT [FKst4hc6t13tkw24f7e36lus6k3]
GO
ALTER TABLE [dbo].[form_dependency]
    WITH CHECK ADD CONSTRAINT [FK18bnvxocl0isdm5yxqniff2q7] FOREIGN KEY ([inbound_flow_id])
        REFERENCES [dbo].[flow] ([uid])
GO
ALTER TABLE [dbo].[form_dependency]
    CHECK CONSTRAINT [FK18bnvxocl0isdm5yxqniff2q7]
GO
ALTER TABLE [dbo].[form_indep]
    WITH CHECK ADD CONSTRAINT [FKgwrhr03hxh6mjgjpyx95m8hbu] FOREIGN KEY ([form_id])
        REFERENCES [dbo].[form] ([uid])
GO
ALTER TABLE [dbo].[form_indep]
    CHECK CONSTRAINT [FKgwrhr03hxh6mjgjpyx95m8hbu]
GO
ALTER TABLE [dbo].[menu_master]
    WITH CHECK ADD CONSTRAINT [FK3rmsg63k8nwg0w75wyuref6wm] FOREIGN KEY ([secondary_ref_id])
        REFERENCES [dbo].[product_properties] ([uid])
GO
ALTER TABLE [dbo].[menu_master]
    CHECK CONSTRAINT [FK3rmsg63k8nwg0w75wyuref6wm]
GO
ALTER TABLE [dbo].[menu_master]
    WITH CHECK ADD CONSTRAINT [FK4a97um98hk4s2gsx1cihxhef2] FOREIGN KEY ([menu_type_uid])
        REFERENCES [dbo].[menu_type] ([uid])
GO
ALTER TABLE [dbo].[menu_master]
    CHECK CONSTRAINT [FK4a97um98hk4s2gsx1cihxhef2]
GO
ALTER TABLE [dbo].[menu_permission]
    WITH CHECK ADD CONSTRAINT [FK2hena0p81s1lxsgkm4mmevjyf] FOREIGN KEY ([menu_uuid])
        REFERENCES [dbo].[menu_master] ([uid])
GO
ALTER TABLE [dbo].[menu_permission]
    CHECK CONSTRAINT [FK2hena0p81s1lxsgkm4mmevjyf]
GO
ALTER TABLE [dbo].[product_config]
    WITH CHECK ADD CONSTRAINT [FK65yaip2aqxapg7aqq07w1irt4] FOREIGN KEY ([role_id])
        REFERENCES [dbo].[role_master] ([uid])
GO
ALTER TABLE [dbo].[product_config]
    CHECK CONSTRAINT [FK65yaip2aqxapg7aqq07w1irt4]
GO
ALTER TABLE [dbo].[product_data_source_master]
    WITH CHECK ADD CONSTRAINT [FK75e55yeg0qsvbewnb6x4p9vq0] FOREIGN KEY ([product_id])
        REFERENCES [dbo].[product_master] ([uid])
GO
ALTER TABLE [dbo].[product_data_source_master]
    CHECK CONSTRAINT [FK75e55yeg0qsvbewnb6x4p9vq0]
GO
ALTER TABLE [dbo].[product_properties]
    WITH CHECK ADD CONSTRAINT [FKhkmujnx0md3hsgg69rn2onpc6] FOREIGN KEY ([product_id])
        REFERENCES [dbo].[product_master] ([uid])
GO
ALTER TABLE [dbo].[product_properties]
    CHECK CONSTRAINT [FKhkmujnx0md3hsgg69rn2onpc6]
GO
ALTER TABLE [dbo].[product_role_mapping]
    WITH CHECK ADD CONSTRAINT [FKen1wq2t89fctql3a1an72wr7d] FOREIGN KEY ([role_id])
        REFERENCES [dbo].[role_master] ([uid])
GO
ALTER TABLE [dbo].[product_role_mapping]
    CHECK CONSTRAINT [FKen1wq2t89fctql3a1an72wr7d]
GO
ALTER TABLE [dbo].[published_form_dependency]
    WITH CHECK ADD CONSTRAINT [FK1v2euaxa9g7uswewwo222vjnb] FOREIGN KEY ([inbound_flow_id])
        REFERENCES [dbo].[flow] ([uid])
GO
ALTER TABLE [dbo].[published_form_dependency]
    CHECK CONSTRAINT [FK1v2euaxa9g7uswewwo222vjnb]
GO
ALTER TABLE [dbo].[role_privilege]
    WITH CHECK ADD CONSTRAINT [FKgrln8424cny9rmwplbtwjhwd4] FOREIGN KEY ([role_id])
        REFERENCES [dbo].[role_master] ([uid])
GO
ALTER TABLE [dbo].[role_privilege]
    CHECK CONSTRAINT [FKgrln8424cny9rmwplbtwjhwd4]
GO
ALTER TABLE [dbo].[role_privilege]
    WITH CHECK ADD CONSTRAINT [FKnv8i9p1l9rx0y4kax3llxja0c] FOREIGN KEY ([privilege_id])
        REFERENCES [dbo].[privilege_master] ([uid])
GO
ALTER TABLE [dbo].[role_privilege]
    CHECK CONSTRAINT [FKnv8i9p1l9rx0y4kax3llxja0c]
GO
ALTER TABLE [dbo].[tabs]
    WITH CHECK ADD CONSTRAINT [FKnbpoesdre31tkepl0t8ri1rn5] FOREIGN KEY ([form_id])
        REFERENCES [dbo].[form] ([uid])
GO
ALTER TABLE [dbo].[tabs]
    CHECK CONSTRAINT [FKnbpoesdre31tkepl0t8ri1rn5]
GO
ALTER TABLE [dbo].[user_role]
    WITH CHECK ADD CONSTRAINT [FK6dymroeysw1ut1tnu2n0en39s] FOREIGN KEY ([role_id])
        REFERENCES [dbo].[role_master] ([uid])
GO
ALTER TABLE [dbo].[user_role]
    CHECK CONSTRAINT [FK6dymroeysw1ut1tnu2n0en39s]
GO
ALTER TABLE [dbo].[version_mapping]
    WITH CHECK ADD CONSTRAINT [FKgnr7fl99cn921401ryob33rmw] FOREIGN KEY ([mapped_app_id])
        REFERENCES [dbo].[version_master] ([uid])
GO
ALTER TABLE [dbo].[version_mapping]
    CHECK CONSTRAINT [FKgnr7fl99cn921401ryob33rmw]
GO
ALTER TABLE [dbo].[version_mapping]
    WITH CHECK ADD CONSTRAINT [FKn96j0ae79g66jnax215pjnvlk] FOREIGN KEY ([bffcore_version_id])
        REFERENCES [dbo].[version_master] ([uid])
GO
ALTER TABLE [dbo].[version_mapping]
    CHECK CONSTRAINT [FKn96j0ae79g66jnax215pjnvlk]
GO

GO
/****** Object:  Table [dbo].[data_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO

/****** Object:  Sequence [dbo].[rev_info_seq]    Script Date: 3/12/2020 10:55:14 PM ******/
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

CREATE TABLE [dbo].[data_aud]
(
    [uid]                     [binary](16)    NOT NULL,
    [rev]                     [int]           NOT NULL,
    [revtype]                 [smallint]      NULL,
    [data_label]              [nvarchar](255) NULL,
    [data_value]              [nvarchar](max) NULL,
    [extended_parent_data_id] [binary](16)    NULL,
    [field_id]                [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[events_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[events_aud]
(
    [uid]                      [binary](16)    NOT NULL,
    [rev]                      [int]           NOT NULL,
    [revtype]                  [smallint]      NULL,
    [action]                   [nvarchar](max) NULL,
    [event]                    [nvarchar](45)  NULL,
    [extended_parent_event_id] [binary](16)    NULL,
    [field_id]                 [binary](16)    NULL,
    [form_id]                  [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[field_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[field_aud]
(
    [uid]                                 [binary](16)      NOT NULL,
    [rev]                                 [int]             NOT NULL,
    [revtype]                             [smallint]        NULL,
    [add_another]                         [nvarchar](30)    NULL,
    [add_another_position]                [nvarchar](30)    NULL,
    [add_filter]                          [bit]             NULL,
    [add_pagination]                      [bit]             NULL,
    [add_sorting]                         [bit]             NULL,
    [alignment]                           [nvarchar](45)    NULL,
    [allow_input]                         [bit]             NULL,
    [always_enabled]                      [bit]             NULL,
    [api_data_source]                     [nvarchar](max)   NULL,
    [auto_adjust]                         [bit]             NULL,
    [autocomplete_api]                    [nvarchar](max)   NULL,
    [autocorrect]                         [bit]             NULL,
    [background_color]                    [nvarchar](45)    NULL,
    [bold]                                [bit]             NULL,
    [bordered]                            [bit]             NULL,
    [button_size]                         [nvarchar](45)    NULL,
    [button_type]                         [nvarchar](45)    NULL,
    [capitalization]                      [bit]             NULL,
    [clear_on_hide]                       [bit]             NULL,
    [condensed]                           [bit]             NULL,
    [currdatedefset]                      [bit]             NULL,
    [currtimedefset]                      [bit]             NULL,
    [custom_class]                        [nvarchar](255)   NULL,
    [custom_format]                       [nvarchar](255)   NULL,
    [datepicker_maxdate]                  [datetime2](7)    NULL,
    [datepicker_mindate]                  [datetime2](7)    NULL,
    [decimal_places]                      [varchar](45)     NULL,
    [default_api_value]                   [nvarchar](max)   NULL,
    [default_static_value]                [nvarchar](max)   NULL,
    [default_value]                       [nvarchar](max)   NULL,
    [default_value_type]                  [nvarchar](45)    NULL,
    [description]                         [nvarchar](255)   NULL,
    [disable_adding_removing_rows]        [bit]             NULL,
    [disable_limit]                       [bit]             NULL,
    [enable_date]                         [bit]             NULL,
    [extended_parent_field_id]            [binary](16)      NULL,
    [field_dependency_disable_condition]  [nvarchar](255)   NULL,
    [field_dependency_disabled]           [bit]             NULL,
    [field_dependency_enable_condition]   [nvarchar](255)   NULL,
    [field_dependency_hidden]             [bit]             NULL,
    [field_dependency_hide_condition]     [nvarchar](255)   NULL,
    [field_dependency_required]           [bit]             NULL,
    [field_dependency_required_condition] [nvarchar](255)   NULL,
    [set_value]                           [nvarchar](max)   NULL,
    [field_dependency_show_condition]     [nvarchar](255)   NULL,
    [font_color]                          [nvarchar](45)    NULL,
    [font_size]                           [varchar](255)    NULL,
    [font_type]                           [varchar](255)    NULL,
    [format]                              [nvarchar](255)   NULL,
    [header_label]                        [nvarchar](45)    NULL,
    [height]                              [nvarchar](45)    NULL,
    [hide_label]                          [bit]             NULL,
    [hide_on_children_hidden]             [bit]             NULL,
    [hot_key_name]                        [nvarchar](255)   NULL,
    [icon]                                [bit]             NULL,
    [icon_alignment]                      [nvarchar](45)    NULL,
    [icon_code]                           [nvarchar](50)    NULL,
    [icon_name]                           [nvarchar](255)   NULL,
    [image_source]                        [nvarchar](255)   NULL,
    [inline]                              [bit]             NULL,
    [input]                               [bit]             NULL,
    [input_type]                          [nvarchar](255)   NULL,
    [italic]                              [bit]             NULL,
    [keys]                                [nvarchar](45)    NULL,
    [label]                               [nvarchar](45)    NULL,
    [lazy_load]                           [bit]             NULL,
    [line_break_mode]                     [varchar](255)    NULL,
    [linked_component_id]                 [binary](16)      NULL,
    [list_image_alignment]                [varchar](45)     NULL,
    [mask]                                [bit]             NULL,
    [max_date]                            [nvarchar](45)    NULL,
    [min_date]                            [nvarchar](45)    NULL,
    [modify_status]                       [bit]             NULL,
    [number_of_rows]                      [nvarchar](5)     NULL,
    [offset_by]                           [varchar](255)    NULL,
    [placeholder]                         [nvarchar](255)   NULL,
    [prefix]                              [nvarchar](45)    NULL,
    [product_config_id]                   [binary](16)      NULL,
    [pull]                                [nvarchar](255)   NULL,
    [push]                                [nvarchar](255)   NULL,
    [radius]                              [nvarchar](45)    NULL,
    [reference]                           [bit]             NULL,
    [remove_placement]                    [nvarchar](30)    NULL,
    [rows]                                [int]             NULL,
    [select_values]                       [nvarchar](45)    NULL,
    [selected]                            [bit]             NULL,
    [sequence]                            [int]             NULL,
    [sort]                                [nvarchar](45)    NULL,
    [striped]                             [bit]             NULL,
    [style]                               [nvarchar](45)    NULL,
    [style_background_color]              [nvarchar](45)    NULL,
    [style_font_color]                    [nvarchar](45)    NULL,
    [style_font_size]                     [nvarchar](45)    NULL,
    [style_font_type]                     [nvarchar](45)    NULL,
    [style_font_weight]                   [nvarchar](45)    NULL,
    [style_height]                        [nvarchar](45)    NULL,
    [style_margin]                        [nvarchar](45)    NULL,
    [style_padding]                       [nvarchar](45)    NULL,
    [style_type]                          [nvarchar](30)    NULL,
    [style_width]                         [nvarchar](45)    NULL,
    [suffix]                              [nvarchar](45)    NULL,
    [table_view]                          [bit]             NULL,
    [text_area_height]                    [int]             NULL,
    [type]                                [nvarchar](45)    NULL,
    [underline]                           [bit]             NULL,
    [validate_integer]                    [nvarchar](45)    NULL,
    [validate_max]                        [decimal](38, 19) NULL,
    [validate_max_date]                   [nvarchar](45)    NULL,
    [validate_max_length]                 [decimal](38, 19) NULL,
    [validate_max_row]                    [nvarchar](10)    NULL,
    [validate_max_time]                   [nvarchar](45)    NULL,
    [validate_min]                        [decimal](38, 19) NULL,
    [validate_min_date]                   [nvarchar](45)    NULL,
    [validate_min_length]                 [decimal](38, 19) NULL,
    [validate_min_row]                    [nvarchar](10)    NULL,
    [validate_min_time]                   [nvarchar](45)    NULL,
    [validate_pattern]                    [nvarchar](4000)  NULL,
    [value_property]                      [nvarchar](45)    NULL,
    [width]                               [nvarchar](45)    NULL,
    [form_id]                             [binary](16)      NULL,
    [parent_field_un_id]                  [binary](16)      NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[field_values_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[field_values_aud]
(
    [uid]                            [binary](16)    NOT NULL,
    [rev]                            [int]           NOT NULL,
    [revtype]                        [smallint]      NULL,
    [extended_parent_field_value_id] [binary](16)    NULL,
    [label]                          [nvarchar](255) NULL,
    [value]                          [nvarchar](255) NULL,
    [field_id]                       [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[flow_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[flow_aud]
(
    [uid]                          [binary](16)    NOT NULL,
    [rev]                          [int]           NOT NULL,
    [revtype]                      [smallint]      NULL,
    [default_form_id]              [binary](16)    NULL,
    [default_form_tabbed]          [bit]           NULL,
    [default_modal_form]           [bit]           NULL,
    [description]                  [nvarchar](255) NULL,
    [extended_parent_flow_id]      [binary](16)    NULL,
    [extended_parent_flow_name]    [varchar](255)  NULL,
    [extended_parent_flow_version] [bigint]        NULL,
    [is_disabled]                  [bit]           NULL,
    [is_ext_disabled]              [bit]           NULL,
    [is_published]                 [bit]           NULL,
    [name]                         [nvarchar](255) NULL,
    [published_default_form_id]    [binary](16)    NULL,
    [published_flow]               [bit]           NULL,
    [tag]                          [nvarchar](45)  NULL,
    [version]                      [bigint]        NULL,
    [product_config_id]            [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[flow_permission_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[flow_permission_aud]
(
    [id]         [binary](16)    NOT NULL,
    [rev]        [int]           NOT NULL,
    [revtype]    [smallint]      NULL,
    [permission] [nvarchar](255) NULL,
    [flow_uuid]  [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [id] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_aud]
(
    [uid]                     [binary](16)     NOT NULL,
    [rev]                     [int]            NOT NULL,
    [revtype]                 [smallint]       NULL,
    [apply_to_all_clones]     [bit]            NULL,
    [description]             [nvarchar](255)  NULL,
    [ext_field_all_disabled]  [bit]            NULL,
    [extended_parent_form_id] [binary](16)     NULL,
    [form_template]           [nvarchar](255)  NULL,
    [form_title]              [nvarchar](255)  NULL,
    [gs1_form]                [nvarchar](max)  NULL,
    [hide_bottom_navigation]  [bit]            NULL,
    [hide_gs1_bar_code]       [bit]            NULL,
    [hide_left_navigation]    [bit]            NULL,
    [hide_toolbar]            [bit]            NULL,
    [is_cloneable]            [bit]            NULL,
    [is_disabled]             [bit]            NULL,
    [is_ext_disabled]         [bit]            NULL,
    [is_orphan]               [bit]            NULL,
    [is_published]            [bit]            NULL,
    [is_tabbed_form]          [bit]            NULL,
    [modal_form]              [bit]            NULL,
    [name]                    [nvarchar](255)  NULL,
    [parent_form_id]          [binary](16)     NULL,
    [product_config_id]       [binary](16)     NULL,
    [published_form]          [varbinary](max) NULL,
    [show_once]               [bit]            NULL,
    [tag]                     [nvarchar](45)   NULL,
    [flow_id]                 [binary](16)     NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_custom_component_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_custom_component_aud]
(
    [uid]                 [binary](16) NOT NULL,
    [rev]                 [int]        NOT NULL,
    [revtype]             [smallint]   NULL,
    [custom_component_id] [binary](16) NULL,
    [form_id]             [binary](16) NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[form_dependency_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[form_dependency_aud]
(
    [uid]              [binary](16) NOT NULL,
    [rev]              [int]        NOT NULL,
    [revtype]          [smallint]   NULL,
    [inbound_form_id]  [binary](16) NULL,
    [outbound_flow_id] [binary](16) NULL,
    [outbound_form_id] [binary](16) NULL,
    [inbound_flow_id]  [binary](16) NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[key_code_master_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[key_code_master_aud]
(
    [uid]              [binary](16)    NOT NULL,
    [rev]              [int]           NOT NULL,
    [revtype]          [smallint]      NULL,
    [is_alt]           [bit]           NULL,
    [code]             [nvarchar](45)  NULL,
    [is_ctrl]          [bit]           NULL,
    [key_description]  [nvarchar](255) NULL,
    [key_display_name] [nvarchar](255) NULL,
    [key_name]         [nvarchar](255) NULL,
    [sequence]         [int]           NULL,
    [is_metakey]       [bit]           NULL,
    [is_shift]         [bit]           NULL,
    [type]             [varchar](45)   NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[product_config_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[product_config_aud]
(
    [uid]              [binary](16) NOT NULL,
    [rev]              [int]        NOT NULL,
    [revtype]          [smallint]   NULL,
    [primary_ref_id]   [binary](16) NULL,
    [secondary_ref_id] [binary](16) NULL,
    [version_id]       [int]        NULL,
    [role_id]          [binary](16) NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[published_form_dependency_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[published_form_dependency_aud]
(
    [uid]              [binary](16) NOT NULL,
    [rev]              [int]        NOT NULL,
    [revtype]          [smallint]   NULL,
    [inbound_form_id]  [binary](16) NULL,
    [outbound_flow_id] [binary](16) NULL,
    [outbound_form_id] [binary](16) NULL,
    [inbound_flow_id]  [binary](16) NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[tabs_aud]    Script Date: 3/12/2020 10:54:32 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[tabs_aud]
(
    [uid]              [binary](16)    NOT NULL,
    [rev]              [int]           NOT NULL,
    [revtype]          [smallint]      NULL,
    [is_default]       [bit]           NULL,
    [linked_form_id]   [binary](16)    NULL,
    [linked_form_name] [nvarchar](255) NULL,
    [sequence]         [int]           NULL,
    [tab_name]         [nvarchar](255) NULL,
    [form_id]          [binary](16)    NULL,
    PRIMARY KEY CLUSTERED
        (
         [uid] ASC,
         [rev] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
/****** Object:  Table [dbo].[revision_info]    Script Date: 3/12/2020 10:52:14 PM ******/
SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE [dbo].[revision_info]
(
    [id]        [int]          NOT NULL,
    [date]      [datetime2](7) NULL,
    [user_name] [varchar](255) NULL,
    PRIMARY KEY CLUSTERED
        (
         [id] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO
ALTER TABLE [dbo].[data_aud]
    WITH CHECK ADD CONSTRAINT [FKpm1t2nc78dum4r7tgnad21r8t] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[data_aud]
    CHECK CONSTRAINT [FKpm1t2nc78dum4r7tgnad21r8t]
GO
ALTER TABLE [dbo].[events_aud]
    WITH CHECK ADD CONSTRAINT [FKmvgwib0iyk793y6i3psbbuhia] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[events_aud]
    CHECK CONSTRAINT [FKmvgwib0iyk793y6i3psbbuhia]
GO
ALTER TABLE [dbo].[field_aud]
    WITH CHECK ADD CONSTRAINT [FK1w3tskk6p1f8iptp4q027yty2] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[field_aud]
    CHECK CONSTRAINT [FK1w3tskk6p1f8iptp4q027yty2]
GO
ALTER TABLE [dbo].[field_values_aud]
    WITH CHECK ADD CONSTRAINT [FKldpu4i0pnely52g62qw8dc57k] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[field_values_aud]
    CHECK CONSTRAINT [FKldpu4i0pnely52g62qw8dc57k]
GO
ALTER TABLE [dbo].[flow_aud]
    WITH CHECK ADD CONSTRAINT [FKlh0hafv0h22y2g2e7r0c66yk2] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[flow_aud]
    CHECK CONSTRAINT [FKlh0hafv0h22y2g2e7r0c66yk2]
GO
ALTER TABLE [dbo].[flow_permission_aud]
    WITH CHECK ADD CONSTRAINT [FKqmwv8i6xi188yixnaop6hgp6d] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[flow_permission_aud]
    CHECK CONSTRAINT [FKqmwv8i6xi188yixnaop6hgp6d]
GO
ALTER TABLE [dbo].[form_aud]
    WITH CHECK ADD CONSTRAINT [FK8lcgycxbkqx3samymxnkhrpei] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[form_aud]
    CHECK CONSTRAINT [FK8lcgycxbkqx3samymxnkhrpei]
GO
ALTER TABLE [dbo].[form_custom_component_aud]
    WITH CHECK ADD CONSTRAINT [FKk9tupvesmk9y67w551g725a4b] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[form_custom_component_aud]
    CHECK CONSTRAINT [FKk9tupvesmk9y67w551g725a4b]
GO
ALTER TABLE [dbo].[form_dependency_aud]
    WITH CHECK ADD CONSTRAINT [FKbfj16n49phlk3c1o7omboqn56] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[form_dependency_aud]
    CHECK CONSTRAINT [FKbfj16n49phlk3c1o7omboqn56]
GO
ALTER TABLE [dbo].[key_code_master_aud]
    WITH CHECK ADD CONSTRAINT [FKihlk84nppwlay0i8c524dktru] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[key_code_master_aud]
    CHECK CONSTRAINT [FKihlk84nppwlay0i8c524dktru]
GO
ALTER TABLE [dbo].[product_config_aud]
    WITH CHECK ADD CONSTRAINT [FK6m6gimyi9amu2eymn3g8rwu0x] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[product_config_aud]
    CHECK CONSTRAINT [FK6m6gimyi9amu2eymn3g8rwu0x]
GO
ALTER TABLE [dbo].[published_form_dependency_aud]
    WITH CHECK ADD CONSTRAINT [FKgto2h8dysvlr4nubfs6rw5es7] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[published_form_dependency_aud]
    CHECK CONSTRAINT [FKgto2h8dysvlr4nubfs6rw5es7]
GO
ALTER TABLE [dbo].[tabs_aud]
    WITH CHECK ADD CONSTRAINT [FKjfftu7419m0gjw6xmwlmc42br] FOREIGN KEY ([rev])
        REFERENCES [dbo].[revision_info] ([id])
GO
ALTER TABLE [dbo].[tabs_aud]
    CHECK CONSTRAINT [FKjfftu7419m0gjw6xmwlmc42br]
GO

SET ANSI_NULLS ON
GO
SET QUOTED_IDENTIFIER ON
GO
CREATE TABLE SESSION_MANAGEMENT
(
    PRIMARY_ID            CHAR(36) NOT NULL,
    SESSION_ID            CHAR(36) NOT NULL,
    CREATION_TIME         BIGINT   NOT NULL,
    LAST_ACCESS_TIME      BIGINT   NOT NULL,
    MAX_INACTIVE_INTERVAL INT      NOT NULL,
    EXPIRY_TIME           BIGINT   NOT NULL,
    PRINCIPAL_NAME        VARCHAR(100)
        CONSTRAINT [SESSION_MANAGEMENT_PK] PRIMARY KEY CLUSTERED
            (
             [PRIMARY_ID] ASC
                ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY]
GO

CREATE UNIQUE INDEX SESSION_MANAGEMENT_IX1 ON SESSION_MANAGEMENT (SESSION_ID)
GO

CREATE INDEX SESSION_MANAGEMENT_IX2 ON SESSION_MANAGEMENT (EXPIRY_TIME)
GO

CREATE INDEX SESSION_MANAGEMENT_IX3 ON SESSION_MANAGEMENT (PRINCIPAL_NAME)
GO

CREATE TABLE SESSION_MANAGEMENT_ATTRIBUTES
(
    SESSION_PRIMARY_ID CHAR(36)     NOT NULL,
    ATTRIBUTE_NAME     VARCHAR(200) NOT NULL,
    ATTRIBUTE_BYTES    IMAGE        NOT NULL,
    CONSTRAINT [SESSION_MANAGEMENT_ATTRIBUTES_PK] PRIMARY KEY CLUSTERED
        (
         [SESSION_PRIMARY_ID] ASC,
         [ATTRIBUTE_NAME] ASC
            ) WITH (PAD_INDEX = OFF, STATISTICS_NORECOMPUTE = OFF, IGNORE_DUP_KEY = OFF, ALLOW_ROW_LOCKS = ON, ALLOW_PAGE_LOCKS = ON) ON [PRIMARY]
) ON [PRIMARY] TEXTIMAGE_ON [PRIMARY]
GO

ALTER TABLE [dbo].[SESSION_MANAGEMENT_ATTRIBUTES]
    WITH CHECK ADD CONSTRAINT [SESSION_MANAGEMENT_ATTRIBUTES_FK] FOREIGN KEY ([SESSION_PRIMARY_ID])
        REFERENCES [dbo].[SESSION_MANAGEMENT] ([PRIMARY_ID])
        ON DELETE CASCADE
GO
ALTER TABLE [dbo].[SESSION_MANAGEMENT_ATTRIBUTES]
    CHECK CONSTRAINT [SESSION_MANAGEMENT_ATTRIBUTES_FK]
GO