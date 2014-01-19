package foo.domain.entity;

import java.sql.Timestamp;
import java.util.Date;

public class ServiceLaborTrackingRecord {

	private String orderNumber;
	private int lineNumber;
	private int activity;
	private String employeeNumber;
	private Timestamp startDate;
	private Timestamp endDate;
	private int recordProcessed;
	private int recordRead;

	private Date startModifyDate;
	private Date endModifyDate;

	private Timestamp localTimeStartDate;
	private Timestamp localTimeEndDate;

	private Timestamp transactionDate; // t$hrdt

	public ServiceLaborTrackingRecord(String orderNumber, int lineNumber, int activity, String employeeNumber,
			Timestamp startDate, Timestamp endDate, int recordProcessed, int recordRead) {
		super();
		this.orderNumber = orderNumber;
		this.lineNumber = lineNumber;
		this.activity = activity;
		this.employeeNumber = employeeNumber;
		this.startDate = startDate;
		this.endDate = endDate;
		this.recordProcessed = recordProcessed;
		this.recordRead = recordRead;
	}

	public ServiceLaborTrackingRecord() {
		super();
	}

	public String getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(String orderNumber) {
		this.orderNumber = orderNumber;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public int getActivity() {
		return activity;
	}

	public void setActivity(int activity) {
		this.activity = activity;
	}

	public String getEmployeeNumber() {
		return employeeNumber;
	}

	public void setEmployeeNumber(String employeeNumber) {
		this.employeeNumber = employeeNumber;
	}

	public Timestamp getStartDate() {
		return startDate;
	}

	public void setStartDate(Timestamp startDate) {
		this.startDate = startDate;
	}

	public Timestamp getEndDate() {
		return endDate;
	}

	public void setEndDate(Timestamp endDate) {
		this.endDate = endDate;
	}

	public int getRecordProcessed() {
		return recordProcessed;
	}

	public void setRecordProcessed(int recordProcessed) {
		this.recordProcessed = recordProcessed;
	}

	public int getRecordRead() {
		return recordRead;
	}

	public void setRecordRead(int recordRead) {
		this.recordRead = recordRead;
	}

	public Timestamp getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Timestamp transactionDate) {
		this.transactionDate = transactionDate;
	}

	public Date getStartModifyDate() {
		return startModifyDate;
	}

	public void setStartModifyDate(Date startModifyDate) {
		this.startModifyDate = startModifyDate;
	}

	public Date getEndModifyDate() {
		return endModifyDate;
	}

	public void setEndModifyDate(Date endModifyDate) {
		this.endModifyDate = endModifyDate;
	}

	public Timestamp getLocalTimeStartDate() {
		return localTimeStartDate;
	}

	public void setLocalTimeStartDate(Timestamp localTimeStartDate) {
		this.localTimeStartDate = localTimeStartDate;
	}

	public Timestamp getLocalTimeEndDate() {
		return localTimeEndDate;
	}

	public void setLocalTimeEndDate(Timestamp localTimeEndDate) {
		this.localTimeEndDate = localTimeEndDate;
	}

}
