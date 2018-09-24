package src.Assets;

public class OBJTexture {

    private OBJObject asset;
    private TextureImg textureImg;

    public OBJTexture(OBJObject asset, TextureImg textureImg) {
        this.asset = asset;
        this.textureImg = textureImg;
    }

    public OBJObject getAsset() {
        return asset;
    }

    public void setAsset(OBJObject asset) {
        this.asset = asset;
    }

    public TextureImg getTextureImg() {
        return textureImg;
    }

    public void setTextureImg(TextureImg textureImg) {
        this.textureImg = textureImg;
    }
}
