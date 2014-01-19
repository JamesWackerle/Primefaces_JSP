package foo.bean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

@ManagedBean(name = "fileUploadController")
@ViewScoped
public class FileUploadController {

	private UploadedFile file;

	public UploadedFile getFile() {
		return file;
	}

	public void setFile(UploadedFile file) {
		this.file = file;
	}

	public void upload() {
		if (file != null) {
			FacesMessage msg = new FacesMessage("Succesful", file.getFileName() + " is uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, msg);
		}
	}

	/**
	 * 
	 * Handles the file upload and writes the bytes of a file to the file
	 * system.
	 * 
	 * @param event
	 */
	public void handleFileUpload(FileUploadEvent event) {

		System.out.println("Got to File upload handler");
		ExternalContext extContext = FacesContext.getCurrentInstance().getExternalContext();
		File result = new File(extContext.getRealPath("//WEB-INF//files//" + event.getFile().getFileName()));
		System.out.println("*** File Path: "
				+ extContext.getRealPath("//WEB-INF//files//" + event.getFile().getFileName()));
		System.out.println("***** File Absolute Path:" + result.getAbsolutePath());

		try {
			FileOutputStream fileOutputStream = new FileOutputStream(result);

			byte[] buffer = new byte[6124];

			int bulk;
			InputStream inputStream = event.getFile().getInputstream();
			while (true) {
				bulk = inputStream.read(buffer);
				if (bulk < 0) {
					break;
				}
				fileOutputStream.write(buffer, 0, bulk);
				fileOutputStream.flush();
			}

			fileOutputStream.close();
			inputStream.close();

			FacesMessage msg = new FacesMessage("File Description", "file name: " + event.getFile().getFileName()
					+ "<br/>file size: " + event.getFile().getSize() / 1024 + " Kb<br/>content type: "
					+ event.getFile().getContentType() + "<br/><br/>The file was uploaded.");
			FacesContext.getCurrentInstance().addMessage(null, msg);

		} catch (IOException e) {
			e.printStackTrace();

			FacesMessage error = new FacesMessage(FacesMessage.SEVERITY_ERROR, "The files were not uploaded!", "");
			FacesContext.getCurrentInstance().addMessage(null, error);
		}
	}
}
