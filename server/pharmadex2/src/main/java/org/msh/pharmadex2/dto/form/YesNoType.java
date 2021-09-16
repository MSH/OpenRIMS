package org.msh.pharmadex2.dto.form;

public enum YesNoType {
	YES ("global.yes"),
	NO ("global.no");
	
	private final String messageKey;

	YesNoType(String msg) {
		messageKey = msg;
	}
	
	public String getKey() {
		return messageKey;
	}
	
	public boolean isNo(){
		return this.ordinal() == YesNoType.NO.ordinal();
	}
	
	public boolean isYes(){
		return this.ordinal() == YesNoType.YES.ordinal();
	}
}
