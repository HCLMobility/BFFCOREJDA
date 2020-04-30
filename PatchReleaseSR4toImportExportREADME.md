# Overview

Export and Import logic has been refactored to support 'no touch' data migration from one system to another.

If you wish to use this refactored functionality you must use a database that has been bootstrapped by flyway. To facilitate data migration many of the unchanging database records have been given hardcoded uids.

Note: You can export data from a system not running on a flyway bootstrapped database, but the import part of the process is unlikely to work.

# Steps to deploy

1. Rebuild the root project.
   ```
   $ mvn -f pom.xml clean install
   ```
1. Create a new empty database.
1. Copy the [application-deploy.properties](./bffWebManagement/src/main/resources/application-deploy.properties) and place it in the `./config` directory.
1. Update the various property values to reflect your database and product backend (If you leave the `product-api.base-url` property blank, the server will use the connection information present in the `product_master` database table).
1. Start your server using the appropriate spring profile. Here we use `deploy` because if you copied `application-deploy.properties` into `./config` that's the profile it belongs to. 
   ```bash
   $ java -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5076 \
          -Dspring.profiles.active=deploy \
          -jar bffWebManagement/target/bffWebManagement-0.0.1-SNAPSHOT.jar
   ```
   
When the server starts up it will run the [db migration scripts](./bffWebManagement/src/main/resources/db/migration) automatically.

You're now ready to import your data from an older environment.

# Migrating data from older versions

A [migration script](https://stash.jda.com/users/j1014702/repos/mab-migrator/browse) exists to transform JSON exported from older servers into the structure understood by the current server. It can export from one system, transform the JSON, and then import the JSON into a different system.
```bash
$ migrate ~/output \
    --source-url https://jdawamobile.jdadelivers.com \
    --dest-url http://localhost:8080 \
    --export-type RESOURCE_BUNDLE \
    --export-type SPECIFIC_FLOWS

✔ Select flows to export: › Baljinder Test, JohnFormFlow, Orchestration Test

Exporting from https://jdawamobile.jdadelivers.com ...
✓       Exported:  ~/output/exported/system.zip (448 ms)
✓       Exported:  ~/output/exported/ee3a32c7-9f9e-4878-b88a-8f2f60d890dc.zip (356 ms)
✓       Exported:  ~/output/exported/8a510e4e-37de-4057-be94-c0205b955700.zip (768 ms)
✓       Exported:  ~/output/exported/b43563af-3866-46a7-b5fc-7fa4cbb475fe.zip (221 ms)

⌛  Export Time: 1795.582ms

Migrating JSON ...
✓       Migrated JSON: ~/output/exported/system.zip => ~/output/migrated/system.zip (65 ms)
✓       Migrated JSON: ~/output/exported/ee3a32c7-9f9e-4878-b88a-8f2f60d890dc.zip => ~/output/migrated/ee3a32c7-9f9e-4878-b88a-8f2f60d890dc.zip (32 ms)
✓       Migrated JSON: ~/output/exported/8a510e4e-37de-4057-be94-c0205b955700.zip => ~/output/migrated/8a510e4e-37de-4057-be94-c0205b955700.zip (40 ms)
✓       Migrated JSON: ~/output/exported/b43563af-3866-46a7-b5fc-7fa4cbb475fe.zip => ~/output/migrated/b43563af-3866-46a7-b5fc-7fa4cbb475fe.zip (14 ms)

⌛  Migrate Time: 153.383ms

Importing into http://localhost:8080 ...
✓       Imported: ~/output/migrated/system.zip => ~/output/imported/system.zip (1067 ms)
✓       Imported: ~/output/migrated/ee3a32c7-9f9e-4878-b88a-8f2f60d890dc.zip => ~/output/imported/ee3a32c7-9f9e-4878-b88a-8f2f60d890dc.zip (789 ms)
✓       Imported: ~/output/migrated/8a510e4e-37de-4057-be94-c0205b955700.zip => ~/output/imported/8a510e4e-37de-4057-be94-c0205b955700.zip (1587 ms)
✓       Imported: ~/output/migrated/b43563af-3866-46a7-b5fc-7fa4cbb475fe.zip => ~/output/imported/b43563af-3866-46a7-b5fc-7fa4cbb475fe.zip (393 ms)

⌛  Import Time: 3837.142ms

Total Script Runtime: 32013.319ms
``` 