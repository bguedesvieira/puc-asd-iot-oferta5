package br.com.monitoramento.controller;

import java.lang.reflect.Field;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import br.com.monitoramento.domain.UserDomain;
import br.com.monitoramento.exception.FalhaException;
import br.com.monitoramento.request.ConfigRequest;
import br.com.monitoramento.request.CreateUserRequest;
import br.com.monitoramento.request.LoginRequest;
import br.com.monitoramento.response.BaseResponse;
import br.com.monitoramento.response.FindResponse;

@RestController
@EnableAutoConfiguration
@RequestMapping("/user")
public class MonitorServiceController {

    private Logger logger = Logger.getLogger(MonitorServiceController.class);
	
	@ExceptionHandler(FalhaException.class)
	@ResponseStatus(value = HttpStatus.BAD_REQUEST)
	public @ResponseBody BaseResponse handleException(FalhaException e) {
        logger.error(e.getMessage(), e);
		return new BaseResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
	public @ResponseBody BaseResponse handleException(Exception e) {
        logger.error("Ocorreu um erro inesperado!", e);
		return new BaseResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Ocorreu um erro inesperado!");
	}
	
   
    
    @RequestMapping(method = RequestMethod.POST, path="create")
    @ResponseBody
    public ResponseEntity<BaseResponse> createUser(@RequestBody CreateUserRequest request) throws FalhaException {
    	if(!isValidRequest(request)){
    		BaseResponse response = new BaseResponse(HttpStatus.BAD_REQUEST.value(), "Campos obrigatÃ³rios!");
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    	}
    	UserDomain domain = new UserDomain();
    	domain.save(request);
    	
		BaseResponse response = new BaseResponse(HttpStatus.OK.value(), "Criado com sucesso!");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
    
    @RequestMapping(method = RequestMethod.POST, path="login")
    @ResponseBody
    public ResponseEntity<BaseResponse> login(@RequestBody LoginRequest request) throws FalhaException {
    	if(!isValidRequest(request)){
    		BaseResponse response = new BaseResponse(HttpStatus.BAD_REQUEST.value(), "Campos obrigatÃ³rios!");
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    	}
    	
    	UserDomain domain = new UserDomain();
    	domain.login(request);
    	
		BaseResponse response = new BaseResponse(HttpStatus.OK.value(), "UsuÃ¡rio autenticado com sucesso!");
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
    /*
    @RequestMapping(method=RequestMethod.GET, path="config")
	@ResponseBody
	public ResponseEntity<FindResponse> listConfigs(@RequestBody String user) throws FalhaException{
    	UserDomain domain = new UserDomain();
    	if(user == null || user.isEmpty()){
            FindResponse response = new FindResponse(HttpStatus.BAD_REQUEST.value(), "Campos obrigatórios!");
            return (ResponseEntity<FindResponse>) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    	
		List<ConfigRequest> list = domain.getConfigs(user);
		String message = !list.isEmpty() ? list.size() + " resultado(s) encontrado(s)." : "Pesquisa não retornou nenhum resultado!";
		
		FindResponse response = new FindResponse();
		response.setCode(HttpStatus.OK.value());
		response.setResult(list);
		response.setMessage(message);
		
		return ResponseEntity.ok().body(response);
	}
	*/
    
    @RequestMapping(method=RequestMethod.GET, path="config")
	@ResponseBody
	public ResponseEntity<FindResponse> getConfig() throws FalhaException{
    	UserDomain domain = new UserDomain();
//    	if(user == null || user.isEmpty()){
//            FindResponse response = new FindResponse(HttpStatus.BAD_REQUEST.value(), "Campos obrigatórios!");
//            return (ResponseEntity<FindResponse>) ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
//        }
    	
    	ConfigRequest config = UserDomain.getConfig();
		String message = !(config == null) ? " resultado encontrado." : "Pesquisa não retornou nenhum resultado!";
		
		FindResponse response = new FindResponse();
		response.setCode(HttpStatus.OK.value());
		response.setResult(config);
		response.setMessage(message);
		
		return ResponseEntity.ok().body(response);
    }
    
    @RequestMapping(method = RequestMethod.POST, path="config")
    @ResponseBody
    public ResponseEntity<BaseResponse> config(@RequestBody ConfigRequest request) throws FalhaException {
        if(!isValidRequest(request)){
            BaseResponse response = new BaseResponse(HttpStatus.BAD_REQUEST.value(), "Campos obrigatórios!");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        UserDomain domain = new UserDomain();
        domain.config(request);

        BaseResponse response = new BaseResponse(HttpStatus.OK.value(), "Configurações alteradas com sucesso!");
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
    
    private boolean isValidRequest(Object obj){
    	if(obj == null)
    		return false;
    	
    	try{
			for (Field f : obj.getClass().getDeclaredFields()) {
				f.setAccessible(true);
				if (f.get(obj) == null || f.get(obj).toString().isEmpty()) {
					return false;
				}
			}
    	} catch(Exception e){
			return false;
		}
		
		return true;
    }
}
