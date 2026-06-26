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

package com.shatteredpixel.shatteredpixeldungeon.messages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.I18NBundle;
import io.github.pixeldungeonmultiplayer.common.localizedstring.LocalizedKey;
import io.github.pixeldungeonmultiplayer.common.localizedstring.LocalizedString;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IllegalFormatException;
import java.util.Locale;

/*
	Simple wrapper class for libGDX I18NBundles.

	The core idea here is that each string resource's key is a combination of the class definition and a local value.
	An object or static method would usually call this with an object/class reference (usually its own) and a local key.
	This means that an object can just ask for "name" rather than, say, "items.weapon.enchantments.death.name"
 */
public class Messages {

	private static ArrayList<I18NBundle> bundles;
	private static Languages lang;
	private static Locale locale;

	public static final String NO_TEXT_FOUND = "!!!NO TEXT FOUND!!!";

	public static Languages lang(){
		return lang;
	}

	public static Locale locale(){
		return locale;
	}

	/**
	 * Setup Methods
	 */

	private static String[] prop_files = new String[]{
			Assets.Messages.ACTORS,
			Assets.Messages.ITEMS,
			Assets.Messages.JOURNAL,
			Assets.Messages.LEVELS,
			Assets.Messages.MISC,
			Assets.Messages.PLANTS,
			Assets.Messages.SCENES,
			Assets.Messages.UI,
			Assets.Messages.WINDOWS
	};

	static{
		formatters = new HashMap<>();
		setup(SPDSettings.language());
	}

	public static void setup( Languages lang ){
		//seeing as missing keys are part of our process, this is faster than throwing an exception
		I18NBundle.setExceptionOnMissingKey(false);

		//store language and locale info for various string logic
		Messages.lang = lang;
		Locale bundleLocal;
		if (lang == Languages.ENGLISH){
			locale = Locale.ENGLISH;
			bundleLocal = Locale.ROOT; //english is source, uses root locale for fetching bundle
		} else {
			locale = new Locale(lang.code());
			bundleLocal = locale;
		}
		formatters.clear();

		bundles = new ArrayList<>();
		for (String file : prop_files) {
			if (bundleLocal.getLanguage().equals("id")){
				//This is a really silly hack to fix some platforms using "id" for indonesian and some using "in" (Android 14- mostly).
				//So if we detect "id" then we treat "###_in" as the base bundle so that it gets loaded instead of English.
				bundles.add(I18NBundle.createBundle(Gdx.files.internal(file + "_in"), bundleLocal));
			} else {
				bundles.add(I18NBundle.createBundle(Gdx.files.internal(file), bundleLocal));
			}
		}
	}



	/**
	 * Resource grabbing methods
	 */

	public static String get(String key, Object...args){
		return resolve((Class<?>) null, key, args);
	}

	public static String get(Object o, String k, Object...args){
		return resolve(o.getClass(), k, args);
	}

	public static String get(Class<?> c, String k, Object...args){
		return resolve(c, k, args);
	}

	public static String resolve(String key, Object...args){
		return resolve((Class<?>) null, key, args);
	}

	public static String resolve(Object o, String k, Object...args){
		return resolve(o.getClass(), k, args);
	}

	public static String resolve(LocalizedString text) {
		return text.resolve();
	}

	public static String resolve(LocalizedKey key, Object... args) {
		String[] ownerClasses = key.ownerClasses();
		if (ownerClasses == null || ownerClasses.length == 0) {
			return resolve(key.name(), args);
		}
		return resolveByOwners(ownerClasses, key.name(), args);
	}

	public static String resolve(Class<?> c, String k, Object...args){
		if (c != null){
			return resolveByOwners(ownerHierarchy(c), k, args);
		}

		return resolveByKey(k, k, args);
	}

	private static String resolveByKey(String key, String fallbackKey, Object... args) {
		String value = getFromBundle(key.toLowerCase(Locale.ENGLISH));
		if (value != null){
			if (args.length > 0) return resolveFormat(value, args);
			else return value;
		} else {
			//this is so child classes can inherit properties from their parents.
			//in cases where text is commonly grabbed as a utility from classes that aren't mean to be instantiated
			//(e.g. flavourbuff.dispTurns()) using .class directly is probably smarter to prevent unnecessary recursive calls.

			return fallbackKey;
		}
	}

	private static String resolveByOwners(String[] ownerClasses, String fallbackKey, Object... args) {
		for (String ownerClass : ownerClasses) {
			String value = getFromBundle((toPropertyOwner(ownerClass) + "." + fallbackKey).toLowerCase(Locale.ENGLISH));
			if (value != null) {
				if (args.length > 0) return resolveFormat(value, args);
				else return value;
			}
		}
		return fallbackKey;
	}

	private static String toPropertyOwner(String ownerClass) {
		return ownerClass.replace("com.shatteredpixel.shatteredpixeldungeon.", "");
	}

	private static String[] ownerHierarchy(Class<?> c) {
		ArrayList<String> owners = new ArrayList<>();
		while (c != null) {
			owners.add(c.getName());
			c = c.getSuperclass();
		}
		return owners.toArray(new String[0]);
	}

	private static String getFromBundle(String key){
		String result;
		for (I18NBundle b : bundles){
			result = b.get(key);
			//if it isn't the return string for no key found, return it
			if (result.length() != key.length()+6 || !result.contains(key)){
				return result;
			}
		}
		return null;
	}



	/**
	 * String Utility Methods
	 */

	public static String format( String format, Object...args ) {
		return resolveFormat(format, args);
	}

	public static String concat( Object...parts ) {
		return resolveConcat(parts);
	}

	public static String resolveFormat( String format, Object...args ) {
		try {
			return String.format(locale(), format, args);
		} catch (IllegalFormatException e) {
			ShatteredPixelDungeon.reportException( new Exception("formatting error for the string: " + format, e) );
			return format;
		}
	}

	private static HashMap<String, DecimalFormat> formatters;

	public static String decimalFormat( String format, double number ){
		if (!formatters.containsKey(format)){
			formatters.put(format, new DecimalFormat(format, DecimalFormatSymbols.getInstance(locale())));
		}
		return formatters.get(format).format(number);
	}

	public static String capitalize( String str ){
		return resolveCapitalize(str);
	}

	public static String capitalize( LocalizedString text ){
		return resolveCapitalize(text.resolve());
	}

	public static String resolveCapitalize( String str ){
		if (str.length() == 0)  return str;
		else                    return str.substring( 0, 1 ).toUpperCase(locale) + str.substring( 1 );
	}

	//Words which should not be capitalized in title case, mostly prepositions which appear ingame
	//This list is not comprehensive!
	private static final HashSet<String> noCaps = new HashSet<>(
			Arrays.asList("a", "an", "and", "of", "by", "to", "the", "x", "for")
	);

	public static String titleCase( String str ){
		return resolveTitleCase(str);
	}

	public static String titleCase( LocalizedString text ){
		return resolveTitleCase(text.resolve());
	}

	public static String resolveTitleCase( String str ){
		//English capitalizes every word except for a few exceptions
		if (lang == Languages.ENGLISH){
			String result = "";
			//split by any unicode space character
			for (String word : str.split("(?<=\\p{Zs})")){
				if (noCaps.contains(word.trim().toLowerCase(Locale.ENGLISH).replaceAll(":|[0-9]", ""))){
					result += word;
				} else {
					result += resolveCapitalize(word);
				}
			}
			//first character is always capitalized.
			return resolveCapitalize(result);
		}

		//Otherwise, use sentence case
		return resolveCapitalize(str);
	}

	public static String upperCase( String str ){
		return resolveUpperCase(str);
	}

	public static String upperCase( LocalizedString text ){
		return resolveUpperCase(text.resolve());
	}

	public static String toUpperCase( String str, Locale ignoredLocale ){
		return resolveToUpperCase(str, ignoredLocale);
	}

	public static String toUpperCase( LocalizedString text, Locale ignoredLocale ){
		return resolveToUpperCase(text.resolve(), ignoredLocale);
	}

	public static String resolveUpperCase( String str ){
		return str.toUpperCase(locale);
	}

	public static String resolveToUpperCase( String str, Locale locale ){
		return str.toUpperCase(locale);
	}

	public static String lowerCase( String str ){
		return resolveLowerCase(str);
	}

	public static String lowerCase( LocalizedString text ){
		return resolveLowerCase(text.resolve());
	}

	public static String resolveLowerCase( String str ){
		return str.toLowerCase(locale);
	}

	private static String resolveConcat(Object[] parts) {
		StringBuilder result = new StringBuilder();
		for (Object part : parts) {
			if (part instanceof LocalizedString) {
				result.append(resolve((LocalizedString) part));
			} else if (part != null) {
				result.append(part);
			}
		}
		return result.toString();
	}

}
