package com.example.viewer.geometry;

import com.example.viewer.model.VaseParameters;
import com.example.viewer.utils.MathUtils;
import javafx.scene.shape.TriangleMesh;

public class SpoutMeshGenerator {
    
    public static TriangleMesh createSpoutTriangleMesh(VaseParameters params) {
        int radialSegments = params.radialSegments.get();
        int verticalSegments = 64;
        float totalHeight = (float) params.height.get();
        float yStart = -totalHeight / 2f;

        float[] baseOuterRadius = new float[verticalSegments + 1];
        float[] yValues = new float[verticalSegments + 1];

        for (int i = 0; i <= verticalSegments; i++) {
            float t = (float) i / verticalSegments;
            yValues[i] = yStart + t * totalHeight;
            baseOuterRadius[i] = MathUtils.vaseProfileRadius(t, params);
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);

        int ringSize = radialSegments + 1;

        // Create spout geometry with smooth blending
        for (int yi = 0; yi <= verticalSegments; yi++) {
            float t = (float) yi / verticalSegments;
            float y = yValues[yi];
            float baseRadius = baseOuterRadius[yi];
            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                float spoutWeight = MathUtils.computeSpoutWeight(t, angle, params);
                
                // Always add points, but use spout weight for smooth blending
                float radius = baseRadius + (float) params.spoutLength.get() * spoutWeight;
                float liftedY = y + (float) params.spoutLift.get() * spoutWeight;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, liftedY, z);
            }
        }

        // Create faces for spout surface
        for (int yi = 0; yi < verticalSegments; yi++) {
            for (int ri = 0; ri < radialSegments; ri++) {
                int p0 = yi * ringSize + ri;
                int p1 = p0 + 1;
                int p2 = p0 + ringSize;
                int p3 = p2 + 1;
                
                mesh.getFaces().addAll(p0, 0, p2, 0, p1, 0);
                mesh.getFaces().addAll(p1, 0, p2, 0, p3, 0);
            }
        }

        // Close the bottom opening so the spout has a base.
        float bottomCenterY = yValues[0];
        int bottomCenterIndex = mesh.getPoints().size() / 3;
        mesh.getPoints().addAll(0f, bottomCenterY, 0f);
        for (int ri = 0; ri < radialSegments; ri++) {
            int b0 = ri;
            int b1 = ri + 1;
            mesh.getFaces().addAll(bottomCenterIndex, 0, b1, 0, b0, 0);
        }

        return mesh;
    }
}
