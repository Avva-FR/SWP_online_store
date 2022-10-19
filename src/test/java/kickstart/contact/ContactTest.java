package kickstart.contact;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class ContactTest {
	@Autowired
	MockMvc mvc;

	@Test
	void showsLegalNotice() throws Exception {
		mvc.perform(get("/impressum"))
				.andExpect(status().isOk())
				.andExpect(view().name("impressum"));
	}
}
