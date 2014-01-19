package MockUps;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

@ManagedBean(name = "growlBean1")
@SessionScoped
public class GrowlBean {

	private String text = "x";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void save(ActionEvent actionEvent) {
		FacesContext context = FacesContext.getCurrentInstance();

		context.addMessage(null, new FacesMessage("Successful", "Hello " + text));
		context.addMessage(null, new FacesMessage("Second Message", "Additional Info Here..."));
	}
}
