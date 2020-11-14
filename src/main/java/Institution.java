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

    //@Column(unique = true)
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    @ManyToMany(mappedBy = "institutions")
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
