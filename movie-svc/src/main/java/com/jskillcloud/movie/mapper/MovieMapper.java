package com.jskillcloud.movie.mapper;

import com.jskillcloud.movie.model.Movie;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MovieMapper {
    List<Movie> findByUserId(Long userId);

    int saveMovie(@Param("movie")Movie movie);

    // for testing
    boolean deleteAll();
}
