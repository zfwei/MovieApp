import React, {Component} from 'react'
import { Route, Redirect, Switch, Link} from 'react-router-dom'
import axios from 'axios'

import './App.css';

import SearchBar from './components/SearchBar';
import MovieList from './components/MovieList';
import LoginForm from './components/LoginForm';
import RegisterForm from './components/RegisterForm';
import FlashMessages from './components/FlashMessages';
import NotFound from './components/NotFound';
import SavedMovies from './components/SavedMovies';

const API_URL = 'http://www.omdbapi.com/?apikey=c5a8df09&s='; // sample
const USERS_SERVICE_URL = process.env.REACT_APP_AUTH_SERVICE_URL;
const MOVIES_SERVICE_URL = process.env.REACT_APP_MOVIE_SERVICE_URL;

class App extends Component {

    constructor (props) {
        super (props)
        this.state = {
            movies: [],
            saved: [],
            flashMessages: [],
            isAuthenticated: window.localStorage.authToken? true : false
        }
        this.registerUser = this.registerUser.bind(this)
        this.loginUser = this.loginUser.bind(this)
        this.logoutUser = this.logoutUser.bind(this)
        this.deleteFlashMessage = this.deleteFlashMessage.bind(this)
        this.createFlashMessage = this.createFlashMessage.bind(this)
        this.saveMovie = this.saveMovie.bind(this)
        this.getMovies = this.getMovies.bind(this)
        this.searchMovie('lion')
        if (this.state.isAuthenticated) {
            this.getMovies()
        }
    }
    searchMovie(term) {
        axios.get(`${API_URL}${term}`)
            .then((res) => { 
                if (res.data.Response === 'True') {
                    this.setState({ movies: res.data.Search }); 
                } else {
                    this.setState({movies : []});
                    this.createFlashMessage(res.data.Error, 'error');
                }
            })
            .catch((err) => { console.log(err); })
    }
    createFlashMessage (text, type = 'success') {
        const message = { text, type }
        this.setState( {
            flashMessages: [...this.state.flashMessages, message]
        })
    }
    deleteFlashMessage (index) {
        if (index > 0) {
            this.setState({
                flashMessages: [
                    ...this.state.flashMessages.slice(0, index),
                    ...this.state.flashMessages.slice(index + 1)
                ]
            })
        } else {
            this.setState({
                flashMessages: [...this.state.flashMessages.slice(index + 1)]
            })
        }
    }
    registerUser (userData, callback) {
        return axios.post(`${USERS_SERVICE_URL}/auth/signup`, userData)
            .then((res) => {
                this.createFlashMessage('You successfully registered! Please Login!')
                this.props.history.push('/')
            })
            .catch((error) => {
                const errorMessage = error.response.data.message
                callback(errorMessage)
            })
    }
    loginUser (userData, callback) {
        return axios.post(`${USERS_SERVICE_URL}/auth/signin`, userData)
            .then((res) => {
                window.localStorage.setItem('authToken', res.data.accessToken)
                this.setState({ isAuthenticated: true })
                this.createFlashMessage('You successfully logged in! Welcome!')
                this.props.history.push('/')
                this.getMovies()
            })
            .catch((error) => {
                const errorMessage = error.response.data.message;
                callback(errorMessage)
            })
    }
    logoutUser (e) {
        e.preventDefault()
        window.localStorage.clear()
        this.setState({ isAuthenticated: false })
        this.props.history.push('/')
        this.createFlashMessage('You are now logged out.')
    }
    saveMovie (movie) {
        const options = {
            url: `${MOVIES_SERVICE_URL}/movie/save`,
            method: 'post',
            data: {
                title: movie
            },
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${window.localStorage.authToken}`
            }
        };
        return axios(options)
            .then((res) => { 
                const successMessage = "Movie '" + movie + "' has been saved successfully!"
                this.createFlashMessage(successMessage)
                this.getMovies() 
            })
            .catch((error) => {console.log(error); })
    }
    getMovies() {
        const options = {
            url: `${MOVIES_SERVICE_URL}/movie/get`,
            method: 'get',
            headers: {
                'Content-Type': 'application/json',
                Authorization: `Bearer ${window.localStorage.authToken}`
            }
        };
        return axios(options)
            .then((res) => {
                this.setState( {saved: res.data });
            })
            .catch((err) => { console.log(err); })
    }
    render () {
        const {isAuthenticated, flashMessages} = this.state
        return (
            <div className='App container'>
                <br/>
                <FlashMessages
                    deleteFlashMessage={this.deleteFlashMessage}
                    messages={flashMessages}
                />
                <Switch>
                    <Route exact path='/' render={() => (
                        isAuthenticated
                        ? <div className="container text-center">
                            <h1>OMDB Movie Search</h1>
                            <SearchBar searchMovie={this.searchMovie.bind(this)} />
                                <button className="btn btn-link" onClick={this.logoutUser}>Logout</button>&nbsp;&#124;&nbsp;<Link to='/collection'>My Collection</Link>
                                <br/><br/><br/>
                                <MovieList
                                    movies={this.state.movies}
                                    saveMovie={this.saveMovie}
                                />
                          </div>
                        : <Redirect to={{
                            pathname: '/login'
                        }} />
                    )} />
                    <Route path='/register' render={() => (
                        isAuthenticated
                        ? <Redirect to='/' />
                        : <RegisterForm
                            createFlashMessage={this.createFlashMessage}
                            registerUser={this.registerUser} />
                    )} />
                    <Route path='/login' render={() => (
                        isAuthenticated
                        ? <Redirect to='/' />
                        : <LoginForm
                            createFlashMessage={this.createFlashMessage}
                            loginUser={this.loginUser} />
                    )} />
                    <Route path='/collection' render={() => (
                        isAuthenticated
                        ? <SavedMovies
                            createFlashMessage={this.createFlashMessage}
                            saved={this.state.saved} />
                        : <Redirect to={{ pathname: '/login' }} />
                    )} />
                    <Route component={NotFound} />
                </Switch>
            </div>
        )
    }
}

export default App