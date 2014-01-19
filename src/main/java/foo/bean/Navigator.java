package foo.bean;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean
@SessionScoped
public class Navigator {

	private String test = "reqs";

	private String[] resultPages = { "servicerequestsreview", "reqs", "statusReqs" };

	public String choosePage() {

		return "reqs";
	}

	public String getTest() {
		return test;
	}

	public void setTest(String test) {
		this.test = test;
	}

}
