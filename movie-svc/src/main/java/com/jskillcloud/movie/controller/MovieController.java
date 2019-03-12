package com.jskillcloud.movie.controller;

import com.jskillcloud.movie.dto.ApiResponse;
import com.jskillcloud.movie.model.Movie;
import com.jskillcloud.movie.security.CurrentUser;
import com.jskillcloud.movie.security.UserPrincipal;
import com.jskillcloud.movie.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/movie")
public class MovieController {
    @Autowired
    MovieService movieService;

    @GetMapping("/get")
    public ResponseEntity<?> get(@CurrentUser UserPrincipal currentUser) {
        List<Movie> movies = movieService.findMoviesByUserId(currentUser.getId());

        return ResponseEntity.ok(movies);
    }

    @PostMapping("/save")
    public ResponseEntity<?> save(@CurrentUser UserPrincipal currentUser, @RequestBody Movie movie) {
        movie.setUserId(currentUser.getId());
        movieService.saveMovie(movie);

        return ResponseEntity.ok(new ApiResponse(true, "movie saved"));
    }

}
