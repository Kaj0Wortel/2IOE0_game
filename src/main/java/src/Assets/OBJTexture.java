package src.Assets;

public class OBJTexture {

    private OBJCollection asset;
    private TextureImg textureImg;

    public OBJTexture(OBJCollection asset, TextureImg textureImg) {
        this.asset = asset;
        this.textureImg = textureImg;
    }

    public OBJCollection getAsset() {
        return asset;
    }

    public void setAsset(OBJCollection asset) {
        this.asset = asset;
    }

    public TextureImg getTextureImg() {
        return textureImg;
    }

    public void setTextureImg(TextureImg textureImg) {
        this.textureImg = textureImg;
    }
}
