package com.example.unityplugin;

import android.view.KeyEvent;
import android.webkit.WebView;

import java.util.HashMap;

public class WebViewCustomInput {
    private WebView webview;
    private HashMap<String, Integer> keyMapString = new HashMap<>();
    private HashMap<Integer, Integer> keyMapUnity = new HashMap<>();

    public WebViewCustomInput() {
        keyMapString.put("a", KeyEvent.KEYCODE_A);
        keyMapString.put("b", KeyEvent.KEYCODE_B);
        keyMapString.put("c", KeyEvent.KEYCODE_C);
        keyMapString.put("d", KeyEvent.KEYCODE_D);
        keyMapString.put("e", KeyEvent.KEYCODE_E);
        keyMapString.put("f", KeyEvent.KEYCODE_F);
        keyMapString.put("g", KeyEvent.KEYCODE_G);
        keyMapString.put("h", KeyEvent.KEYCODE_H);
        keyMapString.put("i", KeyEvent.KEYCODE_I);
        keyMapString.put("j", KeyEvent.KEYCODE_J);
        keyMapString.put("k", KeyEvent.KEYCODE_K);
        keyMapString.put("l", KeyEvent.KEYCODE_L);
        keyMapString.put("m", KeyEvent.KEYCODE_M);
        keyMapString.put("n", KeyEvent.KEYCODE_N);
        keyMapString.put("o", KeyEvent.KEYCODE_O);
        keyMapString.put("p", KeyEvent.KEYCODE_P);
        keyMapString.put("q", KeyEvent.KEYCODE_Q);
        keyMapString.put("r", KeyEvent.KEYCODE_R);
        keyMapString.put("s", KeyEvent.KEYCODE_S);
        keyMapString.put("t", KeyEvent.KEYCODE_T);
        keyMapString.put("u", KeyEvent.KEYCODE_U);
        keyMapString.put("v", KeyEvent.KEYCODE_V);
        keyMapString.put("w", KeyEvent.KEYCODE_W);
        keyMapString.put("x", KeyEvent.KEYCODE_X);
        keyMapString.put("y", KeyEvent.KEYCODE_Y);
        keyMapString.put("z", KeyEvent.KEYCODE_Z);

        keyMapString.put("0", KeyEvent.KEYCODE_0);
        keyMapString.put("1", KeyEvent.KEYCODE_1);
        keyMapString.put("2", KeyEvent.KEYCODE_2);
        keyMapString.put("3", KeyEvent.KEYCODE_3);
        keyMapString.put("4", KeyEvent.KEYCODE_4);
        keyMapString.put("5", KeyEvent.KEYCODE_5);
        keyMapString.put("6", KeyEvent.KEYCODE_6);
        keyMapString.put("7", KeyEvent.KEYCODE_7);
        keyMapString.put("8", KeyEvent.KEYCODE_8);
        keyMapString.put("9", KeyEvent.KEYCODE_9);

        keyMapString.put("@", KeyEvent.KEYCODE_AT);
        keyMapString.put("#", KeyEvent.KEYCODE_POUND);
        keyMapString.put("-", KeyEvent.KEYCODE_MINUS);
        keyMapString.put("+", KeyEvent.KEYCODE_PLUS);
        keyMapString.put("=", KeyEvent.KEYCODE_EQUALS);
        keyMapString.put("[", KeyEvent.KEYCODE_LEFT_BRACKET);
        keyMapString.put("]", KeyEvent.KEYCODE_RIGHT_BRACKET);
        keyMapString.put(";", KeyEvent.KEYCODE_SEMICOLON);
        keyMapString.put("'", KeyEvent.KEYCODE_APOSTROPHE);
        keyMapString.put(",", KeyEvent.KEYCODE_COMMA);
        keyMapString.put(".", KeyEvent.KEYCODE_PERIOD);
        keyMapString.put("/", KeyEvent.KEYCODE_SLASH);
        keyMapString.put("\\", KeyEvent.KEYCODE_BACKSLASH);
        keyMapString.put(" ", KeyEvent.KEYCODE_SPACE);

        keyMapUnity.put(48, KeyEvent.KEYCODE_0);
        keyMapUnity.put(49, KeyEvent.KEYCODE_1);
        keyMapUnity.put(50, KeyEvent.KEYCODE_2);
        keyMapUnity.put(51, KeyEvent.KEYCODE_3);
        keyMapUnity.put(52, KeyEvent.KEYCODE_4);
        keyMapUnity.put(53, KeyEvent.KEYCODE_5);
        keyMapUnity.put(54, KeyEvent.KEYCODE_6);
        keyMapUnity.put(55, KeyEvent.KEYCODE_7);
        keyMapUnity.put(56, KeyEvent.KEYCODE_8);
        keyMapUnity.put(57, KeyEvent.KEYCODE_9);

        keyMapUnity.put(97, KeyEvent.KEYCODE_A);
        keyMapUnity.put(98, KeyEvent.KEYCODE_B);
        keyMapUnity.put(99, KeyEvent.KEYCODE_C);
        keyMapUnity.put(100, KeyEvent.KEYCODE_D);
        keyMapUnity.put(101, KeyEvent.KEYCODE_E);
        keyMapUnity.put(102, KeyEvent.KEYCODE_F);
        keyMapUnity.put(103, KeyEvent.KEYCODE_G);
        keyMapUnity.put(104, KeyEvent.KEYCODE_H);
        keyMapUnity.put(105, KeyEvent.KEYCODE_I);
        keyMapUnity.put(106, KeyEvent.KEYCODE_J);
        keyMapUnity.put(107, KeyEvent.KEYCODE_K);
        keyMapUnity.put(108, KeyEvent.KEYCODE_L);
        keyMapUnity.put(109, KeyEvent.KEYCODE_M);
        keyMapUnity.put(110, KeyEvent.KEYCODE_N);
        keyMapUnity.put(111, KeyEvent.KEYCODE_O);
        keyMapUnity.put(112, KeyEvent.KEYCODE_P);
        keyMapUnity.put(113, KeyEvent.KEYCODE_Q);
        keyMapUnity.put(114, KeyEvent.KEYCODE_R);
        keyMapUnity.put(115, KeyEvent.KEYCODE_S);
        keyMapUnity.put(116, KeyEvent.KEYCODE_T);
        keyMapUnity.put(117, KeyEvent.KEYCODE_U);
        keyMapUnity.put(118, KeyEvent.KEYCODE_V);
        keyMapUnity.put(119, KeyEvent.KEYCODE_W);
        keyMapUnity.put(120, KeyEvent.KEYCODE_X);
        keyMapUnity.put(121, KeyEvent.KEYCODE_Y);
        keyMapUnity.put(122, KeyEvent.KEYCODE_Z);

        keyMapUnity.put(64, KeyEvent.KEYCODE_AT);
        keyMapUnity.put(35, KeyEvent.KEYCODE_POUND);
        keyMapUnity.put(45, KeyEvent.KEYCODE_MINUS);
        keyMapUnity.put(43, KeyEvent.KEYCODE_PLUS);
        keyMapUnity.put(61, KeyEvent.KEYCODE_EQUALS);
        keyMapUnity.put(91, KeyEvent.KEYCODE_LEFT_BRACKET);
        keyMapUnity.put(93, KeyEvent.KEYCODE_RIGHT_BRACKET);
        keyMapUnity.put(59, KeyEvent.KEYCODE_SEMICOLON);
        keyMapUnity.put(39, KeyEvent.KEYCODE_APOSTROPHE);
        keyMapUnity.put(44, KeyEvent.KEYCODE_COMMA);
        keyMapUnity.put(46, KeyEvent.KEYCODE_PERIOD);
        keyMapUnity.put(47, KeyEvent.KEYCODE_SLASH);
        keyMapUnity.put(92, KeyEvent.KEYCODE_BACKSLASH);
        keyMapUnity.put(32, KeyEvent.KEYCODE_SPACE);

        keyMapUnity.put(282, KeyEvent.KEYCODE_F1);
        keyMapUnity.put(283, KeyEvent.KEYCODE_F2);
        keyMapUnity.put(284, KeyEvent.KEYCODE_F3);
        keyMapUnity.put(285, KeyEvent.KEYCODE_F4);
        keyMapUnity.put(286, KeyEvent.KEYCODE_F5);
        keyMapUnity.put(287, KeyEvent.KEYCODE_F6);
        keyMapUnity.put(288, KeyEvent.KEYCODE_F7);
        keyMapUnity.put(289, KeyEvent.KEYCODE_F8);
        keyMapUnity.put(290, KeyEvent.KEYCODE_F9);
        keyMapUnity.put(291, KeyEvent.KEYCODE_F10);
        keyMapUnity.put(292, KeyEvent.KEYCODE_F11);
        keyMapUnity.put(293, KeyEvent.KEYCODE_F12);

        keyMapUnity.put(9, KeyEvent.KEYCODE_TAB); // Tab key
        keyMapUnity.put(13, KeyEvent.KEYCODE_ENTER); // Enter key
        keyMapUnity.put(27, KeyEvent.KEYCODE_ESCAPE); // Escape key
        keyMapUnity.put(303, KeyEvent.KEYCODE_SHIFT_RIGHT); // Shift key
        keyMapUnity.put(304, KeyEvent.KEYCODE_SHIFT_LEFT); // Shift key
        keyMapUnity.put(305, KeyEvent.KEYCODE_CTRL_RIGHT); // Control key
        keyMapUnity.put(306, KeyEvent.KEYCODE_CTRL_LEFT); // Control key
        keyMapUnity.put(307, KeyEvent.KEYCODE_ALT_RIGHT); // Alt key
        keyMapUnity.put(308, KeyEvent.KEYCODE_ALT_LEFT); // Alt key
        keyMapUnity.put(301, KeyEvent.KEYCODE_CAPS_LOCK); // Caps Lock key
        keyMapUnity.put(280, KeyEvent.KEYCODE_PAGE_UP); // Page Up key
        keyMapUnity.put(281, KeyEvent.KEYCODE_PAGE_DOWN); // Page Down key
        keyMapUnity.put(279, KeyEvent.KEYCODE_MOVE_END); // End key
        keyMapUnity.put(278, KeyEvent.KEYCODE_HOME); // Home key
        keyMapUnity.put(276, KeyEvent.KEYCODE_DPAD_LEFT); // Left Arrow key
        keyMapUnity.put(273, KeyEvent.KEYCODE_DPAD_UP); // Up Arrow key
        keyMapUnity.put(275, KeyEvent.KEYCODE_DPAD_RIGHT); // Right Arrow key
        keyMapUnity.put(274, KeyEvent.KEYCODE_DPAD_DOWN); // Down Arrow key
        keyMapUnity.put(277, KeyEvent.KEYCODE_INSERT); // Insert key
        keyMapUnity.put(127, KeyEvent.KEYCODE_DEL);
        keyMapUnity.put(8, KeyEvent.KEYCODE_BACK);

        keyMapUnity.put(256, KeyEvent.KEYCODE_NUMPAD_0);
        keyMapUnity.put(257, KeyEvent.KEYCODE_NUMPAD_1);
        keyMapUnity.put(258, KeyEvent.KEYCODE_NUMPAD_2);
        keyMapUnity.put(259, KeyEvent.KEYCODE_NUMPAD_3);
        keyMapUnity.put(260, KeyEvent.KEYCODE_NUMPAD_4);
        keyMapUnity.put(261, KeyEvent.KEYCODE_NUMPAD_5);
        keyMapUnity.put(262, KeyEvent.KEYCODE_NUMPAD_6);
        keyMapUnity.put(263, KeyEvent.KEYCODE_NUMPAD_7);
        keyMapUnity.put(264, KeyEvent.KEYCODE_NUMPAD_8);
        keyMapUnity.put(265, KeyEvent.KEYCODE_NUMPAD_9);

        keyMapUnity.put(267, KeyEvent.KEYCODE_NUMPAD_DIVIDE);
        keyMapUnity.put(271, KeyEvent.KEYCODE_NUMPAD_ENTER);
        keyMapUnity.put(272, KeyEvent.KEYCODE_NUMPAD_EQUALS);
        keyMapUnity.put(269, KeyEvent.KEYCODE_NUMPAD_SUBTRACT);
        keyMapUnity.put(268, KeyEvent.KEYCODE_NUMPAD_MULTIPLY);
        keyMapUnity.put(266, KeyEvent.KEYCODE_NUMPAD_COMMA);
        keyMapUnity.put(170, KeyEvent.KEYCODE_NUMPAD_ADD);
        keyMapUnity.put(300, KeyEvent.KEYCODE_NUM_LOCK);

        keyMapUnity.put(19, KeyEvent.KEYCODE_BREAK);
    }

    public void receiveWebView(WebView newWebview) {
        System.out.println("[Webview] Initializing WebView plugin.");
        webview = newWebview;
    }

    private int getKeyCode(String key) {
        int keyCode;
        try {
            keyCode = keyMapString.get(key);
        } catch(NullPointerException e) {
            keyCode = -1;
        }
        return keyCode;
    }

    private int getKeyCode(int unityKeyCode) {
        int keyCode;
        try {
            keyCode = keyMapUnity.get(unityKeyCode);
        } catch(NullPointerException e) {
            keyCode = -1;
        }
        return keyCode;
    }

    private <T> void sendKeyInternal(T key, Boolean keyDown) {
        int keyCode = -1;
        if(key instanceof Integer)
            keyCode = getKeyCode((Integer)key);
        else if(key instanceof String)
            keyCode = getKeyCode((String)key);

        if(keyCode == -1)
            return;

        int finalKeyCode = keyCode;
        webview.post(new Runnable() {
           @Override
           public void run() {
               if(keyDown) {
                   KeyEvent downKeyEvent = new KeyEvent(KeyEvent.ACTION_DOWN, finalKeyCode);
                   webview.dispatchKeyEvent(downKeyEvent);
               }
               else {
                   KeyEvent upKeyEvent = new KeyEvent(KeyEvent.ACTION_UP, finalKeyCode);
                   webview.dispatchKeyEvent(upKeyEvent);
               }
           }
        });
    }

    public void keyDown(String key) {
        sendKeyInternal(key, true);
    }

    public void keyUp(String key) {
        sendKeyInternal(key, false);
    }

    public void keyDown(int key) {
        sendKeyInternal(key, true);
    }

    public void keyUp(int key) {
        sendKeyInternal(key, false);
    }
}
