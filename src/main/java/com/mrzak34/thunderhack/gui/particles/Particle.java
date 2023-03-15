package com.mrzak34.thunderhack.gui.particles;

import com.mrzak34.thunderhack.modules.client.Particles;
import net.minecraft.client.gui.ScaledResolution;

import javax.vecmath.Vector2f;
import java.awt.*;
import java.util.Random;

import static com.mrzak34.thunderhack.util.Util.mc;

public class Particle {
    public static Color[] colors = new Color[]{
            new Color(0, 233, 255),
            new Color(209, 2, 209),
            new Color(237, 0, 87),
            new Color(195, 0, 195),
            new Color(255, 1, 255),
            new Color(1, 95, 245),
            new Color(206, 2, 30),
            new Color(90, 14, 214),

    };
    private final Color color;
    private final Vector2f pos;
    private Vector2f velocity;
    private float size;
    private float alpha;

    public Particle(Vector2f velocity, float x, float y, float size, Color color) {
        this.velocity = velocity;
        this.color = color;
        this.pos = new Vector2f(x, y);
        this.size = size;
    }

    public static Particle generateParticle(float sc) {
        ScaledResolution sr = new ScaledResolution(mc);
        Vector2f velocity = new Vector2f((float) (Math.random() * 2.0f - 1.0f), (float) (Math.random() * 2.0f - 1.0f));
        float x = getRandomNumberUsingNextInt(100, sr.getScaledWidth() - 30);
        float y = getRandomNumberUsingNextInt(100, sr.getScaledHeight() - 30);
        float size = sc + getRandomNumberUsingNextInt(0, (int) (sc / 3f));
        int n = (int) Math.floor(Math.random() * colors.length);
        Color color = colors[n];
        return new Particle(velocity, x, y, size, color);
    }

    public static int getRandomNumberUsingNextInt(int min, int max) {
        Random random = new Random();
        return random.nextInt(max - min) + min;
    }

    public static double distance(float x, float y, float x1, float y1) {
        return Math.sqrt((x - x1) * (x - x1) + (y - y1) * (y - y1));
    }

    public float getAlpha() {

        return this.alpha;

    }

    public float getX() {
        return pos.getX();
    }

    public void setX(float x) {
        this.pos.setX(x);
    }

    public float getY() {
        return pos.getY();
    }

    public void setY(float y) {
        this.pos.setY(y);
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
    }

    public void tick(int delta, float speed) {

        ScaledResolution sr = new ScaledResolution(mc);

        pos.x += velocity.getX() * delta * speed;
        pos.y += velocity.getY() * delta * speed;
        if (alpha < 255.0f) this.alpha += 0.05f * delta;

        if (pos.getX() + Particles.getInstance().scale1.getValue() > sr.getScaledWidth())
            velocity = new Vector2f(-velocity.x, velocity.y);
        if (pos.getX() - Particles.getInstance().scale1.getValue() < 0)
            velocity = new Vector2f(-velocity.x, velocity.y);

        if (pos.getY() + Particles.getInstance().scale1.getValue() > sr.getScaledWidth())
            velocity = new Vector2f(velocity.x, -velocity.y);
        if (pos.getY() - Particles.getInstance().scale1.getValue() < 0)
            velocity = new Vector2f(velocity.x, -velocity.y);
    }

    public float getDistanceTo(Particle particle1) {
        return getDistanceTo(particle1.getX(), particle1.getY());
    }

    public Color getColor() {
        return this.color;
    }

    public float getDistanceTo(float x, float y) {
        return (float) distance(getX(), getY(), x, y);
    }

}