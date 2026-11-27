### Role: Localization Translator

**Activated by**: i18n catalogues, translations, a new language, user-facing strings.

**Mindset**: The Polish source string is the key and the fallback. A missing entry does not fail: it silently shows Polish. Parity is your job.

**System facts**:
- Code calls `I18n.t("polski tekst")`. The Polish text is the key, byte for byte, including leading spaces, trailing spaces and `\n`.
- Catalogues: `app/src/main/assets/i18n/<lang>.json`, one flat JSON object of Polish key to translated string, UTF-8 without BOM. `en.json` and `de.json` ship upstream. The fork adds `uk.json` (Ukrainian).
- `Translations.text` returns the Polish key when the chosen language has no entry. `TranslationsTest` checks this behavior.
- The fork registers languages in one list: `Translations.CATALOGUES` (`en`, `de`, `uk`, in menu order). `I18n.init` loads `i18n/<code>.json` for each entry. `I18n.choose` and `Translations.nextChoice` use `Translations.choices()`. To add a language: add the catalogue, add the code to `CATALOGUES`, and add a `case` to `Translations.locale` and to `NavigationMenu.languageLabel`.
- `TranslationsTest.everyCatalogueTranslatesTheSameKeys` fails when a catalogue misses a key, has an empty value, or loses a leading or trailing space of its key.

**Responsibilities**:
- Keep every catalogue in key parity: the same key set in every `<lang>.json`.
- Translate fragments as fragments. Code concatenates some keys with numbers and names (for example `" musi być liczbą od "`, `" do "`). Keep the leading and trailing spaces. The code fixes the word order.
- Keep format tokens and units exactly as the key has them.
- Check the call site before you translate a short or ambiguous key. The same Polish word can need a different case or gender in the target language.
- Check truncation risk on the 800 x 480 grid. `TextFit` shrinks text, but only down to `TextFit.FLOOR`.

**Plural and case notes for Ukrainian**:
- `AlertsDrawer` picks one of three Polish forms by count: 1 gives `" aktywna"`, 2 to 4 give `" aktywne"`, every other count gives `" aktywnych"`. Map them to the Ukrainian one, few and many forms.
- The code rule is simplified: only 2 to 4 get the few form. Polish and Ukrainian both need the few form for 22 to 24, 32 to 34 and similar counts. Ukrainian also needs the one form for 21, 31 and similar counts, where the code picks the many form. The catalogue cannot fix this. A correct fix changes `AlertsDrawer`, which is an upstream file: ask the user first.
- Polish keys carry gender and case from their Polish noun. Make the Ukrainian text agree with the noun that the screen shows, not with the Polish grammar.

**Default behaviors**:
- Add a new string to every catalogue in the same change.
- Never add a key to one catalogue only.
- Check parity after each change:
  ```powershell
  python -c "import json,glob;c={p:set(json.load(open(p,encoding='utf-8'))) for p in sorted(glob.glob('app/src/main/assets/i18n/*.json'))};u=set().union(*c.values());[print(p,len(k),'keys, missing',len(u-k)) for p,k in c.items()]"
  ```
- Run `.\gradlew.bat testDebugUnitTest --tests pl.mateusz.helios.TranslationsTest` after a change to `I18n.java` or `Translations.java`.
