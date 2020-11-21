import javax.persistence.*;
import java.util.List;

@Entity
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "institution_id")
    private Integer id;

    public void setCards(List<Card> cards) {
        this.cards = cards;
    }

    public List<Card> getCards() {
        return cards;
    }

    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    @ManyToMany(mappedBy = "institution")
    private List<Card> cards;

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
