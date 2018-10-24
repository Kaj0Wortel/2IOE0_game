
package src.Assets.instance;


// Own imports
import com.jogamp.opengl.GL3;
import org.joml.Matrix4f;
import src.Assets.GraphicsObject;
import src.Assets.OBJTexture;
import src.Assets.instance.ThrowingItemFactory.ItemType;
import src.GS;
import src.Physics.PStructAction;
import src.Physics.PhysicsContext;
import src.Shaders.CarShader;
import src.Shaders.ShadowShader;
import src.tools.PosHitBox3f;
import src.tools.log.Logger;

import java.util.Comparator;

// Java imports


/**
 *
 */
public class Car
        extends GridItemInstance {

    private ItemType inventoryItem = ItemType.RED_SHELL; // = null
    private CarShader carShader;
    private Matrix4f projectionMatrix;
    private int shadowMap;
    
    /**
     * Comparator for comparing the progress of two cars.
     */
    public static Comparator<Car> progressComparator = (Car c1, Car c2) -> {
        return c1.progress.compareTo(c2.progress);
    };
    

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

    public void draw(GL3 gl, Matrix4f shadowMatrix) {
        prepare(gl);

        carShader.loadShadowMatrix(gl, shadowMatrix);

        for(GraphicsObject obj : model.getAsset()) {
            carShader.loadModelMatrix(gl, getTransformationMatrix());
            carShader.loadTextureLightValues(gl, model.getTextureImg().getShininess(),
                    model.getTextureImg().getReflectivity());

            for (int i = 0; i < obj.size(); i++) {
                if(carShader.useMaterial()) carShader.loadMaterial(gl,obj.getMaterials().get(i));
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
        
        gl.glDisable(GL3.GL_TEXTURE_2D);
    }

    private void prepare(GL3 gl){
        carShader.start(gl);

        carShader.loadProjectionMatrix(gl,projectionMatrix);
        carShader.loadViewMatrix(gl, GS.camera.getViewMatrix());
        carShader.loadLight(gl,GS.getLights().get(0));
        carShader.loadCameraPos(gl, GS.camera.getPosition());
        carShader.loadTextures(gl);

        gl.glActiveTexture(GL3.GL_TEXTURE0);
        gl.glBindTexture(GL3.GL_TEXTURE_2D, shadowMap);
        gl.glEnable(GL3.GL_TEXTURE_2D);
    }

    @Override
    public void draw(GL3 gl, ShadowShader shader) {
        for(GraphicsObject obj : model.getAsset()){
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
        if(isFinished() && GS.first == null){
            GS.first = this;
        }

        if (action.throwItem) {
            System.out.println("throw!");
            ThrowingItemFactory.createItem(inventoryItem, this);
            inventoryItem = null;
        }

        super.movement(action);
    }
    
    /**
     * @return the item this car has currently in it's inventory.
     */
    public ItemType getItem() {
        return inventoryItem;
    }
    
    /**
     * @return the position this car is in the race, staring at 0.
     * 
     * Here we use a non-optimized approach, but still in O(n).
     */
    public int getRacePosition() {
        GS.cars.sort(progressComparator);
        int num = 0;
        for (Car car : GS.cars) {
            if (car == this) {
                return num;
            }
            num++;
        }
        
        return 0;
    }
    
    public float getSpeedAngle() {
        return Math.min((float) Math.PI - 0.4f , Math.abs(getState().velocity / 9)) - 0.4f;
    }

    public void setCarShaderVariables(CarShader shader, Matrix4f projectionMatrix) {
        this.carShader = shader;
        this.projectionMatrix = projectionMatrix;
    }


    public void setShadowMap(int depthTexture) {
        this.shadowMap = depthTexture;
    }

    public boolean isFinished(){
        return getProgressManager().lap > getProgressManager().lapTotal;
    }

    public boolean isFirst(){
        return GS.first == this;
    }


}