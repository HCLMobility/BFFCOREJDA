/**
 * 
 */
package com.jda.mobility.framework.extensions.utils;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jda.mobility.framework.extensions.entity.Data;
import com.jda.mobility.framework.extensions.entity.Events;
import com.jda.mobility.framework.extensions.entity.ExtendedDataBase;
import com.jda.mobility.framework.extensions.entity.ExtendedEventsBase;
import com.jda.mobility.framework.extensions.entity.ExtendedFieldBase;
import com.jda.mobility.framework.extensions.entity.ExtendedFieldValuesBase;
import com.jda.mobility.framework.extensions.entity.Field;
import com.jda.mobility.framework.extensions.entity.FieldValues;

/**
 * The class FieldComparatorTest.java
 * @author HCL Technologies Ltd.
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class FieldComparatorTest {
	@Test
	public void testCompare() {		
		int response = FieldComparator.compare(getField(), getExtendedFieldBase());
		assertEquals(0, response);
	}
	@Test
	public void testCompareFieldAttributeOfTypeList() {
		boolean response = FieldComparator.compareFieldAttributeOfTypeList(new ArrayList<>(), new ArrayList<>());
		assertEquals(true, response);
	}
	
	@Test
	public void testCompareFieldAttributeOfTypeList1() {
		boolean response = FieldComparator.compareFieldAttributeOfTypeList(getFieldAttributes(), getExtFieldAttributes());
		assertEquals(false, response);
	}
	
	@Test
	public void testCompareFieldAttributeOfTypeListWithEvents() {
		
		boolean response = FieldComparator.compareFieldAttributeOfTypeList(getEvents(), getExtEvents());
		assertEquals(true, response);
	}
	
	@Test
	public void testCompareFieldAttributeOfTypeListWithData() {
		boolean response = FieldComparator.compareFieldAttributeOfTypeList(getData(), getExtData());
		assertEquals(true, response);
	}
	
	@Test
	public void testCompareFieldAttributeOfTypeListWithFieldValues() {
		boolean response = FieldComparator.compareFieldAttributeOfTypeList(getFieldValues(), getExtFieldValues());
		assertEquals(true, response);
	}
	
	@Test
	public void testTraverse() throws JsonMappingException, JsonProcessingException {
		JsonNode root = new ObjectMapper().readTree("{\"fieldNames\":\"WMD1\"}");
		
		List<String> fieldToIgnoreList = new ArrayList<>();
		String field="test";
		fieldToIgnoreList.add(field);
		 FieldComparator.traverse(root, fieldToIgnoreList);
		 assertTrue(true);
	}
	
	private Field getField() {
		Field field = new Field();
		field.setKeys("");
		return field;
	}
	 
	private ExtendedFieldBase getExtendedFieldBase() {
		ExtendedFieldBase extendedFieldBase = new ExtendedFieldBase();
		extendedFieldBase.setKeys("");
		return extendedFieldBase;
	}
	
	private List<Field> getFieldAttributes(){
		List<Field> fieldAttributes = new ArrayList<>();
		Field field = new Field();
		field.setKeys("");
		fieldAttributes.add(field);
		return fieldAttributes;
	}
	
	private List<ExtendedFieldBase> getExtFieldAttributes(){
		List<ExtendedFieldBase> extFieldAttributes = new ArrayList<>();
		ExtendedFieldBase extendedFieldBase = new ExtendedFieldBase();
		extendedFieldBase.setKeys("");
		extFieldAttributes.add(extendedFieldBase);
		return extFieldAttributes;
	}
	
	private List<Events> getEvents(){
		List<Events> events = new ArrayList<>();
		Events eventAttr = new Events();
		eventAttr.setUid(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209"));
		eventAttr.setExtendedFromEventId(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0"));
		eventAttr.setEvent("event");
		eventAttr.setAction("Test");
		events.add(eventAttr);
		return events;
	}
	
	private List<ExtendedEventsBase> getExtEvents(){
		List<ExtendedEventsBase> extEvents = new ArrayList<>();
		ExtendedEventsBase extEventAttr = new ExtendedEventsBase();
		extEventAttr.setUid(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0"));
		extEventAttr.setEvent("event");
		extEventAttr.setAction("Test");
		extEvents.add(extEventAttr);
		return extEvents;
	}
	
	private List<Data> getData(){
		List<Data> data = new ArrayList<>();
		Data dataAttr = new Data();
		dataAttr.setUid(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209"));
		dataAttr.setExtendedFromDataId(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0"));
		dataAttr.setDatalabel("datalabel");
		data.add(dataAttr);
		return data;
	}
	
	private List<ExtendedDataBase> getExtData(){
		List<ExtendedDataBase> extData = new ArrayList<>();
		ExtendedDataBase extDataAttr = new ExtendedDataBase();
		extDataAttr.setUid(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0"));
		extDataAttr.setDatalabel("datalabel");
		extData.add(extDataAttr);
		return extData;
	}
	
	private List<FieldValues> getFieldValues(){
		List<FieldValues> fieldValues = new ArrayList<>();
		FieldValues fieldValuesAttr = new FieldValues();
		fieldValuesAttr.setUid(UUID.fromString("37c05061-2620-433f-9c8f-54bb990c8209"));
		fieldValuesAttr.setExtendedFromFieldValuesId(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0"));
		fieldValuesAttr.setLabel("label");
		fieldValues.add(fieldValuesAttr);
		return fieldValues;
	}
	
	private List<ExtendedFieldValuesBase> getExtFieldValues(){
		List<ExtendedFieldValuesBase> extFieldValues = new ArrayList<>();
		ExtendedFieldValuesBase extFieldValueAttr = new ExtendedFieldValuesBase();
		extFieldValueAttr.setUid(UUID.fromString("cb527cc6-0c75-4dc2-b789-39a9cdc167a0"));
		extFieldValueAttr.setLabel("label");
		extFieldValues.add(extFieldValueAttr);
		return extFieldValues;
	}
	
}
