import net.bytebuddy.implementation.bind.annotation.Super;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class Main {
    public static SessionFactory sessionFactory = null;
    public static int choice;
    public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    public static Institution inst = null;

    public static SessionFactory createSessionFactory() {
        StandardServiceRegistry registry = null;
        try {
            registry = new StandardServiceRegistryBuilder().configure().build();
            MetadataSources sources = new MetadataSources(registry);
            Metadata metadata = sources.getMetadataBuilder().build();
            sessionFactory = metadata.getSessionFactoryBuilder().build();
        } catch (Exception e) {
            e.printStackTrace();
            if (registry != null) {
                StandardServiceRegistryBuilder.destroy(registry);
            }
        }
        return sessionFactory;
    }
    public static void main(String[] args) throws IOException {

//        рабочее подключение к DB через jdbc
//        System.out.println("START stp_ORM");
//        String url = "jdbc:mysql://127.0.0.1:3306/misc?serverTimezone=Europe/Minsk&useSSL=false";
//        String username = "fred";
//        String password = "zap";
//        System.out.println("Connecting...");
//
//        Statement statement = null;
//        ResultSet result;
//
//        try (Connection connection = DriverManager.getConnection(url, username, password)) {
//            System.out.println("Connection successful!");
//            String query = "select * from institution";
//            statement = connection.createStatement();
//            result = statement.executeQuery(query);
//            System.out.println(result);
//            while(result.next()) {
//                System.out.println(result.getString("institution_id") + " " + result.getString("name"));
//            }
//
//        } catch (SQLException e) {
//            System.out.println("Connection failed!");
//            e.printStackTrace();
//        }

        //-------------------------------------

        System.out.println("\n----- START PROGRAM -----");


        boolean flag = false;
        try {
            do {
                System.out.println("\n----- Главное меню -----");
                System.out.println("1. Добавить институт.");
                System.out.println("2. Вывести список учебных учреждений.");
                System.out.println("3. UPDATE учебного учреждения.");
                System.out.println("0. Выход.");
                choice = Integer.parseInt(reader.readLine());

                switch (choice) {
                    case 1:
                        inst = new Institution();
                        System.out.print("Enter institution name: ");
                        String myInst = reader.readLine();
                        inst.setName(myInst);
                        Transaction transaction = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transaction = session.beginTransaction();
                            session.save(inst);
                            transaction.commit();
                            System.out.println("SUCCESS ADDED (" + inst.toString() + ")");
                            session.clear();
                            session.close();
                        } catch (Exception e) {
                            if (transaction != null) {
                                transaction.rollback();
                            }
                            e.printStackTrace();
                        }
                        break;
                    case 2:
                        try (Session session = createSessionFactory().openSession()) {
//                            Рабочий select по ID
//                            Institution result = (Institution) session.get(Institution.class, 3);
//                            System.out.println(result);
                            Query query =  session.createQuery("from Institution");
                            List<Institution> list = query.list();
                            for(Institution s : list) {
                                System.out.println(s);
                            }
                            System.out.println("SUCCESS DONE");
                            session.close();

                        }catch (Exception e){
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;
                    case 3:
                        try (Session session = createSessionFactory().openSession()) {
                            Institution inst = new Institution();
                            System.out.print("Enter institution ID: ");
                            int pId = Integer.parseInt(reader.readLine());
                            System.out.print("Enter institution NAME: ");
                            String pName = reader.readLine();

                            transaction = session.beginTransaction();
                            Institution updInst = new Institution(pId, pName);
                            session.update(updInst);

                            transaction.commit();
                            System.out.println("SUCCESS UPDATED");
                            session.close();
                        }catch (Exception e){
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }

                        break;
                    case 0:
                        flag = true;
                        break;
                    default:
                        if (flag == false) System.out.println("Некорректные данные. Повторите ввод!\n");
                        break;
                }

            } while (flag == false);
        } catch (Exception e) {
            System.out.println("Некорректные данные!\nПодробнее: " + e.getMessage());
        }





        System.out.println("\n----- END PROGRAM -----\n");

    }
}
