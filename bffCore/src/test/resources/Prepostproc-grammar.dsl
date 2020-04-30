[keyword][]Include Libraries=import java.util.*;import org.springframework.http.ResponseEntity;import com.jda.mobility.framework.extensions.utils.*;import com.jda.mobility.framework.extensions.model.*;import org.springframework.http.HttpEntity;global List<String> apiList;dialect "java";expander Pre-processor.dsl;
[keyword][]Step "{stepName}"=rule "{stepName}"
[keyword][]Priority {num}=salience {num}
[keyword][]Orchestration "{orchName}"=agenda-group "{orchName}"
[condition][]Get Inputs=prodApiWrkMemRequest: ProdApiWrkMemRequest()
[consequence][]Insert "{fieldNameToBeInserted}" with "{fieldValue}" of type "{fieldType}"  at "{nodePath}"=prodApiWrkMemRequest.setApiResponse(prodApiWrkMemRequest.getJsonNodeModifier().insertField( prodApiWrkMemRequest.getApiResponse(), "{nodePath}", prodApiWrkMemRequest.getJsonNodeModifier().createJsonNode("{fieldType}","{fieldValue}"), "{fieldNameToBeInserted}"));update(prodApiWrkMemRequest);
[consequence][]Copy "{nodePath}" as "{fieldName}" to "{newnodePath}"=prodApiWrkMemRequest.setApiResponse(prodApiWrkMemRequest.getJsonNodeModifier().copyField(prodApiWrkMemRequest.getApiResponse(), "{fieldName}", "{nodePath}", "{newnodePath}"));update(prodApiWrkMemRequest);
[consequence][]Remove "{fieldNameToBedeleted}" at "{nodePath}"=prodApiWrkMemRequest.setApiResponse(prodApiWrkMemRequest.getJsonNodeModifier().removeField("{fieldNameToBedeleted}",prodApiWrkMemRequest.getApiResponse(),"{nodePath}"));update(prodApiWrkMemRequest);
[consequence][]Say "{message}"=System.out.println("{message}");
[consequence][]Replace "{fieldNameToBeReplaced}" with "{fieldValue}" of type "{fieldType}"  at "{nodePath}"=prodApiWrkMemRequest.setApiResponse(prodApiWrkMemRequest.getJsonNodeModifier().insertField( prodApiWrkMemRequest.getApiResponse(), "{nodePath}", prodApiWrkMemRequest.getJsonNodeModifier().createJsonNode("{fieldType}","{fieldValue}"), "{fieldNameToBeReplaced}"));update(prodApiWrkMemRequest);
[consequence][]Change date format at "{nodePath}" from "{inputFormat}" to "{outputFormat}"=prodApiWrkMemRequest.setApiResponse(prodApiWrkMemRequest.getJsonNodeModifier().dateModifier("{inputFormat}","{outputFormat}",prodApiWrkMemRequest.getApiResponse(),"{nodePath}"));update(prodApiWrkMemRequest);
[consequence][]Create a new request object from fields "{fieldsSeparatedWithComma}" with values "{valuesSeparatedWithComma}" of types "{typesSeparatedWithComma}"=prodApiWrkMemRequest.setApiRequest(prodApiWrkMemRequest.getJsonNodeModifier().createObject( "{fieldsSeparatedWithComma}","{valuesSeparatedWithComma}","{typesSeparatedWithComma}"));update(prodApiWrkMemRequest);
[consequence][]"{PREorPOST}" APPEND  "{valueToBeAppended}" to the value at "{nodePath}"=prodApiWrkMemRequest.setApiResponse(prodApiWrkMemRequest.getJsonNodeModifier().prePostAppend(prodApiWrkMemRequest.getApiResponse(),"{valueToBeAppended}","{PREorPOST}","{nodePath}"));update(prodApiWrkMemRequest);
[consequence][]add "{valueInteger}" to the value at  "{nodePath}"=prodApiWrkMemRequest.setApiResponse(prodApiWrkMemRequest.getJsonNodeModifier();
[consequence][]pick element "{position}" from the response at "{nodePath}"=prodApiWrkMemRequest.setApiResponse(prodApiWrkMemRequest.getJsonNodeModifier().getJSONNode(prodApiWrkMemRequest.getApiResponse(),"{nodePath}","{position}"));update(prodApiWrkMemRequest);
[consequence][]modify the request=prodApiWrkMemRequest.setApiRequest(prodApiWrkMemRequest.getJsonNodeModifier().requestModification(prodApiWrkMemRequest.getApiRequest()));update(prodApiWrkMemRequest);
[consequence][]Modify "{attribute}" of request to array=prodApiWrkMemRequest.setApiRequest(prodApiWrkMemRequest.getJsonNodeModifier().requestModification (prodApiWrkMemRequest.getApiRequest() ,"{attribute}"));update(prodApiWrkMemRequest);