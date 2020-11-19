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
                System.out.println("1. Insert Users & Cards & Institutions");
                System.out.println("2. Select Users");
                System.out.println("3. Select Cards(UserName)");
                System.out.println("4. Delete User");
                System.out.println("---------");
                System.out.println("9. Update User");
                System.out.println("10. Update Instiutions in Cards");
                System.out.println("11. Update Card");
                System.out.println("12. Delete Card");
                System.out.println("---------");
                System.out.println("20. Select institution");
                System.out.println("21. Delete institution");
                System.out.println("---------");
                System.out.println("30. Insert User only");
                System.out.println("31. Insert Сard for User");
                System.out.println("32. Insert Institution in Card");
                System.out.println("---------");
                System.out.println("0. Выход.");
                System.out.println("---------\nВыберите действие: ");
                choice = Integer.parseInt(reader.readLine());

                switch (choice) {

                    case 1:
                        Users users = new Users();
                        System.out.print("Enter User name: ");
                        String pUserName = reader.readLine();
                        users.setName(pUserName);
                        System.out.print("Enter password: ");
                        String pPassword = reader.readLine();
                        users.setPassword(pPassword);

                        Transaction testTransaction = null;
                        try (Session session = createSessionFactory().openSession()) {
                            testTransaction = session.beginTransaction();
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
                                do {
                                    instCard = new Institution();
                                    System.out.print("Enter name institution: ");
                                    String pInstName = reader.readLine();
                                    instCard.setName(pInstName);
                                    myInstList.add(instCard);

                                    System.out.println("Желаете ещё добавить институт (1/0)?");
                                    Integer pAnsInstCard = Integer.parseInt(reader.readLine());
                                    if (pAnsInstCard == 0) stopCreateInstCard = true;
                                } while (stopCreateInstCard == false);

                                cards.setInstitutions(myInstList);
                                session.save(cards);

                                System.out.println("Желаете ещё создать карточку (1/0)?");
                                Integer pAns = Integer.parseInt(reader.readLine());
                                if (pAns == 0) stopCreateCards = true;

                            } while (stopCreateCards == false);

                            testTransaction.commit();
                            System.out.println("--- SUCCESS ADDED ---");
                            session.clear();
                            session.close();
                        } catch (Exception e) {
                            if (testTransaction != null) {
                                testTransaction.rollback();
                            }
                            e.printStackTrace();
                        }
                        break;

                    case 2:
                        try (Session session = createSessionFactory().openSession()) {
                            CriteriaBuilder cbSelect = session.getCriteriaBuilder();
                            CriteriaQuery<Users> cq = cbSelect.createQuery(Users.class);
                            Root<Users> rootEntry = cq.from(Users.class);
                            cq.select(rootEntry);
                            Query query = session.createQuery(cq);
                            List<Users> collection = query.getResultList();

                            System.out.println("Список пользователей");
                            for (Users s : collection) {
                                System.out.println(s);
                            }

                            System.out.println("SUCCESS");
                            session.close();

                        } catch (Exception e) {
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 3:
                        System.out.print("Enter User name: ");
                        String pUserNameForCard = reader.readLine();

                        try (Session session = createSessionFactory().openSession()) {

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> usersRoot = getUser.from(Users.class);
                            getUser.select(usersRoot);
                            getUser.where(cb.equal(usersRoot.get("name"), pUserNameForCard));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("ВЫВОД (результат поиска select): " + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                CriteriaQuery<Cards> getUserCards = cb.createQuery(Cards.class);
                                Root<Cards> myRoot = getUserCards.from(Cards.class);
                                getUserCards.select(myRoot);
                                getUserCards.where(cb.equal(myRoot.get("users"), userIdis));
                                Query<Cards> query2 = session.createQuery(getUserCards);
                                List<Cards> resultCards = query2.getResultList();

                                System.out.println("Список карточек пользователя (" + pUserNameForCard + ") ");
                                for (Cards s : resultCards) {
                                    System.out.println(s);
                                }
                            } else {
                                System.out.println("Пользователь с данным UserName (" + pUserNameForCard + ") не найден!");
                            }

                            System.out.println("SUCCESS DONE");
                            session.close();

                        } catch (Exception e) {
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 4:
                        try (Session session = createSessionFactory().openSession()) {

                            CriteriaBuilder cbSelect = session.getCriteriaBuilder();
                            CriteriaQuery<Users> cbSelectQuery = cbSelect.createQuery(Users.class);
                            Root<Users> rootEntry = cbSelectQuery.from(Users.class);
                            cbSelectQuery.select(rootEntry);
                            Query query = session.createQuery(cbSelectQuery);
                            List<Users> collection = query.getResultList();

                            System.out.println("Список пользователей");
                            for (Users s : collection) {
                                System.out.println(s);
                            }

                            System.out.print("Enter user name for DELETE: ");
                            String pUsName = reader.readLine();

                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> usersRoot = getUser.from(Users.class);
                            getUser.select(usersRoot);
                            getUser.where(cb.equal(usersRoot.get("name"), pUsName));
                            Query<Users> querySelect = session.createQuery(getUser);
                            List<Users> resultsUser = querySelect.getResultList();
                            Integer userId = resultsUser.get(0).getId_user();
                            if (resultsUser.size() != 0) {

                                CriteriaBuilder cbDel = session.getCriteriaBuilder();
                                CriteriaDelete<Cards> criteriaDelete = cbDel.createCriteriaDelete(Cards.class);
                                Root<Cards> root = criteriaDelete.from(Cards.class);
                                criteriaDelete.where(cbDel.equal(root.get("users"), userId));

                                Transaction transactionDel = session.beginTransaction();
                                session.createQuery(criteriaDelete).executeUpdate();
                                transactionDel.commit();


                                CriteriaBuilder cbDel2 = session.getCriteriaBuilder();
                                CriteriaDelete<Users> criteriaDelete2 = cbDel2.createCriteriaDelete(Users.class);
                                Root<Users> root2 = criteriaDelete2.from(Users.class);
                                criteriaDelete2.where(cbDel2.equal(root2.get("id_user"), userId));

                                Transaction tranDelUs = session.beginTransaction();
                                session.createQuery(criteriaDelete2).executeUpdate();
                                tranDelUs.commit();

                                System.out.println("SUCCESS DONE");

                            } else {
                                System.out.println("Пользователь с именем " + pUsName + " не найден");
                            }


                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при DELETE\nПодробнее: " + e.getMessage());
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
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 10:
                        System.out.println("Update Institution Card");
                        System.out.print("Enter User name: ");
                        String pUserCard2 = reader.readLine();

                        Transaction transactionDel = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transactionDel = session.beginTransaction();

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pUserCard2));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("Пользователь " + pUserCard2 + ": \n" + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();

                                CriteriaBuilder cbGetUserCard = session.getCriteriaBuilder();
                                CriteriaQuery<Cards> getUserCard2 = cbGetUserCard.createQuery(Cards.class);
                                Root<Cards> cRoot = getUserCard2.from(Cards.class);
                                getUserCard2.select(cRoot);
                                getUserCard2.where(cbGetUserCard.equal(cRoot.get("users"), userIdis));
                                Query<Cards> query2 = session.createQuery(getUserCard2);
                                List<Cards> resultsUserCard = query2.getResultList();
                                System.out.println("Карточки у пользователя" + pUserCard2 + ":");
                                for (Cards uCars : resultsUserCard)
                                    System.out.println("name: " + uCars.getHeadline());

                                // поиск Id карточки
                                System.out.print("Enter HeadLine: ");
                                String pHeadLine = reader.readLine();
                                CriteriaQuery<Cards> getUserCard = cb.createQuery(Cards.class);
                                Root<Cards> card = getUserCard.from(Cards.class);
                                getUserCard.select(card);

                                Predicate p1 = cb.equal(card.get("users"), userIdis);
                                Predicate p2 = cb.equal(card.get("headline"), pHeadLine);
                                getUserCard.where(cb.and(p1, p2));

                                Query<Cards> queryUserCard = session.createQuery(getUserCard);
                                Cards resultsCars = queryUserCard.getSingleResult();
                                List<Institution> ins = resultsCars.getInstitutions();

                                System.out.print("Имена институтов пользователя " + pUserCard2 + "\n");
                                for (Institution i : ins) System.out.println(i);

                                Integer cardIdis = resultsCars.getId();
                                System.out.println("Id карточки: " + cardIdis);

                                System.out.print("Enter OLD institution NAME: ");
                                String pOldInstName = reader.readLine();

                                System.out.print("Enter NEW institution NAME: ");
                                String pNesInstName = reader.readLine();

                                boolean isRevers = false;
                                for (Institution i : ins) {
                                    if (pOldInstName.equals(i.getName())) {
                                        i.setName(pNesInstName);
                                        isRevers = true;
                                        System.out.print("Новое имя установлено!");
                                    }
                                }

                                if (isRevers) {
                                    resultsCars.setInstitutions(ins);
                                    session.update(resultsCars);
                                    transactionDel.commit();

                                    System.out.print("Обновлённый список институтов\n");
                                    for (Institution i : ins) System.out.println(i);

                                    System.out.println("SUCCESS");
                                } else {
                                    System.out.print("Введённое имя института (" + pOldInstName + ") не найдено!\n");
                                }

                            } else {
                                System.out.println("Пользователь с данным UserName (" + pUserCard2 + ") не найден!");
                            }

                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 11:
                        System.out.println("Update Card");
                        System.out.println("Enter User name: ");
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

                                System.out.println("Список карточек пользователя (" + pUserNameForUpdCard + ") ");
                                for (Cards s : resultCards) {
                                    System.out.println(s);
                                }

                                System.out.println("SUCCESS updated");

                            } else {
                                System.out.println("Пользователь с данным UserName (" + pUserNameForUpdCard + ") не найден!");
                            }

                            System.out.println("SUCCESS DONE");
                            session.close();

                        } catch (Exception e) {
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;
                    case 12:
                        try (Session session = createSessionFactory().openSession()) {
                            System.out.print("Enter user name: ");
                            String pUsName = reader.readLine();

                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pUsName));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            if (resultsUser.size() != 0) {

                                Integer userIdi = resultsUser.get(0).getId_user();
                                // поиск Id карточки
                                System.out.print("Enter HeadLine: ");
                                String pHeadLine = reader.readLine();
                                CriteriaQuery<Cards> getUserCard = cb.createQuery(Cards.class);
                                Root<Cards> card = getUserCard.from(Cards.class);
                                getUserCard.select(card);

                                Predicate p1 = cb.equal(card.get("users"), userIdi);
                                Predicate p2 = cb.equal(card.get("headline"), pHeadLine);
                                getUserCard.where(cb.and(p1, p2));

                                Query<Cards> queryUserCard = session.createQuery(getUserCard);
                                List<Cards> results = queryUserCard.getResultList();
                                List<Institution> ins = results.get(0).getInstitutions();

                                System.out.print("Вся информация об этой карточке будет адалена! Удалить? (1/0)");
                                Integer myAnsw = Integer.parseInt(reader.readLine());
                                if (myAnsw == 1) {
                                    CriteriaBuilder cbForDel = session.getCriteriaBuilder();
                                    CriteriaDelete<Cards> criteriaDelete = cbForDel.createCriteriaDelete(Cards.class);
                                    Root<Cards> root = criteriaDelete.from(Cards.class);
                                    criteriaDelete.where(cbForDel.equal(root.get("users"), userIdi));

                                    Transaction transactionDel3 = session.beginTransaction();
                                    session.createQuery(criteriaDelete).executeUpdate();
                                    transactionDel3.commit();
                                    System.out.println("SUCCESS DONE");
                                }
                            } else {
                                System.out.println("Пользователь с именем " + pUsName + " не найден");
                            }

                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при DELETE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 20:
                        System.out.println("Select institution");
                        System.out.print("Enter User name: ");
                        String pUs = reader.readLine();

                        try (Session session = createSessionFactory().openSession()) {

                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pUs));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("Данные пользователя " + pUs + ": \n" + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();

                                CriteriaQuery<Cards> getUserCardInfo = cb.createQuery(Cards.class);
                                Root<Cards> cardRoot = getUserCardInfo.from(Cards.class);
                                getUserCardInfo.select(cardRoot);
                                getUserCardInfo.where(cb.equal(cardRoot.get("users"), userIdis));
                                Query<Cards> query2 = session.createQuery(getUserCardInfo);
                                List<Cards> resultsUserCard = query2.getResultList();
                                System.out.println("Карточки у пользователя" + pUs + ": \n" + resultsUserCard);

                                System.out.print("Enter HeadLine: ");
                                String pHeadLine = reader.readLine();
                                CriteriaQuery<Cards> getUserCard = cb.createQuery(Cards.class);
                                Root<Cards> card = getUserCard.from(Cards.class);
                                getUserCard.select(card);

                                Predicate p1 = cb.equal(card.get("users"), userIdis);
                                Predicate p2 = cb.equal(card.get("headline"), pHeadLine);
                                getUserCard.where(cb.and(p1, p2));

                                Query<Cards> queryUserCard = session.createQuery(getUserCard);
                                List<Cards> results = queryUserCard.getResultList();
                                List<Institution> ins = results.get(0).getInstitutions();

                                System.out.print("Имена институтов пользователя " + pUs + "\n");
                                for (Institution i : ins) {
                                    System.out.println(i);
                                }

                            } else {
                                System.out.println("Пользователь с данным UserName (" + pUs + ") не найден!");
                            }

                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 21:
                        System.out.println("Delete institution");
                        System.out.print("Enter User name: ");
                        String pUsCard = reader.readLine();

                        Transaction transactionDelInst = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transactionDelInst = session.beginTransaction();

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pUsCard));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("Данные пользователя " + pUsCard + ": \n" + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                CriteriaQuery<Cards> getUserCardInfo = cb.createQuery(Cards.class);
                                Root<Cards> cardRoot = getUserCardInfo.from(Cards.class);
                                getUserCardInfo.select(cardRoot);
                                getUserCardInfo.where(cb.equal(cardRoot.get("users"), userIdis));
                                Query<Cards> query2 = session.createQuery(getUserCardInfo);
                                List<Cards> resultsUserCard = query2.getResultList();
                                System.out.println("Карточки у пользователя" + pUsCard + ": \n" + resultsUserCard);

                                // поиск Id карточки
                                System.out.print("Enter HeadLine: ");
                                String pHeadLine = reader.readLine();
                                CriteriaQuery<Cards> getUserCard = cb.createQuery(Cards.class);
                                Root<Cards> card = getUserCard.from(Cards.class);
                                getUserCard.select(card);

                                Predicate p1 = cb.equal(card.get("users"), userIdis);
                                Predicate p2 = cb.equal(card.get("headline"), pHeadLine);
                                getUserCard.where(cb.and(p1, p2));

                                Query<Cards> queryUserCard = session.createQuery(getUserCard);
                                Cards results = queryUserCard.getSingleResult();
                                List<Institution> ins = results.getInstitutions();

                                System.out.print("Имена институтов пользователя " + pUsCard + "\n");
                                for (Institution i : ins) System.out.println(i);

                                System.out.print("Enter institution NAME for delete: ");
                                String pDelIame = reader.readLine();

                                Institution instForDel = null;
                                for (Institution ii : ins) {
                                    if (pDelIame.equals(ii.getName())) {
                                        instForDel = ii;
                                    }
                                }
                                if(instForDel != null) {
                                    ins.remove(instForDel);

                                    results.setInstitutions(ins);
                                    session.update(results);
                                    transactionDelInst.commit();

                                    System.out.println("SUCCESS");
                                } else {
                                    System.out.println("Институт с именем "+ pDelIame +" не найден!");
                                }

                            } else {
                                System.out.println("Пользователь с данным UserName (" + pUsCard + ") не найден!");
                            }

                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 30:
                        System.out.println("Insert user only");
                        Users usersOnly = new Users();
                        System.out.print("Enter User name: ");
                        String pUserNameOnly = reader.readLine();
                        usersOnly.setName(pUserNameOnly);
                        System.out.print("Enter password: ");
                        String pPswdOnly = reader.readLine();
                        usersOnly.setPassword(pPswdOnly);

                        Transaction transAddUserOnly = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transAddUserOnly = session.beginTransaction();
                            session.save(usersOnly);
                            transAddUserOnly.commit();
                            System.out.println("--- SUCCESS ---");
                            session.clear();
                            session.close();
                        } catch (Exception e) {
                            if (transAddUserOnly != null) {
                                transAddUserOnly.rollback();
                            }
                            e.printStackTrace();
                        }
                        break;

                    case 31:
                        System.out.print("Insert card only");
                        System.out.print("Enter User name: ");
                        String pUserNameForCardOnly = reader.readLine();

                        Transaction transactionAddCard = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transactionAddCard = session.beginTransaction();

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pUserNameForCardOnly));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("ВЫВОД (результат поиска select): " + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                Boolean stopCreateCards = false;
                                Cards cards;
                                List<Cards> myCardsList = new ArrayList<Cards>();
                                do {
                                    cards = new Cards();
                                    System.out.print("Enter HeadLine: ");
                                    String pHeadLine = reader.readLine();
                                    cards.setHeadline(pHeadLine);
                                    cards.setUsers(resultsUser.get(0));
                                    myCardsList.add(cards);

                                    Boolean stopCreateInstCard = false;
                                    Institution instCard = null;
                                    List<Institution> myInstList = new ArrayList<Institution>();
                                    do {
                                        instCard = new Institution();
                                        System.out.print("Enter name institution: ");
                                        String pInstName = reader.readLine();
                                        instCard.setName(pInstName);
                                        myInstList.add(instCard);

                                        System.out.println("Желаете ещё добавить институт (1/0)?");
                                        Integer pAnsInstCard = Integer.parseInt(reader.readLine());
                                        if (pAnsInstCard == 0) stopCreateInstCard = true;
                                    } while (stopCreateInstCard == false);

                                    cards.setInstitutions(myInstList);
                                    session.save(cards);

                                    System.out.println("Желаете ещё создать карточку (1/0)?");
                                    Integer pAns = Integer.parseInt(reader.readLine());
                                    if (pAns == 0) stopCreateCards = true;

                                } while (stopCreateCards == false);

                                transactionAddCard.commit();
                                System.out.println("--- SUCCESS ADDED ---");
                                session.clear();
                                session.close();


                            } else {
                                System.out.println("Пользователь с данным UserName (" + pUserNameForCardOnly + ") не найден!");
                            }

                            System.out.println("SUCCESS DONE");
                            session.close();

                        } catch (Exception e) {
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 32:
                        System.out.println("Select institution");
                        System.out.print("Enter User name: ");
                        String pNameUsStr = reader.readLine();

                        Transaction transAddInst = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transAddInst = session.beginTransaction();

                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<Users> getUser = cb.createQuery(Users.class);
                            Root<Users> c = getUser.from(Users.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), pNameUsStr));
                            Query<Users> query = session.createQuery(getUser);
                            List<Users> resultsUser = query.getResultList();
                            System.out.println("Данные пользователя " + pNameUsStr + ": \n" + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();

                                CriteriaQuery<Cards> getUserCardInfo = cb.createQuery(Cards.class);
                                Root<Cards> cardRoot = getUserCardInfo.from(Cards.class);
                                getUserCardInfo.select(cardRoot);
                                getUserCardInfo.where(cb.equal(cardRoot.get("users"), userIdis));
                                Query<Cards> query2 = session.createQuery(getUserCardInfo);
                                List<Cards> resultsUserCard = query2.getResultList();
                                System.out.println("Карточки у пользователя " + pNameUsStr + ":");
                                for (Cards card: resultsUserCard) System.out.println("name: " + card.getHeadline());

                                System.out.print("Enter HeadLine: ");
                                String pHeadLine = reader.readLine();
                                CriteriaQuery<Cards> getUserCard = cb.createQuery(Cards.class);
                                Root<Cards> card = getUserCard.from(Cards.class);
                                getUserCard.select(card);

                                Predicate p1 = cb.equal(card.get("users"), userIdis);
                                Predicate p2 = cb.equal(card.get("headline"), pHeadLine);
                                getUserCard.where(cb.and(p1, p2));

                                Query<Cards> queryUserCard = session.createQuery(getUserCard);
                                Cards results = queryUserCard.getSingleResult();
                                List<Institution> ins = results.getInstitutions();

                                System.out.print("Имена институтов пользователя " + pNameUsStr + "\n");
                                for (Institution i : ins) {
                                    System.out.println(i);
                                }

                                boolean flagStop = false;
                                do{
                                    System.out.print("Enter name new institut: ");
                                    String pNewNameInstStr = reader.readLine();
                                    Institution newInst = new Institution();
                                    newInst.setName(pNewNameInstStr);
                                    ins.add(newInst);
                                    System.out.println("Желаете ещё добавить институт (1/0)?");
                                    Integer pAns = Integer.parseInt(reader.readLine());

                                    if (pAns == 0){
                                        flagStop = true;
                                        results.setInstitutions(ins);
                                        session.update(results);
                                        transAddInst.commit();
                                        System.out.println("--- SUCCESS ADDED ---");
                                        System.out.print("Имена институтов пользователя " + pNameUsStr + "\n");
                                        for (Institution i : ins) System.out.println("name: " + i.getName());
                                        session.clear();
                                        session.close();

                                    }
                                }while (flagStop == false);


                            } else {
                                System.out.println("Пользователь с данным UserName (" + pNameUsStr + ") не найден!");
                            }

                            session.close();
                        } catch (Exception e) {
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
