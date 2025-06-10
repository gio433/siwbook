package it.uniroma3.siw.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import it.uniroma3.siw.model.Autore;
import it.uniroma3.siw.repository.AutoreRepository;

@Service
public class AutoreService {
	@Autowired
    private AutoreRepository autoreRepository;

    public List<Autore> findAll() {
        return (List<Autore>) autoreRepository.findAll();
    }

    public Autore findById(Long id) {
        return autoreRepository.findById(id).orElse(null);
    }

    public Autore save(Autore autore) {
        return autoreRepository.save(autore);
    }

    public void deleteById(Long id) {
        autoreRepository.deleteById(id);
    }
    
}
