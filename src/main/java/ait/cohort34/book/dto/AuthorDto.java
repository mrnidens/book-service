package ait.cohort34.book.dto;

import java.time.LocalDate;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode(of = "name")
public class AuthorDto {
	String name;
	LocalDate birthDate;
}
