# Exposed Environment Variables

* `DATASOURCE_URL` (*required*)

   Specifies the database to be used by the server. Do not include user/password information in the URL, those are specified as separate environment variables. Only SQL Server / Azure SQL databases are supported. Therefore the `jdbc:sqlserver://` portion of the URL should not be included.
   
   Example: `localhost:1433;databaseName=myDatabase`
   
* `DB_USER` (*required*)

   Specifies the database user that owns the database schema and data.

* `DB_PASSWORD` (*required*)

   Specifies the password for the database user.

* `AUTHENTICATION_STRATEGY` (*optional*)

   Specifies the authentication strategy the server should use. Supported values are `BASIC_AUTH` and `OPENID`. The default value is `BASIC_AUTH`.

* `INTERNAL_TOKEN_SECRET` (*required if `AUTHENTICATION_STRATEGY`=`BASIC_AUTH`*)

   Specifies a secret value used to sign JWTs if the `BASIC_AUTH` authentication strategy is in use. All api servers in a cluster must use the same key.
   
* `OIDC_PROVIDER_URL` (*required if `AUTHENTICATION_STRATEGY`=`OPENID`)

   The location of the OIDC provider.
   
* `OIDC_CLIENT_ID` (*required if `AUTHENTICATION_STRATEGY`=`OPENID`)

   The client id to use with OIDC
   
* `OIDC_AUDIENCE` (*required if `AUTHENTICATION_STRATEGY`=`OPENID`)

   The audience the OIDC JWTs are expected to be for

* `OIDC_SCOPE` (*optional and unused if `AUTHENTICATION_STRATEGY`='BASIC_AUTH`*)

   The scopes to expect the OIDC token to support. Each scope should be separated by a space. The default value is `openid`.
   
   Example: `openid user_impersonation`

* `PRODUCT_BASE_URI` (*required*)

   Specifies the base URI of the product server with which the mobile server will communicate. The base URI *must* include the scheme (`http` or `https`). 
   
   Example: `https://example.com`

* `ACTUATOR_API_KEY` (*optional*)

   This value secures `/actuator` endpoints other than `/actuator/health`. Requests to actuator endpoints must include this value in an `X-Actuator-Api-Key` header to avoid rejection. The default value for this property is a randomly generated UUID. 

# Suggested Volume Mounts

* `/app/config`

  Spring looks in this directory on startup to find app settings. You may place your own custom `application.(yml|properties)` and its properties will override the configurations provided in the image. See also the `SPRING_ACTIVE_PROFILE` environment variable.
  
* `/app/logs`

  Logs are written to this directory by default.
  
# Building and Running an Image

```
$ docker build -t api .
$ docker run -p 8080:8080 -p 5076:5076 <documented env props> -d -t api:latest
```

Alternatively, a [docker-compose](./docker-compose.yml) file is available for use with sane defaults configured for running the image in a local container. The docker-compose file pulls values from the environment if they are present.

```bash
$ docker build -t mobile-app-builder-api .

$ export DATASOURCE_URL="host.docker.internal:1433;databaseName=yourDatabase"
$ export DB_USER=yourUser
$ export DB_PASSWORD=yourPassword

$ docker-compose up -d
```