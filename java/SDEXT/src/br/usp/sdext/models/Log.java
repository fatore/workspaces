package br.usp.sdext.models;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import br.usp.sdext.core.Model;

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
	
	public Log() {}
	
	public Log(String line, String cause, String message) {
		
		this.line = line;
		this.cause = cause;
		this.message = message;
	}

	public Long getId() {return id;}
	public String getLine() {return line;}
	public String getCause() {return cause;}
	public String getMessage() {return message;}

	public void setId(Long id) {}
	public void setLine(String line) {this.line = line;}
	public void setCause(String error) {this.cause = error;}
	public void setMessage(String message) {this.message = message;}
}
