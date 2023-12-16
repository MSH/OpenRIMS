package org.msh.pharmadex2.dto.enums;

/**
 * Assistance that is required by FormFieldDTO definition as well as by AssemblyDTO
 * @author alexk
 *
 */
public enum AssistantEnum {
	NO,											// no assistance
	URL_ANY,								// Any existing or not existing URL that is suit URL syntax
	URL_NEW,								//URL should suit the syntax, however does not exist
	URL_DICTIONARY_NEW,			// URL that is suit dictionary URL syntax, however does not exist yet
	URL_DICTIONARY_ALL,			//Any existing or not existing dictionary
	URL_APPLICATION_NEW,			//An application should be new
	URL_APPLICATION_ALL,			//Any existing or not existing URL for applications
	URL_DATA_ANY,						//Any existing or not existing data configurations
	URL_DATA_NEW,						//data URL should not be existing
	URL_RESOURCE_NEW,				//A file resource should be new new
	URL_RESOURCE_ALL				//A file resource should be existing 
}
