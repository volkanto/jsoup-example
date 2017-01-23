package de.immobilien.scout.analyze;

import java.util.Map;

import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AnalyzeController {
	
	@Autowired 
	private AnalyzeService analyzeService;

    @GetMapping("/")
    public String welcome(Model model) {
        model.addAttribute("analyzeObject", new AnalyzeParameter());
        return "index";
    }
    
    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("analyzeObject", new AnalyzeParameter());
        return "index";
    }

    @PostMapping("/analyze-content")
    public String greetingSubmit(Model model, @ModelAttribute AnalyzeParameter analyzeObject, RedirectAttributes redirectAttr) {
    	String givenUrl = analyzeObject.getContent();
    	
    	if(! this.analyzeService.isUrlValid(givenUrl)) {
    		redirectAttr.addFlashAttribute("success", false);
    		redirectAttr.addFlashAttribute("message", "Given url is not valid!");
    		return "redirect:/";
    	}
    	
    	Document document = this.analyzeService.connectToPage(givenUrl);
    	
    	if(document == null) {
    		redirectAttr.addFlashAttribute("success", false);
    		redirectAttr.addFlashAttribute("message", "Couldn't reach to given web site!");
    		return "redirect:/";
    	}
    	
    	model.addAttribute("htmlVersion", this.analyzeService.getVersionOfPage(document));
		model.addAttribute("pageTitle", this.analyzeService.getTitleOfPage(document));
    	model.addAttribute("numberOfHeadings", this.analyzeService.getNumberOfHeadings(document));
    	
    	Map<String, Integer> numberOfLinks = this.analyzeService.getNumberOfHyperMediaLinks(document);
    	model.addAttribute("numberOfExternalLinks", numberOfLinks.containsKey("external") ? numberOfLinks.get("external") : 0);
    	model.addAttribute("numberOfInternalLinks", numberOfLinks.containsKey("internal") ? numberOfLinks.get("internal") : 0);
    	model.addAttribute("isLoginFormExists", this.analyzeService.isPageContainsLogin(document));
    	model.addAttribute("analyzeFor", analyzeObject.getContent());
        return "result";
    }

}
