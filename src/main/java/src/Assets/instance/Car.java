
package src.Assets.instance;


// Own imports

import com.jogamp.opengl.GL3;
import src.Assets.GraphicsObject;
import src.Assets.OBJTexture;
import src.Assets.instance.ThrowingItemFactory.ItemType;
import src.GS;
import src.Physics.PStructAction;
import src.Physics.PhysicsContext;
import src.Shaders.ShaderProgram;
import src.shadows.ShadowShader;
import src.tools.PosHitBox3f;
import src.tools.log.Logger;
import tools.observer.HashObservableInterface;

// Java imports


/**
 *
 */
public class Car
        extends GridItemInstance {

    private ItemType inventoryItem = ItemType.RED_SHELL; // = null

    public Car(PosHitBox3f box, float size,
            float rotx, float roty, float rotz,
            OBJTexture model, float integratedRotation,
            PhysicsContext physicConst) {
        super(box, size, rotx, roty, rotz, model, integratedRotation,
                physicConst);
    }

    @Override
    public boolean isStatic() {
        return false;
    }


    @Override
    public void draw(GL3 gl, ShaderProgram shader) {
        for (GraphicsObject obj : model.getAsset()) {
            shader.loadModelMatrix(gl, getTransformationMatrix());
            shader.loadTextureLightValues(gl, model.getTextureImg().getShininess(),
                    model.getTextureImg().getReflectivity());

            for (int i = 0; i < obj.size(); i++) {
                if (shader.useMaterial()) shader.loadMaterial(gl, obj.getMaterials().get(i));
                gl.glBindVertexArray(obj.getVao(i));
                gl.glEnableVertexAttribArray(0);
                gl.glEnableVertexAttribArray(1);
                gl.glEnableVertexAttribArray(2);
                gl.glDrawElements(GL3.GL_TRIANGLES, obj.getNrV(i),
                        GL3.GL_UNSIGNED_INT, 0);
                gl.glDisableVertexAttribArray(0);
                gl.glDisableVertexAttribArray(1);
                gl.glDisableVertexAttribArray(2);
            }
            gl.glBindVertexArray(0);
        }
    }

    @Override
    public void draw(GL3 gl, ShadowShader shader) {
        for (GraphicsObject obj : model.getAsset()) {
            shader.loadModelMatrix(gl, getTransformationMatrix());

            for (int i = 0; i < obj.size(); i++) {
                gl.glBindVertexArray(obj.getVao(i));
                gl.glEnableVertexAttribArray(0);
                gl.glDrawElements(GL3.GL_TRIANGLES, obj.getNrV(i),
                        GL3.GL_UNSIGNED_INT, 0);
                gl.glDisableVertexAttribArray(0);
            }
            gl.glBindVertexArray(0);
        }
    }

    public void giveItem() {
        if (inventoryItem != null) return;

        ItemType[] types = ItemType.values();
        inventoryItem = types[GS.R.nextInt(types.length)];
        Logger.write("GOT ITEM: " + inventoryItem);
    }

    @Override
    public void movement(PStructAction action) {
        if (action.throwItem) {
            System.out.println("throw!");
            ThrowingItemFactory.createItem(inventoryItem, this);
            inventoryItem = null;
        }

        super.movement(action);
    }

    @Override
    public char getSimpleRepr() {
        return '4';
    }
}
