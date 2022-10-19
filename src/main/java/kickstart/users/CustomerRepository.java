package kickstart.users;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.util.Streamable;

public interface CustomerRepository extends CrudRepository<Customer, Long> {
	/**
	 * Re-declared {@link CrudRepository#findAll()} to return a {@link Streamable} instead of {@link Iterable}.
	 */
	@Override
	Streamable<Customer> findAll();
}