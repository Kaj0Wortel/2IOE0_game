package src.Assets;

public class OBJTexture {

    private OBJObject asset;
    private Texture texture;

    public OBJTexture(OBJObject asset, Texture texture) {
        this.asset = asset;
        this.texture = texture;
    }

    public OBJObject getAsset() {
        return asset;
    }

    public void setAsset(OBJObject asset) {
        this.asset = asset;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
