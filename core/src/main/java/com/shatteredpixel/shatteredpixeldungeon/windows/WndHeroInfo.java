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

import com.shatteredpixel.shatteredpixeldungeon.Badges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.CustomTalent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroSubClass;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Talent;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.abilities.ArmorAbility;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.network.JsonStringHelper;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSprite;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.ui.IconButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.shatteredpixel.shatteredpixeldungeon.ui.RenderedTextBlock;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentButton;
import com.shatteredpixel.shatteredpixeldungeon.ui.TalentsPane;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.DeviceCompat;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class WndHeroInfo extends WndTabbed {

	private HeroInfoTab heroInfo;
	private TalentInfoTab talentInfo;
	private SubclassInfoTab subclassInfo;
	private ArmorAbilityInfoTab abilityInfo;

	private static int WIDTH = 120;
	private static int MIN_HEIGHT = 125;
	private static int MARGIN = 2;

    public WndHeroInfo(int id, JSONObject args) {
		super();
		setId(id);
		JSONObject safeArgs = args == null ? new JSONObject() : args;
		int finalHeight = MIN_HEIGHT;

		RemoteHeroInfoTab remoteHeroInfo = new RemoteHeroInfoTab(safeArgs);
		add(remoteHeroInfo);
		remoteHeroInfo.setSize(WIDTH, MIN_HEIGHT);
		finalHeight = (int)Math.max(finalHeight, remoteHeroInfo.height());
		add(new IconTab(heroClassIcon(JsonStringHelper.getString(safeArgs, "hero_class"))) {
			@Override
			protected void select(boolean value) {
				super.select(value);
				remoteHeroInfo.visible = remoteHeroInfo.active = selected;
			}
		});

		RemoteTalentInfoTab remoteTalentInfo = new RemoteTalentInfoTab(safeArgs.optJSONArray("talent_tiers"));
		add(remoteTalentInfo);
		remoteTalentInfo.setSize(WIDTH, MIN_HEIGHT);
		finalHeight = (int)Math.max(finalHeight, remoteTalentInfo.height());
		add(new IconTab(Icons.get(Icons.TALENT)) {
			@Override
			protected void select(boolean value) {
				super.select(value);
				remoteTalentInfo.visible = remoteTalentInfo.active = selected;
			}
		});

		if (safeArgs.optBoolean("subclass_unlocked", false)) {
			RemoteTextListTab subclasses = new RemoteTextListTab(Messages.titleCase(Messages.get(WndHeroInfo.class, "subclasses")), safeArgs.optJSONArray("subclasses"), "title", "short_description");
			add(subclasses);
			subclasses.setSize(WIDTH, MIN_HEIGHT);
			finalHeight = (int)Math.max(finalHeight, subclasses.height());
			add(new IconTab(new ItemSprite(ItemSpriteSheet.MASK, null)) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					subclasses.visible = subclasses.active = selected;
				}
			});
		}

		if (safeArgs.optBoolean("ability_unlocked", false)) {
			RemoteTextListTab abilities = new RemoteTextListTab(Messages.titleCase(Messages.get(WndHeroInfo.class, "abilities")), safeArgs.optJSONArray("abilities"), "name", "short_description");
			add(abilities);
			abilities.setSize(WIDTH, MIN_HEIGHT);
			finalHeight = (int)Math.max(finalHeight, abilities.height());
			add(new IconTab(new ItemSprite(ItemSpriteSheet.CROWN, null)) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					abilities.visible = abilities.active = selected;
				}
			});
		}

		resize(WIDTH, finalHeight);
		layoutTabs();
		int selectedTab = args.optInt("selected_tab", 0);
		if (selectedTab >= 0 && selectedTab < tabs.size()) {
			select(selectedTab);
		} else {
			select(0);
		}
	}

	private static @NotNull Image heroClassIcon(@NotNull String heroClass) {
		try {
			switch (HeroClass.valueOf(heroClass).id()) {
				case "MAGE":
					return new ItemSprite(ItemSpriteSheet.MAGES_STAFF, null);
				case "ROGUE":
					return new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK, null);
				case "HUNTRESS":
					return new ItemSprite(ItemSpriteSheet.SPIRIT_BOW, null);
				case "DUELIST":
					return new ItemSprite(ItemSpriteSheet.RAPIER, null);
				case "CLERIC":
					return new ItemSprite(ItemSpriteSheet.ARTIFACT_TOME, null);
				default:
					return new ItemSprite(ItemSpriteSheet.SEAL, null);
			}
		} catch (IllegalArgumentException e) {
			return Icons.get(Icons.INFO);
		}
	}

	//catalog-only. Do not use in game!
	public WndHeroInfo( HeroClass cl ){

		Image tabIcon;
		switch (cl.id()){
			case "WARRIOR": default:
				tabIcon = new ItemSprite(ItemSpriteSheet.SEAL, null);
				break;
			case "MAGE":
				tabIcon = new ItemSprite(ItemSpriteSheet.MAGES_STAFF, null);
				break;
			case "ROGUE":
				tabIcon = new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK, null);
				break;
			case "HUNTRESS":
				tabIcon = new ItemSprite(ItemSpriteSheet.SPIRIT_BOW, null);
				break;
			case "DUELIST":
				tabIcon = new ItemSprite(ItemSpriteSheet.RAPIER, null);
				break;
			case "CLERIC":
				tabIcon = new ItemSprite(ItemSpriteSheet.ARTIFACT_TOME, null);
				break;
		}

		int finalHeight = MIN_HEIGHT;

		heroInfo = new HeroInfoTab(cl);
		add(heroInfo);
		heroInfo.setSize(WIDTH, MIN_HEIGHT);
		finalHeight = (int)Math.max(finalHeight, heroInfo.height());

		add( new IconTab( tabIcon ){
			@Override
			protected void select(boolean value) {
				super.select(value);
				heroInfo.visible = heroInfo.active = value;
			}
		});

		talentInfo = new TalentInfoTab(cl);
		add(talentInfo);
		talentInfo.setSize(WIDTH, MIN_HEIGHT);
		finalHeight = (int)Math.max(finalHeight, talentInfo.height());

		add( new IconTab( Icons.get(Icons.TALENT) ){
			@Override
			protected void select(boolean value) {
				super.select(value);
				talentInfo.visible = talentInfo.active = value;
			}
		});

		if (Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_2) || DeviceCompat.isDebug()) {
			subclassInfo = new SubclassInfoTab(cl);
			add(subclassInfo);
			subclassInfo.setSize(WIDTH, MIN_HEIGHT);
			finalHeight = (int)Math.max(finalHeight, subclassInfo.height());

			add(new IconTab(new ItemSprite(ItemSpriteSheet.MASK, null)) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					subclassInfo.visible = subclassInfo.active = value;
				}
			});
		}

		if (Badges.isUnlocked(Badges.Badge.BOSS_SLAIN_4) || DeviceCompat.isDebug()) {
			abilityInfo = new ArmorAbilityInfoTab(cl);
			add(abilityInfo);
			abilityInfo.setSize(WIDTH, MIN_HEIGHT);
			finalHeight = (int)Math.max(finalHeight, abilityInfo.height());

			add(new IconTab(new ItemSprite(ItemSpriteSheet.CROWN, null)) {
				@Override
				protected void select(boolean value) {
					super.select(value);
					abilityInfo.visible = abilityInfo.active = value;
				}
			});
		}

		resize(WIDTH, finalHeight);

		layoutTabs();
		talentInfo.layout();

		select(0);

	}

	@Override
	public void offset(int xOffset, int yOffset) {
		super.offset(xOffset, yOffset);
		if (talentInfo != null) {
			talentInfo.layout();
		}
	}

	private static class RemoteHeroInfoTab extends Component {

		private final @NotNull JSONObject args;
		private RenderedTextBlock title;
		private RenderedTextBlock description;

		private RemoteHeroInfoTab(@NotNull JSONObject args) {
			super();
			this.args = args;
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			title = PixelScene.renderTextBlock(JsonStringHelper.getString(args, "title"), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			description = PixelScene.renderTextBlock(JsonStringHelper.getString(args, "description"), 6);
			add(description);
		}

		@Override
		protected void layout() {
			super.layout();
			title.setPos((width - title.width()) / 2, MARGIN);
			description.maxWidth((int)width);
			description.setPos(0, title.bottom() + 4 * MARGIN);
			height = Math.max(height, description.bottom());
		}
	}

	private static class RemoteTalentInfoTab extends Component {

		private final @NotNull JSONArray tiers;
		private RenderedTextBlock title;
		private TalentsPane talentPane;

		private RemoteTalentInfoTab(@org.jetbrains.annotations.Nullable JSONArray tiers) {
			super();
			this.tiers = tiers == null ? new JSONArray() : tiers;
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "talents")), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			talentPane = new TalentsPane(TalentButton.Mode.INFO, parseTalents(tiers));
			add(talentPane);
		}

		@Override
		protected void layout() {
			super.layout();
			title.setPos((width - title.width()) / 2, MARGIN);
			talentPane.setRect(0, title.bottom() + 3 * MARGIN, width, 100);
			height = Math.max(height, talentPane.bottom());
		}
	}

	private static class RemoteTextListTab extends Component {

		private final @NotNull String titleText;
		private final @NotNull JSONArray entries;
		private final @NotNull String titleKey;
		private final @NotNull String descriptionKey;
		private RenderedTextBlock title;
		private final @NotNull ArrayList<RenderedTextBlock> lines = new ArrayList<>();

		private RemoteTextListTab(@NotNull String titleText, @org.jetbrains.annotations.Nullable JSONArray entries, @NotNull String titleKey, @NotNull String descriptionKey) {
			super();
			this.titleText = titleText;
			this.entries = entries == null ? new JSONArray() : entries;
			this.titleKey = titleKey;
			this.descriptionKey = descriptionKey;
		}

		@Override
		protected void createChildren() {
			super.createChildren();
			title = PixelScene.renderTextBlock(titleText, 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			for (int i = 0; i < entries.length(); i++) {
				JSONObject entry = entries.getJSONObject(i);
				RenderedTextBlock line = PixelScene.renderTextBlock(
						Messages.titleCase(JsonStringHelper.getString(entry, titleKey)) + "\n" + JsonStringHelper.getString(entry, descriptionKey),
						6
				);
				lines.add(line);
				add(line);
			}
		}

		@Override
		protected void layout() {
			super.layout();
			title.setPos((width - title.width()) / 2, MARGIN);
			float pos = title.bottom() + 4 * MARGIN;
			for (RenderedTextBlock line : lines) {
				line.maxWidth((int)width);
				line.setPos(0, pos);
				pos = line.bottom() + 4 * MARGIN;
			}
			height = Math.max(height, pos - 4 * MARGIN);
		}
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

	//catalog-only. Do not use in game!
	private static class HeroInfoTab extends Component {

		private RenderedTextBlock title;
		private RenderedTextBlock[] info;
		private Image[] icons;

		public HeroInfoTab(HeroClass cls){
			super();
			title = PixelScene.renderTextBlock(Messages.titleCase(cls.title()), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			String[] desc_entries = cls.desc().split("\n\n");

			info = new RenderedTextBlock[desc_entries.length];

			for (int i = 0; i < desc_entries.length; i++){
				info[i] = PixelScene.renderTextBlock(desc_entries[i], 6);
				add(info[i]);
			}

			switch (cls.id()){
				case "WARRIOR": default:
					icons = new Image[]{ new ItemSprite(ItemSpriteSheet.SEAL),
							new ItemSprite(ItemSpriteSheet.WORN_SHORTSWORD),
							new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
					break;
				case "MAGE":
					icons = new Image[]{ new ItemSprite(ItemSpriteSheet.MAGES_STAFF),
							new ItemSprite(ItemSpriteSheet.WAND_MAGIC_MISSILE),
							new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
					break;
				case "ROGUE":
					icons = new Image[]{ new ItemSprite(ItemSpriteSheet.ARTIFACT_CLOAK),
							Icons.get(Icons.STAIRS),
							new ItemSprite(ItemSpriteSheet.DAGGER),
							new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
					break;
				case "HUNTRESS":
					icons = new Image[]{ new ItemSprite(ItemSpriteSheet.SPIRIT_BOW),
							Icons.GRASS.get(),
							new ItemSprite(ItemSpriteSheet.GLOVES),
							new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
					break;
				case "DUELIST":
					icons = new Image[]{ new ItemSprite(ItemSpriteSheet.RAPIER),
							new ItemSprite(ItemSpriteSheet.WAR_HAMMER),
							new ItemSprite(ItemSpriteSheet.THROWING_SPIKE),
							new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
					break;
				case "CLERIC":
					icons = new Image[]{ new ItemSprite(ItemSpriteSheet.ARTIFACT_TOME),
							Icons.TALENT.get(),
							new ItemSprite(ItemSpriteSheet.CUDGEL),
							new ItemSprite(ItemSpriteSheet.SCROLL_ISAZ)};
					break;
			}
			for (Image im : icons) {
				add(im);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);

			float pos = title.bottom()+4*MARGIN;

			for (int i = 0; i < info.length; i++){
				info[i].maxWidth((int)width - 20);
				info[i].setPos(20, pos);

				icons[i].x = (20-icons[i].width())/2;
				icons[i].y = info[i].top() + (info[i].height() - icons[i].height())/2;
				PixelScene.align(icons[i]);

				pos = info[i].bottom() + 4*MARGIN;
			}

			height = Math.max(height, pos - 4*MARGIN);

		}
	}

	//catalog-only. Do not use in game!
	private static class TalentInfoTab extends Component {

		private RenderedTextBlock title;
		private RenderedTextBlock message;
		private TalentsPane talentPane;

		public TalentInfoTab( HeroClass cls ){
			super();
			title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "talents")), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "talents_msg"), 6);
			add(message);

			ArrayList<LinkedHashMap<Talent, Integer>> talents = new ArrayList<>();
			for (LinkedHashMap<Talent, Integer> tier : cls.talentTiers()) {
				talents.add(new LinkedHashMap<>(tier));
			}
			if (talents.size() > 2) {
				talents.get(2).clear(); //we show T3 talents with subclasses
			}

			talentPane = new TalentsPane(TalentButton.Mode.INFO, talents);
			add(talentPane);
		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);
			message.maxWidth((int)width);
			message.setPos(0, title.bottom()+4*MARGIN);

			talentPane.setRect(0, message.bottom() + 3*MARGIN, width, 85);

			height = Math.max(height, talentPane.bottom());
		}
	}

	//catalog-only. Do not use in game!
	private static class SubclassInfoTab extends Component {

		private RenderedTextBlock title;
		private RenderedTextBlock message;
		private RenderedTextBlock[] subClsDescs;
		private IconButton[] subClsInfos;

		public SubclassInfoTab( HeroClass cls ){
			super();
			title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "subclasses")), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "subclasses_msg"), 6);
			add(message);

			List<HeroSubClass> subClasses = cls.subClasses();

			subClsDescs = new RenderedTextBlock[subClasses.size()];
			subClsInfos = new IconButton[subClasses.size()];

			for (int i = 0; i < subClasses.size(); i++){
				HeroSubClass subClass = subClasses.get(i);
				subClsDescs[i] = PixelScene.renderTextBlock(subClass.shortDesc(), 6);
				int finalI = i;
				subClsInfos[i] = new IconButton( Icons.get(Icons.INFO) ){
					@Override
					protected void onClick() {
						Game.scene().addToFront(new WndInfoSubclass(cls, subClasses.get(finalI)));
					}
				};
				add(subClsDescs[i]);
				add(subClsInfos[i]);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);
			message.maxWidth((int)width);
			message.setPos(0, title.bottom()+4*MARGIN);

			float pos = message.bottom()+4*MARGIN;

			for (int i = 0; i < subClsDescs.length; i++){
				subClsDescs[i].maxWidth((int)width - 20);
				subClsDescs[i].setPos(0, pos);

				subClsInfos[i].setRect(width-20, subClsDescs[i].top() + (subClsDescs[i].height()-20)/2, 20, 20);

				pos = subClsDescs[i].bottom() + 4*MARGIN;
			}

			height = Math.max(height, pos - 4*MARGIN);

		}
	}

	//catalog-only. Do not use in game!
	private static class ArmorAbilityInfoTab extends Component {

		private RenderedTextBlock title;
		private RenderedTextBlock message;
		private RenderedTextBlock[] abilityDescs;
		private IconButton[] abilityInfos;

		public ArmorAbilityInfoTab(HeroClass cls){
			super();
			title = PixelScene.renderTextBlock(Messages.titleCase(Messages.get(WndHeroInfo.class, "abilities")), 9);
			title.hardlight(TITLE_COLOR);
			add(title);

			message = PixelScene.renderTextBlock(Messages.get(WndHeroInfo.class, "abilities_msg"), 6);
			add(message);

			List<ArmorAbility> abilities = cls.armorAbilities();

			abilityDescs = new RenderedTextBlock[abilities.size()];
			abilityInfos = new IconButton[abilities.size()];

			for (int i = 0; i < abilities.size(); i++){
				ArmorAbility ability = abilities.get(i);
				abilityDescs[i] = PixelScene.renderTextBlock(ability.shortDesc(), 6);
				int finalI = i;
				abilityInfos[i] = new IconButton( Icons.get(Icons.INFO) ){
					@Override
					protected void onClick() {
						Game.scene().addToFront(new WndInfoArmorAbility(cls, abilities.get(finalI)));
					}
				};
				add(abilityDescs[i]);
				add(abilityInfos[i]);
			}

		}

		@Override
		protected void layout() {
			super.layout();

			title.setPos((width-title.width())/2, MARGIN);
			message.maxWidth((int)width);
			message.setPos(0, title.bottom()+4*MARGIN);

			float pos = message.bottom()+4*MARGIN;

			for (int i = 0; i < abilityDescs.length; i++){
				abilityDescs[i].maxWidth((int)width - 20);
				abilityDescs[i].setPos(0, pos);

				abilityInfos[i].setRect(width-20, abilityDescs[i].top() + (abilityDescs[i].height()-20)/2, 20, 20);

				pos = abilityDescs[i].bottom() + 4*MARGIN;
			}

			height = Math.max(height, pos - 4*MARGIN);

		}
	}

}
