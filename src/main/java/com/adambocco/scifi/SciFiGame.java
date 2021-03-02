package com.adambocco.scifi;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.core.math.Vec2;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.action.ContinuousAction;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.level.Level;
import com.almasb.fxgl.input.Input;
import com.almasb.fxgl.physics.CollisionHandler;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.physics.PhysicsWorld;
import com.almasb.fxgl.texture.Texture;
import com.almasb.fxgl.time.LocalTimer;
import com.almasb.fxgl.time.Timer;
import com.almasb.fxgl.ui.ProgressBar;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Map;

import static com.almasb.fxgl.dsl.FXGL.*;

public class SciFiGame extends GameApplication {

    public static Entity player;
    Image tilesheet;
    public static boolean dying;
    int runVelocity = 100;
    Entity lever, goldChest, silverChest;
    Entity skeleton1, skeleton2, skeleton3, skeleton4, skeleton5;
    ProgressBar skeleton1hb, skeleton2hb, skeleton3hb,  bosshb;
    Entity boss;
    LocalTimer bossLastSlash;
    PhysicsComponent playerPhysics, bossPhysics;
    boolean isOnGround, leverAccess, goldChestAccess, silverChestAccess, goldKeyObtained, silverKeyObtained;



    @Override
    protected void initSettings(GameSettings gameSettings) {
        gameSettings.setWidth(1504);
        gameSettings.setHeight(1024);
        gameSettings.setTitle("SciFi Game");

    }


    @Override
    protected void initGameVars(Map<String, Object> vars) {
        vars.put("playerLives", 5);

    }

    @Override
    protected void initGame() {
        getGameWorld().addEntityFactory(new GameEntityFactory());
        setLevelFromMap("darkforestflat.tmx");


        tilesheet = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\sheet.png");
        player = spawn("player", 300, 300);

        player.setProperty("busy", false);
        playerPhysics = player.getComponent(PhysicsComponent.class);
        playerPhysics.overwritePosition(new Point2D(100,150));

//        getGameScene().getViewport().bindToEntity(player, getAppWidth() / 2, getAppHeight() / 2);
//        getGameScene().getViewport().setZoom(1.3);
        getGameScene().setBackgroundColor(Color.BLACK);


//        BOSS ROOM
        boss = spawn("pigman");
        bossPhysics = boss.getComponent(PhysicsComponent.class);
        bossPhysics.overwritePosition(new Point2D(950,700));
        bossLastSlash = getGameTimer().newLocalTimer();
        bosshb = new ProgressBar();
        bosshb.setHeight(8.0D);
        bosshb.setWidth(50.0D);
        bosshb.setFill(Color.DARKRED);
        bosshb.getBackgroundBar().setEffect(null);
        bosshb.setCurrentValue(100);
        boss.getViewComponent().addChild(bosshb);


//        LEVEL 1
//        skeleton1 = spawn("skeleton", 200, 500);
//        skeleton2 = spawn("skeleton", 200,400);
//        skeleton3 = spawn("skeleton", 400,250);
//
//        skeleton1.setProperty("dying", false);
//        skeleton2.setProperty("dying", false);
//        skeleton3.setProperty("dying", false);
//
//        lever = (Entity) getGameWorld().getEntitiesByType(EntityTypes.LEVER).get(0);


//        LEVEL 2: Dark Forest
        skeleton1 = spawn("skeleton");
        skeleton2 = spawn("skeleton");
        skeleton3 = spawn("skeleton");

        skeleton1.setProperty("dying", false);
        skeleton2.setProperty("dying", false);
        skeleton3.setProperty("dying", false);
        skeleton1.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(250,600));
        skeleton2.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(700,200));
        skeleton3.getComponent(PhysicsComponent.class).overwritePosition(new Point2D(900,100));


        skeleton1hb = makeSkeletonHPBar();
        skeleton2hb = makeSkeletonHPBar();
        skeleton3hb = makeSkeletonHPBar();
        skeleton1.getViewComponent().addChild(skeleton1hb);
        skeleton2.getViewComponent().addChild(skeleton2hb);
        skeleton3.getViewComponent().addChild(skeleton3hb);


        lever = (Entity) getGameWorld().getEntitiesByType(EntityTypes.LEVER).get(0);
    }

    @Override
    protected void initPhysics() {
        PhysicsWorld pw = getPhysicsWorld();
        pw.setGravity(0, 1000);


        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.SPIKES) {
            @Override
            protected void onCollisionBegin(Entity player, Entity spikes) {
                System.out.println("You are dead :(");
                player.getComponent(SpriteControl.class).death();
                dying = true;
            }
        });


        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.ELEVATOR) {
            @Override
            protected void onCollisionBegin(Entity player, Entity elevator) {

                elevator.getComponent(PhysicsComponent.class).setVelocityY(-3000);
            }

        });


        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.PLATFORM) {
            @Override
            protected void onCollisionBegin(Entity player, Entity platform) {
                playerPhysics.setVelocityX(0);
             }
            @Override
            protected void onCollisionEnd(Entity a, Entity b) {

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

        pw.addCollisionHandler(new CollisionHandler(EntityTypes.PLAYER, EntityTypes.GATE) {
            @Override
            protected void onCollisionBegin(Entity player, Entity wall) {
                System.out.println("NEXT LEVEL!");
                FXGL.setLevelFromMap("bossroom2.tmx");
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
                if (playerPhysics.isOnGround() && !(boolean) player.getPropertyOptional("busy").get()) {
                    playerPhysics.setVelocityY(-450);
                }
            }
        });



        FXGL.onKeyBuilder(KeyCode.A, "left").onAction(new Runnable() {
            @Override
            public void run() {
                if (!(boolean) player.getPropertyOptional("busy").get()) {
                    playerPhysics.setVelocityX(-runVelocity);
                }
            }
        }).onActionEnd(new Runnable() {
            @Override
            public void run() {
                if (playerPhysics.isOnGround()) {
                    playerPhysics.setVelocityX(0);
                }
            }
        });

        FXGL.onKeyBuilder(KeyCode.D, "right").onAction(new Runnable() {
            @Override
            public void run() {
                if (!(boolean) player.getPropertyOptional("busy").get()) {
                    playerPhysics.setVelocityX(runVelocity);
                }
            }
        }).onActionEnd(new Runnable() {
            @Override
            public void run() {
                if (playerPhysics.isOnGround()) {
                    playerPhysics.setVelocityX(0);
                }
            }
        });

        FXGL.onKeyBuilder(KeyCode.E, "slash").onActionBegin(new Runnable() {
            @Override
            public void run() {
                if (!(boolean) player.getPropertyOptional("busy").get()) {
                    player.getComponent(SpriteControl.class).slash();

//                    BOSS LEVEL
                    if (boss.distanceBBox(player) < 50 && !boss.getComponent(PigControl.class).isHurt && !boss.getBoolean("dying")) {
                        bosshb.setCurrentValue(bosshb.getCurrentValue()-10);
                        boss.getComponent(PigControl.class).hurt();
                        if (bosshb.getCurrentValue() <=0) {
                            boss.removeComponent(CollidableComponent.class);
                            boss.removeComponent(PhysicsComponent.class);
                            boss.getComponent(PigControl.class).death();
                        }
                    }
                }
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

//        BOSS ROOM
            bossPattern();

//        LEVEL 1
//        skeletonPattern(skeleton1, 100, 600, 50);
//        skeletonPattern(skeleton2, 100, 400, 50);
//        skeletonPattern(skeleton3, 50, 100, 10);

//        LEVEL 2: Dark Forest
        skeletonPattern(skeleton1, 255, 350, 30);
        skeletonPattern(skeleton2, 800, 1000, 30);
        skeletonPattern(skeleton3, 760, 860, 25);

        if (player.getPosition().getY() > getAppHeight()) {
            playerPhysics.overwritePosition(new Point2D(500,100));
        }
    }

    public void skeletonPattern(Entity skeleton, int leftBound, int rightBound, int speed) {
        if (skeleton.getBoolean("dying") || player.getBoolean("busy")) {
            return;
        }
        else if (skeleton.distanceBBox(player) < 40) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(0);
            getGameTimer().runOnceAfter(new Runnable() {
                @Override
                public void run() {
                    skeleton.getComponent(SkeletonControl.class).slash();

                    if (skeleton.distanceBBox(player) < 30) {
                        player.setProperty("busy", true);
                        player.getComponent(SpriteControl.class).death();
                    }
                }
            }, Duration.seconds(1));

            getGameTimer().runOnceAfter(new Runnable() {
                @Override
                public void run() {
                    if (player.getComponent(SpriteControl.class).isSlashing) {
                        skeleton.getComponent(SkeletonControl.class).death();
                    }
                }
            }, Duration.seconds(0));
        }
        else if (player.getX() < rightBound && player.getX() > leftBound && player.getY() < skeleton.getY() +50 && player.getY() > skeleton.getY() -50) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(skeleton.getX()-player.getX() > 0 ? -speed : speed);
        }
        else if (skeleton.getX() > rightBound) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(-speed);
        } else if (skeleton.getX() < leftBound) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(speed);
        } else if (skeleton.getComponent(PhysicsComponent.class).getVelocityX() > 0) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(speed);
        } else if (skeleton.getComponent(PhysicsComponent.class).getVelocityX() <= 0) {
            skeleton.getComponent(PhysicsComponent.class).setVelocityX(-speed);
        }
    }

    public void bossPattern() {
        System.out.println(boss.getComponent(PigControl.class).isHurt);
        if (boss.getBoolean("dying") || boss.getComponent(PigControl.class).isSlashing || boss.getComponent(PigControl.class).isHurt ) return;

        double distToPlayer = boss.distanceBBox(player);
        Point2D bossPos = boss.getPosition();
        Point2D playerPos = player.getPosition();

        if (distToPlayer < 50 && bossLastSlash.elapsed(Duration.seconds(3))) {
            bossLastSlash.capture();
            getGameTimer().runOnceAfter(new Runnable() {
                @Override
                public void run() {
                    bossPhysics.setVelocityX(0);
                    boss.getComponent(PigControl.class).slash1();
                }
            },Duration.seconds(1));
        }

        else if (distToPlayer < 80 && bossLastSlash.elapsed(Duration.seconds(3))) {
            bossLastSlash.capture();
            getGameTimer().runOnceAfter(new Runnable() {
                @Override
                public void run() {
                    bossPhysics.setVelocityX(0);
                    boss.getComponent(PigControl.class).slash2();
//                    if (player.distanceBBox(boss) < 50) {
//
//                        playerPhysics.setLinearVelocity(player.getX()-boss.getX(), player.getY()-boss.getY());
//                    }
                }
            },Duration.seconds(1));
        }
        else if (distToPlayer < 130) {
            bossPhysics.setVelocityX(playerPos.getX() - bossPos.getX() > 0 ? 200 : -200);
        }
    }

    public static ProgressBar makeSkeletonHPBar() {
        ProgressBar newHPBar = new ProgressBar();
        newHPBar.setWidth(28.0D);
        newHPBar.setHeight(8.0D);
        newHPBar.setFill(Color.DARKRED);
        newHPBar.setMinValue(0);
        newHPBar.setMaxValue(100);
        newHPBar.setCurrentValue(100);
        newHPBar.setTranslateX(23);
        newHPBar.getBackgroundBar().setEffect(null);

        return newHPBar;
    }

    public static void main(String[] args) {
        launch(args);

    }
}
