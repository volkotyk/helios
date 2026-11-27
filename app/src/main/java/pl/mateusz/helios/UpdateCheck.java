package pl.mateusz.helios;

/**
 * Fork feature: when the clock asks GitHub for a newer Helios by itself, and which version it offers. The check reads
 * release metadata only (Updater.latest); the download and the install wait for a tap (Updater.run). One address gets
 * 60 unauthenticated GitHub API requests per hour, so the interval is long and every failure doubles the wait.
 */
final class UpdateCheck {
    /** The first check after the service starts: a clock that just booted needs a moment for its Wi-Fi. Also the floor of every wait. */
    static final long FIRST_DELAY_MS=2*60_000L;
    static final long INTERVAL_MS=6*3_600_000L;
    /** The wait after the first failure; it doubles with each further failure, up to INTERVAL_MS. */
    static final long RETRY_MS=15*60_000L;
    private UpdateCheck(){}
    /**
     * Milliseconds from now until the next check, or -1 when the checks are off. lastAttempt is the wall-clock time of
     * the last request (0 = never) and failures the count of failed requests since the last good one.
     */
    static long delay(boolean enabled,long now,long lastAttempt,int failures){
        if(!enabled)return -1;
        if(lastAttempt<=0||lastAttempt>now)return FIRST_DELAY_MS; // never asked, or the wall clock went back
        return Math.max(FIRST_DELAY_MS,lastAttempt+(failures>0?retry(failures):INTERVAL_MS)-now);
    }
    static long retry(int failures){
        long wait=RETRY_MS;
        for(int i=1;i<failures&&wait<INTERVAL_MS;i++)wait*=2;
        return Math.min(wait,INTERVAL_MS);
    }
    /** The version to offer, or null: the stored find only while it is newer than the installed version, so an installed update clears it. */
    static String notice(boolean enabled,String found,String installed){
        if(!enabled||found==null||installed==null)return null;
        try{return "newer".equals(Updater.decision(found,installed))?found:null;}
        catch(IllegalArgumentException e){return null;} // a version that is not dotted digits: offer nothing rather than crash the UI thread
    }
}
