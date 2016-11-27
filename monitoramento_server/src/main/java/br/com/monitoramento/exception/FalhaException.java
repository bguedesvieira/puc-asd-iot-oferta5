package br.com.monitoramento.exception;

public class FalhaException extends Exception{

	private static final long serialVersionUID = 1L;
	
	private String message;
	private Throwable throwable;
	
	public FalhaException(String message){
		this.message = message;
	}
	
	public FalhaException(String message, Throwable t){
		this.message = message;
		this.throwable = t;
	}
	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Throwable getThrowable() {
		return throwable;
	}
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	
}
