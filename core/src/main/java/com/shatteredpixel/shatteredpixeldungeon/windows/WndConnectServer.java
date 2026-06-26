package com.shatteredpixel.shatteredpixeldungeon.windows;

import io.github.pixeldungeonmultiplayer.shattered.client.network.Client;
import io.github.pixeldungeonmultiplayer.shattered.client.network.NetworkScanner;
import io.github.pixeldungeonmultiplayer.shattered.client.network.scanners.ServerInfo;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.StartScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.watabou.noosa.BitmapTextMultiline;
import com.watabou.noosa.Scene;

public class WndConnectServer extends Window {
    private static final int WIDTH			= 120;
    private static final int MARGIN 		= 2;
    private static final int BUTTON_HEIGHT	= 20;
    private ServerInfo serverInfo;
    private Scene scene;

    private String generateMessage(ServerInfo server){
        StringBuilder message = new StringBuilder();
        message.append("Players: ");
        message.append((server.players > -1) ? server.players : "?");
        message.append('/');
        message.append((server.maxPlayers > -1) ? server.maxPlayers : "?");
        if (server.serverVersion != null && !server.serverVersion.trim().isEmpty()) {
            message.append('\n');
            message.append("Version: ");
            message.append(server.serverVersion);
        }
        if (server.motd != null && !server.motd.trim().isEmpty()) {
            message.append('\n');
            message.append('\n');
            message.append(server.motd.trim());
        }
        return message.toString();
    }
    public WndConnectServer(Scene scene, ServerInfo server){
        super();
        this.serverInfo = server;
        this.scene=scene;

        BitmapTextMultiline tfTitle = new BitmapTextMultiline(server.name, PixelScene.pixelFont);
        tfTitle.hardlight( TITLE_COLOR );
        tfTitle.x = tfTitle.y = MARGIN;
        tfTitle.maxWidth = WIDTH - MARGIN * 2;
        tfTitle.measure();
        tfTitle.x= (tfTitle.maxWidth-tfTitle.width()) / 2 ;
        add( tfTitle );

        BitmapTextMultiline tfMessage = new BitmapTextMultiline(generateMessage(server), PixelScene.pixelFont);
        tfMessage.maxWidth = WIDTH - MARGIN * 2;
        tfMessage.measure();
        tfMessage.x = MARGIN;
        tfMessage.y = tfTitle.y + tfTitle.height() + MARGIN;
        add( tfMessage );

        float pos = tfMessage.y + tfMessage.height() + MARGIN;

      /*  for (int i=0; i < options.length; i++) {
            final int index = i;
            RedButton btn = new RedButton( options[i] ) {
                @Override
                protected void onClick() {
                    hide();
                    onSelect( index );
                }
            };
            btn.setRect( MARGIN, pos, WIDTH - MARGIN * 2, BUTTON_HEIGHT );
            add( btn );

            pos += BUTTON_HEIGHT + MARGIN;
        }
        */
        {
            RedButton btn = new RedButton("Connect" ) {
                @Override
                protected void onClick() {
                    hide();
                    onSelect( 1 );
                }
            };
            btn.setRect( MARGIN, pos, WIDTH - MARGIN * 2, BUTTON_HEIGHT );
            add( btn );

            pos += BUTTON_HEIGHT + MARGIN;
        }
        {
            RedButton btn = new RedButton( "Exit" ) {
                @Override
                protected void onClick() {
                    hide();
                    onSelect( 2 );
                }
            };
            btn.setRect( MARGIN, pos, WIDTH - MARGIN * 2, BUTTON_HEIGHT );
            add( btn );

            pos += BUTTON_HEIGHT + MARGIN;
        }

        resize( WIDTH, (int)pos );
    }
    //Fixme delete this function
    protected void onSelect( int index ) {
        if (index == 1) {
            if (!Client.connect(serverInfo)) {
                scene.add(new WndError("Can't connect"));
            } else {
                NetworkScanner.stop();
                //FIXME
                StartScene.startNewGame();
            }
        }
    }
}
