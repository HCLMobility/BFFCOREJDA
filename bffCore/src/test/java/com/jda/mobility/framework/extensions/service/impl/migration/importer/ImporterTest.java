package com.jda.mobility.framework.extensions.service.impl.migration.importer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

@DataJpaTest(showSql = false)
@TestPropertySource(properties = {
        "spring.flyway.enable=false",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Sql(scripts = "/imports-baseline.sql")
@Target({ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ImporterTest {}
