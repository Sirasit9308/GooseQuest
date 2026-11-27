package com.goosequest.state;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.goosequest.character.StatusEffect;

import java.util.EnumMap;
import java.util.Map;

/**
 * [คนที่ 1] ไอคอนของสถานะแต่ละแบบ (ไฟล์อยู่ใน resources/icons/status/)
 * โหลดตอนเปิดหน้าต่อสู้ และต้อง dispose ตอนออก
 */
final class StatusIcons {
    private final Map<StatusEffect, Texture> textures = new EnumMap<>(StatusEffect.class);

    StatusIcons() {
        for (StatusEffect effect : StatusEffect.values()) {
            String file = "icons/status/" + effect.name().toLowerCase() + ".png";
            // ไฟล์ต้นฉบับใหญ่ (512px) ย่อลงมาวาดเล็ก ๆ จึงใช้ mipmap กันภาพแตก
            Texture t = new Texture(Gdx.files.classpath(file), true);
            t.setFilter(Texture.TextureFilter.MipMapLinearLinear, Texture.TextureFilter.Linear);
            textures.put(effect, t);
        }
    }

    Texture get(StatusEffect effect) { return textures.get(effect); }

    void dispose() {
        textures.values().forEach(Texture::dispose);
        textures.clear();
    }
}
