import React from 'react'
import MovieCard from './MovieCard';

const MovieList = (props) => {
    let sett = new Set();
    let dedupedMovies = props.movies
                        .filter(movie => {
                            if (sett.has(movie.imdbID)) {
                                return false;
                            } else {
                                sett.add(movie.imdbID);
                                return true;
                            }
                        })
    return (
        <div className="text-center">
            {   
                dedupedMovies
                .filter(movie => movie.Poster !== 'N/A')
                .map(movie => (
                    <MovieCard
                        key={movie.imdbID}
                        title={movie.Title}
                        posterUrl={movie.Poster}
                        saveMovie={props.saveMovie}
                    />
                ))}
        </div>
    )
}

export default MovieList;
