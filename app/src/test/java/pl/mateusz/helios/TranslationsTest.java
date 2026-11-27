package pl.mateusz.helios;

import org.junit.Test;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TranslationsTest {
    private static Translations sample() {
        Map<String,String> en=new HashMap<>(),de=new HashMap<>(),uk=new HashMap<>();
        en.put("Zamknij", "Close");
        de.put("Zamknij", "Schließen");
        uk.put("Zamknij", "Закрити");
        Map<String,Map<String,String>> catalogues=new HashMap<>();
        catalogues.put("en",en);catalogues.put("de",de);catalogues.put("uk",uk);
        return new Translations(catalogues);
    }

    private static Map<String,String> asset(String language) throws Exception {
        return Translations.parse(new String(Files.readAllBytes(Paths.get("src/main/assets/i18n",language+".json")),StandardCharsets.UTF_8));
    }

    @Test public void manualChoiceOverridesSystemLanguage() {
        assertEquals("Schließen",sample().text("de",Locale.US,"Zamknij"));
        assertEquals("Close",sample().text("en",Locale.GERMANY,"Zamknij"));
        assertEquals("Закрити",sample().text("uk",Locale.US,"Zamknij"));
    }

    @Test public void automaticChoiceUsesSupportedSystemLanguage() {
        assertEquals("Schließen",sample().text("auto",Locale.GERMANY,"Zamknij"));
        assertEquals("Close",sample().text("auto",Locale.UK,"Zamknij"));
        assertEquals("Закрити",sample().text("auto",new Locale("uk","UA"),"Zamknij"));
        assertEquals("Zamknij",sample().text("auto",Locale.FRANCE,"Zamknij"));
    }

    @Test public void missingTranslationFallsBackToPolish() {
        assertEquals("Brak danych",sample().text("de",Locale.GERMANY,"Brak danych"));
        assertEquals("Zamknij",sample().text("pl",Locale.UK,"Zamknij"));
    }

    @Test public void readsCataloguesWithEscapedText() throws Exception {
        Map<String,Map<String,String>> catalogues=new HashMap<>();
        catalogues.put("de",Translations.parse("{\"Głos\\nHA\":\"Stimme\\nHA\"}"));
        assertEquals("Stimme\nHA",new Translations(catalogues).text("de",Locale.US,"Głos\nHA"));
    }

    @Test public void cyclesThroughAutomaticAndFourLanguages() {
        assertEquals("pl",Translations.nextChoice("auto"));
        assertEquals("en",Translations.nextChoice("pl"));
        assertEquals("de",Translations.nextChoice("en"));
        assertEquals("uk",Translations.nextChoice("de"));
        assertEquals("auto",Translations.nextChoice("uk"));
        assertEquals("auto",Translations.nextChoice("fr"));
    }

    @Test public void everyCatalogueTranslatesTheSameKeys() throws Exception {
        Set<String> reference=asset("en").keySet();
        for(String language:Translations.CATALOGUES) {
            Map<String,String> catalogue=asset(language);
            assertEquals(language,reference,catalogue.keySet());
            for(Map.Entry<String,String> e:catalogue.entrySet()) {
                String key=e.getKey(),value=e.getValue();
                assertTrue(language+": "+key,!value.trim().isEmpty()&&key.startsWith(" ")==value.startsWith(" ")&&key.endsWith(" ")==value.endsWith(" "));
            }
        }
    }

    @Test public void numberFormattingFollowsChosenLanguage() {
        assertEquals("1.5",String.format(Translations.locale("en",Locale.GERMANY),"%.1f",1.5));
        assertEquals("1,5",String.format(Translations.locale("de",Locale.US),"%.1f",1.5));
        assertEquals("1,5",String.format(Translations.locale("pl",Locale.US),"%.1f",1.5));
        assertEquals("1,5",String.format(Translations.locale("uk",Locale.US),"%.1f",1.5));
    }
}
