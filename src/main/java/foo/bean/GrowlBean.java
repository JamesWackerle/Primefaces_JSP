package foo.bean;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@ManagedBean(name = "growlBean")
@SessionScoped
public class GrowlBean {

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void save(ActionEvent actionEvent) {
		this.text += "hey";
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage("Successful", "Hello " + getText()));
		context.addMessage(null, new FacesMessage("Second Message", "Additional Info Here..."));

	}

	private String test;

	public String getTest() {
		test = getText();
		return test;
	}

}
