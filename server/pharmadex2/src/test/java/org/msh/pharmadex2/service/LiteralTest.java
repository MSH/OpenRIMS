package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.i18n.Messages;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pharmadex2.Pharmadex2Application;
import org.msh.pharmadex2.service.r2.ClosureService;
import org.msh.pharmadex2.service.r2.LiteralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Literal is the special node with identifier _LITERALS_
 * The literal is sub-node of some other node, i.e. dictionary item  or name space and intends to keep all string variables 
 * This node should contains children nodes - variables with variable names as identifiers, i.e., prefName, description, etc
 * In own turn a variable contains children nodes - languages  with the locale names.i.e.,  en_US, ru_RU. These nodes provides real
 * values of variables in a field "label" 
 * The responsible for literals is LiteralService class
 * 
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class LiteralTest {
	@Autowired
	LiteralService literalServ;
	@Autowired
	ClosureService closureServ;
	@Autowired
	Messages messages;
	/**
	 * CRUD for literals
	 * @throws ObjectNotFoundException 
	 */
	@Test
	public void literalTests() throws ObjectNotFoundException {
		//remove the parent node for literals as well as all test literals
		Concept root = closureServ.loadRoot("test.literal.root");
		closureServ.removeNode(root);

		//how many languages?
		int langs = messages.getAllUsed().size();
		//create a parent node for literals
		root = closureServ.loadRoot("test.literal.root");
		
		//create a literal on the current language
		root = literalServ.createUpdateLiteral("name","kaban",root);
		List<Concept> literals =closureServ.loadLevel(root);
		assertEquals(1, literals.size());															//should be only one concept "_LITERALS_"
		List<Concept> variables = closureServ.loadLevel(literals.get(0));
		assertEquals(1, variables.size());														//should be only one variable "name"	
		List<Concept> values = closureServ.loadLevel(variables.get(0));
		assertEquals(langs, values.size());												//should be variables on all languages
		//read a literal value on the current language
		String value = literalServ.readValue("name",root);
		assertEquals("kaban", value);
		//change a value of a literal
		root = literalServ.createUpdateLiteral("name","svinka",root);
		value = literalServ.readValue("name",root);
		assertEquals("svinka", value);
		//add the common used variables "prefLabel" and "description"
		root=literalServ.prefAndDescription("irka", "svinka", root);
		value = literalServ.readValue("prefLabel",root);
		assertEquals("irka", value);
		//remove a literal variable
		root=literalServ.removeVariable("name",root);
		value = literalServ.readValue("name",root);
		assertEquals("", value);
		
		//remove the parent node for literals as well as all test literals
		closureServ.removeNode(root);

	}

}
