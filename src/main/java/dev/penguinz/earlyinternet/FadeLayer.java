package dev.penguinz.earlyinternet;

import dev.penguinz.Sylk.Application;
import dev.penguinz.Sylk.animation.Animation;
import dev.penguinz.Sylk.animation.Animator;
import dev.penguinz.Sylk.animation.values.AnimatableColor;
import dev.penguinz.Sylk.event.Event;
import dev.penguinz.Sylk.graphics.RenderLayer;
import dev.penguinz.Sylk.ui.UIBlock;
import dev.penguinz.Sylk.ui.UIContainer;
import dev.penguinz.Sylk.ui.constraints.UIConstraints;
import dev.penguinz.Sylk.util.Color;
import dev.penguinz.Sylk.util.Layer;

public class FadeLayer implements Layer {

    private UIContainer fade;

    private Animator animator;
    private final float fadeInTime, fadeOutTime;

    private final Layer nextLayer;
    private final Runnable middleAction;

    public FadeLayer(Layer nextLayer, float fadeInTime, float fadeOutTime, Runnable middleAction) {
        this.fadeInTime = fadeInTime;
        this.fadeOutTime = fadeOutTime;
        this.nextLayer = nextLayer;
        this.middleAction = middleAction;
    }

    @Override
    public void init() {
        this.animator = new Animator();

        this.fade = new UIContainer(RenderLayer.UI1);

        UIBlock fadeLayer = new UIBlock(new Color(0, 0, 0, 0));

        this.fade.addComponent(fadeLayer, UIConstraints.getFullConstraints());

        Animation fadeIn = new Animation(fadeInTime).addValue(fadeLayer.color, new Color(0, 0, 0, 0), new Color(0, 0, 0, 1));
        Animation fadeOut = new Animation(fadeOutTime).addValue(fadeLayer.color, new Color(0, 0, 0, 1), new Color(0, 0, 0, 0)).setCompletionCallback(() -> Application.getInstance().removeLayers(this));
        fadeIn.setCompletionCallback(() -> {
            middleAction.run();
            animator.playAnimation(fadeOut);

            Application.getInstance().attachLayers(nextLayer);
        });

        animator.playAnimation(fadeIn);
    }

    @Override
    public void update() {
        this.animator.update();
        this.fade.update();
    }

    @Override
    public void render() {
        this.fade.render();
    }

    @Override
    public void onEvent(Event event) {
        this.fade.onEvent(event);
    }

    @Override
    public void dispose() {
        this.fade.dispose();
    }
}
