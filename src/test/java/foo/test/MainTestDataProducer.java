package foo.test;

import javax.persistence.EntityManager;

import foo.domain.entity.MyData;
import foo.domain.entity.MyLookup;

public class MainTestDataProducer extends AbstractTestDataProducer {

	public MainTestDataProducer(EntityManager entityManager) {
		super(entityManager);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createTestData() throws Exception {
		createSimpleLookupList(MyLookup.class, "aa", 2);
		for (int i = 0; i < 2; i++) {
			MyLookup myLookup = createSimpleLookup(MyLookup.class, "aa" + i);
			for (int j = 0; j < 2; j++) {
				MyData myData = new MyData(Math.random() * 100, myLookup);
				entityManager.persist(myData);
			}
		}

		// TODO Auto-generated method stub

	}

}
