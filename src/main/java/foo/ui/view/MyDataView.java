package foo.ui.view;

import java.io.Serializable;

import javax.faces.model.ListDataModel;

import foo.domain.entity.MyData;

public class MyDataView implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private ListDataModel<MyData> myDataModel;

	public ListDataModel<MyData> getMyDataModel() {
		return myDataModel;
	}

	public void setMyDataModel(ListDataModel<MyData> myDataModel) {
		this.myDataModel = myDataModel;
	}

}
