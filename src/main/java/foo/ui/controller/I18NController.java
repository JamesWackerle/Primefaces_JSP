/* 	
 *	Copyright (C)  2011 open knowledge GmbH
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package foo.ui.controller;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Named;

@Named("i18nController")
@SessionScoped
public class I18NController implements Serializable {

	private final List<Locale> supportedLocales = Arrays.asList(new Locale[] { Locale.ENGLISH, Locale.GERMAN });

	private static final long serialVersionUID = 1L;

	private Locale currentLocale = Locale.ENGLISH;

	public String getLocaleDisplayName() {
		return currentLocale.getDisplayName(currentLocale);
	}

	// ---------- JSF callbacks ---------------------------

	public Locale getCurrentLocale() {
		return currentLocale;
	}

	public void switchLanguageTo(String newLocale) {
		for (Locale locale : supportedLocales) {
			if (locale.getLanguage().equals(newLocale)) {
				this.currentLocale = locale;
				FacesContext.getCurrentInstance().getViewRoot().setLocale(getCurrentLocale());
				return;
			}
		}

	}

	public List<Locale> getSupportedLocales() {
		return supportedLocales;
	}

}
