INSERT INTO songs (title, artist, label, released) VALUES ('O''Children', 'Nick Cave & The Bad Seeds', 'Mute', 2004);
INSERT INTO songs (title, artist, label, released) VALUES ('The Lyre of Orpheus', 'Nick Cave & The Bad Seeds', 'Mute', 2004);
INSERT INTO songs (title, artist, label, released) VALUES ('Easy Money', 'Nick Cave & The Bad Seeds', 'Mute', 2004);

INSERT INTO playlists (ownerId, name, isPrivate) VALUES ('eschuler', 'playlist 1', false);
INSERT INTO playlists (ownerId, name, isPrivate) VALUES ('mmuster', 'playlist 2', true);

INSERT INTO playlist_song (playlist_id, song_id) VALUES (1, 1);
INSERT INTO playlist_song (playlist_id, song_id) VALUES (1, 2);
INSERT INTO playlist_song (playlist_id, song_id) VALUES (2, 3);