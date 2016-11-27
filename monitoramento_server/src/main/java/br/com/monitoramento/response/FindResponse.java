package br.com.monitoramento.response;

public class FindResponse extends BaseResponse {

	private static final long serialVersionUID = 1L;

	private Object result;
	
	public FindResponse() {	}
	
	public FindResponse(Integer code, String message) {
		super(code,message);
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}
}
