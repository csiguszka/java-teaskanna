package com.example.viewer.geometry;

import com.example.viewer.model.VaseParameters;
import com.example.viewer.utils.MathUtils;
import javafx.scene.shape.TriangleMesh;

public class VaseMeshGenerator {
    
    public static TriangleMesh createVaseTriangleMesh(VaseParameters params) {
        int radialSegments = params.radialSegments.get();
        int verticalSegments = 64;
        float totalHeight = (float) params.height.get();
        float wallThickness = (float) params.wallThickness.get();
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

        // Kulso fal pontok
        for (int yi = 0; yi <= verticalSegments; yi++) {
            float t = (float) yi / verticalSegments;
            float y = yValues[yi];
            float baseRadius = baseOuterRadius[yi];
            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                float radius = baseRadius;
                float liftedY = y;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, liftedY, z);
            }
        }

        int innerOffset = mesh.getPoints().size() / 3;

        // Belso fal pontok
        for (int yi = 0; yi <= verticalSegments; yi++) {
            float t = (float) yi / verticalSegments;
            float y = yValues[yi];
            float baseRadius = baseOuterRadius[yi];
            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                float radius = Math.max(10f, baseRadius - wallThickness);
                float liftedY = y;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, liftedY, z);
            }
        }

        // Kulso oldalfeluletek
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

        // Belso oldalfeluletek (forditott irany)
        for (int yi = 0; yi < verticalSegments; yi++) {
            for (int ri = 0; ri < radialSegments; ri++) {
                int p0 = innerOffset + yi * ringSize + ri;
                int p1 = p0 + 1;
                int p2 = p0 + ringSize;
                int p3 = p2 + 1;
                mesh.getFaces().addAll(p0, 0, p1, 0, p2, 0);
                mesh.getFaces().addAll(p1, 0, p3, 0, p2, 0);
            }
        }

        // Also zaro perem
        int outerBottom = 0;
        int innerBottom = innerOffset;
        for (int ri = 0; ri < radialSegments; ri++) {
            int ob0 = outerBottom + ri;
            int ob1 = outerBottom + ri + 1;
            int ib0 = innerBottom + ri;
            int ib1 = innerBottom + ri + 1;
            mesh.getFaces().addAll(ob0, 0, ib0, 0, ob1, 0);
            mesh.getFaces().addAll(ob1, 0, ib0, 0, ib1, 0);
        }

        // Felso perem
        int outerTop = verticalSegments * ringSize;
        int innerTop = innerOffset + verticalSegments * ringSize;

        // Peremvastagsag
        for (int ri = 0; ri < radialSegments; ri++) {
            int ot0 = outerTop + ri;
            int ot1 = outerTop + ri + 1;
            int it0 = innerTop + ri;
            int it1 = innerTop + ri + 1;
            mesh.getFaces().addAll(ot0, 0, it0, 0, ot1, 0);
            mesh.getFaces().addAll(ot1, 0, it0, 0, it1, 0);
        }

        // Teljes also alaplemez
        float bottomCenterY = yValues[0];
        int bottomCenterIndex = mesh.getPoints().size() / 3;
        mesh.getPoints().addAll(0f, bottomCenterY, 0f);
        for (int ri = 0; ri < radialSegments; ri++) {
            int ib0 = innerBottom + ri;
            int ib1 = innerBottom + ri + 1;
            mesh.getFaces().addAll(bottomCenterIndex, 0, ib1, 0, ib0, 0);
        }

        return mesh;
    }
}
