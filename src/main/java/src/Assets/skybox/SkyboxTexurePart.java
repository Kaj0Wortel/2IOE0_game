package src.Assets.skybox;

import java.nio.ByteBuffer;

public class SkyboxTexurePart {

    private ByteBuffer data;
    private int width;
    private int height;

    public SkyboxTexurePart(ByteBuffer data, int width, int height) {
        this.data = data;
        this.width = width;
        this.height = height;
    }

    public ByteBuffer getData() {
        return data;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
