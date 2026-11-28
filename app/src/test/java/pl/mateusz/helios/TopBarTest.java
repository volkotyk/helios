package pl.mateusz.helios;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TopBarTest {
    @Test public void expandedBarAlwaysShows() {
        assertTrue(TopBar.shown(false,null,"",false));
        assertTrue(TopBar.shown(false,null,null,false));
    }

    @Test public void collapsedBarHidesWithNothingToSay() {
        assertFalse(TopBar.shown(true,null,"",true));
        assertFalse(TopBar.shown(true,null,null,true));
    }

    @Test public void collapsedBarShowsForAnIssueUntilItIsGone() {
        assertTrue(TopBar.shown(true,"HA niedostępny - dane nieaktualne","",false));
    }

    @Test public void collapsedBarShowsAMessageOnlyWhileItIsFresh() {
        assertTrue(TopBar.shown(true,null,"Odpowiadam…",true));
        assertFalse(TopBar.shown(true,null,"Odpowiadam…",false));
    }

    @Test public void collapsedBarShowsAFreshUpdateNoticeOnly() {
        assertTrue(TopBar.shown(true,null,"",false,true));
        assertFalse(TopBar.shown(true,null,"",false,false));
        assertTrue(TopBar.shown(false,null,"",false,false));
    }

    @Test public void collapsedGridStartsAtTheTopEdge() {
        assertEquals(DashboardView.BAR,TopBar.gridTop(false));
        assertEquals(0,TopBar.gridTop(true));
    }

    @Test public void dotsStayInAGapAboveTheFirstRow() {
        // the dots are 6 units high; they must end before the first row starts
        assertTrue(TopBar.dotsTop(true)+6<=TopBar.gridTop(true)+DashboardView.GAP);
        assertTrue(TopBar.dotsTop(false)+6<=TopBar.gridTop(false)+DashboardView.GAP);
        assertTrue(TopBar.dotsTop(false)>=32+10); // under the bar text (10..42)
    }

    @Test public void menuHoldWorksOnlyInTheTopLeftCorner() {
        float scale=1.5f; // 1200 x 720 pixels for the 800 x 480 grid
        assertTrue(TopBar.inMenuCorner(0,0,scale));
        assertTrue(TopBar.inMenuCorner(TopBar.MENU_CORNER_W*scale-1,TopBar.MENU_CORNER_H*scale-1,scale));
        assertFalse(TopBar.inMenuCorner(TopBar.MENU_CORNER_W*scale,10,scale));
        assertFalse(TopBar.inMenuCorner(10,TopBar.MENU_CORNER_H*scale,scale));
        assertFalse(TopBar.inMenuCorner(600,300,scale)); // the middle of the grid belongs to the tiles
        assertFalse(TopBar.inMenuCorner(-1,10,scale));
        assertFalse(TopBar.inMenuCorner(10,10,0));
    }

    @Test public void menuCornerIsABigEnoughTouchTarget() {
        assertTrue(TopBar.MENU_CORNER_W>=64&&TopBar.MENU_CORNER_H>=64);
    }
}
