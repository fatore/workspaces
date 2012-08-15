package br.usp.sdext.util;

public class ParseException extends Exception {

	private static final long serialVersionUID = 1610020117392557110L;
	
	private String message;
	private String detail;
	
	public ParseException(String message, String detail) {
		
		this.message = message;
		this.detail = detail;
	}
	
	public ParseException(String message) {
		
		this.message = message;
		this.detail = null;
	}

	public String getMessage() {
		return message;
	}

	public String getDetail() {
		return detail;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}
}
