package foo.domain.repository;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import com.abuscom.core.qualifier.DataRepository;
import foo.domain.entity.MyData;
import foo.domain.entity.MyData_;
import foo.domain.entity.MyLookup_;

public class MyDataRepository {

	@Inject
	@DataRepository
	private EntityManager entityManager;

	public MyDataRepository(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public MyDataRepository() {
	}
	public List<MyData> findAllBy(String name) {
		String query = " from " + MyData_.REF + " data join data." + MyData_.MY_LOOKUP + " lok  where lok."
				+ MyLookup_.NAME + " like ?";

		return entityManager.createQuery(query).setParameter(1, name).getResultList();
	}

}
