package pl.mateusz.helios;

import org.junit.Test;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TileEmphasisTest {
    @Test public void tilesThatRunSomethingShowTheirNameFirst() {
        assertTrue(TileEmphasis.nameFirst("tile","automation.halloween",null));
        assertTrue(TileEmphasis.nameFirst("tile","script.briefing",null));
        assertTrue(TileEmphasis.nameFirst("tile","scene.kino",null));
        assertTrue(TileEmphasis.nameFirst("entity","input_boolean.christmas",null));
        assertTrue(TileEmphasis.nameFirst("entity","switch.kettle",null));
        assertTrue(TileEmphasis.nameFirst("light","light.kitchen",null));
    }

    @Test public void readingsKeepTheLargeValue() {
        assertFalse(TileEmphasis.nameFirst("tile","sensor.temperature",null));
        assertFalse(TileEmphasis.nameFirst("entity","binary_sensor.front_door",null));
        assertFalse(TileEmphasis.nameFirst("tile","lock.front",null));
        assertFalse(TileEmphasis.nameFirst("tile","light.kitchen","brightness"));
    }

    @Test public void otherCardTypesAreNotChanged() {
        assertFalse(TileEmphasis.nameFirst("climate","climate.living",null));
        assertFalse(TileEmphasis.nameFirst("energy","sensor.pv",null));
        assertFalse(TileEmphasis.nameFirst("music",null,null));
        assertFalse(TileEmphasis.nameFirst("tile",null,null));
        assertFalse(TileEmphasis.nameFirst("tile","nodot",null));
    }

    @Test public void nameStaysLargerThanTheState() {
        assertTrue(TileEmphasis.NAME_MIN>TileEmphasis.STATE);
        assertTrue(TileEmphasis.NAME_MAX>=TileEmphasis.NAME_MIN);
    }
}
