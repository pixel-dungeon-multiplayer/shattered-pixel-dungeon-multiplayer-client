/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2025 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.scenes;

import com.shatteredpixel.shatteredpixeldungeon.*;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Belongings;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.effects.particles.SparkParticle;
import com.shatteredpixel.shatteredpixeldungeon.items.*;
import com.shatteredpixel.shatteredpixeldungeon.items.bags.Bag;
import com.shatteredpixel.shatteredpixeldungeon.journal.Document;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.nikita22007.multiplayer.utils.text.LocalizedString;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.network.ParseThread;
import com.shatteredpixel.shatteredpixeldungeon.network.SendData;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.Button;
import com.shatteredpixel.shatteredpixeldungeon.ui.ExitButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.ItemSlot;
import com.shatteredpixel.shatteredpixeldungeon.ui.RadialMenu;
import com.shatteredpixel.shatteredpixeldungeon.ui.RedButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StyledButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Toolbar;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.windows.IconTitle;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndBag;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndInfoItem;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndJournal;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndKeyBindings;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndMessage;
import com.watabou.gltextures.TextureCache;
import com.watabou.glwrap.Blending;
import com.watabou.input.ControllerHandler;
import com.watabou.input.GameAction;
import com.watabou.input.KeyBindings;
import com.watabou.noosa.Camera;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.NoosaScript;
import com.watabou.noosa.NoosaScriptNoLighting;
import com.watabou.noosa.SkinnedBlock;
import com.watabou.noosa.audio.Sample;
import com.watabou.noosa.particles.Emitter;
import com.watabou.noosa.ui.Component;
import org.json.JSONArray;
import org.json.JSONObject;
import com.watabou.utils.RectF;

import java.util.ArrayList;

public class AlchemyScene extends PixelScene {

	private static final int BACK_BUTTON = -1; //special constant

	private static final int CONTROL_BUTTON_GROUP = 000;
	private static final int INPUT_BUTTON_GROUP = 100;
	private static final int COMBINE_BUTTON_GROUP = 200;
	private static final int OUTPUT_BUTTON_GROUP = 300;

	private static final int CANCEL_BUTTON = CONTROL_BUTTON_GROUP + 0;
	private static final int REPEAT_BUTTON = CONTROL_BUTTON_GROUP + 1;
	private static final int ENERGY_ADD_BUTTON = CONTROL_BUTTON_GROUP + 2;
	private static final int EXIT_BUTTON = CONTROL_BUTTON_GROUP + 3;

	//max of 3 inputs, and 3 potential recipe outputs
	private static final InputButton[] inputs = new InputButton[3];
	private static final CombineButton[] combines = new CombineButton[3];
	private static final OutputSlot[] outputs = new OutputSlot[3];
	int windowID;

	private IconButton cancel;
	private IconButton repeat;
	private static ArrayList<Item> lastIngredients = new ArrayList<>();

    private Emitter smokeEmitter;
	private Emitter bubbleEmitter;
	private Emitter sparkEmitter;
	
	private Emitter lowerBubbles;
	private SkinnedBlock water;

	private ItemSprite energyIcon;
	private RenderedTextBlock energyLeft;
	private IconButton energyAdd;
	private boolean energyAddBlinking = false;

	private static boolean splitAlchGuide = false;
	private WndJournal.AlchemyTab alchGuide = null;
	private static int centerW;

	private static final int BTN_SIZE	= 28;

	{
		inGameScene = true;
	}
	
	@Override
	public void create() {
		super.create();

		int w = Camera.main.width;
		int h = Camera.main.height;
		RectF insets = getCommonInsets();

		water = new SkinnedBlock(
				w, h,
				Dungeon.level.waterTex() ){
			
			@Override
			protected NoosaScript script() {
				return NoosaScriptNoLighting.get();
			}
			
			@Override
			public void draw() {
				//water has no alpha component, this improves performance
				Blending.disable();
				super.draw();
				Blending.enable();
			}
		};
		water.autoAdjust = true;
		add(water);
		
		Image im = new Image(TextureCache.createGradient(0x66000000, 0x88000000, 0xAA000000, 0xCC000000, 0xFF000000));
		im.angle = 90;
		im.x = w;
		im.scale.x = h/5f;
		im.scale.y = w;
		add(im);

		w -= insets.left + insets.right;
		h -= insets.top + insets.bottom;

		ExitButton btnExit = new ExitButton(){
			@Override
			protected void onClick() {
				Game.switchScene(GameScene.class);
				sendResult(EXIT_BUTTON, false);
			}
		};
		btnExit.setPos( insets.left + w - btnExit.width(), insets.top );
		add( btnExit );

		bubbleEmitter = new Emitter();
		add(bubbleEmitter);

		lowerBubbles = new Emitter();
		add(lowerBubbles);
		
		IconTitle title = new IconTitle(Icons.ALCHEMY.get(), Messages.get(this, "title") );
		title.setSize(200, 0);
		title.setPos(
				insets.left + (w - title.reqWidth()) / 2f,
				insets.top + (20 - title.height()) / 2f
		);
		align(title);
		add(title);
		
		int pw = Math.min(50 + w/2, 150);
		int left = (int)(insets.left) + (w - pw)/2;

		centerW = left + pw/2;

		int pos = (int)(insets.top) + (h - 120)/2;

		if (splitAlchGuide &&
				w >= 300 &&
				h >= PixelScene.MIN_HEIGHT_FULL){
			pw = Math.min(150, w/2);
			left = (w/2 - pw);
			centerW = left + pw/2;

			NinePatch guideBG = Chrome.get(Chrome.Type.TOAST);
			guideBG.size(126 + guideBG.marginHor(), Math.min(Camera.main.height - 18, 191 + guideBG.marginVer()));
			guideBG.y = Math.max(17, insets.top + (h - guideBG.height())/2f);
			guideBG.x = insets.left + w - left - guideBG.width();
			add(guideBG);

			alchGuide = new WndJournal.AlchemyTab();
			add(alchGuide);
			alchGuide.setRect(guideBG.x + guideBG.marginLeft(),
					guideBG.y + guideBG.marginTop(),
					guideBG.width() - guideBG.marginHor(),
					guideBG.height() - guideBG.marginVer());

		} else {
			splitAlchGuide = false;
		}
		
		RenderedTextBlock desc = PixelScene.renderTextBlock(6);
		desc.maxWidth(pw);
		desc.text( Messages.get(AlchemyScene.class, "text") );
		desc.setPos(left + (pw - desc.width())/2, pos);
		add(desc);
		
		pos += desc.height() + 6;

		NinePatch inputBG = Chrome.get(Chrome.Type.TOAST_TR);
		inputBG.x = left + 6;
		inputBG.y = pos;
		inputBG.size(BTN_SIZE+8, 3*BTN_SIZE + 4 + 8);
		add(inputBG);

		pos += 4;

		synchronized (inputs) {
			for (int i = 0; i < inputs.length; i++) {
				inputs[i] = new InputButton(i);
				inputs[i].setRect(left + 10, pos, BTN_SIZE, BTN_SIZE);
				add(inputs[i]);
				pos += BTN_SIZE + 2;
			}
		}

		cancel = new IconButton(Icons.CLOSE.get()){
			@Override
			protected void onClick() {
				sendResult(CANCEL_BUTTON, false);
			}

			@Override
			public GameAction keyAction() {
				return SPDAction.BACK;
			}

			@Override
			protected String hoverText() {
				return Messages.get(AlchemyScene.class, "cancel");
			}
		};
		cancel.setRect(left + 8, pos + 2, 16, 16);
		cancel.enable(false);
		add(cancel);

		repeat = new IconButton(Icons.REPEAT.get()){
			@Override
			protected void onClick() {
				sendResult(REPEAT_BUTTON, false);
            }

			@Override
			public GameAction keyAction() {
				return SPDAction.TAG_RESUME;
			}

			@Override
			protected String hoverText() {
				return Messages.get(AlchemyScene.class, "repeat");
			}
		};
		repeat.setRect(left + 24, pos + 2, 16, 16);
		repeat.enable(false);
		add(repeat);

		lastIngredients.clear();

        for (int i = 0; i < inputs.length; i++){
			combines[i] = new CombineButton(i);
			combines[i].enable(false);

			outputs[i] = new OutputSlot(i);
			outputs[i].item(null);

			if (i == 0){
				//first ones are always visible
				combines[i].setRect(left + (pw-30)/2f, inputs[1].top()+5, 30, inputs[1].height()-10);
				outputs[i].setRect(left + pw - BTN_SIZE - 10, inputs[1].top(), BTN_SIZE, BTN_SIZE);
			} else {
				combines[i].visible = false;
				outputs[i].visible = false;
			}

			add(combines[i]);
			add(outputs[i]);
		}

		smokeEmitter = new Emitter();
		smokeEmitter.pos(outputs[0].left() + (BTN_SIZE-16)/2f, outputs[0].top() + (BTN_SIZE-16)/2f, 16, 16);
		smokeEmitter.autoKill = false;
		add(smokeEmitter);
		
		pos += 10;

		if (Camera.main.height >= 280){
			//last elements get centered even with a split alch guide UI, as long as there's enough height
			centerW = (int)(insets.left) + w/2;
		}

		bubbleEmitter.pos(0,
				0,
				2*centerW,
				Camera.main.height);
		bubbleEmitter.autoKill = false;

		lowerBubbles.pos(0,
				pos,
				2*centerW,
				Math.max(0, h-pos));
		lowerBubbles.pour(Speck.factory( Speck.BUBBLE ), 0.1f );

		energyLeft = PixelScene.renderTextBlock("", 9);
		energyLeft.setPos(
				centerW - energyLeft.width()/2,
				insets.top + h - 8 - energyLeft.height()
		);
		energyLeft.hardlight(0x44CCFF);
		add(energyLeft);

		energyIcon = new ItemSprite(ItemSpriteSheet.ENERGY);
		energyIcon.x = energyLeft.left() - energyIcon.width();
		energyIcon.y = energyLeft.top() - (energyIcon.height() - energyLeft.height())/2;
		align(energyIcon);
		add(energyIcon);

		energyAdd = new IconButton(Icons.get(Icons.PLUS)){

			private float time = 0;

			@Override
			public void update() {
				super.update();
				if (energyAddBlinking){
					icon.brightness( 0.5f + (float)Math.abs(Math.cos( StatusPane.FLASH_RATE * (time += Game.elapsed) )));
				} else {
					if (time > 0){
						icon.resetColor();
					}
					time = 0;
				}
			}

			@Override
			protected void onClick() {
				sendResult(ENERGY_ADD_BUTTON, false);
			}

			@Override
			public GameAction keyAction() {
				return SPDAction.TAG_ACTION;
			}

			@Override
			protected String hoverText() {
				return Messages.get(AlchemyScene.class, "energize");
			}
		};

		energyAdd.setRect(energyLeft.right(), energyLeft.top() - (16 - energyLeft.height())/2, 16, 16);
		align(energyAdd);
		add(energyAdd);

		sparkEmitter = new Emitter();
		sparkEmitter.pos(energyLeft.left(), energyLeft.top(), energyLeft.width(), energyLeft.height());
		sparkEmitter.autoKill = false;
		add(sparkEmitter);

		StyledButton btnGuide = new StyledButton( Chrome.Type.TOAST_TR, Messages.get(AlchemyScene.class, "guide")){
			@Override
			protected void onClick() {
				super.onClick();
				if (Camera.main.width >= 300 && Camera.main.height >= PixelScene.MIN_HEIGHT_FULL){
					splitAlchGuide = !splitAlchGuide;
					ShatteredPixelDungeon.seamlessResetScene();
				} else {
					AlchemyScene.this.addToFront(new Window() {

						{
							WndJournal.AlchemyTab t = new WndJournal.AlchemyTab();
							int w, h;
							if (landscape()) {
								w = WndJournal.WIDTH_L;
								h = WndJournal.HEIGHT_L+8;
							} else {
								w = WndJournal.WIDTH_P;
								h = WndJournal.HEIGHT_P+10;
							}
							resize(w, h);
							add(t);
							t.setRect(0, 0, w, h);
						}

					});
				}
			}

			@Override
			public GameAction keyAction() {
				return SPDAction.JOURNAL;
			}

			@Override
			protected String hoverText() {
				return Messages.titleCase(Document.ALCHEMY_GUIDE.title());
			}
		};
		btnGuide.icon(new ItemSprite(ItemSpriteSheet.ALCH_PAGE));
		btnGuide.setSize(btnGuide.reqWidth()+4, 18);
		btnGuide.setPos(centerW - btnGuide.width()/2f, energyAdd.top()- btnGuide.height()-2);
		align(btnGuide);
		add(btnGuide);


        fadeIn();

	}
	
	@Override
	public void update() {
		super.update();
		ParseThread activeThread = ParseThread.getActiveThread();
		if (activeThread != null) {
			activeThread.parseIfHasData();
		}
		water.offset( 0, -5 * Game.elapsed );
	}
	
	@Override
	protected void onBackPressed() {
		sendResult(BACK_BUTTON, false);
	}

	private<T extends Item> ArrayList<T> filterInput(){
		ArrayList<T> filtered = new ArrayList<>();
		for (int i = 0; i < inputs.length; i++){
			Item item = inputs[i].item();
			if (item != null){
				filtered.add((T)item);
			}
		}
		return filtered;
	}
	private void updateState(){
		int recipesSize = 0;
		for (int i = 0; i < outputs.length; i++) {
			if(outputs[i].item() != null){
				recipesSize++;
			}
		}

		if (recipesSize == 0){
			combines[0].setPos(combines[0].left(), inputs[1].top()+5);
			outputs[0].setPos(outputs[0].left(), inputs[1].top());
			return;
		}

		//positions active buttons
		float gap = recipesSize == 2 ? 6 : 2;

		float height = inputs[2].bottom() - inputs[0].top();
		height -= recipesSize*BTN_SIZE + (recipesSize-1)*gap;
		float top = inputs[0].top() + height/2;

		//positions active buttons
		for (int i = 0; i < recipesSize; i++){
			outputs[i].setRect(outputs[0].left(), top, BTN_SIZE, BTN_SIZE);
			top += BTN_SIZE+gap;

			combines[i].setRect(combines[0].left(), outputs[i].top()+5, 30, 20);
		}

		if (alchGuide != null){
			alchGuide.updateList();
		}
	}

	public void populate(ArrayList<Item> toFind, Belongings inventory){
		clearSlots();
		
		int curslot = 0;
		for (Item finding : toFind){
			int needed = finding.quantity();
			ArrayList<Item> found = inventory.getAllSimilar(finding);
			while (!found.isEmpty() && needed > 0){
				Item detached;
				if (false || false) {
					detached = found.get(0);
				} else {
					detached = found.get(0);
				}
				inputs[curslot].item(detached);
				curslot++;
				needed -= detached.quantity();
				if (detached == found.get(0)) {
					found.remove(0);
				}
			}
		}
		updateState();
	}


	@Override
	public void onPause() {
	}

	@Override
	public void destroy() {
	}
	
	public void clearSlots(){
		synchronized ( inputs ) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] != null && inputs[i].item() != null) {
                    inputs[i].item(null);
				}
			}
		}
		cancel.enable(false);
		repeat.enable(false);
		if (alchGuide != null){
			alchGuide.updateList();
		}
	}

	public void createEnergy(){
		bubbleEmitter.start(Speck.factory( Speck.BUBBLE ), 0.01f, 100 );
		sparkEmitter.burst(SparkParticle.FACTORY, 20);
		Sample.INSTANCE.play( Assets.Sounds.LIGHTNING );
	}

	public void showIdentify(JSONObject oldNameObj, JSONObject newNameObj) {
		final NinePatch BG = Chrome.get(Chrome.Type.TOAST);

		IconTitle oldName = new IconTitle(){
			@Override
			public synchronized void update() {
				super.update();
				alpha(this.alpha()-Game.elapsed);
				if (this.alpha() <= 0){
					killAndErase();
				}
			}
		};
		oldName.fromJson(oldNameObj);

		IconTitle newName = new IconTitle(){

			boolean fading;

			@Override
			public synchronized void update() {
				super.update();
				if (!fading) {
					alpha(this.alpha() + Game.elapsed);
					if (this.alpha() >= 1) {
						fading = true;
					}
				} else {
					alpha(this.alpha() - Game.elapsed);
					BG.alpha(this.alpha());
					if (this.alpha() <= 0){
						killAndErase();
						BG.killAndErase();
					}
				}
			}
		};
		newName.fromJson(newNameObj);
		newName.alpha(-0.5f);

		oldName.setSize(200, oldName.height());
		newName.setSize(200, newName.height());

		int w = (int)Math.ceil(Math.max(oldName.reqWidth(), newName.reqWidth())+5);

		oldName.setSize(w, oldName.height());
		oldName.setPos(
				centerW - oldName.width()/2,
				energyAdd.top()
		);
		align(oldName);

		newName.setSize(w, oldName.height());
		newName.setPos(
				centerW - newName.width()/2,
				energyAdd.top()
		);
		align(newName);

		BG.x = oldName.left()-2;
		BG.y = oldName.top()-2;
		BG.size(oldName.width()+4, oldName.height()+4);

		add(BG);
		add(oldName);
		add(newName);
	}

	private class InputButton extends Component {
		int id;

		protected NinePatch bg;
		protected ItemSlot slot;
		
		private Item item = null;

		private InputButton(int id) {
			super();
			this.id = id;
		}
		
		@Override
		protected void createChildren() {
			super.createChildren();
			
			bg = Chrome.get( Chrome.Type.RED_BUTTON);
			add( bg );
			
			slot = new ItemSlot() {
				@Override
				protected void onPointerDown() {
					bg.brightness( 1.2f );
					Sample.INSTANCE.play( Assets.Sounds.CLICK );
				}
				@Override
				protected void onPointerUp() {
					bg.resetColor();
				}
				@Override
				protected void onClick() {
					super.onClick();
					sendResult(id + INPUT_BUTTON_GROUP, false);
				}

				@Override
				protected boolean onLongClick() {
					sendResult(id + INPUT_BUTTON_GROUP, true);
					return true;
				}

				@Override
				//only the first empty button accepts key input
				public GameAction keyAction() {
					for (InputButton i : inputs){
						if (i != null) {
							if (i.item == null || i.item instanceof WndBag.Placeholder) {
								if (i == InputButton.this) {
									return SPDAction.INVENTORY;
								} else {
									return super.keyAction();
								}
							}
						}
					}
					return super.keyAction();
				}

				@Override
				protected String hoverText() {
					if (item == null || item instanceof WndBag.Placeholder){
						return Messages.get(AlchemyScene.class, "add");
					}
					return super.hoverText();
				}

				@Override
				public GameAction secondaryTooltipAction() {
					return SPDAction.INVENTORY_SELECTOR;
				}
			};
			slot.enable(true);
			add( slot );
		}

		@Override
		protected void layout() {
			super.layout();
			
			bg.x = x;
			bg.y = y;
			bg.size( width, height );
			
			slot.setRect( x + 2, y + 2, width - 4, height - 4 );
		}

		public Item item(){
			return item;
		}

		public void item( Item item ) {
			if (item == null){
				this.item = null;
				slot.item(new WndBag.Placeholder(ItemSpriteSheet.SOMETHING));
			} else {
				slot.item(this.item = item);
			}
		}
		public void fromJson(JSONObject object) {
			item(CustomItem.createItem(object));
			update();
		}
	}

	private class CombineButton extends Component {
		protected int slot;

		protected RedButton button;
		protected RenderedTextBlock costText;
		protected int cost;

		private CombineButton(int slot){
			super();

			this.slot = slot;
		}

		@Override
		protected void createChildren() {
			super.createChildren();

			button = new RedButton(""){
				@Override
				protected void onClick() {
					super.onClick();
					sendResult(slot + COMBINE_BUTTON_GROUP, false);
				}

				@Override
				protected String hoverText() {
					return Messages.get(AlchemyScene.class, "craft");
				}

				@Override
				public GameAction keyAction() {
					if (slot == 0 && !combines[1].active && !combines[2].active){
						return SPDAction.TAG_LOOT;
					}
					return super.keyAction();
				}
			};
			button.icon(Icons.get(Icons.ARROW));
			add(button);

			costText = PixelScene.renderTextBlock(6);
			add(costText);
		}

		@Override
		protected void layout() {
			super.layout();

			button.setRect(x, y, width(), height());

			costText.setPos(
					left() + (width() - costText.width())/2,
					top() - costText.height()
			);
		}

		public void enable( boolean enabled ){
			enable(enabled, 0);
		}

		public void enable( boolean enabled, int cost ){
			this.cost = cost;
			button.enable(enabled);
			if (enabled) {
				button.icon().tint(1, 1, 0, 1);
				button.alpha(1f);
				costText.hardlight(0x44CCFF);
			} else {
				button.icon().color(0, 0, 0);
				button.alpha(0.6f);
				costText.hardlight(0xFF0000);
			}

			if (cost == 0){
				costText.visible = false;
			} else {
				costText.visible = true;
				costText.text(Messages.get(AlchemyScene.class, "energy") + " " + cost);
			}
			layout();
			active = enabled;
		}

	}

	private class OutputSlot extends Component {
		int id;

		protected NinePatch bg;
		protected ItemSlot slot;
		Item item;

		private OutputSlot(int id) {
			super();
			this.id = id;
		}

		@Override
		protected void createChildren() {

			bg = Chrome.get(Chrome.Type.TOAST_TR);
			add(bg);

			slot = new ItemSlot() {
				@Override
				protected void onClick() {
					super.onClick();
					sendResult(id + OUTPUT_BUTTON_GROUP, false);
				}
				@Override
				protected boolean onLongClick() {
					sendResult(id + OUTPUT_BUTTON_GROUP, true);
					return true;
				}
			};
			slot.item(null);
			add( slot );
		}

		@Override
		protected void layout() {
			super.layout();

			bg.x = x;
			bg.y = y;
			bg.size(width(), height());

			slot.setRect(x+2, y+2, width()-4, height()-4);
		}

		public void item( Item item ) {
			this.item = item;
			slot.item(item);
		}
		public Item item(){
			return item;
		}
	}

	public void parseJson(JSONObject object){
		windowID = object.getInt("id");
		JSONObject args = object.getJSONObject("args");

		cancel.enable(args.getBoolean("cancel_enabled"));
		repeat.enable(args.getBoolean("repeat_enabled"));
		energyAdd.enable(args.getBoolean("energy_add_enabled"));

		JSONObject energyIconObj = args.optJSONObject("energy_icon");
		if (energyIconObj != null) {
			int image = energyIconObj.optInt("image", 0);
			ItemSprite.Glowing glowing = null;
			if (energyIconObj.has("glowing") && !energyIconObj.isNull("glowing")) {
				glowing = new ItemSprite.Glowing(energyIconObj.getJSONObject("glowing"));
			}
			String spriteSheet = energyIconObj.optString("sprite_sheet", null);
			if (spriteSheet == null && energyIconObj.has("texture")) {
				spriteSheet = energyIconObj.optString("texture", null);
			}
			if (spriteSheet != null && !spriteSheet.isEmpty()) {
				energyIcon.view(spriteSheet, image, glowing);
			} else {
				energyIcon.view(image, glowing);
			}
		}

		LocalizedString energyText = JsonStringHelper.optLocalizedString(args, "energy_text", null);
		if (energyText != null) {
			energyLeft.text(energyText.resolve());
			energyLeft.setPos(
					centerW - energyLeft.width()/2,
					energyLeft.top()
			);
			energyIcon.x = energyLeft.left() - energyIcon.width();
			energyIcon.y = energyLeft.top() - (energyIcon.height() - energyLeft.height())/2;
			align(energyIcon);
			energyAdd.setRect(energyLeft.right(), energyLeft.top() - (16 - energyLeft.height())/2, 16, 16);
			align(energyAdd);
			sparkEmitter.pos(energyLeft.left(), energyLeft.top(), energyLeft.width(), energyLeft.height());
		}

		JSONArray input = args.getJSONArray("inputs");
		for (int i = 0; i < input.length() && i < inputs.length; i++) {
			if (input.isNull(i)) {
				inputs[i].item(null);
			} else {
				inputs[i].fromJson(input.getJSONObject(i));
				inputs[i].update();
			}
		}
		for (int i = input.length(); i < inputs.length; i++) {
			inputs[i].item(null);
		}

		JSONArray combineButtons = args.getJSONArray("combine_buttons");
		for (int i = 0; i < combineButtons.length() && i < combines.length; i++) {
			JSONObject combineButtonObj = combineButtons.getJSONObject(i);
			combines[i].visible = combineButtonObj.getBoolean("visible");
			boolean enabled = combineButtonObj.getBoolean("enabled");
			int cost = combineButtonObj.getInt("cost");
			combines[i].enable(enabled, cost);
		}

		JSONArray outputArray = args.getJSONArray("outputs");
		for (int i = 0; i < outputArray.length() && i < outputs.length; i++) {
			JSONObject outputObject = outputArray.getJSONObject(i);
			outputs[i].visible = outputObject.getBoolean("visible");
			if (outputObject.isNull("item")) {
				outputs[i].item(null);
			} else {
				outputs[i].item(CustomItem.createItem(outputObject.getJSONObject("item")));
			}
		}
		for (int i = outputArray.length(); i < outputs.length; i++) {
			outputs[i].item(null);
			outputs[i].visible = false;
		}

		energyAddBlinking = args.getBoolean("energy_add_blinking");

		if (args.optBoolean("create_energy_effect", false)) {
			createEnergy();
		}
		if (args.optBoolean("craft_effect", false)) {
			bubbleEmitter.start(Speck.factory( Speck.BUBBLE ), 0.01f, 100 );
			smokeEmitter.burst(Speck.factory( Speck.WOOL ), 10 );
		}

		if (args.has("identify_effect") && !args.isNull("identify_effect")) {
			JSONObject identifyEffect = args.getJSONObject("identify_effect");
			showIdentify(identifyEffect.getJSONObject("old_name"), identifyEffect.getJSONObject("new_name"));
		}

		updateState();
	}

	public void sendResult(int result, boolean longClick) {
		JSONObject object = new JSONObject();
		object.put("long_click", longClick);
		SendData.sendWindowResult(windowID, result, object);
	}
}
