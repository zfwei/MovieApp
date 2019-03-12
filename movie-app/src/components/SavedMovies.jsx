import React from 'react';
import { Link } from 'react-router-dom';

const SavedMovies = (props) => {

    const hasSavedMovies = props.saved.length > 0;
    let result;

    if (!hasSavedMovies) {
        result = <div>
            <h1>Collection</h1>
            <hr/>
            <div><Link to='/'>Home</Link></div>
            <br/><br/>
            <div>No saved movie yet, please search and add first!</div>
        </div>
    } else {
        result = <div>
            <h1>Collection</h1>
            <hr/>
            <div><Link to='/'>Home</Link></div>
            <br/><br/>
            <table className='table table-hover'>
                <thead>
                  <tr>
                      <th>ID</th>
                      <th>Title</th>
                      <th>Date Added</th>
                      <th></th>
                  </tr>
                </thead>
                <tbody>
                  {
                    props.saved.map((movie) => {
                        return (
                            <tr key={ movie.id }>
                                <td>{ movie.id }</td>
                                <td>{ movie.title }</td>
                                <td>{ movie.createdAt }</td>
                            </tr>
                        )
                    })
                  }
                </tbody>
            </table>
        </div>
    }

    return (
        result
    )
}

export default SavedMovies;