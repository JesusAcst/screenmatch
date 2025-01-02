package com.aluracursos.screenmatch.model;


import jakarta.persistence.*;

import java.util.List;
import java.util.OptionalDouble;

@Entity
@Table(name="series")
public class Serie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String titulo;
    private Integer totalDeTemporadas;
    private Double evaluacion;
    @Enumerated(EnumType.STRING)
    private Categoria genero;
    private String actores;
    private String poster;
    private String sinopsis;
    @OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Episodio> episodios;

public Serie(){}

public Serie(DatoSerie datoSerie){
     this.titulo = datoSerie.titulo();
     this.totalDeTemporadas = datoSerie.totalDeTemporadas();
     this.evaluacion = OptionalDouble.of(Double.valueOf(datoSerie.evaluacion())).orElse(0);
     this.poster = datoSerie.poster();
     this.genero = Categoria.fromString(datoSerie.genero().split(",")[0].trim());
     this.actores = datoSerie.actores();
     this.sinopsis = datoSerie.sinopsis();
}

 @Override
 public String toString() {
  return "titulo='" + titulo + '\'' +
          ", totalDeTemporadas=" + totalDeTemporadas +
          ", evaluacion=" + evaluacion +
          ", genero=" + genero +
          ", actores='" + actores + '\'' +
          ", poster='" + poster + '\'' +
          ", sinopsis='" + sinopsis + '\'' +
          ", episodios ='" + episodios + '\'' +
          '}';
 }

 //Setters and getters:

 public Long getId() {
  return id;
 }

 public void setId(Long id) {
  this.id = id;
 }

 public String getTitulo() {
  return titulo;
 }

 public void setTitulo(String titulo) {
  this.titulo = titulo;
 }

 public Integer getTotalDeTemporadas() {
  return totalDeTemporadas;
 }

 public void setTotalDeTemporadas(Integer totalDeTemporadas) {
  this.totalDeTemporadas = totalDeTemporadas;
 }

 public Double getEvaluacion() {
  return evaluacion;
 }

 public void setEvaluacion(Double evaluacion) {
  this.evaluacion = evaluacion;
 }

 public Categoria getGenero() {
  return genero;
 }

 public void setGenero(Categoria genero) {
  this.genero = genero;
 }

 public String getActores() {
  return actores;
 }

 public void setActores(String actores) {
  this.actores = actores;
 }

 public String getPoster() {
  return poster;
 }

 public void setPoster(String poster) {
  this.poster = poster;
 }

 public String getSinopsis() {
  return sinopsis;
 }

 public void setSinopsis(String sinopsis) {
  this.sinopsis = sinopsis;
 }

    public List<Episodio> getEpisodios() {
        return episodios;
    }

    public void setEpisodios(List<Episodio> episodios) {
        episodios.forEach(e->e.setSerie(this));
        this.episodios = episodios;
    }

}


