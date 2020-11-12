import javax.persistence.*;
import java.util.List;

@Entity
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institution_id")
    private Integer id;

    public void setCards(List<Cards> employees) {
        this.Cards = employees;
    }

    public List<Cards> getCards() {
        return Cards;
    }

    @Column(unique = true)
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @ManyToMany(cascade = { CascadeType.ALL })
    @JoinTable(
            name = "Card_institution",
            joinColumns = { @JoinColumn(name = "institution_id") },
            inverseJoinColumns = { @JoinColumn(name = "card_id") }
    )
    private List<Cards> Cards;

    public Institution() {    }
    public Institution(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String toString() {
        return "{name: " + this.name + "}";
    }

}
