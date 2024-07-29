# Supported operations

Below is the list of supported operations from the fhir base module. Click on each operation name to see its details.

- [addBundleEntry](#addbundleentry-operation)
- [addBundleLink](#addbundlelink-operation)
- [addElement](#addelement-operation)
- [createAddress](#createaddress-operation)
- [createAge](#createage-operation)
- [createAnnotation](#createannotation-operation)
- [createAttachment](#createattachment-operation)
- [createBundle](#createbundle-operation)
- [createCodeableConcept](#createcodeableconcept-operation)
- [createCoding](#createcoding-operation)
- [createContactDetail](#createcontactdetail-operation)
- [createContactPoint](#createcontactpoint-operation)
- [createCount](#createcount-operation)
- [createContributor](#createcontributor-operation)
- [createDataRequirement](#createdatarequirement-operation)
- [createDosage](#createdosage-operation)
- [createDuration](#createduration-operation)
- [createExpression](#createexpression-operation)
- [createHumanName](#createhumanname-operation)
- [createIdentifier](#createidentifier-operation)
- [createMeta](#createmeta-operation)
- [createNarrative](#createnarrative-operation)
- [createParameterDefinition](#createparameterdefinition-operation)
- [createPeriod](#createperiod-operation)
- [createQuantity](#createquantity-operation)
- [createRange](#createrange-operation)
- [createRatio](#createratio-operation)
- [createReference](#createreference-operation)
- [createRelatedArtifact](#createrelatedartifact-operation)
- [createSampledData](#createsampleddata-operation)
- [createSignature](#createsignature-operation)
- [createSimpleQuantity](#createsimplequantity-operation)
- [createTiming](#createtiming-operation)
- [createTriggerDefinition](#createtriggerdefinition-operation)
- [createUsageContext](#createusagecontext-operation)
- [evaluateFHIRPath](#evaluatefhirpath-operation)
- [serialize](#serialize-operation)
- [validate](#validate-operation)
- [setBundleIdentifier](#setbundleidentifier-operation)
- [setBundleType](#setbundletype-operation)
- [setBundleTimestamp](#setbundletimestamp-operation)
- [setBundleTotal](#setbundletotal-operation)
- [setBundleSignature](#setbundlesignature-operation)


# addBundleEntry Operation

The `addBundleEntry` operation allows you to add an entry to a FHIR bundle. This operation is part of the FHIR integration connector and facilitates the inclusion of various entry-related parameters such as links, full URLs, resources, search details, requests, and responses.

## Properties

| Property Name                      | Property Description                                                    |
|------------------------------------|--------------------------------------------------------------------------|
| `objectId`                         | The unique identifier for the object to which the entry is being added. |
| `entry`                            | The main entry parameter containing various sub-properties.             |
| `entry.link`                       | Links related to the entry.                                              |
| `entry.fullUrl`                    | The full URL for the entry resource.                                     |
| `entry.resource`                   | The resource contained within the entry.                                 |
| `entry.search`                     | Search-related information for the entry.                                |
| `entry.search.mode`                | The mode of the search operation.                                        |
| `entry.search.score`               | The score of the search result.                                          |
| `entry.request`                    | Request-related details for the entry.                                   |
| `entry.request.method`             | The HTTP method used for the request (e.g., GET, POST, PUT, DELETE).     |
| `entry.request.url`                | The URL for the request.                                                 |
| `entry.request.ifNoneMatch`        | The If-None-Match header value for the request.                          |
| `entry.request.ifModifiedSince`    | The If-Modified-Since header value for the request.                      |
| `entry.request.ifMatch`            | The If-Match header value for the request.                               |
| `entry.request.ifNoneExist`        | The If-None-Exist header value for the request.                          |
| `entry.response`                   | Response-related details for the entry.                                  |
| `entry.response.status`            | The HTTP status code of the response.                                    |
| `entry.response.location`          | The location header value of the response.                               |
| `entry.response.etag`              | The ETag header value of the response.                                   |
| `entry.response.lastModified`      | The Last-Modified header value of the response.                          |
| `entry.response.outcome`           | The outcome of the response operation.                                   |


# addBundleLink Operation

The `addBundleLink` operation allows you to construct link for a FHIR bundle. This operation is part of the FHIR integration connector and facilitates the inclusion of link-related parameters such as relation and URL.

## Properties

| Property Name       | Property Description                                            |
|---------------------|------------------------------------------------------------------|
| `objectId`          | The unique identifier for the object to which the link is being added. |
| `link`              | The main link parameter containing various sub-properties.      |
| `link.relation`     | The relation of the link.                                        |
| `link.url`          | The URL for the link.                                            |

# addElement Operation

The `addElement` operation allows you to add an element to a FHIR resource. This operation is part of the FHIR integration connector and facilitates the inclusion of various parameters such as the object ID, FHIRPath, and source object ID.

## Properties

| Property Name     | Property Description                                                                           |
|-------------------|------------------------------------------------------------------------------------------------|
| `objectId`        | The unique identifier for the target object to which the element is being added.               |
| `FHIRPath`        | The FHIRPath expression specifying where the element should be added within the target object. |
| `sourceObjectId`  | The unique identifier for the source object which is used as the source Element.               |

# createAddress Operation

The `createAddress` operation allows you to create an FHIR Address datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name        | Property Description                                            |
|----------------------|------------------------------------------------------------------|
| `objectId`           | The unique identifier for the target object to which the address is being added. |
| `id`                 | The unique identifier for the address resource.                 |
| `extension`          | Extensions for additional address attributes.                   |
| `use`                | The purpose of this address (e.g., home, work).                 |
| `type`               | The type of address (e.g., postal, physical).                   |
| `text`               | The full text representation of the address.                    |
| `line`               | The lines of the address (e.g., street address).                |
| `city`               | The city of the address.                                        |
| `district`           | The district or county of the address.                          |
| `state`              | The state or province of the address.                           |
| `postalCode`         | The postal code of the address.                                 |
| `country`            | The country of the address.                                     |
| `period`             | The period during which the address is valid.                   |
| `period.start`       | The start date of the period during which the address is valid. |
| `period.end`         | The end date of the period during which the address is valid.   |
| `targetObjectId`     | The unique identifier for the target object where the address is created. |
| `FHIRPath`           | The FHIRPath expression specifying where the address should be added within the target object. |

# createAge Operation

The `createAge` operation allows you to create an FHIR Age datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation is part of the FHIR integration connector and facilitates the inclusion of various age-related parameters such as value, comparator, unit, system, and code.

## Properties

| Property Name        | Property Description                                                                       |
|----------------------|--------------------------------------------------------------------------------------------|
| `objectId`           | The unique identifier for the target object to which the age is being added.               |
| `id`                 | The unique identifier for the age datatype.                                                |
| `extension`          | Extensions for additional age attributes.                                                  |
| `value`              | The numerical value of the age.                                                            |
| `comparator`         | The comparator for the age value (e.g., <, <=, >, >=).                                     |
| `unit`               | The unit of the age value (e.g., years, months).                                           |
| `system`             | The system that defines the unit (e.g., UCUM).                                             |
| `code`               | The code for the age unit (e.g., a for years, mo for months).                              |
| `targetObjectId`     | The unique identifier for the target object where the age is created.                      |
| `FHIRPath`           | The FHIRPath expression specifying where the age should be added within the target object. |


# createAnnotation Operation

The `createAnnotation` operation allows you to create an FHIR Annotation datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation is part of the FHIR integration connector and facilitates the inclusion of various annotation-related parameters such as authorReference, authorString, time, and text.

## Properties

| Property Name                                           | Property Description                                                                              |
|---------------------------------------------------------|---------------------------------------------------------------------------------------------------|
| `objectId`                                              | The unique identifier for the target object to which the annotation is being added.               |
| `id`                                                    | The unique identifier for the annotation datatype.                                                |
| `extension`                                             | Extensions for additional annotation attributes.                                                  |
| `authorReference`                                       | Reference to the author of the annotation.                                                        |
| `authorReference.reference`                             | The reference to the author entity.                                                               |
| `authorReference.type`                                  | The type of the author entity.                                                                    |
| `authorReference.identifier`                            | The identifier of the author entity.                                                              |
| `authorReference.identifier.use`                        | The use of the identifier.                                                                        |
| `authorReference.identifier.type`                       | The type of the identifier.                                                                       |
| `authorReference.identifier.type.coding.system`         | The system that defines the coding of the identifier type.                                        |
| `authorReference.identifier.type.coding.version`        | The version of the coding system.                                                                 |
| `authorReference.identifier.type.coding.code`           | The code for the identifier type.                                                                 |
| `authorReference.identifier.type.coding.display`        | The display value for the identifier type code.                                                   |
| `authorReference.identifier.type.coding.userSelected`   | Indicates if the coding was user selected.                                                        |
| `authorReference.identifier.type.text`                  | The text description of the identifier type.                                                      |
| `authorReference.identifier.system`                     | The system that assigns the identifier.                                                           |
| `authorReference.identifier.value`                      | The value of the identifier.                                                                      |
| `authorReference.identifier.period`                     | The period during which the identifier is valid.                                                  |
| `authorReference.identifier.period.start`               | The start date of the period during which the identifier is valid.                                |
| `authorReference.identifier.period.end`                 | The end date of the period during which the identifier is valid.                                  |
| `authorReference.identifier.assigner`                   | The entity that assigns the identifier.                                                           |
| `authorReference.identifier.assigner.reference`         | The reference to the assigner entity.                                                             |
| `authorReference.identifier.assigner.type`              | The type of the assigner entity.                                                                  |
| `authorReference.identifier.assigner.identifier`        | The identifier of the assigner entity.                                                            |
| `authorReference.identifier.assigner.display`           | The display value for the assigner entity.                                                        |
| `authorReference.display`                               | The display value for the author reference.                                                       |
| `authorString`                                          | The string representation of the author.                                                          |
| `time`                                                  | The time when the annotation was made.                                                            |
| `text`                                                  | The text content of the annotation.                                                               |
| `targetObjectId`                                        | The unique identifier for the target object where the annotation is created.                      |
| `FHIRPath`                                              | The FHIRPath expression specifying where the annotation should be added within the target object. |


# createAttachment Operation

The `createAttachment` operation allows you to create an Attachment datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation is part of the FHIR integration connector and facilitates the inclusion of various attachment-related parameters such as contentType, language, data, and more.

## Properties

| Property Name       | Property Description                                                                              |
|---------------------|---------------------------------------------------------------------------------------------------|
| `objectId`          | The unique identifier for the target object to which the attachment is being added.               |
| `id`                | The unique identifier for the attachment datatype.                                                |
| `extension`         | Extensions for additional attachment attributes.                                                  |
| `contentType`       | The MIME type of the attachment content.                                                          |
| `language`          | The language of the attachment content.                                                           |
| `data`              | The base64 encoded data of the attachment.                                                        |
| `url`               | The URL where the attachment can be accessed.                                                     |
| `size`              | The size of the attachment in bytes.                                                              |
| `hash`              | The hash of the attachment content.                                                               |
| `title`             | The title of the attachment.                                                                      |
| `creation`          | The creation date of the attachment.                                                              |
| `targetObjectId`    | The unique identifier for the target object where the attachment is created.                      |
| `FHIRPath`          | The FHIRPath expression specifying where the attachment should be added within the target object. |


# createBundle Operation

The `createBundle` operation allows you to create a Bundle resource in your FHIR integration flow. This operation is part of the FHIR integration connector and facilitates the creation of various bundle-related parameters such as identifier, type, timestamp, and more.

## Properties

| Property Name                               | Property Description                                                                 |
|---------------------------------------------|---------------------------------------------------------------------------------------|
| `objectId`                                  | The unique identifier for the target object to which the bundle is being added.       |
| `id`                                        | The unique identifier for the bundle resource.                                        |
| `meta.versionId`                            | The version ID of the bundle.                                                         |
| `meta.lastUpdated`                          | The timestamp when the bundle was last updated.                                       |
| `meta.source`                               | The source system of the bundle.                                                      |
| `meta.profile`                              | Profiles the bundle conforms to.                                                      |
| `meta.security`                             | Security labels for the bundle.                                                       |
| `meta.tag`                                  | Tags for the bundle.                                                                  |
| `implicitRules`                             | A set of rules under which the bundle operates.                                       |
| `language`                                  | The language of the bundle.                                                           |
| `text.status`                               | The status of the narrative text.                                                     |
| `text.div`                                  | The narrative text of the bundle.                                                     |
| `identifier`                                | The unique identifier for the bundle.                                                 |
| `identifier.use`                            | The use of the identifier.                                                            |
| `identifier.type`                           | The type of the identifier.                                                           |
| `identifier.type.coding.system`             | The system of the identifier type coding.                                             |
| `identifier.type.coding.version`            | The version of the identifier type coding.                                            |
| `identifier.type.coding.code`               | The code of the identifier type coding.                                               |
| `identifier.type.coding.display`            | The display of the identifier type coding.                                            |
| `identifier.type.coding.userSelected`       | Indicates if the identifier type coding was user-selected.                            |
| `identifier.type.text`                      | The text of the identifier type.                                                      |
| `identifier.system`                         | The system of the identifier.                                                         |
| `identifier.value`                          | The value of the identifier.                                                          |
| `identifier.period`                         | The period during which the identifier is valid.                                      |
| `identifier.period.start`                   | The start date of the identifier period.                                              |
| `identifier.period.end`                     | The end date of the identifier period.                                                |
| `identifier.assigner`                       | The assigner of the identifier.                                                       |
| `identifier.assigner.reference`             | The reference of the assigner.                                                        |
| `identifier.assigner.type`                  | The type of the assigner.                                                             |
| `identifier.assigner.identifier`            | The identifier of the assigner.                                                       |
| `identifier.assigner.identifier.use`        | The use of the assigner's identifier.                                                 |
| `identifier.assigner.identifier.type`       | The type of the assigner's identifier.                                                |
| `identifier.assigner.identifier.system`     | The system of the assigner's identifier.                                              |
| `identifier.assigner.identifier.value`      | The value of the assigner's identifier.                                               |
| `identifier.assigner.identifier.period`     | The period of the assigner's identifier.                                              |
| `identifier.assigner.identifier.assigner`   | The assigner of the assigner's identifier.                                            |
| `identifier.assigner.display`               | The display of the assigner.                                                          |
| `type`                                      | The type of the bundle.                                                               |
| `timestamp`                                 | The timestamp of the bundle.                                                          |
| `total`                                     | The total number of entries in the bundle.                                            |
| `link`                                      | The links associated with the bundle.                                                 |
| `link.relation`                             | The relation type of the link.                                                        |
| `link.url`                                  | The URL of the link.                                                                  |
| `entry`                                     | The entries in the bundle.                                                            |
| `entry.link`                                | The links associated with the entry.                                                  |
| `entry.fullUrl`                             | The full URL of the entry.                                                            |
| `entry.resource`                            | The resource contained in the entry.                                                  |
| `entry.search`                              | The search information for the entry.                                                 |
| `entry.search.mode`                         | The search mode for the entry.                                                        |
| `entry.search.score`                        | The search score for the entry.                                                       |
| `entry.request`                             | The request information for the entry.                                                |
| `entry.request.method`                      | The HTTP method for the entry request.                                                |
| `entry.request.url`                         | The URL for the entry request.                                                        |
| `entry.request.ifNoneMatch`                 | The If-None-Match header value for the entry request.                                 |
| `entry.request.ifModifiedSince`             | The If-Modified-Since header value for the entry request.                             |
| `entry.request.ifMatch`                     | The If-Match header value for the entry request.                                      |
| `entry.request.ifNoneExist`                 | The If-None-Exist header value for the entry request.                                 |
| `entry.response`                            | The response information for the entry.                                               |
| `entry.response.status`                     | The status of the entry response.                                                     |
| `entry.response.location`                   | The location of the entry response.                                                   |
| `entry.response.etag`                       | The ETag of the entry response.                                                       |
| `entry.response.lastModified`               | The last modified timestamp of the entry response.                                    |
| `entry.response.outcome`                    | The outcome of the entry response.                                                    |
| `signature`                                 | The signature for the bundle.                                                         |
| `signature.type.system`                     | The system of the signature type.                                                     |
| `signature.type.version`                    | The version of the signature type.                                                    |
| `signature.type.code`                       | The code of the signature type.                                                       |
| `signature.type.display`                    | The display of the signature type.                                                    |
| `signature.type.userSelected`               | Indicates if the signature type was user-selected.                                    |
| `signature.when`                            | The timestamp of the signature.                                                       |
| `signature.who`                             | The entity who created the signature.                                                 |
| `signature.who.reference`                   | The reference of the entity who created the signature.                                |
| `signature.who.type`                        | The type of the entity who created the signature.                                     |
| `signature.who.identifier`                  | The identifier of the entity who created the signature.                               |
| `signature.who.identifier.use`              | The use of the identifier of the entity who created the signature.                    |
| `signature.who.identifier.type`             | The type of the identifier of the entity who created the signature.                   |
| `signature.who.identifier.type.coding.system` | The system of the identifier type coding of the entity who created the signature.   |
| `signature.who.identifier.type.coding.version` | The version of the identifier type coding of the entity who created the signature.  |
| `signature.who.identifier.type.coding.code` | The code of the identifier type coding of the entity who created the signature.      |
| `signature.who.identifier.type.coding.display` | The display of the identifier type coding of the entity who created the signature.  |
| `signature.who.identifier.type.coding.userSelected` | Indicates if the identifier type coding was user-selected.                         |
| `signature.who.identifier.type.text`        | The text of the identifier type of the entity who created the signature.              |
| `signature.who.identifier.system`           | The system of the identifier of the entity who created the signature.                 |
| `signature.who.identifier.value`            | The value of the identifier of the entity who created the signature.                  |
| `signature.who.identifier.period`           | The period of the identifier of the entity who created the signature.                 |
| `signature.who.identifier.period.start`     | The start date of the identifier period of the entity who created the signature.      |
| `signature.who.identifier.period.end`       | The end date of the identifier period of the entity who created the signature.        |
| `signature.who.identifier.assigner`         | The assigner of the identifier of the entity who created the signature.               |
| `signature.who.identifier.assigner.reference` | The reference of the assigner of the identifier of the entity who created the signature. |
| `signature.who.identifier.assigner.type`    | The type of the assigner of the identifier of the entity who created the signature.   |
| `signature.who.identifier.assigner.identifier` | The identifier of the assigner of the identifier of the entity who created the signature. |
| `signature.who.identifier.assigner.display` | The display of the assigner of the identifier of the entity who created the signature. |
| `signature.who.display`                     | The display of the entity who created the signature.                                  |
| `signature.onBehalfOf`                      | The entity on behalf of whom the signature is made.                                   |
| `signature.onBehalfOf.reference`            | The reference of the entity on behalf of whom the signature is made.                  |
| `signature.onBehalfOf.type`                 | The type of the entity on behalf of whom the signature is made.                       |
| `signature.onBehalfOf.identifier`           | The identifier of the entity on behalf of whom the signature is made.                 |
| `signature.onBehalfOf.identifier.use`       | The use of the identifier of the entity on behalf of whom the signature is made.      |
| `signature.onBehalfOf.identifier.type`      | The type of the identifier of the entity on behalf of whom the signature is made.     |
| `signature.onBehalfOf.identifier.type.coding.system` | The system of the identifier type coding of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.type.coding.version` | The version of the identifier type coding of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.type.coding.code` | The code of the identifier type coding of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.type.coding.display` | The display of the identifier type coding of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.type.coding.userSelected` | Indicates if the identifier type coding was user-selected.                            |
| `signature.onBehalfOf.identifier.type.text` | The text of the identifier type of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.system`    | The system of the identifier of the entity on behalf of whom the signature is made.   |
| `signature.onBehalfOf.identifier.value`     | The value of the identifier of the entity on behalf of whom the signature is made.    |
| `signature.onBehalfOf.identifier.period`    | The period of the identifier of the entity on behalf of whom the signature is made.   |
| `signature.onBehalfOf.identifier.period.start` | The start date of the identifier period of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.period.end` | The end date of the identifier period of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.assigner`  | The assigner of the identifier of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.assigner.reference` | The reference of the assigner of the identifier of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.assigner.type` | The type of the assigner of the identifier of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.assigner.identifier` | The identifier of the assigner of the identifier of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.identifier.assigner.display` | The display of the assigner of the identifier of the entity on behalf of whom the signature is made. |
| `signature.onBehalfOf.display`              | The display of the entity on behalf of whom the signature is made.                    |
| `signature.targetFormat`                    | The target format of the signature.                                                   |
| `signature.sigFormat`                       | The signature format.                                                                 |
| `signature.data`                            | The data of the signature.                                                            |


# createCodeableConcept Operation

The `createCodeableConcept` operation facilitates the creation of a FHIR CodeableConcept datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name                | Property Description                                                  |
|------------------------------|------------------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                            |
| `id`                         | The unique identifier for the CodeableConcept resource.                 |
| `extension`                  | Extensions to the CodeableConcept resource.                             |
| `code`                       | The code value of the CodeableConcept.                                  |
| `valueSet`                   | The value set associated with the CodeableConcept.                      |
| `coding.system`              | The coding system for the CodeableConcept.                              |
| `coding.version`             | The version of the coding system.                                       |
| `coding.code`                | The code within the coding system.                                      |
| `coding.display`             | The display name for the coding system.                                  |
| `coding.userSelected`        | Indicates if the coding was user-selected.                               |
| `text`                       | The text representation of the CodeableConcept.                         |
| `targetObjectId`             | The target object's unique identifier for association.                  |
| `FHIRPath`                   | The FHIRPath expression associated with the CodeableConcept.            |


# createCoding Operation

The `createCoding` operation facilitates the creation of a FHIR Coding datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `system`          | The coding system (e.g., LOINC, SNOMED CT).              |
| `version`         | The version of the coding system.                         |
| `code`            | The code value within the coding system.                  |
| `display`         | The human-readable display name for the coding system.    |
| `userSelected`    | Indicates if the coding was selected by the user.         |
| `custom`          | Custom properties related to the coding.                  |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the Coding.       |

# createContactDetail Operation

The `createContactDetail` operation allows you to create a FHIR ContactDetail datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name                | Property Description                                                  |
|------------------------------|------------------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                            |
| `id`                         | The unique identifier for the ContactDetail resource.                 |
| `extension`                  | Extensions to the ContactDetail resource.                             |
| `name`                       | The name of the contact.                                              |
| `telecom`                    | The contact details, for example, phone, email, etc.                  |
| `telecom.system`             | The system of the telecom contact detail (e.g., phone, email).        |
| `telecom.value`              | The value of the telecom contact detail.                              |
| `telecom.use`                | The use of the telecom contact detail (e.g., home, work).             |
| `telecom.rank`               | The rank of the telecom contact detail.                               |
| `telecom.period`             | The period during which the telecom contact detail is valid.          |
| `telecom.period.start`       | The start date of the period during which the telecom is valid.       |
| `telecom.period.end`         | The end date of the period during which the telecom is valid.         |
| `targetObjectId`             | The target object's unique identifier for association.                |
| `FHIRPath`                   | The FHIRPath expression associated with the ContactDetail.            |


# createContactPoint Operation

The `createContactPoint` operation allows you to create a FHIR ContactPoint datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name                | Property Description                                                  |
|------------------------------|------------------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                            |
| `id`                         | The unique identifier for the ContactPoint resource.                 |
| `extension`                  | Extensions to the ContactPoint resource.                             |
| `system`                     | The system of the contact point (e.g., phone, email).                |
| `value`                      | The value of the contact point.                                      |
| `use`                        | The use of the contact point (e.g., home, work).                     |
| `rank`                       | The rank of the contact point.                                       |
| `period`                     | The period during which the contact point is valid.                  |
| `period.start`               | The start date of the period during which the contact point is valid.|
| `period.end`                 | The end date of the period during which the contact point is valid.  |
| `targetObjectId`             | The target object's unique identifier for association.                |
| `FHIRPath`                   | The FHIRPath expression associated with the ContactPoint.            |

# createCount Operation

The `createCount` operation allows you to create a FHIR Count datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name                | Property Description                                                  |
|------------------------------|------------------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                            |
| `id`                         | The unique identifier for the Count resource.                         |
| `extension`                  | Extensions to the Count resource.                                     |
| `value`                      | The numerical value of the count.                                     |
| `comparator`                 | The comparator for the count value (e.g., <, <=, >, >=).              |
| `unit`                       | The unit of the count value (e.g., kg, lbs).                          |
| `system`                     | The system that defines the unit (e.g., UCUM).                        |
| `code`                       | The code for the count unit (e.g., kg for kilograms, lbs for pounds). |
| `targetObjectId`             | The target object's unique identifier for association.                |
| `FHIRPath`                   | The FHIRPath expression associated with the Count.                    |

# createContributor Operation

The `createContributor` operation allows you to create a FHIR Contributor datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name                | Property Description                                                  |
|------------------------------|------------------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                            |
| `id`                         | The unique identifier for the Contributor resource.                   |
| `extension`                  | Extensions to the Contributor resource.                               |
| `type`                       | The type of contributor (e.g., author, editor, reviewer).             |
| `name`                       | The name of the contributor.                                          |
| `contact`                    | The contact details of the contributor, including telecom details.    |
| `contact.system`             | The system of the contact detail (e.g., phone, email).                |
| `contact.value`              | The value of the contact detail.                                      |
| `contact.use`                | The use of the contact detail (e.g., home, work).                     |
| `contact.rank`               | The rank of the contact detail.                                       |
| `contact.period`             | The period during which the contact detail is valid.                  |
| `contact.period.start`       | The start date of the period during which the contact is valid.       |
| `contact.period.end`         | The end date of the period during which the contact is valid.         |
| `targetObjectId`             | The target object's unique identifier for association.                |
| `FHIRPath`                   | The FHIRPath expression associated with the Contributor.              |

# createDataRequirement Operation

The `createDataRequirement` operation allows you to create a FHIR DataRequirement datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name                | Property Description                                                  |
|------------------------------|------------------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                            |
| `id`                         | The unique identifier for the DataRequirement resource.               |
| `extension`                  | Extensions to the DataRequirement resource.                           |
| `type`                       | The type of data (e.g., Condition, Observation).                      |
| `profile`                    | The profile for the data.                                             |
| `subject`                    | The subject of the data.                                              |
| `mustSupport`                | Elements which must be supported.                                     |
| `codeFilter`                 | Code-based filters for the data.                                      |
| `codeFilter.searchParam`     | The search parameter for the code filter.                             |
| `codeFilter.valueSet`        | The value set for the code filter.                                    |
| `codeFilter.code`            | The code for the code filter.                                         |
| `codeFilter.code.system`     | The system of the code for the code filter.                           |
| `codeFilter.code.version`    | The version of the code for the code filter.                          |
| `codeFilter.code.code`       | The code of the code for the code filter.                             |
| `codeFilter.code.display`    | The display of the code for the code filter.                          |
| `codeFilter.code.userSelected`| Indicates if the code was user-selected.                              |
| `dateFilter`                 | Date/DateTime-based filters for the data.                             |
| `dateFilter.path`            | The path for the date filter.                                         |
| `dateFilter.searchParam`     | The search parameter for the date filter.                             |
| `dateFilter.valueDateTime`   | The DateTime value for the date filter.                               |
| `dateFilter.valuePeriod`     | The Period value for the date filter.                                 |
| `dateFilter.valuePeriod.start`| The start of the Period value for the date filter.                    |
| `dateFilter.valuePeriod.end` | The end of the Period value for the date filter.                      |
| `dateFilter.valueDuration`   | The Duration value for the date filter.                               |
| `dateFilter.valueDuration.value`| The value of the Duration for the date filter.                      |
| `dateFilter.valueDuration.comparator`| The comparator of the Duration for the date filter.            |
| `dateFilter.valueDuration.unit`| The unit of the Duration for the date filter.                      |
| `dateFilter.valueDuration.system`| The system of the Duration for the date filter.                  |
| `dateFilter.valueDuration.code`| The code of the Duration for the date filter.                      |
| `limit`                      | The limit for the data.                                               |
| `sort`                       | Sort order for the data.                                              |
| `sort.path`                  | The path for the sort order.                                          |
| `sort.direction`             | The direction of the sort order.                                      |
| `targetObjectId`             | The target object's unique identifier for association.                |
| `FHIRPath`                   | The FHIRPath expression associated with the DataRequirement.          |

# createDistance Operation

The `createDistance` operation allows you to create a FHIR Distance datatype and include it to a given FHIR path of constructed FHIR resource(s).

## Properties

| Property Name                | Property Description                                                  |
|------------------------------|------------------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                            |
| `id`                         | The unique identifier for the Distance resource.                      |
| `extension`                  | Extensions to the Distance resource.                                  |
| `value`                      | The numerical value of the distance.                                  |
| `comparator`                 | The comparator for the distance value (e.g., <, <=, >, >=).           |
| `unit`                       | The unit of the distance value (e.g., km, miles).                     |
| `system`                     | The system that defines the unit (e.g., UCUM).                        |
| `code`                       | The code for the distance unit (e.g., km for kilometers, mi for miles).|
| `targetObjectId`             | The target object's unique identifier for association.                |
| `FHIRPath`                   | The FHIRPath expression associated with the Distance.                 |

# createDosage Operation

The `createDosage` operation facilitates the creation of a FHIR Dosage datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to dosage information, including related elements such as timing, route, and rate.

## Properties

| Property Name                      | Property Description                                                                                       |
|------------------------------------|------------------------------------------------------------------------------------------------------------|
| `objectId`                         | The unique identifier for the target object.                                                               |
| `id`                               | Unique identifier for the element.                                                                         |
| `extension`                        | Additional content defined by implementations.                                                             |
| `sequence`                         | The order of the dosage instructions.                                                                      |
| `text`                             | Free text dosage instructions.                                                                             |
| `additionalInstruction`            | Supplemental instruction, e.g., "with meals".                                                              |
| `additionalInstruction.code`       | Code for the additional instruction.                                                                       |
| `additionalInstruction.valueSet`   | Value set for the additional instruction.                                                                  |
| `additionalInstruction.coding.system` | The system of the coding for the additional instruction.                                               |
| `additionalInstruction.coding.version` | The version of the coding for the additional instruction.                                             |
| `additionalInstruction.coding.code` | The code of the additional instruction.                                                                 |
| `additionalInstruction.coding.display` | The display text of the additional instruction.                                                      |
| `additionalInstruction.coding.userSelected` | Indicates if the coding was selected by the user.                                                |
| `additionalInstruction.text`       | Free text additional instruction.                                                                          |
| `timing`                           | The timing of the dosage.                                                                                   |
| `timing.event`                     | Event timing of the dosage.                                                                                 |
| `timing.repeat`                    | Repetition pattern of the dosage.                                                                           |
| `timing.repeat.boundsDuration`     | Duration of the dosage repetition bounds.                                                                   |
| `timing.repeat.boundsDuration.value` | Value of the dosage repetition bounds duration.                                                          |
| `timing.repeat.boundsDuration.comparator` | Comparator for the dosage repetition bounds duration.                                              |
| `timing.repeat.boundsDuration.unit` | Unit for the dosage repetition bounds duration.                                                          |
| `timing.repeat.boundsDuration.system` | System for the dosage repetition bounds duration.                                                     |
| `timing.repeat.boundsDuration.code` | Code for the dosage repetition bounds duration.                                                         |
| `timing.repeat.boundsRange`        | Range of the dosage repetition bounds.                                                                      |
| `timing.repeat.boundsRange.low`    | Lower bound of the dosage repetition range.                                                                |
| `timing.repeat.boundsRange.low.value` | Value of the lower bound of the dosage repetition range.                                              |
| `timing.repeat.boundsRange.low.unit` | Unit of the lower bound of the dosage repetition range.                                                |
| `timing.repeat.boundsRange.low.system` | System of the lower bound of the dosage repetition range.                                           |
| `timing.repeat.boundsRange.low.code` | Code of the lower bound of the dosage repetition range.                                               |
| `timing.repeat.boundsRange.high`   | Upper bound of the dosage repetition range.                                                                |
| `timing.repeat.boundsRange.high.value` | Value of the upper bound of the dosage repetition range.                                              |
| `timing.repeat.boundsRange.high.unit` | Unit of the upper bound of the dosage repetition range.                                                |
| `timing.repeat.boundsRange.high.system` | System of the upper bound of the dosage repetition range.                                           |
| `timing.repeat.boundsRange.high.code` | Code of the upper bound of the dosage repetition range.                                               |
| `timing.repeat.boundsPeriod`       | Period of the dosage repetition bounds.                                                                    |
| `timing.repeat.boundsPeriod.start` | Start of the dosage repetition bounds period.                                                             |
| `timing.repeat.boundsPeriod.end`   | End of the dosage repetition bounds period.                                                                |
| `timing.repeat.count`              | Count of the dosage repetitions.                                                                           |
| `timing.repeat.countMax`           | Maximum count of the dosage repetitions.                                                                   |
| `timing.repeat.duration`           | Duration of the dosage repetitions.                                                                        |
| `timing.repeat.durationMax`        | Maximum duration of the dosage repetitions.                                                                |
| `timing.repeat.durationUnit`       | Unit of the dosage repetition duration.                                                                    |
| `timing.repeat.frequency`          | Frequency of the dosage repetitions.                                                                       |
| `timing.repeat.frequencyMax`       | Maximum frequency of the dosage repetitions.                                                               |
| `timing.repeat.period`             | Period of the dosage repetitions.                                                                          |
| `timing.repeat.periodMax`          | Maximum period of the dosage repetitions.                                                                  |
| `timing.repeat.periodUnit`         | Unit of the dosage repetition period.                                                                      |
| `timing.repeat.dayOfWeek`          | Days of the week for the dosage repetitions.                                                               |
| `timing.repeat.timeOfDay`          | Time of day for the dosage repetitions.                                                                    |
| `timing.repeat.when`               | When the dosage repetitions occur.                                                                         |
| `timing.repeat.offset`             | Offset for the dosage repetitions.                                                                         |
| `timing.code`                      | Code for the timing of the dosage.                                                                         |
| `timing.code.coding.system`        | System of the timing code.                                                                                 |
| `timing.code.coding.version`       | Version of the timing code.                                                                                |
| `timing.code.coding.code`          | Code value for the timing.                                                                                 |
| `timing.code.coding.display`       | Display value for the timing code.                                                                         |
| `timing.code.coding.userSelected`  | Indicates if the timing code was selected by the user.                                                     |
| `timing.code.text`                 | Free text timing code.                                                                                     |
| `asNeededBoolean`                  | Indicates if the dosage is taken as needed (boolean).                                                      |
| `asNeededCodeableConcept`          | Codeable concept indicating if the dosage is taken as needed.                                              |
| `asNeededCodeableConcept.code`     | Code for the as-needed codeable concept.                                                                   |
| `asNeededCodeableConcept.valueSet` | Value set for the as-needed codeable concept.                                                              |
| `asNeededCodeableConcept.coding.system` | System for the as-needed codeable concept.                                                           |
| `asNeededCodeableConcept.coding.version` | Version for the as-needed codeable concept.                                                       |
| `asNeededCodeableConcept.coding.code` | Code value for the as-needed codeable concept.                                                       |
| `asNeededCodeableConcept.coding.display` | Display value for the as-needed codeable concept.                                                   |
| `asNeededCodeableConcept.coding.userSelected` | Indicates if the as-needed codeable concept was selected by the user.                             |
| `asNeededCodeableConcept.text`     | Free text as-needed codeable concept.                                                                      |
| `site`                             | Site of the dosage administration.                                                                         |
| `site.code`                        | Code for the site of administration.                                                                       |
| `site.valueSet`                    | Value set for the site of administration.                                                                  |
| `site.coding.system`               | System for the site coding.                                                                                |
| `site.coding.version`              | Version for the site coding.                                                                               |
| `site.coding.code`                 | Code value for the site of administration.                                                                 |
| `site.coding.display`              | Display value for the site of administration.                                                              |
| `site.coding.userSelected`         | Indicates if the site coding was selected by the user.                                                     |
| `site.text`                        | Free text site of administration.                                                                          |
| `route`                            | Route of the dosage administration.                                                                        |
| `route.code`                       | Code for the route of administration.                                                                      |
| `route.valueSet`                   | Value set for the route of administration.                                                                 |
| `route.coding.system`              | System for the route coding.                                                                               |
| `route.coding.version`             | Version for the route coding.                                                                              |
| `route.coding.code`                | Code value for the route of administration.                                                                |
| `route.coding.display`             | Display value for the route of administration.                                                             |
| `route.coding.userSelected`        | Indicates if the route coding was selected by the user.                                                    |
| `route.text`                       | Free text route of administration.                                                                         |
| `method`                           | Method of the dosage administration.                                                                       |
| `method.code`                      | Code for the method of administration.                                                                     |
| `method.valueSet`                  | Value set for the method of administration.                                                                |
| `method.coding.system`             | System for the method coding.                                                                              |
| `method.coding.version`            | Version for the method coding.                                                                             |
| `method.coding.code`               | Code value for the method of administration.                                                               |
| `method.coding.display`            | Display value for the method of administration.                                                            |
| `method.coding.userSelected`       | Indicates if the method coding was selected by the user.                                                   |
| `method.text`                      | Free text method of administration.                                                                        |
| `doseAndRate`                      | Dosage and rate information.                                                                               |
| `doseAndRate.type`                 | Type of dose and rate.                                                                                     |
| `doseAndRate.type.code`            | Code for the type of dose and rate.                                                                        |
| `doseAndRate.type.valueSet`        | Value set for the type of dose and rate.                                                                   |
| `doseAndRate.type.coding.system`   | System for the type coding.                                                                                |
| `doseAndRate.type.coding.version`  | Version for the type coding.                                                                               |
| `doseAndRate.type.coding.code`     | Code value for the type of dose and rate.                                                                  |
| `doseAndRate.type.coding.display`  | Display value for the type of dose and rate.                                                               |
| `doseAndRate.type.coding.userSelected` | Indicates if the type coding was selected by the user.                                               |
| `doseAndRate.type.text`            | Free text type of dose and rate.                                                                           |
| `doseAndRate.doseRange`            | Range of the dose.                                                                                         |
| `doseAndRate.doseRange.low`        | Lower bound of the dose range.                                                                             |
| `doseAndRate.doseRange.low.value`  | Value of the lower bound of the dose range.                                                                |
| `doseAndRate.doseRange.low.unit`   | Unit of the lower bound of the dose range.                                                                 |
| `doseAndRate.doseRange.low.system` | System of the lower bound of the dose range.                                                               |
| `doseAndRate.doseRange.low.code`   | Code of the lower bound of the dose range.                                                                 |
| `doseAndRate.doseRange.high`       | Upper bound of the dose range.                                                                             |
| `doseAndRate.doseRange.high.value` | Value of the upper bound of the dose range.                                                                |
| `doseAndRate.doseRange.high.unit`  | Unit of the upper bound of the dose range.                                                                 |
| `doseAndRate.doseRange.high.system` | System of the upper bound of the dose range.                                                             |
| `doseAndRate.doseRange.high.code`  | Code of the upper bound of the dose range.                                                                 |
| `doseAndRate.doseQuantity`         | Quantity of the dose.                                                                                      |
| `doseAndRate.doseQuantity.value`   | Value of the dose quantity.                                                                                |
| `doseAndRate.doseQuantity.unit`    | Unit of the dose quantity.                                                                                 |
| `doseAndRate.doseQuantity.system`  | System of the dose quantity.                                                                               |
| `doseAndRate.doseQuantity.code`    | Code of the dose quantity.                                                                                 |
| `doseAndRate.rateRatio`            | Ratio of the rate.                                                                                         |
| `doseAndRate.rateRatio.numerator`  | Numerator of the rate ratio.                                                                               |
| `doseAndRate.rateRatio.numerator.value` | Value of the numerator of the rate ratio.                                                             |
| `doseAndRate.rateRatio.numerator.comparator` | Comparator for the numerator of the rate ratio.                                                  |
| `doseAndRate.rateRatio.numerator.unit` | Unit for the numerator of the rate ratio.                                                            |
| `doseAndRate.rateRatio.numerator.system` | System for the numerator of the rate ratio.                                                      |
| `doseAndRate.rateRatio.numerator.code` | Code for the numerator of the rate ratio.                                                          |
| `doseAndRate.rateRatio.denominator` | Denominator of the rate ratio.                                                                        |
| `doseAndRate.rateRatio.denominator.value` | Value of the denominator of the rate ratio.                                                      |
| `doseAndRate.rateRatio.denominator.comparator` | Comparator for the denominator of the rate ratio.                                              |
| `doseAndRate.rateRatio.denominator.unit` | Unit for the denominator of the rate ratio.                                                      |
| `doseAndRate.rateRatio.denominator.system` | System for the denominator of the rate ratio.                                                  |
| `doseAndRate.rateRatio.denominator.code` | Code for the denominator of the rate ratio.                                                    |
| `doseAndRate.rateRange`            | Range of the rate.                                                                                         |
| `doseAndRate.rateRange.low`        | Lower bound of the rate range.                                                                             |
| `doseAndRate.rateRange.low.value`  | Value of the lower bound of the rate range.                                                                |
| `doseAndRate.rateRange.low.unit`   | Unit of the lower bound of the rate range.                                                                 |
| `doseAndRate.rateRange.low.system` | System of the lower bound of the rate range.                                                               |
| `doseAndRate.rateRange.low.code`   | Code of the lower bound of the rate range.                                                                 |
| `doseAndRate.rateRange.high`       | Upper bound of the rate range.                                                                             |
| `doseAndRate.rateRange.high.value` | Value of the upper bound of the rate range.                                                                |
| `doseAndRate.rateRange.high.unit`  | Unit of the upper bound of the rate range.                                                                 |
| `doseAndRate.rateRange.high.system` | System of the upper bound of the rate range.                                                             |
| `doseAndRate.rateRange.high.code`  | Code of the upper bound of the rate range.                                                                 |
| `doseAndRate.rateQuantity`         | Quantity of the rate.                                                                                      |
| `doseAndRate.rateQuantity.value`   | Value of the rate quantity.                                                                                |
| `doseAndRate.rateQuantity.comparator` | Comparator for the rate quantity.                                                                    |
| `doseAndRate.rateQuantity.unit`    | Unit of the rate quantity.                                                                                 |
| `doseAndRate.rateQuantity.system`  | System of the rate quantity.                                                                               |
| `doseAndRate.rateQuantity.code`    | Code of the rate quantity.                                                                                 |
| `maxDosePerPeriod`                 | Maximum dose per period.                                                                                   |
| `maxDosePerPeriod.numerator`       | Numerator of the maximum dose per period.                                                                  |
| `maxDosePerPeriod.numerator.value` | Value of the numerator of the maximum dose per period.                                                     |
| `maxDosePerPeriod.numerator.comparator` | Comparator for the numerator of the maximum dose per period.                                         |
| `maxDosePerPeriod.numerator.unit`  | Unit of the numerator of the maximum dose per period.                                                      |
| `maxDosePerPeriod.numerator.system` | System of the numerator of the maximum dose per period.                                                |
| `maxDosePerPeriod.numerator.code`  | Code of the numerator of the maximum dose per period.                                                      |
| `maxDosePerPeriod.denominator`     | Denominator of the maximum dose per period.                                                                |
| `maxDosePerPeriod.denominator.value` | Value of the denominator of the maximum dose per period.                                               |
| `maxDosePerPeriod.denominator.comparator` | Comparator for the denominator of the maximum dose per period.                                       |
| `maxDosePerPeriod.denominator.unit` | Unit of the denominator of the maximum dose per period.                                                |
| `maxDosePerPeriod.denominator.system` | System of the denominator of the maximum dose per period.                                             |
| `maxDosePerPeriod.denominator.code` | Code of the denominator of the maximum dose per period.                                               |
| `maxDosePerAdministration`         | Maximum dose per administration.                                                                           |
| `maxDosePerAdministration.value`   | Value of the maximum dose per administration.                                                              |
| `maxDosePerAdministration.unit`    | Unit of the maximum dose per administration.                                                               |
| `maxDosePerAdministration.system`  | System of the maximum dose per administration.                                                             |
| `maxDosePerAdministration.code`    | Code of the maximum dose per administration.                                                               |
| `maxDosePerLifetime`               | Maximum dose per lifetime.                                                                                 |
| `maxDosePerLifetime.value`         | Value of the maximum dose per lifetime.                                                                    |
| `maxDosePerLifetime.unit`          | Unit of the maximum dose per lifetime.                                                                     |
| `maxDosePerLifetime.system`        | System of the maximum dose per lifetime.                                                                   |
| `maxDosePerLifetime.code`          | Code of the maximum dose per lifetime.                                                                     |
| `targetObjectId`                   | The target object's unique identifier for association.                                                     |
| `FHIRPath`                         | The FHIRPath expression associated with the Dosage.                                                        |

# createDuration Operation

The `createDuration` operation facilitates the creation of a FHIR Duration datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to duration information, such as value, comparator, unit, system, and code.

## Properties

| Property Name     | Property Description                                       |
|-------------------|------------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.               |
| `id`              | Unique identifier for the element.                         |
| `extension`       | Additional content defined by implementations.             |
| `value`           | Numerical value for the duration.                          |
| `comparator`      | Comparator for the duration value (e.g., <, <=, >, >=).    |
| `unit`            | Unit of time for the duration (e.g., seconds, minutes).    |
| `system`          | System that defines the unit of time (e.g., UCUM).         |
| `code`            | Code representing the unit of time.                        |
| `targetObjectId`  | The target object's unique identifier for association.     |
| `FHIRPath`        | The FHIRPath expression associated with the Duration.      |

# createExpression Operation

The `createExpression` operation facilitates the creation of an FHIR Expression datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to expressions, such as description, name, language, expression, and reference.

## Properties

| Property Name     | Property Description                                       |
|-------------------|------------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.               |
| `id`              | Unique identifier for the element.                         |
| `extension`       | Additional content defined by implementations.             |
| `description`     | Textual description of the expression.                     |
| `name`            | Name of the expression.                                    |
| `language`        | The language of the expression (e.g., FHIRPath, CQL).      |
| `expression`      | The actual expression.                                     |
| `reference`       | Reference to where the expression is found.                |
| `targetObjectId`  | The target object's unique identifier for association.     |
| `FHIRPath`        | The FHIRPath expression associated with the Expression.    |

# createHumanName Operation

The `createHumanName` operation facilitates the creation of a FHIR HumanName datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to human names, such as use, text, family, given, prefix, suffix, and period.

## Properties

| Property Name     | Property Description                                        |
|-------------------|-------------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.                |
| `id`              | Unique identifier for the element.                          |
| `extension`       | Additional content defined by implementations.              |
| `use`             | The use of the name (e.g., usual, official, temp, nickname).|
| `text`            | The full name as a single string.                           |
| `family`          | The family name (last name).                                |
| `given`           | The given names (first name, middle name).                  |
| `prefix`          | Parts of the name that come before the given name.          |
| `suffix`          | Parts of the name that come after the given name.           |
| `period`          | The period during which the name is valid.                  |
| `period.start`    | The start of the period.                                    |
| `period.end`      | The end of the period.                                      |
| `targetObjectId`  | The target object's unique identifier for association.      |
| `FHIRPath`        | The FHIRPath expression associated with the HumanName.      |

# createIdentifier Operation

The `createIdentifier` operation facilitates the creation of an FHIR Identifier datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to identifiers, such as use, type, system, value, period, and assigner.

## Properties

| Property Name                | Property Description                                        |
|------------------------------|-------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                |
| `id`                         | Unique identifier for the element.                          |
| `extension`                  | Additional content defined by implementations.              |
| `use`                        | The use of the identifier (e.g., usual, official, temp).    |
| `type`                       | The type of identifier.                                     |
| `type.coding.system`         | The coding system for the type of identifier.               |
| `type.coding.version`        | The version of the coding system.                           |
| `type.coding.code`           | The code value within the coding system.                    |
| `type.coding.display`        | The human-readable display name for the coding system.      |
| `type.coding.userSelected`   | Indicates if the coding was selected by the user.           |
| `type.text`                  | The text representation of the identifier type.             |
| `system`                     | The system that assigns the identifier.                     |
| `value`                      | The value of the identifier.                                |
| `period`                     | The period during which the identifier is valid.            |
| `period.start`               | The start of the period.                                    |
| `period.end`                 | The end of the period.                                      |
| `assigner`                   | The organization that issued the identifier.                |
| `assigner.reference`         | Reference to the assigner.                                  |
| `assigner.type`              | Type of the assigner.                                       |
| `assigner.identifier.use`    | The use of the assigner's identifier.                       |
| `assigner.identifier.type`   | The type of the assigner's identifier.                      |
| `assigner.identifier.period` | The period during which the assigner's identifier is valid. |
| `assigner.identifier.assigner` | The assigner of the assigner's identifier.                |
| `assigner.display`           | The display name for the assigner.                          |
| `targetObjectId`             | The target object's unique identifier for association.      |
| `FHIRPath`                   | The FHIRPath expression associated with the Identifier.     |

# createMeta Operation

The `createMeta` operation facilitates the creation of a FHIR Meta datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to meta-information, such as version ID, source, profiles, security labels, and tags.

## Properties

| Property Name                | Property Description                                        |
|------------------------------|-------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                |
| `id`                         | Unique identifier for the element.                          |
| `extension`                  | Additional content defined by implementations.              |
| `versionId`                  | The version ID of the resource.                             |
| `source`                     | The source of the resource.                                 |
| `profile`                    | Profiles associated with the resource.                      |
| `security`                   | Security labels associated with the resource.               |
| `security.code`              | The code for the security label.                            |
| `security.valueSet`          | The value set for the security label.                       |
| `security.coding.system`     | The coding system for the security label.                   |
| `security.coding.version`    | The version of the coding system.                           |
| `security.coding.code`       | The code value within the coding system.                    |
| `security.coding.display`    | The human-readable display name for the coding system.      |
| `security.coding.userSelected` | Indicates if the coding was selected by the user.         |
| `security.text`              | The text representation of the security label.              |
| `tag`                        | Tags associated with the resource.                          |
| `tag.code`                   | The code for the tag.                                       |
| `tag.valueSet`               | The value set for the tag.                                  |
| `tag.coding.system`          | The coding system for the tag.                              |
| `tag.coding.version`         | The version of the coding system.                           |
| `tag.coding.code`            | The code value within the coding system.                    |
| `tag.coding.display`         | The human-readable display name for the coding system.      |
| `tag.coding.userSelected`    | Indicates if the coding was selected by the user.           |
| `tag.text`                   | The text representation of the tag.                         |
| `targetObjectId`             | The target object's unique identifier for association.      |
| `FHIRPath`                   | The FHIRPath expression associated with the Meta.           |

# createMoney Operation

The `createMoney` operation facilitates the creation of a FHIR Money datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to monetary values, such as value, currency, and system.

## Properties

| Property Name                | Property Description                                        |
|------------------------------|-------------------------------------------------------------|
| `objectId`                   | The unique identifier for the target object.                |
| `id`                         | Unique identifier for the element.                          |
| `extension`                  | Additional content defined by implementations.              |
| `value`                      | The numerical value of the money.                           |
| `currency`                   | The currency of the money.                                 |
| `system`                     | The system that defines the currency.                      |
| `code`                       | The code for the currency.                                 |
| `targetObjectId`             | The target object's unique identifier for association.      |
| `FHIRPath`                   | The FHIRPath expression associated with the Money.          |

# createMoneyQuantity Operation

The `createMoneyQuantity` operation facilitates the creation of a FHIR MoneyQuantity datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to quantities representing monetary amounts.

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `id`              | Unique identifier for the element.                        |
| `extension`       | Additional content defined by implementations.            |
| `value`           | The numerical value of the quantity.                      |
| `comparator`      | The comparator for the value (e.g., `<`, `<=`, `>`, `>=`).|
| `unit`            | The unit of the quantity.                                 |
| `system`          | The system that defines the coded unit form.              |
| `code`            | The code that specifies the unit of measurement.          |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the MoneyQuantity.|

# createNarrative Operation

The `createNarrative` operation facilitates the creation of a FHIR Narrative datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to the textual content and status of a narrative.

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `id`              | Unique identifier for the element.                        |
| `extension`       | Additional content defined by implementations.            |
| `status`          | The status of the narrative (e.g., generated, extensions).|
| `div`             | The XHTML representation of the narrative content.        |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the Narrative.    |

# createParameterDefinition Operation

The `createParameterDefinition` operation facilitates the creation of a FHIR ParameterDefinition datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to defining parameters used in operations and interactions.

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `id`              | Unique identifier for the element.                        |
| `extension`       | Additional content defined by implementations.            |
| `name`            | The name of the parameter.                                |
| `use`             | The use of the parameter (e.g., input, output).           |
| `min`             | The minimum number of times the parameter can appear.    |
| `max`             | The maximum number of times the parameter can appear.    |
| `documentation`   | Description or documentation about the parameter.         |
| `type`            | The type of the parameter (e.g., string, integer).        |
| `profile`         | The profile that describes the parameter.                |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the ParameterDefinition. |

# createPeriod Operation

The `createPeriod` operation facilitates the creation of a FHIR Period datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to defining a time period.

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `id`              | Unique identifier for the element.                        |
| `extension`       | Additional content defined by implementations.            |
| `start`           | The start date/time of the period.                        |
| `end`             | The end date/time of the period.                          |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the Period.       |

# createPrimitiveType Operation

The `createPrimitiveType` operation allows for the creation of a Primitive datatype include it to a given FHIR path of constructed FHIR resource(s). This operation is flexible, handling different primitive data types based on the provided parameters.

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `type`            | The type of the primitive data (e.g., string, integer).    |
| `value`           | The value of the primitive data.                         |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the Primitive Data Type. |

# createQuantity Operation

The `createQuantity` operation is used to create a FHIR Quantity datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to quantities, such as values, units, and codes.

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `id`              | The unique identifier for the resource, inherited from Element. |
| `extension`       | Extensions for additional data, inherited from Element.  |
| `value`           | The numerical value of the quantity.                     |
| `comparator`      | The comparator for the quantity value (e.g., `<`, `<=`).  |
| `unit`            | The unit of measure for the quantity (e.g., `mg`, `ml`).  |
| `system`          | The coding system used for the unit.                     |
| `code`            | The code that represents the unit of measure.            |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the Quantity.     |

# createRange Operation

The `createRange` operation facilitates the creation of a FHIR Range datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters that define a range of values.

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `id`              | The unique identifier for the resource, inherited from Element. |
| `extension`       | Extensions for additional data, inherited from Element.  |
| `low`             | The lower bound of the range.                            |
| `low.value`       | The numerical value of the lower bound.                  |
| `low.unit`        | The unit of measure for the lower bound.                 |
| `low.system`      | The coding system used for the unit of the lower bound.  |
| `low.code`        | The code that represents the unit of the lower bound.    |
| `high`            | The upper bound of the range.                            |
| `high.value`      | The numerical value of the upper bound.                  |
| `high.unit`       | The unit of measure for the upper bound.                 |
| `high.system`     | The coding system used for the unit of the upper bound.  |
| `high.code`       | The code that represents the unit of the upper bound.    |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the Range.        |

# createRatio Operation

The `createRatio` operation facilitates the creation of a FHIR Ratio datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters that define the numerator and denominator of the ratio.

## Properties

| Property Name     | Property Description                                    |
|-------------------|----------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.              |
| `id`              | The unique identifier for the resource, inherited from Element. |
| `extension`       | Extensions for additional data, inherited from Element.  |
| `numerator`       | The numerator of the ratio.                              |
| `numerator.value` | The numerical value of the numerator.                    |
| `numerator.comparator` | The comparator used for the numerator value.          |
| `numerator.unit`  | The unit of measure for the numerator.                   |
| `numerator.system`| The coding system used for the unit of the numerator.    |
| `numerator.code`  | The code that represents the unit of the numerator.      |
| `denominator`     | The denominator of the ratio.                            |
| `denominator.value` | The numerical value of the denominator.                |
| `denominator.comparator` | The comparator used for the denominator value.       |
| `denominator.unit`| The unit of measure for the denominator.                 |
| `denominator.system`| The coding system used for the unit of the denominator. |
| `denominator.code`| The code that represents the unit of the denominator.    |
| `targetObjectId`  | The target object's unique identifier for association.    |
| `FHIRPath`        | The FHIRPath expression associated with the Ratio.        |

# createReference Operation

The `createReference` operation facilitates the creation of a FHIR Reference datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters that define the reference to other resources.

## Properties

| Property Name             | Property Description                                          |
|---------------------------|--------------------------------------------------------------|
| `objectId`                | The unique identifier for the target object.                |
| `id`                      | The unique identifier for the resource, inherited from Element. |
| `extension`               | Extensions for additional data, inherited from Element.      |
| `reference`               | The reference URL to the other resource.                     |
| `type`                    | The type of the referenced resource.                         |
| `identifier`              | An identifier used for the reference.                        |
| `identifier.use`          | The use of the identifier (e.g., usual, official).           |
| `identifier.type`         | The type of the identifier (e.g., medical record number).    |
| `identifier.system`       | The system that issues the identifier.                       |
| `identifier.value`        | The value of the identifier.                                 |
| `identifier.period.start` | The start date of the period during which the identifier is valid. |
| `identifier.period.end`   | The end date of the period during which the identifier is valid. |
| `identifier.assigner.reference` | Reference to the organization or individual who assigned the identifier. |
| `identifier.assigner.display` | The display name of the organization or individual who assigned the identifier. |
| `display`                 | The human-readable name for the reference.                   |
| `targetObjectId`          | The target object's unique identifier for association.       |
| `FHIRPath`                | The FHIRPath expression associated with the Reference.       |
| `custom`                  | Custom properties related to the reference.                  |

# createRelatedArtifact Operation

The `createRelatedArtifact` operation facilitates the creation of a FHIR RelatedArtifact datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters related to the various forms of related artifacts such as documents, citations, or URLs.

## Properties

| Property Name             | Property Description                                          |
|---------------------------|--------------------------------------------------------------|
| `objectId`                | The unique identifier for the target object.                |
| `id`                      | The unique identifier for the resource, inherited from Element. |
| `extension`               | Extensions for additional data, inherited from Element.      |
| `type`                    | The type of related artifact (e.g., document, citation).     |
| `label`                   | A label for the related artifact.                            |
| `display`                 | A human-readable display name for the related artifact.      |
| `citation`                | A citation for the related artifact.                         |
| `url`                     | URL of the related artifact.                                |
| `document`                | Document information related to the artifact.               |
| `document.contentType`    | The MIME type of the document (e.g., application/pdf).        |
| `document.language`       | The language of the document.                               |
| `document.data`           | The base64-encoded content of the document.                 |
| `document.url`            | URL to the document if hosted externally.                    |
| `document.size`           | Size of the document in bytes.                              |
| `document.hash`           | Hash of the document for integrity verification.            |
| `document.title`          | Title of the document.                                      |
| `document.creation`       | Creation date of the document.                              |
| `resource`                | Reference to another FHIR resource related to this artifact. |
| `targetObjectId`          | The target object's unique identifier for association.       |
| `FHIRPath`                | The FHIRPath expression associated with the RelatedArtifact. |

# createResource Operation

The `createResource` operation facilitates the creation of a FHIR Resource instance. This operation allows for the creation of various types of FHIR resources, as specified by the `resourceType` parameter.

## Properties

| Property Name    | Property Description                                           |
|------------------|---------------------------------------------------------------|
| `objectId`       | The unique identifier for the target object.                 |
| `resourceType`   | The type of the FHIR resource to be created (e.g., Patient, Observation). |

# createSampledData Operation

The `createSampledData` operation facilitates the creation of a FHIR SampledData datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to SampledData, which represents a set of measurements taken at regular intervals.

## Properties

| Property Name     | Property Description                                           |
|-------------------|---------------------------------------------------------------|
| `objectId`        | The unique identifier for the target object.                 |
| `id`              | The identifier for the SampledData resource.                 |
| `extension`       | Extensions to the SampledData resource.                      |
| `origin.value`    | The origin value of the sampled data.                         |
| `origin.unit`     | The unit of measure for the origin.                           |
| `origin.system`   | The coding system for the origin.                             |
| `origin.code`     | The code for the origin.                                     |
| `period`          | The period between measurements.                             |
| `factor`          | Multiplicative factor for the data.                           |
| `lowerLimit`      | The lower limit of the data range.                            |
| `upperLimit`      | The upper limit of the data range.                            |
| `dimensions`      | The number of dimensions in the data.                         |
| `data`            | The actual sampled data.                                     |
| `targetObjectId`  | The target object's unique identifier for association.        |
| `FHIRPath`        | The FHIRPath expression associated with the SampledData.      |

# createSignature Operation

The `createSignature` operation facilitates the creation of a Signature datatype and include it to a given FHIR path of constructed FHIR resource(s). This operation includes parameters specific to signatures, which represent digital or handwritten endorsements.

## Properties

| Property Name                  | Property Description                                           |
|--------------------------------|---------------------------------------------------------------|
| `objectId`                     | The unique identifier for the target object.                 |
| `id`                           | The identifier for the Signature resource.                   |
| `extension`                    | Extensions to the Signature resource.                        |
| `type.system`                  | The coding system for the type of signature.                  |
| `type.version`                 | The version of the coding system for the type.                |
| `type.code`                    | The code for the type of signature.                           |
| `type.display`                 | The display name for the type of signature.                   |
| `type.userSelected`            | Indicates if the type was selected by the user.               |
| `when`                         | The date and time the signature was applied.                  |
| `who`                          | The individual or entity who applied the signature.           |
| `who.reference`                | Reference to the individual or entity who signed.             |
| `who.type`                     | The type of reference for the individual or entity.           |
| `who.identifier`               | The identifier of the individual or entity.                   |
| `who.identifier.use`           | The use of the identifier (e.g., official, secondary).         |
| `who.identifier.type`          | The type of identifier.                                       |
| `who.identifier.type.coding.system` | The coding system for the identifier type.                   |
| `who.identifier.type.coding.version` | The version of the coding system for the identifier type.     |
| `who.identifier.type.coding.code` | The code for the identifier type.                            |
| `who.identifier.type.coding.display` | The display name for the identifier type.                    |
| `who.identifier.type.coding.userSelected` | Indicates if the identifier type was user-selected.         |
| `who.identifier.type.text`     | The textual description of the identifier type.               |
| `who.identifier.system`        | The system for the identifier.                                |
| `who.identifier.value`         | The value of the identifier.                                  |
| `who.identifier.period`        | The period during which the identifier is valid.              |
| `who.identifier.period.start`  | The start date of the identifier's validity period.            |
| `who.identifier.period.end`    | The end date of the identifier's validity period.              |
| `who.identifier.assigner`      | The entity that assigned the identifier.                      |
| `who.identifier.assigner.reference` | Reference to the assignee of the identifier.                 |
| `who.identifier.assigner.type` | The type of the assignee.                                     |
| `who.identifier.assigner.identifier` | The identifier of the assignee.                             |
| `who.identifier.assigner.display` | The display name of the assignee.                            |
| `who.display`                  | The display name of the individual or entity who signed.       |
| `onBehalfOf`                   | The entity on behalf of whom the signature was made.          |
| `onBehalfOf.reference`         | Reference to the entity on behalf of whom the signature was made. |
| `onBehalfOf.type`              | The type of reference for the entity.                         |
| `onBehalfOf.identifier`        | The identifier of the entity.                                 |
| `onBehalfOf.identifier.use`    | The use of the identifier (e.g., official, secondary).         |
| `onBehalfOf.identifier.type`   | The type of identifier.                                       |
| `onBehalfOf.identifier.type.coding.system` | The coding system for the identifier type.                   |
| `onBehalfOf.identifier.type.coding.version` | The version of the coding system for the identifier type.     |
| `onBehalfOf.identifier.type.coding.code` | The code for the identifier type.                            |
| `onBehalfOf.identifier.type.coding.display` | The display name for the identifier type.                    |
| `onBehalfOf.identifier.type.coding.userSelected` | Indicates if the identifier type was user-selected.         |
| `onBehalfOf.identifier.type.text` | The textual description of the identifier type.               |
| `onBehalfOf.identifier.system` | The system for the identifier.                                |
| `onBehalfOf.identifier.value`  | The value of the identifier.                                  |
| `onBehalfOf.identifier.period` | The period during which the identifier is valid.              |
| `onBehalfOf.identifier.period.start` | The start date of the identifier's validity period.            |
| `onBehalfOf.identifier.period.end` | The end date of the identifier's validity period.              |
| `onBehalfOf.identifier.assigner` | The entity that assigned the identifier.                      |
| `onBehalfOf.identifier.assigner.reference` | Reference to the assignee of the identifier.                 |
| `onBehalfOf.identifier.assigner.type` | The type of the assignee.                                     |
| `onBehalfOf.identifier.assigner.identifier` | The identifier of the assignee.                             |
| `onBehalfOf.identifier.assigner.display` | The display name of the assignee.                            |
| `onBehalfOf.display`           | The display name of the entity on behalf of whom the signature was made. |
| `targetFormat`                 | The format of the signed data.                                |
| `sigFormat`                    | The signature format.                                        |
| `data`                         | The data that is signed.                                     |
| `targetObjectId`               | The target object's unique identifier for association.        |
| `FHIRPath`                     | The FHIRPath expression associated with the Signature.         |

# createSimpleQuantity Operation

The `createSimpleQuantity` operation is used to create a SimpleQuantity datatype and include it to a given FHIR path of constructed FHIR resource(s). SimpleQuantity is a data type used to represent a quantity with a value and unit, often used in clinical settings.

## Properties

| Property Name   | Property Description                              |
|-----------------|--------------------------------------------------|
| `objectId`      | The unique identifier for the target object.    |
| `id`            | The identifier for the SimpleQuantity resource. |
| `extension`     | Extensions to the SimpleQuantity resource.      |
| `value`         | The numeric value of the quantity.              |
| `unit`          | The unit of measurement for the quantity.       |
| `system`        | The system that defines the units of measurement.|
| `code`          | The code that represents the unit of measurement.|
| `targetObjectId`| The target object's unique identifier for association.|
| `FHIRPath`      | The FHIRPath expression associated with the SimpleQuantity.|

# createTiming Operation

The `createTiming` operation is used to create a FHIR Timing datatype and include it to a given FHIR path of constructed FHIR resource(s). Timing is a complex data type used to define time periods for repeating events or schedules.

## Properties

| Property Name                            | Property Description                                             |
|------------------------------------------|-----------------------------------------------------------------|
| `objectId`                               | The unique identifier for the target object.                   |
| `id`                                     | The identifier for the Timing resource.                        |
| `extension`                              | Extensions to the Timing resource.                             |
| `event`                                  | The specific times or events.                                  |
| `repeat`                                 | Defines the repeat pattern for the Timing.                     |
| `repeat.boundsDuration`                  | Duration bounds for the repeat pattern.                        |
| `repeat.boundsDuration.value`            | Value of the duration.                                         |
| `repeat.boundsDuration.comparator`        | Comparator for the duration value.                              |
| `repeat.boundsDuration.unit`              | Unit of the duration.                                          |
| `repeat.boundsDuration.system`            | System defining the duration units.                            |
| `repeat.boundsDuration.code`              | Code representing the duration unit.                           |
| `repeat.boundsRange`                     | Range bounds for the repeat pattern.                           |
| `repeat.boundsRange.low`                 | Low value of the range.                                        |
| `repeat.boundsRange.low.value`           | Value of the low range bound.                                  |
| `repeat.boundsRange.low.unit`            | Unit of the low range bound.                                   |
| `repeat.boundsRange.low.system`          | System defining the unit for the low range bound.              |
| `repeat.boundsRange.low.code`            | Code representing the unit for the low range bound.            |
| `repeat.boundsRange.high`                | High value of the range.                                       |
| `repeat.boundsRange.high.value`          | Value of the high range bound.                                 |
| `repeat.boundsRange.high.unit`           | Unit of the high range bound.                                  |
| `repeat.boundsRange.high.system`         | System defining the unit for the high range bound.             |
| `repeat.boundsRange.high.code`           | Code representing the unit for the high range bound.           |
| `repeat.boundsPeriod`                    | Period for the repeat pattern.                                 |
| `repeat.boundsPeriod.start`              | Start date and time of the period.                             |
| `repeat.boundsPeriod.end`                | End date and time of the period.                               |
| `repeat.count`                           | Number of repetitions.                                         |
| `repeat.countMax`                        | Maximum number of repetitions.                                 |
| `repeat.duration`                        | Duration of each repetition.                                   |
| `repeat.durationMax`                     | Maximum duration for each repetition.                          |
| `repeat.durationUnit`                    | Unit of the duration.                                          |
| `repeat.frequency`                       | Frequency of repetition.                                      |
| `repeat.frequencyMax`                    | Maximum frequency of repetition.                               |
| `repeat.period`                          | Period of repetition.                                         |
| `repeat.periodMax`                       | Maximum period for repetition.                                 |
| `repeat.periodUnit`                      | Unit of the period.                                            |
| `repeat.dayOfWeek`                       | Days of the week for the repetition.                           |
| `repeat.timeOfDay`                       | Times of the day for the repetition.                           |
| `repeat.when`                            | When the repetition occurs.                                    |
| `repeat.offset`                          | Offset from the start time.                                   |
| `code`                                   | Code representing the Timing resource.                         |
| `code.coding.system`                     | Coding system for the code.                                    |
| `code.coding.version`                    | Version of the coding system.                                  |
| `code.coding.code`                       | Code within the coding system.                                 |
| `code.coding.display`                    | Display text for the code.                                     |
| `code.coding.userSelected`               | Indicates if the code was user-selected.                       |
| `code.text`                              | Text representation of the code.                               |
| `targetObjectId`                         | The target object's unique identifier for association.         |
| `FHIRPath`                               | The FHIRPath expression associated with the Timing resource.    |


# createUsageContext Operation

The `createUsageContext` operation is used to create a FHIR UsageContext datatype and include it to a given FHIR path of constructed FHIR resource(s). UsageContext is used to specify contextual information that describes the applicability of a resource or profile.

## Properties

| Property Name                              | Property Description                                             |
|--------------------------------------------|-----------------------------------------------------------------|
| `objectId`                                 | The unique identifier for the target object.                   |
| `id`                                       | The identifier for the UsageContext resource.                   |
| `extension`                                | Extensions to the UsageContext resource.                       |
| `code`                                     | Code specifying the context type.                               |
| `valueCodeableConcept`                     | Value represented as a CodeableConcept.                         |
| `valueCodeableConcept.code`                | Code within the CodeableConcept.                                |
| `valueCodeableConcept.valueSet`            | Value set for the CodeableConcept.                              |
| `valueCodeableConcept.coding.system`        | Coding system for the CodeableConcept code.                     |
| `valueCodeableConcept.coding.version`      | Version of the coding system.                                   |
| `valueCodeableConcept.coding.code`         | Code within the coding system.                                  |
| `valueCodeableConcept.coding.display`      | Display text for the CodeableConcept code.                      |
| `valueCodeableConcept.coding.userSelected` | Indicates if the code was user-selected.                        |
| `valueCodeableConcept.text`                | Text representation of the CodeableConcept.                     |
| `valueQuantity`                           | Value represented as a Quantity.                                |
| `valueQuantity.value`                     | Value of the Quantity.                                          |
| `valueQuantity.comparator`                | Comparator for the Quantity value.                              |
| `valueQuantity.unit`                     | Unit of the Quantity.                                           |
| `valueQuantity.system`                   | System defining the unit of the Quantity.                       |
| `valueQuantity.code`                     | Code representing the unit of the Quantity.                     |
| `valueRange`                              | Value represented as a Range.                                   |
| `valueRange.low`                          | Low bound of the Range.                                        |
| `valueRange.low.value`                    | Value of the low bound.                                        |
| `valueRange.low.unit`                     | Unit of the low bound.                                         |
| `valueRange.low.system`                   | System defining the unit for the low bound.                     |
| `valueRange.low.code`                     | Code representing the unit for the low bound.                   |
| `valueRange.high`                         | High bound of the Range.                                       |
| `valueRange.high.value`                   | Value of the high bound.                                        |
| `valueRange.high.unit`                    | Unit of the high bound.                                         |
| `valueRange.high.system`                  | System defining the unit for the high bound.                    |
| `valueRange.high.code`                    | Code representing the unit for the high bound.                  |
| `valueReference`                          | Value represented as a Reference.                               |
| `valueReference.reference`                | Reference URI or identifier.                                    |
| `valueReference.type`                     | Type of the referenced resource.                                |
| `valueReference.identifier`               | Identifier for the referenced resource.                         |
| `valueReference.identifier.use`           | Use of the identifier.                                          |
| `valueReference.identifier.type`          | Type of the identifier.                                         |
| `valueReference.identifier.system`        | System defining the identifier.                                 |
| `valueReference.identifier.value`         | Value of the identifier.                                        |
| `valueReference.identifier.period.start`  | Start date and time of the identifier period.                   |
| `valueReference.identifier.period.end`    | End date and time of the identifier period.                     |
| `valueReference.identifier.assigner.reference` | Reference to the identifier assigner.                     |
| `valueReference.identifier.assigner.display` | Display name of the identifier assigner.                      |
| `valueReference.display`                  | Display text for the Reference value.                           |
| `targetObjectId`                           | The target object's unique identifier for association.         |
| `FHIRPath`                                 | The FHIRPath expression associated with the UsageContext resource. |

# evaluateFHIRPath Operation

The `evaluateFHIRPath` operation is used to evaluate a FHIRPath expression against a target resource. It allows you to extract specific information from a FHIR resource using FHIRPath expressions ans save it to a property.

## Properties

| Property Name      | Property Description                                                                          |
|--------------------|-----------------------------------------------------------------------------------------------|
| `objectId`         | The unique identifier for the target object.                                                  |
| `FHIRPath`         | The FHIRPath expression to be evaluated.                                                      |
| `targetProperty`   | The synapse message context property name where the evaluated FHIR path value will be stored. |

# serialize Operation

The `serialize` operation is used to serialize the target FHIR object into a specified content type format. This operation is useful for getting serializable wire format of the message to the message context which can be returned to the caller. By default content will be converted to application/fhir+json.

## Properties

| Property Name      | Property Description                                                                                           |
|--------------------|----------------------------------------------------------------------------------------------------------------|
| `objectId`         | The unique identifier for the object to be serialized.                                                         |
| `targetContentType` | The desired content type format for the serialized output (e.g., application/fhir+json, application/fhir+xml). |

# setBundleIdentifier Operation

The `setBundleIdentifier` operation is used to assign or update an identifier for a FHIR Bundle created.

## Properties

| Property Name                   | Property Description                                      |
|---------------------------------|------------------------------------------------------------|
| `objectId`                      | The unique identifier for the bundle to be updated.       |
| `identifier`                    | The identifier value for the bundle.                      |
| `identifier.use`                | The use case of the identifier (e.g., official, secondary).|
| `identifier.type`               | The type of the identifier.                               |
| `identifier.type.coding.system` | The coding system for the identifier type.                |
| `identifier.type.coding.version`| The version of the coding system.                         |
| `identifier.type.coding.code`   | The code representing the identifier type.                |
| `identifier.type.coding.display`| The display name for the identifier type.                 |
| `identifier.type.coding.userSelected` | Indicates if the identifier type is user-selected.     |
| `identifier.type.text`          | The textual description of the identifier type.           |
| `identifier.system`             | The system that assigns the identifier.                   |
| `identifier.value`              | The actual value of the identifier.                       |
| `identifier.period`             | The period during which the identifier is valid.          |
| `identifier.period.start`       | The start date of the identifier's validity period.       |
| `identifier.period.end`         | The end date of the identifier's validity period.         |
| `identifier.assigner`           | The entity that assigned the identifier.                  |
| `identifier.assigner.reference` | Reference to the assigner.                                |
| `identifier.assigner.type`      | Type of the assigner.                                     |
| `identifier.assigner.identifier`| The identifier of the assigner.                           |
| `identifier.assigner.identifier.use` | Use case of the assigner's identifier.               |
| `identifier.assigner.identifier.type` | Type of the assigner's identifier.                    |
| `identifier.assigner.identifier.system` | System for the assigner's identifier.                |
| `identifier.assigner.identifier.value` | Value of the assigner's identifier.                   |
| `identifier.assigner.identifier.period` | Period for the assigner's identifier.                |
| `identifier.assigner.identifier.assigner` | Assigner of the assigner's identifier.              |
| `identifier.assigner.display`   | Display name of the assigner.                             |

# setBundleSignature Operation

The `setBundleSignature` operation is used to assign or update the signature information for a FHIR Bundle created.

## Properties

| Property Name                           | Property Description                                      |
|-----------------------------------------|------------------------------------------------------------|
| `objectId`                              | The unique identifier for the bundle to be updated.       |
| `signature`                            | The signature value for the bundle.                       |
| `signature.type.system`                | The coding system for the signature type.                 |
| `signature.type.version`               | The version of the coding system.                         |
| `signature.type.code`                  | The code representing the signature type.                 |
| `signature.type.display`               | The display name for the signature type.                  |
| `signature.type.userSelected`          | Indicates if the signature type is user-selected.         |
| `signature.when`                       | The date and time when the signature was applied.         |
| `signature.who`                        | The individual or entity who signed the bundle.           |
| `signature.who.reference`              | Reference to the signer.                                 |
| `signature.who.type`                   | Type of the signer.                                      |
| `signature.who.identifier`             | Identifier of the signer.                                |
| `signature.who.identifier.use`         | Use case of the signer's identifier.                      |
| `signature.who.identifier.type`        | Type of the signer's identifier.                         |
| `signature.who.identifier.type.coding.system` | Coding system for the signer's identifier type.      |
| `signature.who.identifier.type.coding.version` | Version of the coding system.                        |
| `signature.who.identifier.type.coding.code` | Code for the signer's identifier type.                  |
| `signature.who.identifier.type.coding.display` | Display name for the signer's identifier type.         |
| `signature.who.identifier.type.coding.userSelected` | Indicates if the identifier type is user-selected. |
| `signature.who.identifier.type.text`   | Textual description of the signer's identifier type.      |
| `signature.who.identifier.system`      | System for the signer's identifier.                       |
| `signature.who.identifier.value`       | Value of the signer's identifier.                         |
| `signature.who.identifier.period`      | Validity period for the signer's identifier.              |
| `signature.who.identifier.period.start`| Start date of the validity period.                        |
| `signature.who.identifier.period.end`  | End date of the validity period.                          |
| `signature.who.identifier.assigner`    | Assigner of the signer's identifier.                      |
| `signature.who.identifier.assigner.reference` | Reference to the assigner.                            |
| `signature.who.identifier.assigner.type` | Type of the assigner.                                    |
| `signature.who.identifier.assigner.identifier` | Identifier of the assigner.                            |
| `signature.who.identifier.assigner.display` | Display name of the assigner.                          |
| `signature.who.display`                | Display name of the signer.                               |
| `signature.onBehalfOf`                 | On behalf of whom the signature was made.                 |
| `signature.onBehalfOf.reference`       | Reference to the entity on behalf of whom the signature was made. |
| `signature.onBehalfOf.type`            | Type of the entity on behalf of whom the signature was made. |
| `signature.onBehalfOf.identifier`      | Identifier of the entity on behalf of whom the signature was made. |
| `signature.onBehalfOf.identifier.use`  | Use case of the entity's identifier.                      |
| `signature.onBehalfOf.identifier.type` | Type of the entity's identifier.                          |
| `signature.onBehalfOf.identifier.type.coding.system` | Coding system for the entity's identifier type.       |
| `signature.onBehalfOf.identifier.type.coding.version` | Version of the coding system.                         |
| `signature.onBehalfOf.identifier.type.coding.code` | Code for the entity's identifier type.                 |
| `signature.onBehalfOf.identifier.type.coding.display` | Display name for the entity's identifier type.         |
| `signature.onBehalfOf.identifier.type.coding.userSelected` | Indicates if the entity's identifier type is user-selected. |
| `signature.onBehalfOf.identifier.type.text` | Textual description of the entity's identifier type.   |
| `signature.onBehalfOf.identifier.system` | System for the entity's identifier.                     |
| `signature.onBehalfOf.identifier.value` | Value of the entity's identifier.                        |
| `signature.onBehalfOf.identifier.period` | Validity period for the entity's identifier.            |
| `signature.onBehalfOf.identifier.period.start` | Start date of the validity period.                    |
| `signature.onBehalfOf.identifier.period.end` | End date of the validity period.                      |
| `signature.onBehalfOf.identifier.assigner` | Assigner of the entity's identifier.                    |
| `signature.onBehalfOf.identifier.assigner.reference` | Reference to the assigner.                            |
| `signature.onBehalfOf.identifier.assigner.type` | Type of the assigner.                                    |
| `signature.onBehalfOf.identifier.assigner.identifier` | Identifier of the assigner.                            |
| `signature.onBehalfOf.identifier.assigner.display` | Display name of the assigner.                          |
| `signature.onBehalfOf.display`        | Display name of the entity on behalf of whom the signature was made. |
| `signature.targetFormat`             | Format of the target for the signature.                   |
| `signature.sigFormat`                | Format of the signature.                                  |
| `signature.data`                     | Data associated with the signature.                       |

# setBundleTimestamp Operation

The `setBundleTimestamp` operation is used to set or update the timestamp for a FHIR Bundle created.

## Properties

| Property Name | Property Description                                   |
|---------------|-------------------------------------------------------|
| `objectId`    | The unique identifier for the bundle to be updated.  |
| `timestamp`   | The timestamp to set for the bundle. This could be a date and time indicating when the bundle was created or last updated. |

# setBundleTotal Operation

The `setBundleTotal` operation is used to set or update the total count of entries in a FHIR bundle created.

## Properties

| Property Name | Property Description                                   |
|---------------|-------------------------------------------------------|
| `objectId`    | The unique identifier for the bundle to be updated.  |
| `total`       | The total number of entries in the bundle.           |

# setBundleType Operation

The `setBundleType` operation is used to set or update the type of a FHIR Bundle created.

## Properties

| Property Name | Property Description                        |
|---------------|--------------------------------------------|
| `objectId`    | The unique identifier for the bundle to be updated. |
| `type`        | The type of the bundle (e.g., `document`, `message`, `transaction`). |

# validate Operation

The `validate` operation is used to validate a created FHIR resource or payload against a specified FHIR profile.

## Properties

| Property Name | Property Description                               |
|---------------|---------------------------------------------------|
| `objectId`    | The unique identifier for the resource to be validated. |
| `profile`     | The FHIR profile against which the resource will be validated. |
