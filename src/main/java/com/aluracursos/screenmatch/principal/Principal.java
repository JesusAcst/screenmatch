package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.DatoSerie;
import com.aluracursos.screenmatch.model.DatosTemporadas;
import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvertirDatos;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    //Instancias necesarias
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvertirDatos convertirDatos = new ConvertirDatos();
    private List<DatoSerie> datoSeries = new ArrayList<>();

    //https://omdbapi.com/?t=game+of+thrones&apikey=d943d5a4
    //Declaracion de constantes
    private final String URL_BASE = "https://omdbapi.com/?t=";
    private final String API_KEY = "&apikey=d943d5a4";

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    mostrarSeriesBuscadas();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    //Interaccion con el usuario
        private DatoSerie getDatoSerie() {
            System.out.println("Escribe el nombre de la serie que deseas buscar");
            var nombreSerie = teclado.nextLine();

            //Busca los datos generales de las series
            var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
            System.out.println(json);
            DatoSerie datos = convertirDatos.obtenerDatos(json, DatoSerie.class);
            return datos;
        }

        //Busca los datos de las temporadas
        private void buscarEpisodioPorSerie () {
            DatoSerie datoSerie = getDatoSerie();
            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= datoSerie.totalDeTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + datoSerie.titulo().replace(" ", "+") + "&season=" + i + API_KEY);
                var datosTemporadas = convertirDatos.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporadas);
            }
            temporadas.forEach(System.out::println);
        }
        private void buscarSerieWeb() {
            DatoSerie datos = getDatoSerie();
            datoSeries.add(datos);
            System.out.println(datos);
        }


    private void mostrarSeriesBuscadas() {
        List<Serie> series = new ArrayList<>();
        series = datoSeries.stream()
                .map(d->new Serie(d))
                .collect(Collectors.toList());

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }
}







        //temporadas.forEach(System.out::println);


        //Mostrar solo el itulo de los episodios de las temporadas
    /*    for (int i = 0; i < datos.totalDeTemporadas(); i++) {
            List<DatosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
            for (int j = 0; j < episodiosTemporada.size(); j++) {
                System.out.println(episodiosTemporada.get(j).titulo());
            }
        }

        //Uso de funciones lambda para simplificar codigo, imprime lo mismo que el codigo de arriba
        System.out.println();// para separar entre uno y otro
        System.out.println();
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())) );
*/
        //Convertir todas las informaciones a una lista del tipo DatosEpisodio
/*        List<DatosEpisodio> datosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());
*/
        //Top 5 episodios
       /* System.out.println("\nTop 5 mejores episodios:");
        datosEpisodios.stream()
                .filter(e -> !e.evaluacion().equalsIgnoreCase("N/A"))
                .peek(e -> System.out.println("\nPrimer filtro (N/A)"+e))
                .sorted(Comparator.comparing(DatosEpisodio::evaluacion).reversed())
                .peek(e-> System.out.println("\nSegundo filtro (M/m)"+e))
                .map(e->e.titulo().toUpperCase())
                .peek(e->System.out.println("\nTercer filtro Mayuscula (m>n)"))
                .limit(5)
                .forEach(System.out::println);
*/
        //Convirtiendo los datos a una lista del tipo Episodio
       /* List<Episodio> episodios=temporadas.stream()
                .flatMap(t->t.episodios().stream()
                        .map(d->new Episodio(t.numero(),d)))
                .collect(Collectors.toList());
*/
        //episodios.forEach(System.out::println);

        //Busqueda de episodios a partir de x año
    /*    System.out.println("Apartir de que año quiere ver  los episodios");
        var fecha=teclado.nextInt();
        teclado.nextLine();

       DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy"); //Para darle formato que queremos a la fecha que se imprime
        LocalDate fechaBusqueda = LocalDate.of(fecha, 1, 1);

    */

   /*      episodios.stream()
                .filter(e -> e.getFechaDeLanzamiento() != null && e.getFechaDeLanzamiento().isAfter(fechaBusqueda))
                .forEach(e -> System.out.println(
                        "Temporada "+e.getTemporada()+"Episodio "+e.getTitulo()+
                                "Fecha de lanzamiento "+e.getFechaDeLanzamiento().format(dtf)
                ));*/

        //Busca episodios por pedazo de titulo
    /*    System.out.println("Escriba el titulo del episodio que busca: ");
        var pedazoTitulo = teclado.nextLine();
        Optional<Episodio> episodioBuscado = episodios.stream()
                .filter(e -> e.getTitulo().toUpperCase().contains(pedazoTitulo.toUpperCase()))
                .findFirst();
        if (episodioBuscado.isPresent()){
            System.out.println("Episodio encontrado!");
            System.out.println("los datos son: "+episodioBuscado.get());
        }else {
            System.out.println("Episodio no encontrado");
        }
    */
        //Creando un mapa de datos por temporada y recolectando estadisticas
       /* Map<Integer, Double> evaluacionesPorTemporada = episodios.stream()
                .filter(e->e.getEvaluacion()>0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada,
                        Collectors.averagingDouble(Episodio::getEvaluacion)));
        System.out.println(evaluacionesPorTemporada);
        DoubleSummaryStatistics est = episodios.stream()
                .filter(e->e.getEvaluacion()>0.0)
                .collect(Collectors.summarizingDouble(Episodio::getEvaluacion));
        System.out.println("Media de las evaluaciones: "+est.getAverage());
        System.out.println("Episodio mejor evaluado: "+est.getMax());
        System.out.println("Episodio peor evaluado: "+est.getMin());
*/


