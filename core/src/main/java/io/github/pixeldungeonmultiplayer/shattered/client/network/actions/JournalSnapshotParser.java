package io.github.pixeldungeonmultiplayer.shattered.client.network.actions;

import com.shatteredpixel.shatteredpixeldungeon.journal.RemoteJournal;
import io.github.pixeldungeonmultiplayer.shattered.client.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;

import org.json.JSONException;
import org.json.JSONObject;

public class JournalSnapshotParser implements ActionParser {
	@Override
	public void parse(ParseThread parseThread, JSONObject action) throws JSONException {
		RemoteJournal.patch(DefaultActionParserRegistry.payloadObject(action));
		WndJournal.refreshNotes();
	}
}
