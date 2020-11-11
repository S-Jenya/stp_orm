import javax.persistence.*;

@Entity
public class Institution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

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
