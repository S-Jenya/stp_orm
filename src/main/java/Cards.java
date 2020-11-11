import javax.persistence.*;

@Entity
public class Cards {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String headline;

    @ManyToOne
    @JoinColumn(name = "id_user", nullable = false)
    private Users users;

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
