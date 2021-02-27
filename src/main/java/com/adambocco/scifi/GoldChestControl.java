package com.adambocco.scifi;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.scene.image.Image;
import javafx.util.Duration;

public class GoldChestControl extends Component {
    private AnimatedTexture texture;
    private AnimationChannel animUnopened, animOpening, animOpened;

    public GoldChestControl() {

        Image sheet = new Image("file:C:\\Users\\User\\Documents\\Projects\\SciFiGame\\target\\classes\\assets\\textures\\sheet.png");
        animUnopened = new AnimationChannel(sheet, 23, 16, 16, Duration.seconds(1), 84, 84);
        animOpening = new AnimationChannel(sheet, 23, 16, 16, Duration.seconds(1), 84, 87);
        animOpened = new AnimationChannel(sheet, 23, 16, 16, Duration.seconds(1), 87, 87);

        texture = new AnimatedTexture(animUnopened);
    }
    @Override
    public void onAdded() {
        entity.getViewComponent().addChild(texture);
        texture.loopAnimationChannel(animUnopened);
    }

    @Override
    public void onUpdate(double tpf) {

    }

    public void openChest() {
        texture.playAnimationChannel(animOpening);
        texture.setOnCycleFinished(new Runnable() {
            @Override
            public void run() {
                texture.playAnimationChannel(animOpened);
            }
        });
    }
}