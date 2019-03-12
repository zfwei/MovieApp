package com.jskillcloud.movie.service;

import com.jskillcloud.movie.mapper.MovieMapper;
import com.jskillcloud.movie.model.Movie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MovieService {
    @Autowired
    MovieMapper movieMapper;

    public List<Movie> findMoviesByUserId(Long userId) {
        List<Movie> movies = movieMapper.findByUserId(userId);
        return movies;
    }

    @Transactional
    public void saveMovie(Movie movie) {
        movieMapper.saveMovie(movie);
    }
}
