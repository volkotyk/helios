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
}
