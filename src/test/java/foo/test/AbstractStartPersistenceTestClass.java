package foo.test;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import com.abuscom.core.jpa.AbuscomEntityManagerFacade;

public abstract class AbstractStartPersistenceTestClass {

	private static final String _TEST_DB_ = "testDb";
	private static EntityManagerFactory emf;
	protected static EntityManager entityManager;
	protected static EntityTransaction tx;

	protected abstract ITestDataProducer getTestDataProducer();

	public static String getDbName() {
		return _TEST_DB_;
	}

	@BeforeClass
	public static void initEntityManager() throws Exception {

		emf = Persistence.createEntityManagerFactory(getDbName());
		entityManager = new AbuscomEntityManagerFacade(emf.createEntityManager(), "SYSTEM");

	}

	@AfterClass
	public static void releaseEntityManager() {
		if (entityManager.isOpen()) {
			entityManager.close();
		}
		emf.close();
	}

	@Before
	public void initTransaction() {
		tx = entityManager.getTransaction();
		tx.begin();
		if (getTestDataProducer() != null) {
			try {
				getTestDataProducer().createTestData();
			} catch (Exception e) {
				System.out.println("error on creating data");
				e.printStackTrace();
			}
			tx.commit();
		}
		tx.begin();
	}

	@After
	public void rollback() {
		if (tx.isActive()) {
			tx.rollback();
		}
	}

}
