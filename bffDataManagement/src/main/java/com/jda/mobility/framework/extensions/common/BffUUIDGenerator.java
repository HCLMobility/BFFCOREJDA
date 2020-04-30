package com.jda.mobility.framework.extensions.common;

import java.io.Serializable;
import java.util.Properties;
import java.util.UUID;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.UUIDGenerator;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
/**
 * Generates UUID values as a pluggable UUIDGenerationStrategy.  The values this generator can return
 * include {@link UUID}, {@link String} and byte[16]
 *
 */
public class BffUUIDGenerator extends UUIDGenerator {
	private String entityName;

    @Override
    public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) {
        entityName = params.getProperty(ENTITY_NAME);
        super.configure(type, params, serviceRegistry);
    }

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
    	
        Serializable id = session
                .getEntityPersister(entityName, object)
                .getIdentifier(object, session);

        if (id == null) {
            return super.generate(session, object);
        } else {
            return id;
        }
    }
}
