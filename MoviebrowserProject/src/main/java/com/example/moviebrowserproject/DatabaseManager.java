package com.example.moviebrowserproject;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String DATABASE_URL = "jdbc:sqlite:movies.db";
    private static DatabaseManager instance;
    private Connection connection;

    private DatabaseManager() {
        initializeDatabase();
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabase() {
        try {
            connection = DriverManager.getConnection(DATABASE_URL);
            createTables();
            populateMovies();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() throws SQLException {
        String createMoviesTable = """
            CREATE TABLE IF NOT EXISTS movies (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                genre TEXT NOT NULL,
                cast TEXT NOT NULL,
                duration INTEGER NOT NULL,
                imdb_rating REAL NOT NULL,
                plot_summary TEXT NOT NULL,
                poster_image_path TEXT NOT NULL
            )
        """;

        String createWatchLaterTable = """
            CREATE TABLE IF NOT EXISTS watch_later (
                movie_id INTEGER PRIMARY KEY,
                FOREIGN KEY (movie_id) REFERENCES movies(id)
            )
        """;

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createMoviesTable);
            stmt.execute(createWatchLaterTable);
        }
    }

    private void populateMovies() throws SQLException {
        // Check if movies already exist and if we need to update
        String checkQuery = "SELECT COUNT(*) FROM movies";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(checkQuery)) {
            if (rs.next() && rs.getInt(1) >= 25) {
                return; // All movies already exist
            } else if (rs.next() && rs.getInt(1) > 0) {
                // Clear existing movies to repopulate with updated list
                stmt.execute("DELETE FROM movies");
                stmt.execute("DELETE FROM watch_later"); // Clear watch later references
            }
        }

        // Insert sample movies
        String insertQuery = """
            INSERT INTO movies (title, genre, cast, duration, imdb_rating, plot_summary, poster_image_path) 
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        Object[][] movieData = {
                {"The Shawshank Redemption", "Drama", "Tim Robbins, Morgan Freeman", 142, 9.3,
                        "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
                        "images/shawshank.jpg"},
                {"The Godfather", "Crime", "Marlon Brando, Al Pacino", 175, 9.2,
                        "The aging patriarch of an organized crime dynasty transfers control of his clandestine empire to his reluctant son.",
                        "images/godfather.jpg"},
                {"The Dark Knight", "Action", "Christian Bale, Heath Ledger", 152, 9.0,
                        "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological and physical tests.",
                        "images/darkknight.jpg"},
                {"Forrest Gump", "Drama", "Tom Hanks, Robin Wright", 142, 8.8,
                        "The presidencies of Kennedy and Johnson, the Vietnam War, and other historical events unfold from the perspective of an Alabama man with an IQ of 75.",
                        "images/forrestgump.jpg"},
                {"Inception", "Sci-Fi", "Leonardo DiCaprio, Marion Cotillard", 148, 8.8,
                        "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
                        "images/inception.jpg"},
                {"The Matrix", "Sci-Fi", "Keanu Reeves, Laurence Fishburne", 136, 8.7,
                        "A computer programmer is led to fight an underground war against powerful computers who have constructed his entire reality with a system called the Matrix.",
                        "images/matrix.jpg"},
                {"Goodfellas", "Crime", "Robert De Niro, Ray Liotta", 146, 8.7,
                        "The story of Henry Hill and his life in the mob, covering his relationship with his wife Karen Hill and his mob partners Jimmy Conway and Tommy DeVito.",
                        "images/goodfellas.jpg"},
                {"The Lord of the Rings: The Return of the King", "Fantasy", "Elijah Wood, Viggo Mortensen", 201, 8.9,
                        "Gandalf and Aragorn lead the World of Men against Sauron's army to draw his gaze from Frodo and Sam as they approach Mount Doom with the One Ring.",
                        "images/lotr3.jpg"},
                {"Interstellar", "Sci-Fi", "Matthew McConaughey, Anne Hathaway", 169, 8.6,
                        "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival.",
                        "images/interstellar.jpg"},
                {"Parasite", "Thriller", "Kang-ho Song, Sun-kyun Lee", 132, 8.6,
                        "Act of violence, greed and class discrimination threaten the newly formed symbiotic relationship between the wealthy Park family and the destitute Kim clan.",
                        "images/parasite.jpg"},
                {"The Departed", "Crime", "Leonardo DiCaprio, Matt Damon", 151, 8.5,
                        "An undercover cop and a police informant play a cat-and-mouse game with each other while infiltrating an Irish gang in South Boston.",
                        "images/departed.jpg"},
                {"Whiplash", "Drama", "Miles Teller, J.K. Simmons", 106, 8.5,
                        "A promising young drummer enrolls at a cut-throat music conservatory where his dreams of greatness are mentored by an instructor who will stop at nothing to realize a student's potential.",
                        "images/whiplash.jpg"},
                {"The Prestige", "Mystery", "Christian Bale, Hugh Jackman", 130, 8.5,
                        "After a tragic accident, two stage magicians engage in a battle to create the ultimate illusion while sacrificing everything they have to outwit each other.",
                        "images/prestige.jpg"},
                {"The Silence of the Lambs", "Thriller", "Jodie Foster, Anthony Hopkins", 118, 8.6,
                        "A young F.B.I. cadet must receive the help of an incarcerated and manipulative cannibal killer to help catch another serial killer, a madman who skins his victims.",
                        "images/silencelambs.jpg"},
                {"Spirited Away", "Animation", "Rumi Hiiragi, Miyu Irino", 125, 9.2,
                        "During her family's move to the suburbs, a sullen 10-year-old girl wanders into a world ruled by gods, witches, and spirits, and where humans are changed into beasts.",
                        "images/spiritedaway.jpg"},
                {"One Piece Film: Red", "Animation", "Mayumi Tanaka, Akio Otsuka", 115, 8.2,
                        "Uta, the most beloved singer in the world, reveals herself to the public for the first time at a live concert, and the Straw Hats and many other fans gather to enjoy her otherworldly voice.",
                        "images/onepiecered.jpg"},
                {"Grave of the Fireflies", "Animation", "Tsutomu Tatsumi, Ayano Shiraishi", 89, 8.5,
                        "A teenage boy and his little sister struggle to survive in Japan during World War II after their mother dies in an air raid and their father goes missing in action.",
                        "images/graveoffireflies.jpg"},
                {"The Pianist", "Biography", "Adrien Brody, Thomas Kretschmann", 150, 8.5,
                        "A Polish Jewish musician struggles to survive the destruction of the Warsaw ghetto during World War II with the help of a sympathetic German officer.",
                        "images/pianist.jpg"},
                {"Haikyu!! The Dumpster Battle", "Animation", "Ayumu Murase, Kaito Ishikawa", 85, 8.7,
                        "The long-awaited showdown between Karasuno and Nekoma high school volleyball teams finally takes place in this climactic battle of crows versus cats.",
                        "images/haikyudumpster.jpg"},
                {"My Neighbor Totoro", "Animation", "Noriko Hidaka, Chika Sakamoto", 86, 8.2,
                        "When two girls move to the country to be near their ailing mother, they have adventures with the wondrous forest spirits who live nearby, including the massive cuddly Totoro.",
                        "images/totoro.jpg"}
        };

        try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
            for (Object[] movie : movieData) {
                pstmt.setString(1, (String) movie[0]);
                pstmt.setString(2, (String) movie[1]);
                pstmt.setString(3, (String) movie[2]);
                pstmt.setInt(4, (Integer) movie[3]);
                pstmt.setDouble(5, (Double) movie[4]);
                pstmt.setString(6, (String) movie[5]);
                pstmt.setString(7, (String) movie[6]);
                pstmt.executeUpdate();
            }
        }
    }

    public ObservableList<Movie> getAllMovies() {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        String query = "SELECT * FROM movies";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("cast"),
                        rs.getInt("duration"),
                        rs.getDouble("imdb_rating"),
                        rs.getString("plot_summary"),
                        rs.getString("poster_image_path")
                );

                // Check if movie is in watch later
                movie.setInWatchLater(isInWatchLater(movie.getId()));
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public ObservableList<Movie> searchMovies(String searchTerm) {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        String query = "SELECT * FROM movies WHERE title LIKE ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, "%" + searchTerm + "%");

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getString("cast"),
                            rs.getInt("duration"),
                            rs.getDouble("imdb_rating"),
                            rs.getString("plot_summary"),
                            rs.getString("poster_image_path")
                    );
                    movie.setInWatchLater(isInWatchLater(movie.getId()));
                    movies.add(movie);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public ObservableList<Movie> getMoviesByGenre(String genre) {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        String query = "SELECT * FROM movies WHERE genre = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, genre);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Movie movie = new Movie(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("genre"),
                            rs.getString("cast"),
                            rs.getInt("duration"),
                            rs.getDouble("imdb_rating"),
                            rs.getString("plot_summary"),
                            rs.getString("poster_image_path")
                    );
                    movie.setInWatchLater(isInWatchLater(movie.getId()));
                    movies.add(movie);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public List<String> getAllGenres() {
        List<String> genres = new ArrayList<>();
        String query = "SELECT DISTINCT genre FROM movies ORDER BY genre";

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                genres.add(rs.getString("genre"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return genres;
    }

    public ObservableList<Movie> getWatchLaterMovies() {
        ObservableList<Movie> movies = FXCollections.observableArrayList();
        String query = """
            SELECT m.* FROM movies m 
            INNER JOIN watch_later wl ON m.id = wl.movie_id
        """;

        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Movie movie = new Movie(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("genre"),
                        rs.getString("cast"),
                        rs.getInt("duration"),
                        rs.getDouble("imdb_rating"),
                        rs.getString("plot_summary"),
                        rs.getString("poster_image_path")
                );
                movie.setInWatchLater(true);
                movies.add(movie);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return movies;
    }

    public void addToWatchLater(int movieId) {
        String query = "INSERT OR IGNORE INTO watch_later (movie_id) VALUES (?)";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, movieId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeFromWatchLater(int movieId) {
        String query = "DELETE FROM watch_later WHERE movie_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, movieId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isInWatchLater(int movieId) {
        String query = "SELECT COUNT(*) FROM watch_later WHERE movie_id = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setInt(1, movieId);

            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Method to force database refresh with updated movies
    public void refreshMovieDatabase() {
        try {
            // Clear existing data
            try (Statement stmt = connection.createStatement()) {
                stmt.execute("DELETE FROM watch_later");
                stmt.execute("DELETE FROM movies");
                // Reset auto-increment counter
                stmt.execute("DELETE FROM sqlite_sequence WHERE name='movies'");
            }

            // Repopulate with updated movie list
            populateMovies();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}