package io.github.pixeldungeonmultiplayer.shattered.client.network.utils;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;

import static com.shatteredpixel.shatteredpixeldungeon.levels.traps.Trap.*;
import static com.shatteredpixel.shatteredpixeldungeon.ui.BuffIndicator.*;

public class TranslationUtils {
    public static String translateTilesTexture(String texture) {
        switch (texture){
            case "tiles0.png": return Assets.Environment.TILES_SEWERS;
            case "tiles1.png": return Assets.Environment.TILES_PRISON;
            case "tiles2.png": return Assets.Environment.TILES_CAVES;
            case "tiles3.png": return Assets.Environment.TILES_CITY;
            case "tiles4.png": return Assets.Environment.TILES_HALLS;
            default: return Assets.Environment.TILES_SEWERS;
        }
    }
    //TODO: add trap support
    public static int translateCell(int id, int cell) {
        switch (id) {
            case 0: return Terrain.CHASM;
            case 1: return Terrain.EMPTY;
            case 2: return Terrain.GRASS;
            case 3: return Terrain.EMPTY_WELL;
            case 4: return Terrain.WALL;
            case 5: return Terrain.DOOR;
            case 6: return Terrain.OPEN_DOOR;
            case 7: return Terrain.ENTRANCE;
            case 8: return Terrain.EXIT;
            case 9: return Terrain.EMBERS;
            case 10: return Terrain.LOCKED_DOOR;
            case 11: return Terrain.PEDESTAL;
            case 12: return Terrain.WALL_DECO;
            case 13: return Terrain.BARRICADE;
            case 14: return Terrain.EMPTY_SP;
            case 15: return Terrain.HIGH_GRASS;
            case 24: return Terrain.EMPTY_DECO;
            case 25: return Terrain.LOCKED_EXIT;
            case 26: return Terrain.UNLOCKED_EXIT;
            //no sign
            case 29: return Terrain.GRASS;
            case 34: return Terrain.WELL;
            case 35: return Terrain.STATUE;
            case 36: return Terrain.STATUE_SP;
            case 41: return Terrain.BOOKSHELF;
            case 42: return Terrain.ALCHEMY;
            case 43: return Terrain.CHASM;
            case 44: return Terrain.CHASM;
            case 45: return Terrain.CHASM;
            case 46: return Terrain.CHASM;
            case 16: return Terrain.SECRET_DOOR;
            case 17: setTrap(cell, GREEN, DOTS, true); return Terrain.TRAP;
            case 18: return Terrain.EMPTY;
            case 19: setTrap(cell, RED, DOTS, true);return Terrain.TRAP;
            case 20: return Terrain.EMPTY;

            case 21: setTrap(cell, YELLOW, DOTS, true);return Terrain.TRAP;
            case 22: return Terrain.EMPTY;
            case 23: setTrap(cell, BLACK, DOTS, true, false);return Terrain.INACTIVE_TRAP;
            case 27: setTrap(cell, VIOLET, DOTS, true);return Terrain.TRAP;
            case 28: return Terrain.EMPTY;
            case 30: setTrap(cell, RED, DOTS, true);return Terrain.TRAP;
            case 31: return Terrain.EMPTY;
            case 32: setTrap(cell, TEAL, DOTS, true);return Terrain.TRAP;
            case 33: return Terrain.EMPTY;
            case 37: setTrap(cell, GREY, DOTS, true);return Terrain.TRAP;
            case 38: return Terrain.EMPTY;
            case 39: setTrap(cell, TEAL, WAVES, true);return Terrain.TRAP;
            case 40: return Terrain.EMPTY;
            //PD uses different id for every water type
            case 48: return Terrain.WATER;
            case 49: return Terrain.WATER;
            case 50: return Terrain.WATER;
            case 51: return Terrain.WATER;
            case 52: return Terrain.WATER;
            case 53: return Terrain.WATER;
            case 54: return Terrain.WATER;
            case 55: return Terrain.WATER;
            case 56: return Terrain.WATER;
            case 57: return Terrain.WATER;
            case 58: return Terrain.WATER;
            case 59: return Terrain.WATER;
            case 60: return Terrain.WATER;
            case 61: return Terrain.WATER;
            case 62: return Terrain.WATER;
            case 63: return Terrain.WATER;
            default: return id;
        }
    }
    private static void setTrap(int cell, int trapColor, int trapShape, boolean trapVisible) {
        Trap trap = new Trap() {
            {
                color = trapColor;
                shape = trapShape;
                visible = trapVisible;
            }
            @Override
            public void activate() {

            }

        };
        Dungeon.level.setTrap(trap, cell);
    }
    private static void setTrap(int cell, int trapColor, int trapShape, boolean trapVisible, boolean trapActive) {
        Trap trap = new Trap() {
            {
                color = trapColor;
                shape = trapShape;
                visible = trapVisible;
                active =  trapActive;
            }
            @Override
            public void activate() {

            }

        };
        Dungeon.level.setTrap(trap, cell);
    }
    public static int translateItemImage(int image){
        switch (image) {
            case 0: return ItemSpriteSheet.BONES;
            case 1: return ItemSpriteSheet.ANKH;
            case 2: return ItemSpriteSheet.SHORTSWORD;
            case 3: return ItemSpriteSheet.WAND_MAGIC_MISSILE;
            case 4: return ItemSpriteSheet.RATION;
            case 5: return ItemSpriteSheet.WEAPON_HOLDER;
            case 6: return ItemSpriteSheet.ARMOR_HOLDER;
            case 7: return ItemSpriteSheet.RING_HOLDER;
            case 8: return ItemSpriteSheet.ARTIFACT_KEY;
            case 9: return ItemSpriteSheet.IRON_KEY;
            case 10: return ItemSpriteSheet.GOLDEN_KEY;
            case 11: return ItemSpriteSheet.CHEST;
            case 12: return ItemSpriteSheet.LOCKED_CHEST;
            case 13: return ItemSpriteSheet.TOMB;
            case 14: return ItemSpriteSheet.GOLD;
            case 15: return ItemSpriteSheet.SHURIKEN;
            case 16: return ItemSpriteSheet.GLOVES;
            case 17: return ItemSpriteSheet.QUARTERSTAFF;
            case 18: return ItemSpriteSheet.MACE;
            case 19: return ItemSpriteSheet.DIRK;
            case 20: return ItemSpriteSheet.SWORD;
            case 21: return ItemSpriteSheet.LONGSWORD;
            case 22: return ItemSpriteSheet.BATTLE_AXE;
            case 23: return ItemSpriteSheet.WAR_HAMMER;
            case 24: return ItemSpriteSheet.ARMOR_CLOTH;
            case 25: return ItemSpriteSheet.ARMOR_LEATHER;
            case 26: return ItemSpriteSheet.ARMOR_MAIL;
            case 27: return ItemSpriteSheet.ARMOR_SCALE;
            case 28: return ItemSpriteSheet.ARMOR_PLATE;
            case 29: return ItemSpriteSheet.SPEAR;
            case 30: return ItemSpriteSheet.GLAIVE;
            case 31: return ItemSpriteSheet.DART;
            case 32: return ItemSpriteSheet.RING_DIAMOND;
            case 33: return ItemSpriteSheet.RING_OPAL;
            case 34: return ItemSpriteSheet.RING_GARNET;
            case 35: return ItemSpriteSheet.RING_RUBY;
            case 36: return ItemSpriteSheet.RING_AMETHYST;
            case 37: return ItemSpriteSheet.RING_TOPAZ;
            case 38: return ItemSpriteSheet.RING_ONYX;
            case 39: return ItemSpriteSheet.RING_TOURMALINE;
            case 40: return ItemSpriteSheet.SCROLL_KAUNAN;
            case 41: return ItemSpriteSheet.SCROLL_SOWILO;
            case 42: return ItemSpriteSheet.SCROLL_LAGUZ;
            case 43: return ItemSpriteSheet.SCROLL_YNGVI;
            case 44: return ItemSpriteSheet.SCROLL_GYFU;
            case 45: return ItemSpriteSheet.SCROLL_RAIDO;
            case 46: return ItemSpriteSheet.SCROLL_ISAZ;
            case 47: return ItemSpriteSheet.SCROLL_MANNAZ;
            case 48: return ItemSpriteSheet.WAND_PRISMATIC_LIGHT;
            case 49: return ItemSpriteSheet.WAND_DISINTEGRATION;
            case 50: return ItemSpriteSheet.WAND_CORROSION;
            case 51: return ItemSpriteSheet.WAND_CORRUPTION;
            case 52: return ItemSpriteSheet.WAND_WARDING;
            case 53: return ItemSpriteSheet.WAND_FROST;
            case 54: return ItemSpriteSheet.WAND_LIGHTNING;
            case 55: return ItemSpriteSheet.WAND_FIREBOLT;
            case 56: return ItemSpriteSheet.POTION_TURQUOISE;
            case 57: return ItemSpriteSheet.POTION_CRIMSON;
            case 58: return ItemSpriteSheet.POTION_AZURE;
            case 59: return ItemSpriteSheet.POTION_JADE;
            case 60: return ItemSpriteSheet.POTION_GOLDEN;
            case 61: return ItemSpriteSheet.POTION_MAGENTA;
            case 62: return ItemSpriteSheet.POTION_CHARCOAL;
            case 63: return ItemSpriteSheet.POTION_IVORY;
            case 64: return ItemSpriteSheet.POTION_AMBER;
            case 65: return ItemSpriteSheet.POTION_BISTRE;
            case 66: return ItemSpriteSheet.POTION_INDIGO;
            case 67: return ItemSpriteSheet.POTION_SILVER;
            case 68: return ItemSpriteSheet.WAND_REGROWTH;
            case 69: return ItemSpriteSheet.WAND_TRANSFUSION;
            case 70: return ItemSpriteSheet.WAND_LIVING_EARTH;
            case 71: return ItemSpriteSheet.WAND_BLAST_WAVE;
            case 72: return ItemSpriteSheet.RING_EMERALD;
            case 73: return ItemSpriteSheet.RING_SAPPHIRE;
            case 74: return ItemSpriteSheet.RING_QUARTZ;
            case 75: return ItemSpriteSheet.RING_AGATE;
            case 76: return ItemSpriteSheet.SCROLL_NAUDIZ;
            case 77: return ItemSpriteSheet.SCROLL_BERKANAN;
            case 78: return ItemSpriteSheet.SCROLL_ODAL;
            case 79: return ItemSpriteSheet.SCROLL_TIWAZ;

            case 80: return ItemSpriteSheet.STYLUS;
            case 81: return ItemSpriteSheet.DEWDROP;
            case 82: return ItemSpriteSheet.MASK;
            case 83: return ItemSpriteSheet.POUCH;
            case 84: return ItemSpriteSheet.TORCH;
            case 85: return ItemSpriteSheet.BEACON;
            case 86: return ItemSpriteSheet.CROWN;
            case 87: return ItemSpriteSheet.AMULET;
            case 88: return ItemSpriteSheet.SEED_FIREBLOOM;
            case 89: return ItemSpriteSheet.SEED_ICECAP;
            case 90: return ItemSpriteSheet.SEED_SORROWMOSS;
            case 91: return ItemSpriteSheet.SEED_MAGEROYAL;
            case 92: return ItemSpriteSheet.SEED_SUNGRASS;
            case 93: return ItemSpriteSheet.SEED_EARTHROOT;
            case 94: return ItemSpriteSheet.SEED_FADELEAF;
            case 95: return ItemSpriteSheet.SEED_ROTBERRY;
            case 96: return ItemSpriteSheet.ARMOR_ROGUE;
            case 97: return ItemSpriteSheet.ARMOR_WARRIOR;
            case 98: return ItemSpriteSheet.ARMOR_MAGE;
            case 99: return ItemSpriteSheet.ARMOR_HUNTRESS;
            case 100: return ItemSpriteSheet.ARTIFACT_ROSE3;
            case 101: return ItemSpriteSheet.PICKAXE;
            case 102: return ItemSpriteSheet.ORE;
            case 103: return ItemSpriteSheet.RAT_SKULL;
            case 104: return ItemSpriteSheet.HOLDER;
            case 105: return ItemSpriteSheet.CRYSTAL_CHEST;
            case 106: return ItemSpriteSheet.BOOMERANG;
            case 107: return ItemSpriteSheet.TOMAHAWK;
            case 108: return ItemSpriteSheet.INCENDIARY_DART;
            case 109: return ItemSpriteSheet.PARALYTIC_DART;
            case 110: return ItemSpriteSheet.JAVELIN;
            case 111: return ItemSpriteSheet.HOLSTER;
            case 112: return ItemSpriteSheet.PASTY;
            case 113: return ItemSpriteSheet.MEAT;
            case 114: return ItemSpriteSheet.STEAK;
            case 115: return ItemSpriteSheet.OVERPRICED;
            case 116: return ItemSpriteSheet.CARPACCIO;
            case 117: return ItemSpriteSheet.ARTIFACT_SPELLBOOK;
            case 118: return ItemSpriteSheet.PHANTOM_MEAT;
            case 119: return ItemSpriteSheet.SOMETHING;
            case 120: return ItemSpriteSheet.WATERSKIN;
            case 121: return ItemSpriteSheet.DUST;
            case 122: return ItemSpriteSheet.TOKEN;
            case 123: return ItemSpriteSheet.STONE_AUGMENTATION;
            case 124: return ItemSpriteSheet.BOMB;
            case 125: return ItemSpriteSheet.HONEYPOT;
            case 126: return ItemSpriteSheet.SOMETHING;
            case 127: return ItemSpriteSheet.SOMETHING;


            default: return image;
        }
    }
    public static int translateBuffIcon(int buffIcon){
        switch (buffIcon) {
            case -1: return NONE;
            case 29: return VERTIGO;
            case 30: return RAGE;
            case 31: return SACRIFICE;
        }
        return buffIcon;
    }
}
