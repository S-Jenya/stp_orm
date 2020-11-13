import com.sun.jmx.mbeanserver.Repository;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.Query;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
                System.out.println("7. Select Cards(UserName)");
                System.out.println("8. Delete User");
                System.out.println("---------");
                System.out.println("9. Update User");
                System.out.println("-10. Update Instiution of Cards");
                System.out.println("11. Update Card");
                System.out.println("12. Delete Card");
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
                            System.out.print("Enter User name: ");
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
                                Institution instCard = null;
                                List<Institution> myInstList = new ArrayList<Institution>();
                                do{
                                    instCard = new Institution();
                                    System.out.print("Enter name institution: ");
                                    String pInstName = reader.readLine();
                                    instCard.setName(pInstName);
                                    myInstList.add(instCard);

                                    System.out.println("Желаете ещё добавить институт (1/0)?");
                                    Integer pAnsInstCard = Integer.parseInt(reader.readLine());
                                    if(pAnsInstCard == 0) stopCreateInstCard = true;
                                }while (stopCreateInstCard == false);

                                cards.setInstitutions(myInstList);
                                //instCard.setCards(myCardsList);

                                session.save(cards);
                              //  session.save(instCard);

                                System.out.println("Желаете ещё создать карточку (1/0)?");
                                Integer pAns = Integer.parseInt(reader.readLine());
                                if(pAns == 0) stopCreateCards = true;

                            } while (stopCreateCards == false);

                            TestTransaction.commit();
                            System.out.println("--- SUCCESS ADDED ---");
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
                            CriteriaBuilder cbSelect = session.getCriteriaBuilder();
                            CriteriaQuery<Users> cq = cbSelect.createQuery(Users.class);
                            Root<Users> rootEntry = cq.from(Users.class);
                            cq.select(rootEntry);
                            Query query = session.createQuery(cq);
                            List<Users> collection = query.getResultList();

                            System.out.println("Список пользователей");
                            for(Users s : collection) {
                                System.out.println(s);
                            }

                            System.out.println("SUCCESS");
                            session.close();

                        }catch (Exception e){
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;
                    case 7:

                        System.out.print("Enter User name: ");
                        String pUserNameForCard = reader.readLine();

                        try (Session session = createSessionFactory().openSession()) {

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pUserNameForCard));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("ВЫВОД (результат поиска select): " + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                CriteriaQuery<Cards> getUserCards = cb.createQuery(Cards.class);
                                Root<Cards> my = getUserCards.from(Cards.class);
                                getUserCards.select(my);
                                getUserCards.where(cb.equal(my.get("users"), userIdis));
                                Query<Cards> query2 = session.createQuery(getUserCards);
                                List<Cards> resultCards = query2.getResultList();

                                System.out.println("Список карточек пользователя ("+ pUserNameForCard +") ");
                                for(Cards s : resultCards) {
                                    System.out.println(s);
                                }
                            } else {
                                System.out.println("Пользователь с данным UserName ("+ pUserNameForCard +") не найден!");
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
                    case 10:
                        System.out.println("Update Institution Card");
                        System.out.print("Enter User name: ");
                        String pUserCard = reader.readLine();

                        try (Session session = createSessionFactory().openSession()) {

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pUserCard));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("ВЫВОД (результат поиска select): " + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                // поиск Id карточки
                                System.out.print("Enter HeadLine: ");
                                String pHeadLine = reader.readLine();
                                CriteriaQuery<Cards> getUserCard = cb.createQuery(Cards.class);
                                Root<Cards> card = getUserCard.from(Cards.class);
                                getUserCard.select(card);
                                getUserCard.where(cb.equal(card.get("headline"), pHeadLine));
                                Query<Cards> queryUserCard = session.createQuery(getUserCard);
                                List<Cards> results = queryUserCard.getResultList();

                                if (results.size() != 0) {
                                    Integer cardIdis = results.get(0).getId();
                                    System.out.println("Id карточки: " + cardIdis);

                                    // поиск Id Института
                                    System.out.print("Enter OLD institution NAME: ");
                                    String pOldInstName = reader.readLine();
                                    CriteriaQuery<Institution> getUpdInst = cb.createQuery(Institution.class);
                                    Root<Institution> inst = getUpdInst.from(Institution.class);
                                    getUpdInst.select(inst);
                                    getUpdInst.where(cb.equal(inst.get("name"), pOldInstName));
                                    Query<Institution> queryUserCardInst = session.createQuery(getUpdInst);
                                    List<Institution> resultsInst = queryUserCardInst.getResultList();

                                    if (resultsInst.size() != 0) {
                                        Integer instIdis = results.get(0).getId();
                                        System.out.println("Id карточки: " + instIdis);

                                        System.out.print("Enter NEW institution NAME: ");
                                        String pNesInstName = reader.readLine();

                                        CriteriaUpdate<Institution> instUpdate = cb.createCriteriaUpdate(Institution.class);
                                        Root<Institution> root = instUpdate.from(Institution.class);
                                        instUpdate.set("name", pNesInstName);
                                        instUpdate.where(cb.equal(root.get("id_institution"), instIdis));

                                        Transaction transactionUpd = session.beginTransaction();
                                        session.createQuery(instUpdate).executeUpdate();
                                        transactionUpd.commit();
                                        System.out.println("SUCCESS updated");
                                    } else {
                                        System.out.println("Картачка с данным headline ("+ pOldInstName +") не найдена!");
                                    }

                                } else {
                                    System.out.println("Картачка с данным headline ("+ pHeadLine +") не найдена!");
                                }

                            } else {
                                System.out.println("Пользователь с данным UserName ("+ pUserCard +") не найден!");
                            }

                            session.close();
                        }catch (Exception e){
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 11:
                        System.out.println("Update Card");
                        System.out.print("Enter User name: ");
                        String pUserNameForUpdCard = reader.readLine();

                        try (Session session = createSessionFactory().openSession()) {

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pUserNameForUpdCard));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("ВЫВОД (результат поиска select): " + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                System.out.print("Enter OLD headline: ");
                                String strOld = reader.readLine();

                                System.out.print("Enter NEW headline: ");
                                String strNew = reader.readLine();

                                CriteriaBuilder cardUpd = session.getCriteriaBuilder();
                                CriteriaUpdate<Cards> criteriaUpdate = cardUpd.createCriteriaUpdate(Cards.class);
                                Root<Cards> my = criteriaUpdate.from(Cards.class);
                                criteriaUpdate.set("headline", strNew);

                                Predicate p1 = cardUpd.equal(my.get("users"), userIdis);
                                Predicate p2 = cardUpd.equal(my.get("headline"), strOld);
                                criteriaUpdate.where(cardUpd.and(p1, p2));


                                Transaction transactionUpd = session.beginTransaction();
                                session.createQuery(criteriaUpdate).executeUpdate();
                                transactionUpd.commit();

                                // вывод результата
                                CriteriaQuery<Cards> getUserCards = cb.createQuery(Cards.class);
                                Root<Cards> my2 = getUserCards.from(Cards.class);
                                getUserCards.select(my2);
                                getUserCards.where(cb.equal(my2.get("users"), userIdis));
                                Query<Cards> query2 = session.createQuery(getUserCards);
                                List<Cards> resultCards = query2.getResultList();

                                System.out.println("Список карточек пользователя ("+ pUserNameForUpdCard +") ");
                                for(Cards s : resultCards) {
                                    System.out.println(s);
                                }

                                System.out.println("SUCCESS updated");

                            } else {
                                System.out.println("Пользователь с данным UserName ("+ pUserNameForUpdCard +") не найден!");
                            }

                            System.out.println("SUCCESS DONE");
                            session.close();

                        }catch (Exception e){
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;
                    case 12:

                        // then merge() and flush()
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
