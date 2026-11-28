package pl.mateusz.helios;

import org.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.*;
import static org.junit.Assert.*;

public class LightControlsTest {
    @BeforeClass public static void icons() throws Exception {MdiIconsTest.install();}

    private static EntityStates.Entity light(String state,String... pairs){
        Map<String,String> a=new HashMap<>();for(int n=0;n<pairs.length;n+=2)a.put(pairs[n],pairs[n+1]);
        return new EntityStates.Entity(state,a);
    }
    private static DashboardSpec.Item item(String entity){
        return new DashboardSpec.Item("id","tile",1,1,1,1,null,null,entity,null,null,"toggle",null,null,false,null,Collections.emptyList(),null,null,null,null);
    }

    @Test public void onlyLightTilesTakeTheLongPress() {
        assertTrue(LightControls.holds(item("light.table")));
        assertFalse(LightControls.holds(item("switch.lamp")));
        assertFalse(LightControls.holds(item("sensor.light_level")));
        assertFalse(LightControls.holds(item(null)));
        assertFalse(LightControls.holds(null));
    }

    @Test public void colourLampShowsEverySliderWithItsValues() {
        // light.table on the owner's HA: hs + color_temp, 2700-6500 K, yellow at full brightness
        LightControls c=LightControls.of(light("on","supported_color_modes","[\"color_temp\",\"hs\"]","brightness","255","hs_color","[60,100]",
                "color_temp_kelvin","null","min_color_temp_kelvin","2700","max_color_temp_kelvin","6500"));
        assertTrue(c.brightness);assertTrue(c.temperature);assertTrue(c.color);
        assertEquals(100,c.brightnessPct);assertEquals(60,c.hue);assertEquals(-1,c.kelvin);
        assertEquals(2700,c.minKelvin);assertEquals(6500,c.maxKelvin);
    }

    @Test public void controlsFollowTheSupportedModes() {
        LightControls onoff=LightControls.of(light("on","supported_color_modes","[\"onoff\"]"));
        assertFalse(onoff.brightness);assertFalse(onoff.temperature);assertFalse(onoff.color);
        LightControls dim=LightControls.of(light("on","supported_color_modes","[\"brightness\"]","brightness","128"));
        assertTrue(dim.brightness);assertFalse(dim.temperature);assertFalse(dim.color);assertEquals(50,dim.brightnessPct);
        LightControls white=LightControls.of(light("on","supported_color_modes","[\"color_temp\"]","color_temp_kelvin","3000"));
        assertTrue(white.brightness);assertTrue(white.temperature);assertFalse(white.color);assertEquals(3000,white.kelvin);
        LightControls rgb=LightControls.of(light("on","supported_color_modes","[\"rgb\"]"));
        assertTrue(rgb.brightness);assertTrue(rgb.color);assertFalse(rgb.temperature);
    }

    @Test public void unknownValuesStayUnknown() {
        LightControls off=LightControls.of(light("off","supported_color_modes","[\"color_temp\",\"hs\"]"));
        assertEquals(-1,off.brightnessPct);assertEquals(-1,off.kelvin);assertEquals(-1,off.hue);
        LightControls none=LightControls.of(null);
        assertFalse(none.brightness);assertFalse(none.temperature);assertFalse(none.color);
        LightControls broken=LightControls.of(light("on","supported_color_modes","hs","brightness","bright"));
        assertFalse(broken.brightness);assertEquals(-1,broken.brightnessPct);
    }

    @Test public void lowBrightnessNeverReadsAsZero() {
        assertEquals(1,LightControls.of(light("on","supported_color_modes","[\"brightness\"]","brightness","1")).brightnessPct);
    }

    @Test public void missingRangeUsesTheDefaultAndValuesAreClamped() {
        LightControls c=LightControls.of(light("on","supported_color_modes","[\"color_temp\"]","color_temp_kelvin","9000"));
        assertEquals(LightControls.DEFAULT_MIN_K,c.minKelvin);assertEquals(LightControls.DEFAULT_MAX_K,c.maxKelvin);assertEquals(LightControls.DEFAULT_MAX_K,c.kelvin);
        assertEquals(LightControls.DEFAULT_MIN_K,c.temperatureData(1000).optInt("color_temp_kelvin"));
    }

    @Test public void serviceDataUsesKelvinPercentAndHueAtFullSaturation() throws Exception {
        assertEquals("{\"brightness_pct\":40}",LightControls.brightnessData(40).toString());
        assertEquals(1,LightControls.brightnessData(0).getInt("brightness_pct"));
        assertEquals(100,LightControls.brightnessData(250).getInt("brightness_pct"));
        LightControls c=LightControls.of(light("on","supported_color_modes","[\"color_temp\"]","min_color_temp_kelvin","2700","max_color_temp_kelvin","6500"));
        JSONObject t=c.temperatureData(4000);assertEquals(4000,t.getInt("color_temp_kelvin"));assertFalse(t.has("color_temp"));
        assertEquals("[120,100]",LightControls.hueData(120).getJSONArray("hs_color").toString());
        assertEquals("[10,100]",LightControls.hueData(370).getJSONArray("hs_color").toString());
        assertEquals("[350,100]",LightControls.hueData(-10).getJSONArray("hs_color").toString());
    }

    @Test public void dashboardRetainsTheLightAttributes() throws Exception {
        DashboardSpec spec=DashboardSpec.parse(new JSONObject(DashboardSpecV6Test.example().toString()));
        Set<String> kept=spec.attributes().get("light.salon");
        assertNotNull(kept);assertTrue(kept.containsAll(LightControls.ATTRIBUTES));
        Set<String> sensor=spec.attributes().get("sensor.temperatura_salon");
        assertTrue(sensor==null||!sensor.contains("supported_color_modes"));
    }
}
