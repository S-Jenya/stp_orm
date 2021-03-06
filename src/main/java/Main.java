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
                        User user = new User();
                        System.out.print("Enter User name: ");
                        String userName = reader.readLine();
                        user.setName(userName);
                        System.out.print("Enter password: ");
                        String passwordStr = reader.readLine();
                        user.setPassword(passwordStr);

                        Transaction testTransaction = null;
                        try (Session session = createSessionFactory().openSession()) {
                            testTransaction = session.beginTransaction();
                            session.save(user);

                            Boolean stopCreateCards = false;
                            Card card;
                            List<Card> myCardList = new ArrayList<Card>();
                            do {
                                card = new Card();
                                System.out.print("Enter HeadLine: ");
                                String headLine = reader.readLine();
                                card.setHeadline(headLine);
                                card.setUser(user);
                                myCardList.add(card);

                                Boolean stopCreateInstCard = false;
                                Institution instCard = null;
                                List<Institution> myInstList = new ArrayList<Institution>();
                                do {
                                    instCard = new Institution();
                                    System.out.print("Enter name institution: ");
                                    String instName = reader.readLine();
                                    instCard.setName(instName);
                                    myInstList.add(instCard);

                                    System.out.println("Желаете ещё добавить институт (1/0)?");
                                    Integer ansInstCard = Integer.parseInt(reader.readLine());
                                    if (ansInstCard == 0) stopCreateInstCard = true;
                                } while (stopCreateInstCard == false);

                                card.setInstitutions(myInstList);
                                session.save(card);

                                System.out.println("Желаете ещё создать карточку (1/0)?");
                                Integer answer = Integer.parseInt(reader.readLine());
                                if (answer == 0) stopCreateCards = true;

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
                            CriteriaQuery<User> cq = cbSelect.createQuery(User.class);
                            Root<User> rootEntry = cq.from(User.class);
                            cq.select(rootEntry);
                            Query query = session.createQuery(cq);
                            List<User> collection = query.getResultList();

                            System.out.println("Список пользователей");
                            for (User userFor : collection) {
                                System.out.println(userFor);
                            }

                            System.out.println("SUCCESS");
                            session.close();

                        } catch (Exception e) {
                            System.out.println("ОШИБКА при выборке данных\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 3:
                        System.out.print("Enter User name: ");
                        String userNameForCard = reader.readLine();

                        try (Session session = createSessionFactory().openSession()) {

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> usersRoot = getUser.from(User.class);
                            getUser.select(usersRoot);
                            getUser.where(cb.equal(usersRoot.get("name"), userNameForCard));
                            Query<User> query = session.createQuery(getUser);
                            List<User> resultsUser = query.getResultList();
                            System.out.println("ВЫВОД (результат поиска select): " + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                CriteriaQuery<Card> getUserCards = cb.createQuery(Card.class);
                                Root<Card> myRoot = getUserCards.from(Card.class);
                                getUserCards.select(myRoot);
                                getUserCards.where(cb.equal(myRoot.get("user"), userIdis));
                                Query<Card> cardQuery = session.createQuery(getUserCards);
                                List<Card> resultCards = cardQuery.getResultList();

                                System.out.println("Список карточек пользователя (" + userNameForCard + ") ");
                                for (Card cardFor : resultCards) {
                                    System.out.println(cardFor);
                                }
                            } else {
                                System.out.println("Пользователь с данным UserName (" + userNameForCard + ") не найден!");
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
                            CriteriaQuery<User> cbSelectQuery = cbSelect.createQuery(User.class);
                            Root<User> rootEntry = cbSelectQuery.from(User.class);
                            cbSelectQuery.select(rootEntry);
                            Query query = session.createQuery(cbSelectQuery);
                            List<User> collection = query.getResultList();

                            System.out.println("Список пользователей");
                            for (User userFor : collection) {
                                System.out.println(userFor);
                            }

                            System.out.print("Enter user name for DELETE: ");
                            String usName = reader.readLine();

                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> usersRoot = getUser.from(User.class);
                            getUser.select(usersRoot);
                            getUser.where(cb.equal(usersRoot.get("name"), usName));
                            Query<User> querySelect = session.createQuery(getUser);
                            List<User> resultsUser = querySelect.getResultList();
                            Integer userId = resultsUser.get(0).getId_user();
                            if (resultsUser.size() != 0) {

                                CriteriaBuilder cbDel = session.getCriteriaBuilder();
                                CriteriaDelete<Card> criteriaDelete = cbDel.createCriteriaDelete(Card.class);
                                Root<Card> root = criteriaDelete.from(Card.class);
                                criteriaDelete.where(cbDel.equal(root.get("user"), userId));

                                Transaction transactionDel = session.beginTransaction();
                                session.createQuery(criteriaDelete).executeUpdate();
                                transactionDel.commit();


                                CriteriaBuilder cbDel2 = session.getCriteriaBuilder();
                                CriteriaDelete<User> criteriaDelete2 = cbDel2.createCriteriaDelete(User.class);
                                Root<User> root2 = criteriaDelete2.from(User.class);
                                criteriaDelete2.where(cbDel2.equal(root2.get("id_user"), userId));

                                Transaction tranDelUs = session.beginTransaction();
                                session.createQuery(criteriaDelete2).executeUpdate();
                                tranDelUs.commit();

                                System.out.println("SUCCESS DONE");

                            } else {
                                System.out.println("Пользователь с именем " + usName + " не найден");
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
                            CriteriaUpdate<User> criteriaUpdate = cbUpd.createCriteriaUpdate(User.class);
                            Root<User> root = criteriaUpdate.from(User.class);
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
                        String userNameForUpdCard = reader.readLine();

                        Transaction transactionDel = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transactionDel = session.beginTransaction();

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> userRoot = getUser.from(User.class);
                            getUser.select(userRoot);
                            getUser.where(cb.equal(userRoot.get("name"), userNameForUpdCard));
                            Query<User> query = session.createQuery(getUser);
                            List<User> resultsUser = query.getResultList();
                            System.out.println("Пользователь " + userNameForUpdCard + ": \n" + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();

                                CriteriaBuilder cbGetUserCard = session.getCriteriaBuilder();
                                CriteriaQuery<Card> getUserCard2 = cbGetUserCard.createQuery(Card.class);
                                Root<Card> cRoot = getUserCard2.from(Card.class);
                                getUserCard2.select(cRoot);
                                getUserCard2.where(cbGetUserCard.equal(cRoot.get("user"), userIdis));
                                Query<Card> cardQuery = session.createQuery(getUserCard2);
                                List<Card> resultsUserCard = cardQuery.getResultList();
                                System.out.println("Карточки у пользователя" + userNameForUpdCard + ":");
                                for (Card card : resultsUserCard) {
                                    System.out.println("name: " + card.getHeadline());
                                }

                                // поиск Id карточки
                                System.out.print("Enter HeadLine: ");
                                String headLine = reader.readLine();
                                CriteriaQuery<Card> getUserCard = cb.createQuery(Card.class);
                                Root<Card> card = getUserCard.from(Card.class);
                                getUserCard.select(card);

                                Predicate predicateFirst = cb.equal(card.get("user"), userIdis);
                                Predicate predicateSecond = cb.equal(card.get("headline"), headLine);
                                getUserCard.where(cb.and(predicateFirst, predicateSecond));

                                Query<Card> queryUserCard = session.createQuery(getUserCard);
                                Card resultsCars = queryUserCard.getSingleResult();
                                List<Institution> ins = resultsCars.getInstitutions();

                                System.out.print("Имена институтов пользователя " + userNameForUpdCard + "\n");
                                for (Institution inst : ins){
                                    System.out.println(inst);
                                }

                                Integer cardIdis = resultsCars.getId();
                                System.out.println("Id карточки: " + cardIdis);

                                System.out.print("Enter OLD institution NAME: ");
                                String oldInstName = reader.readLine();

                                System.out.print("Enter NEW institution NAME: ");
                                String newInstName = reader.readLine();

                                boolean isRevers = false;
                                for (Institution i : ins) {
                                    if (oldInstName.equals(i.getName())) {
                                        i.setName(newInstName);
                                        isRevers = true;
                                        System.out.print("Новое имя установлено!");
                                    }
                                }

                                if (isRevers) {
                                    resultsCars.setInstitutions(ins);
                                    session.update(resultsCars);
                                    transactionDel.commit();

                                    System.out.print("Обновлённый список институтов\n");
                                    for (Institution inst : ins) {
                                        System.out.println(inst);
                                    }

                                    System.out.println("SUCCESS");
                                } else {
                                    System.out.print("Введённое имя института (" + oldInstName + ") не найдено!\n");
                                }

                            } else {
                                System.out.println("Пользователь с данным UserName (" + userNameForUpdCard + ") не найден!");
                            }

                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 11:
                        System.out.println("Update Card");
                        System.out.println("Enter User name: ");
                        String myUserNameForUpdCard = reader.readLine();

                        try (Session session = createSessionFactory().openSession()) {

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();

                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> c = getUser.from(User.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), myUserNameForUpdCard));
                            Query<User> query = session.createQuery(getUser);
                            List<User> resultsUser = query.getResultList();
                            System.out.println("ВЫВОД (результат поиска select): " + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                System.out.print("Enter OLD headline: ");
                                String strOld = reader.readLine();

                                System.out.print("Enter NEW headline: ");
                                String strNew = reader.readLine();

                                CriteriaBuilder cardUpd = session.getCriteriaBuilder();
                                CriteriaUpdate<Card> criteriaUpdate = cardUpd.createCriteriaUpdate(Card.class);
                                Root<Card> my = criteriaUpdate.from(Card.class);
                                criteriaUpdate.set("headline", strNew);

                                Predicate predicateFirst = cardUpd.equal(my.get("user"), userIdis);
                                Predicate predicateSecond = cardUpd.equal(my.get("headline"), strOld);
                                criteriaUpdate.where(cardUpd.and(predicateFirst, predicateSecond));


                                Transaction transactionUpd = session.beginTransaction();
                                session.createQuery(criteriaUpdate).executeUpdate();
                                transactionUpd.commit();

                                // вывод результата
                                CriteriaQuery<Card> getUserCards = cb.createQuery(Card.class);
                                Root<Card> myRoot = getUserCards.from(Card.class);
                                getUserCards.select(myRoot);
                                getUserCards.where(cb.equal(myRoot.get("user"), userIdis));
                                Query<Card> cardQuery = session.createQuery(getUserCards);
                                List<Card> resultCards = cardQuery.getResultList();

                                System.out.println("Список карточек пользователя (" + myUserNameForUpdCard + ") ");
                                for (Card cardFor : resultCards) {
                                    System.out.println(cardFor);
                                }

                                System.out.println("SUCCESS updated");

                            } else {
                                System.out.println("Пользователь с данным UserName (" + myUserNameForUpdCard + ") не найден!");
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
                            String usName = reader.readLine();

                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> c = getUser.from(User.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), usName));
                            Query<User> query = session.createQuery(getUser);
                            List<User> resultsUser = query.getResultList();
                            if (resultsUser.size() != 0) {

                                Integer userIdi = resultsUser.get(0).getId_user();
                                // поиск Id карточки
                                System.out.print("Enter HeadLine: ");
                                String headLine = reader.readLine();
                                CriteriaQuery<Card> getUserCard = cb.createQuery(Card.class);
                                Root<Card> card = getUserCard.from(Card.class);
                                getUserCard.select(card);

                                Predicate predicateFirst = cb.equal(card.get("user"), userIdi);
                                Predicate predicateSecond = cb.equal(card.get("headline"), headLine);
                                getUserCard.where(cb.and(predicateFirst, predicateSecond));

                                Query<Card> queryUserCard = session.createQuery(getUserCard);
                                List<Card> results = queryUserCard.getResultList();
                                List<Institution> ins = results.get(0).getInstitutions();

                                System.out.print("Вся информация об этой карточке будет адалена! Удалить? (1/0)");
                                Integer myAnsw = Integer.parseInt(reader.readLine());
                                if (myAnsw == 1) {
                                    CriteriaBuilder cbForDel = session.getCriteriaBuilder();
                                    CriteriaDelete<Card> criteriaDelete = cbForDel.createCriteriaDelete(Card.class);
                                    Root<Card> root = criteriaDelete.from(Card.class);
                                    criteriaDelete.where(cbForDel.equal(root.get("user"), userIdi));

                                    Transaction transactionDel3 = session.beginTransaction();
                                    session.createQuery(criteriaDelete).executeUpdate();
                                    transactionDel3.commit();
                                    System.out.println("SUCCESS DONE");
                                }
                            } else {
                                System.out.println("Пользователь с именем " + usName + " не найден");
                            }

                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при DELETE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 20:
                        System.out.println("Select institution");
                        System.out.print("Enter User name: ");
                        String userNameForSelectInst = reader.readLine();

                        try (Session session = createSessionFactory().openSession()) {

                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> c = getUser.from(User.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), userNameForSelectInst));
                            Query<User> query = session.createQuery(getUser);
                            List<User> resultsUser = query.getResultList();
                            System.out.println("Данные пользователя " + userNameForSelectInst + ": \n" + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();

                                CriteriaQuery<Card> getUserCardInfo = cb.createQuery(Card.class);
                                Root<Card> cardRoot = getUserCardInfo.from(Card.class);
                                getUserCardInfo.select(cardRoot);
                                getUserCardInfo.where(cb.equal(cardRoot.get("user"), userIdis));
                                Query<Card> query2 = session.createQuery(getUserCardInfo);
                                List<Card> resultsUserCard = query2.getResultList();
                                System.out.println("Карточки у пользователя" + userNameForSelectInst + ": \n" + resultsUserCard);

                                System.out.print("Enter HeadLine: ");
                                String headLine = reader.readLine();
                                CriteriaQuery<Card> getUserCard = cb.createQuery(Card.class);
                                Root<Card> card = getUserCard.from(Card.class);
                                getUserCard.select(card);

                                Predicate predicateFirst = cb.equal(card.get("user"), userIdis);
                                Predicate predicateSecond = cb.equal(card.get("headline"), headLine);
                                getUserCard.where(cb.and(predicateFirst, predicateSecond));

                                Query<Card> queryUserCard = session.createQuery(getUserCard);
                                List<Card> results = queryUserCard.getResultList();
                                List<Institution> ins = results.get(0).getInstitutions();

                                System.out.print("Имена институтов пользователя " + userNameForSelectInst + "\n");
                                for (Institution inst : ins) {
                                    System.out.println(inst);
                                }

                            } else {
                                System.out.println("Пользователь с данным UserName (" + userNameForSelectInst + ") не найден!");
                            }

                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при UPDATE\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 21:
                        System.out.println("Delete institution");
                        System.out.print("Enter User name: ");
                        String userNameDelCard = reader.readLine();

                        Transaction transactionDelInst = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transactionDelInst = session.beginTransaction();

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> userRoot = getUser.from(User.class);
                            getUser.select(userRoot);
                            getUser.where(cb.equal(userRoot.get("name"), userNameDelCard));
                            Query<User> query = session.createQuery(getUser);
                            List<User> resultsUser = query.getResultList();
                            System.out.println("Данные пользователя " + userNameDelCard + ": \n" + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                CriteriaQuery<Card> getUserCardInfo = cb.createQuery(Card.class);
                                Root<Card> cardRoot = getUserCardInfo.from(Card.class);
                                getUserCardInfo.select(cardRoot);
                                getUserCardInfo.where(cb.equal(cardRoot.get("user"), userIdis));
                                Query<Card> query2 = session.createQuery(getUserCardInfo);
                                List<Card> resultsUserCard = query2.getResultList();
                                System.out.println("Карточки у пользователя" + userNameDelCard + ": \n" + resultsUserCard);

                                // поиск Id карточки
                                System.out.print("Enter HeadLine: ");
                                String headLine = reader.readLine();
                                CriteriaQuery<Card> getUserCard = cb.createQuery(Card.class);
                                Root<Card> card = getUserCard.from(Card.class);
                                getUserCard.select(card);

                                Predicate predicateFirst = cb.equal(card.get("user"), userIdis);
                                Predicate predicateSecond = cb.equal(card.get("headline"), headLine);
                                getUserCard.where(cb.and(predicateFirst, predicateSecond));

                                Query<Card> queryUserCard = session.createQuery(getUserCard);
                                Card results = queryUserCard.getSingleResult();
                                List<Institution> ins = results.getInstitutions();

                                System.out.print("Имена институтов пользователя " + userNameDelCard + "\n");
                                for (Institution inst : ins){
                                    System.out.println(inst);
                                }

                                System.out.print("Enter institution NAME for delete: ");
                                String instNameForDel = reader.readLine();

                                Institution instForDel = null;
                                for (Institution ii : ins) {
                                    if (instNameForDel.equals(ii.getName())) {
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
                                    System.out.println("Институт с именем "+ instNameForDel +" не найден!");
                                }

                            } else {
                                System.out.println("Пользователь с данным UserName (" + userNameDelCard + ") не найден!");
                            }

                            session.close();
                        } catch (Exception e) {
                            System.out.println("ОШИБКА при DEL института\nПодробнее: " + e.getMessage());
                        }
                        break;

                    case 30:
                        System.out.println("Insert user only");
                        User userOnly = new User();
                        System.out.print("Enter User name: ");
                        String userNameOnly = reader.readLine();
                        userOnly.setName(userNameOnly);
                        System.out.print("Enter password: ");
                        String userPassOnly = reader.readLine();
                        userOnly.setPassword(userPassOnly);

                        Transaction transAddUserOnly = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transAddUserOnly = session.beginTransaction();
                            session.save(userOnly);
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
                        String userNameForCardOnly = reader.readLine();

                        Transaction transactionAddCard = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transactionAddCard = session.beginTransaction();

                            // беру ID пользователя
                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> c = getUser.from(User.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), userNameForCardOnly));
                            Query<User> query = session.createQuery(getUser);
                            List<User> resultsUser = query.getResultList();
                            System.out.println("ВЫВОД (результат поиска select): " + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();
                                System.out.println("Id пользователя: " + userIdis);

                                Boolean stopCreateCards = false;
                                Card card;
                                List<Card> cardList = new ArrayList<Card>();
                                do {
                                    card = new Card();
                                    System.out.print("Enter HeadLine: ");
                                    String pHeadLine = reader.readLine();
                                    card.setHeadline(pHeadLine);
                                    card.setUser(resultsUser.get(0));
                                    cardList.add(card);

                                    Boolean stopCreateInstCard = false;
                                    Institution instCard = null;
                                    List<Institution> myInstList = new ArrayList<Institution>();
                                    do {
                                        instCard = new Institution();
                                        System.out.print("Enter name institution: ");
                                        String instName = reader.readLine();
                                        instCard.setName(instName);
                                        myInstList.add(instCard);

                                        System.out.println("Желаете ещё добавить институт (1/0)?");
                                        Integer answerInstCard = Integer.parseInt(reader.readLine());
                                        if (answerInstCard == 0) stopCreateInstCard = true;
                                    } while (stopCreateInstCard == false);

                                    card.setInstitutions(myInstList);
                                    session.save(card);

                                    System.out.println("Желаете ещё создать карточку (1/0)?");
                                    Integer answer = Integer.parseInt(reader.readLine());
                                    if (answer == 0) stopCreateCards = true;

                                } while (stopCreateCards == false);

                                transactionAddCard.commit();
                                System.out.println("--- SUCCESS ADDED ---");
                                session.clear();
                                session.close();


                            } else {
                                System.out.println("Пользователь с данным UserName (" + userNameForCardOnly + ") не найден!");
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
                        String nameUsStr = reader.readLine();

                        Transaction transAddInst = null;
                        try (Session session = createSessionFactory().openSession()) {
                            transAddInst = session.beginTransaction();

                            CriteriaBuilder cb = session.getCriteriaBuilder();
                            CriteriaQuery<User> getUser = cb.createQuery(User.class);
                            Root<User> c = getUser.from(User.class);
                            getUser.select(c);
                            getUser.where(cb.equal(c.get("name"), nameUsStr));
                            Query<User> query = session.createQuery(getUser);
                            List<User> resultsUser = query.getResultList();
                            System.out.println("Данные пользователя " + nameUsStr + ": \n" + resultsUser);

                            if (resultsUser.size() != 0) {
                                Integer userIdis = resultsUser.get(0).getId_user();

                                CriteriaQuery<Card> getUserCardInfo = cb.createQuery(Card.class);
                                Root<Card> cardRoot = getUserCardInfo.from(Card.class);
                                getUserCardInfo.select(cardRoot);
                                getUserCardInfo.where(cb.equal(cardRoot.get("user"), userIdis));
                                Query<Card> cardQuery = session.createQuery(getUserCardInfo);
                                List<Card> resultsUserCard = cardQuery.getResultList();
                                System.out.println("Карточки у пользователя " + nameUsStr + ":");
                                for (Card card: resultsUserCard){
                                    System.out.println("name: " + card.getHeadline());
                                }

                                System.out.print("Enter HeadLine: ");
                                String headLine = reader.readLine();
                                CriteriaQuery<Card> getUserCard = cb.createQuery(Card.class);
                                Root<Card> card = getUserCard.from(Card.class);
                                getUserCard.select(card);

                                Predicate predicateFirst = cb.equal(card.get("user"), userIdis);
                                Predicate predicateSecond = cb.equal(card.get("headline"), headLine);
                                getUserCard.where(cb.and(predicateFirst, predicateSecond));

                                Query<Card> queryUserCard = session.createQuery(getUserCard);
                                Card results = queryUserCard.getSingleResult();
                                List<Institution> ins = results.getInstitutions();

                                System.out.print("Имена институтов пользователя " + nameUsStr + "\n");
                                for (Institution inst : ins) {
                                    System.out.println(inst);
                                }

                                boolean flagStop = false;
                                do{
                                    System.out.print("Enter name new institut: ");
                                    String newNameInstStr = reader.readLine();
                                    Institution newInst = new Institution();
                                    newInst.setName(newNameInstStr);
                                    ins.add(newInst);
                                    System.out.println("Желаете ещё добавить институт (1/0)?");
                                    Integer answer = Integer.parseInt(reader.readLine());

                                    if (answer == 0){
                                        flagStop = true;
                                        results.setInstitutions(ins);
                                        session.update(results);
                                        transAddInst.commit();
                                        System.out.println("--- SUCCESS ADDED ---");
                                        System.out.print("Имена институтов пользователя " + nameUsStr + "\n");
                                        for (Institution inst : ins){
                                            System.out.println("name: " + inst.getName());
                                        }
                                        session.clear();
                                        session.close();

                                    }
                                }while (flagStop == false);


                            } else {
                                System.out.println("Пользователь с данным UserName (" + nameUsStr + ") не найден!");
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
