package com.jda.mobility.framework.extensions.model;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jda.mobility.framework.extensions.dto.TranslationRequest;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FormData implements Serializable {

	private static final long serialVersionUID = -7999776757905607543L;

	private UUID formId;
	@NotNull
	private String name;
	private String description;
	private boolean published;
	@NotNull
	private UUID flowId;
	private String formTemplate;
	private String tag;
	@NotNull
	private boolean orphanForm;
	@NotNull
	private boolean clonableForm;
	@NotNull
	private boolean applyToAllClones;
	private UUID parentFormId;
	private boolean defaultForm;
	private boolean showonce;
	private String formType;
	@Valid
	private List<UUID> deleteFields;
	@Valid
	private List<UUID> deleteEvents;
	@Valid
	private List<UUID> deleteValues;
	@Valid
	private List<UUID> deleteDataValues;
	@Valid
	private List<FieldComponent> components;
	private String flowName;
	private List<String> flowPermissions;
	private List<FieldComponent> columns;
	@Valid
	private List<Tab> tabs;
	private boolean tabbedForm;
	private FormProperties formProperties;
	@Valid
	private List<UUID> deleteTabs;
	private TranslationRequest formTitle; 
	private Layer layer;
	private boolean inboundOrphan;
	private boolean outboundOrphan;
	private long flowVersion;


	public UUID getFormId() {
		return formId;
	}

	public void setFormId(UUID formId) {
		this.formId = formId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public UUID getFlowId() {
		return flowId;
	}

	public void setFlowId(UUID flowId) {
		this.flowId = flowId;
	}

	public String getFormTemplate() {
		return formTemplate;
	}

	public void setFormTemplate(String formTemplate) {
		this.formTemplate = formTemplate;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public boolean isOrphanForm() {
		return orphanForm;
	}

	public void setOrphanForm(boolean orphanForm) {
		this.orphanForm = orphanForm;
	}

	public boolean isClonableForm() {
		return clonableForm;
	}

	public void setClonableForm(boolean clonableForm) {
		this.clonableForm = clonableForm;
	}

	public boolean isApplyToAllClones() {
		return applyToAllClones;
	}

	public void setApplyToAllClones(boolean applyToAllClones) {
		this.applyToAllClones = applyToAllClones;
	}

	public UUID getParentFormId() {
		return parentFormId;
	}

	public void setParentFormId(UUID parentFormId) {
		this.parentFormId = parentFormId;
	}

	public boolean isDefaultForm() {
		return defaultForm;
	}

	public void setDefaultForm(boolean defaultForm) {
		this.defaultForm = defaultForm;
	}

	public boolean isShowonce() {
		return showonce;
	}

	public void setShowonce(boolean showonce) {
		this.showonce = showonce;
	}

	public String getFormType() {
		return formType;
	}

	public void setFormType(String formType) {
		this.formType = formType;
	}

	public List<UUID> getDeleteFields() {
		return deleteFields;
	}

	public void setDeleteFields(List<UUID> deleteFields) {
		this.deleteFields = deleteFields;
	}

	public List<UUID> getDeleteEvents() {
		return deleteEvents;
	}

	public void setDeleteEvents(List<UUID> deleteEvents) {
		this.deleteEvents = deleteEvents;
	}

	public List<UUID> getDeleteValues() {
		return deleteValues;
	}

	public void setDeleteValues(List<UUID> deleteValues) {
		this.deleteValues = deleteValues;
	}

	public List<UUID> getDeleteDataValues() {
		return deleteDataValues;
	}

	public void setDeleteDataValues(List<UUID> deleteDataValues) {
		this.deleteDataValues = deleteDataValues;
	}

	public List<FieldComponent> getComponents() {
		return components;
	}

	public void setComponents(List<FieldComponent> components) {
		this.components = components;
	}

	public String getFlowName() {
		return flowName;
	}

	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}

	public List<String> getFlowPermissions() {
		return flowPermissions;
	}

	public void setFlowPermissions(List<String> flowPermissions) {
		this.flowPermissions = flowPermissions;
	}

	public List<FieldComponent> getColumns() {
		return columns;
	}

	public void setColumns(List<FieldComponent> columns) {
		this.columns = columns;
	}

	public List<Tab> getTabs() {
		return tabs;
	}

	public void setTabs(List<Tab> tabs) {
		this.tabs = tabs;
	}

	public boolean isTabbedForm() {
		return tabbedForm;
	}

	public void setTabbedForm(boolean tabbedForm) {
		this.tabbedForm = tabbedForm;
	}

	public FormProperties getFormProperties() {
		return formProperties;
	}

	public void setFormProperties(FormProperties formProperties) {
		this.formProperties = formProperties;
	}

	public List<UUID> getDeleteTabs() {
		return deleteTabs;
	}

	public void setDeleteTabs(List<UUID> deleteTabs) {
		this.deleteTabs = deleteTabs;
	}

	public TranslationRequest getFormTitle() {
		return formTitle;
	}

	public void setFormTitle(TranslationRequest formTitle) {
		this.formTitle = formTitle;
	}

	public Layer getLayer() {
		return layer;
	}

	public void setLayer(Layer layer) {
		this.layer = layer;
	}

	public boolean isInboundOrphan() {
		return inboundOrphan;
	}

	public void setInboundOrphan(boolean inboundOrphan) {
		this.inboundOrphan = inboundOrphan;
	}

	public boolean isOutboundOrphan() {
		return outboundOrphan;
	}

	public void setOutboundOrphan(boolean outboundOrphan) {
		this.outboundOrphan = outboundOrphan;
	}

	public long getFlowVersion() {
		return flowVersion;
	}

	public void setFlowVersion(long flowVersion) {
		this.flowVersion = flowVersion;
	}

	@Override
	public int hashCode() {
		return Objects.hash(applyToAllClones, columns, components, deleteDataValues, deleteEvents, deleteFields,
				deleteValues, description, flowId, flowName, flowPermissions, formId, formProperties, formTemplate,
				formType, clonableForm, defaultForm, orphanForm, layer, name, parentFormId, published,
				showonce, tabbedForm, tabs, tag,deleteTabs,formTitle, inboundOrphan, outboundOrphan,flowVersion);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FormData other = (FormData) obj;
		return applyToAllClones == other.applyToAllClones && Objects.equals(columns, other.columns)
				&& Objects.equals(components, other.components)
				&& Objects.equals(deleteDataValues, other.deleteDataValues)
				&& Objects.equals(deleteEvents, other.deleteEvents) && Objects.equals(deleteFields, other.deleteFields)
				&& Objects.equals(deleteValues, other.deleteValues) && Objects.equals(description, other.description)
				&& Objects.equals(flowId, other.flowId) && Objects.equals(flowName, other.flowName)
				&& Objects.equals(flowPermissions, other.flowPermissions) && Objects.equals(formId, other.formId)
				&& Objects.equals(formProperties, other.formProperties)
				&& Objects.equals(formTemplate, other.formTemplate) && Objects.equals(formType, other.formType)
				&& clonableForm == other.clonableForm && defaultForm == other.defaultForm && orphanForm == other.orphanForm
				&& Objects.equals(layer, other.layer) && Objects.equals(name, other.name)
				&& Objects.equals(parentFormId, other.parentFormId)
				&& published == other.published
				&& showonce == other.showonce && tabbedForm == other.tabbedForm && Objects.equals(tabs, other.tabs)
				&& Objects.equals(tag, other.tag) 	
				&&  Objects.equals(deleteTabs, other.deleteTabs) &&  Objects.equals(formTitle, other.formTitle)
				&& Objects.equals(inboundOrphan, other.inboundOrphan) && Objects.equals(outboundOrphan, other.outboundOrphan)
				&& Objects.equals(flowVersion, other.flowVersion);
	}

	
}