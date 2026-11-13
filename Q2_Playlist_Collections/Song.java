package Q2_Playlist_Collections;

import java.util.Objects;

public class Song {
    private final String songId;
    private final String title;
    private final String artist;
    private final int durationSeconds;

    public Song(String songId, String title, String artist, int durationSeconds) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.durationSeconds = durationSeconds;
    }

    public String getSongId() { return songId; }
    public String getTitle() { return title; }
    public String getArtist() { return artist; }
    public int getDurationSeconds() { return durationSeconds; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Song)) return false;
        Song song = (Song) o;
        return Objects.equals(songId, song.songId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(songId);
    }

    @Override
    public String toString() {
        return title + " - " + artist + " [" + durationSeconds + "s]";
    }
}

