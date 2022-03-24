package org.msh.pdex2.services.r2;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.msh.pdex2.exception.ObjectNotFoundException;
import org.msh.pdex2.model.r2.Closure;
import org.msh.pdex2.model.r2.Concept;
import org.msh.pdex2.model.r2.ThingPerson;
import org.msh.pdex2.repository.common.JdbcRepository;
import org.msh.pdex2.repository.r2.ClosureRepo;
import org.msh.pdex2.repository.r2.ConceptRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
/**
 * Service to manipulate data trees built on Closure template
 * @author alexk
 *
 */
@Service
@Transactional
public class ClosureService {
	private static final Logger logger = LoggerFactory.getLogger(ClosureService.class);
	@Autowired
	private ClosureRepo closureRepo;
	@Autowired
	private ConceptRepo conceptRepo;
	@PersistenceContext
	EntityManager entityManager;
	@Autowired
	JdbcRepository jdbcRepo;

	/**
	 * Save a concept to a tree
	 * Create a new tree if needed. Concept identifiers on any level as well as tree roots should be unique
	 * @param parent parent node
	 * @param node child node
	 * @return saved child node
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept saveToTree(Concept parent, Concept node) throws ObjectNotFoundException {
		if(node != null && node.getIdentifier()==null) {
			throw new ObjectNotFoundException("saveToTree. Identifier is null",logger);
		}
		if(node != null && node.getIdentifier().length()>0) {
			if(parent==null) { 									//search for tree root with the same identifier
				List<Concept> concepts = conceptRepo.findByIdentifierIgnoreCase(node.getIdentifier());
				for(Concept conc: concepts) {
					List<Closure> list = closureRepo.findByChild(conc);
					//if(conc.getParents().size()==1) {
					if(list.size() == 1) {
						if(node.getLabel()!=null) {
							conc.setLabel(node.getLabel().trim());
						}else {
							if(conc.getLabel()==null) {
								conc.setLabel("");
							}
						}
						conc= conceptRepo.save(conc);
						try {
							entityManager.flush();
						} catch (Exception e) {
							// nothing to do
						}
						if(parent != null) {
							entityManager.refresh(parent);	//to ensure new children
						}
						return conc;								//we will return the root of the tree with the same identifier
					}
				}
			}
			List<Closure> toInsert=new ArrayList<Closure>();
			//closure to itself
			Closure closure = new Closure();
			closure.setChild(node);
			closure.setParent(node);
			closure.setLevel(0);
			toInsert.add(closure);
			if(parent != null) {
				parent = loadConceptById(parent.getID());

				List<Closure> childs = closureRepo.findByParentAndLevel(parent, 1);
				//for(Closure child :parent.getChilds()) {			//check unique identifier on a level 
				for(Closure child :childs) {
					//if(child.getLevel()==1) {								// right below the parent
					Concept oldNode = child.getChild();
					if(oldNode.getIdentifier().equalsIgnoreCase(node.getIdentifier())) {
						if(node.getLabel()!=null) {
							oldNode.setLabel(node.getLabel().trim());
						}
						oldNode.setActive(node.getActive());
						oldNode = conceptRepo.save(oldNode);
						try {
							entityManager.flush();
						} catch (Exception e) {
							// nothing to do
						}
						if(parent != null) {
							entityManager.refresh(parent);	//to ensure new children
						}
						return oldNode;									//we will return the node with the same identifier
					}
					//}
				}

				//closures to all parents of the parent and parent itself
				List<Closure> allParents = closureRepo.findByChild(parent);
				for(Closure clos : allParents) {
					Closure closure1 = new Closure();
					closure1.setChild(node);
					closure1.setParent(clos.getParent());
					closure1.setLevel(clos.getLevel()+1);
					toInsert.add(closure1);
				}
			}
			closureRepo.saveAll(toInsert);
			node = conceptRepo.save(node);
			entityManager.flush();
			if(parent != null) {
				entityManager.refresh(parent);	//to ensure new children
			}
			entityManager.refresh(node);
			return node;
		}else {
			throw new ObjectNotFoundException("Node is null or node identifier not defined. Cannot save to the tree!",logger);
		}
	}

	/**
	 * Get a tree level right below some parent node
	 * @param parent
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<Concept> loadLevel(Concept parent) {
		List<Concept> ret = new ArrayList<Concept>();
		if(parent!=null) {
			try {
				parent = loadConceptById(parent.getID());

				List<Closure> childs = closureRepo.findByParentAndLevel(parent, 1);
				for(Closure clos :childs) {

					//for(Closure clos : parent.getChilds()) {
					//if(clos.getLevel()==1) {
					ret.add(clos.getChild());
					//}
				}
			} catch (Exception e) {
				//nothing to do;
			}
		}
		return ret;
	}

	/**
	 * Remove the node as well as all children of it
	 * <b>DANAGER. Doesn't work in transaction!!!!!</b>
	 * @param node node to remove
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public void removeNode(Concept node) throws ObjectNotFoundException{
		if(node != null && node.getID()>0) {
			jdbcRepo.removeConcept(node);
		}
	}
	/**
	 * Get immediate parent of the concept given
	 * Returns null if the concept is a root of the tree
	 * @param child
	 * @return null if it is a root
	 */
	@Transactional
	public Concept getParent(Concept child) {
		List<Closure> parents = closureRepo.findByChild(child);
		//for(Closure clos :child.getParents()) {
		for(Closure clos : parents) {
			if(clos.getLevel()==1) {
				return clos.getParent();
			}
		}
		return null;
	}
	/**
	 * Load or create and save root concept by identifier
	 * @param identifier
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept loadRoot(String identifier) throws ObjectNotFoundException {
		Concept root = new Concept();
		root.setIdentifier(identifier);
		root = saveToTree(null, root);
		return root;
	}

	/**
	 * Load the Concept by ID
	 * @param id
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept loadConceptById(long id) throws ObjectNotFoundException {
		Optional<Concept> parento = conceptRepo.findById(id);
		if(parento.isPresent()) {
			return parento.get();
		}else {
			throw new ObjectNotFoundException("loadConceptById. Parent concept not found, id="+id,logger);
		}
	}

	/**
	 * Get a url of tree by a node given
	 * @param concept
	 * @return
	 */
	@Transactional
	public String getUrlByNode(Concept node) {
		if(node == null) {
			return "";
		}
		List<Closure> closures = untilRoot(node);
		Closure rootClosure = closures.get(closures.size()-1);
		Concept url =rootClosure.getParent(); 
		return url.getIdentifier();
	}
	/**
	 * find all parents untill the root in reverse order
	 * @return
	 */
	@Transactional
	public List<Closure> untilRoot(Concept node) {
		List<Closure> ret = closureRepo.findByChildOrderByLevelAsc(node);
		return ret;
	}

	/**
	 * Load all parents for a node given from root to node
	 * @param node
	 * @return
	 */
	@Transactional
	public List<Concept> loadParents(Concept node) {
		List<Concept> ret = new ArrayList<Concept>();
		List<Closure> clos = closureRepo.findByChildOrderByLevelDesc(node);
		for(Closure clo : clos) {
			ret.add(clo.getParent());
		}
		return ret;
	}
	/**
	 * Load all root concepts
	 * @return
	 */
	@Transactional
	public List<Concept> allRoots() {
		List<Concept> ret = conceptRepo.findAllRoots(); 
		return ret;
	}
	/**
	 * move the whole subtree start with node to the newParent
	 * @param node
	 * @param newParent
	 */
	@Transactional
	public void moveSubTree(Concept node, Concept newParent) {
		closureRepo.moveSubTree(node.getID(), newParent.getID());
	}

	/**
	 * Read a variable values from all records of a dictionary
	 * @param dictUrl
	 * @param varName
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public List<String> dictAllValues(String dictUrl, String varName) throws ObjectNotFoundException {
		List<String> ret = new ArrayList<String>();
		Concept root = loadRoot(dictUrl);
		List<String> urls=closureRepo.dictvariables(root.getID(), varName);
		if(urls!= null) {
			ret.addAll(urls);
		}
		return ret;
	}
	/**
	 * Save a concept
	 * @param fileNode
	 * @return
	 */
	@Transactional
	public Concept save(Concept concept) {
		concept = conceptRepo.save(concept);
		return concept;
	}
	/**
	 * Save and flush data
	 * @param iNode
	 * @return
	 */
	@Transactional
	public Concept saveAndFlush(Concept concept) {
		concept = conceptRepo.save(concept);
		entityManager.flush();
		entityManager.refresh(concept);
		return concept;
	}
	/**
	 * Get or create concept with identifier under the parent
	 * @param personRoot
	 * @param email
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept saveToTree(Concept parent, String identifier) throws ObjectNotFoundException {
		Concept ret = new Concept();
		ret.setIdentifier(identifier);
		ret=saveToTree(parent, ret);
		return ret;
	}

	/**
	 * Create List of IDs instead List of Concept.
	 * Utility
	 * @param concepts
	 * @return
	 */
	public List<Long> extractIds(List<Concept> concepts) {
		List<Long> ids = new ArrayList<Long>();
		for(Concept dc : concepts) {
			ids.add(dc.getID());
		}
		return ids;
	}
	/**
	 * Get or place concept under the url, and user's email
	 * @param url
	 * @param email
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept placeConceptToTree(String url, String email, Concept concept) throws ObjectNotFoundException {
		Concept root = loadRoot(url);
		Concept user = new Concept();
		user.setIdentifier(email);
		user = saveToTree(root, user);
		if(concept.getIdentifier()==null) {
			concept=saveAndFlush(concept);
			concept.setIdentifier(concept.getID()+"");
		}
		concept=saveToTree(user, concept);
		return concept;
	}
	/**
	 * Clone all tree from the node under the root
	 * Root should be stored
	 * @param root - store under this root
	 * @param node - clone this node
	 * @return
	 * @throws ObjectNotFoundException 
	 */
	@Transactional
	public Concept cloneTree(Concept root, Concept node) throws ObjectNotFoundException {
		Concept ret= cloneConcept(node);
		ret=saveToTree(root,ret);
		List<Concept> childs = loadLevel(node);
		for(Concept child : childs) {
			cloneTree(ret,child);
		}
		return ret;
	}
	/**
	 * Clone a concept
	 * @param root
	 * @return
	 */
	@Transactional
	private Concept cloneConcept(Concept root) {
		Concept ret = new Concept();
		ret.setActive(true);
		if(!root.getIdentifier().equalsIgnoreCase(root.getID()+"")) {
			ret.setIdentifier(root.getIdentifier());
		}else {
			ret=save(ret);
			ret.setIdentifier(ret.getID()+"");
		}
		ret.setLabel(root.getLabel());
		return ret;
	}
	/**
	 * Load concept by Identifier and label
	 * @param identifier
	 * @param label
	 * @return
	 */
	public List<Concept> loadConceptByIdentifierAndLabel(String identifier, String label) {
		List<Concept> ret= new ArrayList<Concept>();
		ret=conceptRepo.findAllByIdentifierAndLabel(identifier, label);
		return ret;
	}


}
