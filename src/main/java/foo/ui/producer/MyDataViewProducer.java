package foo.ui.producer;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Produces;
import javax.faces.model.ListDataModel;
import javax.inject.Inject;
import javax.inject.Named;

import foo.domain.entity.MyData;
import foo.domain.repository.MyDataRepository;
import foo.ui.view.MyDataView;

public class MyDataViewProducer {

	@Inject
	private MyDataRepository myDataRepository;

	@Produces
	@Named("myDataView")
	@RequestScoped
	public MyDataView createMyDataView() {
		MyDataView myDataView = new MyDataView();

		ListDataModel<MyData> myDataModel = new ListDataModel<MyData>(myDataRepository.findAllBy("%"));

		myDataView.setMyDataModel(myDataModel);

		return myDataView;
	}

}
