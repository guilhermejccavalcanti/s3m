package com.pa.database.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

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

public class HibernateUtil {

	private static SessionFactory sessionFactory;
    private static ServiceRegistry serviceRegistry;
    
	private static SessionFactory configure(String xml) {
		Configuration configuration = new Configuration();
	    configuration.configure(xml);
	    serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
	    
	    MetadataSources metadataSources = new MetadataSources(serviceRegistry);
	    metadataSources.addAnnotatedClass(Qualis.class);
	    metadataSources.addAnnotatedClass(QualisData.class);
	    metadataSources.addAnnotatedClass(Group.class);
	    metadataSources.addAnnotatedClass(Publication.class);
	    metadataSources.addAnnotatedClass(Curriculo.class);
	    metadataSources.addAnnotatedClass(PublicationType.class);
	    metadataSources.addAnnotatedClass(TechnicalProduction.class);
	    metadataSources.addAnnotatedClass(Orientation.class);
	    metadataSources.addAnnotatedClass(Chapter.class);
	    metadataSources.addAnnotatedClass(Book.class);
	    Metadata metadata = metadataSources.buildMetadata();
	    
	    sessionFactory =  metadata.buildSessionFactory();
		
		return sessionFactory;
	}
	
	public static SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			configure("hibernate.cfg.xml");
		}
		
		return sessionFactory;
	}
	
	public static SessionFactory getSessionFactory(String xml) {
		if(sessionFactory == null) {
			configure(xml);
		}
		
		return sessionFactory;
	}
	
	public static SessionFactory createSessionFactory(String xml) {
		configure(xml);
		
		return sessionFactory;
	}
}
