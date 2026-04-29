package com.example.viewer.geometry;

import com.example.viewer.model.VaseParameters;
import com.example.viewer.utils.MathUtils;
import javafx.scene.shape.TriangleMesh;

public class LidDomeMeshGenerator {
    
    public static TriangleMesh createLidDomeTriangleMesh(VaseParameters params) {
        int radialSegments = Math.max(24, params.radialSegments.get());
        int domeRings = 18;
        int ringSize = radialSegments + 1;

        float totalHeight = (float) params.height.get();
        float yStart = -totalHeight / 2f;
        float topY = yStart + totalHeight;

        float topRadius = MathUtils.vaseProfileRadius(1f, params);
        float lidBaseRadius = topRadius;
        float lidHeight = (float) params.lidHeight.get();
        float lidBottomY = topY + 0.6f;

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);

        // Dome rings: gently stepped profile, richer than a plain truncated hemisphere
        for (int yi = 0; yi <= domeRings; yi++) {
            float t = (float) yi / domeRings;
            float y = lidBottomY + t * lidHeight;
            float profile = (float) Math.pow(Math.max(0f, 1f - t * t), 0.62);
            float ridge = 1.0f + 0.08f * (float) Math.sin(4.0 * Math.PI * t) * (1f - t);
            float radius = Math.max(lidBaseRadius * 0.22f, lidBaseRadius * profile * ridge);

            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, y, z);
            }
        }

        for (int yi = 0; yi < domeRings; yi++) {
            for (int ri = 0; ri < radialSegments; ri++) {
                int p0 = yi * ringSize + ri;
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
