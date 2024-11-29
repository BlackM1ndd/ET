package com.example.expense_tracker.dao;

import com.example.expense_tracker.model.Expense;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class ExpenseDao {
    private SessionFactory sessionFactory;

    public ExpenseDao() {
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }

    public void save(Expense expense) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        session.save(expense);
        session.getTransaction().commit();
        session.close();
    }

    public List<Expense> findAll() {
        Session session = sessionFactory.openSession();
        List<Expense> expenses = session.createQuery("from Expense", Expense.class).list();
        session.close();
        return expenses;
    }

    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        session.beginTransaction();
        Expense expense = session.get(Expense.class, id);
        if (expense != null) {
            session.delete(expense);
        }
        session.getTransaction().commit();
        session.close();
    }
}