package pl.mateusz.helios;

import android.content.SharedPreferences;
import android.os.Handler;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.function.Consumer;

/**
 * Fork feature: runs the automatic update check in the service. UpdateCheck decides, Updater.latest reads the release
 * metadata on the network executor, and this class only keeps the timer and the "helios" prefs. Main thread only.
 */
final class UpdateWatch {
    static final String OFF="update_check_off",AT="update_checked_at",FAILURES="update_check_failures",FOUND="update_found";
    private final SharedPreferences prefs;private final Handler main;private final Executor network;private final Updater updater;
    private final Runnable tick=this::check;
    private Consumer<String> listener;private boolean stopped;
    UpdateWatch(SharedPreferences prefs,Handler main,Executor network,Updater updater){this.prefs=prefs;this.main=main;this.network=network;this.updater=updater;}

    boolean enabled(){return !prefs.getBoolean(OFF,false);}
    /** The newer version to offer, or null. */
    String available(){return UpdateCheck.notice(enabled(),prefs.getString(FOUND,null),BuildConfig.VERSION_NAME);}
    /** Gets the current offer at once, then after every good check that still finds one, and when the checks are turned off. */
    void setListener(Consumer<String> listener){this.listener=listener;if(listener!=null)listener.accept(available());}
    void setEnabled(boolean on){prefs.edit().putBoolean(OFF,!on).apply();schedule();if(listener!=null)listener.accept(available());}
    void start(){schedule();}
    void stop(){stopped=true;main.removeCallbacks(tick);listener=null;}

    private void schedule(){
        main.removeCallbacks(tick);if(stopped)return;
        long wait=UpdateCheck.delay(enabled(),System.currentTimeMillis(),prefs.getLong(AT,0),prefs.getInt(FAILURES,0));
        if(wait>=0)main.postDelayed(tick,wait);
    }
    private void check(){
        if(stopped||!enabled())return;
        // stored before the request: a process that dies during it still counts that request against the API limit
        prefs.edit().putLong(AT,System.currentTimeMillis()).apply();
        try{
            network.execute(()->{
                String found=null;boolean ok=false;
                try{found=updater.latest();ok=true;}catch(Exception ignored){} // offline, timeout, bad JSON, bad version: all back off
                final String result=found;final boolean good=ok;
                main.post(()->done(good,result));
            });
        }catch(RejectedExecutionException e){/* the service is going away */}
    }
    private void done(boolean ok,String found){
        if(stopped)return;
        SharedPreferences.Editor edit=prefs.edit();
        if(!ok)edit.putInt(FAILURES,Math.min(prefs.getInt(FAILURES,0)+1,100));
        else{
            edit.putInt(FAILURES,0);
            // null also means a refused request (AndroidHost.release returns null for any non-200, a rate limit too):
            // keep an earlier find; installing it, or a newer find, replaces it
            if(found!=null)edit.putString(FOUND,found);
        }
        edit.apply();
        if(ok&&listener!=null)listener.accept(available()); // repeats a standing offer too: a collapsed bar shows it again
        schedule();
    }
}
