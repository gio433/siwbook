package it.uniroma3.siw.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Optional;
import it.uniroma3.siw.model.Libro;
import it.uniroma3.siw.repository.LibroRepository;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LibroService {

    @Autowired
    private LibroRepository libroRepository;

    public List<Libro> findAll() {
        return (List<Libro>) libroRepository.findAll();
    }

    public Libro findById(Long id) {
        return libroRepository.findById(id).get();
    }
    
    @Transactional
    public Libro save(Libro libro) {
        return libroRepository.save(libro);
    }

    public void deleteById(Long id) {
        libroRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public Libro findByIdConRecensioni(Long id) {
        Optional<Libro> result = libroRepository.findByIdWithRecensioniAndUtenti(id);
        return result.orElse(null);
    }
}