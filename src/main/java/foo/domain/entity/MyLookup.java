package foo.domain.entity;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.abuscom.core.domain.entity.LookupEntity;

@Entity
@Table(name = "START_MYLOOKUP")
public class MyLookup extends LookupEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

}
