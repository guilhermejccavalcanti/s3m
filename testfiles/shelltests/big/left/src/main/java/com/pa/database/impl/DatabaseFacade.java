package com.pa.database.impl;

import java.util.List;

import org.hibernate.Criteria;

import com.pa.entity.Book;
import com.pa.entity.Chapter;
import com.pa.entity.Curriculo;
import com.pa.entity.Group;
import com.pa.entity.Orientation;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.entity.Qualis;
import com.pa.entity.QualisData;
import com.pa.entity.TechnicalProduction;
import com.pa.util.EnumPublicationLocalType;


public class DatabaseFacade {

	private static DatabaseFacade _instance = null;
	
	private CurriculoDAO cDAO = new CurriculoDAO(Curriculo.class);
	private GroupDAO gDAO = new GroupDAO(Group.class);
	private PublicationDAO pDAO = new PublicationDAO(Publication.class);
	private PublicationTypeDAO pTDAO = new PublicationTypeDAO(PublicationType.class);
	private QualisDAO qDAO = new QualisDAO(Qualis.class);
	private QualisDataDAO qdDAO = new QualisDataDAO(QualisData.class);
	private TechinicalProductionDAO tPDAO = new TechinicalProductionDAO(TechnicalProduction.class);
	private OrientationDAO oDAO = new OrientationDAO(Orientation.class);
	private BookDAO bDAO = new BookDAO(Book.class);
	private ChapterDAO chDAO = new ChapterDAO(Chapter.class);
	
	private DatabaseFacade() {}
	
	public static DatabaseFacade getInstance() {
		if(_instance == null) {
			_instance = new DatabaseFacade();
		}
		
		return _instance;
	}
	
	public TechnicalProduction saveTechnicalProduction(TechnicalProduction t){
		return tPDAO.save(t);
	}
	
	public void updateTechnicalProduction(TechnicalProduction t) {
		tPDAO.update(t);
	}
	
	public List<TechnicalProduction> listAllTechnicalProductions() {
		return tPDAO.listAll();
	}
	
	public List<Orientation> listAllOrientations() {
		return oDAO.listAll();
	}
	
	public List<Book> listAllBooks() {
		return bDAO.listAll();
	}
	
	public List<Chapter> listAllChapters() {
		return chDAO.listAll();
	}
	
	public Curriculo saveCurriculo(Curriculo c) {
		return cDAO.save(c);
	}
	
	public void deleteCurriculo(Curriculo c) {
		cDAO.delete(c);
	}
	
	public void updateCurriculo(Curriculo c) {
		cDAO.update(c);
	}
	
	public void refreshCurriculo(Curriculo c) {
		cDAO.refresh(c);
	}
	
	public Curriculo getCurriculoByName(String name) {
		return cDAO.getCurriculoByName(name);
	}
	
	public Curriculo getCurriculoById(Long id) {
		return cDAO.get(id);
	}
	
	public Criteria createCurriculoCriteria() {
		return cDAO.createCriteria(Curriculo.class);
	}
	
	public Criteria createCurriculoCriteria(String alias) {
		return cDAO.createCriteria(Curriculo.class, alias);
	}
	
	public List<Curriculo> listAllCurriculos() {
		return cDAO.listAll();
	}
	
	public List<Curriculo> listAllCurriculos(Object example) {
		return cDAO.listAll(example);
	}
	
	public List<Curriculo> listAllCurriculos(int first, int max) {
		return cDAO.listAll(first, max);
	}
	
	public List<Curriculo> listAllCurriculosByQuery(String query) {
		return cDAO.listAllByQuery(query);
	}
	
	public Group saveGroup(Group c) {
		return gDAO.save(c);
	}
	
	public void deleteGroup(Group c) {
		gDAO.delete(c);
	}
	
	public void updateGroup(Group c) {
		gDAO.update(c);
	}
	
	public void refreshGroup(Group c) {
		gDAO.refresh(c);
	}
	
	public Group getGroupById(Long id) {
		return gDAO.get(id);
	}
	
	public Criteria createGroupCriteria() {
		return gDAO.createCriteria(Group.class);
	}
	
	public Criteria createGroupCriteria(String alias) {
		return gDAO.createCriteria(Group.class, alias);
	}
	
	public List<Group> listAllGroups() {
		return gDAO.listAll();
	}
	
	public List<Group> listAllGroups(Object example) {
		return gDAO.listAll(example);
	}
	
	public List<Group> listAllGroups(int first, int max) {
		return gDAO.listAll(first, max);
	}
	
	public List<Group> listAllGroupsByQuery(String query) {
		return gDAO.listAllByQuery(query);
	}
	
	public PublicationType savePublicationType(PublicationType pt) {
		return pTDAO.save(pt);
	}
	
	public void deletePublicationType(PublicationType pt) {
		pTDAO.delete(pt);
	}
	
	public void updatePublicationType(PublicationType pt) {
		pTDAO.update(pt);
	}
	
	public void refreshPublicationType(PublicationType pt) {
		pTDAO.refresh(pt);
	}
	
	public PublicationType getPublicationTypeById(Long id) {
		return pTDAO.get(id);
	}
	
	public PublicationType getPublicationTypeByNameAndType(String name, EnumPublicationLocalType type) {
		return pTDAO.getPublicationTypeByNameAndType(name, type);
	}
	
	public Criteria createPublicationTypeCriteria() {
		return pTDAO.createCriteria(PublicationType.class);
	}
	
	public Criteria createPublicationTypeCriteria(String alias) {
		return pTDAO.createCriteria(PublicationType.class, alias);
	}
	
	public List<PublicationType> listAllPublicationTypes() {
		return pTDAO.listAll();
	}
	
	public List<PublicationType> listAllPublicationTypes(Object example) {
		return pTDAO.listAll(example);
	}
	
	public List<PublicationType> listAllPublicationTypes(int first, int max) {
		return pTDAO.listAll(first, max);
	}
	
	public List<PublicationType> listAllPublicationTypesByQuery(String query) {
		return pTDAO.listAllByQuery(query);
	}
	
	public Publication savePublication(Publication p) {
		return pDAO.save(p);
	}
	
	public void deletePublication(Publication p) {
		pDAO.delete(p);
	}
	
	public void updatePublication(Publication p) {
		pDAO.update(p);
	}
	
	public void refreshPublication(Publication p) {
		pDAO.refresh(p);
	}
	
	public Publication getPublicationById(Long id) {
		return pDAO.get(id);
	}
	
	public Criteria createPublicationCriteria() {
		return pDAO.createCriteria(Publication.class);
	}
	
	public Criteria createPublicationCriteria(String alias) {
		return pDAO.createCriteria(Publication.class, alias);
	}
	
	public List<Publication> listAllPublications() {
		return pDAO.listAll();
	}
	
	public List<Publication> listAllPublications(Object example) {
		return pDAO.listAll(example);
	}
	
	public List<Publication> listAllPublications(int first, int max) {
		return pDAO.listAll(first, max);
	}
	
	public List<Publication> listAllPublicationsByQuery(String query) {
		return pDAO.listAllByQuery(query);
	}
	
	public QualisData saveQualisData(QualisData c) {
		return qdDAO.save(c);
	}
	
	public void deleteQualisData(QualisData c) {
		qdDAO.delete(c);
	}
	
	public void updateQualisData(QualisData c) {
		qdDAO.update(c);
	}
	
	public void refreshQualisData(QualisData c) {
		qdDAO.refresh(c);
	}
	
	public QualisData getQualisDataByTypeAndYear(EnumPublicationLocalType type, int year) {
		return qdDAO.getQualisDataByTypeAndYear(type, year);
	}
	
	public QualisData getQualisDataById(Long id) {
		return qdDAO.get(id);
	}
	
	public Criteria createQualisDataCriteria() {
		return qdDAO.createCriteria(QualisData.class);
	}
	
	public Criteria createQualisDataCriteria(String alias) {
		return qdDAO.createCriteria(QualisData.class, alias);
	}
	
	public List<QualisData> listAllQualisData() {
		return qdDAO.listAll();
	}
	
	public List<QualisData> listAllQualisData(Object example) {
		return qdDAO.listAll(example);
	}
	
	public List<QualisData> listAllQualisData(int first, int max) {
		return qdDAO.listAll(first, max);
	}
	
	public List<QualisData> listAllQualisDataByQuery(String query) {
		return qdDAO.listAllByQuery(query);
	}
	
	public Qualis saveQualis(Qualis c) {
		return qDAO.save(c);
	}
	
	public void deleteQualis(Qualis c) {
		qDAO.delete(c);
	}
	
	public void updateQualis(Qualis c) {
		qDAO.update(c);
	}
	
	public void refreshQualis(Qualis c) {
		qDAO.refresh(c);
	}
		
	public Qualis getQualisById(Long id) {
		return qDAO.get(id);
	}
	
	public Criteria createQualisCriteria() {
		return qDAO.createCriteria(Qualis.class);
	}
	
	public Criteria createQualisCriteria(String alias) {
		return qDAO.createCriteria(Qualis.class, alias);
	}
	
	public List<Qualis> listAllQualis() {
		return qDAO.listAll();
	}
	
	public List<Qualis> listAllQualis(Object example) {
		return qDAO.listAll(example);
	}
	
	public List<Qualis> listAllQualis(int first, int max) {
		return qDAO.listAll(first, max);
	}
	
	public List<Qualis> listAllQualisByQuery(String query) {
		return qDAO.listAllByQuery(query);
	}
}
