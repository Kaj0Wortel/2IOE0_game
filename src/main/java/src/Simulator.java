
package src;


// Jogamp imports
import com.jogamp.opengl.GL3;
import org.joml.Vector2f;
import org.joml.Vector3f;


// Own imports
import src.Assets.*;
import src.Assets.instance.*;
import src.Assets.skybox.Skybox;
import src.Controllers.PlayerController;
import src.OBJ.LoadOBJ;
import src.Physics.PhysicsContext;
import src.racetrack.BezierTrack;
import src.tools.Binder;
import src.tools.PosHitBox3f;
import src.tools.io.BufferedReaderPlus;
import src.tools.log.Logger;
import static src.Simulator.TYPE.*;
import src.glGUI.SpeedNeedleGUI;
import src.glGUI.StaticGUI;
import static src.tools.io.BufferedReaderPlus.NO_COMMENT;
import static src.tools.io.BufferedReaderPlus.TYPE_CSV;


// Java imports
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class Simulator {

    public static enum TYPE {
        CAR, ITEM, TRACK, ENVIRONMENT_TYPE, PLAYER, OTHER;
    }

    private GL3 gl;

    private Binder binder;

    public Simulator () {
        this.binder = new Binder();
    }

    public void setGL(GL3 gl){
        this.gl = gl;
    }

    public void initAssets() {
        /*
        //Terrain terrain = new Terrain(gl);
        //OBJTexture texturedTerrain = new OBJTexture(terrain,
          //      new TextureImg(gl,"test.jpg",0, 0));
        PosHitBox3f box = new PosHitBox3f(
                new Vector3f(texturedTerrain.getAsset().getCenteredPosition())
                .add(new Vector3f(0, -2, 0)), new Vector3f(), 2f, 2f, 6f);
        Instance terrainInstance = new TerrainInstance(box, 1f, 0f, 0f, 0f,
                texturedTerrain, 0f, new PhysicsContext());
        GS.addTerrain(terrainInstance);
       */
        
        ThrowingItemFactory.init(gl);
        
        PosHitBox3f box;
        
        OBJCollection col = LoadOBJ.load(gl, GS.OBJ_DIR + "cube.obj");
        OBJCollection sp = LoadOBJ.load(gl, GS.OBJ_DIR + "dragon.obj");
        OBJCollection car = LoadOBJ.load(gl, GS.OBJ_DIR + "car_better.obj");
        OBJCollection car2 = LoadOBJ.load(gl, GS.OBJ_DIR + "offroadcar_better.obj");
        OBJCollection rock = LoadOBJ.load(gl, GS.OBJ_DIR + "Low-Poly_models.obj");
        OBJCollection planet = LoadOBJ.load(gl, GS.OBJ_DIR + "planet.obj");
        OBJCollection banner = LoadOBJ.load(gl, GS.OBJ_DIR + "startBanner.obj");
        
        Map<Integer, OBJObject> rocks = new HashMap<Integer, OBJObject>();
        rocks.put(0, rock.get(0));
        rocks.put(1, rock.get(1));
        rocks.put(2, rock.get(2));
        rocks.put(3, rock.get(3));

        
        // (0,0,0) REFERENCE
        for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(col,
                    new TextureImg(5, 0.5f));
            //box = new Box3f(new Vector3f(0f, 0f, 0f), 2f, 2f, 6f);
            box = obj.createBoundingBox();
            //box.setPosKeepHitBox();
            box.translate(new Vector3f(0f, 0f, 0f));
            Instance cubeInstance = new EnvironmentItem(box,
                    1f, 0, 0, 0, texturedCube, 0, new PhysicsContext(),
                    EnvironmentItem.Type.SPEED_BOOST);
            GS.addAsset(cubeInstance);
        }


        addToGamestate(ENVIRONMENT_TYPE, col, new Vector3f(-40f, 1f, 0.001f), 1, 0, 0, 0, 0,
                new TextureImg(5, 0.5f), null, EnvironmentItem.Type.STATIC_OBSTACLE);

        addToGamestate(ENVIRONMENT_TYPE, col, new Vector3f(0f, 1f, -40f), 1, 0, 0, 0, 0,
                new TextureImg(5, 0.5f), null, EnvironmentItem.Type.SPEED_BOOST);



        // SLOWDOWN TEST
        /*for (OBJObject obj : col) {
            OBJTexture texturedCube = new OBJTexture(obj,
                    new TextureImg(5, 0.5f));
            box = new Box3f(new Vector3f(100f, -52f, 0), 2f, 2f, 6f);
            Instance cubeInstance = new EnvironmentItem(box,
                    50f, 0, 0, 0, texturedCube, 0, new PhysicsContext(),
                    EnvironmentItem.Type.SLOW_DOWN);
            GS.addAsset(cubeInstance);
        }*/
        
        /*
        addToGamestate(ITEM, col, new Vector3f(0f, -115f, 540f), 1, 45, 45, 45, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(-10f, -115f, 540f), 1, 45, 45, 45, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(10f, -115f, 540f), 1, 45, 45, 45, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1015f), 1, 45, 45, 45, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1025f), 1, 45, 45, 45, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1035f), 1, 45, 45, 45, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(80f, -52f, -5f), 1, 45, 45, 45, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(-40f,-52f, 30f), 1, 45, 45, 45, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        */
        addToGamestate(ITEM, col, new Vector3f(0f, -115f, 540f), 1, 0, 0, 0, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(-10f, -115f, 540f), 1, 0, 0, 0, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(10f, -115f, 540f), 1, 0, 0, 0, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1015f), 1, 0, 0, 0, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1025f), 1, 0, 0, 0, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(385f, -73f, 1035f), 1, 0, 0, 0, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(80f, -52f, -5f), 1, 0, 0, 0, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        addToGamestate(ITEM, col, new Vector3f(-40f,-52f, 30f), 1, 0, 0, 0, 0,
                new TextureImg(gl,"item_block.png",5, 0.5f), null, null);
        
        addToGamestate(OTHER, sp, new Vector3f(0f, -60f, 500f), 4, 0, -90, 0, 0,
                new TextureImg(5, 0.5f), null, null);

        /*
        Instance aiCar = addToGamestate(CAR, car, new Vector3f(0,2,0), 5,0,180,0,90,
                new TextureImg(5,0.5f),null,null);
        new AIController((Car) aiCar);
        */

        addToGamestate(PLAYER, car2, new Vector3f(0, 2, -30), 3,
                0, 180, 0, 0, new TextureImg(5, 3f), null, null);
        
        addToGamestate(PLAYER, car, new Vector3f(0, 2, 0), 5,
                0, 180, 0, 0, new TextureImg(5, 0.5f), null, null);

        addLight(new Vector3f(30000f, 50000f, 1f),
                new Vector3f(1f, 1f, 1f));
        
        // Add GUIs (in this exact order!)
        Vector2f guiPos = new Vector2f(-1f, -1f);
        Vector2f guiSize = new Vector2f(2f, 2f);
        new StaticGUI(gl, guiPos, guiSize);
        new SpeedNeedleGUI(gl, guiPos, guiSize);
        
        // Add track.
        addToGamestate(TRACK, null, new Vector3f(0,1,-5), 3, 0, 0,0, 0,
                new TextureImg(gl,"rainbow_road.png"),
                new TextureImg(gl, "tileNormalMap.png"), null);
        
        try (BufferedReaderPlus brp = new BufferedReaderPlus(GS.ASTROID_POSITIONS,
                NO_COMMENT, TYPE_CSV)) {
            
            String line;
            while ((line = brp.readCSVCell(false)) != null) {
                try {
                    int rocktype = Integer.parseInt(line);
                    Vector3f pos = new Vector3f(
                            Integer.parseInt(brp.readCSVCell(false)),
                            Integer.parseInt(brp.readCSVCell(false)),
                            Integer.parseInt(brp.readCSVCell(false))
                    );

                    int size =  Integer.parseInt(brp.readCSVCell(false));
                    int angle1 = Integer.parseInt(brp.readCSVCell(false));
                    int angle2 = Integer.parseInt(brp.readCSVCell(false));
                    int angle3 = Integer.parseInt(brp.readCSVCell(false));

                    addRock(rocks.get(rocktype), pos, size,
                            angle1, angle2, angle3, 0,
                            new TextureImg(5, 3f),
                            MaterialInstance.Type.SPACE_ROCK);
                    
                } catch (NumberFormatException e) {
                    Logger.write(e);
                }
            }
            
        } catch (IOException e) {
            Logger.write(e);
        }

        addRock(planet.get(0), new Vector3f(310, -30, 780), 25, 0, 0, 0, 0,
                new TextureImg(5, 3f), MaterialInstance.Type.PLANET);
        
        addSkybox();
        /*
        for (Car player : GS.getPlayers()) {
            addSkybox(player);
        }
        */
        //addBanner(banner, new Vector3f(0, 0, 40), 4, 0, 90, 0, 0,
         //       new TextureImg(gl, "rainbow_road.png"), null);
        
        System.out.println("Assets initialized");

    }

    public void cleanup(){
        binder.clean(gl);
    }

    public Instance addToGamestate(TYPE type, OBJCollection col,
            Vector3f position, int size, int rotx, int roty, int rotz,
            int integratedRotation, TextureImg texture, TextureImg normalMap,
            EnvironmentItem.Type envType){
        Instance cubeInstance = null;
        switch(type){
            case ITEM:{
                for(OBJObject obj : col) {
                    OBJTexture texturedCube = new OBJTexture(col,
                            texture);
                    //box = new Box3f(new Vector3f(-40f, -52f, 30f), 2f, 2f, 2f);
                    PosHitBox3f box = obj.createBoundingBox();
                    //box.setPosKeepHitBox();
                    box.translate(position);
                    cubeInstance = new PickupItem(box,
                            5*size, rotx, roty, rotz, texturedCube,
                            integratedRotation, new PhysicsContext());
                    GS.addItem((Item) cubeInstance);
                }
                break;
            }
            case ENVIRONMENT_TYPE:{
                for (OBJObject obj : col) {
                    OBJTexture texturedCube = new OBJTexture(col,
                            texture);
                    //box = new Box3f(new Vector3f(0f, 1f, -40f), 2f, 2f, 6f);
                    PosHitBox3f box = obj.createBoundingBox();
                    //box.setPosKeepHitBox();
                    box.translate(position);
                    cubeInstance = new EnvironmentItem(box,
                            size, rotx, roty, rotz, texturedCube,
                            integratedRotation, new PhysicsContext(),
                            envType);
                    GS.addAsset(cubeInstance);
                }
                break;
            }
            case TRACK:{
                BezierTrack testTrack = new BezierTrack(
                        position, size, rotx, roty, rotz,
                        texture,
                        normalMap);
                testTrack.generateTrack(gl);
                GS.setTrack(testTrack);
                break;
            }
            case CAR:{
                // CAR
                OBJTexture texturedCube = new OBJTexture(col,
                        texture);
                //box = new Box3f(new Vector3f(0f, 0f, 0f), 2f, 2f, 6f);
                PosHitBox3f box = col.createBoundingBox();
                //box.setPosKeepHitBox();
                box.translate(position);
                cubeInstance = new Car(box,
                        size, rotx, roty, rotz, texturedCube,
                        integratedRotation, new PhysicsContext());
                GS.cars.add((Car) cubeInstance);
                GS.addMaterialAsset(cubeInstance);
                break;
            }
            case PLAYER:{
                // CAR
                OBJTexture texturedCube = new OBJTexture(col,
                        texture);
                //box = new Box3f(new Vector3f(0f, 0f, 0f), 2f, 2f, 6f);
                PosHitBox3f box = col.createBoundingBox();
                //box.setPosKeepHitBox();
                box.translate(position);
                //box.pos().
                cubeInstance = new Car(box,
                        size/1.75f, rotx, roty, rotz, texturedCube, 
                        integratedRotation, new PhysicsContext());
                GS.addPlayer((Car) cubeInstance);
                GS.cars.add((Car) cubeInstance);
                break;
            }
            case OTHER:{
                for (OBJObject obj : col) {
                    OBJTexture texturedCube = new OBJTexture(col,
                            texture);
                    //box = new Box3f(new Vector3f(0f, -60f, 500f));
                    PosHitBox3f box = obj.createBoundingBox();
                    //box.setPosKeepHitBox();
                    box.translate(position);
                    cubeInstance = new EnvironmentItem(box,
                            size, rotx, roty, rotz, texturedCube,
                            integratedRotation, new PhysicsContext(),
                            EnvironmentItem.Type.SPEED_BOOST);
                    GS.addAsset(cubeInstance);
                }
                break;
            }
        }
        
        return cubeInstance;
    }

    public void addLight(Vector3f position, Vector3f color) {
        Light light = new Light(position,
                color);
        GS.addLight(light);
    }

    public void addSkybox() {
        Skybox skybox = new Skybox(gl);
        //GS.setSkybox(player, skybox);
        GS.setSkybox(skybox);
    }
    
    public void addRock(OBJObject rock, Vector3f position, int size,
            int rotx, int roty, int rotz, int integratedRotation,
            TextureImg texture, MaterialInstance.Type type) {
        OBJCollection col = new OBJCollection();
        col.add(rock);
        OBJTexture texturedCube = new OBJTexture(col,
                texture);
        //box = new Box3f(new Vector3f(0f, -60f, 500f));
        PosHitBox3f box = col.createBoundingBox();
        box.translate(position);
        Instance cubeInstance = new MaterialInstance(box,
                size, 0, 0, 0, texturedCube,
                rotx, roty, rotz, new PhysicsContext(), type);
        GS.addMaterialAsset(cubeInstance);
    }

    public void addBanner(OBJCollection col, Vector3f position, int size,
                          int rotx, int roty, int rotz, int integratedRotation,
                          TextureImg texture, MaterialInstance.Type type){

        OBJTexture texturedCube = new OBJTexture(col,
                texture);
        //box = new Box3f(new Vector3f(0f, -60f, 500f));
        PosHitBox3f box = col.createBoundingBox();
        box.translate(position);
        Instance cubeInstance = new MaterialInstance(box,
                size, 0, 0, 0, texturedCube,
                rotx, roty, rotz, new PhysicsContext(), type);
        GS.addTerrain(cubeInstance);
    }
    
    
}