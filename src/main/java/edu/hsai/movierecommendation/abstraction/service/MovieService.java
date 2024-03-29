package edu.hsai.movierecommendation.abstraction.service;

import edu.hsai.movierecommendation.repository.Movie;

import java.util.List;

public interface MovieService {
    MovieDto getById(Long id);
    MovieDto getByName(String name);
    List<MovieDto> getAll();
    List<MovieDto> getByGenreId(Long genreId);
    record MovieDto(Long id, String name, String genreName) {
        public static MovieDto fromDbEntity(Movie movie) {
            return new MovieDto(
                    movie.getId(),
                    movie.getName(),
                    movie.getGenre().getName()
            );
        }
    }
}
