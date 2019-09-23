package com.pa.extractor;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.pa.database.impl.DatabaseFacade;
import com.pa.entity.Book;
import com.pa.entity.Chapter;
import com.pa.entity.Curriculo;
import com.pa.entity.Orientation;
import com.pa.entity.OrientationType;
import com.pa.entity.Publication;
import com.pa.entity.PublicationType;
import com.pa.entity.TechnicalProduction;
import com.pa.exception.InvalidPatternFileException;
import com.pa.util.EnumPublicationLocalType;

public class XMLExtractor {

	public Curriculo lattesExtractor(InputStream file) throws InvalidPatternFileException {
		Curriculo curriculo = null;

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			// Load and Parse the XML document
			Document document = builder.parse(file);

			curriculo = this.extractBasicInformations(document);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			throw new InvalidPatternFileException(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			throw new InvalidPatternFileException(e.getMessage());
		}

		if (curriculo == null) {
			throw new InvalidPatternFileException("XML file is invalid: the file is not about a lattes curriculum");
		}

		return curriculo;
	}

	private Curriculo extractBasicInformations(Document document) {
		Curriculo curriculo = null;
		String name;
		Long id = null;
		Date lastUpdate = null;
		ArrayList<Orientation> orientationsCaptured = null;

		if (document.getDocumentElement().getNodeName().equals("CURRICULO-VITAE")) {
			// Get last update from xml
			String update = document.getDocumentElement().getAttributes().getNamedItem("DATA-ATUALIZACAO")
					.getNodeValue();
			try {
				SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy");
				lastUpdate = sdf1.parse(update);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			// Get identifier
			String identifier = document.getDocumentElement().getAttributes().getNamedItem("NUMERO-IDENTIFICADOR")
					.getNodeValue();
			id = Long.valueOf(identifier);

			// Iterating through the nodes and extracting the data.
			NodeList nodeList = document.getDocumentElement().getChildNodes();

			for (int i = 0; i < nodeList.getLength(); i++) {
				Node node = nodeList.item(i);

				if (node instanceof Element) {
					if (node.getNodeName().equals("DADOS-GERAIS")) {
						// Get name
						name = node.getAttributes().getNamedItem("NOME-COMPLETO").getNodeValue();

						if (id != null && !name.isEmpty() && lastUpdate != null) {
							curriculo = new Curriculo(name, lastUpdate);
							curriculo.setId(id);
						}
					} else if (node.getNodeName().equals("PRODUCAO-BIBLIOGRAFICA")) {
						// Publicações
						curriculo.setPublications(this.extractPublications(node));
						curriculo.setBooks(this.extractBookPublications(node));
						curriculo.setChapters(this.extractChapterPublications(node));
					} else if (node.getNodeName().equals("PRODUCAO-TECNICA")) {
						// Software
						curriculo.setTechinicalProduction(this.extractTechinicalProduction(node));
					} else if (node.getNodeName().equals("OUTRA-PRODUCAO")) {
						// Orientações concluídas
						int orientations = this.extractQtdOrientations(node, "ORIENTACOES-CONCLUIDAS-PARA-MESTRADO");
						orientations += this.extractQtdOrientations(node, "ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO");
						curriculo.setCountConcludedOrientations(orientations);
						orientationsCaptured = this.extractOrientations(node);
						curriculo.setOrientations(orientationsCaptured);
					} else if (node.getNodeName().equals("DADOS-COMPLEMENTARES")) {
						// Orientações não concluídas
						int orientations = this.extractQtdOrientations(node, "ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO");
						orientations += this.extractQtdOrientations(node, "ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO");
						curriculo.setCountOnGoingOrientations(orientations);
						orientationsCaptured.addAll(this.extractOrientations(node));
					}
				}
			}
		}
		return curriculo;
	}

	private List<Chapter> extractChapterPublications(Node nodeChapter) {
		ArrayList<Chapter> chapters = new ArrayList<Chapter>();

		NodeList nodeList = nodeChapter.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node instanceof Element) {
				if (node.getNodeName().equals("LIVROS-E-CAPITULOS")) {
					NodeList events = node.getChildNodes();
					Node basicDataEvent = events.item(1);
					if (basicDataEvent != null) {
						if (basicDataEvent.getNodeName().equals("CAPITULOS-DE-LIVROS-PUBLICADOS")) {
							extractChapterPublished(chapters, basicDataEvent);
						}
					}
				}
			}
		}

		return chapters;
	}

	private List<Book> extractBookPublications(Node nodeBook) {
		ArrayList<Book> books = new ArrayList<Book>();

		NodeList nodeList = nodeBook.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node instanceof Element) {
				if (node.getNodeName().equals("LIVROS-E-CAPITULOS")) {
					NodeList events = node.getChildNodes();
					Node basicDataEvent = events.item(0);
					if (basicDataEvent != null) {
						if (basicDataEvent.getNodeName().equals("LIVROS-PUBLICADOS-OU-ORGANIZADOS")) {
							extractBookPublished(books, basicDataEvent);
						}
					}
				}
			}
		}

		return books;
	}

	private ArrayList<TechnicalProduction> extractTechinicalProduction(Node nodeProduction) {
		ArrayList<TechnicalProduction> techinicalProductions = new ArrayList<TechnicalProduction>();

		NodeList nodeList = nodeProduction.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node instanceof Element) {
				if (node.getNodeName().equals("SOFTWARE")) {
					NodeList events = node.getChildNodes();

					Node basicDataEvent = events.item(0);
					if (basicDataEvent != null) {
						if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DO-SOFTWARE")) {
							Node softwareTitle = basicDataEvent.getAttributes().getNamedItem("TITULO-DO-SOFTWARE");
							Node softwareYear = basicDataEvent.getAttributes().getNamedItem("ANO");

							if (softwareTitle != null) {
								TechnicalProduction techinicalProduction = new TechnicalProduction(
										softwareTitle.getNodeValue(), softwareYear.getNodeValue(), "SOFTWARE");

								techinicalProductions.add(techinicalProduction);
							}
						}
					}
				}
			}
		}

		return techinicalProductions;
	}

	private ArrayList<Orientation> extractOrientations(Node dataNode) {
		ArrayList<Orientation> orientations = new ArrayList<Orientation>();

		NodeList nodeList = dataNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			NodeList childNodeList = node.getChildNodes();
			for (int j = 0; j < childNodeList.getLength(); j++) {
				Node node2 = childNodeList.item(j);
				Node basicDataEvent = node2.getChildNodes().item(0);
				if (basicDataEvent != null) {
					if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DE-ORIENTACOES-CONCLUIDAS-PARA-MESTRADO")) {
						extractBasicDataOrientations("TITULO", orientations, basicDataEvent,
								OrientationType.ORIENTACOES_CONCLUIDAS_PARA_MESTRADO);
					}
					if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DE-ORIENTACOES-CONCLUIDAS-PARA-DOUTORADO")) {
						extractBasicDataOrientations("TITULO", orientations, basicDataEvent,
								OrientationType.ORIENTACOES_CONCLUIDAS_PARA_DOUTORADO);
					}
					if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-MESTRADO")) {
						extractBasicDataOrientations("TITULO-DO-TRABALHO", orientations, basicDataEvent,
								OrientationType.ORIENTACAO_EM_ANDAMENTO_DE_MESTRADO);
					}
					if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DA-ORIENTACAO-EM-ANDAMENTO-DE-DOUTORADO")) {
						extractBasicDataOrientations("TITULO-DO-TRABALHO", orientations, basicDataEvent,
								OrientationType.ORIENTACAO_EM_ANDAMENTO_DE_DOUTORADO);
					}
					if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DE-OUTRAS-ORIENTACOES-CONCLUIDAS")) {
						Node orientationNatureza = basicDataEvent.getAttributes().getNamedItem("NATUREZA");
						if (orientationNatureza.getNodeValue().equals("INICIACAO_CIENTIFICA")) {
							extractBasicDataOrientations("TITULO", orientations, basicDataEvent,
									OrientationType.ORIENTACAO_INICIACAO_CIENTIFICA);
						}
					}
				}
			}
		}
		return orientations;
	}

	private void extractBasicDataOrientations(String title, ArrayList<Orientation> orientations, Node basicDataEvent,
			OrientationType orientationType) {
		Node orientationTitle = basicDataEvent.getAttributes().getNamedItem(title);
		Node orientationNatureza = basicDataEvent.getAttributes().getNamedItem("NATUREZA");
		Node orientationYear = basicDataEvent.getAttributes().getNamedItem("ANO");
		Node orientationLanguage = basicDataEvent.getAttributes().getNamedItem("IDIOMA");

		if (orientationTitle != null) {
			Orientation orientation = new Orientation(orientationNatureza.getNodeValue(), orientationType,
					orientationTitle.getNodeValue(), orientationYear.getNodeValue(),
					orientationLanguage.getNodeValue());
			orientations.add(orientation);
		}
	}

	private Set<Publication> extractPublications(Node nodeProduction) {
		Set<Publication> publications = new HashSet<Publication>();

		NodeList nodeList = nodeProduction.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			if (node instanceof Element) {
				if (node.getNodeName().equals("TRABALHOS-EM-EVENTOS")) {
					extractWorksInEvents(publications, node);
				}
				if (node.getNodeName().equals("ARTIGOS-PUBLICADOS")) {
					extractPapersPublished(publications, node);
				}
			}
		}

		return publications;
	}

	private void extractWorksInEvents(Set<Publication> publications, Node node) {
		NodeList events = node.getChildNodes();
		Node eventTitle = null, eventYear = null;
		List<String> authors = new ArrayList<String>();

		for (int j = 0; j < events.getLength(); j++) {
			// Evento (Conferência)
			Node event = events.item(j);
			NodeList basicData = event.getChildNodes();
			if (basicData != null) {
				for (int k = 0; k <= basicData.getLength(); k++) {
					Node basicDataEvent = basicData.item(k);
					if (basicDataEvent != null) {
						if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DO-TRABALHO")) {
							eventTitle = basicDataEvent.getAttributes().getNamedItem("TITULO-DO-TRABALHO");
							eventYear = basicDataEvent.getAttributes().getNamedItem("ANO-DO-TRABALHO");
						}
						if (basicDataEvent.getNodeName().equals("AUTORES")) {
							authors.add(extractAuthorsPublications(basicDataEvent));
						}
					}
				}

				if (eventTitle != null && eventYear != null) {
					PublicationType type = getPublicationType(event, EnumPublicationLocalType.CONFERENCE);
					Publication publication = new Publication(eventTitle.getNodeValue(),
							Integer.valueOf(eventYear.getNodeValue()), type, authors);

					publication = getRealPublication(publication);

					if (publication.getId() == null) {
						publications.add(publication);
					}

					authors = new ArrayList<String>();
				}
			}
		}
	}

	private void extractChapterPublished(ArrayList<Chapter> chapters, Node node) {
		NodeList nodeListBooks = node.getChildNodes();
		List<String> authors = new ArrayList<String>();

		for (int k = 0; k < nodeListBooks.getLength(); k++) {
			Node nodeBook = nodeListBooks.item(k);

			if (nodeBook.getNodeName().equals("CAPITULO-DE-LIVRO-PUBLICADO")) {
				NodeList events = nodeBook.getChildNodes();
				Node chapterTitle = null, chapterYear = null, chapterBookTitle = null;
				for (int j = 0; j <= events.getLength(); j++) {
					Node basicDataEvent = events.item(j);
					if (basicDataEvent != null) {
						if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DO-CAPITULO")) {
							chapterTitle = basicDataEvent.getAttributes().getNamedItem("TITULO-DO-CAPITULO-DO-LIVRO");
							chapterYear = basicDataEvent.getAttributes().getNamedItem("ANO");
						}
						if (basicDataEvent.getNodeName().equals("AUTORES")) {
							authors.add(extractAuthorsPublications(basicDataEvent));
						}
						if (basicDataEvent.getNodeName().equals("DETALHAMENTO-DO-CAPITULO")) {
							chapterBookTitle = basicDataEvent.getAttributes().getNamedItem("TITULO-DO-LIVRO");
						}
					}
				}
				if (chapterTitle != null) {
					Chapter chapter = new Chapter(chapterTitle.getNodeValue(), chapterYear.getNodeValue(),
							chapterBookTitle.getNodeValue(), authors);

					chapters.add(chapter);
				}
				authors = new ArrayList<String>();
			}
		}
	}

	private void extractBookPublished(ArrayList<Book> books, Node node) {
		NodeList nodeListBooks = node.getChildNodes();
		List<String> authors = new ArrayList<String>();

		for (int k = 0; k < nodeListBooks.getLength(); k++) {
			Node nodeBook = nodeListBooks.item(k);

			if (nodeBook instanceof Element) {
				if (nodeBook.getNodeName().equals("LIVRO-PUBLICADO-OU-ORGANIZADO")) {
					NodeList events = nodeBook.getChildNodes();
					Node bookTitle = null, bookYear = null, bookPublishingCompany = null;
					for (int j = 0; j <= events.getLength(); j++) {
						Node basicDataEvent = events.item(j);
						if (basicDataEvent != null) {
							if (basicDataEvent != null) {
								if (basicDataEvent.getNodeName().equals("DADOS-BASICOS-DO-LIVRO")) {
									bookTitle = basicDataEvent.getAttributes().getNamedItem("TITULO-DO-LIVRO");
									bookYear = basicDataEvent.getAttributes().getNamedItem("ANO");
								}
								if (basicDataEvent.getNodeName().equals("DETALHAMENTO-DO-LIVRO")) {
									bookPublishingCompany = basicDataEvent.getAttributes()
											.getNamedItem("NOME-DA-EDITORA");
								}
								if (basicDataEvent.getNodeName().equals("AUTORES")) {
									authors.add(extractAuthorsPublications(basicDataEvent));

								}
							}
						}
					}
					if (bookTitle != null) {
						Book book = new Book(bookTitle.getNodeValue(), bookYear.getNodeValue(),
								bookPublishingCompany.getNodeValue(), authors);
						books.add(book);
					}
					authors = new ArrayList<String>();
				}
			}
		}
	}

	private void extractPapersPublished(Set<Publication> publications, Node node) {
		NodeList articles = node.getChildNodes();
		Node articleTitle = null, articleYear = null;
		List<String> authors = new ArrayList<String>();

		for (int j = 0; j < articles.getLength(); j++) {
			// Artigo (Periodico ou Revista)
			Node article = articles.item(j);
			NodeList basicData = article.getChildNodes();
			if (basicData != null) {
				for (int k = 0; k <= basicData.getLength(); k++) {
					Node basicDataArticle = basicData.item(k);
					if (basicDataArticle != null) {
						if (basicDataArticle.getNodeName().equals("DADOS-BASICOS-DO-ARTIGO")) {
							articleTitle = basicDataArticle.getAttributes().getNamedItem("TITULO-DO-ARTIGO");
							articleYear = basicDataArticle.getAttributes().getNamedItem("ANO-DO-ARTIGO");
						}
						if (basicDataArticle.getNodeName().equals("AUTORES")) {
							authors.add(extractAuthorsPublications(basicDataArticle));
						}
					}
				}
				if (articleTitle != null && articleYear != null) {
					PublicationType type = getPublicationType(article, EnumPublicationLocalType.PERIODIC);
					Publication publication = new Publication(articleTitle.getNodeValue(),
							Integer.valueOf(articleYear.getNodeValue()), type, authors);

					publication = getRealPublication(publication);

					if (publication.getId() == null) {
						publications.add(publication);
					}

					authors = new ArrayList<String>();
				}
			}
		}
	}

	private String extractAuthorsPublications(Node node) {
		String s = node.getAttributes().item(1).getNodeValue();
		String[] t = s.split(Pattern.quote(";"));
		String author = t[0];
		if (author != null) {
			return author;
		} else {
			return "";
		}
	}

	private Publication getRealPublication(Publication publication) {
		List<Publication> databasePublications = DatabaseFacade.getInstance().listAllPublications(publication);
		if (!databasePublications.isEmpty()) {
			for (Publication basePublication : databasePublications) {
				if (basePublication.getPublicationType().equals(publication.getPublicationType())
						&& basePublication.getTitle().equals(publication.getTitle())) {
					publication = basePublication;
				}
			}
		}
		return publication;
	}

	private PublicationType getPublicationType(Node mainNode, EnumPublicationLocalType local) {
		PublicationType type = null;
		String name;

		Node details = mainNode.getChildNodes().item(1);
		Node eventName = null;

		if (local.equals(EnumPublicationLocalType.CONFERENCE)) {
			if (details.getNodeName().equals("DETALHAMENTO-DO-TRABALHO")) {
				eventName = details.getAttributes().getNamedItem("NOME-DO-EVENTO");
			}
			if (details.getNodeName().equals("DETALHAMENTO-DO-LIVRO")) {
				eventName = details.getAttributes().getNamedItem("NOME-DA-EDITORA");
			}
		} else if (local.equals(EnumPublicationLocalType.PERIODIC)) {
			if (details.getNodeName().equals("DETALHAMENTO-DO-ARTIGO")) {
				eventName = details.getAttributes().getNamedItem("TITULO-DO-PERIODICO-OU-REVISTA");
			}
		}

		if (eventName != null) {
			name = eventName.getNodeValue();
			type = new PublicationType(name, local);

			// Update objects if the publication already exists
			type = getRealPublicationType(type);
		}

		return type;
	}

	private PublicationType getRealPublicationType(PublicationType newType) {
		PublicationType databaseType = DatabaseFacade.getInstance().getPublicationTypeByNameAndType(newType.getName(),
				newType.getType());
		if (databaseType != null) {
			newType = databaseType;
		}

		return newType;
	}

	private int extractQtdOrientations(Node mainNode, String tag) {
		int orientations = 0;

		NodeList nodeList = mainNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);

			NodeList filhosDeIde2 = node.getChildNodes();
			for (int j = 0; j < filhosDeIde2.getLength(); j++) {
				Node node2 = filhosDeIde2.item(j);
				if (node2.getNodeName().equals(tag)) {
					orientations++;
				}
			}
		}
		return orientations;
	}
}
