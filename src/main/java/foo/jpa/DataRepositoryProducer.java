package foo.jpa;

import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.abuscom.core.jpa.AbuscomEntityManagerFacade;
import com.abuscom.core.qualifier.ActualUser;
import com.abuscom.core.qualifier.DataRepository;

public class DataRepositoryProducer {

	@Inject
	@ActualUser
	String actualUser;

	private EntityManager entityManager;

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = new AbuscomEntityManagerFacade(entityManager, actualUser);
	}

	@Produces
	@DataRepository
	public EntityManager getEntityManager() {
		return entityManager;
	}

}
