package org.msh.pharmadex2.service.common;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import org.msh.pdex2.dto.table.Headers;
import org.msh.pdex2.dto.table.TableCell;
import org.msh.pdex2.dto.table.TableRow;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Assembly;
import org.msh.pdex2.model.r2.Checklistr2;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.dto.DataVariableDTO;
import org.msh.pharmadex2.dto.DictNodeDTO;
import org.msh.pharmadex2.dto.QuestionDTO;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * DTO to entity conversion
 * @author alexk
 *
 */
@Service
public class EntityService {
	private static final Logger logger = LoggerFactory.getLogger(EntityService.class);
	@Autowired
	private ClosureService closureServ;
	@Autowired
	private LiteralService literalServ;
	@Autowired
	private BoilerService boilerServ;
	/**
	 * Create concept from node
	 * @param nodeDTO
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept node(DictNodeDTO nodeDTO) throws ObjectNotFoundException {
		Concept ret = new Concept();
		if(nodeDTO.getNodeId()>0) {
			ret = closureServ.loadConceptById(nodeDTO.getNodeId());
		}else {
			ret=closureServ.save(ret);
			ret.setIdentifier(ret.getID()+"");
			if(nodeDTO.getParentId()>0) {
				Concept parent = closureServ.loadConceptById(nodeDTO.getParentId());
				ret = closureServ.saveToTree(parent, ret);
			}else {
				if(nodeDTO.getUrl().length()>0) {
					Concept root = closureServ.loadRoot(nodeDTO.getUrl());
					ret=closureServ.saveToTree(root, ret);
				}else {
					throw new ObjectNotFoundException("Can't convert empty DictNodeDTO to concept",logger);
				}
			}
		}
		for(String key : nodeDTO.getLiterals().keySet()) {
			literalServ.createUpdateLiteral(key, nodeDTO.getLiterals().get(key).getValue(), ret);
		}
		return ret;
	}
	/**
	 * Checklistr2 record from dto
	 * @param qdto
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Checklistr2 checklist(QuestionDTO qdto, Concept activity, Concept applicationData) throws ObjectNotFoundException {
		Checklistr2 ret = new Checklistr2();
		ret.setActivity(activity);
		ret.setAnswer(qdto.getAnswer());
		ret.setApplicationData(applicationData);
		ret.setComment(qdto.getComment().getValue());
		Concept dictItem = closureServ.loadConceptById(qdto.getDictId());
		ret.setDictItem(dictItem);
		ret.setQuestion(qdto.getQuestion());
		return ret;
	}
	/**
	 * Create and assembly record from DTO
	 * data assumes as validated
	 * @param data
	 * @param assm
	 * @return
	 */
	@Transactional
	public Assembly assembly(DataVariableDTO data, Concept node, Assembly assm) {
		assm.setPublicavailable(data.getPublicavailable().getValue().getId()==1);
		assm.setHidefromapplicant(data.getHidefromapplicant().getValue().getId()==1);
		assm.setClazz(data.getClazz().getValue().getCode());
		assm.setCol(data.getCol().getValue().intValue());
		assm.setDictUrl(data.getDictUrl().getValue());
		assm.setFileTypes(data.getFileTypes().getValue());
		assm.setMax(data.getMaxLen().getValue().intValue());
		assm.setMin(data.getMinLen().getValue().intValue());
		assm.setMult(data.getMult().getValue().getId()==1);
		assm.setUnique(data.getUnique().getValue().getId()==1);
		assm.setPrefLabel(data.getPrefLabel().getValue().getId()==1);
		assm.setOrd(data.getOrd().getValue().intValue());
		assm.setPropertyName(node);
		assm.setReadOnly(data.getReadOnly().getValue().getId()==1);
		assm.setRequired(data.getRequired().getValue().getId()==1);
		assm.setRow(data.getRow().getValue().intValue());
		assm.setUrl(data.getUrl().getValue());
		assm.setAuxDataUrl(data.getAuxUrl().getValue());
		return assm;
	}
	/**
	 * Render any object from a TableQtb row
	 * Typically for entities
	 * @param row
	 * @param obj
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	public Object renderFromQtbTable(TableRow row, Object obj) throws ObjectNotFoundException {
		Field[] flds = obj.getClass().getDeclaredFields();
		for(TableCell cell : row.getRow()) {
			Field fld=searchForFiled(flds, cell.getKey());
			if(fld!=null) {
				fld = setFieldValue(obj, fld,cell.getOriginalValue());
			}
		}
		return obj;
	}

	/**
	 * Set field value to the object given
	 * We presume that objVal is from known types of TableQtb cells
	 * @param object to set a value
	 * @param fld field in the object to set a value
	 * @param objVal - value to set 
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	Field setFieldValue(Object object, Field fld, Object objVal) throws ObjectNotFoundException {
		fld.setAccessible(true);
		try {
			//integers
			if(fld.getType().getName().equalsIgnoreCase("int")) {
				if(objVal instanceof Long){
					Long lval = (Long) objVal;
					fld.setInt(object,lval.intValue());
					return fld;
				}else {
					throw new IllegalArgumentException();
				}
			}
			if(fld.getType().getName().equalsIgnoreCase("long")) {
				if(objVal instanceof Long){
					Long lval = (Long) objVal;
					fld.setLong(object,lval.longValue());
					return fld;
				}else {
					throw new IllegalArgumentException();
				}
			}
			if(fld.getType().getName().equalsIgnoreCase("short")) {
				if(objVal instanceof Long){
					Long lval = (Long) objVal;
					fld.setShort(object,lval.shortValue());
					return fld;
				}else {
					throw new IllegalArgumentException();
				}
			}
			if(fld.getType().getName().equalsIgnoreCase("byte")) {
				if(objVal instanceof Long){
					Long lval = (Long) objVal;
					fld.setByte(object,lval.byteValue());
					return fld;
				}else {
					throw new IllegalArgumentException();
				}
			}
			// floating are near impossible
			if(fld.getType().getName().equalsIgnoreCase("float")) {
				if(objVal instanceof BigDecimal){
					BigDecimal lval = (BigDecimal) objVal;
					fld.setFloat(object,lval.floatValue());
					return fld;
				}else {
					throw new IllegalArgumentException();
				}
			}
			if(fld.getType().getName().equalsIgnoreCase("double")) {
				if(objVal instanceof BigDecimal){
					BigDecimal lval = (BigDecimal) objVal;
					fld.setDouble(object,lval.doubleValue());
					return fld;
				}else {
					throw new IllegalArgumentException();
				}
			}
			//booleans
			if(fld.getType().getName().equalsIgnoreCase("boolean")) {
				if(objVal instanceof Boolean){
					Boolean lval = (Boolean) objVal;
					fld.setBoolean(object,lval.booleanValue());
					return fld;
				}else {
					throw new IllegalArgumentException();
				}
			}
			//char is impossible in this particular case
			
			//date and date time
			if(fld.getType().getName().equalsIgnoreCase("Date")) {
				if(objVal instanceof LocalDate){
					LocalDate lval= (LocalDate) objVal;
					Date dt = boilerServ.localDateToDate(lval);
					fld.set(object,dt);
					return fld;
				}else {
					if(objVal instanceof LocalDateTime) {
						LocalDateTime lval = (LocalDateTime) objVal;
						Date dt = boilerServ.localDateTimeToDate(lval);
						fld.set(object,dt);
						return fld;
					}else {
						throw new IllegalArgumentException();
					}
				}
			}
			//objects
			fld.set(object,objVal);
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			throw new ObjectNotFoundException("setFieldValue. Inappropriative field type "+ fld.getName() +
					"/"+objVal.getClass().getSimpleName(), logger);
		}
		return fld;
	}

	/**
	 * Search for a field with a name given
	 * Case insensitive
	 * @param flds
	 * @param name
	 * @return null if not found
	 */
	private Field searchForFiled(Field[] flds, String name) {
		for(Field fld : flds) {
			if(fld.getName().equalsIgnoreCase(name)) {
				return fld;
			}
		}
		return null;
	}



}
