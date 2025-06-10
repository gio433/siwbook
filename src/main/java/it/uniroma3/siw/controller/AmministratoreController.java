package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.dao.DataIntegrityViolationException;
//import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
//import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import it.uniroma3.siw.dto.AmministratoreDTO;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Amministratore;
import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.model.Recensione;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.LibroService;
import it.uniroma3.siw.service.RecensioneService;
import jakarta.validation.Valid;
import it.uniroma3.siw.service.AutoreService;
import java.util.ArrayList;
import java.util.List;

@Controller
//@RequestMapping("/amministratore")
public class AmministratoreController {

    @Autowired
    private LibroService libroService;
    @Autowired
    private CredentialsService credentialsService;
    @Autowired
    private AutoreService autoreService;
    @Autowired
    private RecensioneService recensioneService;
    
    /* ritorna il riferimento all'amministratore attualmente loggato */
	private Amministratore getAmministratoreCorrente() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal(); //ci fornisce i dati di autenticazione
        String username;
        
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername(); 
        }
        else { 
        	//se non è un oggetto UserDetails usiamo una stringa corrispondente.
            username = principal.toString();
        }
        //recuperiamo l'oggetto credentials tramite valore username
        Credentials credentials = this.credentialsService.findByUsername(username);
        return (credentials != null) ? credentials.getAmministratore() : null;
	}
	
	@GetMapping("/amministratore")
    public String homeAmministratore(Model model) {
		Amministratore amministratoreCorrente = getAmministratoreCorrente();
		AmministratoreDTO amministratoreDTO = new AmministratoreDTO(amministratoreCorrente.getNome(), amministratoreCorrente.getCognome());
		List<Libro> libri = (List<Libro>) libroService.findAll();
        model.addAttribute("libri", libri);
        model.addAttribute("amministratore", amministratoreDTO);
        return "/index";
    }
	
    @GetMapping("/amministratore/newLibro")
    public String showLibroForm(Model model) {
        model.addAttribute("libro", new Libro());
        List<Autore> autori = (List<Autore>) autoreService.findAll();
        model.addAttribute("tuttiGliAutori", autori);
        return "newLibro";
    }

    @PostMapping("/amministratore/newLibro")
    public String aggiungiLibro(@Valid @ModelAttribute("libro") Libro libro,
    							BindingResult bindingResult,
    							@RequestParam(name = "autoriSelezionatiIds", required = false) List<Long> autoriSelezionatiIds,
    							Model model) {
    	
    	if (autoriSelezionatiIds == null || autoriSelezionatiIds.isEmpty()) {
            bindingResult.rejectValue("autori", "NotEmpty.libro.autori", "È necessario selezionare almeno un autore.");
        }
    	
    	if (bindingResult.hasErrors()) {
            model.addAttribute("autori", (List<Autore>) autoreService.findAll());
            return "newLibro";
        }
   
    	if (autoriSelezionatiIds != null && !autoriSelezionatiIds.isEmpty()) {
            List<Autore> autoriDelLibro = new ArrayList<>();
            for (Long autoreId : autoriSelezionatiIds) {
                Autore autore = autoreService.findById(autoreId);
            	if(autore != null) {
            		autoriDelLibro.add(autore);
            	}
            }
            libro.setAutori(autoriDelLibro);
        } else {
            libro.setAutori(new ArrayList<>());
        }
        libroService.save(libro);
        return "redirect:/index";
    }
    
    @GetMapping("/amministratore/newAutore")
    public String showAutoreForm(Model model) {
    	model.addAttribute("autore", new Autore());
    	return "newAutore";
    }
    
    @PostMapping("/amministratore/newAutore")
    public String aggiungiAutore(@ModelAttribute("autore") Autore autore) {
    	autoreService.save(autore);
    	return "redirect:/index";
    }
    
    @GetMapping("/viewAutore/{id}")
    public String getDettaglioAutore(@PathVariable("id") Long id, Model model) {
    	Autore autore = autoreService.findById(id);
    	if(autore != null) {
    		model.addAttribute("autore", autore);
    	} else {
    		 model.addAttribute("messaggioErroreAutore", "L'autore con ID " + id + " non è stato trovato.");
    	}
    	return "viewAutore";
    }
    
    @GetMapping("/amministratore/cancellalibro/{libroId}")
    public String cancellaLibro(@PathVariable("libroId") Long id) {
    	libroService.deleteById(id);
    	return "redirect:/index";
    }
    
    @PostMapping("/amministratore/cancellaautore/{autoreId}")
    public String cancellaAutore(@PathVariable("autoreId") Long id) {
    	Autore autore = autoreService.findById(id);
    	if(!autore.getLibri().isEmpty()) {
    		return "redirect:/amministratore/autori";
    	} else {
    		autoreService.deleteById(id);
    	}
    	return "redirect:/amministratore/autori";
    }
    
    @GetMapping("/amministratore/autori")
    public String vediAutori(Model model) {
    	List<Autore> autori = autoreService.findAll();
    	model.addAttribute("autori", autori);
    	return "autori";
    }
    
    @GetMapping("/recensioni/libro/{id}")
    public String mostraRecensioni(@PathVariable("id") Long libroId, Model model) {
    	Libro libro = libroService.findByIdConRecensioni(libroId);
    	List<Recensione> recensioniDelLibro = libro.getRecensioni();
    	model.addAttribute("libro", libro);
    	model.addAttribute("recensioni", recensioniDelLibro);
    	return "recensioni";
    }
    
    @PostMapping("/amministratore/cancellarecensione/{idRecensione}")
    public String processCancellaRecensione(@PathVariable("idRecensione") Long id) {
    	recensioneService.deleteById(id);
    	return "redirect:/index";
    }
    
    @GetMapping("/amministratore/modificalibro/{id}")
    public String modificaLibro(@PathVariable("id") Long id, Model model) {
        Libro libro = libroService.findById(id);
        model.addAttribute("libro", libro);
        model.addAttribute("tuttiGliAutori", autoreService.findAll());
        return "modificaLibro";
    }

    @PostMapping("/amministratore/modificalibro/{id}")
    public String postModificaLibro(@Valid @ModelAttribute("libro") Libro libro,
    								BindingResult bindingResult,
    								@RequestParam(name = "autori", required = false) List<Long> autori,
    								@PathVariable("id") Long id, Model model) {
        
    	if (bindingResult.hasErrors()) {
            libro.setId(id); 
            model.addAttribute("libro", libro);
            model.addAttribute("tuttiGliAutori", autoreService.findAll());
            return "modificaLibro";
        }
    	List<Autore> autoriLibro = new ArrayList<>();
    	if (autori != null) {
    	    for (Long autoreId : autori) {
    	        Autore autore = autoreService.findById(autoreId); // Chiamata esplicita al service
    	        if (autore != null) {
    	            autoriLibro.add(autore);
    	        }
    	    }
    	}
    	Libro libroModificato = libroService.findById(id);
    	libroModificato.setTitolo(libro.getTitolo());
        libroModificato.setAnno(libro.getAnno());
        libroModificato.setAutori(autoriLibro);
    	libroService.save(libroModificato);
        return "redirect:/index";
    }
    
    /*@PostMapping("/amministratore/cancellarecensione/{idRecensione}") // Path relativo a "/amministratore"
    public String processCancellaRecensione(@PathVariable("idRecensione") Long id,
                                            RedirectAttributes redirectAttributes) {
                                                
        System.out.println("+++++ DEBUG AdminController: INIZIO processCancellaRecensione POST per ID: " + id + " +++++");
        
        try {
            recensioneService.deleteById(id); // Assicurati che questo metodo sia @Transactional nel service
            redirectAttributes.addFlashAttribute("messaggioSuccesso", "Recensione ID " + id + " cancellata con successo.");
            System.out.println("DEBUG AdminController: Recensione ID " + id + " cancellata.");

        } catch (EmptyResultDataAccessException e) { // Specifica per quando deleteById non trova l'entità (alcune implementazioni)
            System.err.println("ERRORE EmptyResultDataAccessException: Recensione con ID " + id + " non trovata durante il tentativo di cancellazione.");
            redirectAttributes.addFlashAttribute("messaggioErrore", "Recensione non trovata. Impossibile cancellare.");
            // Stampa lo stack trace per debug interno
            e.printStackTrace();
        } catch (DataIntegrityViolationException e) { // Per problemi di vincoli (es. altre tabelle che referenziano questa recensione)
             System.err.println("ERRORE DataIntegrityViolationException cancellando recensione ID " + id + ": " + e.getMessage());
             redirectAttributes.addFlashAttribute("messaggioErrore", "Impossibile cancellare la recensione a causa di dipendenze o problemi di integrità dei dati.");
             e.printStackTrace();
        } 
        catch (Exception e) { // Catch-all per qualsiasi altra eccezione imprevista
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            System.err.println("ERRORE IMPREVISTO in processCancellaRecensione per ID " + id + ": " + e.getMessage());
            e.printStackTrace(); // FONDAMENTALE PER IL DEBUG
            System.err.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            redirectAttributes.addFlashAttribute("messaggioErrore", "Si è verificato un errore imprevisto durante la cancellazione della recensione.");
        }
        
        // Reindirizza alla lista di tutte le recensioni (URL pubblico)
        // o a una pagina di gestione recensioni per l'admin
        return "redirect:/recensioni"; 
        // Se l'admin ha una sua pagina per vedere/gestire TUTTE le recensioni (diversa da quella pubblica):
        // return "redirect:/amministratore/listaTutteRecensioni"; 
    }*/
}
