package pl.mateusz.helios;

import java.util.Arrays;
import java.util.List;

/**
 * Fork feature: on a tile that runs something (an automation, a script, a scene, a switch, a light) the name tells
 * which tile it is, and the on/off state is secondary. Such a tile shows its name large and its state small. A tile
 * that reads a value (a sensor, a door, a lock, an attribute) keeps the upstream order: small name, large value.
 */
final class TileEmphasis {
    /** Name sizes in screen units: the upstream title is 17 and the upstream FIT value 24..28. */
    static final float NAME_MIN=19,NAME_MAX=26,STATE=17;
    private static final List<String> TYPES=Arrays.asList("entity","tile","light");
    private static final List<String> DOMAINS=Arrays.asList("automation","script","scene","switch","input_boolean","light","fan","button","input_button");
    private TileEmphasis(){}
    /** True when the tile shows its name above a small state. attribute: the card shows an attribute, which is a reading. */
    static boolean nameFirst(String type,String entity,String attribute){
        if(!TYPES.contains(type)||entity==null||attribute!=null)return false;
        int dot=entity.indexOf('.');
        return dot>0&&DOMAINS.contains(entity.substring(0,dot));
    }
}
