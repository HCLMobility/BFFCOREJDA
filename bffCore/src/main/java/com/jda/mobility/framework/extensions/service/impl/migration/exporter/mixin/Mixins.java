package com.jda.mobility.framework.extensions.service.impl.migration.exporter.mixin;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Contains Jackson ObjectMapper mixins that are used to hide unwanted properties
 * during entity serialization for export.
 */
public final class Mixins {

    @JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
    @JsonIgnoreProperties({"uid", "createdBy", "creationDate", "lastModifiedBy", "lastModifiedDate"})
    public interface ResourceBundle {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
    @JsonIgnoreProperties({"uid", "apiMasters", "createdBy", "creationDate", "lastModifiedBy", "lastModifiedDate"})
    public interface ApiRegistry {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
    @JsonIgnoreProperties({"uid", "createdBy", "creationDate", "lastModifiedBy", "lastModifiedDate"})
    public interface Api {}

    @JsonIdentityInfo(generator = ObjectIdGenerators.None.class)
    @JsonIgnoreProperties({"uid", "apiVersion", "basePath", "contextPath", "port", "versionId", "apiMasters",
                           "helperClass", "createdBy", "creationDate", "lastModifiedBy", "lastModifiedDate"})
    public interface OwnerRegistry {}
}
