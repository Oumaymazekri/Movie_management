document.addEventListener('DOMContentLoaded', function() {
    // Éléments DOM
    const navLinks = document.querySelectorAll('nav a');
    const sections = document.querySelectorAll('.section');
    const movieList = document.getElementById('movie-list');
    const addMovieForm = document.getElementById('add-movie-form');
    const searchForm = document.getElementById('search-form');
    const searchResults = document.getElementById('search-results');
    const modal = document.getElementById('movie-modal');
    const closeModal = document.querySelector('.close');
    const movieDetails = document.getElementById('movie-details');
    
    // URL de base de l'API
    const API_URL = '/api/movies';
    
    // Navigation
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            e.preventDefault();
            
            // Supprimer la classe active de tous les liens
            navLinks.forEach(l => l.classList.remove('active'));
            
            // Ajouter la classe active au lien cliqué
            this.classList.add('active');
            
            // Afficher la section correspondante
            const targetId = this.id.replace('nav-', '') + '-section';
            sections.forEach(section => {
                section.classList.remove('active');
                if (section.id === targetId) {
                    section.classList.add('active');
                }
            });
            
            // Charger les films si on va à la page d'accueil
            if (targetId === 'home-section') {
                loadMovies();
            }
        });
    });
    
    // Charger tous les films
    function loadMovies() {
        movieList.innerHTML = '<div class="loading">Chargement des films...</div>';
        
        fetch(API_URL)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur lors du chargement des films');
                }
                return response.json();
            })
            .then(movies => {
                displayMovies(movies, movieList);
            })
            .catch(error => {
                movieList.innerHTML = `<div class="error">${error.message}</div>`;
            });
    }
    
    // Afficher les films dans un conteneur
    function displayMovies(movies, container) {
        if (movies.length === 0) {
            container.innerHTML = '<div class="loading">Aucun film trouvé</div>';
            return;
        }
        
        container.innerHTML = '';
        
        movies.forEach(movie => {
            const movieCard = document.createElement('div');
            movieCard.className = 'movie-card';
            movieCard.dataset.id = movie.id;
            
            const releaseYear = new Date(movie.releaseDate).getFullYear();
            
            movieCard.innerHTML = `
                <div class="movie-poster">
                    <i class="fas fa-film"></i>
                </div>
                <div class="movie-info">
                    <h3 class="movie-title">${movie.title}</h3>
                    <p class="movie-director">${movie.director}</p>
                    <div class="movie-meta">
                        <span>${releaseYear} | ${movie.genre || 'Non classé'}</span>
                        <span class="movie-rating"><i class="fas fa-star"></i> ${movie.rating || 'N/A'}</span>
                    </div>
                </div>
            `;
            
            // Ajouter un événement de clic pour afficher les détails
            movieCard.addEventListener('click', () => {
                showMovieDetails(movie.id);
            });
            
            container.appendChild(movieCard);
        });
    }
    
    // Afficher les détails d'un film
    function showMovieDetails(id) {
        fetch(`${API_URL}/${id}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Film non trouvé');
                }
                return response.json();
            })
            .then(movie => {
                const releaseDate = new Date(movie.releaseDate).toLocaleDateString();
                
                movieDetails.innerHTML = `
                    <h2>${movie.title}</h2>
                    <p><strong>Réalisateur:</strong> ${movie.director}</p>
                    <p><strong>Date de sortie:</strong> ${releaseDate}</p>
                    <p><strong>Genre:</strong> ${movie.genre || 'Non classé'}</p>
                    <p><strong>Durée:</strong> ${movie.durationMinutes} minutes</p>
                    <p><strong>Note:</strong> ${movie.rating || 'Non noté'}/10</p>
                    <div class="movie-description">
                        <h3>Synopsis</h3>
                        <p>${movie.description || 'Aucune description disponible.'}</p>
                    </div>
                    <div class="movie-actions">
                        <button class="btn btn-danger" id="delete-movie">Supprimer</button>
                    </div>
                `;
                
                // Ajouter l'événement de suppression
                document.getElementById('delete-movie').addEventListener('click', () => {
                    if (confirm('Êtes-vous sûr de vouloir supprimer ce film ?')) {
                        deleteMovie(id);
                    }
                });
                
                // Afficher le modal
                modal.style.display = 'block';
            })
            .catch(error => {
                alert(error.message);
            });
    }
    
    // Fermer le modal
    closeModal.addEventListener('click', () => {
        modal.style.display = 'none';
    });
    
    window.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.style.display = 'none';
        }
    });
    
    // Ajouter un film
    addMovieForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const formData = {
            title: document.getElementById('title').value,
            director: document.getElementById('director').value,
            releaseDate: document.getElementById('releaseDate').value,
            description: document.getElementById('description').value,
            durationMinutes: parseInt(document.getElementById('durationMinutes').value),
            genre: document.getElementById('genre').value,
            rating: parseFloat(document.getElementById('rating').value)
        };
        
        fetch(API_URL, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(formData)
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erreur lors de l\'ajout du film');
            }
            return response.json();
        })
        .then(movie => {
            // Réinitialiser le formulaire
            addMovieForm.reset();
            
            // Afficher un message de succès
            const successMessage = document.createElement('div');
            successMessage.className = 'success';
            successMessage.textContent = 'Film ajouté avec succès !';
            addMovieForm.prepend(successMessage);
            
            // Supprimer le message après 3 secondes
            setTimeout(() => {
                successMessage.remove();
            }, 3000);
        })
        .catch(error => {
            // Afficher un message d'erreur
            const errorMessage = document.createElement('div');
            errorMessage.className = 'error';
            errorMessage.textContent = error.message;
            addMovieForm.prepend(errorMessage);
            
            // Supprimer le message après 3 secondes
            setTimeout(() => {
                errorMessage.remove();
            }, 3000);
        });
    });
    
    // Rechercher des films
    searchForm.addEventListener('submit', function(e) {
        e.preventDefault();
        
        const searchTerm = document.getElementById('search-input').value;
        const searchType = document.getElementById('search-type').value;
        
        if (!searchTerm) {
            return;
        }
        
        searchResults.innerHTML = '<div class="loading">Recherche en cours...</div>';
        
        fetch(`${API_URL}/search?${searchType}=${searchTerm}`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Erreur lors de la recherche');
                }
                return response.json();
            })
            .then(movies => {
                displayMovies(movies, searchResults);
            })
            .catch(error => {
                searchResults.innerHTML = `<div class="error">${error.message}</div>`;
            });
    });
    
    // Supprimer un film
    function deleteMovie(id) {
        fetch(`${API_URL}/${id}`, {
            method: 'DELETE'
        })
        .then(response => {
            if (!response.ok) {
                throw new Error('Erreur lors de la suppression du film');
            }
            
            // Fermer le modal
            modal.style.display = 'none';
            
            // Recharger la liste des films
            loadMovies();
            
            // Afficher un message de succès
            alert('Film supprimé avec succès !');
        })
        .catch(error => {
            alert(error.message);
        });
    }
    
    // Charger les films au démarrage
    loadMovies();
});