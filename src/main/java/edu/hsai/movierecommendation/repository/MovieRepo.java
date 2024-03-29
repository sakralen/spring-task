package edu.hsai.movierecommendation.repository;

import edu.hsai.movierecommendation.repository.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MovieRepo extends JpaRepository<Movie, Long> {
        Optional<Movie> findByName(@Param("name") String name);
        List<Movie> findByGenreId(Long genreId);
}
