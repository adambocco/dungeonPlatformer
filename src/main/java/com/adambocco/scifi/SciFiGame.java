package com.adambocco.scifi;

import com.almasb.fxgl.animation.AnimatedPath;
import com.almasb.fxgl.animation.AnimationBuilder;
import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.action.ContinuousAction;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.input.InputModifier;
import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.physics.box2d.collision.Manifold;
import com.almasb.fxgl.physics.box2d.collision.shapes.Shape;
import com.almasb.fxgl.physics.box2d.common.Transform;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.physics.box2d.dynamics.contacts.Contact;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;

import java.util.List;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class SciFiGame extends GameApplication{

    int skeletonSpeed = -700;
    Image tilesheet;
    public static boolean dying;
    int runVelocity = 100;
    Entity player, lever, goldChest, silverChest, skeleton;
    PhysicsComponent playerPhysics;
    boolean aDown, dDown, spaceDown;
    boolean isOnGround, leverAccess, goldChestAccess, silverChestAccess, goldKeyObtained, silverKeyObtained;
    int coins = 0;



    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(16*64);
        gameSettings.setHeight(16*64);
        gameSettings.setTitle("SciFi Game");
    }


    @Override
    protected void initGameVars(Map<String, Object> vars) {

    }

    @Override
    protected void initGame() {


        getGameWorld().addEntityFactory(new GameEntityFactory());
        setLevelFromMap("dungeon1.tmx");
        player = spawn("player");
        skeleton = spawn("skeleton");

        playerPhysics = player.getComponent(PhysicsComponent.class);
        tilesheet = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\sheet.png");
        lever = (Entity) getGameWorld().getEntitiesByType(EntityTypes.LEVER).get(0);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld pw = getPhysicsWorld();
        FixtureDef fd = new FixtureDef();
        fd.friction(0);
        playerPhysics.setFixtureDef(fd);
        pw.setGravity(0, 1000);
        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.SPIKES) {
            @Override
            protected void onCollisionBegin(Entity player, Entity spikes) {
                System.out.println("You are dead :(");
                player.getComponent(SpriteControl.class).death();
                dying = true;

                // TODO: Respawn, decrement lives
            }
        });



        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.ELEVATOR) {
            @Override
            protected void onCollision(Entity player, Entity elevator) {

            elevator.getComponent(PhysicsComponent.class).setVelocityY(-1000);


            }

            @Override
            protected void onCollisionEnd(Entity a, Entity b) {
                getPhysicsWorld().setGravity(0, 1000);
            }
        });

        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.PLATFORM) {
            @Override
            protected void onCollisionBegin(Entity player, Entity platform) {
                System.out.println("On ground");
                isOnGround = true;
             }

            @Override
            protected void onCollisionEnd(Entity a, Entity b) {
                isOnGround = false;
                System.out.println("Off ground");
            }
        });


        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.WALL) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                Vec2 vec = new Vec2(player.getX(), player.getY());
                Vec2 fv = new Vec2(100, -100);
            }

            @Override
            protected void onCollisionEnd(Entity a, Entity b) {

            }
        });

        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.LEVER) {
            @Override
            protected void onCollisionBegin(Entity player, Entity lever) {
                leverAccess = true;
            }

            @Override
            protected void onCollisionEnd(Entity a, Entity b) {
                leverAccess = false;
            }
        });

        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.COIN) {
            @Override
            protected void onCollisionBegin(Entity player, Entity coin) {
                System.out.println("Coin obtained");
                coin.removeFromWorld();
            }
        });

        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.GOLDCHEST) {
            @Override
            protected void onCollisionBegin(Entity player, Entity goldchest) {
                goldChest = goldchest;
                goldChestAccess = true;
            }
        });

        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.SILVERCHEST) {
            @Override
            protected void onCollisionBegin(Entity player, Entity silverchest) {
                silverChest = silverchest;
                silverChestAccess = true;
            }
        });

        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.GOLDKEY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity key) {
                System.out.println("Key obtained! Find the gold chest.");
                goldKeyObtained = true;
                key.removeFromWorld();
            }
        });
        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.SILVERKEY) {
            @Override
            protected void onCollisionBegin(Entity player, Entity key) {
                System.out.println("Key obtained! Find the silver chest.");
                silverKeyObtained = true;
                key.removeFromWorld();
            }
        });


        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.SKELETON) {
            @Override
            protected void onCollisionBegin(Entity player, Entity key) {
                System.out.println("You touched an icky skeleton.");

            }
        });
    }

    @Override
    protected void initInput() {
        Input input = getInput();

        FXGL.onKeyBuilder(KeyCode.SPACE, "jump").onActionBegin(new Runnable() {
            @Override
            public void run() {
                if (isOnGround && !dying)
                    playerPhysics.setVelocityY(-450);
            }
        });



        FXGL.onKeyBuilder(KeyCode.A, "left").onAction(new Runnable() {
            @Override
            public void run() {
                if (!dying)
                    playerPhysics.setVelocityX(-runVelocity);

            }
        }).onActionEnd(new Runnable() {
            @Override
            public void run() {
                if (isOnGround) {
                    playerPhysics.setVelocityX(0);
                }
            }
        });

        FXGL.onKeyBuilder(KeyCode.D, "right").onAction(new Runnable() {
            @Override
            public void run() {
                if (!dying)
                    playerPhysics.setVelocityX(runVelocity);
            }
        }).onActionEnd(new Runnable() {
            @Override
            public void run() {
                if (isOnGround) {
                    playerPhysics.setVelocityX(0);
                }
            }
        });



        FXGL.onKeyBuilder(KeyCode.E, "slash").onActionBegin(new Runnable() {
            @Override
            public void run() {
                if (!dying)
                    player.getComponent(SpriteControl.class).slash();
                skeleton.getComponent(ActionComponent.class).addAction(new MoveAction(
                        getInput().getMouseXWorld(), getInput().getMouseYWorld()));
            }
        });

        FXGL.onKeyBuilder(KeyCode.F, "interact").onActionBegin(new Runnable() {
            @Override
            public void run() {
                player.getComponent(SpriteControl.class).interact();
                if (leverAccess) {
                    lever.getViewComponent().clearChildren();
                    lever.getViewComponent().addChild( new Texture(tilesheet).subTexture(new Rectangle2D(80,80,16,16)));
                }
                if (goldChestAccess && !goldChest.getBoolean("opened") && goldKeyObtained) {
                    goldChest.setProperty("opened", true);
                    goldChest.getComponent(GoldChestControl.class).openChest();
                    System.out.println("Gold chest obtained! + 500 points");
                }
                if (silverChestAccess  && !silverChest.getBoolean("opened") && silverKeyObtained) {
                    silverChest.setProperty("opened", true);
                    silverChest.getComponent(SilverChestControl.class).openChest();
                    System.out.println("Silver chest obtained! + 200 points");
                }
            }
        });



        FXGL.onKeyBuilder(KeyCode.O, "sprint").onActionBegin(new Runnable() {
            @Override
            public void run() {
                runVelocity = 200;

            }
        }).onActionEnd(new Runnable() {
            @Override
            public void run() {
                runVelocity = 100;
            }
        });

    }

    @Override
    protected void onUpdate(double tpf) {

        if (skeleton.getX() > 600) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(-100);
        } else if (skeleton.getX() < 100) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(100);
        } else if (skeleton.getComponent(PhysicsComponent.class).getVelocityX() > 0) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(100);
        } else if (skeleton.getComponent(PhysicsComponent.class).getVelocityX() < 0) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(-100);
        }

        if (!playerPhysics.isMovingY()) {
            playerPhysics.setVelocityX(0);
        }
        if (player.getPosition().getY() > getAppHeight()) {

            playerPhysics.overwritePosition(new Point2D(400,400));
        }

    }

    public static void main(String[] args) {
        launch(args);

    }

    public static class MoveAction extends ContinuousAction {

        private double x;
        private double y;

        public MoveAction(double x, double y) {
            this.x = x;
            this.y = y;
        }

        @Override
        protected void perform(double tpf) {

            entity.translateTowards(new Point2D(x, y), tpf * 50);

            if (entity.getPosition().distance(x, y) < tpf * 50) {
                setComplete();
            }
        }
    }
    
}
