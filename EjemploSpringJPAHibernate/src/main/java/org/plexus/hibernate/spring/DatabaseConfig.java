package org.plexus.hibernate.spring;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

	@Bean
	public DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(env.getProperty("db.driver"));
		dataSource.setUrl(env.getProperty("db.url"));
		dataSource.setUsername(env.getProperty("db.username"));
		dataSource.setPassword(env.getProperty("db.password"));
		
		return dataSource;
		
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(){
		LocalContainerEntityManagerFactoryBean entityManagerFactory =
				new LocalContainerEntityManagerFactoryBean();
		
		//Asignar origen de datos el data source que ha sido inyectado en esta clase
		entityManagerFactory.setDataSource(dataSource());
		
		// Paquetess que tiene que escanear para buscar las clases anotadas (property en el fichero de properties)
		entityManagerFactory.setPackagesToScan("org.plexus.hibernate.spring");
		
		//Implementacion/vendor usaremos hibernate
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		entityManagerFactory.setJpaVendorAdapter(vendorAdapter);
		
		//Añadir las propiedades de hibernate (Dialecto, show_sql, ddl) se cargan desde el fichero de properties
		Properties additionalProperties = new Properties();
		additionalProperties.put("hibernate.dialect", env.getProperty("hibernate.dialect"));
		additionalProperties.put("hibernate.show_sql",env.getProperty("hibernate.show_sql"));
		additionalProperties.put("hibernate.hbm2ddl.auto",env.getProperty("hibernate.hbm2ddl.auto"));
		entityManagerFactory.setJpaProperties(additionalProperties);
	
		return entityManagerFactory; 
	}
	//Gestor de transacciones
	@Bean
	public JpaTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory.getObject());
		return transactionManager;
	}
	
	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}
	
	
	//Elemento autocableado/autoinyectado Permite leer la configuracion de las diferentes properties que tengamos definidas/Par que los beans se inyecten en el codigo 
	@Autowired
	private Environment env;

	@Autowired
	private DataSource dataSource;  

	@Autowired
	private LocalContainerEntityManagerFactoryBean entityManagerFactory;



	/** Revisión de conceptos claves:
	 * 		-@Bean
	 * 		-@Autowired
	 * 		-Funciones EntityManager() 
	 * 					TransactionManager()
	 */
}
