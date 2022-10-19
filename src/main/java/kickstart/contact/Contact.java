package kickstart.contact;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Contact {
	@GetMapping("/impressum")
	String impressum(){
		return "impressum";
	}
}
