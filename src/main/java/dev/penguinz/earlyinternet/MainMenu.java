package dev.penguinz.earlyinternet;

import dev.penguinz.Sylk.Application;
import dev.penguinz.Sylk.event.Event;
import dev.penguinz.Sylk.ui.UIButton;
import dev.penguinz.Sylk.ui.UIContainer;
import dev.penguinz.Sylk.ui.UIText;
import dev.penguinz.Sylk.ui.constraints.*;
import dev.penguinz.Sylk.ui.font.RelativeTextHeight;
import dev.penguinz.Sylk.util.Alignment;
import dev.penguinz.Sylk.util.Color;
import dev.penguinz.Sylk.util.Layer;

public class MainMenu implements Layer {

    private UIContainer container;

    @Override
    public void init() {
        Assets.getAssets().loadAssets();

        this.container = new UIContainer();

        this.container.addComponent(
                new UIText("Early Internet", Color.white, Assets.getAssets().getFont(), new RelativeTextHeight(1)).setHorizontalAlignment(Alignment.CENTER),
                new UIConstraints().
                        setXConstraint(new CenterConstraint()).
                        setYConstraint(new PixelConstraint(50, Alignment.TOP)).
                        setWidthConstraint(new AbsoluteConstraint(250)).setHeightConstraint(new AbsoluteConstraint(100)));

        UIButton playButton = new UIButton(Color.white, Color.white,
                new UIText("Play", Color.black, Assets.getAssets().getFont(), new RelativeTextHeight(1)).setHorizontalAlignment(Alignment.CENTER)
                , () -> {
            Application.getInstance().attachLayers(new FadeLayer(new Game(), 1f, 0.75f, () -> Application.getInstance().removeLayers(this)));
        });
        playButton.roundness.value = 5f;

        this.container.addComponent(playButton, new UIConstraints().
                    setXConstraint(new PixelConstraint(250, Alignment.LEFT)).
                    setYConstraint(new PixelConstraint(200, Alignment.BOTTOM)).
                    setWidthConstraint(new AbsoluteConstraint(300)).setHeightConstraint(new AbsoluteConstraint(100)));

        UIButton quitButton = new UIButton(Color.white, Color.white,
                new UIText("Quit", Color.black, Assets.getAssets().getFont(), new RelativeTextHeight(1)).setHorizontalAlignment(Alignment.CENTER)
                , () -> {
            Application.getInstance().attachLayers(new FadeLayer(new MainMenu(), 0.5f, 30f, () -> Application.getInstance().terminate()));
        });
        quitButton.roundness.value = 5f;

        this.container.addComponent(quitButton, new UIConstraints().
                setXConstraint(new PixelConstraint(250, Alignment.RIGHT)).
                setYConstraint(new PixelConstraint(200, Alignment.BOTTOM)).
                setWidthConstraint(new AbsoluteConstraint(300)).setHeightConstraint(new AbsoluteConstraint(100)));
    }

    @Override
    public void update() {
        this.container.update();
    }

    @Override
    public void render() {
        this.container.render();
    }

    @Override
    public void onEvent(Event event) {
        this.container.onEvent(event);
    }

    @Override
    public void dispose() {
        this.container.dispose();
    }
}
