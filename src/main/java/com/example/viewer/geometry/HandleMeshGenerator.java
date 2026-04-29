package com.example.viewer.geometry;

import com.example.viewer.model.VaseParameters;
import com.example.viewer.utils.MathUtils;
import javafx.scene.shape.TriangleMesh;

public class HandleMeshGenerator {
    
    public static TriangleMesh createHandleTriangleMesh(VaseParameters params) {
        int segments = 32;
        int thicknessSegments = 8;
        
        float handleSize = (float) params.handleSize.get();
        float handleThickness = (float) params.handleThickness.get();
        float posX = (float) params.handlePos.get();
        float totalHeight = (float) params.height.get();
        
        // Find the actual vase edge point where handle should attach
        float yStart = -totalHeight / 2f;
        float attachRadius = 0;
        
        // Find the point where vase has maximum radius (the "edge")
        float maxRadius = 0;
        float maxRadiusY = yStart;
        
        for (int i = 0; i <= 100; i++) {
            float t = (float) i / 100;
            float y = yStart + t * totalHeight;
            float radius = MathUtils.vaseProfileRadius(t, params);
            
            if (radius > maxRadius) {
                maxRadius = radius;
                maxRadiusY = y;
            }
        }
        
        attachRadius = maxRadius;

        // The handle is rotated around Z_AXIS by -90 degrees in start().
        // After that rotation, the handle's local Y coordinate becomes the radial distance from the vase's Y axis.
        float arcX0 = attachRadius + handleSize - posX;      // angle=0 -> cos=1
        float arcX1 = attachRadius - handleSize - posX;      // angle=pi -> cos=-1

        // Rotation: newY = -oldX (Rotate(-90, Z_AXIS))
        float vaseY0 = -arcX0;
        float vaseY1 = -arcX1;

        float t0 = (vaseY0 - yStart) / totalHeight;
        float t1 = (vaseY1 - yStart) / totalHeight;
        t0 = Math.max(0f, Math.min(1f, t0));
        t1 = Math.max(0f, Math.min(1f, t1));

        float r0 = MathUtils.vaseProfileRadius(t0, params);
        float r1 = MathUtils.vaseProfileRadius(t1, params);

        // Both arc endpoints share the same local arcY term
        float d0 = Math.abs((t0 * totalHeight + yStart) - maxRadiusY);
        float d1 = Math.abs((t1 * totalHeight + yStart) - maxRadiusY);
        boolean baseIs0 = d0 <= d1;

        float attachYBase = baseIs0 ? r0 : r1;
        float deltaY = baseIs0 ? (r1 - r0) : (r0 - r1);
        
        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);
        
        // Create simple arc handle points
        for (int i = 0; i <= segments; i++) {
            float arcT = (float) i / segments;
            float angle = (float) Math.PI * arcT; // Half circle arc
            
            float arcX = attachRadius + handleSize * (float) Math.cos(angle) - posX;

            float w = 0.5f * (1f - (float) Math.cos(angle)); // 0 at angle=0, 1 at angle=pi
            float wForOtherHalf = baseIs0 ? w : (1f - w);
            float arcY = attachYBase + deltaY * wForOtherHalf + handleSize * (float) Math.sin(angle);
            float arcZ = 0; // Z position fixed at 0
            
            // Create circular cross-section at each arc point
            for (int j = 0; j <= thicknessSegments; j++) {
                float thicknessAngle = 2.0f * (float) Math.PI * j / thicknessSegments;
                
                // Thickness in X-Z plane (perpendicular to arc direction)
                float thicknessX = handleThickness * 0.5f * (float) Math.cos(thicknessAngle);
                float thicknessZ = handleThickness * 0.5f * (float) Math.sin(thicknessAngle);
                
                // Keep cross-section thickness centered on the arc centerline
                float x = arcX + thicknessX;
                float y = arcY;
                float z = arcZ + thicknessZ;
                
                mesh.getPoints().addAll(x, y, z);
            }
        }
        
        // Create faces for the handle
        int ringSize = thicknessSegments + 1;
        for (int i = 0; i < segments; i++) {
            for (int j = 0; j < thicknessSegments; j++) {
                int p0 = i * ringSize + j;
                int p1 = p0 + 1;
                int p2 = p0 + ringSize;
                int p3 = p2 + 1;
                
                mesh.getFaces().addAll(p0, 0, p2, 0, p1, 0);
                mesh.getFaces().addAll(p1, 0, p2, 0, p3, 0);
            }
        }
        
        return mesh;
    }
}
