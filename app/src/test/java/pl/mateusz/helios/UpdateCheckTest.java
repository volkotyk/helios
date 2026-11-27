package pl.mateusz.helios;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UpdateCheckTest {
    private static final long NOW=1_800_000_000_000L,HOUR=3_600_000L,MINUTE=60_000L;

    @Test public void offMeansNoCheck() {
        assertEquals(-1,UpdateCheck.delay(false,NOW,0,0));
        assertEquals(-1,UpdateCheck.delay(false,NOW,NOW-10*HOUR,3));
    }

    @Test public void firstCheckWaitsForTheNetwork() {
        assertEquals(UpdateCheck.FIRST_DELAY_MS,UpdateCheck.delay(true,NOW,0,0));
    }

    @Test public void aClockThatWentBackChecksSoonInsteadOfNever() {
        assertEquals(UpdateCheck.FIRST_DELAY_MS,UpdateCheck.delay(true,NOW,NOW+30*24*HOUR,0));
    }

    @Test public void goodCheckWaitsTheFullInterval() {
        assertEquals(UpdateCheck.INTERVAL_MS,UpdateCheck.delay(true,NOW,NOW,0));
        assertEquals(UpdateCheck.INTERVAL_MS-HOUR,UpdateCheck.delay(true,NOW,NOW-HOUR,0));
    }

    @Test public void overdueCheckStillWaitsTheFloor() {
        // a service restart after a long sleep must not fire at once, and never in a tight loop
        assertEquals(UpdateCheck.FIRST_DELAY_MS,UpdateCheck.delay(true,NOW,NOW-7*HOUR,0));
        assertEquals(UpdateCheck.FIRST_DELAY_MS,UpdateCheck.delay(true,NOW,NOW-HOUR,1));
    }

    @Test public void failuresBackOffAndStopAtTheInterval() {
        assertEquals(15*MINUTE,UpdateCheck.retry(1));
        assertEquals(30*MINUTE,UpdateCheck.retry(2));
        assertEquals(60*MINUTE,UpdateCheck.retry(3));
        assertEquals(UpdateCheck.INTERVAL_MS,UpdateCheck.retry(6));
        assertEquals(UpdateCheck.INTERVAL_MS,UpdateCheck.retry(1000));
        assertEquals(30*MINUTE,UpdateCheck.delay(true,NOW,NOW,2));
    }

    @Test public void apiBudgetStaysFarUnderTheGitHubLimit() {
        // the worst steady case: every request fails, one per floor after the backoff tops out
        assertTrue(HOUR/UpdateCheck.FIRST_DELAY_MS<=30);
        assertTrue(UpdateCheck.RETRY_MS>=UpdateCheck.FIRST_DELAY_MS);
        assertTrue(HOUR/UpdateCheck.RETRY_MS<=4);
    }

    @Test public void noticeOffersOnlyANewerVersion() {
        assertEquals("0.21.190",UpdateCheck.notice(true,"0.21.190","0.21.183"));
        assertNull("installed already",UpdateCheck.notice(true,"0.21.190","0.21.190"));
        assertNull("older than installed",UpdateCheck.notice(true,"0.21.180","0.21.183"));
        assertNull("nothing found",UpdateCheck.notice(true,null,"0.21.183"));
        assertNull("checks off",UpdateCheck.notice(false,"0.21.190","0.21.183"));
        assertNull("a malformed version offers nothing",UpdateCheck.notice(true,"0.21.190","0.21.0-dev"));
    }

    private static JSONObject release(String version,boolean prerelease) throws Exception {
        String url="https://github.com/volkotyk/helios/releases/download/v"+version+"/helios-"+version+".apk";
        return new JSONObject().put("tag_name","v"+version).put("draft",false).put("prerelease",prerelease)
                .put("assets",new JSONArray().put(new JSONObject().put("name","helios-"+version+".apk").put("browser_download_url",url).put("size",6_000_000)));
    }

    @Test public void latestReadsReleaseMetadataOnly() throws Exception {
        UpdaterTest.Host h=new UpdaterTest.Host();Updater u=new Updater(h,"0.9.0",28);
        try{u.latest();fail("offline must reach the caller, which backs off");}catch(IOException expected){}
        h.release=release("0.9.1",false);assertEquals("0.9.1",u.latest());
        h.release=release("0.9.0",false);assertNull("same version",u.latest());
        h.release=release("0.9.2",true);assertNull("a prerelease is not offered",u.latest());
        assertTrue("no status, no download, no install record: "+h.log,h.log.isEmpty());
        assertNull(h.record);
    }
}
