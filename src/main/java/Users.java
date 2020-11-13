import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.List;

@Entity
public class Users {
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

    public void setCards(Set<Cards> cards) {
        this.cards = cards;
    }

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Cards> cards;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Users() {    }
    public Users(Integer id, String name, String password) {
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
