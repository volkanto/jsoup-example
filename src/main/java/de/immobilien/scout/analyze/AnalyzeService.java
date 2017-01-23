package de.immobilien.scout.analyze;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.DocumentType;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AnalyzeService {

	private static final String REGEX_URL_WITHOUT_PROTOCOL = "[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
	private static final String REGEX_URL_WITH_PROTOCOL = "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)";
	private static final int TEN_SECONDS = 10000;
	private static final String INTERNAL_LINK = "internal";
	private static final String EXTERNAL_LINK = "external";
	private static final String HEADING_NUMBERS = "h1, h2, h3, h4, h5, h6";
	private static final String HTTPS = "https://";
	private static final String HTTP = "http://";
	private static final String DOCTYPE_HTML = "<!doctype html>";
	private static final String[] LOGIN_ACTIONS = {"login", "session"};
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	public String getTitleOfPage(Document argDocument) {
		return argDocument != null ? argDocument.title() : null;
	}
	
	public String getVersionOfPage(Document document) {
		DocumentType documentType = this.getPageDocumentType(document);
		if(documentType == null) {
			return null;
		}
		return this.isDocumentHTML5(documentType) ? "HTML5" : documentType.attr("publicid");
	}
	
	public int getNumberOfHeadings(Document argDocument) {
		return argDocument != null ? argDocument.select(HEADING_NUMBERS).size() : 0;
	}
	
	public Map<String, Integer> getNumberOfHyperMediaLinks(Document argDocument) {
		Map<String, Integer> hyperMediaLinks = new HashMap<>(); 
		if(argDocument != null) {
			Elements links = argDocument.select("a");
			for (Element link : links) {
				if(this.isLinkInternal(argDocument.baseUri(), link.attr("abs:href"))) {
					Integer internalCount = hyperMediaLinks.get(INTERNAL_LINK);
					if(internalCount == null) {
						hyperMediaLinks.put(INTERNAL_LINK, 1);
					} else {
						internalCount = internalCount+1;
						hyperMediaLinks.put(INTERNAL_LINK, internalCount);
					}
				} else {
					Integer internalCount = hyperMediaLinks.get(EXTERNAL_LINK);
					if(internalCount == null) {
						hyperMediaLinks.put(EXTERNAL_LINK, 1);
					} else {
						internalCount = internalCount+1;
						hyperMediaLinks.put(EXTERNAL_LINK, internalCount);
					}
				}
			}
		}
		return hyperMediaLinks;
	}
	
	public Document connectToPage(String argUrl) {
		try {
			return Jsoup.connect(this.controlAndCorrectUrl(argUrl)).timeout(TEN_SECONDS).get();
		} catch (IOException e) {
			logger.error("error on connecting to " + argUrl + " in 10 seconds");
		}
		return null;
	}
	
	public boolean isPageContainsLogin(Document argDocument) {
		Elements formElements = argDocument.select("form[method=post]");
		if(formElements != null && !formElements.isEmpty()) {
			for (Element element : formElements) {
				return this.isElementContainsLoginAction(element.attr("action"));
			}			
		}
		return false;
	}

	private String controlAndCorrectUrl(String argUrl) {
		if (!argUrl.startsWith(HTTP) && !argUrl.startsWith(HTTPS)) {
			return HTTP + argUrl;
		}
		return argUrl;
	}

	private DocumentType getPageDocumentType(Document argDocument) {
		List<Node> childNodes = argDocument.childNodes();
		if(childNodes != null && !childNodes.isEmpty()) {
			for (Node node : childNodes) {
				if (node instanceof DocumentType) {
					return (DocumentType) node;
				}
			}			
		}
		return null;
	}
	
	private boolean isDocumentHTML5(DocumentType argDocumentType) {
		return DOCTYPE_HTML.equals(argDocumentType.toString().toLowerCase());
	}

	private boolean isLinkInternal(String argBaseUrl, String argLink) {
		return argLink.contains(this.extractHostFromUrl(argBaseUrl));
	}

	private String extractHostFromUrl(String argUrl) {
		try {
			return new URL(argUrl).getHost();
		} catch (MalformedURLException e) {
			logger.error("url error : " + e.getMessage(), e);
		}
		return null;
	}
	
	private boolean isElementContainsLoginAction(String argAttributeData) {
		for (String loginAction : LOGIN_ACTIONS) {
			return argAttributeData.contains(loginAction);
		}
		return false;
	}

	public boolean isUrlValid(String givenUrl) {
		return Pattern.matches(REGEX_URL_WITH_PROTOCOL, givenUrl) || Pattern.matches(REGEX_URL_WITHOUT_PROTOCOL, givenUrl);
	}

}
