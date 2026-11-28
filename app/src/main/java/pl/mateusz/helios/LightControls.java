package pl.mateusz.helios;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.*;

/**
 * Fork feature: what the light panel (LightPanel) may show for one HA light, and the service data it sends.
 * The controls follow the light's own supported_color_modes; an unknown mode list shows no slider at all, so the
 * panel never offers a value the light would reject. Unknown values stay unknown (-1), never 0.
 */
final class LightControls {
    /** Attributes retained for every light entity on the dashboard (DashboardSpec.attributes). */
    static final List<String> ATTRIBUTES=Collections.unmodifiableList(Arrays.asList(
            "brightness","color_mode","supported_color_modes","hs_color","color_temp_kelvin","min_color_temp_kelvin","max_color_temp_kelvin"));
    /** Used when the light reports colour temperature support but no range. */
    static final int DEFAULT_MIN_K=2000,DEFAULT_MAX_K=6500;
    private static final Set<String> COLOR=new HashSet<>(Arrays.asList("hs","xy","rgb","rgbw","rgbww"));
    private static final Set<String> DIMMABLE=new HashSet<>(Arrays.asList("brightness","color_temp","white","hs","xy","rgb","rgbw","rgbww"));

    final boolean brightness,temperature,color;
    /** Current values; -1 when the light does not report them (it is off, or HA did not send them). */
    final int brightnessPct,kelvin,hue;
    final int minKelvin,maxKelvin;

    private LightControls(boolean brightness,boolean temperature,boolean color,int brightnessPct,int kelvin,int hue,int minKelvin,int maxKelvin){
        this.brightness=brightness;this.temperature=temperature;this.color=color;
        this.brightnessPct=brightnessPct;this.kelvin=kelvin;this.hue=hue;this.minKelvin=minKelvin;this.maxKelvin=maxKelvin;
    }

    /** A long press opens the panel only on a tile whose entity is a light. */
    static boolean holds(DashboardSpec.Item item){return item!=null&&item.entity!=null&&item.entity.startsWith("light.");}

    static LightControls of(EntityStates.Entity e){
        Set<String> modes=modes(e==null?null:e.attribute("supported_color_modes"));
        boolean dim=false,col=false;
        for(String m:modes){if(DIMMABLE.contains(m))dim=true;if(COLOR.contains(m))col=true;}
        boolean temp=modes.contains("color_temp");
        int min=number(e,"min_color_temp_kelvin"),max=number(e,"max_color_temp_kelvin");
        if(min<=0||max<=min){min=DEFAULT_MIN_K;max=DEFAULT_MAX_K;}
        int b=number(e,"brightness");
        int pct=b<0?-1:Math.max(1,Math.min(100,Math.round(b*100f/255f)));
        int k=number(e,"color_temp_kelvin");
        return new LightControls(dim,temp,col,pct,k<0?-1:clamp(k,min,max),hueOf(e==null?null:e.attribute("hs_color")),min,max);
    }

    /** The light.turn_on data for a brightness slider value (1-100 %). */
    static JSONObject brightnessData(int pct){return data("brightness_pct",Math.max(1,Math.min(100,pct)));}
    /** The light.turn_on data for a colour temperature, kept inside the light's own range. */
    JSONObject temperatureData(int kelvin){return data("color_temp_kelvin",clamp(kelvin,minKelvin,maxKelvin));}
    /** The light.turn_on data for a hue (0-359) at full saturation. */
    static JSONObject hueData(int hue){
        try{return new JSONObject().put("hs_color",new JSONArray().put(((hue%360)+360)%360).put(100));}catch(JSONException ex){throw new IllegalStateException(ex);}
    }

    static Set<String> modes(String json){
        Set<String> out=new HashSet<>();
        if(json==null||json.isEmpty())return out;
        try{JSONArray a=new JSONArray(json);for(int n=0;n<a.length();n++)out.add(a.optString(n));}catch(JSONException ignored){} // not a list: no controls, the panel shows only on/off
        out.remove("");
        return out;
    }
    private static int hueOf(String json){
        if(json==null)return -1;
        try{JSONArray a=new JSONArray(json);double h=a.getDouble(0);return Double.isNaN(h)?-1:((int)Math.round(h)%360+360)%360;}catch(JSONException ex){return -1;}
    }
    private static int number(EntityStates.Entity e,String name){
        String v=e==null?null:e.attribute(name);
        if(v==null)return -1;
        try{double d=Double.parseDouble(v);return Double.isNaN(d)||d<0?-1:(int)Math.round(d);}catch(NumberFormatException ex){return -1;}
    }
    private static int clamp(int v,int min,int max){return Math.max(min,Math.min(max,v));}
    private static JSONObject data(String key,int value){
        try{return new JSONObject().put(key,value);}catch(JSONException ex){throw new IllegalStateException(ex);}
    }
}
