import javax.persistence.*;
import java.util.List;

@Entity
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_card")
    private Integer id;
    private String headline;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private User user;

    public Integer getId() {
        return id;
    }

    public void setInstitution(List<Institution> institutions) {
        this.institution = institutions;
    }

    public List<Institution> getInstitution() {
        return institution;
    }

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "Card_institution",
            joinColumns = { @JoinColumn(name = "card_id") },
            inverseJoinColumns = { @JoinColumn(name = "institution_id") }
    )
    private List<Institution> institution;

    public void setUsers(User user) {
        this.user = user;
    }

    public User getUsers() {
        return user;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getHeadline() {
        return headline;
    }

    public void removeFrom(List<Institution> pInst){
        this.institution.remove(pInst);
    }

    public Card() {    }

    public Card(Integer id, String headline) {
        this.id = id;
        this.headline = headline;
    }

    public String toString() {
        return "{ Id: " + this.id + "; headline: " + this.headline + "} ";
    }
}
