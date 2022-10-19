package kickstart.support;

import kickstart.users.Customer;
import kickstart.users.Employee;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "supportcases")
@Builder
public class Case {

	@Id
	@Column(nullable = false)
	@GeneratedValue
	private Long id;

	private String title;

	boolean closed;

	@ManyToOne(fetch = FetchType.LAZY)
	private Customer creator;

	@OneToMany(cascade = CascadeType.ALL)
	private List<Message> messages = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public Customer getCreator() {
		return creator;
	}


	public Case(String title, Customer creator, List<Message> messages) {
		this.title = title;
		this.creator = creator;
		this.messages = messages;
		closed = false;
	}

	public List<Message> getMessagesChrono(){
		ArrayList<Message> clone = new ArrayList<>(getMessages());
		clone.sort(Comparator.comparing(Message::getTimeSent));

		return clone;
	}
}
