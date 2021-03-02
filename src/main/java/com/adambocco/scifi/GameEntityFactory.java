package com.adambocco.scifi;

import com.almasb.fxgl.animation.AnimatedValue;
import com.almasb.fxgl.animation.AnimationBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.entity.action.ActionComponent;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.entity.components.CollidableComponent;
import com.almasb.fxgl.entity.components.TransformComponent;
import com.almasb.fxgl.physics.*;
import com.almasb.fxgl.physics.box2d.common.JBoxSettings;
import com.almasb.fxgl.physics.box2d.dynamics.BodyDef;
import com.almasb.fxgl.physics.box2d.dynamics.BodyType;
import com.almasb.fxgl.physics.box2d.dynamics.FixtureDef;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.ImagesKt;
import com.almasb.fxgl.texture.Texture;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;


public class GameEntityFactory implements EntityFactory {

    Image tilesheet = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\sheet.png");

    @Spawns("background")
    public Entity newBackground(SpawnData data) {
        return FXGL.entityBuilder()
                .from(data)
                .view(new Rectangle(FXGL.getAppWidth()*2, FXGL.getAppHeight()*2))
                .build();
    }

    @Spawns("platform")
    public Entity newPlatform(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();

        return FXGL.entityBuilder()
                .type(EntityTypes.PLATFORM)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with(physics)
                .build();
    }


    @Spawns("wall")
    public Entity newWall(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();

        return FXGL.entityBuilder()
                .type(EntityTypes.WALL)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with(physics)
                .build();
    }


    @Spawns("player")
    public Entity newPlayer(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        FixtureDef fd = new FixtureDef();
        fd.setFriction(0);
        physics.setFixtureDef(fd);
        physics.addGroundSensor(new HitBox(BoundingShape.box(40, 38)));


        return FXGL.entityBuilder()
                .type(EntityTypes.PLAYER)
                .bbox(new HitBox(BoundingShape.box(24,38)))
                .with(physics)
                .with(new SpriteControl())
                .with(new CollidableComponent(true))
                .build();
    }



    @Spawns("skeleton")
    public Entity newSkeleton(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.friction(0);
        fd.setDensity(0);
        fd.setRestitution(0);
        physics.setFixtureDef(fd);

        HitBox hb = new HitBox("body", new Point2D(21,16),BoundingShape.box(20,32));

        return FXGL.entityBuilder()
                .type(EntityTypes.SKELETON)
                .bbox(hb)
                .with(new CollidableComponent(true))
                .with(new ActionComponent())
                .with(new SkeletonControl())
                .with(physics)
                .with("dying", false)
                .with("dead", false)
                .build();
    }

    @Spawns("pigman")
    public Entity newPigman(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);

        FixtureDef fd = new FixtureDef();
        fd.friction(1000);
        physics.setFixtureDef(fd);

        HitBox hb = new HitBox(BoundingShape.box(80,80));

        return FXGL.entityBuilder()
                .type(EntityTypes.PIGMAN)
                .bbox(hb)
                .with(new CollidableComponent(true))
                .with(new ActionComponent())
                .with(new PigControl())
                .with(physics)
                .with("dying", false)
                .build();
    }

    @Spawns("goldchest")
    public Entity newGoldChest(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.addSensor(new HitBox(BoundingShape.box(20, 20)), new SensorCollisionHandler() {
            @Override
            protected void onCollisionBegin(Entity other) {
                other.setProperty("goldChestAccess", true);
            }

            @Override
            protected void onCollisionEnd(Entity other) {
                other.setProperty("goldChestAccess", false);
            }
        });

        return FXGL.entityBuilder()
                .type(EntityTypes.GOLDCHEST)
                .at(200,200)
                .bbox(new HitBox(BoundingShape.box(16, 16)))
                .with(physics)
                .with(new GoldChestControl())
                .with(new CollidableComponent(true))
                .with("opened", false)
                .build();
    }


    @Spawns("goldkey")
    public Entity newGoldKey(SpawnData data) {
        Texture tex = new Texture(tilesheet).subTexture(new Rectangle2D(224,32,16,16));

        return FXGL.entityBuilder()
                .type(EntityTypes.GOLDKEY)
                .viewWithBBox(tex)
                .with(new CollidableComponent(true))
                .build();

    }


    @Spawns("silverchest")
    public Entity newSilverChest(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();

        return FXGL.entityBuilder()
                .type(EntityTypes.SILVERCHEST)
                .bbox(new HitBox(BoundingShape.box(16, 16)))
                .with(physics)
                .with(new SilverChestControl())
                .with(new CollidableComponent(true))
                .with("opened", false)
                .build();
    }

    @Spawns("silverkey")
    public Entity newSilverKey(SpawnData data) {
        Texture tex = new Texture(tilesheet).subTexture(new Rectangle2D(224,48,16,16));

        return FXGL.entityBuilder()
                .type(EntityTypes.SILVERKEY)
                .viewWithBBox(tex)
                .with(new CollidableComponent(true))
                .build();

    }
    @Spawns("spikes")
    public Entity newSpikes(SpawnData data) {
        return FXGL.entityBuilder()
                .type(EntityTypes.SPIKES)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }


    @Spawns("gate")
    public Entity newGate(SpawnData data) {
        PhysicsComponent p = new PhysicsComponent();

        return FXGL.entityBuilder()
                .type(EntityTypes.GATE)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .with(p)
                .build();

    }
    @Spawns("door")
    public Entity newDoor(SpawnData data) {
        return FXGL.entityBuilder()
                .type(EntityTypes.DOOR)
                .bbox(new HitBox(BoundingShape.box(data.<Integer>get("width"), data.<Integer>get("height"))))
                .with(new CollidableComponent(true))
                .build();
    }

    @Spawns("coin")
    public Entity newCoin(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();

        Image sheet = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\sheet.png");
        AnimationChannel animSpin = new AnimationChannel(sheet, 23, 16, 16, Duration.seconds(1), 15, 18);

        AnimatedTexture texture = new AnimatedTexture(animSpin);
        Component anim = new Component() {
            @Override
            public void onAdded() {
                entity.getViewComponent().addChild(texture);
                texture.loopAnimationChannel(animSpin);
            }
        };

        return FXGL.entityBuilder()
                .type(EntityTypes.COIN)
                .with(new CollidableComponent(true))
                .bbox(new HitBox(BoundingShape.box(16, 16)))
                .with(anim)
                .with(physics)
                .build();
    }

    @Spawns("elevator")
    public Entity newElevator(SpawnData data) {
        PhysicsComponent physics = new PhysicsComponent();
        physics.setBodyType(BodyType.DYNAMIC);
        FixtureDef fd = new FixtureDef();
        fd.setFriction(0);
        physics.setFixtureDef(fd);
        Image platformts = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\decorative_obj.png");
        System.out.println(platformts.getHeight());
        Texture tex = new Texture(platformts).subTexture(new Rectangle2D(416,192,48,16));

        return FXGL.entityBuilder()
                .type(EntityTypes.ELEVATOR)
                .viewWithBBox(tex)
                .with(new CollidableComponent(true))
                .with(physics)
                .build();

    }

    @Spawns("lever")
    public Entity newLever(SpawnData data) {
        Texture tex = new Texture(tilesheet).subTexture(new Rectangle2D(64,80,16,16));

        return FXGL.entityBuilder()
                .type(EntityTypes.LEVER)
                .viewWithBBox(tex)
                .with(new CollidableComponent(true))
                .build();
    }
}
