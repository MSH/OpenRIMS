package org.msh.pharmadex2.service.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.dto.form.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * All validators are here. Low coupling
 * @author alexk
 *
 */
@Service
public class ValidationCommon {

	private static final Logger logger = LoggerFactory.getLogger(ValidationCommon.class);

	@Autowired
	Messages messages;


	/**
	 * Validate FormFieldDTO in this and all included to this DTOs
	 * Any DTO should be AllowValidation ancestor
	 * Validate only DTO fields, but doesnt touch field valid in DTO
	 * @param <T>
	 * @param dto - any object
	 * @param screenForm - check only FormFieldDTOs with property justloaded==false, i.e. has been rendered for edit on a screen
	 * @param strict - is this check final or preliminary only?
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@SuppressWarnings("rawtypes")
	public <T extends AllowValidation> void validateDTO(T dto, boolean screenForm,  boolean strict) throws ObjectNotFoundException {
		dto.setValid(true);
		List<Field> all = FieldUtils.getAllFieldsList(dto.getClass());
		//paint all to green and validate all included
		for(Field fld : all) {
			try {
				Object obj = PropertyUtils.getProperty(dto, fld.getName());
				if(obj instanceof FormFieldDTO) {
					FormFieldDTO formFieldDTO = (FormFieldDTO) obj;
					formFieldDTO.setStrict(strict);
				}
				if(obj instanceof AllowValidation) {
					AllowValidation aobj = (AllowValidation)obj;
					validateDTO(aobj, screenForm,strict);
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new ObjectNotFoundException(e,logger);
			}
		}
		//validate all fields with the annotation
		List<Field> toValidate = FieldUtils.getFieldsListWithAnnotation(dto.getClass(), Validator.class);
		for(Field fld : toValidate) {
			Validator criteria = fld.getAnnotationsByType(Validator.class)[0];
			try {
				Object obj = PropertyUtils.getProperty(dto, fld.getName());
				if(obj instanceof FormFieldDTO) {
					FormFieldDTO formFieldDTO = (FormFieldDTO) obj;
					if((screenForm && !formFieldDTO.isJustloaded())|| !screenForm) {
						formFieldDTO = validateFormField(formFieldDTO,criteria, fld.getName());
						formFieldDTO.setStrict(strict);
					}else {
						formFieldDTO.clearValidation();
					}
				}
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				throw new ObjectNotFoundException(e,logger);
			}
		}
	}
	/**
	 * Validate a field
	 * @param dt
	 * @param criteria
	 * @param name
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	private FormFieldDTO validateFormField(FormFieldDTO formFieldDTO, Validator criteria, String name) {
		Object value = formFieldDTO.getValue();
		formFieldDTO.setSuggest("-");
		if(value instanceof OptionDTO) {
			OptionDTO ovalue= (OptionDTO) value;
			if(ovalue.getOptions().size()>0) {	//no choices, no value
				formFieldDTO.setError(!validateString(ovalue.getCode(), criteria));
				formFieldDTO.setSuggest(stringSuggest(criteria));
			}else {
				formFieldDTO.setError(false);
				formFieldDTO.setSuggest("");
			}
		}
		if(value instanceof String) {
			formFieldDTO.setError(!validateString((String) value, criteria));
			formFieldDTO.setSuggest(stringSuggest(criteria));
		}
		if(value instanceof LocalDate) {
			formFieldDTO.setError(!validateDate((LocalDate) value, criteria));
			formFieldDTO.setSuggest(dateSuggest(criteria));
		}
		if(value instanceof BigDecimal) {
			formFieldDTO.setError(!validateBigDecimal((BigDecimal)value, criteria));
			formFieldDTO.setSuggest(numericSuggest(criteria));
		}
		if(value instanceof Long) {
			BigDecimal bdVal = BigDecimal.valueOf((Long)value);
			formFieldDTO.setError(!validateBigDecimal(bdVal, criteria));
			formFieldDTO.setSuggest(numericSuggest(criteria));
		}
		if(value instanceof Integer) {
			BigDecimal bdVal = BigDecimal.valueOf((Integer)value);
			formFieldDTO.setError(!validateBigDecimal(bdVal, criteria));
			formFieldDTO.setSuggest(numericSuggest(criteria));
		}

		if(formFieldDTO.getSuggest().equals("-")) {
			String msg1=messages.get("valueisempty");
			if(value != null) {
				msg1=value.getClass().getSimpleName();
				logger.error("Validation service for "+ name +" " +messages.get("validationdataunrecognized")+": "+ msg1);
			}
			String msg = messages.get("validationdataunrecognized")+": "+ msg1;
			formFieldDTO.setError(true);
			formFieldDTO.setSuggest(msg);

		}
		return formFieldDTO;
	}

	/**
	 * Numeric suggest
	 * @param criteria
	 * @return
	 */
	private String numericSuggest(Validator criteria) {
		String ret="";
		if(criteria.above()>Integer.MIN_VALUE) {
			ret = messages.get("minnumber") + ": " + criteria.above()+". ";
		}
		if(criteria.below()<Integer.MAX_VALUE) {
			ret = ret+ messages.get("maxnumber") + ": " + criteria.below();
		}
		return ret;
	}
	/***
	 * Suggest for date
	 * @param criteria
	 * @return
	 */
	private String dateSuggest(Validator criteria) {
		String ret="";
		DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
		if(criteria.above()>Integer.MIN_VALUE) {
			LocalDate from = LocalDate.now().minusDays(criteria.above());
			ret=messages.get("fromdate") + ": " + formatter.format(from) +". ";
		}
		if(criteria.below()<Integer.MAX_VALUE) {
			LocalDate to = LocalDate.now().plusDays(criteria.below());
			ret=ret+ messages.get("todate") + ": " + formatter.format(to) + ".";
		}
		return ret;
	}
	/**
	 * Create a suggestion on the current language
	 * @param criteria
	 * @return
	 */
	private String stringSuggest(Validator criteria) {
		String format = "-";
		if(criteria.above()>0) {
			format = String.format(messages.get("atleastchars"),criteria.above());
		}
		if(criteria.below()!=Integer.MAX_VALUE) {
			format= format+String.format(messages.get("maxchars"),criteria.below());
		}
		return format;
	}
	/**
	 * Validate BigDecimal, really any numeric
	 * @param value
	 * @param validateFormField
	 * @return
	 */
	private boolean validateBigDecimal(BigDecimal value, Validator validateFormField) {
		if(value != null ) {
			BigDecimal min = BigDecimal.valueOf(validateFormField.above());
			BigDecimal max = BigDecimal.valueOf(validateFormField.below());
			return value.compareTo(min)>=0 && value.compareTo(max)<=0;
		}else {
			return false;
		}
	}
	/**
	 * Validate data
	 * @param value
	 * @param validateFormField
	 * @return
	 */
	private boolean validateDate(LocalDate value, Validator validateFormField) {
		if(value != null) {
			LocalDate min = LocalDate.now().minusDays(validateFormField.above());
			LocalDate max = LocalDate.now().plusDays(validateFormField.below());
			return (value.isAfter(min) && value.isBefore(max)) || value.isEqual(max) || value.isEqual(min);
		}else {
			return false;
		}
	}
	/**
	 * Validate a string
	 * @param code
	 * @param validateFormField
	 * @return
	 */
	private boolean validateString(String str, Validator validateFormField) {
		if(str != null) {
			boolean ret= str.length()>=validateFormField.above() && str.length()<= validateFormField.below();
			if(ret) {		//Convenient for debug
				return true;
			}else {
				return false;
			}
		}else {
			return false;
		}
	}

}
