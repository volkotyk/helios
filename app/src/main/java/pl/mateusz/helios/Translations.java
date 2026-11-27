package pl.mateusz.helios;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.json.JSONObject;
import java.util.Iterator;

/** Polish source text is the fallback when a locale or an entry is unavailable. */
final class Translations {
    /** Languages with a catalogue in assets/i18n/<code>.json, in menu order; Polish is the source and needs none. */
    static final List<String> CATALOGUES=Collections.unmodifiableList(Arrays.asList("en","de","uk"));
    private final Map<String,Map<String,String>> catalogues;

    Translations(Map<String,Map<String,String>> catalogues) {
        Map<String,Map<String,String>> copy=new HashMap<>();
        for(Map.Entry<String,Map<String,String>> e:catalogues.entrySet())copy.put(e.getKey(),Collections.unmodifiableMap(new HashMap<>(e.getValue())));
        this.catalogues=Collections.unmodifiableMap(copy);
    }

    static Map<String,String> parse(String json) throws Exception {
        JSONObject source=new JSONObject(json);
        Map<String,String> result=new HashMap<>();
        for(Iterator<String> keys=source.keys();keys.hasNext();) {
            String key=keys.next();result.put(key,source.getString(key));
        }
        return result;
    }

    static String effective(String choice,Locale system) {
        String language="auto".equals(choice)?system.getLanguage():choice;
        return CATALOGUES.contains(language)?language:"pl";
    }

    static Locale locale(String choice,Locale system) {
        switch(effective(choice,system)) {
            case "en":return Locale.UK;
            case "de":return Locale.GERMANY;
            case "uk":return new Locale("uk","UA");
            default:return new Locale("pl","PL");
        }
    }

    /** Every value the language menu can hold: auto, pl, then the catalogues in menu order. */
    static List<String> choices() {
        List<String> all=new ArrayList<>(Arrays.asList("auto","pl"));all.addAll(CATALOGUES);return all;
    }

    /** The next menu value; an unknown value starts the cycle again at auto. */
    static String nextChoice(String choice) {
        List<String> all=choices();return all.get((all.indexOf(choice)+1)%all.size());
    }

    String text(String choice,Locale system,String polish) {
        String language=effective(choice,system);
        Map<String,String> catalogue=catalogues.get(language);
        String translated=catalogue==null?null:catalogue.get(polish);
        return translated==null?polish:translated;
    }
}
