import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Cards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_card")
    private Integer id;
    private String headline;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Users users;

    public void setInstitutions(List<Institution> institutions) {
        this.institutions = institutions;
    }

    public List<Institution> getInstitutions() {
        return institutions;
    }

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "Card_institution",
            joinColumns = { @JoinColumn(name = "card_id") },
            inverseJoinColumns = { @JoinColumn(name = "institution_id") }
    )
    private List<Institution> institutions;

    public void setUsers(Users users) {
        this.users = users;
    }

    public Users getUsers() {
        return users;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getHeadline() {
        return headline;
    }


    public Cards() {    }

    public Cards(Integer id, String headline) {
        this.id = id;
        this.headline = headline;
    }

    public String toString() {
        return "{ Id: " + this.id + "; headline: " + this.headline + "} ";
    }
}
