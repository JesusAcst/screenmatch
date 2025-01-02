package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvertirDatos;

import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    //Declaracion de constantes
    private final String URL_BASE = "https://omdbapi.com/?t=";
    private final String API_KEY = "&apikey=d943d5a4";
    //Instancias necesarias
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private ConvertirDatos convertirDatos = new ConvertirDatos();
    private List<DatoSerie> datoSeries = new ArrayList<>();
    private SerieRepository respositorio;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;
    private SerieRepository repositorio;


    public Principal(SerieRepository repository) {
        this.repositorio=repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Mostrar series buscadas
                    4 - Buscar serie por titulo
                    5 - Top 5 series
                    6 - Buscar series por categoria
                    7 - Filtrar series por temporadas y evaluacion
                    8 - Buscar episodios por nombre
                    9 - Top 5 episodios por Serie
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
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    filtrarSeriesPorTemporadaYEvaluacion();
                    break;
                case 8:
                    buscarEpisodiosPorTitulo();
                    break;
                case 9:
                    buscarTop5Episodios();
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
            mostrarSeriesBuscadas();
            System.out.println("Escoge la serie que deseas ver los episodios");
            var nombreSerie = teclado.nextLine();

            Optional<Serie> serie = series.stream()
                    .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                    .findFirst();

            if(serie.isPresent()){
                var serieEncontrada = serie.get();
                List<DatosTemporadas> temporadas = new ArrayList<>();

                for (int i = 1; i <= serieEncontrada.getTotalDeTemporadas(); i++) {
                    var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                    DatosTemporadas datosTemporadas = convertirDatos.obtenerDatos(json, DatosTemporadas.class);
                    temporadas.add(datosTemporadas);
                }
                temporadas.forEach(System.out::println);

                List<Episodio> episodios = temporadas.stream()
                        .flatMap(d -> d.episodios().stream()
                                .map(e -> new Episodio(d.numero(), e)))
                        .collect(Collectors.toList());

                serieEncontrada.setEpisodios(episodios);
                repositorio.save(serieEncontrada);
            }
        }
        private void buscarSerieWeb() {
            DatoSerie datos = getDatoSerie();
            Serie serie = new Serie(datos);
            repositorio.save(serie);
            //datoSeries.add(datos);
            System.out.println(datos);
        }


    private void mostrarSeriesBuscadas() {
         series = repositorio.findAll();

        series.stream()
                .sorted(Comparator.comparing(Serie::getGenero))
                .forEach(System.out::println);

    }


    private void buscarSeriesPorTitulo() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        Optional<Serie> serieBuscada = repositorio.findByTituloContainsIgnoreCase(nombreSerie);

        if(serieBuscada.isPresent()){
            System.out.println("Serie buscada: "+serieBuscada.get());
        }else {
            System.out.println("La serie no fue encontrada");
        }
    }

    public void buscarTop5Series(){
        List<Serie> topSeries = repositorio.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s-> System.out.println("Serie: "+s.getTitulo()+"Evaluacion: "+s.getEvaluacion()));
    }


    private void buscarSeriesPorCategoria() {
        System.out.println("Ecriba el genero o categoria de la serie que desea");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspañol(genero);
        List<Serie> seriesPorCategoria = repositorio.findByGenero(categoria);
        System.out.println("Las series de la categoria: "+genero);
        seriesPorCategoria.forEach(System.out::println);
    }

    public void filtrarSeriesPorTemporadaYEvaluacion(){
        System.out.println("¿Filtrar séries con cuántas temporadas? ");
        var totalTemporadas = teclado.nextInt();
        teclado.nextLine();
        System.out.println("¿Com evaluación apartir de cuál valor? ");
        var evaluacion = teclado.nextDouble();
        teclado.nextLine();
        List<Serie> filtroSeries = repositorio.seriesPorTemparadaYEvaluacion(totalTemporadas,evaluacion);
        System.out.println("*** Series filtradas ***");
        filtroSeries.forEach(s ->
                System.out.println(s.getTitulo() + "  - evaluacion: " + s.getEvaluacion()));
    }

    private void  buscarEpisodiosPorTitulo(){
        System.out.println("Escribe el nombre del episodio que deseas buscar");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repositorio.episodiosPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s Temporada %s Episodio %s Evaluación %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));

    }

    private void buscarTop5Episodios(){
        buscarSeriesPorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repositorio.top5Episodios(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Serie: %s - Temporada %s - Episodio %s - Evaluación %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));

        }
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


