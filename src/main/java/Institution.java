import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
public class Institution {
    @Id
    @GeneratedValue
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
