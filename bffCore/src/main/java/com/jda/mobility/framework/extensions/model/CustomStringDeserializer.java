package com.jda.mobility.framework.extensions.model;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.deser.std.StringDeserializer;

public class CustomStringDeserializer extends StdDeserializer<String>{

	private static final long serialVersionUID = 1L;

	public CustomStringDeserializer() { 
        this(null); 
    } 
 
    public CustomStringDeserializer(Class<?> vc) { 
        super(vc); 
    }
    
    @Override
    public String deserialize(JsonParser parser, DeserializationContext context)
        throws IOException {
    	return  StringDeserializer.instance.deserialize(parser, context);
    }
}
