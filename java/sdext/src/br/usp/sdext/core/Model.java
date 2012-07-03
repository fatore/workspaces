package br.usp.sdext.core;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.exception.ConstraintViolationException;

import br.usp.sdext.db.HibernateUtil;

public abstract class Model  {
	
	abstract public void setId(Long id);
	
	public static Model fetchAndSave(Model model, HashMap<Model, Model> map) {
		
		if (model == null) {
			
			return null;
		}
		Model mappedModel = map.get(model);
		
		if (mappedModel == null) {
			
			model.setId(new Long(map.size()));
			map.put(model, model);
			model.save();
		} 
		else {
			
			model = mappedModel;
		}
		
		return model;
	}
	
	public static Model fetch(Model model, HashMap<Model, Model> map) {
		
		if (model == null) {
			
			return null;
		}

		// Look if model exists in map
		Model mappedModel = map.get(model);
		
		// If didn't find anything ..
		if (mappedModel == null) {
			
			// Set the ID for the new Model ...
			model.setId(new Long(map.size()));
			
			// ... and put it in the map.
			map.put(model, model);
		} 
		// If found ... 
		else {
			
			model = mappedModel;
		}
		
		return model;
	}
	
	public static Long numElements(Class<?> target) {
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		
		Criteria criteria = session.createCriteria(target);
		criteria.setProjection(Projections.rowCount());
		
		Long result = (Long) criteria.uniqueResult();
		
		transaction.commit();	
		session.close();
		
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Model> findAll(Class<?> target) {
	
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		
		Criteria criteria = session.createCriteria(target);
		criteria.setCacheable(true);
//		criteria.setFetchMode("candidate", FetchMode.JOIN);
		
		List<Model> list = criteria.list();
		
		transaction.commit();	
		session.close();
		
		return list;
	}
	
	public static void findAll(Class<?> target, HashMap<Model, Model> map) {
	
		for (Model model : findAll(target)) {
			
			map.put(model, model);
		}
	}
	
	public static void findAll(Class<?> target, Set<Model> set) {
		
		for (Model model : findAll(target)) {
			
			set.add(model);
		}
	}
	
	public Map<Model, Model> loadAll() {
		
		return null;
	}

	public void save() {
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		
		try {
			
			session.save(this);
			transaction.commit();	
		} 
		catch (ConstraintViolationException e) {
			
			transaction.rollback();	
			throw e;
		}
		session.close();
	}
	
	public static void bulkSave(Collection<Model> collection) {
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = session.beginTransaction();
		   
		int count = 0;
		for (Model model : collection) {
			
			session.save(model);
			
			if (count % 50 == 0 ) { //50, same as the JDBC batch size
		        //flush a batch of inserts and release memory:
		        session.flush();
		        session.clear();
		    }
		}
		
		transaction.commit();
		session.close();
	}
	
//	public static Model findByPK(Long id) {
//		
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		
//		Model party = (Model) session.get(Model.class, id);
//		
//		session.getTransaction().commit();
//		
//		return party;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static List<Model> findAll() {
//		
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		
//		List<Model> parties = (List<Model>) session.createCriteria(Election.class).list();
//		
//		session.getTransaction().commit();
//		
//		return parties;
//	}
	
//public void delete() {
//		
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		
//		try {
//			session.delete(this);
//			session.getTransaction().commit();		
//		} catch (ConstraintViolationException e) {
//			session.getTransaction().rollback();
//			throw e;
//		}
//	}
//	
//	public void update(Candidate newer) {
//		
//		this.merge(newer);
//		
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		
//		try {
//			session.update(this);
//			session.getTransaction().commit();		
//		} catch (ConstraintViolationException e) {
//			session.getTransaction().rollback();
//			throw e;
//		}
//	}
//	
//	public static Candidate findByID(Long id) {
//		
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		
//		Candidate candidate = (Candidate) session.get(Candidate.class, id);
//		
//		session.getTransaction().commit();
//		
//		return candidate;
//	}
//	
//	@SuppressWarnings("unchecked")
//	public static List<Candidate> findAll() {
//		
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		
//		List<Candidate> candidates = (List<Candidate>) session.createCriteria(Candidate.class).list();
//		
//		session.getTransaction().commit();
//		
//		return candidates;
//	}
//	
//	public static Candidate findByBasic(String name, Date birthDate, String birthState) {
//		
//		Session session = HibernateUtil.getSessionFactory().getCurrentSession();
//		session.beginTransaction();
//		
//		Candidate candidate = null;
//		
//		candidate =  (Candidate) session.createQuery(
//				"select c from Candidate c where c.name = :name and c.birthDate = :birthDate " +
//				"and c.birthState = :birthState").
//				setParameter("name", name).
//				setParameter("birthDate", birthDate).
//				setParameter("birthState", birthState).
//				uniqueResult();
//		
//		session.getTransaction().commit();
//		
//		return candidate;
//	}
}
