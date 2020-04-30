Pre-requisite checks/notes: 
1. If SRPINT 10 DB has not been upgraded to SPRINT 10 SR1 DB, Please follow instructions in "PatchReleaseSR1README.md" before following the steps below
2. Once SPRINT 10 DB is upgraded to SPRINT 10 SR1 DB as per Step 1, to upgrade SRPINT 10 SR1 DB to SPRINT 10 SR2 DB, please follow instructions in "PatchReleaseSR1toSR2README.md" before following the steps below to upgrade to SPRINT 10 SR3 DB

Steps to upgrade BFFCORE SPRINT 10 SR2 to SPRINT 10 SR3 (DB and APP)
====================================================================
1. Stop the BFFCORE application - Kill relevant process (if Ubuntu / Linux) OR stop service / CTRL + C if windows OS
2. Take a back up of the existing "SPRINT 10 SR2 SQL SERVER DB" using SQL server management studio
3. Execute the following scripts on the existing "SPRINT 10 SR2 SQL SERVER DB" DB -
	a. BffCore_DBScript_DDLDML_UpgradeSR2toSR3.sql - The differential/upgrade script to be executed to upgrade the BFFCORE SPRINT 10 SR2 DB to SR3 release DB
	Path: <PROJECT ROOT>/bffDataManagement/src/main/resources/BffCore_DBScript_DDLDML_UpgradeSR2toSR3.sql
	b.	BffCore_DBScript_DML_ResourceBundle.sql - The master data script to populate the resource bundle keys for BFFCORE API success/error messages in English language
	Path: <PROJECT ROOT>/bffDataManagement/src/main/resources/ BffCore_DBScript_DML_ResourceBundle.sql
	c.	BffCore_DBScript_DML_ResourceBundle_fr.sql - The master data script to populate the resource bundle keys for BFFCORE API success/error messages in French language.
	Path: <PROJECT ROOT>/bffDataManagement/src/main/resources/ BffCore_DBScript_DML_ResourceBundle_fr.sql
	Note: To add support for more languages, please create a separate copy of this DML script and insert into the resource_bundle table.
	d. BffCore_DBScript_DML_LocalizedMobileVariables.sql - The master data script to populate the resource bundle keys for ‘MobileRenderer’ app internal labels/texts in English language.
	Path: <PROJECT ROOT>/bffDataManagement/src/main/resources/BffCore_DBScript_DML_LocalizedMobileVariables.sql
	e. BffCore_DBScript_DML_LocalizedMobileVariables_fr.sql - The master data script to populate the resource bundle keys for ‘MobileRenderer’ app internal labels/texts in French language.
	Path: <PROJECT ROOT>/bffDataManagement/src/main/resources/BffCore_DBScript_DML_LocalizedMobileVariables_fr.sql
	Note: To add support for more languages, please create a separate copy of this DML script and insert into the resource_bundle table.

4. Re-build and re-deploy the application - Follow steps in Section 2.4 (INSTALL LIBRARIES & GENERATE BFFCORE APPLICATION BOOTABLE JAR) of the master README.docx file to re-build and re-deploy the latest BFFCORE SPRINT 10 SR1 application code from the JDA stash 'develop' branch after merging with the latest code from 'release/SPRINT-10' branch.