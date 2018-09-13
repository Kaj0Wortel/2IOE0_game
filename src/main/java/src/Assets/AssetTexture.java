package src.Assets;

public class AssetTexture {

    private Asset asset;
    private Texture texture;

    public AssetTexture(Asset asset, Texture texture) {
        this.asset = asset;
        this.texture = texture;
    }

    public Asset getAsset() {
        return asset;
    }

    public void setAsset(Asset asset) {
        this.asset = asset;
    }

    public Texture getTexture() {
        return texture;
    }

    public void setTexture(Texture texture) {
        this.texture = texture;
    }
}
