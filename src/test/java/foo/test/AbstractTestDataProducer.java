package foo.test;

import javax.persistence.EntityManager;

import com.abuscom.core.domain.entity.LookupEntity;

public abstract class AbstractTestDataProducer implements ITestDataProducer {

	protected final EntityManager entityManager;

	public AbstractTestDataProducer(EntityManager entityManager) {
		super();
		this.entityManager = entityManager;
	}

	protected <T extends LookupEntity> T createSimpleLookup(Class<T> clazz, String name) throws Exception {

		T instance = clazz.newInstance();
		instance.setName(name);
		instance.setDescription(name);
		entityManager.persist(instance);

		return instance;

	}

	protected <T extends LookupEntity> void createSimpleLookupList(Class<T> clazz, String namePrefix,
			int numberOfInstances) throws Exception {

		for (int i = 0; i < numberOfInstances; i++) {
			createSimpleLookup(clazz, namePrefix + "_" + i);
		}

	}

}
