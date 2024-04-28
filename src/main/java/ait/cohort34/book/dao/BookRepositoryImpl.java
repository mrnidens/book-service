package ait.cohort34.book.dao;

import ait.cohort34.book.model.Book;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Repository
public class BookRepositoryImpl implements BookRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public Stream<Book> findByAuthorsName(String authorName) {
        List<Book> books = em.createQuery(
                        "SELECT b FROM Book b JOIN b.authors a WHERE a.name = :authorName", Book.class)
                .setParameter("authorName", authorName)
                .getResultList();
        return books.stream();
    }

    @Override
    public Stream<Book> findByPublisherPublisherName(String name) {
        List<Book> books = em.createQuery(
                        "SELECT b FROM Book b WHERE b.publisher.publisherName = :publisherName", Book.class)
                .setParameter("publisherName", name)
                .getResultList();
        return books.stream();
    }

    @Override
    public void deleteByAuthorsName(String name) {
        em.createQuery("DELETE FROM Book b WHERE :name MEMBER OF b.authors")
                .setParameter("name", name)
                .executeUpdate();
    }

    @Override
    public boolean existsById(String isbn) {
        return em.find(Book.class, isbn) != null;
    }

    @Override
    public Book save(Book book) {
        return em.merge(book);
    }

    @Override
    public Optional<Book> findById(String isbn) {
        return Optional.ofNullable(em.find(Book.class, isbn));
    }

    @Override
    public void deleteById(String isbn) {
        em.remove(em.find(Book.class, isbn));
    }
}

