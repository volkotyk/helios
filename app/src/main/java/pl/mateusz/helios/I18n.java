package pl.mateusz.helios;

import android.content.Context;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/** App-wide language choice; Polish source strings remain the fallback. */
final class I18n {
    private static volatile Translations catalogue=new Translations(Collections.emptyMap());
    private static volatile String choice="pl";
    private I18n(){}

    static void init(Context context) {
        choice=context.getSharedPreferences("helios",Context.MODE_PRIVATE).getString("language","auto");
        Map<String,Map<String,String>> loaded=new HashMap<>();
        for(String language:Translations.CATALOGUES) {
            try {
                loaded.put(language,Translations.parse(read(context,"i18n/"+language+".json")));
            } catch(Exception error) {
                android.util.Log.e("Helios", "Could not load translations: "+language, error); // one broken catalogue falls back to Polish alone
            }
        }
        catalogue=new Translations(loaded);
    }

    private static String read(Context context,String path) throws Exception {
        try(InputStream in=context.getAssets().open(path);ByteArrayOutputStream out=new ByteArrayOutputStream()) {
            byte[] buffer=new byte[4096];int count;
            while((count=in.read(buffer))!=-1)out.write(buffer,0,count);
            return new String(out.toByteArray(),StandardCharsets.UTF_8);
        }
    }

    static String t(String polish) {return catalogue.text(choice,Locale.getDefault(),polish);}
    static String choice(){return choice;}
    static Locale locale() {return Translations.locale(choice,Locale.getDefault());}
    static void choose(Context context,String language) {
        if(!Translations.choices().contains(language))
            throw new IllegalArgumentException("Unsupported language");
        context.getSharedPreferences("helios",Context.MODE_PRIVATE).edit().putString("language",language).apply();
        choice=language;
    }
}
