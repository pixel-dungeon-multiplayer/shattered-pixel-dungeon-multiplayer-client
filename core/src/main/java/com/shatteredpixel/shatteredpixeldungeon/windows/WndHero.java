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

package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.SPDAction;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.Statistics;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.CustomTalent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.HeroSprite;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIcon;
import com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.ScrollPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.StatusPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.shatteredpixel.shatteredpixeldungeon.ui.Window;
import com.shatteredpixel.shatteredpixeldungeon.utils.DungeonSeed;
import com.watabou.input.KeyBindings;
import com.watabou.input.KeyEvent;
import com.watabou.noosa.Gizmo;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Locale;

public class WndHero extends WndTabbed {
	
	private static final int WIDTH		= 120;
	private static final int HEIGHT		= 120;
	
	private StatsTab stats;
	private TalentsTab talents;
	private BuffsTab buffs;

	public static int lastIdx = 0;

	public WndHero(int id, @NotNull JSONObject args) {
		super();
		setId(id);
		resize(WIDTH, HEIGHT);

		RemoteStatsTab remoteStats = new RemoteStatsTab(args);
		add(remoteStats);
		remoteStats.setRect(0, 0, WIDTH, HEIGHT);

		RemoteTalentsTab remoteTalents = new RemoteTalentsTab(args.optJSONArray("talent_tiers"));
		add(remoteTalents);
		remoteTalents.setRect(0, 0, WIDTH, HEIGHT);

		RemoteBuffsTab remoteBuffs = new RemoteBuffsTab(args.optJSONArray("buffs"));
		add(remoteBuffs);
		remoteBuffs.setRect(0, 0, WIDTH, HEIGHT);

		add(new IconTab(Icons.get(Icons.RANKINGS)) {
			@Override
			protected void select(boolean value) {
				super.select(value);
				remoteStats.visible = remoteStats.active = selected;
			}
		});
		add(new IconTab(Icons.get(Icons.TALENT)) {
			@Override
			protected void select(boolean value) {
				super.select(value);
				remoteTalents.visible = remoteTalents.active = selected;
			}
		});
		add(new IconTab(Icons.get(Icons.BUFFS)) {
			@Override
			protected void select(boolean value) {
				super.select(value);
				remoteBuffs.visible = remoteBuffs.active = selected;
			}
		});

		layoutTabs();
		int selectedTab = args.optInt("selected_tab", 0);
		if (selectedTab >= 0 && selectedTab < tabs.size()) {
			select(selectedTab);
		} else {
			select(0);
		}
	}

	public WndHero() {
		
		super();
		
		resize( WIDTH, HEIGHT );
		
		stats = new StatsTab();
		add( stats );

		talents = new TalentsTab();
		add(talents);
		talents.setRect(0, 0, WIDTH, HEIGHT);

		buffs = new BuffsTab();
		add( buffs );
		buffs.setRect(0, 0, WIDTH, HEIGHT);
		buffs.setupList();
		
		add( new IconTab( Icons.get(Icons.RANKINGS) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) {
					lastIdx = 0;
					if (!stats.visible) {
						stats.initialize();
					}
				}
				stats.visible = stats.active = selected;
			}
		} );
		add( new IconTab( Icons.get(Icons.TALENT) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) lastIdx = 1;
				if (selected) StatusPane.talentBlink = 0;
				talents.visible = talents.active = selected;
			}
		} );
		add( new IconTab( Icons.get(Icons.BUFFS) ) {
			protected void select( boolean value ) {
				super.select( value );
				if (selected) lastIdx = 2;
				buffs.visible = buffs.active = selected;
			}
		} );

		layoutTabs();

		talents.setRect(0, 0, WIDTH, HEIGHT);
		talents.pane.scrollTo(0, talents.pane.content().height() - talents.pane.height());
		talents.layout();

		select( lastIdx );
	}

	@Override
	public boolean onSignal(KeyEvent event) {
		if (event.pressed && KeyBindings.getActionForKey( event ) == SPDAction.HERO_INFO) {
			onBackPressed();
			return true;
		} else {
			return super.onSignal(event);
		}
	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		talents.layout();
		buffs.layout();
	}

	private class StatsTab extends Group {
		
		private static final int GAP = 6;
		
		private float pos;
		
		public StatsTab() {
			initialize();
		}

		public void initialize(){

			for (Gizmo g : members){
				if (g != null) g.destroy();
			}
			clear();
			
			Hero hero = Dungeon.hero;

			IconTitle title = new IconTitle();
			title.icon( HeroSprite.avatar(hero) );
			if (hero.name().equals(hero.className()))
				title.label( Messages.get(this, "title", hero.lvl, hero.className() ).toUpperCase( Locale.ENGLISH ) );
			else
				title.label((hero.name() + "\n" + Messages.get(this, "title", hero.lvl, hero.className())).toUpperCase(Locale.ENGLISH));
			title.color(Window.TITLE_COLOR);
			title.setRect( 0, 0, WIDTH-16, 0 );
			add(title);

			IconButton infoButton = new IconButton(Icons.get(Icons.INFO)){
				@Override
				protected void onClick() {
					super.onClick();
					if (ShatteredPixelDungeon.scene() instanceof GameScene){
						GameScene.show(new WndHeroInfo(hero.heroClass));
					} else {
						ShatteredPixelDungeon.scene().addToFront(new WndHeroInfo(hero.heroClass));
					}
				}

				@Override
				protected String hoverText() {
					return Messages.titleCase(Messages.get(WndKeyBindings.class, "hero_info"));
				}

			};
			infoButton.setRect(title.right(), 0, 16, 16);
			add(infoButton);

			pos = title.bottom() + 2*GAP;

			int strBonus = hero.STR() - hero.STR;
			if (strBonus > 0)           statSlot( Messages.get(this, "str"), hero.STR + " + " + strBonus );
			else if (strBonus < 0)      statSlot( Messages.get(this, "str"), hero.STR + " - " + -strBonus );
			else                        statSlot( Messages.get(this, "str"), hero.STR() );
			if (hero.shielding() > 0)   statSlot( Messages.get(this, "health"), hero.HP + "+" + hero.shielding() + "/" + hero.HT );
			else                        statSlot( Messages.get(this, "health"), (hero.HP) + "/" + hero.HT );
			statSlot( Messages.get(this, "exp"), hero.exp + "/" + hero.maxExp() );

			pos += GAP;

			statSlot( Messages.get(this, "gold"), Statistics.goldCollected );
			statSlot( Messages.get(this, "depth"), Statistics.deepestFloor );
			if (Dungeon.daily){
				if (!Dungeon.dailyReplay) {
					statSlot(Messages.get(this, "daily_for"), "_" + Dungeon.customSeedText + "_");
				} else {
					statSlot(Messages.get(this, "replay_for"), "_" + Dungeon.customSeedText + "_");
				}
			} else if (!Dungeon.customSeedText.isEmpty()){
				statSlot( Messages.get(this, "custom_seed"), "_" + Dungeon.customSeedText + "_" );
			} else {
				statSlot( Messages.get(this, "dungeon_seed"), DungeonSeed.convertToCode(Dungeon.seed) );
			}

			pos += GAP;
		}

		private void statSlot( String label, String value ) {

			int size = 8;
			RenderedTextBlock txt;
			do {
				txt = PixelScene.renderTextBlock( label, size );
				size--;
			} while (txt.width() >= WIDTH * 0.55f);
			txt.setPos(0, pos + (6 - txt.height())/2);
			PixelScene.align(txt);
			add( txt );

			size = 8;
			do {
				txt = PixelScene.renderTextBlock( value, size );
				size--;
			} while (txt.width() >= WIDTH * 0.45f);
			txt.setPos(WIDTH * 0.55f, pos + (6 - txt.height())/2);
			PixelScene.align(txt);
			add( txt );
			
			pos += GAP + txt.height();
		}
		
		private void statSlot( String label, int value ) {
			statSlot( label, Integer.toString( value ) );
		}
		
		public float height() {
			return pos;
		}
	}

	private static class RemoteStatsTab extends Component {

		private static final int GAP = 6;

		private final @Nullable JSONObject args;
		private float pos;

		private RemoteStatsTab(@NotNull JSONObject args) {
			super();
			this.args = args;
			createChildren();
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			if (args == null) { return; }

			IconTitle title = new IconTitle();
			JSONObject ownerHero = args.optJSONObject("owner_hero");
			String classId = ownerHero == null ? JsonStringHelper.optString(args, "hero_class", "") : JsonStringHelper.optString(ownerHero, "class", "");
			HeroClass heroClass = heroClass(classId);
			int armorTier = ownerHero == null ? args.optInt("tier", args.optInt("armor_tier", 0)) : ownerHero.optInt("tier", ownerHero.optInt("armor_tier", 0));
			title.icon(remoteHeroIcon(heroClass, armorTier));
			title.label(JsonStringHelper.getString(args, "title"));
			title.color(Window.TITLE_COLOR);
			title.setRect(0, 0, WIDTH, 0);
			add(title);

			pos = title.bottom() + 2 * GAP;
			JSONArray stats = args.optJSONArray("stats");
			if (stats != null) {
				for (int i = 0; i < stats.length(); i++) {
					if (stats.isNull(i)) {
						pos += GAP;
						continue;
					}
					JSONObject stat = stats.getJSONObject(i);
					statSlot(JsonStringHelper.getString(stat, "label"), String.valueOf(stat.get("value")));
				}
			}
		}

		private void statSlot(@NotNull String label, @NotNull String value) {
			int size = 8;
			RenderedTextBlock txt;
			do {
				txt = PixelScene.renderTextBlock(label, size);
				size--;
			} while (txt.width() >= WIDTH * 0.55f);
			txt.setPos(0, pos + (6 - txt.height()) / 2);
			PixelScene.align(txt);
			add(txt);

			size = 8;
			do {
				txt = PixelScene.renderTextBlock(value, size);
				size--;
			} while (txt.width() >= WIDTH * 0.45f);
			txt.setPos(WIDTH * 0.55f, pos + (6 - txt.height()) / 2);
			PixelScene.align(txt);
			add(txt);

			pos += GAP + txt.height();
		}

		private static @Nullable HeroClass heroClass(@NotNull String classId) {
			try {
				return HeroClass.valueOf(classId);
			} catch (IllegalArgumentException e) {
				return null;
			}
		}

		private static @NotNull Image remoteHeroIcon(@Nullable HeroClass heroClass, int armorTier) {
			if (heroClass != null) {
				try {
					return HeroSprite.avatar(heroClass, armorTier);
				} catch (RuntimeException ignored) {
					return HeroSprite.avatar(heroClass, 0);
				}
			}
			return Icons.get(Icons.RANKINGS);
		}
	}

	private static class RemoteTalentsTab extends Component {

		private final @Nullable JSONArray tiers;
		private TalentsPane pane;

		private RemoteTalentsTab(@Nullable JSONArray tiers) {
			super();
			this.tiers = tiers == null ? new JSONArray() : tiers;
			createChildren();
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			if (tiers == null) { return; }
			pane = new TalentsPane(TalentButton.Mode.INFO, parseTalents(tiers));
			add(pane);
		}

		@Override
		protected void layout() {
			super.layout();
			pane.setRect(x, y, width, height);
		}

		private static @NotNull ArrayList<LinkedHashMap<Talent, Integer>> parseTalents(@NotNull JSONArray tiers) {
			ArrayList<LinkedHashMap<Talent, Integer>> result = new ArrayList<>();
			for (int i = 0; i < tiers.length(); i++) {
				JSONObject tier = tiers.getJSONObject(i);
				LinkedHashMap<Talent, Integer> talents = new LinkedHashMap<>();
				JSONArray talentArray = tier.optJSONArray("talents");
				if (talentArray != null) {
					for (int j = 0; j < talentArray.length(); j++) {
						JSONObject talent = talentArray.getJSONObject(j);
						talents.put(CustomTalent.fromJson(talent), talent.optInt("points", 0));
					}
				}
				result.add(talents);
			}
			return result;
		}
	}

	private static class RemoteBuffsTab extends Component {

		private static final int GAP = 2;

		private final @Nullable JSONArray buffs;
		private ScrollPane buffList;
		private float pos;
		private ArrayList<RemoteBuffSlot> slots = new ArrayList<>();

		private RemoteBuffsTab(@Nullable JSONArray buffs) {
			super();
			this.buffs = buffs == null ? new JSONArray() : buffs;
			createChildren();
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			if (buffs == null) { return; }
			buffList = new ScrollPane(new Component()) {
				@Override
				public void onClick(float x, float y) {
					int size = slots.size();
					for (int i = 0; i < size; i++) {
						if (slots.get(i).onClick(x, y)) {
							break;
						}
					}
				}
			};
			add(buffList);
			Component content = buffList.content();
			for (int i = 0; i < buffs.length(); i++) {
				JSONObject buff = buffs.getJSONObject(i);
				if (buff.optInt("icon", 0) == BuffIndicator.NONE) {
					continue;
				}
				RemoteBuffSlot slot = new RemoteBuffSlot(buff);
				slot.setRect(0, pos, WIDTH, slot.icon.height());
				content.add(slot);
				slots.add(slot);
				pos += GAP + slot.height();
			}
			content.setSize(WIDTH, pos);
		}

		@Override
		protected void layout() {
			super.layout();
			buffList.setRect(0, 0, width, height);
		}
	}

	private static class RemoteBuffSlot extends Component {

		private static final int GAP = 2;

		private final @Nullable JSONObject buff;
		private Image icon;
		private RenderedTextBlock txt;

		private RemoteBuffSlot(@NotNull JSONObject buff) {
			super();
			this.buff = buff;
			createChildren();
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			if (buff == null) { return; }
			icon = new BuffIcon(buff.optInt("icon", 0), true);
			if (buff.has("hardlight")) {
				JSONObject hardlight = buff.optJSONObject("hardlight");
				if (hardlight != null) {
					icon.hardlight(
							(float)hardlight.optDouble("rm", 0),
							(float)hardlight.optDouble("gm", 0),
							(float)hardlight.optDouble("bm", 0));
				}
			}
			add(icon);

			txt = PixelScene.renderTextBlock(Messages.titleCase(JsonStringHelper.getString(buff, "name")), 8);
			add(txt);
		}

		@Override
		protected void layout() {
			super.layout();
			icon.y = this.y;
			txt.maxWidth((int)(width - icon.width()));
			txt.setPos(
					icon.width + GAP,
					this.y + (icon.height - txt.height()) / 2
			);
			PixelScene.align(txt);
		}

		private boolean onClick(float x, float y) {
			if (inside(x, y) && buff != null) {
				GameScene.show(new RemoteBuffInfo(buff));
				return true;
			}
			return false;
		}
	}

	private static class RemoteBuffInfo extends Window {

		private static final float GAP = 2;
		private static final int WIDTH = 120;

		private RemoteBuffInfo(@NotNull JSONObject buff) {
			super();

			IconTitle titlebar = new IconTitle();
			Image buffIcon = new BuffIcon(buff.optInt("icon", 0), true);
			if (buff.has("hardlight")) {
				JSONObject hardlight = buff.optJSONObject("hardlight");
				if (hardlight != null) {
					buffIcon.hardlight(
							(float)hardlight.optDouble("rm", 0),
							(float)hardlight.optDouble("gm", 0),
							(float)hardlight.optDouble("bm", 0));
				}
			}

			titlebar.icon(buffIcon);
			titlebar.label(Messages.titleCase(JsonStringHelper.getString(buff, "name")), Window.TITLE_COLOR);
			titlebar.setRect(0, 0, WIDTH, 0);
			add(titlebar);

			RenderedTextBlock txtInfo = PixelScene.renderTextBlock(JsonStringHelper.getString(buff, "description"), 6);
			txtInfo.maxWidth(WIDTH);
			txtInfo.setPos(titlebar.left(), titlebar.bottom() + 2 * GAP);
			add(txtInfo);

			resize(WIDTH, (int)txtInfo.bottom() + 2);
		}
	}

	public class TalentsTab extends Component {

		TalentsPane pane;

		@Override
		protected void createChildren() {
			super.createChildren();
			pane = new TalentsPane(TalentButton.Mode.UPGRADE);
			add(pane);
		}

		@Override
		protected void layout() {
			super.layout();
			pane.setRect(x, y, width, height);
		}

	}
	
	private class BuffsTab extends Component {
		
		private static final int GAP = 2;
		
		private float pos;
		private ScrollPane buffList;
		private ArrayList<BuffSlot> slots = new ArrayList<>();

		@Override
		protected void createChildren() {

			super.createChildren();

			buffList = new ScrollPane( new Component() ){
				@Override
				public void onClick( float x, float y ) {
					int size = slots.size();
					for (int i=0; i < size; i++) {
						if (slots.get( i ).onClick( x, y )) {
							break;
						}
					}
				}
			};
			add(buffList);
		}
		
		@Override
		protected void layout() {
			super.layout();
			buffList.setRect(0, 0, width, height);
		}
		
		private void setupList() {
			Component content = buffList.content();
			for (Buff buff : Dungeon.hero.buffs()) {
				if (buff.icon() != BuffIndicator.NONE) {
					BuffSlot slot = new BuffSlot(buff);
					slot.setRect(0, pos, WIDTH, slot.icon.height());
					content.add(slot);
					slots.add(slot);
					pos += GAP + slot.height();
				}
			}
			content.setSize(buffList.width(), pos);
			buffList.setSize(buffList.width(), buffList.height());
		}

		private class BuffSlot extends Component {

			private Buff buff;

			Image icon;
			RenderedTextBlock txt;

			public BuffSlot( Buff buff ){
				super();
				this.buff = buff;

				icon = new BuffIcon(buff, true);
				icon.y = this.y;
				add( icon );

				txt = PixelScene.renderTextBlock( Messages.titleCase(buff.name()), 8 );
				txt.setPos(
						icon.width + GAP,
						this.y + (icon.height - txt.height()) / 2
				);
				PixelScene.align(txt);
				add( txt );

			}

			@Override
			protected void layout() {
				super.layout();
				icon.y = this.y;
				txt.maxWidth((int)(width - icon.width()));
				txt.setPos(
						icon.width + GAP,
						this.y + (icon.height - txt.height()) / 2
				);
				PixelScene.align(txt);
			}
			
			protected boolean onClick ( float x, float y ) {
				if (inside( x, y )) {
					GameScene.show(new WndInfoBuff(buff));
					return true;
				} else {
					return false;
				}
			}
		}
	}
}
