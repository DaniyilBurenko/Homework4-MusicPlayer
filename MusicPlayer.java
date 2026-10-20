import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/*
 * Data structures used:
 * Playlist - LinkedList
 * Library - HashMap
 * Up Next - ArrayDeque
 * History - ArrayDeque
 * Play Counts - HashMap
 *
 * All use Java's built-in collections.
 */
public class MusicPlayer {
    private LinkedList<Song> playlist = new LinkedList<>();
    private HashMap<String, Song> library = new HashMap<>();
    private ArrayDeque<Song> upNext = new ArrayDeque<>();
    private ArrayDeque<Song> history = new ArrayDeque<>();
    private HashMap<String, Integer> playCounts = new HashMap<>();
    private Song current = null;

    public boolean addSong(String id, String title, String artist) {
        if (library.containsKey(id)) {
            return false;
        }

        Song song = new Song(id, title, artist);
        playlist.addLast(song);
        library.put(id, song);
        return true;
    }

    public Song findSong(String id) {
        return library.get(id);
    }

    public List<String> showPlaylist() {
        List<String> titles = new ArrayList<>();
        for (Song song : playlist) {
            titles.add(song.title);
        }
        return titles;
    }

    // Use the same method each time a song starts so counts stay correct
    private Song startSong(Song song) {
        current = song;
        playCounts.put(song.id, playCount(song.id) + 1);
        return song;
    }

    public Song play(String id) {
        Song song = findSong(id);
        if (song == null) {
            return null;
        }

        if (current != null) {
            history.push(current);
        }
        return startSong(song);
    }

    public Song nowPlaying() {
        return current;
    }

    public int playCount(String id) {
        return playCounts.getOrDefault(id, 0);
    }

    public boolean queueLater(String id) {
        Song song = findSong(id);
        if (song == null) {
            return false;
        }

        upNext.addLast(song);
        return true;
    }

    public boolean queueNext(String id) {
        Song song = findSong(id);
        if (song == null) {
            return false;
        }

        upNext.addFirst(song);
        return true;
    }

    public List<String> showUpNext() {
        List<String> titles = new ArrayList<>();
        for (Song song : upNext) {
            // Do not show songs that were removed from the library
            if (library.get(song.id) == song) {
                titles.add(song.title);
            }
        }
        return titles;
    }

    public Song next() {
        Song nextSong = null;

        // Try the queue first, skip songs that were removed
        while (!upNext.isEmpty()) {
            Song song = upNext.removeFirst();
            if (library.get(song.id) == song) {
                nextSong = song;
                break;
            }
        }

        // If nothing was queued, move forward in the playlist
        if (nextSong == null) {
            if (current == null) {
                if (!playlist.isEmpty()) {
                    nextSong = playlist.getFirst();
                }
            } else {
                int position = playlist.indexOf(current);
                if (position != -1 && position + 1 < playlist.size()) {
                    nextSong = playlist.get(position + 1);
                }
            }
        }

        // At the end, keep the current song and history unchanged
        if (nextSong == null) {
            return null;
        }

        if (current != null) {
            history.push(current);
        }
        return startSong(nextSong);
    }

    public Song back() {
        Song previousSong = null;

        // History is a stack, so the most recent song comes out first
        while (!history.isEmpty()) {
            Song song = history.pop();
            if (library.get(song.id) == song) {
                previousSong = song;
                break;
            }
        }

        if (previousSong == null) {
            return null;
        }

        // Next should take us back to the song we just left
        if (current != null) {
            upNext.addFirst(current);
        }
        return startSong(previousSong);
    }

    public List<String> showHistory() {
        List<String> titles = new ArrayList<>();
        // Iterating an ArrayDeque used as a stack starts at the top
        for (Song song : history) {
            if (library.get(song.id) == song) {
                titles.add(song.title);
            }
        }
        return titles;
    }

    public boolean removeSong(String id) {
        Song song = findSong(id);
        if (song == null) {
            return false;
        }
        if (current == song) {
            return false;
        }

        playlist.remove(song);
        library.remove(id);
        playCounts.remove(id);
        return true;
    }
}
