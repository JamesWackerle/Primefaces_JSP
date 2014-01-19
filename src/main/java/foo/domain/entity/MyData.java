package foo.domain.entity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.abuscom.core.domain.entity.BaseEntity;

@Entity
@Table(name = "START_MYDATA")
public class MyData extends BaseEntity {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Double value;

	@ManyToOne(fetch = FetchType.LAZY)
	@NotNull
	private MyLookup myLookup;

	@NotNull
	public Double getValue() {
		return value;
	}

	public MyLookup getMyLookup() {
		return myLookup;
	}

	public void setValue(Double value) {
		this.value = value;
	}

	public void setMyLookup(MyLookup myLookup) {
		this.myLookup = myLookup;
	}

	public MyData(Double value, MyLookup myLookup) {
		super();
		this.value = value;
		this.myLookup = myLookup;
	}

	protected MyData() {
		super();
		// TODO Auto-generated constructor stub
	}

}
