package org.lyrion.squeezelite;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AndroidAutoSessionActionTest {
    @Test
    public void stopsWhenAndroidAutoAutostartIsEnabled() {
        assertEquals(AndroidAutoSessionAction.STOP,
                AndroidAutoSessionAction.forSettings(true, true));
        assertEquals(AndroidAutoSessionAction.STOP,
                AndroidAutoSessionAction.forSettings(true, false));
    }

    @Test
    public void pausesWhenOnlyPauseOnDisconnectIsEnabled() {
        assertEquals(AndroidAutoSessionAction.PAUSE,
                AndroidAutoSessionAction.forSettings(false, true));
    }

    @Test
    public void doesNothingWhenNeitherSettingIsEnabled() {
        assertEquals(AndroidAutoSessionAction.NONE,
                AndroidAutoSessionAction.forSettings(false, false));
    }
}
