package com.adambocco.scifi;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.util.Duration;

class SkeletonControl extends Component {

    private AnimatedTexture texture;
    private final AnimationChannel animIdle;
    private final AnimationChannel animWalk;
    private final AnimationChannel animJump;
    private final AnimationChannel animSlash;
    private final AnimationChannel animInteract;
    private final AnimationChannel animDeath;

    public SkeletonControl() {

        Image sprite = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\skeleton.png");
        animIdle = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(1), 0, 3);
        animInteract = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(0.3), 5, 7);
        animWalk = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(2), 26, 37);
        animJump = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(2), 13, 22);
        animSlash = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(0.3), 4, 13);
        animDeath = new AnimationChannel(sprite, 13, 64, 64, Duration.seconds(0.6), 52, 58);

        texture = new AnimatedTexture(animIdle);
    }

    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(animWalk);

    }

    @Override
    public void onUpdate(double tpf) {
        PhysicsComponent p = entity.getComponent(PhysicsComponent.class);
        double xVel = p.getVelocityX();
        double yVel = p.getVelocityY();
        if (xVel < 0) entity.setScaleX(-1);
        if (xVel > 0) entity.setScaleX(1);

        AnimationChannel curAnim = texture.getAnimationChannel();
        if (yVel > 1 || yVel < -1) {
            if (curAnim == animIdle || curAnim == animWalk) {
                texture.loopAnimationChannel(animJump);
            }
        }
        else if (xVel > 0) {
            if (curAnim == animIdle || curAnim == animJump) {
                texture.loopAnimationChannel(animWalk);
            }
        }
        else if (xVel < 0) {
            if (curAnim == animIdle || curAnim == animJump) {
                texture.loopAnimationChannel(animWalk);
            }
        }
        else {
            if (curAnim == animWalk || curAnim == animJump) {
                texture.loopAnimationChannel(animIdle);
            }
        }
    }
}
