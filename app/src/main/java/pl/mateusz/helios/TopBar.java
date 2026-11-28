package pl.mateusz.helios;

/**
 * Fork feature: the HELIOS bar can collapse so the grid takes the whole screen. A collapsed bar comes back over the
 * grid while it has something to say: an issue for as long as it lasts, a status message for a short time.
 */
final class TopBar {
    /** How long a status message keeps a collapsed bar on screen. */
    static final long MESSAGE_MS=8000;
    /** A finger held this long in the menu corner opens the menu while the bar, and so the HELIOS label, is collapsed. */
    static final long HOLD_MS=1000;
    /** The menu corner, in screen units from the top-left edge. A hold anywhere else belongs to the tile under the finger (its long press). */
    static final int MENU_CORNER_W=96,MENU_CORNER_H=72;
    /** The page dots sit in the strip under the bar, or in the top gap of the grid when the bar is collapsed. */
    static final int DOTS_TOP=44,DOTS_TOP_COLLAPSED=1;
    private TopBar(){}
    /** Where the grid starts, in screen units: under the bar, or at the top edge when the bar is collapsed. */
    static int gridTop(boolean collapsed){return collapsed?0:DashboardView.BAR;}
    static int dotsTop(boolean collapsed){return collapsed?DOTS_TOP_COLLAPSED:DOTS_TOP;}
    /** An expanded bar always shows; a collapsed one shows for an issue, or for a message that is still fresh. */
    static boolean shown(boolean collapsed,String issue,String message,boolean fresh){
        return !collapsed||issue!=null||(fresh&&message!=null&&!message.isEmpty());
    }
    /** The same, plus an update notice (UpdateCheck) that is still fresh: it brings a collapsed bar back for MESSAGE_MS, so its button can be tapped. */
    static boolean shown(boolean collapsed,String issue,String message,boolean fresh,boolean updateFresh){
        return shown(collapsed,issue,message,fresh)||updateFresh;
    }
    /** True when a touch at (x, y) in pixels falls in the menu corner of a dashboard drawn at this scale. */
    static boolean inMenuCorner(float x,float y,float scale){
        return scale>0&&x>=0&&y>=0&&x<MENU_CORNER_W*scale&&y<MENU_CORNER_H*scale;
    }
}
