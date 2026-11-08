package com.ferrolox.persistence.repositories;

import com.ferrolox.configuration.DatabaseConfiguration;
import com.ferrolox.domain.Genre;
import com.ferrolox.domain.MediaEntry;
import com.ferrolox.domain.MediaType;
import com.ferrolox.domain.User;
import com.ferrolox.foundation.exceptions.DatabaseException;
import com.ferrolox.foundation.exceptions.InexistentResourceException;
import com.ferrolox.persistence.interfaces.MediaEntryInterface;
import com.ferrolox.persistence.queryObjects.MediaEntryFilter;
import com.ferrolox.persistence.queryObjects.MediaEntrySort;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MediaEntryRepository implements MediaEntryInterface {

	private final DatabaseConfiguration database;

	public MediaEntryRepository(DatabaseConfiguration database) {
		this.database = database;
	}

	@Override
	public List<MediaEntry> find(MediaEntryFilter filter) throws DatabaseException {
		String selectMediaEntrySql = """
	    	SELECT
	            media_entry.id AS media_entry_id,
	            account.id AS account_id,
	            account.username AS account_username,
	            account.alias AS account_alias,
	            media_entry.title AS media_entry_title,
	            media_entry.description AS media_entry_description,
	            media_entry.type AS media_entry_type,
	            genre.id AS genre_id,
	            genre.name AS genre_name,
	            media_entry.release_year AS media_entry_release_year,
	            media_entry.minimum_age AS media_entry_minimum_age
	        FROM media_entries AS media_entry
	        JOIN users AS account ON media_entry.author_id = account.id
	        LEFT JOIN media_entry_genres AS media_entry_genre ON media_entry.id = media_entry_genre.media_entry_id
	        LEFT JOIN genres AS genre ON media_entry_genre.genre_id = genre.id
	        LEFT JOIN (
	            SELECT
	                media_entry_id,
	                AVG(score) AS average_score
	            FROM ratings
	            GROUP BY media_entry_id
	        ) AS rating_summary	ON media_entry.id = rating_summary.media_entry_id
	        WHERE 1 = 1
        """;

		List<Object> parameters = new ArrayList<>();

		if (filter != null) {
			if (filter.title() != null && !filter.title().isBlank()) {
				selectMediaEntrySql += " AND media_entry.title ILIKE ?";
				parameters.add("%" + filter.title() + "%");
			}

			if (filter.genre() != null && !filter.genre().isBlank()) {
				selectMediaEntrySql += " AND EXISTS ( SELECT 1 FROM media_entry_genres AS filter_genre JOIN genres AS filter_genre_name ON filter_genre.genre_id = filter_genre_name.id WHERE filter_genre.media_entry_id = media_entry.id AND filter_genre_name.name ILIKE ?)";
				parameters.add("%" + filter.genre() + "%");
			}

			if (filter.type() != null) {
				selectMediaEntrySql += " AND media_entry.type::text = ?";
				parameters.add(filter.type().name());
			}

			if (filter.releaseYear() != null) {
				selectMediaEntrySql += " AND media_entry.release_year = ?";
				parameters.add(filter.releaseYear());
			}

			if (filter.minimumAge() != null) {
				selectMediaEntrySql += " AND media_entry.minimum_age = ?";
				parameters.add(filter.minimumAge());
			}

			if (filter.minimumRating() != null) {
				selectMediaEntrySql += " AND rating_summary.average_score >= ?";
				parameters.add(filter.minimumRating());
			}
		}

		MediaEntrySort sort = filter != null && filter.sort() != null ? filter.sort() : MediaEntrySort.TITLE_ASC;

		selectMediaEntrySql += " ORDER BY ";

		selectMediaEntrySql += switch (sort) {
			case TITLE_ASC -> "media_entry.title ASC";
			case TITLE_DESC -> "media_entry.title DESC";
			case RELEASE_YEAR_ASC -> "media_entry.release_year ASC";
			case RELEASE_YEAR_DESC -> "media_entry.release_year DESC";
			case AVERAGE_SCORE_ASC -> "rating_summary.average_score ASC NULLS LAST";
			case AVERAGE_SCORE_DESC -> "rating_summary.average_score DESC NULLS LAST";
		};

		selectMediaEntrySql += ", media_entry.title ASC";

		try (
			Connection connection = database.getConnection();
			PreparedStatement statement = connection.prepareStatement(selectMediaEntrySql)
		) {
			for (int i = 0; i < parameters.size(); i++) {
				statement.setObject(i + 1, parameters.get(i));
			}

			try (ResultSet resultSet = statement.executeQuery()) {
				Map<Integer, MediaEntry> mediaEntries = new LinkedHashMap<>();

				while (resultSet.next()) {
					int mediaEntryId = resultSet.getInt("media_entry_id");

					MediaEntry mediaEntry = mediaEntries.get(mediaEntryId);

					if (mediaEntry == null) {
						User author = new User(
							resultSet.getInt("account_id"),
							resultSet.getString("account_username"),
							resultSet.getString("account_alias"),
							null
						);

						mediaEntry = new MediaEntry(
							mediaEntryId,
							author,
							resultSet.getString("media_entry_title"),
							resultSet.getString("media_entry_description"),
							MediaType.valueOf(resultSet.getString("media_entry_type")),
							new HashSet<>(),
							resultSet.getInt("media_entry_release_year"),
							resultSet.getInt("media_entry_minimum_age"),
							null
						);

						mediaEntries.put(mediaEntryId, mediaEntry);
					}

					int genreId = resultSet.getInt("genre_id");

					if (!resultSet.wasNull()) {
						mediaEntry.getGenres().add(new Genre(
							genreId,
							resultSet.getString("genre_name")
						));
					}
				}

				return new ArrayList<>(mediaEntries.values());
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to find Media Entries", exception);
		}
	}

	@Override
	public Optional<MediaEntry> findById(int id) throws DatabaseException {
		String sql = """
        SELECT
            media_entry.id AS media_entry_id,
            account.id AS account_id,
            account.username AS account_username,
            account.alias AS account_alias,
            media_entry.title AS media_entry_title,
            media_entry.description AS media_entry_description,
            media_entry.type AS media_entry_type,
            genre.id AS genre_id,
            genre.name AS genre_name,
            media_entry.release_year AS media_entry_release_year,
            media_entry.minimum_age AS media_entry_minimum_age
        FROM media_entries AS media_entry
        JOIN users AS account
            ON media_entry.author_id = account.id
        LEFT JOIN media_entry_genres AS media_entry_genre
            ON media_entry.id = media_entry_genre.media_entry_id
        LEFT JOIN genres AS genre
            ON media_entry_genre.genre_id = genre.id
        WHERE media_entry.id = ?
        """;

		try (
			Connection connection = database.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)
		) {
			statement.setInt(1, id);

			try (ResultSet resultSet = statement.executeQuery()) {
				if (!resultSet.next()) return Optional.empty();

				User author = new User(
					resultSet.getInt("account_id"),
					resultSet.getString("account_username"),
					resultSet.getString("account_alias"),
					null
				);

				MediaEntry mediaEntry = new MediaEntry(
					resultSet.getInt("media_entry_id"),
					author,
					resultSet.getString("media_entry_title"),
					resultSet.getString("media_entry_description"),
					MediaType.valueOf(resultSet.getString("media_entry_type")),
					new HashSet<>(),
					resultSet.getInt("media_entry_release_year"),
					resultSet.getInt("media_entry_minimum_age"),
					null
				);

				do {
					int genreId = resultSet.getInt("genre_id");

					if (!resultSet.wasNull()) {
						mediaEntry.getGenres().add(new Genre(
							genreId,
							resultSet.getString("genre_name")
						));
					}
				} while (resultSet.next());

				return Optional.of(mediaEntry);
			}
		} catch (SQLException exception) {
			throw new DatabaseException("Failed to find Media Entry", exception);
		}
	}

	@Override
	public List<MediaEntry> findFavouritesByUserId(int userId) {
		return List.of();
	}

	@Override
	public int save(MediaEntry mediaEntry) throws DatabaseException {
		String mediaEntrySql = """
	        INSERT INTO media_entries (
	            author_id,
	            title,
	            description,
	            type,
	            release_year,
	            minimum_age
	        )
	        VALUES (?, ?, ?, CAST(? AS MEDIATYPE), ?, ?)
	        RETURNING id
        """;

		String insertGenreSql = """
	        INSERT INTO media_entry_genres (
	            media_entry_id,
	            genre_id
	        )
	        VALUES (?, ?)
        """;

		try (Connection connection = database.getConnection()) {
			connection.setAutoCommit(false);

			try {
				int mediaEntryId;

				try (PreparedStatement statement = connection.prepareStatement(mediaEntrySql)) {
					statement.setInt(1, mediaEntry.getAuthor().getId());
					statement.setString(2, mediaEntry.getTitle());
					statement.setString(3, mediaEntry.getDescription());
					statement.setString(4, mediaEntry.getType().name());
					statement.setInt(5, mediaEntry.getReleaseYear());
					statement.setInt(6, mediaEntry.getMinimumAge());

					try (ResultSet resultSet = statement.executeQuery()) {
						resultSet.next();
						mediaEntryId = resultSet.getInt("id");
					}
				}

				try (PreparedStatement statement = connection.prepareStatement(insertGenreSql)) {
					for (Genre genre : mediaEntry.getGenres()) {
						statement.setInt(1, mediaEntryId);
						statement.setInt(2, genre.getId());
						statement.addBatch();
					}

					statement.executeBatch();
				}

				connection.commit();
				return mediaEntryId;

			} catch (SQLException exception) {
				connection.rollback();
				throw exception;
			}

		} catch (SQLException exception) {
			throw new DatabaseException("Failed to save Media Entry", exception);
		}
	}

	@Override
	public void update(MediaEntry mediaEntry) throws DatabaseException, InexistentResourceException {
		String updateMediaEntrySql = """
	        UPDATE media_entries
	        SET
	            title = ?,
	            description = ?,
	            type = CAST(? AS MEDIATYPE),
	            release_year = ?,
	            minimum_age = ?
	        WHERE id = ?
        """;

		String deleteGenresSql = """
	        DELETE FROM media_entry_genres
	        WHERE media_entry_id = ?
        """;

		String insertGenreSql = """
	        INSERT INTO media_entry_genres (
	            media_entry_id,
	            genre_id
	        )
	        VALUES (?, ?)
        """;

		try (Connection connection = database.getConnection()) {
			connection.setAutoCommit(false);

			try {
				int affectedRows;

				try (PreparedStatement statement = connection.prepareStatement(updateMediaEntrySql)) {
					statement.setString(1, mediaEntry.getTitle());
					statement.setString(2, mediaEntry.getDescription());
					statement.setString(3, mediaEntry.getType().name());
					statement.setInt(4, mediaEntry.getReleaseYear());
					statement.setInt(5, mediaEntry.getMinimumAge());
					statement.setInt(6, mediaEntry.getId());

					affectedRows = statement.executeUpdate();
				}

				if (affectedRows == 0) throw new InexistentResourceException("Media Entry with ID " + mediaEntry.getId() + " not found");

				try (PreparedStatement statement = connection.prepareStatement(deleteGenresSql)) {
					statement.setInt(1, mediaEntry.getId());
					statement.executeUpdate();
				}

				try (PreparedStatement statement = connection.prepareStatement(insertGenreSql)) {
					for (Genre genre : mediaEntry.getGenres()) {
						statement.setInt(1, mediaEntry.getId());
						statement.setInt(2, genre.getId());
						statement.addBatch();
					}

					statement.executeBatch();
				}

				connection.commit();

			} catch (SQLException exception) {
				connection.rollback();
				throw exception;
			}

		} catch (SQLException exception) {
			throw new DatabaseException("Failed to update Media Entry", exception);
		}
	}

	@Override
	public void deleteById(int id) throws DatabaseException, InexistentResourceException {
		String sql = """
        DELETE FROM media_entries
        WHERE id = ?
        """;

		try (
			Connection connection = database.getConnection();
			PreparedStatement statement = connection.prepareStatement(sql)
		) {
			statement.setInt(1, id);

			int affectedRows = statement.executeUpdate();

			if (affectedRows == 0) {
				throw new InexistentResourceException(
					"Media entry with id " + id + " does not exist"
				);
			}

		} catch (SQLException exception) {
			throw new DatabaseException("Failed to delete Media Entry", exception);
		}
	}
}