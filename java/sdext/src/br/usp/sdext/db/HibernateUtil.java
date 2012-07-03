package br.usp.sdext.db;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


@SuppressWarnings("deprecation")
public class HibernateUtil {
	
	private final static SessionFactory sessionFactory;
	
	static {
		
		System.out.println("Initializing database...");
		
		sessionFactory = new Configuration().configure().buildSessionFactory();
		
		new Bootstrap();
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
