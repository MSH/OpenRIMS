package org.msh.pharmadex2.dto.form;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Validate a FormFieldDTO object
 * <ul>
 * <li> <b>above=</b> For numeric value - above or equal some number, for DateTime - days after current date, for string - minimal length. 
 * <li> <b>below=</b> For numeric value - below or equal some number, for DateTime - days before current date, for string - minimal length
 * </ul>
 * @author alexk
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Validator {
	public int below() default Integer.MAX_VALUE;
	public int above() default Integer.MIN_VALUE;

}
