package com.example.viewer.geometry;

import com.example.viewer.model.VaseParameters;
import com.example.viewer.utils.MathUtils;
import javafx.scene.shape.TriangleMesh;

public class LidKnobMeshGenerator {
    
    public static TriangleMesh createLidKnobTriangleMesh(VaseParameters params) {
        int radialSegments = Math.max(24, params.radialSegments.get());
        int domeRings = 18;
        int ringSize = radialSegments + 1;

        float totalHeight = (float) params.height.get();
        float yStart = -totalHeight / 2f;
        float topY = yStart + totalHeight;

        float topRadius = MathUtils.vaseProfileRadius(1f, params);
        float lidBaseRadius = topRadius;
        float lidHeight = (float) params.lidHeight.get();
        float knobHeight = (float) params.knobHeight.get();
        float knobRadius = (float) params.knobRadius.get();
        float lidBottomY = topY + 0.6f;

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);

        int domeTopStart = domeRings * ringSize;
        float knobBottomY = lidBottomY + lidHeight - 1.2f;
        float knobTopY = knobBottomY + knobHeight;
        float knobTopRadius = knobRadius * 0.78f;
        int knobStart = mesh.getPoints().size() / 3;

        // Truncated hexagonal knob
        for (int ring = 0; ring <= 1; ring++) {
            float y = ring == 0 ? knobBottomY : knobTopY;
            float r = ring == 0 ? knobRadius : knobTopRadius;
            for (int i = 0; i <= 6; i++) {
                double angle = 2.0 * Math.PI * i / 6.0 + Math.PI / 6.0;
                float x = (float) (r * Math.cos(angle));
                float z = (float) (r * Math.sin(angle));
                mesh.getPoints().addAll(x, y, z);
            }
        }

        int hexRingSize = 7;
        for (int i = 0; i < 6; i++) {
            int b0 = knobStart + i;
            int b1 = knobStart + i + 1;
            int t0 = knobStart + hexRingSize + i;
            int t1 = knobStart + hexRingSize + i + 1;
            mesh.getFaces().addAll(b0, 0, t0, 0, b1, 0);
            mesh.getFaces().addAll(b1, 0, t0, 0, t1, 0);
        }

        int topCenter = mesh.getPoints().size() / 3;
        mesh.getPoints().addAll(0f, knobTopY, 0f);
        for (int i = 0; i < 6; i++) {
            int t0 = knobStart + hexRingSize + i;
            int t1 = knobStart + hexRingSize + i + 1;
            mesh.getFaces().addAll(topCenter, 0, t0, 0, t1, 0);
        }

        return mesh;
    }
}
