package de.immobilien.scout.analyze;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import org.jsoup.nodes.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class AnalyzeServiceShould {

	@Mock
	private AnalyzeService analyzeService;

	@Mock
	private Document document;

	@Before
	public void setupMock() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void connects_page_given_page() {
		document = new Document("https://github.com/login");
		when(this.analyzeService.connectToPage("https://github.com/login")).thenReturn(document);
		assertNotNull("document is not null", document);
	}

	@Test
	public void throws_exception_when_given_url_is_not_valid() {
		document = null;
		when(this.analyzeService.connectToPage("asdadad.com")).thenReturn(document);
		assertNull("document is null", document);
	}

	@Test
	public void returns_page_title_if_exists() {
		document = new Document("https://github.com/login");
		when(this.analyzeService.getTitleOfPage(document)).thenReturn("Github");
		assertEquals("Github", this.analyzeService.getTitleOfPage(document));
	}

	@Test
	public void returns_null_page_title_if_not_exists() {
		document = new Document("https://test.com");
		when(this.analyzeService.getTitleOfPage(document)).thenReturn(null);
		assertNull(this.analyzeService.getTitleOfPage(document));
	}

	@Test
	public void returns_number_of_headings_if_there_are_some() {
		document = new Document("https://github.com");
		when(this.analyzeService.getNumberOfHeadings(document)).thenReturn(10);
		assertEquals(10, this.analyzeService.getNumberOfHeadings(document));
	}

	@Test
	public void returns_zero_number_of_headings_if_there_are_not_some() {
		document = new Document("https://xyz.com");
		when(this.analyzeService.getNumberOfHeadings(document)).thenReturn(0);
		assertEquals(0, this.analyzeService.getNumberOfHeadings(document));
	}

	@Test
	public void returns_HTML5_if_page_is_HTML5() {
		document = new Document("https://github.com");
		when(this.analyzeService.getVersionOfPage(document)).thenReturn("HTML5");
		assertEquals("HTML5", this.analyzeService.getVersionOfPage(document));
	}

	@Test
	public void returns_true_if_page_has_login() {
		document = new Document("https://github.com/login");
		when(this.analyzeService.isPageContainsLogin(document)).thenReturn(true);
		assertEquals(true, this.analyzeService.isPageContainsLogin(document));
	}

	@Test
	public void returns_false_if_page_has_no_login() {
		document = new Document("https://google.com");
		when(this.analyzeService.isPageContainsLogin(document)).thenReturn(false);
		assertEquals(false, this.analyzeService.isPageContainsLogin(document));
	}

	@Test
	public void returns_false_when_url_is_not_valid() {
		String url = "asadA";
		when(this.analyzeService.isUrlValid(url)).thenReturn(false);
		assertEquals(false, this.analyzeService.isUrlValid(url));
	}

	@Test
	public void returns_true_when_url_is_valid() {
		String url = "google.com";
		when(this.analyzeService.isUrlValid(url)).thenReturn(true);
		assertEquals(true, this.analyzeService.isUrlValid(url));
	}
}
