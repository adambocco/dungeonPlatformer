package com.adambocco.scifi;

import com.almasb.fxgl.animation.AnimationBuilder;
import com.almasb.fxgl.animation.AnimationConfig;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import com.almasb.fxgl.texture.AnimationChannelData;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.getGameTimer;

class SkeletonControl extends Component {

    AnimatedTexture texture;
    public boolean isSlashing = false;
    private final AnimationChannel animIdle;
    private final AnimationChannel animWalk;
    private final AnimationChannel animSlash;
    private final AnimationChannel animDying;
    private final AnimationChannel animDead;



    public SkeletonControl() {

        Image sprite = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\skeleton.png");
        animIdle = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(1), 0, 3);
        animWalk = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(2), 26, 37);
        animSlash = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(0.3), 4, 13);
        animDying = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(0.8), 14, 25);
        animDead = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(2), 25, 25);

        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(animWalk);
        AnimationBuilder x = new AnimationBuilder();

    }

    @Override
    public void onUpdate(double tpf) {
        if (entity.getBoolean("dying")) return;
        PhysicsComponent p = entity.getComponent(PhysicsComponent.class);
        double xVel = p.getVelocityX();
        double yVel = p.getVelocityY();
        if (xVel < 0) entity.setScaleX(-1);
        if (xVel > 0) entity.setScaleX(1);


        AnimationChannel curAnim = texture.getAnimationChannel();

        if (xVel > 0) {
            if (curAnim == animIdle) {
                texture.loopAnimationChannel(animWalk);
            }
        }
        else if (xVel < 0) {
            if (curAnim == animIdle) {
                texture.loopAnimationChannel(animWalk);
            }
        }
        else {
            if (curAnim == animWalk) {
                texture.loopAnimationChannel(animIdle);
            }
        }
    }

    public void slash() {
        if (entity.getBoolean("dying")) return;
        texture.playAnimationChannel(animSlash);
        isSlashing = true;
        texture.setOnCycleFinished(new Runnable() {
            @Override
            public void run() {
                texture.loopAnimationChannel(animIdle);
                isSlashing = false;
            }
        });
    }


    public void death() {
        entity.setProperty("dying", true);
        entity.removeComponent(PhysicsComponent.class);
        texture.loopAnimationChannel(animDying);

        texture.loopNoOverride(animDying);
        texture.setOnCycleFinished(new Runnable() {
            @Override
            public void run() {

                texture.loopNoOverride(animDead);
            }
        });
    }
}
