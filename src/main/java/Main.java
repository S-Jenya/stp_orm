import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import javax.persistence.criteria.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

        System.out.println("\n----- START PROGRAM -----");

        boolean flag = false;
        try {
            do {
                System.out.println("\n----- Главное меню -----");
                System.out.println("1. Добавить институт.");
                System.out.println("2. Вывести список учебных учреждений.");
                System.out.println("3. UPDATE учебного учреждения.");
                System.out.println("4. Delete учебное учреждение.");
                System.out.println("----------");
                System.out.println("5. Insert Users & Cards (OneToMany)  ");
                System.out.println("6. Select Users");
                System.out.println("7. Select Cards(Users)");
                System.out.println("8. Delete User");
                System.out.println("---------\n");
                System.out.println("9. Update User");
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
                            System.out.println("Список учебных учреждений");
//                            Criteria criteria = session.createCriteria(Institution.class);
//                            List<Institution> list = criteria.list();
//                            for(Institution s : list) {
//                                System.out.println(s);
//                            }

//                            Работает!
//                            Institution pinst = (Institution) session.get(Institution.class, 1);
//                            System.out.println(pinst);

                            CriteriaBuilder cbSelect = session.getCriteriaBuilder();
                            CriteriaQuery<Institution> cq = cbSelect.createQuery(Institution.class);
                            Root<Institution> rootEntry = cq.from(Institution.class);
                            cq.select(rootEntry);
                            Query query = session.createQuery(cq);
                            List<Institution> collection2 = query.getResultList();
                            for(Institution s : collection2) {
                                System.out.println(s);
                            }

                            System.out.println("SUCCESS created");
                            session.close();

                        }catch (Exception e){
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;
                    case 3:
                        try (Session session = createSessionFactory().openSession()) {
                            System.out.print("Enter institution ID: ");
                            String pUserName = reader.readLine();
                            System.out.print("Enter institution NAME: ");
                            String pNewName = reader.readLine();

                            CriteriaBuilder cbUpd = session.getCriteriaBuilder();
                            CriteriaUpdate<Institution> criteriaUpdate = cbUpd.createCriteriaUpdate(Institution.class);
                            Root<Institution> root = criteriaUpdate.from(Institution.class);
                            criteriaUpdate.set("name", pNewName);
                            criteriaUpdate.where(cbUpd.equal(root.get("name"), pUserName));

                            Transaction transactionUpd = session.beginTransaction();
                            session.createQuery(criteriaUpdate).executeUpdate();
                            transactionUpd.commit();

                            System.out.println("SUCCESS updated");
                            session.close();
                        }catch (Exception e){
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }

                        break;

                    case 4:
                        try (Session session = createSessionFactory().openSession()) {
                            System.out.print("Enter institution name for DELETE: ");
                            String pInstName = reader.readLine();

                            CriteriaBuilder cbDel = session.getCriteriaBuilder();
                            CriteriaDelete<Institution> criteriaDelete = cbDel.createCriteriaDelete(Institution.class);
                            Root<Institution> root = criteriaDelete.from(Institution.class);
                            criteriaDelete.where(cbDel.equal(root.get("name"), pInstName));

                            Transaction transactionDel = session.beginTransaction();
                            session.createQuery(criteriaDelete).executeUpdate();
                            transactionDel.commit();

                            System.out.println("SUCCESS deleted");
                            session.close();
                        }catch (Exception e){
                            System.out.println("ОШИБКА при DELETE\nПодробнее: " + e.getMessage());
                        }

                        break;

                    case 5:
                        Users users = new Users();
                        System.out.print("Enter User name: ");
                        String pUserName = reader.readLine();
                        users.setName(pUserName);
                        System.out.print("Enter password: ");
                        String pPassword = reader.readLine();
                        users.setPassword(pPassword);

                        Transaction TestTransaction = null;
                        try (Session session = createSessionFactory().openSession()) {
                            TestTransaction = session.beginTransaction();
                            session.save(users);

                            Boolean stopCreateCards = false;
                            Cards cards;
                            List<Cards> myCardsList = new ArrayList<Cards>();
                            do {
                                cards = new Cards();
                                System.out.print("Enter HeadLine: ");
                                String pHeadLine = reader.readLine();
                                cards.setHeadline(pHeadLine);
                                cards.setUsers(users);
                                myCardsList.add(cards);

                                Boolean stopCreateInstCard = false;
                                Institution instCard = new Institution();
                                List<Institution> myInstList = new ArrayList<Institution>();
                                do{
                                    System.out.print("Enter name institution: ");
                                    String pInstName = reader.readLine();
                                    instCard.setName(pInstName);
                                    myInstList.add(instCard);

                                    System.out.println("Желаете ещё добавить институт (1/0)?");
                                    Integer pAnsInstCard = Integer.parseInt(reader.readLine());
                                    if(pAnsInstCard == 0) stopCreateInstCard = true;
                                }while (stopCreateInstCard == false);

                                cards.setInstitutions(myInstList);
                                instCard.setCards(myCardsList);

                                session.save(cards);
                                session.save(instCard);

                                System.out.println("Желаете ещё создать карточку (1/0)?");
                                Integer pAns = Integer.parseInt(reader.readLine());
                                if(pAns == 0) stopCreateCards = true;

                            } while (stopCreateCards == false);


                            System.out.println("--- USER SUCCESS ADDED ---");
                            TestTransaction.commit();
                            System.out.println("--- CARDS SUCCESS ADDED ---");

                            System.out.println("User: " + users.toString());
                            System.out.println("Card1: " + cards.toString());
                            session.clear();
                            session.close();
                        } catch (Exception e) {
                            if (TestTransaction != null) {
                                TestTransaction.rollback();
                            }
                            e.printStackTrace();
                        }
                        break;

                    case 6:
                        try (Session session = createSessionFactory().openSession()) {
                            Query query =  session.createQuery("from Users");
                            List<Users> list = query.list();
                            for(Users s : list) {
                                System.out.println(s);
                            }
                            System.out.println("SUCCESS DONE");
                            session.close();

                        }catch (Exception e){
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;
                    case 7:
                        System.out.println("Введите ID пользователя:");
                        Integer pIdUser = Integer.parseInt(reader.readLine());
                        try (Session session = createSessionFactory().openSession()) {

                            System.out.println("Данные пользователя ID = " + pIdUser);
                            Query query =  session.createQuery("from Users where id_user  = :pId");
                            query.setParameter("pId", pIdUser);
                            System.out.println(query.list());

                            query =  session.createQuery("from Cards where users.id_user  = :pId");
                            query.setParameter("pId", pIdUser);
                            List<Cards> list = query.list();
                            for(Cards s : list) {
                                System.out.println(s);
                            }
                            System.out.println("SUCCESS DONE");
                            session.close();

                        }catch (Exception e){
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;
                    case 8:
                        System.out.println("Введите ID пользователя:");
                        Integer pIdeDel = Integer.parseInt(reader.readLine());
                        try (Session session = createSessionFactory().openSession()) {
                            session.getTransaction().begin();
                            Query query =  session.createQuery("DELETE from Cards where users.id_user = :pIdeDel");
                            query.setParameter("pIdeDel", pIdeDel);
                            query.executeUpdate();
                            System.out.println("Deleted from cards");

                            query =  session.createQuery("DELETE from Users where id_user = :pIdeDel");
                            query.setParameter("pIdeDel", pIdeDel);
                            query.executeUpdate();

                            System.out.println("SUCCESS DONE");
                            session.close();

                        }catch (Exception e){
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;
                    case 9:
                        System.out.println("Update User");
                        try (Session session = createSessionFactory().openSession()) {
                            System.out.print("Enter user name for Update: ");
                            String pUserNameForEdit = reader.readLine();
                            System.out.print("Enter NEW user name: ");
                            String pNewUserName = reader.readLine();

                            CriteriaBuilder cbUpd = session.getCriteriaBuilder();
                            CriteriaUpdate<Users> criteriaUpdate = cbUpd.createCriteriaUpdate(Users.class);
                            Root<Users> root = criteriaUpdate.from(Users.class);
                            criteriaUpdate.set("name", pNewUserName);
                            criteriaUpdate.where(cbUpd.equal(root.get("name"), pUserNameForEdit));

                            Transaction transactionUpd = session.beginTransaction();
                            session.createQuery(criteriaUpdate).executeUpdate();
                            transactionUpd.commit();

                            System.out.println("SUCCESS updated");
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
