package com.talkdesk.tag.reference.user.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

/**
 * JPA Entity for User.
 * <p>
 * In the JPA Entity, we use Lombok annotations to generate some of the required code and to keep the class clean.
 * <p>
 * For the JPA mapping annotations, please convert the camelCase names into snake_case names. This is to follow
 * Talkdesk naming conventions for database object names.
 */
@Getter
@Setter
@NoArgsConstructor
@Accessors(chain = true)
@ToString

@Entity
@Table(name = "app_user")
@NamedQuery(name = User.FIND_BY_FIRST_NAME,
            query = "SELECT u " +
                    "FROM User u " +
                    "WHERE u.firstName = :firstName")
@NamedQuery(name = User.FIND_BY_FIRST_NAME_AND_LAST_NAME,
            query = "SELECT u " +
                    "FROM User u " +
                    "WHERE u.firstName = :firstName AND u.lastName = :lastName")
public class User {
    public static final String FIND_BY_FIRST_NAME = "findByFirstName";
    public static final String FIND_BY_FIRST_NAME_AND_LAST_NAME = "findByFirstNameAndLastName";

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid")
    private String id;

    @Column(name = "first_name")
    @NotNull
    private String firstName;

    @Column(name = "last_name")
    @NotNull
    private String lastName;

    @NotNull
    @PositiveOrZero
    private Integer age;

    @OneToMany(mappedBy = "user", cascade = ALL)
    private List<Address> addresses;

    /**
     * Constructor for the User object.
     * <p>
     * The Lombok Builder annotation is placed here and not the class itself because we don't want to include the id in
     * the Builder, since the id is auto generated.
     *
     * @param firstName the User firstName.
     * @param lastName  the User lastName.
     * @param age       the User age.
     */
    @Builder
    public User(final String firstName, final String lastName, final Integer age) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
    }

    @Override
    public int hashCode() {
        return 42;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof User)) {
            return false;
        }
        final User other = (User) obj;

        return id != null && id.equals(other.id);
    }
}
