package kickstart.support;

import kickstart.users.Customer;
import kickstart.users.UserManagement;

import lombok.AllArgsConstructor;

import org.salespointframework.useraccount.Role;
import org.salespointframework.useraccount.UserAccount;
import org.salespointframework.useraccount.web.LoggedIn;

import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
public class SupportController {
	private final CaseRepository caseRepository;
	private final UserManagement userManagement;

	@GetMapping("/contact")
	@PreAuthorize("isAuthenticated()")
	String contact(@LoggedIn Optional<UserAccount> userAccount, Model model, CreateCaseForm createCaseForm) {
		UserAccount user = userAccount.get();
		Optional<Customer> optionalCustomer = userManagement.findCustomer(user);
		if (optionalCustomer.isPresent()) {
			Customer customer = optionalCustomer.get();
			List<Case> cases = caseRepository.findByCreator(customer);
			model.addAttribute("cases", cases);
		} else if (user.hasRole(Role.of("EMPLOYEE")) || user.hasRole(Role.of("BOSS"))) {
			List<Case> cases = caseRepository.findByClosed(false);
			model.addAttribute("cases", cases);
		} else {
			model.addAttribute("cases", new ArrayList<Case>());
		}
		return "contact";
	}

	@PostMapping("/contact")
	@PreAuthorize("isAuthenticated()")
	String saveContact(@LoggedIn Optional<UserAccount> userAccount, Model model, @Valid CreateCaseForm createCaseForm) {
		UserAccount user = userAccount.get();
		Optional<Customer> optionalCustomer = userManagement.findCustomer(user);
		if (optionalCustomer.isPresent()) {
			Customer customer = optionalCustomer.get();
			caseRepository.save(createCaseForm.toCase(customer));

			if (user.hasRole(Role.of("EMPLOYEE")) || user.hasRole(Role.of("BOSS"))) {
				List<Case> cases = caseRepository.findByClosed(false);
				model.addAttribute("cases", cases);
			} else {
				List<Case> cases = caseRepository.findByCreator(customer);
				model.addAttribute("cases", cases);
			}
		} else if (user.hasRole(Role.of("EMPLOYEE")) || user.hasRole(Role.of("BOSS"))) {
			List<Case> cases = caseRepository.findByClosed(false);
			model.addAttribute("cases", cases);
			model.addAttribute("cases", new ArrayList<Case>());
		} else {
			model.addAttribute("cases", new ArrayList<Case>());
		}
		return "contact";
	}

	@GetMapping("/ticket/{id}")
	@PreAuthorize("isAuthenticated()")
	String showTicket(@PathVariable Long id, Model model, AnswerForm answerForm) {
		Optional<Case> supCase = caseRepository.findById(id);
		if (supCase.isEmpty()) {
			throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
		}

		model.addAttribute("case", supCase.get());
		return "contactdetail";
	}

	@PostMapping("/answer/{id}")
	@PreAuthorize("isAuthenticated()")
	String answer(@LoggedIn Optional<UserAccount> userAccount, @PathVariable Long id, Model model,
				  AnswerForm answerForm) {
		Optional<Case> supCase = caseRepository.findById(id);

		if (supCase.isEmpty()) {
			throw new ResponseStatusException(NOT_FOUND, "Unable to find resource");
		}

		UserAccount user = userAccount.get();
		Case foundCase = supCase.get();
		Optional<Customer> optionalCustomer = userManagement.findCustomer(user);
		if (optionalCustomer.isPresent()) {
			if (supCase.get().getCreator().equals(optionalCustomer.get())) {
				foundCase.getMessages().add(answerForm.toMessage(user));
				if (answerForm.isClosed()) {
					foundCase.setClosed(true);
				}
				caseRepository.save(foundCase);
				model.addAttribute("case", foundCase);
			}
		} else if (user.hasRole(Role.of("EMPLOYEE")) || user.hasRole(Role.of("BOSS"))) {
			List<Message> messages = foundCase.getMessages();
			messages.add(answerForm.toMessage(user));
			if (answerForm.isClosed()) {
				foundCase.setClosed(true);
			}
			caseRepository.save(foundCase);
			model.addAttribute("case", foundCase);
		}
		return "redirect:/ticket/{id}";
	}
}
