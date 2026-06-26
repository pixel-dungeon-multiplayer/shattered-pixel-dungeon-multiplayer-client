package io.github.pixeldungeonmultiplayer.shattered.client.network.actions.items;

public class ItemUpdateParser extends BaseItemActionParser {
    @Override
    protected String getMode() {
        return "update";
    }
}
