package com.jda.mobility.framework.extensions.service.impl.migration.exporter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.jda.mobility.framework.extensions.entity.Form;
import com.jda.mobility.framework.extensions.service.impl.migration.ApiKey;
import com.jda.mobility.framework.extensions.service.impl.migration.FileSuffix;
import com.jda.mobility.framework.extensions.service.impl.migration.References;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

/**
 * Exports {@link Form form} entities
 * <p>
 * This exporter will <em>not</em> include forms that
 * have not been published.
 */
@Service
public class FormExporter {

    private final ObjectMapper mapper;

    public FormExporter(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    /**
     * Takes the specified form and serializes it into JSON.
     * @param form The form to export
     * @param refs A collection of references to dependencies that
     * should be updated to include references found in the specified
     * form.
     * @return The returned map may be empty if the form could not
     * be published, but it will <em>never</em> be {@code null}.
     */
    public Map<String, JsonNode> exportToJson(Form form, References refs) {
        Map<String, JsonNode> exports = new HashMap<>(1);
        if (!form.isPublished()) {
            LOGGER.warn("Form {} is not published. It will not be exported!", form.getUid());
            return exports;
        }
        String formName = form.getName();
        ObjectNode publishedJson = getPublishedJson(form);

        collectReferencesInForm(publishedJson, refs);

        exports.put(FileSuffix.FORM.prepend(formName), mapper.valueToTree(form));

        return exports;
    }

    private ObjectNode getPublishedJson(Form form) {
        try {
            return (ObjectNode) mapper.readTree(form.getPublishedForm());
        }
        catch (IOException e) {
            throw new IllegalStateException("Published JSON is invalid for form " + form.getUid(), e);
        }
    }

    /**
     * Parse condition strings for references to global and context variables.
     * If one or more references are found, add them to the references collection.
     */
    private void collectVarRefs(String condition, References references) {
        if (StringUtils.isEmpty(condition)) {
            return;
        }

        Matcher globalMatcher = GLOBAL_VAR_RE.matcher(condition);
        while (globalMatcher.find()) {
            references.globalVars.add(globalMatcher.group(1));
        }
        globalMatcher = GLOBALVALUE_RE.matcher(condition);
        while (globalMatcher.find()) {
            references.globalVars.add(globalMatcher.group(1));
        }

        Matcher contextMatcher = CONTEXT_VAL_RE.matcher(condition);
        while (contextMatcher.find()) {
            references.contextVals.add(contextMatcher.group(1));
        }
        contextMatcher = CONTEXTVALUE_RE.matcher(condition);
        while (contextMatcher.find()) {
            references.contextVals.add(contextMatcher.group(1));
        }
    }

    /**
     * The SetValue node has a particular structure (an array of
     * objects containing condition and value nodes). Dig through
     * that structure to find any references to globals or context
     * variables.
     */
    private void collectSetValueVarRefs(JsonNode conditionals, References references) {
        if (!conditionals.isArray()) {
            return;
        }

        conditionals.forEach(conditional -> {
            collectVarRefs(conditional.path(CONDITION_NODE).asText(), references);
            collectVarRefs(conditional.path("value").asText(), references);
        });
    }

    /**
     * Forms may refer to many different entities: resource bundles, global/context
     * variables, apis, menus, etc.
     */
    private void collectReferencesInForm(ObjectNode form, References references) {
        safeAdd(references.resourceBundleKeys, form.path("formTitle").path(RBKEY_NODE).asText());

        collectReferencesInComponents(form.path("components"), references);

        JsonNode formProperties = form.path("formProperties");
        collectReferencesInEvents(formProperties.path("events"), references);
        collectReferencesInMenus(formProperties.path("menus"), references);
        collectReferencesInAction(formProperties.path("gs1Form"), references);
    }

    /**
     * Menus can contain label references as well as api invocations (which
     * can internally contain other references)
     */
    private void collectReferencesInMenus(JsonNode menus, References references) {
        if (!menus.isArray()) {
            return;
        }

        menus.forEach(menu -> {
            safeAdd(references.resourceBundleKeys, menu.path("menuName").path(RBKEY_NODE).asText());
            collectReferencesInMenus(menu.path("subMenus"), references);
            collectReferencesInAction(menu.path("action"), references);
            safeAdd(references.menuIds, menu.path("uid").asText());
        });
    }

    /**
     * Components can contain references to labels, apis, and custom components. Apis
     * and custom components can in turn contain their own references.
     */
    private void collectReferencesInComponents(JsonNode components, References references) {
        if (!components.isArray()) {
            return;
        }

        components.forEach(component -> {
            safeAdd(references.resourceBundleKeys, component.path("label").path(RBKEY_NODE).asText());
            safeAdd(references.customComponentIds, component.path("customComponentId").asText());
            collectReferencesInFieldDependencies(component.path("fieldDependency"), references);
            collectReferencesInEvents(component.path("events"), references);
            collectReferencesInComponents(component.path("components"), references);
            // Sometimes things are nested in 'columns' instead of 'components'
            collectReferencesInComponents(component.path("columns"), references);
        });
    }

    /**
     * Fields have conditions tied to several different properties. All these conditions
     * need to be parsed to search for global and context variable references.
     */
    private void collectReferencesInFieldDependencies(JsonNode node, References references) {
        if (!node.isObject()) {
            return;
        }
        ObjectNode dependencies = (ObjectNode) node;
        collectVarRefs(dependencies.path("disable").path(CONDITION_NODE).asText(), references);
        collectVarRefs(dependencies.path("enable").path(CONDITION_NODE).asText(), references);
        collectVarRefs(dependencies.path("hide").path(CONDITION_NODE).asText(), references);
        collectVarRefs(dependencies.path("show").path(CONDITION_NODE).asText(), references);
        collectVarRefs(dependencies.path("setRequired").path(CONDITION_NODE).asText(), references);
        collectSetValueVarRefs(dependencies.path("setValue"), references);
    }

    /**
     * Events contain actions, which can reference apis or global/context variables.
     */
    private void collectReferencesInEvents(JsonNode events, References references) {
        if (!events.isArray()) {
            return;
        }

        events.forEach(event -> collectReferencesInAction(event.path("action"), references));
    }

    /**
     * Action structure is somewhat arbitrary. There are a few different properties we
     * can look for that tell us what references are present.
     */
    private void collectReferencesInAction(JsonNode node, References references) {
        if (node instanceof ObjectNode) {
            ObjectNode obj = (ObjectNode) node;
            Iterator<Entry<String, JsonNode>> fields = obj.fields();
            while (fields.hasNext()) {
                Entry<String, JsonNode> entry = fields.next();
                switch (entry.getKey()) {
                    case RBKEY_NODE:
                        safeAdd(references.resourceBundleKeys, entry.getValue().asText());
                        break;
                    case "variableName":
                        JsonNode variableConfig = entry.getValue();
                        collectVarRefs(variableConfig, references);
                        break;
                    case CONDITION_NODE:
                        String condition = entry.getValue().asText();
                        collectVarRefs(condition, references);
                        break;
                    default:
                        if (entry.getValue().asText().equals("INVOKE_API")) {
                            collectInvocationRefs(obj.path("properties"), references);
                        }
                        collectReferencesInAction(entry.getValue(), references);
                        break;
                }
            }
        }
        else if (node instanceof ArrayNode) {
            node.forEach(el -> collectReferencesInAction(el, references));
        }
        // Nothing to handle for the primitive type case
    }

    /**
     * Apis can bind request and response values from/to global/context values.
     * We need to parse the invocation object to find those references.
     */
    private void collectInvocationRefs(JsonNode node, References references) {
        if (!node.isObject()) {
            return;
        }

        ObjectNode invocationProps = (ObjectNode) node;
        references.apis.add(new ApiKey(invocationProps));
        collectRequestResponseRefs(invocationProps.path("requestParam"), references);
        collectRequestResponseRefs(invocationProps.path("responseSchema"), references);
    }

    private void collectRequestResponseRefs(JsonNode node, References references) {
        if (!node.isArray()) {
            return;
        }

        node.forEach(n -> collectVarRefs(n.path("value").asText(), references));
    }

    private void collectVarRefs(JsonNode node, References references) {
        String type = node.path("configType").asText();
        String name = node.path("configName").asText();
        if (type.equals("GLOBAL")) {
            safeAdd(references.globalVars, name);
        }
        else if (type.equals("CONTEXT")) {
            safeAdd(references.contextVals, name);
        }
    }

    private static <T> void safeAdd(Set<T> set, T value) {
        if (value != null) {
            set.add(value);
        }
    }

    private static final String CONDITION_NODE = "condition";
    private static final String RBKEY_NODE = "rbkey";
    private static final Pattern GLOBAL_VAR_RE = Pattern.compile("GLOBAL_VARIABLE\\.(\\w+)");
    private static final Pattern GLOBALVALUE_RE = Pattern.compile("GLOBALVALUE\\.(\\w+)");
    private static final Pattern CONTEXT_VAL_RE = Pattern.compile("CONTEXT_VALUE\\.(\\w+)");
    private static final Pattern CONTEXTVALUE_RE = Pattern.compile("CONTEXTVALUE\\.(\\w+)");

    private static final Logger LOGGER = LogManager.getLogger();

}
