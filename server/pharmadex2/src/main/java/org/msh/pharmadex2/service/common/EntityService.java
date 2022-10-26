package org.msh.pharmadex2.service.common;

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
	


}
