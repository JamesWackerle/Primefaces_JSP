package foo.ui.producer;

import java.io.Serializable;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

import org.primefaces.component.menuitem.MenuItem;
import org.primefaces.component.submenu.Submenu;

import com.abuscom.core.ui.util.FacesAccessor;

@SessionScoped
@Named("languageMenuModel")
public class LanguageMenuModelProducer implements Serializable {

	private static Locale[] supportedLocales = new Locale[] { Locale.ENGLISH, Locale.GERMAN };

	private Submenu submenu;

	@PostConstruct
	public void initMenu() {

		Submenu languageMenu = new Submenu();
		languageMenu.setLabel(FacesAccessor.getLocalizedMessageforKey("menu_view_language"));

		for (int i = 0; i < supportedLocales.length; i++) {
			Locale locale = supportedLocales[i];
			MenuItem menuItem = new MenuItem();
			menuItem.setValue(locale.getDisplayLanguage());
			menuItem.setAjax(false);
			menuItem.setActionExpression(FacesAccessor.createMethodExpression("#{i18nController.switchLanguageTo('"
					+ locale.getLanguage() + "')}", String.class, new Class[] { String.class }));
			languageMenu.getChildren().add(menuItem);
			setSubmenu(languageMenu);
		}

	}

	public Submenu getSubmenu() {
		return submenu;
	}

	public void setSubmenu(Submenu submenu) {
		this.submenu = submenu;
	}

}
