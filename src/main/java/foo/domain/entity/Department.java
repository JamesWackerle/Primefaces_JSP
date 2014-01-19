package foo.domain.entity;

public class Department {

	private String departmentLabel;
	private int departmentValue;

	public Department(String departmentLabel, int departmentValue) {
		super();
		this.departmentLabel = departmentLabel;
		this.departmentValue = departmentValue;
	}

	public String getDepartmentLabel() {
		return departmentLabel;
	}

	public void setDepartmentLabel(String departmentLabel) {
		this.departmentLabel = departmentLabel;
	}

	public int getDepartmentValue() {
		return departmentValue;
	}

	public void setDepartmentValue(int departmentValue) {
		this.departmentValue = departmentValue;
	}

}
