package edu.dalbers.carnowcontrol;

import android.accessibilityservice.AccessibilityService;
import android.app.KeyguardManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaMetadata;
import android.media.session.MediaSession;
import android.media.session.PlaybackState;
import android.os.PowerManager;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

public class CarControlNowAccessibilityService extends AccessibilityService {
    public CarControlNowAccessibilityService() {
    }

    static final String TAG = "CarControlAccessibility";
    long lastCommand = 0;
    private static CarControlNowAccessibilityService thisService;
    private MediaSession mediaSession;
    private AudioManager audioManager;
    private long lastRewindPressTime;
    private int maxShortcutInterval = 1000;
    private PowerManager.WakeLock wl;
    public static boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = "com.RSen.Commandr/com.RSen.Commandr.core.MyAccessibilityService";

        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(), android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }
        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILIY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                TextUtils.SimpleStringSplitter splitter = mStringColonSplitter;
                splitter.setString(settingValue);
                while (splitter.hasNext()) {
                    String accessabilityService = splitter.next();

                    Log.v(TAG, "-------------- > accessabilityService :: " + accessabilityService);
                    if (accessabilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILIY IS DISABLED***");
        }

        return false;
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        thisService = this;
        lastCommand = 0;
        if (!PreferenceManager.getDefaultSharedPreferences(this).getBoolean("setup", false)) //make sure this only runs when the user explicitly enables it
        {
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean("setup", true).commit();
            Intent openMainActivity= new Intent(this, InputListenerActivity.class);
            openMainActivity.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(openMainActivity);
            //go back to setup activity after enabling
        }

        //required to send text over AVRCP
        mediaSession = new MediaSession(this, "CarControlNow");
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        setUpCallBack();
        setMediaSessionState();

        //This is deprecated but there is no other way to get screen on from a service
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "CarControlNow");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public static CarControlNowAccessibilityService getInstance() {
        return thisService;
    }

    /**
     * Recursively look for data from Google Now app
     * @param ani
     * @return
     */
    private String accessibilityNodeInfoRecursion(AccessibilityNodeInfo ani){
        if (ani==null) return null;
        if (ani.getClassName().toString().equals("com.google.android.search.searchplate.SimpleSearchText") || ani.getClassName().toString().equals("com.google.android.apps.gsa.searchplate.SimpleSearchText")&& ani.getText()!=null) {
            return ani.getText().toString();
        }
        String result = null;
        for(int i=0;i<ani.getChildCount();i++){
            result = accessibilityNodeInfoRecursion(ani.getChild(i));
            if (result!=null) {
                break;
            }
        }
        return result;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        try {
            AccessibilityNodeInfo ani = accessibilityEvent.getSource();
            String command = accessibilityNodeInfoRecursion(ani);
            if (command!=null) {
                Log.d("command", command);
                wl.release();
                sendTextOverAVRCP(command);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onInterrupt() {

    }



    public void sendTextOverAVRCP(String message) {
        PlaybackState state = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackState.STATE_PLAYING, 1, 1, SystemClock.elapsedRealtime())
                .build();

        MediaMetadata metadata = new MediaMetadata.Builder()
                .putString(MediaMetadata.METADATA_KEY_TITLE, message)
                .putString(MediaMetadata.METADATA_KEY_ARTIST, "david")
                .putString(MediaMetadata.METADATA_KEY_ALBUM_ARTIST, "albers")
                .putString(MediaMetadata.METADATA_KEY_ALBUM, "really long text really long text really long text really long text")
                .putLong(MediaMetadata.METADATA_KEY_NUM_TRACKS, 123)
                .putLong(MediaMetadata.METADATA_KEY_DURATION, 456)
                .build();

        mediaSession.setActive(true);
        mediaSession.setMetadata(metadata);
        mediaSession.setPlaybackState(state);
    }

    /**
     * Setup the media control button presses. Even if you don't listen for
     * media buttons you still need this to send over avrcp
     */
    private void setUpCallBack() {
        mediaSession.setFlags(MediaSession.FLAG_HANDLES_MEDIA_BUTTONS |
                MediaSession.FLAG_HANDLES_TRANSPORT_CONTROLS);
        mediaSession.setCallback(new MediaSession.Callback() {
            @Override
            public boolean onMediaButtonEvent(Intent mediaButtonIntent) {

                KeyEvent keyEvent = (KeyEvent) mediaButtonIntent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
                if (keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_NEXT)
                        checkShortcut();
                    else if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_MEDIA_PREVIOUS)
                        lastRewindPressTime = System.currentTimeMillis();
                    else
                        Log.d(TAG, keyEvent.getKeyCode() + "");
                }
                return super.onMediaButtonEvent(mediaButtonIntent);
            }
        });
// Oddly enough, this doesn't capture skipToNext or skipToPrevious
//        mediaSession.setCallback(new MediaSession.Callback() {
//            @Override
//            public void onPlay() {
//                super.onPlay();
//            }
//
//            @Override
//            public void onPause() {
//                super.onPause();
//            }
//
//            @Override
//            public void onSkipToNext() {
//                Log.d(TAG, "Forward");
//                super.onSkipToNext();
//            }
//
//            @Override
//            public void onSkipToPrevious() {
//                Log.d(TAG, "Back");
//                super.onSkipToPrevious();
//            }
//
//            @Override
//            public void onStop() {
//                super.onStop();
//            }
//        });
    }

    /**
     * Set this app's media state as active and as using media control buttons.
     * This must be done to send text over avrcp and to receive button presses
     */
    public void setMediaSessionState() {
        mediaSession.setActive(true);
        PlaybackState state = new PlaybackState.Builder()
                .setActions(
                        PlaybackState.ACTION_PLAY | PlaybackState.ACTION_PLAY_PAUSE |
                                PlaybackState.ACTION_PLAY_FROM_MEDIA_ID | PlaybackState.ACTION_PAUSE |
                                PlaybackState.ACTION_SKIP_TO_NEXT | PlaybackState.ACTION_SKIP_TO_PREVIOUS)
                .setState(PlaybackState.STATE_PLAYING, 1, 1, SystemClock.elapsedRealtime())
                .build();
        mediaSession.setPlaybackState(state);

    }

    /**
     * Check for rewind,forward shortcut
     */
    private void checkShortcut() {
        if( (System.currentTimeMillis() - lastRewindPressTime) < maxShortcutInterval) {

            //wakes up the device if it was asleep
            wl.acquire();
            wl.release();
            WindowManager window = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

            KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
            keyguardManager.newKeyguardLock("TAG").disableKeyguard();

            Log.d(TAG, "Got shortcut" + wl.isHeld());

            initiateGoogleNow();
        }
    }

    /**
     * Start google now activity. This will take you to the homescreen and start listening if the phone is unlocked.
     * If the phone is locked, it is hopefully awake and google now will listen from the lock screen.
     */
    private void initiateGoogleNow() {

        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setClassName("com.google.android.googlequicksearchbox",
                "com.google.android.googlequicksearchbox.VoiceSearchActivity");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException anfe) {
            Log.d("GoogleNow", "Google Voice Search is not found");
        }
    }

    /**
     * Clear media metadata on displaying AVRCP device
     */
    private void resetNotify() {
        if (mediaSession != null)
            mediaSession.setActive(false);

    }
}


