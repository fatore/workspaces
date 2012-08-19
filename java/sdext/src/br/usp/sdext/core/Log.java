package br.usp.sdext.core;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;


@Entity
public class Log extends Model implements Serializable {

	private static final long serialVersionUID = 8653435258560250192L;

	@Id
	@GeneratedValue
	private Long id;
	
	@Column(columnDefinition="text")
	private String line;
	private String cause;
	private String message;
	private String detail;
	
	public Log() {}
	
	public Log(String line, String cause, String message, String detail) {
		super();
		this.line = line;
		this.cause = cause;
		this.message = message;
		this.detail = detail;
	}

	public Log(String message, String detail) {
		
		this.message = message;
		this.detail = detail;
	}

	public Long getId() {return id;}
	public String getLine() {return line;}
	public String getCause() {return cause;}
	public String getMessage() {return message;}
	public String getDetail() {return detail;}

	public void setId(Long id) {}
	public void setLine(String line) {this.line = line;}
	public void setCause(String error) {this.cause = error;}
	public void setMessage(String message) {this.message = message;}
	public void setDetail(String detail) {this.detail = detail;}

	@Override
	public String toString() {
		return "Log [id=" + id + ", line=" + line + ", cause=" + cause
				+ ", message=" + message + "]";
	}
}
