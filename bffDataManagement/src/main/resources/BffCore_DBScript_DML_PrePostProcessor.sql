UPDATE API_MASTER SET post_processor = CONVERT(VARBINARY(MAX), 'Include Libraries

Step "getDepositRequirementsUsingGET"
	Orchestration "POSTMODIFICATION"
	when 
		Get Inputs
	then
		Say "***getDepositRequirementsUsingGET post-processor execution starts****"
		Copy "/depositInformation/0/destinationLocationNumber" as "destinationLocationNumber" to "/"
	    Say "***getDepositRequirementsUsingGET post-processor execution ends****"
end')
WHERE request_endpoint = '/depositRequirements' and NAME = 'getDepositRequirementsUsingGET';

UPDATE API_MASTER SET post_processor = CONVERT(VARBINARY(MAX), 'Include Libraries

Step "OrchExecution"
	Orchestration "POSTMODIFICATION"
	when 
		Get Inputs
	then
		Say "***listDevicesUsingGET post-processor execution starts****" 	
		Copy "/deviceInformation/0" as "firstDeviceInformation" to "/"
	    Say "***listDevicesUsingGET post-processor execution starts****" 	
end')
WHERE request_endpoint = '/devices' AND name = 'listDevicesUsingGET';

UPDATE API_MASTER SET post_processor = CONVERT(VARBINARY(MAX), 'Include Libraries

Step "getPickTasksUsingPOST"
	Orchestration "POSTMODIFICATION"
	when 
		Get Inputs
	then
		Say "***getPickTasksUsingPOST post-processor execution starts****"
		Copy "/tasks/0/captureFields" as "captureFields" to "/"
	    Say "***getPickTasksUsingPOST post-processor execution ends****"
end')
WHERE request_endpoint = '/pickTasks' AND name = 'getPickTasksUsingPOST';

UPDATE API_MASTER SET post_processor = CONVERT(VARBINARY(MAX), 'Include Libraries

Step "listItemsUsingGET"
	Priority 3
	Orchestration "POSTMODIFICATION"
	when 
		Get Inputs
	then
		Say "***listItemsUsingGET post-processor execution starts****"
		Copy "/items/0/footprints/0/footprintCode" as "footprintCode" to "/"
		Copy "/items/0/footprints/0/footprintDetails/0/uomCode" as "uomCode" to "/"
	    Say "***listItemsUsingGET post-processor execution ends****"
end')
WHERE request_endpoint = '/items' AND name = 'listItemsUsingGET';