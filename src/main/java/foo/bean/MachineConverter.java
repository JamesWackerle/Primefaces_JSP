package foo.bean;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;

import foo.domain.entity.Machine;

@FacesConverter(value = "machineConverter")
public class MachineConverter implements Converter {

	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				return new Machine(submittedValue);

			} catch (NumberFormatException exception) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error",
						"Not a valid Machine"));
			}
		}
	}

	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((Machine) value).getMachine());
		}
	}
}