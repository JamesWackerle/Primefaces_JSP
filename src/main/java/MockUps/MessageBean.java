package MockUps;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

@ManagedBean(name = "messageBean")
@SessionScoped
public class MessageBean {

	private String text = "messageBean";

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
}
