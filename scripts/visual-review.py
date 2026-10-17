"""Exercise real Android screens and save screenshots + UI trees for review."""
from pathlib import Path
import re
import subprocess as sp
import time
import xml.etree.ElementTree as ET

out = Path('review')
out.mkdir(exist_ok=True)
package = 'io.github.catoware.ember.debug'

def adb(*args):
    return sp.check_output(['adb', *args])

def tree():
    adb('shell', 'uiautomator', 'dump', '/sdcard/window.xml')
    return ET.fromstring(adb('shell', 'cat', '/sdcard/window.xml'))

def find(name, attempts=8):
    for _ in range(attempts):
        for node in tree().iter('node'):
            if node.get('resource-id', '').endswith(':id/' + name) or node.get('text', '').casefold() == name.casefold():
                return node
        time.sleep(1)
    raise AssertionError('Missing UI control: ' + name)

def tap(name):
    node = find(name)
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))
    assert x2 > x1 and y2 > y1, 'Empty touch target: ' + name
    adb('shell', 'input', 'tap', str((x1+x2)//2), str((y1+y2)//2))
    time.sleep(1)

def screenshot(name):
    (out / (name + '.png')).write_bytes(adb('exec-out', 'screencap', '-p'))
    (out / (name + '.xml')).write_bytes(ET.tostring(tree()))

def back():
    adb('shell', 'input', 'keyevent', '4')
    time.sleep(1)

try:
    adb('install', '-r', 'apk/Ember-Launcher.apk')
    for permission in ['READ_EXTERNAL_STORAGE', 'WRITE_EXTERNAL_STORAGE']:
        adb('shell', 'pm', 'grant', package, 'android.permission.' + permission)
    adb('shell', 'settings', 'put', 'system', 'accelerometer_rotation', '0')
    adb('shell', 'settings', 'put', 'system', 'user_rotation', '0')
    adb('shell', 'am', 'start', '-n', package + '/net.kdt.pojavlaunch.TestStorageActivity')
    find('play_button', 30)
    time.sleep(5)
    find('ember_instance_list')
    screenshot('01-home-portrait')
    tap('account_spinner')
    screenshot('02-identity')
    tap('button_local_authentication')
    screenshot('03-offline-profile')
    tap('login_edit_email')
    adb('shell', 'input', 'text', 'EmberTester')
    back()
    tap('login_button')
    find('play_button')
    tap('setting_button')
    screenshot('04-settings')
    for node in tree().iter('node'):
        if 'Java' in node.get('text', '') and node.get('resource-id', '').endswith('/title'):
            tap(node.get('text'))
            screenshot('05-java-settings')
            tap('Auto Configure RAM')
            assert find('seekbar').get('enabled') == 'false', 'Manual RAM must disable in auto mode'
            screenshot('05-auto-ram-enabled')
            tap('Auto Configure RAM')
            assert find('seekbar').get('enabled') == 'true', 'Manual RAM must restore'
            back()
            break
    tap('Video and renderer')
    screenshot('05-video-fps-off')
    tap('FPS counter')
    tap('Detailed performance overlay')
    screenshot('05-video-fps-on')
    assert sum(n.get('checked') == 'true' for n in tree().iter('node')) >= 2, 'FPS switches must enable'
    back()
    tap('Video and renderer')
    assert sum(n.get('checked') == 'true' for n in tree().iter('node')) >= 2, 'FPS settings must persist'
    back()
    tap('setting_button')
    # Tools are reachable by scrolling on smaller screens.
    adb('shell', 'input', 'swipe', '500', '1500', '500', '500', '400')
    tap('discord_button')
    time.sleep(3)
    screenshot('05-device-report')
    tap('Run benchmark')
    time.sleep(4)
    assert any('Benchmark complete' in n.get('text', '') for n in tree().iter('node')), 'Benchmark did not complete'
    screenshot('05-benchmark')
    back()
    adb('shell', 'input', 'swipe', '500', '500', '500', '1500', '400')
    tap('mc_version_spinner')
    screenshot('06-instance-picker')
    back()
    tap('ember_add_instance')
    screenshot('06-instance-library')
    tap('vanilla_profile')
    screenshot('07-instance-editor')
    back()
    back()
    tap('custom_control_button')
    find('editor_add')
    screenshot('08-controls-editor')
    tap('editor_add')
    tap('Add button')
    node = find('New')
    x1, y1, x2, y2 = map(int, re.findall(r'\d+', node.get('bounds')))
    x, y = str((x1+x2)//2), str((y1+y2)//2)
    adb('shell', 'input', 'swipe', x, y, x, y, '900')
    time.sleep(2)
    find('editName_editText')
    screenshot('08-control-properties')
    tap('editor_preview')
    find('editor_show_tools')
    screenshot('08-control-preview')
    tap('editor_show_tools')
    tap('editor_save')
    screenshot('08-controls-save')
    tap('button1')
    time.sleep(2)
    tap('editor_back')
    tap('Yes')
    find('play_button')
    adb('shell', 'settings', 'put', 'system', 'user_rotation', '1')
    time.sleep(3)
    find('play_button')
    screenshot('08-home-landscape')
    adb('shell', 'settings', 'put', 'system', 'user_rotation', '0')
    adb('shell', 'wm', 'size', '840x1680')
    time.sleep(3)
    find('play_button')
    screenshot('09-home-small-phone')
    log = adb('logcat', '-d', '-s', 'AndroidRuntime:E').decode(errors='replace')
    assert 'FATAL EXCEPTION' not in log, log
    print('UI smoke checks passed; screenshots saved in review/')
finally:
    (out / 'logcat.txt').write_bytes(adb('logcat', '-d'))
