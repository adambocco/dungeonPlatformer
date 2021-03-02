package com.adambocco.scifi;

import com.almasb.fxgl.animation.AnimationBuilder;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class PigControl extends Component {


    AnimatedTexture texture;
    public boolean isSlashing = false;
    public boolean isHurt = false;
    private final AnimationChannel animIdle;
    private final AnimationChannel animWalk;
    private final AnimationChannel animSlash1;
    private final AnimationChannel animSlash2;
    private final AnimationChannel animHurt;
    private final AnimationChannel animDeath;
    private final AnimationChannel animDead;



    public PigControl() {

        Image sprite = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\pigspritesheet.png");
        animWalk = new AnimationChannel(sprite, 14, 80, 80, Duration.seconds(2), 0, 12);
        animIdle = new AnimationChannel(sprite, 14, 80, 80, Duration.seconds(2), 14, 21);
        animSlash1 = new AnimationChannel(sprite, 14, 80, 80, Duration.seconds(1), 28, 36);
        animHurt = new AnimationChannel(sprite, 14, 80, 80, Duration.seconds(1), 42, 46);
        animDeath = new AnimationChannel(sprite, 14, 80, 80, Duration.seconds(4), 56, 69);
        animDead = new AnimationChannel(sprite, 14, 80, 80, Duration.seconds(4), 69, 69);
        animSlash2 = new AnimationChannel(sprite, 14, 80, 80, Duration.seconds(1), 70, 82);

        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(animIdle);

    }

    @Override
    public void onUpdate(double tpf) {
        if (entity.getBoolean("dying") || isHurt) return;
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

    public void slash1() {
        if (entity.getBoolean("dying")) return;
        texture.playAnimationChannel(animSlash1);
        isSlashing = true;
        texture.setOnCycleFinished(new Runnable() {
            @Override
            public void run() {
                texture.loopAnimationChannel(animIdle);
                isSlashing = false;
            }
        });
    }
    public void slash2() {
        if (entity.getBoolean("dying")) return;
        texture.playAnimationChannel(animSlash2);
        isSlashing = true;
        texture.setOnCycleFinished(new Runnable() {
            @Override
            public void run() {
                texture.loopAnimationChannel(animIdle);
                isSlashing = false;
            }
        });
    }

    public void hurt() {
        if (entity.getBoolean("dying")) return;
        isHurt = true;
        FXGL.getGameTimer().runOnceAfter(new Runnable() {
            @Override
            public void run() {
                isHurt = false;
            }
        },Duration.seconds(1));
        texture.loopAnimationChannel(animHurt);
        texture.setOnCycleFinished(new Runnable() {
            @Override
            public void run() {
                isHurt = false;
                System.out.println("SETTING IS HUrT TO FLase");
                texture.loopAnimationChannel(animIdle);

            }
        });
    }


    public void death() {
        entity.setProperty("dying", true);
        entity.removeComponent(PhysicsComponent.class);
        texture.loopAnimationChannel(animDeath);
        texture.setOnCycleFinished(new Runnable() {
            @Override
            public void run() {

                texture.loopNoOverride(animDead);
            }
        });
    }
}