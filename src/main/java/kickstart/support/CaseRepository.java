package kickstart.support;

import kickstart.users.Customer;
import kickstart.users.Employee;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CaseRepository extends CrudRepository<Case, Long> {
	List<Case> findByCreator(Customer creator);

	List<Case> findByClosed(boolean closed);


}
