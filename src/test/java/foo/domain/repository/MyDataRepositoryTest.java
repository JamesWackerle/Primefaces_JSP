package foo.domain.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import foo.domain.entity.MyData;
import foo.test.AbstractStartPersistenceTestClass;
import foo.test.ITestDataProducer;
import foo.test.MainTestDataProducer;

public class MyDataRepositoryTest extends AbstractStartPersistenceTestClass {

	@Test
	public void testLoadDataByLookupName() {

		MyDataRepository underTest = new MyDataRepository(entityManager);

		String name = "a%";
		List<MyData> myDatas = underTest.findAllBy(name);

		assertNotNull(myDatas);
		assertEquals(4, myDatas.size());
	}

	@Override
	protected ITestDataProducer getTestDataProducer() {
		return new MainTestDataProducer(entityManager);
	}

}
