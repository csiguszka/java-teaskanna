package com.example.viewer.utils;

public class MathUtils {
    
    public static float smoothStep(float edge0, float edge1, float x) {
        float t = (x - edge0) / (edge1 - edge0);
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }
    
    public static float findMaxVaseRadius(com.example.viewer.model.VaseParameters params) {
        float maxRadius = 0f;
        for (int i = 0; i <= 120; i++) {
            float t = (float) i / 120f;
            maxRadius = Math.max(maxRadius, vaseProfileRadius(t, params));
        }
        return maxRadius;
    }
    
    public static float vaseProfileRadius(float t, com.example.viewer.model.VaseParameters params) {
        return (float) params.baseRadius.get()
                + (float) params.bellyAmount.get() * (float) Math.sin(Math.PI * t)
                + 9f * (float) Math.sin(2.3 * Math.PI * t + 0.4)
                - (float) params.neckTaper.get() * (float) Math.pow(t, 1.4);
    }
    
    public static float computeSpoutWeight(float t, double angle, com.example.viewer.model.VaseParameters params) {
        float topMask = smoothStep(0.68f, 1.0f, t);
        float widthRad = (float) Math.toRadians(params.spoutWidth.get() * 0.5);
        widthRad = Math.max(0.12f, widthRad);
        float centeredAngle = (float) Math.atan2(Math.sin(angle - Math.PI), Math.cos(angle - Math.PI));
        float angularMask = (float) Math.exp(-Math.pow(centeredAngle / widthRad, 2.0));
        return topMask * angularMask;
    }
}
