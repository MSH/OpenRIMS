package org.msh.pharmadex2.service.common;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pharmadex2.dto.form.AllowValidation;
import org.msh.pharmadex2.dto.form.FormFieldDTO;
import org.msh.pharmadex2.dto.form.Magic;
import org.msh.pharmadex2.dto.form.OptionDTO;
import org.msh.pharmadex2.dto.form.YesNoType;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Convert DTO to Entity. Common
 * @author alexk
 *
 */
@Service
public class EntityCommon {
	private static final Logger logger = LoggerFactory.getLogger(EntityCommon.class);
	@Autowired
	LiteralService literalServ;
	@Autowired
	ClosureService closureServ;

	/**
	 * Cast DTO data to entities in accordance with Magic annotation
	 * @param dto
	 * @param entities list of entities objects to cast data in order of priority
	 * @return array of the objects
	 * @throws ObjectNotFoundException 
	 */
	@SuppressWarnings("rawtypes")
	public void magic(AllowValidation dto, Object...entities ) throws ObjectNotFoundException {
		if(entities.length>0) {
			List<Field> toCast = FieldUtils.getFieldsListWithAnnotation(dto.getClass(), Magic.class);
			for(Field fld : toCast) {
				try {
					Object obj = PropertyUtils.getProperty(dto, fld.getName());
					if(obj instanceof FormFieldDTO) {
						FormFieldDTO formFieldDTO = (FormFieldDTO) obj;
						Magic annotation = fld.getAnnotationsByType(Magic.class)[0];
						castFormFieldDTO(dto.getIdentifier(), formFieldDTO,annotation.name(), entities);
					}else {
						throw new ObjectNotFoundException("Wrong magic annotation for " +dto.getClass().getName() +"/"+fld.getName(),logger);
					}
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					throw new ObjectNotFoundException(e,logger);
				}
			}
		}else {
			throw new ObjectNotFoundException("Cannot cast DTO "+ dto.getClass().getName() + " to empty list of objects",logger);
		}
	}
	/**
	 * Cast FormFieldDTO to object's property with name
	 * @param identifier - root concept for multi-language labels
	 * @param data data field
	 * @param name name of property
	 * @param entities array of objects to cast in order of priority
	 * @throws ObjectNotFoundException 
	 */
	@SuppressWarnings("rawtypes")
	private void castFormFieldDTO(String identifier, FormFieldDTO data, String name, Object[] entities) throws ObjectNotFoundException {
		Object value = data.getValue();
		Object obj = null;
		for(Object o : entities) {
			try {
				PropertyUtils.getProperty(o, name);
				obj=o;			//this object has property with the name
				break;
			} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
				//nothing to do
			}
		}
		if(obj != null) {
			castValue(identifier, value,name,obj);
		}else {
			throw new ObjectNotFoundException("CastFormFieldDTO. Property not found. Name is "+name,logger);
		}

	}
	/**
	 * Cast a value from FormFieldDTO to the object
	 * This method is public and transactional to support literals and dictionaries
	 * Allowable types of value are:
	 * <ul>
	 * <li> Long
	 * <li> String
	 * <li> java.time.LocalDate
	 * <li> OptionDTO
	 * </ul>
	 * @param identifier TODO
	 * @param value
	 * @param name
	 * @param obj
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void castValue(String identifier, Object value, String name, Object obj) throws ObjectNotFoundException {
		try {
			@SuppressWarnings("rawtypes")
			Class clazz = PropertyUtils.getPropertyType(obj, name);
			if(clazz.getSimpleName().contains("Concept")) {
				//variable or dictionary item?
				if(value instanceof String) {
					String strValue = (String) value;
					Concept node = closureServ.loadRoot(identifier);
					node = literalServ.createUpdateLiteral(name, strValue, node);
					PropertyUtils.setProperty(obj, name, node);
					return;
				}
				if(value instanceof OptionDTO) {
					//dictionary
					//TODO
				}
			}
			if(value instanceof Long || value instanceof String || value instanceof Integer) {
				PropertyUtils.setProperty(obj, name, value);
				return;
			}
			if(value instanceof java.time.LocalDate) {
				LocalDate ldate = (LocalDate) value;
				Object dt = PropertyUtils.getProperty(obj, name);
				if(dt instanceof java.time.LocalDate) {
					PropertyUtils.setProperty(obj, name, ldate);
				}
				if(dt instanceof java.util.Date) {
					Date dat = Date.from(ldate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
					PropertyUtils.setProperty(obj, name,dat);
				}
				return;
			}
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ObjectNotFoundException(e,logger);
		}
		//complex OptionDTO type, cast depends on object's property type
		if(value instanceof OptionDTO) {
			OptionDTO opt = (OptionDTO) value;
			castOption(opt,name,obj);
			return;
		}
		throw new ObjectNotFoundException("Cannot cast value of type "+value.getClass().getName(),logger);
	}

	/**
	 * Cast value from OptionDTO to object's property
	 * @param value
	 * @param name
	 * @param obj
	 * @throws ObjectNotFoundException 
	 */
	private void castOption(OptionDTO opt, String name, Object obj) throws ObjectNotFoundException {
		try {
			Class<?> clazz = PropertyUtils.getPropertyType(obj, name);
			if(clazz.isEnum()) {
				Object[] values = clazz.getEnumConstants();
				PropertyUtils.setProperty(obj, name, optionToEnum(values,opt));
			}
			String className = clazz.getName();

			if(Integer.class.getName().equals(className) || int.class.getName().equals(className) ) {
				PropertyUtils.setProperty(obj, name,  opt.getIntId());
			}
			if(Long.class.getName().equals(className) || long.class.getName().equals(className)) {
				PropertyUtils.setProperty(obj, name, opt.getId());
			}
			if(Boolean.class.getName().equals(className) || boolean.class.getName().equals(className)) {
				YesNoType eval = optionToEnum(YesNoType.values()  ,opt);
				Boolean bval = eval ==YesNoType.YES;
				PropertyUtils.setProperty(obj, name, bval);
			}
			//to be extend...

		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new ObjectNotFoundException(e,logger);
		}

	}

	/**
	 * Convert OptionDTO to Enum
	 * example:<br>
	 * Nationality nat = optionToEnum(Nationality.values(), data.getPatient().getNationality())
	 * @param values array of all Enum's values
	 * @param opt OptionDTO to convert
	 * @return Enum value
	 */
	private <T> T optionToEnum(T[] values, OptionDTO opt) {
		if(opt != null) {
			if(values.length>=opt.getId()) {
				Long ordl = new Long(opt.getId()); 
				if(ordl>0) {
					return values[ordl.intValue()-1];
				}else {
					return null;
				}
			}else {
				return null;
			}
		}else {
			return null;
		}
	}


}
