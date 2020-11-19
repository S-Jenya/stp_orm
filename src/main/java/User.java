import javax.persistence.*;
import java.util.Set;

@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Integer id_user;

    public Integer getId_user() {
        return id_user;
    }

    @Column(unique = true)
    private String name;
    private String password;

    public void setCards(Set<Card> card) {
        this.card = card;
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Card> card;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public User() {    }
    public User(Integer id, String name, String password) {
        this.id_user = id;
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String toString() {
        return "{ Id: " + this.id_user + " name: " + this.name + "; password: " + this.password + " }";
    }
}
