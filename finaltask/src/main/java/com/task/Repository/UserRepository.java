package com.task.Repository;

import java.util.Collections;
import java.util.List;

import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.task.Model.Login;
import com.task.Model.User;

@Repository
@Transactional
public class UserRepository {

    private final SessionFactory sessionFactory;

    @Autowired
    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private static final Logger logger = LogManager.getLogger();

    // Repository Method - Add Users
    public boolean addUserInfo(User user) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.save(user);
            logger.info("User saved to Database successfully");
            return true;
        } catch (HibernateException e) {
            logger.error("Hibernate exception in addUserInfo method....");
            return false;
        } catch (Exception e) {
            logger.warn("Exception in adduserInfo Method.....");
            return false;
            // return false;
        }
    }

    // Check User By Email
    public User checkUserByEmailid(String emailId) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("emailId", emailId));
            return (User) criteria.uniqueResult();
        } catch (HibernateException e) {

            logger.error("Hibernate Exception when checking user with Mail Id:{}", emailId);
            return null;
        } catch (Exception e) {
            logger.error("Exception when checking user with Mail Id:{}", emailId);
            return null;
        }
    }

    // Save Login info
    public void saveLoginInfo(Login loginInfo) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.save(loginInfo);
            logger.info("Successfully saved login information: {}", loginInfo);
        } catch (HibernateException e) {
            logger.error("HibernateException in saveLoginInfo: {}", loginInfo, e);
        } catch (Exception e) {
            logger.error("Exception in saveLoginInfo: {}", loginInfo, e);
        }
    }

    // Pagination method for Login Info
    public List<Login> getLoginInfo(int userId, int page, int pageSize) {
        try {
            logger.info("Trying to get login Info");

            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(Login.class);

            criteria.createAlias("user", "u");

            criteria.add(Restrictions.eq("u.userId", userId));

            criteria.setFirstResult((page - 1) * pageSize);
            criteria.setMaxResults(pageSize);

            logger.info("Fetched Login info successfully for user:{}", userId);

            return criteria.list();

        } catch (HibernateException e) {
            logger.error("Hibernate Exception in getLoginInfo method");
            return null;
        } catch (Exception e) {
            logger.error("Exception in getLoginInfo method");
            return null;
        }

    }

    // Total Login Counts for Pagination
    public int getTotalLoginCount(int userId) {
        try {

            logger.info("Attempting to fetch Login Counts of user with ID:", userId);

            Session session = sessionFactory.getCurrentSession();

            // String hql = "SELECT COUNT(L) FROM Login L where l.user.userId= :userId";

            // Query<Long> query = session.createQuery(hql, Long.class);
            // query.setParameter("userId", userId);

            // Long count = query.uniqueResult();

            Criteria criteria = session.createCriteria(Login.class);
            criteria.createAlias("user", "u");
            criteria.add(Restrictions.eq("u.userId", userId));

            criteria.setProjection(Projections.rowCount());

            Long count = (Long) criteria.uniqueResult();
            return count != null ? count.intValue() : 0;
        } catch (HibernateException e) {
            logger.error("Hibernate exception in getLoginCount Method for user Id:{}", userId);
            return 0;
        }
    }

    // Delete User method
    public void deleteUser(int userId) {
        try {
            Session session = sessionFactory.getCurrentSession();
            User user = session.get(User.class, userId);
            if (user != null) {
                session.delete(user);
                logger.info("user deleted with the id:{}", userId);

            }
        } catch (HibernateException e) {
            logger.error("HibernateException while updating user");
        } catch (Exception e) {
            logger.error("Exception while updating user");
        }
    }

    // Find User By ID
    public User findUser(int userIdForAction) {
        try {
            Session session = sessionFactory.getCurrentSession();
            logger.info("user found with the id:{}", userIdForAction);
            return session.get(User.class, userIdForAction);

        } catch (HibernateException e) {
            logger.error("HibernateException while updating user");
            return null;
        } catch (Exception e) {
            logger.error("Exception while updating user");
            return null;
        }
    }

    // MailId Validation for Update
    public User findUserByEmailExcludingId(String emailId, int userId) {
        try {
            Session session = sessionFactory.getCurrentSession();

            // String Hql = "SELECT u from User u WHERE u.emailId = :emailId AND u.userId !=
            // :userId";
            // Query<User> user = session.createQuery(Hql,User.class);
            // user.setParameter("emailId", emailId);
            // user.setParameter("userId", userId);
            // return (User) user.uniqueResult();

            Criteria criteria = session.createCriteria(User.class)
                    .add(Restrictions.eq("emailId", emailId))
                    .add(Restrictions.ne("userId", userId));
            return (User) criteria.uniqueResult();
        } catch (HibernateException e) {
            logger.error("HibernateException while updating user");
            return null;
        } catch (Exception e) {
            logger.error("Exception while updating user");
            return null;
        }
    }

    // Update Users
    public void updateUser(User user) {
        try {
            Session session = sessionFactory.getCurrentSession();
            session.update(user);
        } catch (HibernateException e) {
            logger.error("HibernateException while updating user");
        } catch (Exception e) {
            logger.error("Exception while updating user");
        }
    }

    // Method to fetch All Users with pagination
    public List<User> fetchUsersWithPagination(int offset, int pageSize) {
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.setFirstResult(offset);
            criteria.setMaxResults(pageSize);
            return criteria.list();
        } catch (HibernateException e) {
            logger.error("HibernateException while retrieving inactive users (offset: {}, pageSize: {}): {}", offset,
                    pageSize, e.getMessage());
            return Collections.emptyList();
        }
    }

    // Method to count total Users for pagination
    public int countTotalUsers() {
        try {
            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.setProjection(Projections.rowCount());
            return ((Long) criteria.uniqueResult()).intValue();
        } catch (HibernateException e) {
            logger.error("HibernateException in count total users");
            return 0;
        } catch (Exception e) {
            logger.error("Exception in count total users");
            return 0;
        }
    }

    // Fetch List of Inactive Users
    public List<User> findInactiveUsers(int offset, int pageSize) {
        try {

            logger.info("Fetching Inactive(offset: {}, pageSize: {}): {}", offset, pageSize);

            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("loginStatus", 0));
            criteria.setFirstResult(offset);
            criteria.setMaxResults(pageSize);
            return criteria.list();

        } catch (HibernateException e) {
            logger.error("HibernateException while retrieving inactive users (offset: {}, pageSize: {}): {}", offset,
                    pageSize, e.getMessage());
            return null;
        } catch (Exception e) {
            logger.error("Exception while retrieving inactive users (offset: {}, pageSize: {}): {}", offset, pageSize,
                    e.getMessage());
            return null;
        }
    }

    // Get inactive user counts
    public int countInactiveUsers() {
        try {

            Session session = sessionFactory.getCurrentSession();
            // String Hql = "SELECT Count(u) from User u where u.loginStatus = 0";
            // Query<Long> query = session.createQuery(Hql,Long.class);
            // Long value = query.uniqueResult();
            // return value!=null ? value.intValue() : 0;

            logger.info("Attempting to get count of inactive users");

            Criteria criteria = session.createCriteria(User.class);
            criteria.add(Restrictions.eq("loginStatus", 0));
            criteria.setProjection(Projections.rowCount());
            Long count = (Long) criteria.uniqueResult();

            logger.info("Fetched count of inactive users from table:{}", count);

            return count != null ? count.intValue() : 0;
        } catch (HibernateException e) {
            logger.error("HibernateException while counting inactive users: {}", e.getMessage());
            return 0;
        } catch (Exception e) {
            logger.error("Exception while counting inactive users: {}", e.getMessage());
            return 0;
        }
    }

    public List<User> searchResults(String val) {
        logger.info("Inside repository search Method");
        try {

            Session session = sessionFactory.getCurrentSession();
            Criteria criteria = session.createCriteria(User.class);

            criteria.add(Restrictions.or(
                    Restrictions.ilike("userName", "%" + val + "%"),
                    Restrictions.ilike("emailId", "%" + val + "%"),
                    Restrictions.ilike("designation", "%" + val + "%"),
                    Restrictions.ilike("role", "%" + val + "%")));
            return criteria.list();

        } catch (HibernateException e) {
            logger.error("Hibernate Exception while fetching users for search Field");
            return null;
        } catch (Exception e) {
            logger.error("Exception while fetching users for search Field");
            return null;
        }
    }

}
