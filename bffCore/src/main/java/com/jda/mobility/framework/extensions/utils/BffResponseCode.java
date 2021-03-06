package com.jda.mobility.framework.extensions.utils;

/**
 * The class for BffCore ResponseCodes
 * 
 * @author HCL Technologies Ltd.
 */
public enum BffResponseCode {
	// Authentication and user access
	LOGIN_SUCCESS_CODE_AUTHENTICATION(5001, "5001"), LOGIN_USER_CODE_AUTHENTICATION(5002, "5002"),
	INACTIVE_PERIOD_RETRIVAL_SUCCESS_CODE(5043, "5043"), INACTIVE_PERIOD_RETRIVAL_USER_CODE(5044, "5044"),
	INACTIVE_PERIOD_UPDATE_SUCCESS_CODE(5045, "5045"), INACTIVE_PERIOD_UPDATE_USER_CODE(5046, "5046"),
	ERR_LOGIN_API_AUTH_FAILED(9001, "9001"), ERR_LOGIN_USER_ACCESS_NOT_AUTHORIZED(9002, "9002"),
	ERR_LOGIN_API_AUTH_EXCEPTION(9043, "9043"), ERR_LOGIN_USER_AUTH_EXCEPTION(9044, "9044"),
	ERR_LOGIN_API_REST_CLIENT_EXCEPTION(6003, "6003"), ERR_LOGIN_USER_REST_CLIENT_EXCEPTION(6004, "6004"),
	ERR_LOGIN_API_AUTH_CHANNEL(9807, "9807"), ERR_LOGIN_USER_AUTH_CHANNEL(9808, "9808"),
	LOGOUT_SUCCESS_CODE_USER(5025, "5025"), LOGOUT_USER_CODE_USER(5026, "5026"),
	ERR_LOGIN_API_USER_LOGOUT(9035, "9035"), ERR_LOGIN_USER_USER_LOGOUT(9036, "9036"),
	ERR_LOGIN_API_LOGOUT_DBEXCEPTION(6021, "6021"), ERR_LOGIN_USER_LOGOUT_DBEXCEPTION(6022, "6022"),
	ERR_LOGIN_UI_API_NOT_COMPATIBLE(9810, "9810"),
	ERR_LOGIN_PRODUCT_API_NOT_COMPATIBLE(9973, "9973"),ERR_LOGIN_PRODUCT_USER_NOT_COMPATIBLE(9974, "9974"),

	// FLOW createFlow
	FLOW_SUCCESS_CODE_CREATE_FLOW(5003, "5003"), FLOW_USER_CODE_CREATE_FLOW(5004, "5004"),
	DB_ERR_FLOW_API_CREATE_FLOW(6005, "6005"), DB_ERR_FLOW_USER_CREATE_FLOW(6006, "6006"),
	ERR_FLOW_API_CREATE_FLOW(9003, "9003"), ERR_FLOW_USER_CREATE_FLOW(9004, "9004"),
	ERR_FLOW_API_CREATE_FLOW_EXCEPTION(9005, "9005"), ERR_FLOW_USER_CREATE_FLOW_EXCEPTION(9006, "9006"),
	ERR_FLOW_USER_INVALID_ACTION(9172, "9172"), ERR_FLOW_USER_INVALID_IDENTIFIER(9174, "9174"),

	// FLOW modifyFlow
	FLOW_SUCCESS_CODE_MODIFY_FLOW(5005, "5005"), FLOW_USER_CODE_MODIFY_FLOW(5006, "5006"),
	ERR_FLOW_API_MODIFY_FLOW(9007, "9007"), ERR_FLOW_USER_MODIFY_FLOW(9008, "9008"),
	ERR_FLOW_API_MODIFY_FLOW_EXP(9009, "9009"), ERR_FLOW_USER_MODIFY_FLOW_EXP(9010, "9010"),
	ERR_FLOW_API_MODIFY_FLOW_EXCEPTION(9011, "9011"), ERR_FLOW_USER_MODIFY_FLOW_EXCEPTION(9012, "9012"),
	DB_ERR_FLOW_API_MODIFY_FLOW(6007, "6007"), DB_ERR_FLOW_USER_MODIFY_FLOW(6008, "6008"),

	// FLOW getFlowById
	FLOW_SUCCESS_CODE_FETCH_FLOW_BY_ID(5007, "5007"), FLOW_USER_CODE_FETCH_FLOW_BY_ID(5008, "5008"),
	DB_ERR_FLOW_API_FETCH_FLOW_BY_ID(6009, "6009"), DB_ERR_FLOW_USER_FETCH_FLOW_BY_ID(6010, "6010"),
	ERR_FLOW_API_FETCH_FLOW_BY_ID(9013, "9013"), ERR_FLOW_USER_FETCH_FLOW_BY_ID(9014, "9014"),
	ERR_FLOW_UPUBLISH_CD(9163, "9163"), ERR_FLOW_DISABLE_CD(9164, "9164"), ERR_FLOW_DEFAULT_FORM_DISABLED(9180, "9180"),
	ERR_FLOW_DEFAULT_FORM_NOT_PUBLISHED(9181, "9181"),ERR_FLOW_NO_DEFAULT_FORM(9182, "9182"),
	ERR_NO_FORM_FOUND(9183, "9183"),

	// FLOW fetchCount
	FLOW_SUCCESS_CODE_FLOW_COUNT(5009, "5009"), FLOW_USER_CODE_FLOW_COUNT(5010, "5010"),
	DB_ERR_FLOW_API_FLOW_COUNT(6011, "6011"), DB_ERR_FLOW_USER_FLOW_COUNT(6012, "6012"),
	ERR_FLOW_API_FLOW_COUNT(9015, "9015"), ERR_FLOW_USER_FLOW_COUNT(9016, "9016"),

	// FLOW fetchAllFlows
	FLOW_SUCCESS_CODE_FETCH_ALL_FLOWS(5011, "5011"), FLOW_USER_CODE_FETCH_ALL_FLOWS(5012, "5012"),
	DB_ERR_FLOW_API_FETCH_ALL_FLOWS(6013, "6013"), DB_ERR_FLOW_USER_FETCH_ALL_FLOWS(6014, "6014"),
	ERR_FLOW_API_FETCH_ALL_FLOWS(9017, "9017"), ERR_FLOW_USER_FETCH_ALL_FLOWS(9018, "9018"),

	// FLOW uniqueFlow
	FLOW_SUCCESS_CODE_UNIQUE_FLOW(5013, "5013"), FLOW_USER_CODE_UNIQUE_FLOW(5014, "5014"),
	DB_ERR_FLOW_API_UNIQUE_FLOW(6015, "6015"), DB_ERR_FLOW_USER_UNIQUE_FLOW(6016, "6016"),

	// FLOW deleteFlowById
	FLOW_SUCCESS_CODE_DELETE_FLOW(5017, "5017"), FLOW_USER_CODE_DELETE_FLOW(5018, "5018"),
	DB_ERR_FLOW_API_DELETE_FLOW(6017, "6017"), DB_ERR_FLOW_USER_DELETE_FLOW(6018, "6018"),
	ERR_FLOW_API_DELETE_FLOW(9019, "9019"), ERR_FLOW_USER_DELETE_FLOW(9020, "9020"),
	ERR_FLOW_API_DELETE_FLOW_UNSUCCESS(9080, "9080"), ERR_FLOW_USER_DELETE_DEFAULT_FLOW(9081, "9081"),
	ERR_FLOW_API_DELETE_HOME_FLOW(9082, "9082"), ERR_FLOW_USER_DELETE_FORM_FLOW(9083, "9083"),
	ERR_FLOW_API_DELETE_MENU_FLOW(9084, "9084"),

	FLOW_SUCCESS_CODE_MODIFY_FLOW_CHECK_PUBLISH(5019, "5019"), FLOW_USER_CODE_MODIFY_FLOW_CHECK_PUBLISH(5020, "5020"),
	FLOW_SUCCESS_CODE_FLOW_PUBLISH(5021, "5021"), FLOW_USER_CODE_FLOW_PUBLISH(5022, "5022"),
	ERR_FLOW_API_VALIDATE_ENUM_CHECK_SEARCH(9021, "9021"), ERR_FLOW_USER_VALIDATE_ENUM_CHECK_SEARCH(9022, "9022"),
	ERR_FLOW_API_VALIDATE_ENUM_CHECK_MODIFY(9023, "9023"), ERR_FLOW_USER_VALIDATE_ENUM_CHECK_MODIFY(9024, "9024"),

	// disableFlow
	FLOW_SUCCESS_CODE_DISABLE_FLOW(5023, "5023"), FLOW_USER_CODE_DISABLE_FLOW(5024, "5024"),
	ERR_FLOW_API_CHECK_DEFAULT_FLOW(9025, "9025"), ERR_FLOW_USER_CHECK_DEFAULT_FLOW(9026, "9026"),
	ERR_FLOW_API_CHECK_CURRENT_FLOW_HOME(9027, "9027"), ERR_FLOW_USER_CHECK_CURRENT_FLOW_HOME(9028, "9028"),
	ERR_FLOW_API_ENUM_VALIDTAE_CHECK(9029, "9029"), ERR_FLOW_USER_ENUM_VALIDTAE_CHECK(9030, "9030"),
	ERR_FLOW_API_CHECK_DISABLE_FLOW(9031, "9031"), ERR_FLOW_USER_CHECK_DISABLE_FLOW(9032, "9032"),
	ERR_FLOW_API_DISABLE_FLOW_EXCEPTION(9033, "9033"), ERR_FLOW_USER_DISABLE_FLOW_EXCEPTION(9034, "9034"),
	DB_ERR_FLOW_API_DISABLE_FLOW(6019, "6019"), DB_ERR_FLOW_USER_DISABLE_FLOW(6020, "6020"),

	// publishFlow
	FLOW_SUCCESS_CODE_CONFIRM_PUBLISH(5027, "5027"), FLOW_USER_CODE_CONFIRM_PUBLISH(5028, "5028"),
	ERR_FLOW_API_ENUM_VALIDATE_PUBLISH_FLOW(9037, "9037"), ERR_FLOW_USER_ENUM_VALIDATE_PUBLISH_FLOW(9038, "9038"),
	ERR_FLOW_API_ENUM_VALIDATE_CHECK_PUBLISH(9039, "9039"), ERR_FLOW_USER_ENUM_VALIDATE_CHECK_PUBLISH(9040, "9040"),
	ERR_FLOW_API_PUBLISH_FLOW_EXCEPTION(9041, "9041"), ERR_FLOW_USER_PUBLISH_FLOW_EXCEPTION(9042, "9042"),
	DB_ERR_FLOW_API_PUBLISH_FLOW(6023, "6023"), DB_ERR_FLOW_USER_PUBLISH_FLOW(6024, "6024"),

	// cloneComponent
	FLOW_SUCCESS_CODE_EXTENDED_COMPONENT(5029, "5029"), FLOW_USER_CODE_EXTENDED_COMPONENT(5030, "5030"),
	FLOW_SUCCESS_CODE_CLONE_COMPONENT(5047, "5047"), FLOW_USER_CODE_CLONE_COMPONENT(5048, "5048"),
	FORM_SUCCESS_CODE_CLONE_FORM_IN_SAME_FLOW(5035, "5035"), FORM_USER_CODE_CLONE_FORM_IN_SAME_FLOW(5036, "5036"),
	FORM_SUCCESS_CODE_CLONE_FORM_IN_DIFF_FLOW(5037, "5037"), FORM_USER_CODE_CLONE_FORM_IN_DIFF_FLOW(5038, "5038"),
	ERR_CLONE_COMPONENT_INVALID_ACTION(9054, "9054"), ERR_USER_CLONE_COMPONENT_INVALID_ACTION(9055, "9055"),
	ERR_CLONE_API_FLOW_NAME_UNIQUE_CHECK(9046, "9046"), ERR_CLONE_USER_API_FLOW_NAME_UNIQUE_CHECK(9047, "9047"),
	DB_ERR_FLOW_API_CREATE_FLOW_EXTENDED(6025, "6025"), DB_ERR_FLOW_USER_CREATE_FLOW_EXTENDED(6026, "6026"),
	ERR_FLOW_API_CREATE_FLOW_EXCEPTION_EXTENDED(9048, "9048"),ERR_FLOW_USER_CREATE_FLOW_EXCEPTION_EXTENDED(9049, "9049"),
	DB_ERR_FLOW_API_CREATE_FLOW_CLONED(6033, "6033"), DB_ERR_FLOW_USER_CREATE_FLOW_CLONED(6034, "6034"),
	ERR_FLOW_API_CREATE_FLOW_EXCEPTION_CLONED(9066, "9066"),ERR_FLOW_USER_CREATE_FLOW_EXCEPTION_CLONED(9067, "9067"),
	
	ERR_API_FORM_NAME_UNIQUE_CHECK(9056, "9056"),ERR_CLONE_USER_API_FORM_NAME_UNIQUE_CHECK(9057, "9057"),
	ERR_VERSION_API_FLOW_NAME_UNIQUE_CHECK(9070, "9070"),FLOW_SUCCESS_CODE_VERSION_COMPONENT(9071, "9071"),
	DB_ERR_FLOW_API_CREATE_FLOW_VERSIONED(9072, "9072"),ERR_FLOW_API_CREATE_FLOW_EXCEPTION_VERSIONED(9073, "9073"),

	// create ExtensionFetchFlow
	FLOW_SUCCESS_CODE_FLOW_EXTENDED_FETCH(5031, "5031"), FLOW_USER_CODE_FLOW_EXTENDED_FETCH(5032, "5032"),
	FLOW_SUCCESS_CODE_FLOW_EXTENDED(5033, "5033"), FLOW_USER_CODE_FLOW_EXTENDED(5034, "5034"),
	DB_ERR_FLOW_API_FLOW_EXTENDED_FETCH(6027, "6027"), DB_ERR_FLOW_USER_FLOW_EXTENDED_FETCH(6028, "6028"),
	ERR_FLOW_API_FLOW_EXTENDED_FTECH_ENUM_CHECK(9050, "9050"),
	ERR_FLOW_USER_FLOW_EXTENDED_FETCH_ENUM_CHECK(9051, "9051"),
	ERR_FLOW_API_FLOW_EXCEPTION_EXTENDED_FETCH(9052, "9052"), ERR_FLOW_USER_FLOW_EXCEPTION_EXTENDED_FETCH(9053, "9053"),

	// fetchFlowBasicList
	FLOW_SUCCESS_CODE_FLOW_BASIC_LIST_FETCH(5039, "5039"), FLOW_USER_CODE_FLOW_BASIC_LIST_FETCH(5040, "5040"),
	ERR_FLOW_API_FLOW_BASIC_LIST_FETCH(9058, "9058"), ERR_FLOW_USER_API_FLOW_BASIC_LIST_FETCH(9059, "9059"),
	DB_ERR_FLOW_API_FLOW_BASIC_LIST_FETCH(6029, "6029"), DB_ERR_FLOW_USER_API_FLOW_BASIC_LIST_FETCH(6030, "6030"),

	// getDefaultFormForFlowId
	FLOW_SUCCESS_CODE_FETCH_FLOW_DEF_FORM(5041, "5041"), FLOW_USER_CODE_FETCH_FLOW_DEF_FORM(5042, "5042"),
	ERR_FLOW_API_INVALID_FLOW_PERMISSION(9060, "9060"), ERR_USER_FLOW_API_INVALID_FLOW_PERMISSION(9061, "9061"),
	ERR_FLOW_API_FETCH_FLOW_DEF_FORM(9062, "9062"), ERR_USER_FLOW_API_FETCH_FLOW_DEF_FORM(9063, "9063"),
	ERR_FLOW_API_FLOW_PERMISSIONS_EMPTY(9064,"9064"), ERR_USER_FLOW_API_FLOW_PERMISSIONS_EMPTY(9065,"9065"), 
	DB_ERR_FLOW_API_FETCH_FLOW_DEF_FORM(6031, "6031"), DB_ERR_USER_FLOW_API_FETCH_FLOW_DEF_FORM(6032, "6032"),
	

	// FORM SUCCESS AND ERROR MESSAGES
	// FORM createForm
	FORM_SUCCESS_CODE_CREATE_FORM(5051, "5051"), FORM_USER_CODE_CREATE_FORM(5052, "5052"),
	ERR_FORM_API_FLOW_GIVEN_FORM(9101, "9101"), ERR_FORM_USER_FLOW_GIVEN_FORM(9102, "9102"),
	ERR_FORM_API_INVALID_ACTION(9170, "9170"), ERR_FORM_USER_INVALID_ACTION(9171, "9171"),
	ERR_FORM_API_CREATE_FORM_EXCEPTION(9103, "9103"), ERR_FORM_USER_CREATE_FORM_EXCEPTION(9104, "9104"),
	DB_ERR_FORM_API_CREATE_FORM(6101, "6101"), DB_ERR_FORM_USER_CREATE_FORM(6102, "6102"),
	ERR_FORM_API_INVALID_IDENTIFIER(9133, "9133"), ERR_FORM_USER_INVALID_IDENTIFIER(9134, "9134"),
	ERR_FORM_API_CHECK_DEFAULT(9135, "9135"), ERR_FORM_USER_CHECK_DEFAULT(9136, "9136"),

	// FORM modifyForm
	FORM_SUCCESS_CODE_MODIFY_FORM(5053, "5053"), FORM_USER_CODE_MODIFY_FORM(5054, "5054"),
	ERR_FORM_API_UPDATE_FORM(9105, "9105"), ERR_FORM_USER_UPDATE_FORM(9106, "9106"),
	ERR_FORM_API_UPDATE_FORM_EXCEPTION(9107, "9107"), ERR_FORM_USER_UPDATE_FORM_EXCEPTION(9108, "9108"),
	DB_ERR_FORM_API_UPDATE_FORM(6103, "6103"), DB_ERR_FORM_USER_UPDATE_FORM(6104, "6104"),
	ERR_FORM_API_IS_DISABLED(9137, "9137"), ERR_FORM_USER_IS_DISABLED(9138, "9138"),
	ERR_FORM_API_ENUM_VALIDATE_CHECK(9139, "9139"), ERR_FORM_USER_ENUM_VALIDATE_CHECK(9140, "9140"),

	// FORM getFormById
	FORM_SUCCESS_CODE_FETCH_FORM_BY_ID(5055, "5055"), FORM_USER_CODE_FETCH_FORM_BY_ID(5056, "5056"),
	ERR_FORM_API_FETCH_FORM_BY_ID(9109, "9109"), ERR_FORM_USER_FETCH_FORM_BY_ID(9110, "9110"),
	DB_ERR_FORM_API_FETCH_FLOW_BY_ID(6105, "6105"), DB_ERR_FORM_USER_FETCH_FLOW_BY_ID(6106, "6106"),
	ERR_FORM_UPUBLISH_CD(9160, "9160"), ERR_FORM_DISABLE_CD(9161, "9161"), ERR_FORM_FLOW_NOT_FOUND(5500, "5500"),
	ERR_FORM_USER_CODE_FLOW_NOT_FOUND(5501, "5501"), ERR_FORM_NOT_FOUND(5502, "5502"),
	ERR_FORM_USER_CODE_FORM_NOT_FOUND(5503, "5503"), ERR_FORM_DEFAULT_FORM_NOT_FOUND(5504, "5504"),
	ERR_FORM_USER_CODE_DEFAULT_FORM_NOT_FOUND(5505, "5505"),ERR_CUSTOM_CTRL_NOT_FOUND(5506, "5506"),

	// FORM deleteFormByID
	FORM_SUCCESS_CODE_DELETE_FORM_BY_ID(5057, "5057"), FORM_USER_CODE_DELETE_FORM_BY_ID(5058, "5058"),
	ERR_FORM_API_DELETE_FORM_BY_ID_EXCEPTION(9111, "9111"), ERR_FORM_USER_DELETE_FORM_BY_ID_EXCEPTION(9112, "9112"),
	DB_ERR_FORM_API_DELETE_FORM_BY_ID(6107, "6107"), DB_ERR_FORM_USER_DELETE_FORM_BY_ID(6108, "6108"),
	ERR_FORM_API_DELETE_FORM_BY_ID(9113, "9113"), ERR_FORM_USER_DELETE_FORM_BY_ID(9114, "9114"),

	// FORM fetchOrphanForms
	FORM_SUCCESS_CODE_FETCH_ORPHAN_FORMS(5059, "5059"), FORM_USER_CODE_FETCH_ORPHAN_FORMS(5060, "5060"),
	ERR_FORM_API_FETCH_ORPHAN_FORMS_EXCEPTION(9115, "9115"), ERR_FORM_USER_FETCH_ORPHAN_FORMS_EXCEPTION(9116, "9116"),
	DB_ERR_FORM_API_FETCH_ORPHAN_FORMS(6109, "6109"), DB_ERR_FORM_USER_FETCH_ORPHAN_FORMS(6110, "6110"),
	ERR_FORM_API_ENUM_VALIDATE_MODIFY_FORM(9119, "9119"), ERR_FORM_USER_ENUM_VALIDATE_MODIFY_FORM(9120, "9120"),

	// fetchUnpublishForms
	FORM_SUCCESS_CODE_FECTH_UNPUBLISH_FORMS(5061, "5061"), FORM_USER_CODE_FECTH_UNPUBLISH_FORMS(5062, "5062"),
	DB_ERR_FORM_API_015_FECTH_UNPUBLISH_FORMS(6111, "6111"), DB_ERR_FORM_USER_FECTH_UNPUBLISH_FORMS(6112, "6112"),
	ERR_FORM_API_FECTH_UNPUBLISH_FORMS(9121, "9121"), ERR_FORM_USER_FECTH_UNPUBLISH_FORMS(9122, "9122"),

	// createDefaultForm
	FORM_SUCCESS_CODE_CREATE_DEFAULT_FORM(5063, "5063"), FORM_USER_CODE_CREATE_DEFAULT_FORM(5064, "5064"),
	DB_ERR_FORM_API_CREATE_DEFAULT_FORM(6113, "6113"), DB_ERR_FORM_USER_CREATE_DEFAULT_FORM(6114, "6114"),
	ERR_FORM_API_CREATE_DEFAULT_FORM_EXCEPTION(9123, "9123"), ERR_FORM_USER_CREATE_DEFAULT_FORM_EXCEPTION(9124, "9124"),
	ERR_FORM_API_ENUM_CHECK_DEFAULT(9127, "9127"), ERR_FORM_USER_ENUM_CHECK_DEFAULT(9128, "9128"),
	ERR_FORM_API_ENUM_VALIATE_CHECK_DEFAULT_FORM(9129, "9129"),
	ERR_FORM_USER_ENUM_VALIATE_CHECK_DEFAULT_FORM(9130, "9130"),

	// fetchAllForms
	FORM_SUCCESS_CODE_FETCH_ALL_FORMS(5065, "5065"), FORM_USER_CODE_FETCH_ALL_FORMS(5066, "5066"),
	DB_ERR_FORM_API_FETCH_ALL_FORMS(6115, "6115"), DB_ERR_FORM_USER_FETCH_ALL_FORMS(6116, "6116"),
	ERR_FORM_API_FETCH_ALL_FORMS_EXCEPTION(9125, "9125"), ERR_FORM_USER_FETCH_ALL_FORMS_EXCEPTION(9126, "9126"),

	// publishForm
	FORM_SUCCESS_CODE_PUBLISH_FORM(5067, "5067"), FORM_USER_CODE_PUBLISH_FORM(5068, "5068"),
	ERR_FORM_API_ENUM_VALIDATE_PUBLISH_FORM(9143, "9143"), ERR_FORM_USER_ENUM_VALIDATE_PUBLISH_FORM(9144, "9144"),
	ERR_FORM_API_CHECK_PUBLISH(9145, "9145"), ERR_FORM_USER_CHECK_PUBLISH(9146, "9146"),
	ERR_FORM_API_PUBLISH_FORM_EXCEPTION(9147, "9147"), ERR_FORM_USER_PUBLISH_FORM_EXCEPTION(9148, "9148"),
	DB_ERR_FORM_API_PUBLISH_FORM(6117, "6117"), DB_ERR_FORM_USER_PUBLISH_FORM(6118, "6118"),
	ERR_FORM_USER_CHECK_PUBLISH_DISABLE(9173, "9173"),

	// fetchFormNames
	FORM_SUCCESS_CODE_FETCH_FORM_NAMES(5069, "5069"), FORM_USER_CODE_FETCH_FORM_NAMES(5070, "5070"),
	ERR_FORM_API_FETCH_FORM_NAMES_EXCEPTION(9149, "9149"), ERR_FORM_USER_FETCH_FORM_NAMES_EXCEPTION(9150, "9150"),
	DB_ERR_FORM_API_FETCH_FORM_NAMES(6119, "6119"), DB_ERR_FORM_USER_FETCH_FORM_NAMES(6120, "6120"),
	ERR_FORM_API_FETCH_FORM_NAMES(6123, "6123"), ERR_FORM_USER_FETCH_FORM_NAMES(6124, "6124"),
	ERR_FORM_API_FETCH_FORM_LIST_EMPTY_CHECK(6125, "6125"), ERR_FORM_USER_FETCH_FORM_LIST_EMPTY_CHECK(6126, "6126"),

	// fetchUnpublishOrphanForms
	FORM_SUCCESS_CODE_UNPUBLISH_ORPHAN_FORMS(5071, "5071"), FORM_USER_CODE_UNPUBLISH_ORPHAN_FORMS(5072, "5072"),
	FORM_SUCCESS_CODE_ORPHAN_FORMS(5073, "5073"), FORM_USER_CODE_ORPHAN_FORMS(5074, "5074"),
	ERR_FORM_API_ENUM_CHECK_UNPUBLISH_ORPHAN_FORMS(9151, "9151"),
	ERR_FORM_USER_ENUM_CHECK_UNPUBLISH_ORPHAN_FORMS(9152, "9152"),
	ERR_FORM_API_UNPUBLISH_ORPHAN_FORMS_EXCEPTION(9153, "9153"),
	ERR_FORM_USER_UNPUBLISH_ORPHAN_FORMS_EXCEPTION(9154, "9154"), DB_ERR_FORM_API_UNPUBLISH_ORPHAN_FORMS(6121, "6121"),
	DB_ERR_FORM_USER_UNPUBLISH_ORPHAN_FORMS(6122, "6122"),

	// FORM fetchFormDetails
	FORM_SUCCESS_CODE_FORM_DETAILS(5080, "5080"), FORM_USER_CODE_FORM_DETAILS(5081, "5081"),
	ERR_FORM_API_FORM_DETAILS(9155, "9155"), ERR_FORM_USER_FORM_DETAILS(9156, "9156"),
	DB_ERR_FORM_API_FORM_DETAILS(6150, "6150"), DB_ERR_FORM_USER_013_FORM_DETAILS(6151, "6151"),

	/* Menu success and error codes */
	// create Menu
	MENU_CREATE_SUCCESS_CD(5101, "5101"), MENU_CREATE_SUCCESS_USER_CD(5102, "5102"),
	MENU_UPDATE_SUCCESS_CD(5115, "5115"), MENU_UPDATE_SUCCESS_USER_CD(5116, "5116"),
	MENU_DELETE_SUCCESS_CD(5103, "5103"), MENU_DELETE_SUCCESS_USER_CD(5104, "5104"),
	MENU_NO_INPUT_SUCCESS_CD(5107, "5107"), MENU_NO_INPUT_SUCCESS_USER_CD(5108, "5108"),
	MENU_CREATE_SYS_ERR_CD(6201, "6201"), MENU_CREATE_SYS_USER_ERR_CD(6202, "6202"),
	MENU_CREATE_DB_ERR_CD(9201, "9201"), MENU_CREATE_DB_USER_ERR_CD(9202, "9202"), MENU_DELETE_DB_ERR_CD(9207, "9207"),
	MENU_DELETE_DB_USER_ERR_CD(9208, "9208"), MENU_DELETE_SYS_ERR_CD(6217, "6217"),
	MENU_DELETE_SYS_USER_ERR_CD(6218, "6218"), MENU_NAME_UNIQUE_CHECK_CD(9205, "9205"),
	MENU_NAME_UNIQUE_CHECK_USER_CD(9208, "9206"),

	// Menu - Unique Name and TriggerAction
	MENU_INVALID_INPUT(6205, "6205"), MENU_INVALID_MENU_TYPE_USER_CD(6206, "6206"),
	MENU_INVALID_MENU_TRIGGER_USER_CD(6207, "6207"), MENU_UNIQUE_DB_ERR_CD(6208, "6208"),
	MENU_UNIQUE_SYS_ERR_CD(9209, "9209"), MENU_TRIGGER_ACTION_SUCC_CD(6209, "6209"),
	MENU_TRIGGER_ACTION_DB_ERR_CD(6210, "6210"), MENU_TRIGGER_ACTION_SYS_USER_ERR_CD(9210, "9210"),
	MENU_TRIGGER_ACTION_SYS_ERR_CD(9211, "9211"), MENU_UNIQUE_SYS_USER_ERR_CD(9212, "9212"),
	MENU_UNIQUE_DB_USER_ERR_CD(6211, "6211"), MENU_TRIGGER_ACTION_SUCC_USER_CD(6212, "6212"),
	MENU_TRIGGER_ACTION_DB_USER_ERR_CD(6213, "6213"),

	// MENU fetchMenu
	MENU_FETCH_SUCCESS_CD(5105, "5105"), MENU_FETCH_SUCCESS_USER_CD(5106, "5106"), MENU_NODATA_SUCCESS_CD(5109, "5109"),
	MENU_NODATA_SUCCESS_USER_CD(5110, "5110"), MENU_INVALID_PRODUCT_CD(5111, "5111"),
	MENU_INVALID_PRODUCT_USER_CD(5112, "5112"), MENU_INVALID_REQ_CD(6214, "6214"),
	MENU_INVALID_REQ_USER_CD(6215, "6215"), MENU_FETCH_SYS_ERR_CD(6203, "6203"),
	MENU_FETCH_SYS_USER_ERR_CD(6204, "6204"), MENU_FETCH_DB_ERR_CD(9203, "9203"),
	MENU_FETCH_DB_UDER_ERR_CD(9204, "9204"),

	// UserService getRoles
	ACCESS_SERVICE_SUCCESS_CODE_FETCH_ROLES(5151, "5151"), ACCESS_SERVICE_USER_CODE_FETCH_ROLES(5152, "5152"),
	ERR_ACCESS_SERVICE_API_FETCH_ROLES(6301, "6301"), ERR_ACCESS_SERVICE_USER_FETCH_ROLES(6302, "6302"),

	// UserService mapUserRole
	ACCESS_SERVICE_SUCCESS_CODE_USER_ROLE_MAPPING(5153, "5153"),
	ACCESS_SERVICE_USER_CODE_USER_ROLE_MAPPING(5154, "5154"),
	ACCESS_SERVICE_DEL_SUCC_CODE_USER_ROLE_MAPPING(5181, "5181"),
	ACCESS_SERVICE_DEL_SUCC_USER_CODE_USER_ROLE_MAPPING(5182, "5182"),
	ACCESS_SERVICE_SUCCESS_CODE_SUPER_USER_MAPPING(5656, "5656"),
	ACCESS_SERVICE_USER_CODE_SUPER_USER_MAPPING(5657, "5657"),
	DB_ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING(6303, "6303"),
	DB_ERR_ACCESS_SERVICE_USER_USER_ROLE_MAPPING(6304, "6304"), ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING(9251, "9251"),
	ERR_ACCESS_SERVICE_USER_USER_ROLE_MAPPING(9252, "9252"), ERR_ACCESS_API_VALIDATE_ENUM_CHECK_SEARCH(9954, "9954"),
	ERR_ACCESS_USER_VALIDATE_ENUM_CHECK_SEARCH(9955, "9955"),
	DB_ERR_ACCESS_SERVICE_API_USER_ROLE_MAPPING_EXISTS(6913, "6913"),
	DB_ERR_ACCESS_SERVICE_USER_USER_ROLE_MAPPING_EXISTS(6914, "6914"),
	DB_ERR_ACCESS_SERVICE_API_SUPER_USER_MAPPING_EXISTS(6915, "6915"),
	DB_ERR_ACCESS_SERVICE_USER_SUPER_USER_MAPPING_EXISTS(6916, "6916"),

	// UserService modifyUserRole
	ACCESS_SERVICE_SUCCESS_CODE_MODIFY_USER_ROLE(5155, "5155"), ACCESS_SERVICE_USER_CODE_MODIFY_USER_ROLE(5156, "5156"),
	DB_ERR_ACCESS_SERVICE_API_MODIFY_USER_ROLE(6305, "6305"), DB_ERR_ACCESS_SERVICE_USER_MODIFY_USER_ROLE(6306, "6306"),
	ERR_ACCESS_SERVICE_API_MODIFY_USER_ROLE(9253, "9253"), ERR_ACCESS_SERVICE_USER_MODIFY_USER_ROLE(9254, "9254"),

	// UserService getProductUserRoles
	ACCESS_SERVICE_SUCCESS_CODE_PRODUCT_USER_ROLES(5157, "5157"),
	ACCESS_SERVICE_USER_CODE_PRODUCT_USER_ROLES(5158, "5158"), ERR_ACCESS_SERVICE_API_PRODUCT_USER_ROLES(9255, "9255"),
	ERR_ACCESS_SERVICE_USER_PRODUCT_USER_ROLES(9256, "9256"),

	// UserService getProductUserPermissions
	ERR_ACCESS_SERVICE_API_PRODUCT_PERMISSIOMS(9257, "9257"), ERR_ACCESS_SERVICE_USER_PRODUCT_PERMISSIOMS(9258, "9258"),
	ACCESS_SERVICE_SUCCESS_CODE_PRODUCT_PERMISSIOMS(5159, "5159"),
	ACCESS_SERVICE_USER_CODE_PRODUCT_PERMISSIOMS(5160, "5160"),

	// UserService validateUser
	ACCESS_SERVICE_SUCCESS_CODE_VALIDATE_USER(5161, "5161"), ACCESS_SERVICE_USER_CODE_VALIDATE_USER(5162, "5162"),
	ERR_ACCESS_SERVICE_API_VALIDATE_USER(9259, "9259"), ERR_ACCESS_SERVICE_USER_VALIDATE_USER(9260, "9260"),

	// UserService getRolesForUser
	ACCESS_SERVICE_SUCCESS_CODE_ROLES_PARTICULAR_USER(5163, "5163"),
	ACCESS_SERVICE_USER_CODE_ROLES_PARTICULAR_USER(5164, "5164"),
	ERR_ACCESS_SERVICE_API_ROLES_PARTICULAR_USER(9261, "9261"),
	ERR_ACCESS_SERVICE_USER_ROLES_PARTICULAR_USER(9262, "9262"),
	DB_ERR_ACCESS_SERVICE_API_ROLES_PARTICULAR_USER(6309, "6309"),
	DB_ERR_ACCESS_SERVICE_USER_ROLES_PARTICULAR_USER(6310, "6310"),

	// Warehouse List
	WAREHOUSE_SUCCESS_CODE(5170, "5170"), WAREHOUSE_SUCC_USER_CODE(5171, "5171"),
	WAREHOUSE_USER_ID_ERR_CODE(6370, "6370"), WAREHOUSE_USER_ID_ERR_USER_CODE(6371, "6371"),
	WAREHOUSE_EXP_CODE(9165, "9165"), WAREHOUSE_EXP_USER_CODE(9166, "9166"),

	// ProductPrepareService FetchDefaultFlow
	PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_FLOW(5201, "5201"), PRODUCT_PREPARE_USER_CODE_DEFAULT_FLOW(5202, "5202"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_EXCEPTION(9301, "9301"),
	ERR_PRODUCT_PREPARE_USER_DEFAULT_FLOW_EXCEPTION(9302, "9302"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DBEXCEPTION(6351, "6351"),
	ERR_PRODUCT_PREPARE_USER_DEFAULT_FLOW_DBEXCEPTION(6352, "6352"),

	// ProductPrepareService fetchDashboardFlows
	PRODUCT_PREPARE_SUCCESS_CODE_DASHBOARD_FLOWS(5203, "5203"), PRODUCT_PREPARE_USER_CODE_DASHBOARD_FLOWS(5204, "5204"),
	ERR_PRODUCT_PREPARE_API_DASHBOARD_FLOWS_EXCEPTION(9303, "9303"),
	ERR_PRODUCT_PREPARE_USER_DASHBOARD_FLOWS_EXCEPTION(9304, "9304"),
	ERR_PRODUCT_PREPARE_API_DASHBOARD_FLOWS_DBEXCEPTION(6353, "6353"),
	ERR_PRODUCT_PREPARE_USER_DASHBOARD_FLOWS_DBEXCEPTION(6354, "6354"),

	// ProductPrepareService fetchProductConfigId
	PRODUCT_PREPARE_SUCCESS_CODE_FETCH_CONFIGID(5205, "5205"), PRODUCT_PREPARE_USER_CODE_FETCH_CONFIGID(5206, "5206"),
	ERR_PRODUCT_PREPARE_API_FETCH_CONFIGID_EXCEPTION(9305, "9305"),
	ERR_PRODUCT_PREPARE_USER_FETCH_CONFIGID_EXCEPTION(9306, "9306"),
	ERR_PRODUCT_PREPARE_API_FETCH_CONFIGID_DBEXCEPTION(6355, "6355"),
	ERR_PRODUCT_PREPARE_USER_FETCH_CONFIGID_DBEXCEPTION(6356, "6356"),

	// getDefaultHomeFlow
	PRODUCT_PREPARE_SUCCESS_CODE_DEFAULT_HOME_FLOW(5207, "5207"),
	PRODUCT_PREPARE_USER_CODE_DEFAULT_HOME_FLOW(5208, "5208"), ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW(9307, "9307"),
	ERR_PRODUCT_PREPARE_USER_DEFAULT_HOME_FLOW(9308, "9308"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_HOME_FLOW_DBEXCEPTION(6357, "6357"),
	ERR_PRODUCT_PREPARE_USER_DEFAULT_HOME_FLOW_DBEXCEPTION(6358, "6358"),

	ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_FOUND(6359, "6359"), ERR_PRODUCT_PREPARE_API_HOME_FLOW_DISABLED(6360, "6360"),
	ERR_PRODUCT_PREPARE_API_HOME_FLOW_NOT_PUBLISHED(6361, "6361"),
	ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_FOUND(6362, "6362"),
	ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_DISABLED(6363, "6363"),
	ERR_PRODUCT_PREPARE_API_HOME_FLOW_DEFAULT_FORM_NOT_PUBLISHED(6364, "6364"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DISABLED(6365, "6365"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_NOT_PUBLISHED(6366, "6366"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_FOUND(6367, "6367"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_DISABLED(6368, "6368"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_NOT_PUBLISHED(6369, "6369"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_NOT_FOUND(6372, "6372"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_DEFAULT_FORM_DELETED(6373, "6373"),
	ERR_PRODUCT_PREPARE_API_DEFAULT_FLOW_HOME_FORM_DELETED(6374, "6374"),

	// CustomComponentService createCustomComponent
	CUSTOM_COMPONENT_SUCCESS_CODE_CREATE(5251, "5251"), CUSTOM_COMPONENT_USER_CODE_CREATE(5252, "5252"),
	ERR_CUSTOM_COMPONENT_API_CREATE_EXCEPTION(9401, "9401"), ERR_CUSTOM_COMPONENT_USER_CREATE_EXCEPTION(9402, "9402"),
	ERR_CUSTOM_COMPONENT_API_CREATE_DBEXCEPTION(6401, "6401"),
	ERR_CUSTOM_COMPONENT_USER_CREATE_DBEXCEPTION(6402, "6402"), ERR_USER_CUSTOM_CTRL_UNIQUE_CHECK(9415, "9415"),
	ERR_CUSTOM_CTRL_UNIQUE_CHECK(9414, "9414"),

	// CustomComponentService getCustomComponentById
	CUSTOM_COMPONENT_SUCCESS_CODE_FETCH(5253, "5253"), CUSTOM_COMPONENT_USER_CODE_FETCH(5254, "5254"),
	ERR_CUSTOM_COMPONENT_API_FETCH_EXCEPTION(9403, "9403"), ERR_CUSTOM_COMPONENT_USER_FETCH_EXCEPTION(9404, "9404"),
	ERR_CUSTOM_COMPONENT_API_FETCH_DBEXCEPTION(6403, "6403"), ERR_CUSTOM_COMPONENT_USER_FETCH_DBEXCEPTION(6404, "6404"),

	// CustomComponentService modifyCustomComponent
	CUSTOM_COMPONENT_SUCCESS_CODE_UPDATE(5255, "5255"), CUSTOM_COMPONENT_USER_CODE_UPDATE(5256, "5256"),
	ERR_CUSTOM_COMPONENT_API_UPDATE_EXCEPTION(9405, "9405"), ERR_CUSTOM_COMPONENT_USER_UPDATE_EXCEPTION(9406, "9406"),
	ERR_CUSTOM_COMPONENT_API_UPDATE_DBEXCEPTION(6405, "6405"),
	ERR_CUSTOM_COMPONENT_USER_UPDATE_DBEXCEPTION(6406, "6406"),

	// CustomComponentService deleteCustomComponentById
	CUSTOM_COMPONENT_SUCCESS_CODE_DELETE(5257, "5257"), CUSTOM_COMPONENT_USER_CODE_DELETE(5258, "5258"),
	ERR_CUSTOM_COMPONENT_API_DELETE_EXCEPTION(9407, "9407"), ERR_CUSTOM_COMPONENT_USER_DELETE_EXCEPTION(9408, "9408"),
	ERR_CUSTOM_COMPONENT_API_DELETE_DBEXCEPTION(6407, "6407"),
	ERR_CUSTOM_COMPONENT_USER_DELETE_DBEXCEPTION(6408, "6408"),
	ERR_CUSTOM_COMPONENT_API_CHECK_DELETE_NOT_EMPTY(9409, "9409"),
	ERR_CUSTOM_COMPONENT_USER_CHECK_DELETE_NOT_EMPTY(9410, "9410"), ERR_CUSTOM_COMPONENT_API_CHECK_DELETE(9411, "9411"),
	ERR_CUSTOM_COMPONENT_USER_CHECK_DELETE(9412, "9412"),

	// RegistryService fecthAllRegistries
	REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ALL(5299, "5299"), REGISTRY_SERVICE_USER_CODE_FETCH_ALL(5300, "5300"),
	ERR_REGISTRY_SERVICE_API_FETCH_ALL_EXCEPTION(9501, "9501"),
	ERR_REGISTRY_SERVICE_USER_FETCH_ALL_EXCEPTION(9502, "9502"),
	ERR_REGISTRY_SERVICE_API_FETCH_ALL_DBEXCEPTION(6501, "6501"),
	ERR_REGISTRY_SERVICE_USER_FETCH_ALL_DBEXCEPTION(6502, "6502"),

	// RegistryService fecthRegistryById
	REGISTRY_SERVICE_SUCCESS_CODE_FETCH_ID(5301, "5301"), REGISTRY_SERVICE_USER_CODE_FETCH_ID(5302, "5302"),
	ERR_REGISTRY_SERVICE_API_FETCH_ID_EXCEPTION(9503, "9503"),
	ERR_REGISTRY_SERVICE_USER_FETCH_ID_EXCEPTION(9504, "9504"),
	ERR_REGISTRY_SERVICE_API_FETCH_ID_DBEXCEPTION(6503, "6503"),
	ERR_REGISTRY_SERVICE_USER_FETCH_ID_DBEXCEPTION(6504, "6504"),

	// MasterService fecthAllMasterRegistries
	APIMASTER_SERVICE_SUCCESS_CODE_FETCH_ALL_APIS(5309, "5309"),
	APIMASTER_SERVICE_USER_CODE_FETCH_ALL_APIS(5310, "5310"),
	ERR_APIMASTER_SERVICE_API_FETCH_ALL_APIS_EXCEPTION(9515, "9515"),
	ERR_APIMASTER_SERVICE_USER_FETCH_ALL_APIS_EXCEPTION(9516, "9516"),
	ERR_APIMASTER_SERVICE_API_FETCH_ALL_APIS_DBEXCEPTION(6511, "6511"),
	ERR_APIMASTER_SERVICE_USER_FETCH_ALL_APIS_DBEXCEPTION(6512, "6512"),

	// MasterService fecthApiMasterByRegistryId
	APIMASTER_SERVICE_SUCCESS_CODE_FETCH_REGISTRYID(5311, "5311"),
	APIMASTER_SERVICE_USER_CODE_FETCH_REGISTRYID(5312, "5312"),
	ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_EXCEPTION(9517, "9517"),
	ERR_APIMASTER_SERVICE_USER_FETCH_REGISTRYID_EXCEPTION(9518, "9518"),
	ERR_APIMASTER_SERVICE_API_FETCH_REGISTRYID_DBEXCEPTION(6513, "6513"),
	ERR_APIMASTER_SERVICE_USER_FETCH_REGISTRYID_DBEXCEPTION(6514, "6514"),

	// MasterService fecthApiMasterById
	APIMASTER_SERVICE_SUCCESS_CODE_FETCHBYID(5313, "5313"), APIMASTER_SERVICE_USER_CODE_FETCHBYID(5314, "5314"),
	ERR_APIMASTER_SERVICE_API_FETCHBYID_EXCEPTION(9519, "9519"),
	ERR_APIMASTER_SERVICE_USER_FETCHBYID_EXCEPTION(9520, "9520"),
	ERR_APIMASTER_SERVICE_API_FETCHBYID_DBEXCEPTION(6515, "6515"),
	ERR_APIMASTER_SERVICE_USER_FETCHBYID_DBEXCEPTION(6516, "6516"),

	// Api Import Service createRegistry
	API_IMPORT_SERVICE_SUCCESS_CODE_NEW_REGISTRY(5351, "5351"), API_IMPORT_SERVICE_USER_CODE_NEW_REGISTRY(5352, "5352"),
	ERR_APIIMPORT_API_NEW_REGISTRY_EXCEPTION(9601, "9601"),
	ERR_API_IMPORT_SERVICE_USER_NEW_REGISTRY_EXCEPTION(9602, "9602"),
	ERR_APIIMPORT_API_NEW_REGISTRY_DBEXCEPTION(6601, "6601"),
	ERR_API_IMPORT_SERVICE_USER_NEW_REGISTRY_DBEXCEPTION(6602, "6602"),
	ERR_APIIMPORT_API_VALIDATE_ENUM_CHECK_SEARCH(9956, "9956"),
	ERR_APIIMPORT_USER_VALIDATE_ENUM_CHECK_SEARCH(9957, "9957"),
	VALIDATIION_IMPORT_SWAGGER_FILE(6609, "6609"),
	VALIDATIION_IMPORT_SWAGGER_FILE_USER_CD(6610, "6610"),
	VALIDATIION_EXTERNAL_SWAGGER_FILE_USER_CD(6611, "6611"),

	// Api Import Service modifyRegistry
	API_IMPORT_SERVICE_SUCCESS_CODE_MODIFY_REGISTRY(5353, "5353"),
	API_IMPORT_SERVICE_USER_CODE_MODIFY_REGISTRY(5354, "5354"),
	ERR_APIIMPORT_API_MODIFY_REGISTRY_EXCEPTION(9603, "9603"),
	ERR_API_IMPORT_SERVICE_USER_MODIFY_REGISTRY_EXCEPTION(9604, "9604"),
	ERR_APIIMPORT_API_MODIFY_REGISTRY_DBEXCEPTION(6603, "6603"),
	ERR_API_IMPORT_SERVICE_USER_MODIFY_REGISTRY_DBEXCEPTION(6604, "6604"),

	// Api Import Service uniqueRegistry
	API_IMPORT_SERVICE_SUCCESS_CODE_UNIQUE_REGISTRY(5355, "5355"),
	API_IMPORT_SERVICE_USER_CODE_UNIQUE_REGISTRY(5356, "5356"),
	API_IMPORT_SERVICE_SUCCESS_CODE_NOT_UNIQUE_REGISTRY(6917, "6917"),
	API_IMPORT_SERVICE_USER_CODE_NOT_UNIQUE_REGISTRY(6918, "6918"),
	ERR_APIIMPORT_API_UNIQUE_REGISTRY_EXCEPTION(6605, "6605"),
	ERR_API_IMPORT_SERVICE_USER_UNIQUE_REGISTRY_EXCEPTION(6606, "6606"),

	// Api Import Service overrideExistingApis
	API_IMPORT_SERVICE_SUCCESS_CODE_OVERRIDE_APIS(5359, "5359"),
	API_IMPORT_SERVICE_USER_CODE_OVERRIDE_APIS(5360, "5360"), ERR_APIIMPORT_API_OVERRIDE_APIS_EXCEPTION(9605, "9605"),
	ERR_API_IMPORT_SERVICE_USER_OVERRIDE_APIS_EXCEPTION(9606, "9606"),
	ERR_APIIMPORT_API_OVERRIDE_APIS_DBEXCEPTION(6607, "6607"),
	ERR_API_IMPORT_SERVICE_USER_OVERRIDE_APIS_DBEXCEPTION(6608, "6608"),
	// Generic Search Service - search
	AUTOCOMPLETE_SERVICE_SUCCESS_CODE_RESOURCE_BUNDLE(5400, "5400"),
	AUTOCOMPLETE_SERVICE_USER_CODE_RESOURCE_BUNDLE(5401, "5401"),
	ERR_AUTOCOMPLETE_SERVICE_RESOURCE_BUNDLE_EXCEPTION(9701, "9701"),
	ERR_AUTOCOMPLETE_SERVICE_USER_RESOURCE_BUNDLE_EXCEPTION(9702, "9702"),
	ERR_AUTOCOMPLETE_SERVICE_RESOURCE_BUNDLE_DBEXCEPTION(6701, "6701"),
	ERR_AUTOCOMPLETE_SERVICE_USER_RESOURCE_BUNDLE_DBEXCEPTION(6702, "6702"),

	// AutoComplete Search Service - configType
	APP_CONFIG_AUTOCOMPLETE_SERVICE_SUCCESS_CODE_CONFIG_TYPE(5402, "5402"),
	APP_CONFIG_AUTOCOMPLETE_SERVICE_USER_CODE_CONFIG_TYPE(5403, "5403"),
	APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_CONFIG_TYPE_EXCEPTION(9703, "9703"),
	APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_USER_CONFIG_TYPE_EXCEPTION(9704, "9704"),
	APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_CONFIG_TYPE_DBEXCEPTION(6703, "6703"),
	APP_CONFIG_ERR_AUTOCOMPLETE_SERVICE_USER_CONFIG_TYPE_DBEXCEPTION(6704, "6704"),

	/* Resource bundle success and error codes */
	/* createResourceBundle */
	RESOURCE_BUNDLE_SERVICE_SUCCESS_CODE_CREATE(5450, "5450"), RESOURCE_BUNDLE_SERVICE_USER_CODE_CREATE(5451, "5451"),
	ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_EXCEPTION(9801, "9801"),
	ERR_RESOURCE_BUNDLE_SERVICE_USER_CREATE_EXCEPTION(9802, "9802"),
	ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_DBEXCEPTION(6801, "6801"),
	ERR_RESOURCE_BUNDLE_SERVICE_USER_CREATE_DBEXCEPTION(6802, "6802"),
	ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_CHECK(6807, "6807"),
	ERR_RESOURCE_BUNDLE_SERVICE_USER_CREATE_CHECK(6808, "6808"),
	ERR_RESOURCE_BUNDLE_SERVICE_API_CREATE_CHECK_RBKEY_EMPTY(6809, "6809"),
	ERR_RESOURCE_BUNDLE_SERVICE_USER_CREATE_CHECK_RBKEY_EMPTY(6810, "6810"),

	/* getResourceBundles */
	RESOURCE_BUNDLE_SERVICE_SUCCESS_CODE_FETCH(5452, "5452"), RESOURCE_BUNDLE_SERVICE_USER_CODE_FETCH(5453, "5453"),
	ERR_RESOURCE_BUNDLE_SERVICE_API_FETCH_EXCEPTION(9803, "9803"),
	ERR_RESOURCE_BUNDLE_SERVICE_USER_FETCH_EXCEPTION(9804, "9804"),
	ERR_RESOURCE_BUNDLE_SERVICE_API_FETCH_DBEXCEPTION(6803, "6803"),
	ERR_RESOURCE_BUNDLE_SERVICE_USER_FETCH_DBEXCEPTION(6804, "6804"),

	// Locale success codes
	LOCALE_SUCCESS_CODE_UPDATE(5801, "5801"), LOCALE_SUCCESS_USER_CODE_UPDATE(5802, "5802"),
	LOCALE_ERR_INVALID_LOCALE(5803, "5803"), LOCALE_ERR_USER_CDE_INVALID_LOCALE(5804, "5804"),
	LOCALE_DB_ERR_UPDATE_LOCALE(5805, "5805"), LOCALE_DB_ERR_USER_CODE_UPDATE_LOCALE(5806, "5806"),
	LOCALE_ERR_UPDATE_LOCALE(5807, "5807"), LOCALE_ERR_USER_CODE_UPDATE_LOCALE(5808, "5808"),

	// User roles and permissions in auth
	ERR_LOGIN_ROLESPERMS_API_REST_CLIENT_EXCEPTION(9805, "9805"),
	ERR_LOGIN_ROLESPERMS_USER_REST_CLIENT_EXCEPTION(9806, "9806"), ERR_LOGIN_ROLESPERMS_API_EXCEPTION(6805, "6805"),
	ERR_LOGIN_ROLESPERMS_USER_EXCEPTION(6806, "6806"),

	// AppService createAppConfiguration
	APP_CONFIG_SERVICE_SUCCESS_CONTEXT_VARIABLES(5601, "5601"), APP_CONFIG_SERVICE_USER_CONTEXT_VARIABLES(5602, "5602"),
	APP_CONFIG_SERVICE_SUCCESS_GLOBAL_VARIABLES(5607, "5607"), APP_CONFIG_SERVICE_USER_GLOBAL_VARIABLES(5608, "5608"),
	APP_CONFIG_SERVICE_SUCCESS_APPLICATION_VARIABLES(5609, "5609"),
	APP_CONFIG_SERVICE_USER_APPLICATION_VARIABLES(5610, "5610"),
	APP_CONFIG_SERVICE_SUCCESS_INTERNAL_VARIABLES(5611, "5611"),
	APP_CONFIG_SERVICE_USER_INTERNAL_VARIABLES(5612, "5612"),
	ERR_APP_CONFIG_SERVICE_API_CREATION_DBEXCEPTION(6901, "6901"),
	ERR_APP_CONFIG_SERVICE_CREATION_DBEXCEPTION(6902, "6902"), ERR_APP_CONFIG_SERVICE_API_CREATION_CHECK(6907, "6907"),
	ERR_APP_CONFIG_SERVICE_USER_CREATION_CHECK(6908, "6908"),
	ERR_APP_CONFIG_SERVICE_API_CREATION_EXCEPTION(9901, "9901"),
	ERR_APP_CONFIG_SERVICE_USER_CREATION_EXCEPTION(9902, "9902"),

	// AppService getAppConfig based on type and name
	APP_CONFIG_SERVICE_SUCCESS_CODE_FETCH(5603, "5603"), APP_CONFIG_SERVICE_USER_CODE_FETCH(5604, "5604"),
	ERR_APP_CONFIG_SERVICE_API_FETCH_DBEXCEPTION(6903, "6903"),
	ERR_APP_CONFIG_SERVICE_USER_FETCH_DBEXCEPTION(6904, "6904"),
	ERR_APP_CONFIG_SERVICE_API_FETCH_EXCEPTION(9903, "9903"), ERR_APP_CONFIG_SERVICE_USER_FETCH_EXCEPTION(9904, "9904"),

	// AppService getAppConfigAll based on configType
	APP_CONFIG_SERVICE_SUCCESS_CODE_FETCHALL(5605, "5605"), APP_CONFIG_SERVICE_USER_CODE_FETCHALL(5606, "5606"),
	ERR_APP_CONFIG_SERVICE_API_FETCHALL_DBEXCEPTION(6905, "6905"),
	ERR_APP_CONFIG_SERVICE_USER_FETCHALL_DBEXCEPTION(6906, "6906"),
	ERR_APP_CONFIG_SERVICE_API_FETCHALL_EXCEPTION(9905, "9905"),
	ERR_APP_CONFIG_SERVICE_USER_FETCHALL_EXCEPTION(9906, "9906"), ERR_APP_CONFIG_SERVICE_API_TYPE_CHECK(9907, "9907"),
	ERR_APP_CONFIG_SERVICE_USER_TYPE_CHECK(9908, "9908"),

	// AppService Update configvalue

	APP_CONFIG_SERVICE_SUCCESS_UPDATE_VALUE(5613, "5613"), APP_CONFIG_SERVICE_USER_CODE_UPDATE_VALUE(5614, "5614"),
	ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_DBEXCEPTION(6909, "6909"),
	ERR_APP_CONFIG_SERVICE_USER_UPDATE_VALUE_DBEXCEPTION(6910, "6910"),
	ERR_APP_CONFIG_SERVICE_API_UPDATE_VALUE_EXCEPTION(9909, "9909"),
	ERR_APP_CONFIG_SERVICE_USER_UPDATE_VALUE_EXCEPTION(9910, "9910"),
	ERR_APP_CONFIG_SERVICE_API_UPDATE_CONFIGTYPE(9913, "9913"),
	ERR_APP_CONFIG_SERVICE_USER_UPDATE_CONFIGTYPE(9914, "9914"),

	// AppService clear configvalue

	APP_CONFIG_SERVICE_SUCCESS_CLEAR_VALUE(5615, "5615"), APP_CONFIG_SERVICE_USER_CLEAR_VALUE(5616, "5616"),
	ERR_APP_CONFIG_SERVICE_API_CLEAR_VALUE_DBEXCEPTION(6911, "6911"),
	ERR_APP_CONFIG_SERVICE_USER_CLEAR_VALUE_DBEXCEPTION(6912, "6912"),
	ERR_APP_CONFIG_SERVICE_API_CLEAR_VALUE_EXCEPTION(9911, "9911"),
	ERR_APP_CONFIG_SERVICE_USER_CLEAR_VALUE_EXCEPTION(9912, "9912"),

	// ProductInvokeController
	PRODUCT_INVOKE_SERVICE_SUCCESS_CODE_001(5651, "5651"), PRODUCT_INVOKE_SERVICE_USER_CODE_001(5652, "5652"),
	ERR_PRODUCT_INVOKE_SERVICE_API_001(9950, "9950"), ERR_PRODUCT_INVOKE_SERVICE_USER_002(9951, "9951"),

	// ImportServiceImpl
	IMPORT_SERVICE_API_SUCCESS_CODE(5653, "5653"), IMPORT_SERVICE_API_USER_CODE(5654, "5654"),
	SAVE_IMPORTED_DATA_SUCCESS_CODE(5659, "5659"), SAVE_IMPORTED_DATA_USER_CODE(5660, "5660"),
	SAVE_IMPORTED_DATA_PUBLISH_USER_CODE(5661, "5661"),
	ERR_IMPORT_SERVICE_API(9952, "9952"), ERR_IMPORT_SERVICE_API_USER_CODE(9953, "9953"),
	ERR_IMPORT_SERVICE_EMPTY_FILE(9960, "9960"), ERR_IMPORT_SERVICE_EMPTY_FILE_USER_CODE(9961, "9961"),
	ERR_SAVE_IMPORTED_DATA(9967, "9967"), ERR_SAVE_IMPORTED_DATA_USER_CODE(9968, "9968"),
	ERR_IMPORT_VIOLATION(9969, "9969"), ERR_IMPORT_VIOLATION_USER_CODE(9970, "9970"),
	ERR_IMPORT_NESTED_FOLDER(9971, "9971"), ERR_IMPORT_NESTED_FOLDER_USER_CODE(9972, "9972"),
	
	// Orchestration
	ERR_ORCHESTRATION_API_BADLAYER_EXCEPTION(9973,"9973"),ERR_ORCHESTRATION_USER_API_BADLAYER_EXCEPTION(9974,"9974"),
	ERR_ORCHESTRATION(9975, "9975"),

	// ExtensionHistory comparison for new field is disallowed
	COMPARE_DIS_ALLOWED_API_CODE(5701, "5701"), COMPARE_DIS_ALLOWED_USER_CODE(5702, "5702"),
	COMPARE_MISSING_PARENT_API_CODE(5709, "5709"), COMPARE_MISSING_PARENT_USER_CODE(5710, "5710"),
	// HotKeyService fecthAllHotKeysMapped

	HOTKEY_SERVICE_SUCCESS_CODE_FETCH_ALL(5703, "5703"), HOTKEY_SERVICE_USER_CODE_FETCH_ALL(5704, "5704"),
	ERR_HOTKEY_SERVICE_API_FETCH_ALL_EXCEPTION(9958, "9958"), ERR_HOTKEY_SERVICE_USER_FETCH_ALL_EXCEPTION(9959, "9959"),
	ERR_HOTKEY_SERVICE_API_FETCH_ALL_DBEXCEPTION(6919, "6919"),
	ERR_HOTKEY_SERVICE_USER_FETCH_ALL_DBEXCEPTION(6920, "6920"),

	// UserLanguageCodeService fecthAllUserLanguageCodes

	USER_LANGUAGE_CODE_SERVICE_SUCCESS_CODE_FETCH_ALL(5705, "5705"),
	USER_LANGUAGE_CODE_SERVICE_USER_CODE_FETCH_ALL(5706, "5706"),
	ERR_USER_LANGUAGE_CODE_SERVICE_API_FETCH_ALL_EXCEPTION(9962, "9962"),
	ERR_USER_LANGUAGE_CODE_SERVICE_USER_FETCH_ALL_EXCEPTION(9963, "9963"),
	ERR_USER_LANGUAGE_CODE_SERVICE_API_FETCH_ALL_DBEXCEPTION(6921, "6921"),
	ERR_USER_LANGUAGE_CODE_SERVICE_USER_FETCH_ALL_DBEXCEPTION(6922, "6922"),

	// PreAndPostProcessorServiceImpl
	PRE_AND_POST_PROCESSOR_UPLOAD_CONFIRM_SUCCESS(5708, "5708"), PRE_AND_POST_PROCESSOR_UPLOAD_SUCCESS(5707, "5707"),
	ERR_PRE_AND_POST_PROCESSOR_FILE_NULL(9964, "9964"), DB_ERR_PRE_AND_POST_PROCESSOR_UPLOAD(6923, "6923"),
	ERR_PRE_AND_POST_PROCESSOR_UPLOAD(9965, "9965"), ERR_PRE_AND_POST_PROCESSOR_EXISTING(9966, "9966");

	/** The field code of type int */
	private final int code;
	/** The field key of type String */
	private final String key;

	/**
	 * @param code
	 * @param msgId
	 */
	private BffResponseCode(int code, String key) {
		this.code = code;
		this.key = key;
	}

	/**
	 * @return the code of type int
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @return the key of type String
	 */
	public String getKey() {
		return key;
	}
}