package org.msh.pharmadex2.dto.form;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.slf4j.LoggerFactory;

/**
 * All DTO should extends this class to allow validation and justloaded behavior
 * In addition this class allows using of literals and dictionaries
 * @author alexk
 *
 */
public abstract class AllowValidation{
	private boolean valid=true;
	private boolean strict=true;			//do not allow save in case of not validate
	private String identifier="";	//error message

	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean _valid) {
		this.valid = _valid;
	}

	public boolean isStrict() {
		return strict;
	}
	public void setStrict(boolean strict) {
		this.strict = strict;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/** If at least one object is invalid and/or FormFieldDTO is an error - propagate this state to the whole chain
	 * @param data
	 * @throws ObjectNotFoundException 
	 */
	public void propagateValidation() throws ObjectNotFoundException {
		List<Field> all = FieldUtils.getAllFieldsList(this.getClass());
		boolean valid = this.valid;
		for(Field fld :all) {
			try {
				if(!Modifier.isStatic(fld.getModifiers())) {
					Object obj = PropertyUtils.getProperty(this, fld.getName());
					if(obj instanceof AllowValidation) {
						AllowValidation oval = (AllowValidation) obj;
						oval.propagateValidation();
						valid = valid && oval.isValid();
					}
					if(obj instanceof Iterable) {
						Iterable iObj = (Iterable) obj;
						for(Object o : iObj) {
							if(o instanceof AllowValidation) {
								AllowValidation av = (AllowValidation) o;
								av.propagateValidation();
								valid= valid && av.isValid();
							}
							if(obj instanceof FieldSuggest) {
								FieldSuggest ofield = (FieldSuggest)o;
								valid=valid&&(!ofield.isError());
							}
						}
					}
					if(obj instanceof Map) {
						Map map=(Map)obj;
						for(Object key :map.keySet()) {
							if(map.get(key) instanceof AllowValidation) {
								AllowValidation av = (AllowValidation) map.get(key);
								av.propagateValidation();
								valid= valid && av.isValid();
							}
							if(map.get(key) instanceof FieldSuggest) {
								FieldSuggest ofield = (FieldSuggest)map.get(key);
								valid=valid&&(!ofield.isError());
							}
						}
					}
					if(obj instanceof FieldSuggest) {
						FieldSuggest ofield = (FieldSuggest)obj;
						valid=valid&&(!ofield.isError());
					}
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new ObjectNotFoundException(e, LoggerFactory.getLogger(AllowValidation.class));
			}
		}
		this.setValid(valid);
	}
	/**
	 * Clear all errors in this and included objects
	 * @throws ObjectNotFoundException 
	 */
	public void clearErrors() throws ObjectNotFoundException {
		this.makeValid();
		List<Field> all = FieldUtils.getAllFieldsList(this.getClass());
		for(Field fld :all) {
			try {
				if(!Modifier.isStatic(fld.getModifiers())) {
					Object obj = PropertyUtils.getProperty(this, fld.getName());
					if(obj instanceof AllowValidation) {
						AllowValidation aobj = (AllowValidation)obj;
						aobj.setValid(true);
						aobj.clearErrors();
					}
					
					if(obj instanceof Iterable) {
						Iterable iObj = (Iterable) obj;
						for(Object o : iObj) {
							if(o instanceof AllowValidation) {
								AllowValidation av = (AllowValidation) o;
								av.clearErrors();
							}
							if(obj instanceof FieldSuggest) {
								FieldSuggest ofield = (FieldSuggest)o;
								ofield.setError(false);
								ofield.setSuggest("");
							}
						}
					}
					if(obj instanceof Map) {
						Map map=(Map)obj;
						for(Object key :map.keySet()) {
							if(map.get(key) instanceof AllowValidation) {
								AllowValidation av = (AllowValidation) map.get(key);
								av.clearErrors();
							}
							if(map.get(key) instanceof FieldSuggest) {
								FieldSuggest ofield = (FieldSuggest)map.get(key);
								ofield.clearValidation();
							}
						}
					}
					
					if(obj instanceof FieldSuggest) {
						FieldSuggest fobj = (FieldSuggest)obj;
						fobj.clearValidation();
					}
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new ObjectNotFoundException(e, LoggerFactory.getLogger(AllowValidation.class));
			}
		}
	}
	/**
	 * Make single allow validation valid again
	 */
	private void makeValid() {
		setValid(true);
		setIdentifier("");
	}
	
	/**
	 * Mark DTO as invalid and add an error message to it
	 * @param mess
	 */
	public void addError(String mess) {
		setValid(false);
		String err= getIdentifier();
		if(err==null) {
			setIdentifier(mess);
		}
		if(err.length()==0) {
			setIdentifier(mess);
		}else {
			setIdentifier(getIdentifier()+", "+mess);
		}
		
	}
}

