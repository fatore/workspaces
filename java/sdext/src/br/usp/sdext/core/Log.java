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
	
	@Column(columnDefinition="text")
	private String message;
	
	@Column(columnDefinition="text")
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((detail == null) ? 0 : detail.hashCode());
		result = prime * result + ((message == null) ? 0 : message.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Log other = (Log) obj;
		if (detail == null) {
			if (other.detail != null)
				return false;
		} else if (!detail.equals(other.detail))
			return false;
		if (message == null) {
			if (other.message != null)
				return false;
		} else if (!message.equals(other.message))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Log [id=" + id + ", line=" + line + ", cause=" + cause
				+ ", message=" + message + "]";
	}
}
