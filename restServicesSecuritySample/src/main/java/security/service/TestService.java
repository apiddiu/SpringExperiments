package security.service;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestService {

	@RequestMapping(value="/hello", method=RequestMethod.GET)
	public @ResponseBody String hello(){
		return "Hello!!";
	}
	
	@Secured({"ROLE_ADMIN"})
	@RequestMapping(value="/admin/hello", method=RequestMethod.GET)
	public @ResponseBody String helloAdmin(){
		return "Hello Admin!!";
	}
	
	@Secured({"ROLE_SUPERUSER", "ROLE_ADMIN"})
	@RequestMapping(value="/superuser/hello", method=RequestMethod.GET)
	public @ResponseBody String helloPippo(){
		return "Hello Superuser!!";
	}
}
