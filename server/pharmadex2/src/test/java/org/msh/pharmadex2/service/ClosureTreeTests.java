package org.msh.pharmadex2.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.services.r2.ClosureService;
import org.msh.pharmadex2.Pharmadex2Application;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

/**
 * Test main features of closure service:
 * <ul>
 * <li>create a new tree 
 * <li> add a node to the existed tree
 * <li> read a level of the tree
 * <li> remove a node from the existed tree
 * <li> remove a whole tree
 * </ul>
 * BTW we do not need "insert a node" operation.
 * @author alexk
 *
 */
@SpringBootTest(classes=Pharmadex2Application.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public class ClosureTreeTests {
	@Autowired
	ClosureService closureServ;

	@Test
	public void createTree() throws ObjectNotFoundException {
		//create a tree
		Concept root = new Concept();
		root.setIdentifier("test");
		root = closureServ.saveToTree(null, root);
		assertTrue(root.getID()>0);
		long rootId = root.getID();

		//try to create a tree with the same identifier
		Concept root1 = new Concept();
		root1.setIdentifier("test");
		root1 = closureServ.saveToTree(null, root);
		assertEquals(rootId, root1.getID());

		//add two nodes to a root
		Concept node = new Concept();
		node.setIdentifier("Springfield");
		node = closureServ.saveToTree(root, node);
		Concept node2 = new Concept();
		node2.setIdentifier("Melford");
		node2 = closureServ.saveToTree(root, node2);

		//try to add a node with the same identifier
		long nodeID = node2.getID();
		Concept node3 = new Concept();
		node3.setIdentifier("Melford");
		node3 = closureServ.saveToTree(root, node3);
		assertEquals(nodeID, node3.getID());

		//add third level with 3 nodes
		Concept node31 = new Concept();
		node31.setIdentifier("name");
		node31 = closureServ.saveToTree(node3, node31);
		Concept node32 = new Concept();
		node32.setIdentifier("street");
		node32 = closureServ.saveToTree(node3, node32);
		Concept node321= new Concept();
		node321.setIdentifier("Elm");
		node321 = closureServ.saveToTree(node32, node321);
		Concept node33 = new Concept();
		node33.setIdentifier("phone");
		node33 = closureServ.saveToTree(node3, node33);

		//read a level
		List<Concept> level = closureServ.loadLevel(node3);
		assertEquals(3, level.size());

		//get a url by the node
		String url = closureServ.getUrlByNode(node33);
		assertEquals("test", url);

		//remove a leaf
		closureServ.removeNode(node33);
		level=closureServ.loadLevel(node3);
		assertEquals(2, level.size());

		//remove a node
		closureServ.removeNode(node3);
		level=closureServ.loadLevel(root);
		assertEquals(1, level.size());

		//remove a tree
		closureServ.removeNode(root);

	}

	//@Test
	public void parents() throws ObjectNotFoundException {
		Concept node = closureServ.loadConceptById(3606);
		List<Concept> parents = closureServ.loadParents(node);
		System.out.println(parents.size());
	}

	//@Test
	/**
	 * Clone person concept
	 * @throws ObjectNotFoundException
	 */
	public void cloneTree() throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot("test.clone");
		Concept pers = closureServ.loadConceptById(78218);
		Concept clone = closureServ.cloneTree(root,pers);
	}
	
	@Test
	public void findNodeByIdentifier() throws ObjectNotFoundException {
		Concept root = closureServ.loadRoot("dictionary.system.roles");
		Concept node = closureServ.findConceptInBranchByIdentifier(root,"ROLE_ADMIN");
		assertTrue(node.getID()>0);
		node = closureServ.findConceptInBranchByIdentifier(root,"ROLE_ADMINO");
		assertTrue(node.getID()==0);
	}
	
	@Test
	public void saveToTreeFast() throws ObjectNotFoundException {
		Concept root = new Concept();
		root.setIdentifier("test");
		root = closureServ.saveToTreeFast(null, root);
		assertTrue(root.getID()>0);
		long rootId = root.getID();

		//try to create a tree with the same identifier
		Concept root1 = new Concept();
		root1.setIdentifier("test");
		root1 = closureServ.saveToTreeFast(null, root);
		assertEquals(rootId, root1.getID());

		//add two nodes to a root
		Concept node = new Concept();
		node.setIdentifier("Springfield");
		node = closureServ.saveToTreeFast(root, node);
		Concept node2 = new Concept();
		node2.setIdentifier("Melford");
		node2 = closureServ.saveToTree(root, node2);

		//try to add a node with the same identifier
		long nodeID = node2.getID();
		Concept node3 = new Concept();
		node3.setIdentifier("Melford");
		node3 = closureServ.saveToTreeFast(root, node3);
		assertEquals(nodeID, node3.getID());

		//add third level with 3 nodes
		Concept node31 = new Concept();
		node31.setIdentifier("name");
		node31 = closureServ.saveToTreeFast(node3, node31);
		Concept node32 = new Concept();
		node32.setIdentifier("street");
		node32 = closureServ.saveToTreeFast(node3, node32);
		Concept node321= new Concept();
		node321.setIdentifier("Elm");
		node321 = closureServ.saveToTreeFast(node32, node321);
		Concept node33 = new Concept();
		node33.setIdentifier("phone");
		node33 = closureServ.saveToTreeFast(node3, node33);

		//read a level
		List<Concept> level = closureServ.loadLevel(node3);
		assertEquals(3, level.size());

		//get a url by the node
		String url = closureServ.getUrlByNode(node33);
		assertEquals("test", url);

		//remove a leaf
		closureServ.removeNode(node33);
		level=closureServ.loadLevel(node3);
		assertEquals(2, level.size());

		//remove a node
		closureServ.removeNode(node3);
		level=closureServ.loadLevel(root);
		assertEquals(1, level.size());

		//remove a tree
		closureServ.removeNode(root);
	}
	
}
