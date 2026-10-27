<h1>Squeezelite for Android</h1>

This is an Android port of [Squeezelite](https://github.com/ralph-irving/squeezelite) - a 'headless' audio player for [Lyrion Music Server (LMS)](https://github.com/LMS-Community/slimserver)

Features:
1. Uses 'AAudio' for backend
2. Optionally auto-start on Bluetooth device connect
3. Optionally use name and MAC of connected Bluetooth device as player name and ID for Lyrion
4. Support for Bluetooth media controls
5. Confgurable bit rate limiting

## Squeezelite (ar): fork for lms-material-app (ar)

This fork is the phone player for [lms-material-app (ar)](https://github.com/arigit/lms-material-app), an Android Auto version of the LMS Material app optimized for in-car playback of a remote music collection. It is a separate app ("Squeezelite (ar)", `org.lyrion.squeezelite.ar`), so it installs alongside the original, and adds fixes for use in the car (the player can always be started again after it stops; audio focus is given up to another app started after the first seconds of an Android Auto session).

Download the latest `Squeezelite-ar-*.apk` from [Releases](https://github.com/arigit/squeezelite/releases). Setup is described in the [lms-material-app (ar) README](https://github.com/arigit/lms-material-app#install); Android Auto testing in [docs/android-auto.md](docs/android-auto.md).

It builds on the upstream sources: [CDrummond/squeezelite](https://github.com/CDrummond/squeezelite) (the Android port of [ralph-irving/squeezelite](https://github.com/ralph-irving/squeezelite)) and [chrober/squeezelite](https://github.com/chrober/squeezelite/tree/build/android-auto-apk), which added Android Auto support.

This fork was developed with heavy use of AI (Claude Code): the changes were designed, written and tested with an AI assistant, and checked in the car and with Android Auto's Desktop Head Unit. The car version gets almost daily testing.

Once this fork's code has proven fully stable, the plan is to propose merging it upstream.

<br>

[<img src="https://shields.rbtlog.dev/simple/org.lyrion.squeezelite" alt="RB Status">](https://shields.rbtlog.dev/org.lyrion.squeezelite)

[<img src="https://f-droid.org/badge/get-it-on.png"
    alt="Get it on F-Droid"
    height="80">](https://f-droid.org/packages/org.lyrion.squeezelite)
[<img src="https://gitlab.com/IzzyOnDroid/repo/-/raw/master/assets/IzzyOnDroid.png" height="80" alt="Get it at IzzyOnDroid">](https://apt.izzysoft.de/packages/org.lyrion.squeezelite)

<br>
Original README of Squeezelite:
<br>
<hr/>
Squeezelite v2.0.x, Copyright 2012-2015 Adrian Smith, 2015-2026 Ralph Irving.<br>
<br>
See the squeezelite manpage for usage details.<br>
https://ralph-irving.github.io/squeezelite.html<br>
<br>
This program is free software: you can redistribute it and/or modify<br>
it under the terms of the GNU General Public License as published by<br>
the Free Software Foundation, either version 3 of the License, or<br>
(at your option) any later version.<br>
<br>
This program is distributed in the hope that it will be useful,<br>
but WITHOUT ANY WARRANTY; without even the implied warranty of<br>
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the<br>
GNU General Public License for more details.<br>
<br>
You should have received a copy of the GNU General Public License<br>
along with this program.  If not, see <http://www.gnu.org/licenses/>.<br>
<br>
Additional permission under GNU GPL version 3 section 7<br>
<br>
If you modify this program, or any covered work, by linking or
combining it with OpenSSL (or a modified version of that library),
containing parts covered by the terms of The OpenSSL Project, the
licensors of this program grant you additional permission to convey
the resulting work. {Corresponding source for a non-source form of
such a combination shall include the source code for the parts of
OpenSSL used as well as that of the covered work.}<br>
<br>
Contains dsd2pcm library Copyright 2009, 2011 Sebastian Gesemann which<br>
is subject to its own license.<br>
<br>
Contains the Daphile Project full dsd patch Copyright 2013-2017 Daphile,<br>
which is subject to its own license.<br>
<br>
Option to allow server side upsampling for PCM streams (-W) from<br>
squeezelite-R2 (c) Marco Curti 2015, marcoc1712@gmail.com.<br>
<br>
This software uses libraries from the FFmpeg project under<br>
the LGPLv2.1 and its source can be downloaded from<br>
https://sourceforge.net/projects/lmsclients/files/source/<br>
