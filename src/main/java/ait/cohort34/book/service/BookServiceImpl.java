package ait.cohort34.book.service;

import ait.cohort34.book.dao.AuthorRepository;
import ait.cohort34.book.dao.BookRepository;
import ait.cohort34.book.dao.PublisherRepository;
import ait.cohort34.book.dto.AuthorDto;
import ait.cohort34.book.dto.BookDto;
import ait.cohort34.book.dto.exceptions.EntityNotFoundException;
import ait.cohort34.book.model.Author;
import ait.cohort34.book.model.Book;
import ait.cohort34.book.model.Publisher;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    final BookRepository bookRepository;
    final AuthorRepository authorRepository;
    final PublisherRepository publisherRepository;
    final ModelMapper modelMapper;

    @Transactional
    @Override
    public boolean addBook(BookDto bookDto) {
        if (bookRepository.existsById(bookDto.getIsbn())) {
            return false;
        }
        // Publisher handle
        Publisher publisher = publisherRepository.findById(bookDto.getPublisher())
                .orElseGet(() -> publisherRepository.save(new Publisher(bookDto.getPublisher())));
        // Authors handle
        Set<Author> authors = bookDto.getAuthors().stream()
                .map(a -> authorRepository.findById(a.getName()).orElseGet(() -> authorRepository.save(new Author(a.getName(), a.getBirthDate()))))
                .collect(Collectors.toSet());
        Book book = new Book(bookDto.getIsbn(), bookDto.getTitle(), authors, publisher);
        bookRepository.save(book);
        return true;
    }

    @Override
    public BookDto findBookByIsbn(String isbn) {
        Book book = bookRepository.findById(isbn).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(book, BookDto.class);
    }

    @Transactional
    @Override
    public BookDto remove(String isbn) {
        Book book = bookRepository.findById(isbn).orElseThrow(EntityNotFoundException::new);
        bookRepository.deleteById(isbn);
        return modelMapper.map(book, BookDto.class);
    }

    @Transactional
    @Override
    public BookDto updateBook(String isbn, String title) {
        Book book = bookRepository.findById(isbn).orElseThrow(EntityNotFoundException::new);
        book.setTitle(title);
        return modelMapper.map(book, BookDto.class);
    }

    @Override
    public Iterable<BookDto> findBooksByAuthor(String authorName) {
        Author author = authorRepository.findById(authorName).orElseThrow(EntityNotFoundException::new);
        return author.getBooks().stream()
                .map(b -> modelMapper.map(b, BookDto.class))
                .toList();
    }

    @Override
    public Iterable<BookDto> findBooksByPublisher(String publisherName) {
        Publisher publisher = publisherRepository.findById(publisherName).orElseThrow(EntityNotFoundException::new);
        return publisher.getBooks().stream()
                .map(b -> modelMapper.map(b, BookDto.class))
                .toList();
    }

    @Override
    public Iterable<AuthorDto> findBookAuthors(String isbn) {
        Book book = bookRepository.findById(isbn).orElseThrow(EntityNotFoundException::new);
        return book.getAuthors().stream()
                .map(a -> modelMapper.map(a, AuthorDto.class))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public Iterable<String> findPublishersByAuthor(String authorName) {
        return publisherRepository.findDistinctByBooksAuthorsName(authorName)
                .map(Publisher::getPublisherName)
                .toList();
    }

    @Transactional
    @Override
    public AuthorDto removeAuthor(String authorName) {
        Author author = authorRepository.findById(authorName).orElseThrow(EntityNotFoundException::new);
        authorRepository.deleteById(authorName);
        return modelMapper.map(author, AuthorDto.class);
    }
}
