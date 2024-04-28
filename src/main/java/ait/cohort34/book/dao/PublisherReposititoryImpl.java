package ait.cohort34.book.dao;

import ait.cohort34.book.model.Publisher;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@Repository
public class PublisherReposititoryImpl implements PublisherRepository {

    @PersistenceContext
    EntityManager em;

    @Override
    public List<String> findByPublishersAuthor(String authorName) {
        return em.createQuery(
                        "SELECT DISTINCT b.publisher.publisherName FROM Book b WHERE :authorName MEMBER OF b.authors", String.class)
                .setParameter("authorName", authorName)
                .getResultList();
    }

    @Override
    public Stream<Publisher> findDistinctByBooksAuthorsName(String authorName) {
        List<Publisher> publishers = em.createQuery(
                        "SELECT DISTINCT b.publisher FROM Book b WHERE :authorName MEMBER OF b.authors", Publisher.class)
                .setParameter("authorName", authorName)
                .getResultList();
        return publishers.stream();
    }

    @Override
    public Optional<Publisher> findById(String publisherId) {
        return Optional.ofNullable(em.find(Publisher.class, publisherId));
    }

    @Override
    public Publisher save(Publisher publisher) {
        em.persist(publisher);
        return publisher;
    }
}
