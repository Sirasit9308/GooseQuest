package com.goosequest.util;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/** รวมปุ่มที่ใช้บ่อย จะได้เปลี่ยนปุ่มได้ที่เดียว */
public final class Keys {
    private Keys() {}

    private static boolean just(int key) { return Gdx.input.isKeyJustPressed(key); }
    private static boolean held(int key) { return Gdx.input.isKeyPressed(key); }

    public static boolean confirm() { return just(Input.Keys.ENTER) || just(Input.Keys.SPACE) || just(Input.Keys.Z); }
    public static boolean back()    { return just(Input.Keys.ESCAPE) || just(Input.Keys.X); }
    public static boolean menu()    { return just(Input.Keys.I) || just(Input.Keys.ESCAPE); }
    public static boolean interact(){ return just(Input.Keys.F); }
    public static boolean upJust() { return just(Input.Keys.UP) || just(Input.Keys.W); }
    public static boolean downJust(){ return just(Input.Keys.DOWN) || just(Input.Keys.S); }

    public static boolean upHeld()    { return held(Input.Keys.UP) || held(Input.Keys.W); }
    public static boolean downHeld()  { return held(Input.Keys.DOWN) || held(Input.Keys.S); }
    public static boolean leftHeld()  { return held(Input.Keys.LEFT) || held(Input.Keys.A); }
    public static boolean rightHeld() { return held(Input.Keys.RIGHT) || held(Input.Keys.D); }
}
