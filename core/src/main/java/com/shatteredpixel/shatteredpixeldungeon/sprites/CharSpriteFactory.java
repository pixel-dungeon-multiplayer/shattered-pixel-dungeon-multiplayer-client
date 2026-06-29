package com.shatteredpixel.shatteredpixeldungeon.sprites;

import com.shatteredpixel.shatteredpixeldungeon.actors.hero.HeroClass;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;

import java.util.Objects;

public abstract class CharSpriteFactory {

	public abstract CharSprite create();

	public static CharSpriteFactory missing() {
		return missing("CharSpriteFactory.missing", "no sprite factory data");
	}

	public static CharSpriteFactory missing(String source, String reason) {
		return new MissingFactory(source, reason);
	}

	public static CharSpriteFactory forClass(Class<? extends CharSprite> spriteClass, Integer tier, HeroClass heroClass) {
		return new ClassFactory(spriteClass, tier, heroClass);
	}

	public static CharSpriteFactory forAsset(String spriteAsset) {
		return new AssetFactory(spriteAsset);
	}

	private static final class ClassFactory extends CharSpriteFactory {

		private final Class<? extends CharSprite> spriteClass;
		private final Integer tier;
		private final HeroClass heroClass;

		private ClassFactory(Class<? extends CharSprite> spriteClass, Integer tier, HeroClass heroClass) {
			this.spriteClass = spriteClass;
			this.tier = tier;
			this.heroClass = heroClass;
		}

		@Override
		public CharSprite create() {
			if (spriteClass == null) {
				GLog.w("CharSpriteFactory.ClassFactory: sprite class is null. Use MissingSprite default");
				return new MissingSprite();
			}
			CharSprite sprite = CharSprite.spriteFromClass(spriteClass);
			if (sprite instanceof TieredSprite && tier != null) {
				((TieredSprite) sprite).updateTier(tier);
			}
			if (sprite instanceof ClassSprite && heroClass != null) {
				((ClassSprite) sprite).updateHeroClass(heroClass);
			}
			return sprite;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof ClassFactory)) {
				return false;
			}
			ClassFactory other = (ClassFactory) obj;
			return Objects.equals(spriteClass, other.spriteClass)
					&& Objects.equals(tier, other.tier)
					&& Objects.equals(heroClass, other.heroClass);
		}

		@Override
		public int hashCode() {
			return Objects.hash(spriteClass, tier, heroClass);
		}
	}

	private static final class MissingFactory extends CharSpriteFactory {

		private final String source;
		private final String reason;

		private MissingFactory(String source, String reason) {
			this.source = source;
			this.reason = reason;
		}

		@Override
		public CharSprite create() {
			GLog.w("%s: %s. Use MissingSprite default", source, reason);
			return new MissingSprite();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof MissingFactory)) {
				return false;
			}
			MissingFactory other = (MissingFactory) obj;
			return Objects.equals(source, other.source)
					&& Objects.equals(reason, other.reason);
		}

		@Override
		public int hashCode() {
			return Objects.hash(source, reason);
		}
	}

	private static final class AssetFactory extends CharSpriteFactory {

		private final String spriteAsset;

		private AssetFactory(String spriteAsset) {
			this.spriteAsset = spriteAsset;
		}

		@Override
		public CharSprite create() {
			try {
				return new CustomCharSprite(spriteAsset);
			} catch (Exception e) {
				GLog.w("CharSpriteFactory.AssetFactory: failed to create sprite asset \"%s\" (%s: %s). Use MissingSprite default",
						spriteAsset, e.getClass().getSimpleName(), e.getMessage());
				e.printStackTrace();
				return new MissingSprite();
			}
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (!(obj instanceof AssetFactory)) {
				return false;
			}
			AssetFactory other = (AssetFactory) obj;
			return Objects.equals(spriteAsset, other.spriteAsset);
		}

		@Override
		public int hashCode() {
			return Objects.hash(spriteAsset);
		}
	}
}
