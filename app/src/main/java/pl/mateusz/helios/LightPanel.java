package pl.mateusz.helios;

import android.app.Activity;
import android.app.Dialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.view.Window;
import android.widget.*;
import org.json.JSONObject;

/**
 * Fork feature: the light panel behind a long press on a light tile. On/off, brightness, colour temperature and colour,
 * each shown only when LightControls says the light supports it. Views only: every call goes through Host.send, and a
 * slider sends once, when the finger lifts. A slider under the finger is not moved by a state refresh.
 */
final class LightPanel {
    interface Host {
        EntityStates.Entity state();
        boolean live();
        /** Sends light.<service> with this data (may be null); null when it went out, else why it did not (nothing was sent). */
        String send(String service,JSONObject data);
    }
    final Dialog dialog;
    private final Host host;
    private final Switch power;
    private final TextView brightnessLabel,temperatureLabel,colorLabel;
    private final SeekBar brightness,temperature,hue;
    private final LinearLayout brightnessRow,temperatureRow,colorRow;
    private boolean applying;private SeekBar dragging;
    private LightControls controls;

    LightPanel(Activity a,String label,Host host){
        this.host=host;
        dialog=new Dialog(a);dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LinearLayout column=Theme.dialogColumn(a,20);column.setMinimumWidth(Theme.dp(a,440));
        column.addView(Theme.label(a,label,20,false));
        power=new Switch(a);power.setText(I18n.t("Włączone"));power.setTextSize(18);power.setTextColor(Theme.current().text);
        power.setThumbTintList(ColorStateList.valueOf(Theme.current().accent));
        LinearLayout.LayoutParams pp=new LinearLayout.LayoutParams(-1,Theme.dp(a,64));column.addView(power,pp);
        power.setOnCheckedChangeListener((b,on)->{if(!applying)send(on?"turn_on":"turn_off",null);});
        brightnessLabel=Theme.label(a,"",16,true);brightness=slider(a,99);brightnessRow=row(a,column,brightnessLabel,brightness);
        temperatureLabel=Theme.label(a,"",16,true);temperature=slider(a,1);temperatureRow=row(a,column,temperatureLabel,temperature);
        colorLabel=Theme.label(a,I18n.t("Kolor"),16,true);hue=slider(a,359);colorRow=row(a,column,colorLabel,hue);
        GradientDrawable rainbow=new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT,new int[]{0xFFFF0000,0xFFFFFF00,0xFF00FF00,0xFF00FFFF,0xFF0000FF,0xFFFF00FF,0xFFFF0000});
        rainbow.setCornerRadius(Theme.dp(a,6));hue.setProgressDrawable(rainbow);hue.setThumbTintList(ColorStateList.valueOf(Theme.current().text));
        Button close=Theme.button(a,I18n.t("Zamknij"),false,Theme.dp(a,17),Theme.dp(a,Theme.RADIUS));close.setOnClickListener(v->dialog.cancel());
        LinearLayout.LayoutParams c=new LinearLayout.LayoutParams(-1,Theme.dp(a,64));c.topMargin=Theme.dp(a,12);column.addView(close,c);
        dialog.setContentView(column);dialog.setCanceledOnTouchOutside(true);
        if(dialog.getWindow()!=null)dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        refresh();
    }
    void show(){dialog.show();}

    /** Re-reads the light; controls work only while HA is live and the light is known. */
    void refresh(){
        EntityStates.Entity e=host.state();
        controls=LightControls.of(e);
        boolean usable=host.live()&&e!=null&&e.known(),on=e!=null&&"on".equals(e.state);
        applying=true;
        try{
            power.setChecked(on);power.setEnabled(usable);
            brightnessRow.setVisibility(controls.brightness?LinearLayout.VISIBLE:LinearLayout.GONE);
            temperatureRow.setVisibility(controls.temperature?LinearLayout.VISIBLE:LinearLayout.GONE);
            colorRow.setVisibility(controls.color?LinearLayout.VISIBLE:LinearLayout.GONE);
            temperature.setMax(Math.max(1,controls.maxKelvin-controls.minKelvin));
            if(dragging!=brightness&&controls.brightnessPct>0)brightness.setProgress(controls.brightnessPct-1);
            if(dragging!=temperature&&controls.kelvin>0)temperature.setProgress(controls.kelvin-controls.minKelvin);
            if(dragging!=hue&&controls.hue>=0)hue.setProgress(controls.hue);
            brightness.setEnabled(usable);temperature.setEnabled(usable);hue.setEnabled(usable);
            if(dragging!=brightness)brightnessLabel.setText(I18n.t("Jasność: ")+(controls.brightnessPct>0?controls.brightnessPct+"%":"—"));
            if(dragging!=temperature)temperatureLabel.setText(I18n.t("Temperatura bieli: ")+(controls.kelvin>0?controls.kelvin+" K":"—"));
        }finally{applying=false;}
    }

    private SeekBar slider(Activity a,int max){SeekBar s=new SeekBar(a);s.setMax(max);Theme.tint(s);s.setOnSeekBarChangeListener(new Listener());return s;}
    private static LinearLayout row(Activity a,LinearLayout column,TextView label,SeekBar bar){
        LinearLayout r=new LinearLayout(a);r.setOrientation(LinearLayout.VERTICAL);r.setPadding(0,Theme.dp(a,8),0,0);
        r.addView(label);r.addView(bar,new LinearLayout.LayoutParams(-1,Theme.dp(a,64)));column.addView(r);return r;
    }
    private void send(String service,JSONObject data){
        String why=host.send(service,data);
        if(why!=null){Toast.makeText(dialog.getContext(),why,Toast.LENGTH_SHORT).show();refresh();}
    }
    private final class Listener implements SeekBar.OnSeekBarChangeListener {
        public void onProgressChanged(SeekBar s,int p,boolean user){
            if(!user||controls==null)return;
            if(s==brightness)brightnessLabel.setText(I18n.t("Jasność: ")+(p+1)+"%");
            else if(s==temperature)temperatureLabel.setText(I18n.t("Temperatura bieli: ")+(controls.minKelvin+p)+" K");
        }
        public void onStartTrackingTouch(SeekBar s){dragging=s;}
        public void onStopTrackingTouch(SeekBar s){
            dragging=null;
            if(applying||controls==null)return;
            if(s==brightness)send("turn_on",LightControls.brightnessData(s.getProgress()+1));
            else if(s==temperature)send("turn_on",controls.temperatureData(controls.minKelvin+s.getProgress()));
            else if(s==hue)send("turn_on",LightControls.hueData(s.getProgress()));
        }
    }
}
