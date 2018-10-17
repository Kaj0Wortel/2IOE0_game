
package src.Assets;


//Own imports
import src.tools.Cloneable;


public class OBJTexture
        implements Cloneable {

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
    
    @Override
    public OBJTexture clone() {
        return new OBJTexture(asset, textureImg);
    }
    
}
