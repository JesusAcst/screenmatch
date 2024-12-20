package com.aluracursos.screenmatch.model;


import java.util.OptionalDouble;

public class Serie {
   private String titulo;
    private Integer totalDeTemporadas;
    private Double evaluacion;
    private Categoria genero;
    private String actores;
    private String poster;
    private String sinopsis;

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
  return "Serie{" +
          "titulo='" + titulo + '\'' +
          ", totalDeTemporadas=" + totalDeTemporadas +
          ", evaluacion=" + evaluacion +
          ", genero=" + genero +
          ", actores='" + actores + '\'' +
          ", poster='" + poster + '\'' +
          ", sinopsis='" + sinopsis + '\'' +
          '}';
 }

 //Setters and getters:
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
}


