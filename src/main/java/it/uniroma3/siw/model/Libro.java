package it.uniroma3.siw.model;

import jakarta.persistence.Id;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Libro {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @NotBlank
    private String titolo;
    private int anno;
    @ManyToMany
    private List<Autore> autori;
    @OneToMany(mappedBy = "libro")
    private List<Recensione> recensioni;
    @ElementCollection
    @CollectionTable(name = "libro_immagini")
    @Column(name = "immagine_url")
    private List<String> immagini;
    
    //costruttore
    public Libro() {}

    // setter
    public void setId(Long id) { this.id = id; }
    public void setTitolo(String titolo) { this.titolo = titolo; }
    public void setAnno(int anno) { this.anno = anno; }
    public void setAutori(List<Autore> autori) { this.autori = autori; }
    public void setRecensioni(List<Recensione> recensioni) { this.recensioni = recensioni; }
    public void setImmagini(List<String> immagini) { this.immagini = immagini; }

    // getter
    public Long getId() { return id; }
    public String getTitolo() { return titolo; }
    public int getAnno() { return anno; }
    public List<Autore> getAutori() { return this.autori; }
    public List<Recensione> getRecensioni() { return recensioni; }
    public List<String> getImmagini() { return immagini; }
}
