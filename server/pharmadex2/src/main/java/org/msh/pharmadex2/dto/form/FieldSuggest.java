package org.msh.pharmadex2.dto.form;

/**
 * Possible error data for FormFieldDTO
 * @author alexk
 *
 */
public abstract class FieldSuggest {

	private boolean justloaded=true;	//if false, then this field was rendered on the screen just before data were sent to the server
															//works in coexistence with parameter onlyVisible in ValidatorService validateDTO
	private String suggest="";
	private boolean error=false; //suggest is suggestion or error
	private boolean strict=false; //final of preliminary error?

	public boolean isJustloaded() {
		return justloaded;
	}
	public void setJustloaded(boolean justloaded) {
		this.justloaded = justloaded;
	}
	/**
	 * Suggest for a value for a field, like "from 100 to 200"
	 * @return
	 */
	public String getSuggest() {
		return suggest;
	}
	/**
	 * Suggest for a value for a field, like "from 100 to 200"
	 * @return
	 */
	public void setSuggest(String suggest) {
		this.suggest = suggest;
	}
	/**
	 * This field is invalid
	 * @return
	 */
	public boolean isError() {
		return error;
	}
	/**
	 * This field is invalid or valid
	 * @return
	 */
	public void setError(boolean error) {
		this.error = error;
	}
	/**
	 * This check is strict or for suggest only. Strict paint fields border and subject to red or green
	 * @return
	 */
	public boolean isStrict() {
		return strict;
	}
	/**
	 * This check is strict or for suggest only. Strict paint fields border and subject to red or green
	 * @return
	 */
	public void setStrict(boolean strict) {
		this.strict = strict;
	}

	
	/**
	 * Make this field valid again
	 */
	public void clearValidation() {
		setError(false);
		setSuggest("");
		setStrict(false);
		setJustloaded(true);
	}
	/**
	 * Invalidate this filed
	 * @param message
	 */
	public void invalidate(String message) {
		this.setError(true);
		this.setSuggest(message);
		this.setStrict(true);
	}
}
