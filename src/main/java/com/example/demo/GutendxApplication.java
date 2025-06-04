package com.example.demo;

import com.literatura.model.*;
import com.literatura.repository.AuthorRepository;
import com.literatura.repository.BookRepository;
import com.literatura.service.ApiConsumer;
import com.literatura.service.DataConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@SpringBootApplication
public class GutendxApplication implements CommandLineRunner{

	@Autowired
	private BookRepository bookRepository;
	@Autowired
	private AuthorRepository authorRepository;

	private ApiConsumer apiConsumer = new ApiConsumer();
	private DataConverter converter = new DataConverter();
	private final String BASE_URL = "https://gutendex.com/books/";
	private Scanner scanner = new Scanner(System.in);

	public static void main(String[] args) {
		SpringApplication.run(GutendxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		showMenu();
	}

	private void showMenu() {
		int option = -1;
		while (option != 0) {
			System.out.println("------------------------------------------");
			System.out.println("Bienvenido a LiterAlura, tu catálogo de libros.");
			System.out.println("1. Buscar libro por título");
			System.out.println("2. Listar libros registrados");
			System.out.println("3. Listar autores registrados");
			System.out.println("4. Listar autores vivos en un determinado año");
			System.out.println("5. Listar libros por idioma");
			System.out.println("0. Salir");
			System.out.print("Elige una opción: ");
			try {
				option = Integer.valueOf(scanner.nextLine());

				switch (option) {
					case 1:
						searchBookByTitle();
						break;
					case 2:
						listRegisteredBooks();
						break;
					case 3:
						listRegisteredAuthors();
						break;
					case 4:
						listAuthorsAliveInYear();
						break;
					case 5:
						listBooksByLanguage();
						break;
					case 0:
						System.out.println("Saliendo de LiterAlura. ¡Hasta pronto!");
						break;
					default:
						System.out.println("Opción inválida. Inténtalo de nuevo.");
				}
			} catch (NumberFormatException e) {
				System.out.println("Entrada inválida. Por favor, ingresa un número.");
			}
		}
	}

	private void searchBookByTitle() {
		System.out.print("Ingresa el título del libro a buscar: ");
		String title = scanner.nextLine();
		String url = BASE_URL + "?search=" + title.replace(" ", "%20");
		String json = apiConsumer.getData(url);

		if (json != null) {
			GutendexResponse response = converter.convert(json, GutendexResponse.class);
			if (response != null && !response.results().isEmpty()) {
				BookData bookData = response.results().get(0); // Tomamos el primer resultado
				Book book = new Book(bookData);

				// Verificar si el libro ya existe en la BD
				Optional<Book> existingBook = bookRepository.findByTitleContainsIgnoreCase(book.getTitle());
				if (existingBook.isPresent()) {
					System.out.println("El libro ya se encuentra registrado en la base de datos:");
					System.out.println(existingBook.get());
				} else {
					// Guardar autores primero para evitar duplicados y luego el libro
					Set<Author> authorsToSave = new HashSet<>();
					for (Author author : book.getAuthors()) {
						Optional<Author> existingAuthor = authorRepository.findByNameContainsIgnoreCase(author.getName());
						if (existingAuthor.isPresent()) {
							authorsToSave.add(existingAuthor.get());
						} else {
							authorsToSave.add(authorRepository.save(author));
						}
					}
					book.setAuthors(authorsToSave);
					bookRepository.save(book);
					System.out.println("Libro encontrado y guardado en la base de datos:");
					System.out.println(book);
				}
			} else {
				System.out.println("No se encontró ningún libro con ese título.");
			}
		} else {
			System.out.println("Error al conectar con la API de Gutendex.");
		}
	}

	private void listRegisteredBooks() {
		List<Book> books = bookRepository.findAll();
		if (books.isEmpty()) {
			System.out.println("No hay libros registrados.");
		} else {
			System.out.println("\n--- LIBROS REGISTRADOS ---");
			books.forEach(System.out::println);
		}
	}

	private void listRegisteredAuthors() {
		List<Author> authors = authorRepository.findAll();
		if (authors.isEmpty()) {
			System.out.println("No hay autores registrados.");
		} else {
			System.out.println("\n--- AUTORES REGISTRADOS ---");
			authors.forEach(System.out::println);
		}
	}

	private void listAuthorsAliveInYear() {
		System.out.print("Ingresa el año para buscar autores vivos: ");
		try {
			Integer year = Integer.valueOf(scanner.nextLine());
			List<Author> authors = authorRepository.findAuthorsAliveInYear(year);
			if (authors.isEmpty()) {
				System.out.println("No se encontraron autores vivos en el año " + year + ".");
			} else {
				System.out.println("\n--- AUTORES VIVOS EN EL AÑO " + year + " ---");
				authors.forEach(System.out::println);
			}
		} catch (NumberFormatException e) {
			System.out.println("Año inválido. Por favor, ingresa un número válido.");
		}
	}

	private void listBooksByLanguage() {
		System.out.println("\nIdiomas disponibles (ej. es, en, fr, pt):");
		System.out.print("Ingresa el código del idioma (ej. 'es' para español): ");
		String language = scanner.nextLine().toLowerCase();
		List<Book> books = bookRepository.findByLanguage(language);
		if (books.isEmpty()) {
			System.out.println("No se encontraron libros en el idioma '" + language + "'.");
		} else {
			System.out.println("\n--- LIBROS EN IDIOMA '" + language.toUpperCase() + "' ---");
			books.forEach(System.out::println);
		}
	}





}
