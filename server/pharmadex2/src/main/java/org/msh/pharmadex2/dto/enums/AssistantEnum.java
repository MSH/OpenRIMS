package org.msh.pharmadex2.dto.enums;

/**
 * Assistance that is required by FormFieldDTO definition as well as by AssemblyDTO
 * @author alexk
 *
 */
public enum AssistantEnum {
	NO,											// no assistance
	URL_ANY,								// Any URL, right syntax
	URL_NEW,								//URL should suit the syntax, however does not exist
	URL_DICTIONARY_NEW,			// URL that is suit dictionary URL syntax, however does not exist yet
	URL_DICTIONARY_ALL,			//Any existing dictionary
	URL_APPLICATION_ALL,			//Any existing or not existing URL for applications
	URL_DATA_ANY,						//Any existing or not existing data configurations
	URL_DATA_NEW,						//data URL should not be existing
	URL_RESOURCE_NEW,				//A file resource should be new new
	URL_ACTIVITY,							//existing or new activity URL
	URL_HOST								//HOST Applications for concurrent run
}
