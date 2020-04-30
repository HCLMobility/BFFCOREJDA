/**
 * 
 */
package com.jda.mobility.framework.extensions.utils;

public class BffAdminConstantsUtils {

	private BffAdminConstantsUtils() {
		super();
	}

	public enum FlowType {
		PUBLISHED, UNPUBLISHED, ALL;
	}

	public enum MenuType {
		MAIN("MAIN"), FORM_CONTEXT("FORM_CONTEXT"), BOTTOM_BAR("BOTTOM_BAR"), GLOBAL_CONTEXT("GLOBAL_CONTEXT");
		private final String type;

		/**
		 * @param type
		 */
		private MenuType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}
	}

	public enum FormType {
		INDEP_FORM("INDEP_FORM"), OUTDEP_FORM("OUTDEP_FORM"), ORPHAN_FORM("ORPHAN_FORM"), TABBED_FORM("TABBED_FORM");
		private final String type;

		/**
		 * @param type
		 */
		private FormType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}

	}

	public enum ActionType {
		CONFIRM_PUBLISH("CONFIRM_PUBLISH", true), CHECK_PUBLISH("CHECK_PUBLISH", false), SAVE("NO_PUBLISH", false);

		/** The field type of type String */
		private final String type;
		/** The field value of type boolean */
		private final boolean value;

		/**
		 * @param type
		 * @param value
		 */
		private ActionType(String type, boolean value) {
			this.type = type;
			this.value = value;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}

		/**
		 * @return the value of type boolean
		 */
		public boolean isValue() {
			return value;
		}

	}

	public enum RequestType {
		GET("GET"), POST("POST"), PUT("PUT"), DELETE("DELETE"), PATCH("PATCH"), OPTIONS("OPTIONS");
		private final String type;

		/**
		 * @param type
		 */
		private RequestType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}

	}

	public enum TriggerAction {
		NAVIGATE_TO_WORKFLOW, NAVIGATE_TO_FORM, CONDITIONAL_ACTION, SHOW_ALERT, DISMISS, INVOKE_API, NO_ACTION,
		SHOW_TOAST, SHOW_SNACKBAR, QUESTIONNAIRE, VALIDATE_FORM, CONTEXT, GLOBAL;
	}

	public enum SearchType {
		RESOURCE_BUNDLE("RESOURCE_BUNDLE", true), PRODUCT_PERMISSION("PRODUCT_PERMISSION", false),
		PRODUCT_ROLE("PRODUCT_ROLE", false), APP_CONFIG_GLOBAL("APP_CONFIG_GLOBAL", false),
		APP_CONFIG_CONTEXT("APP_CONFIG_CONTEXT", false);
		/** The field type of type String */
		private final String type;
		/** The field value of type boolean */
		private final boolean value;

		/**
		 * @param type
		 * @param value
		 */
		private SearchType(String type, boolean value) {
			this.type = type;
			this.value = value;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}

		/**
		 * @return the value of type boolean
		 */
		public boolean isValue() {
			return value;
		}

	}

	public enum ChannelType {
		ADMIN_UI("ADMIN_UI"), MOBILE_RENDERER("MOBILE_RENDERER"), BFFCORE("BFFCORE");
		private final String type;

		/**
		 * @param type
		 */
		private ChannelType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}

	}

	public enum AppCfgRequestType {
		GLOBAL("GLOBAL"), CONTEXT("CONTEXT"), APPLICATION("APPLICATION"), INTERNAL("INTERNAL");

		private final String type;

		/**
		 * @param type
		 */
		private AppCfgRequestType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}

	}

	public enum DefaultType {
		CHECK_DEFAULT("CHECK_DEFAULT"), CONFIRM_DEFAULT("CONFIRM_DEFAULT");

		private String type;

		private DefaultType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}
	}

	public enum DisableType {
		CHECK_DISABLE("CHECK_DISABLE"), CONFIRM_DISABLE("CONFIRM_DISABLE");

		private String type;

		private DisableType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}
	}

	public enum FormStatus {
		UNPUBLISH("UNPUBLISH"), ORPHAN("ORPHAN"), ALL("ALL");
		private String type;

		/**
		 * @param type
		 */
		private FormStatus(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}
	}

	public enum CustomFormFilterMode {
		BASIC("BASIC"), ALL("ALL");
		private String type;

		/**
		 * @param type
		 */
		private CustomFormFilterMode(String type) {
			this.type = type;
		}

		/**
		 * @return the type
		 */
		public String getType() {
			return type;
		}
	}

	public enum ExtensionType {
		FLOW("FLOW", 1), FORM("FORM", 2), FIELD("FIELD", 3);

		private String name;
		private int extTypeId;

		private ExtensionType(String name, int extTypeId) {
			this.name = name;
			this.extTypeId = extTypeId;
		}

		public String getName() {
			return name;
		}

		public int getExtTypeId() {
			return extTypeId;
		}
	}

	public enum DeleteType {
		CHECK_DELETE("CHECK_DELETE"), CONFIRM_DELETE("CONFIRM_DELETE");

		private String type;

		private DeleteType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}
	}

	public enum UserRoleActionType {
		ADD_USER_ROLE("ADD_USER_ROLE"), MODIFY_USER_ROLE("MODIFY_USER_ROLE"), DELETE_USER_ROLE("DELETE_USER_ROLE");

		private String type;

		private UserRoleActionType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}
	}

	public enum ApiRegistryType {
		INTERNAL("INTERNAL"), EXTERNAL("EXTERNAL"), ORCHESTRATION("ORCHESTRATION"), LOCAL("LOCAL");

		private String type;

		private ApiRegistryType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}
	}

	public enum ResourceBundleType {
		INTERNAL("INTERNAL"), ADMIN_UI("ADMIN_UI"), MOBILE("MOBILE");

		private String type;

		private ResourceBundleType(String type) {
			this.type = type;
		}

		/**
		 * @return the type of type String
		 */
		public String getType() {
			return type;
		}
	}

	public enum ExportType {
		ALL, FLOW, CUSTOM_CONTROL, MENU, RESOURCE_BUNDLE, APP_CONFIG, REGISTRY, API_ORCHESTRATION
	}

	public enum ImportType {
		FLOW, FORM, CUSTOM_CONTROL, MENU, RESOURCE_BUNDLE, APP_CONFIG, REGISTRY, API_ORCHESTRATION
	}

	public enum ImportAction {
		VALIDATE, SAVE, SAVE_PUBLISH
	}

	public enum CloneType {
		FLOW, FORM_IN_SAME_FLOW, FORM_IN_DIFF_FLOW
	}

	public enum LayerMode {
		CURRENT_LAYER, ALL
	}

	public enum ApiUploadMode {
		CHECK_UPLOAD, CONFIRM_UPLOAD
	}

	public enum TenantConfigName {
		INACTIVE_SESSION_PERIOD
	}

	public enum SessionAttribute {
		CHANNEL, DEVICE_NAME, FLOW_ID, FORM_ID, LOCALE, PRODUCT_CONFIG_ID, REFERER_URL, SESSION_RECORDING, USER_ID,
		VERSION, WAREHOUSE_ID, MENU_ID
	}

	public static final String IMPORT_VIOLATION = "IMPORT_VIOLATION";
	public static final String NESTED_FOLDER = "NESTED_FOLDER";
	/** The field EMPTY_SPACES of type String */
	public static final String EMPTY_SPACES = "";
	/** The field COMMA of type String */
	public static final String COMMA = ",";
	/** The field PUBLISHED_FLOW of type String */
	public static final String PUBLISHED_FLOW = "PUBLISHED_FLOW";
	/** The field UNPUBLISHED_FLOW of type String */
	public static final String UNPUBLISHED_FLOW = "UNPUBLISHED_FLOW";
	/** The field LOCALE of type String */
	public static final String LOCALE = "en-US";

	/** The field UNPUBLISHED_FLOWS of type String */
	public static final String UNPUBLISHED_FLOWS = "UNPUBLISHED_FLOWS";
	/** The field UNPUBLISHED_FORMS of type String */
	public static final String UNPUBLISHED_FORMS = "UNPUBLISHED_FORMS";
	/** The field API_COUNT of type String */
	public static final String API_COUNT = "API_COUNT";
	/** The field CUSTOM_COMPONENT_COUNT of type String */
	public static final String CUSTOM_COMPONENT_COUNT = "CUSTOM_COMPONENT_COUNT";
	/** The field ORPHAN_FORMS of type String */
	public static final String ORPHAN_FORMS = "ORPHAN_FORMS";
	/** The field API_REGISTRY_COUNT of type String */
	public static final String API_REGISTRY_COUNT = "API_REGISTRY_COUNT";
	public static final String DEPENDENCY_BREAK = "SET_VALUE_BREAK";
	public static final String DEPENDENCY_OBJ_BREAK = "SET_OBJ_BREAK";
	/** The field Product_Master_Code of type String */
	public static final String PRODUCT_MASTER_CODE = "WMS";
	/** The field Role_Master_Code of type String */
	public static final String ROLE_MASTER_CODE = "JDA Product Development";
	/** The field SINGLE_COLON of type String */
	public static final String SINGLE_COLON = ":";
	/** The field Single_Colon of type String */
	public static final String REFERENCE = "$ref";
	/** The field VERSION_SWAGGER of type String */
	public static final String VERSION_SWAGGER = "1.0";
	/** The field DEFINITIONS of type String */
	public static final String DEFINITIONS = "#/definitions/";
	/** The field OBJECT of type String */
	public static final String OBJECT_TYPE = "object";
	/** The field SUPER_ADMIN of type String */
	public static final String SUPER_ADMIN = "SUPER ADMIN";
	/** The field WMS of type String */
	public static final String WMS = "WMS";
	/** The field ADMIN of type String */
	public static final String ADMIN = "ADMIN";
	/** The field Warehouse1_DEFAULT of type String */
	public static final String WAREHOUSE_DEFAULT = "___";
	/** The field flow_id of type String */
	public static final String FLOW_ID = "flow_id";
	public static final String API = "api_action";
	public static final String DB_EXP_MSG = "Database Exception occurred for : {}";
	public static final String APP_EXP_MSG = "Exception occurred for : {}";
	public static final String MENU_NOT_FOUND = "No inputs/Input Mismatch";
	public static final String DEFAULT_FLOW_KEY = "DEFAULT_FLOW_ID";
	public static final String HOME_FLOW_KEY = "HOME_FLOW_ID";
	public static final String JSON_NULL_STR = "null";
	public static final String CUSTOM_CONTAINER = "customContainer";
	public static final String RESOURCE_BUNDLE = "resourcebundle";
	public static final String SUCCESS = "success";
	public static final String SUCCESS_IN_PASCAL_CASE = "Success";
	public static final String PARENT = "parent";
	public static final String HTTP = "http";
	public static final String SUPER = "SUPER";
	public static final String QUERY = "QUERY";
	public static final String PATH = "PATH";
	public static final String BODY = "REQUEST BODY";
	public static final String INVALID_IDENTIFIER_MESSAGE = "Invalid Identifier.Form Deletion mode is not valid.";
	public static final String FORM_DELETION_MESSAGE = "check form deletion : Deletion valid.No link form found.";
	public static final String ROLE_PRI_ALREADY_MAPPED_MSG = "Already Role and privileges are mapped to each other";
	public static final String KSESSION_RULES = "ksession-rules";
	public static final String ROLE_USER = "ROLE_USER";
	public static final String AUTHORIZATION = "Authorization";
	public static final String BEARER = "Bearer ";
	public static final String AUTHENTICATION_FAILED = "Authentication Failed.";
	public static final String AUTH_PAYLOAD_USER_ID = "{\"usr_id\":\"";
	public static final String ROLEID = "roleId";
	public static final String AUTH_PARAM1 = "usr_id";
	public static final String AUTH_PARAM2 = "password";
	public static final String PERIOD = ".";
	public static final String QUESTION_MARK = "?";
	public static final String EQUAL = "=";
	public static final String AMPERSAND = "&";
	public static final String OPEN_CURLY_BRACES = "{";
	public static final String CLOSE_CURLY_BRACES = "}";
	public static final String OPEN_SQUARE_BRACES = "[";
	public static final String CLOSE_SQUARE_BRACES = "]";
	public static final String DOUBLE_QUOTE = "\"";
	public static final String FORWARD_SLASH = "/";
	public static final String COLON = ":";
	public static final String ASTERISK = "*";
	public static final String INTEGER_STRING = "integer";
	public static final String NUMBER_STRING = "number";
	public static final String BOOLEAN_STRING = "boolean";
	public static final String STRING_TYPE = "string";
	public static final String WORKFLOW = "workflow";
	public static final String FLOWID = "flowId";
	public static final String REQHEADER_HOST_KEY = "host";
	public static final String VERSIONING = "VERSIONING";
	public static final String EXTENDED = "EXTENDED";
	public static final long FLOW_INITIAL_VERSION = 1;

	public static final String COLUMNS = "columns";
	public static final String COLUMN = "column";
	public static final String DATAGRID = "datagrid";
	public static final String LISTVIEW = "listview";

	public static final String SERVICE_PACKAGE_NAME = "com.jda.mobility.framework.extensions.service";
	public static final String CONTROLLER_PACKAGE_NAME = "com.jda.mobility.framework.extensions.controller";

	public static final String RB_TEST_KEY = "TEST_KEY_001";
	public static final String RB_TEST_VAL = "TEST_VAL_001";
	public static final String PREPEND = "PRE";
	public static final String ACTION_TYPE = "actionType";

	public static final String PROPERTIES = "properties";
	public static final String CONDITIONS = "conditions";
	public static final String DEFAULT_FORM_ID = "defaultFormId";
	public static final String FORM = "form";
	public static final String FORM_ID = "formId";
	public static final String ON_CONDITION_ACTION = "onConditionAction";
	public static final String FORM_NAME = "formName";

	public static final String WAREHOUSE_ID = "warehouseId";
	public static final String WAREHOUSES = "warehouses";
	public static final String NO_DEFAULT_OR_HOME_FLOW = "Default flow or home flow is not configured";
	public static final String SPACE = " ";
	public static final String PRE_PROCESSOR = "Pre";
	public static final String POST_PROCESSOR = "Post";
	public static final String RBKEY = "rbkey";
	public static final String RBVALUE = "rbvalue";

	// Orchestration and prepost processor-related constants
	public static final String API_ORCHESTRATION_GRAMMAR_FILE = "ApiOrchestration-grammar.dsl";
	public static final String PREPOSTPROC_GRAMMAR_FILE = "Prepostproc-grammar.dsl";
	public static final String EMPTY = "";
	public static final String HYPEN = "-";
	public static final String UNDERSCORE = "_";
	public static final String ALL = "ALL";

	// Analytics constants
	public static final String APP_ANALYTICS_ENABLED_KEY = "app.analytics.enabled";
	public static final String APP_ANALYTICS_API_URLSCHEME_KEY = "app.analytics.url.scheme";
	public static final String APP_ANALYTICS_API_HOST_KEY = "app.analytics.server.api.host";
	public static final String APP_ANALYTICS_API_CONTEXTPATH_KEY = "app.analytics.server.api.contextpath";
	public static final String APP_ANALYTICS_API_STARTTIME_KEY = "app.analytics.api.startime";

	public static final String APP_ANALYTICS_API_VERSION_KEY = "app.analytics.server.api.version";
	public static final String APP_ANALYTICS_API_TRACKID_KEY = "app.analytics.server.api.trackid";
	public static final String APP_ANALYTICS_API_PAGEHITTYPE_KEY = "app.analytics.server.api.pagehittype";
	
	public static final String DEVICE_ID = "DeviceID";
	
	public static final String FIELD_PATTERN = "(FIELD.[a-zA-Z0-9].[^\" ]*)";
	public static final String FIELD = "FIELD";
	
	public static final String EXP_MSG = "System Exception occurred for : {}";

}